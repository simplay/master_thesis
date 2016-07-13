addpath('../libs/flow-code-matlab');
addpath('../matlab_shared');
clear all
%% load ground truth img
DATASET = 'bonn_chairs_263_3_434';
img_index = 30;
STEPSIZE_DATA = 8;
PREFIX_INPUT_FILENAME = 'sed_both_2400';
METHODNAME = 'ldof';
SIMPLIFIED_STATISTICS = false;

FILTER_AMBIGUOUS = true;

%%
if FILTER_AMBIGUOUS
    imgName = strcat(num2str(img_index), '_amb.png');
else
    imgName = strcat(num2str(img_index), '.png');
end
DS_BASE_PATH = strcat('../../Data/',DATASET,'/gt/',imgName);
gtImg = imread(DS_BASE_PATH);
[m, n, c] = size(gtImg);
if c == 1
    tmp = zeros(m, n, 3);
    tmp(:,:,1) = gtImg;
    tmp(:,:,2) = 0;
    tmp(:,:,3) = 0;
    gtImg = tmp;
end

gt_img = double(gtImg);
gt_sum = gt_img(:,:,1) + 8*gt_img(:,:,2) + 16*gt_img(:,:,3);

uniqueSumValues = unique(gt_sum);
uniqueSumValues = uniqueSumValues((uniqueSumValues ~= (255+255*8+255*16)));
uniqueSumValues = uniqueSumValues + 1;
uniqueSumValues = uniqueSumValues(uniqueSumValues ~= 1);

if c == 1
    gtImg = gtImg(:,:,1);
else
    gtImg = rgb2gray(gtImg);
end

figure('name', 'ground truth')

imshow(gtImg);

%% load eval img
% load label


disp(['Running simplified statistics mode: ', num2str(SIMPLIFIED_STATISTICS)])

[FileName, FilePath, ~] = uigetfile('.txt');
LABELS_FILE_PATH = strcat(FilePath, FileName);
% LABELS_FILE_PATH = '/Users/simplay/repos/ma_my_pipeline/Source/output/cluster_merges/bonn_watercan_713_3_884_ldof_pd_10_iter_sc_iters_0_c_12_ev_18_nu_1e-08/labels.txt';
% LABELS_FILE_PATH = '/Users/simplay/repos/ma_my_pipeline/Source/output/cluster_merges/bonn_watercan_713_3_884_ldof_ped_s_12_ct_1_c_15_ev_15/labels.txt';


fileID = fopen(LABELS_FILE_PATH);
C = textscan(fileID,'%d,%d');
label_assignments = cell2mat(C(2)) + 1;
fclose(fileID);

fid = fopen(LABELS_FILE_PATH);
label_cluster_assignments = dlmread(LABELS_FILE_PATH);

[BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET); 
pr = '';
if isempty(PREFIX_INPUT_FILENAME) == 0
    pr = strcat(PREFIX_INPUT_FILENAME, '_');
end
label_mappings = labelfile2mat(strcat(BASE, DATASET, '_', pr));

[boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH, METHODNAME);
frames = loadAllTrajectoryLabelFrames(DATASET, boundaries(1), boundaries(2), PREFIX_INPUT_FILENAME);
frame = frames{img_index};


rgb_values = rgb_list(8);
[gt_m, gt_n, ~] = size(gtImg);
dsImg = zeros(gt_m, gt_n);
label_identifiers = unique(label_assignments);

BBB = zeros(2, length(label_assignments));
for idx=1:length(label_assignments)
    lm_idx = label_mappings(idx);
    fl_idx = find(frame.labels == lm_idx);
    if (isempty(fl_idx))
        continue;
    end
    assignment = label_assignments(idx);
    %color_id = label_identifiers(find(label_identifiers == assignment));
    iax = floor(frame.ax(fl_idx));
    iay = floor(frame.ay(fl_idx));
    
    if (iax < 1 || iay < 1) 
        continue 
    end
    
    if (gtImg(iax, iay) == 0 | gtImg(iax, iay) == 255)
        continue;
    end
    
    BBB(1, idx) = gtImg(iax, iay);
    BBB(2, idx) = assignment;
    dsImg(iax, iay) = assignment;
end

gtColor2ClusterLabel = zeros(2, length(label_identifiers));





for k=1:length(label_identifiers)
    u_label = label_identifiers(k);
    idxs = find(BBB(2, :) == u_label);
    
    votingLabels = unique(BBB(1, idxs));
    maxCount = -1;
    bestVoteLabel = -1;
    for v=1:length(votingLabels)
        vote = votingLabels(v);
        voteCount = length(find(BBB(1, idxs) == vote));
        if (voteCount > maxCount)
            maxCount = voteCount;
            bestVoteLabel = vote;
        end
    end
    gtColor2ClusterLabel(1, k) = u_label;
    gtColor2ClusterLabel(2, k) = bestVoteLabel;
    
end


% map gray scale value to cluster id:


figure('name', 'all samples')
dsImgShow = dsImg - min(dsImg(:));
dsImgShow = dsImgShow ./ max(dsImgShow(:));
imshow(im2double(dsImgShow));

TL = -1;
lid = -1;
for k=1:length(label_identifiers)
    l = label_identifiers(k);
    lenL = length(find(dsImg== l));
    if lenL > TL
        TL = lenL;
        lid = l;
    end
end
% keyboard;
gtLabels = uniqueSumValues;
forgroundLabels = uniqueSumValues(~(uniqueSumValues == lid));
backgroundLabel = uniqueSumValues(uniqueSumValues == lid);

allSamples = dsImg;
[a,b,c] = find(dsImg == lid);
for k=1:length(a)
    dsImg(a(k),b(k)) = 0;
end

dsImgShow = dsImg - min(dsImg(:));
dsImgShow = dsImgShow ./ max(dsImgShow(:));
figure('name', 'sparse forground (cluster) samples');
imshow(im2double(dsImgShow));

% do the statistics here
forgroundSamples = dsImg;
backgroundSamples = (allSamples > 0)-(forgroundSamples > 0);

figure('name', 'sparse background (cluster) samples');
imshow(im2double(backgroundSamples));

totalPixelCount = size(gtImg,1)*size(gtImg,2);
samplesUsedCount = sum(sum(allSamples > 0));

% fraction between the used samples and the total number of pixels
density = samplesUsedCount/totalPixelCount;


% transform forground cluster labels to their color value in gt img
forgroundAsColor = zeros(1, length(forgroundLabels));
for k=1:length(forgroundLabels),
    gtColorId = find(gtColor2ClusterLabel(1,:) == forgroundLabels(k));
    if isempty(gtColorId)
        forgroundAsColor(k) = -1;
        continue;
    end
    colorValueOfCurrentCluster = gtColor2ClusterLabel(2, gtColorId);
    forgroundAsColor(k) = colorValueOfCurrentCluster;
end

if SIMPLIFIED_STATISTICS
    % samples classified as forground that actually also belong to the
    % forground.
    TP = forgroundSamples.*((forgroundSamples > 0) & (gtImg > 0));
    TP_Count = sum(sum(TP > 0));

    % samples classified to background that actually belong to forground
    fn_mask = zeros(size(gtImg));
    for k = 1:length(forgroundAsColor)
        fn_mask = fn_mask | (gtImg == forgroundAsColor(k));
    end
    
    FN = backgroundSamples .*(backgroundSamples > 0) & fn_mask;
    FN_Count = sum(sum(FN > 0));

    % samples classified to forground that do belong to the background
    FP = (forgroundSamples > 0) - (TP > 0);
    FP_Count = sum(sum(FP > 0));

    % measure between correct classifications and additional (wrong) hits.
    % can be good, if there are samples missing, but no uncorrect assignments
    % were made (extreme case, only one assignment 
    % (i.e. lots of missing classifications) but no wrong classifications) 
    precission = TP_Count / (TP_Count + FP_Count);

    % measure indicating some relationship between incorrectly classified
    % background samples (that should belong to the forground) and TP.
    % get worse the more samples are missing.
    recall = TP_Count / (TP_Count + FN_Count);
    F1_score = 2*((precission*recall) / (precission+recall));

    disp(['density: ', num2str(100*density), '%'])
    disp(['precission: ', num2str(100*precission), '%'])
    disp(['recall: ', num2str(100*recall), '%'])
    disp(['F1 score: ', num2str(100*F1_score), '%'])    
else
    
    % forgroundLabels = unique(forgroundSamples(:,:));
    % forgroundLabels = forgroundLabels(find(forgroundLabels > 0));

    %backgroundLabel = unique(backgroundSamples(:,:));
    %backgroundLabel = backgroundLabel(find(backgroundLabel > 0));
    
    %gtLabels = unique(gtImg(:,:));
    %gtLabels = gtLabels(find(gtLabels > 0));

    % count how many time a gt mask got assigned by a segmented cluster
    clusterPerMaskCount = zeros(size(gtLabels));
   
    % samples classified to background that actually belong to forground
    fn_mask = zeros(size(gtImg));
    for k = 1:length(forgroundAsColor)
        fn_mask = fn_mask | (gtImg == forgroundAsColor(k));
    end
    FN = backgroundSamples .*(backgroundSamples > 0) & fn_mask;
    FN_Count = sum(sum(FN > 0));
    
    avg_F1_score = 0;
    avg_precission = 0;
    avg_recall = 0;
    for k=1:length(forgroundLabels)
        curr_flabel = forgroundLabels(k);
        
        
        current_fg_mask = (forgroundSamples == curr_flabel);
        figure('name', strcat('mask nr: ', num2str(k)));
        imshow(current_fg_mask);
        
        
        % find best matching gt mask label
        % note that several (detected) clusters clusers can be assigned to
        % the same gt mask.

        
        gtColorId = find(gtColor2ClusterLabel(1,:) == curr_flabel);
        colorValueOfCurrentCluster = gtColor2ClusterLabel(2, gtColorId);
        
        % increment cluster count
        gt_mask_idx = find(gtLabels == curr_flabel);
        clusterPerMaskCount(gt_mask_idx) = clusterPerMaskCount(gt_mask_idx) + 1;
        
        if sum(current_fg_mask(:)) == 0
            TP = zeros(size(current_fg_mask));
        else
            TP = forgroundSamples.*(current_fg_mask & (gtImg == colorValueOfCurrentCluster));
            
        end
        TP_Count = sum(sum(TP > 0));
        % samples classified to forground that do belong to the background
        FP = (forgroundSamples == curr_flabel) - (TP > 0);
        FP_Count = sum(sum(FP > 0));
        
        if ~(TP_Count + FP_Count == 0)
            avg_precission = avg_precission + (TP_Count / (TP_Count + FP_Count));
        end
        
        if ~(TP_Count + FN_Count == 0)
            avg_recall = avg_recall + (TP_Count / (TP_Count + FN_Count));
        end
        avg_F1_score = avg_F1_score + 2*((avg_precission*avg_recall) / (avg_precission+avg_recall));
    end
    
    precission = avg_precission / length(forgroundLabels);
    recall = avg_recall / length(forgroundLabels);
    F1_score = 2 * ((precission * recall) / (precission + recall));
    
    disp(['density: ', num2str(100*density), '%'])
    disp(['precission: ', num2str(100*precission), '%'])
    disp(['recall: ', num2str(100*recall), '%'])
    disp(['F1 score: ', num2str(100*F1_score), '%'])
    
    gtMaskCount = length(clusterPerMaskCount);
    summed_gt_mask_weights = sum(clusterPerMaskCount);
    ratioMaskWeightsMaskCount = summed_gt_mask_weights / gtMaskCount;
    disp(['Fragmentation: ', num2str(ratioMaskWeightsMaskCount)])
    disp(['Ground truth Mask cluster Hits: '])
    disp([clusterPerMaskCount'])
end

%%
X = meshgrid(0:0.01:1);
Y = X';
Z = (X.*Y)./(X+Y);

figure('name', 'F-score isobars')
contourf(X,Y,Z)
hold on
plot(recall, precission, 'rx')
xlabel('recall')
ylabel('precission')
