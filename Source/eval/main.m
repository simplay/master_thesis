addpath('../libs/flow-code-matlab');

%% load ground truth img
DS = 'car2';
imgName = '01.pgm';
DS_BASE_PATH = strcat('../data/ldof/',DS,'/gt/',imgName);
gtImg = imread(DS_BASE_PATH);
figure('name', 'ground truth')
imshow(gtImg);

%% load eval img
% load label

SIMPLIFIED_STATISTICS = false;
disp(['Running simplified statistics mode: ', num2str(SIMPLIFIED_STATISTICS)])

CF_PATH = '../output/clustering/';
suffix = 'labels_1.txt';

cf_fname = strcat(CF_PATH,DS,'_',suffix);
fid = fopen(cf_fname);
label_cluster_assignments = dlmread(cf_fname);

BASE = '../output/similarities/';
label_mappings = labelfile2mat(strcat(BASE,DS));
img_index = 1;
STEPSIZE_DATA = 8;
DATASET = DS;
PREFIX_FRAME_TENSOR_FILE = [DATASET,'_step_',num2str(STEPSIZE_DATA),'_frame_'];
pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(img_index),'.mat'));
pixeltensor = pixeltensor.tracked_pixels;
[row_inds, col_inds, ~] = find(pixeltensor(:,:,2) > 0);

dsImg = zeros(size(gtImg));
for k=1:length(row_inds)
    pixel_label = pixeltensor(row_inds(k), col_inds(k), 2);
    ax = pixeltensor(row_inds(k), col_inds(k), 3);
    ay = pixeltensor(row_inds(k), col_inds(k), 4);
    
    
    % since the first label is 2 but the first lookup index is 1
    %transformed_pixel_label = label_to_vector_index(label_mappings, pixel_label); % pixel_label - 1;
    if isempty(pixel_label) == 0
        [a,~,~] = find(label_cluster_assignments(:,1) == pixel_label);
        c_val = label_cluster_assignments(a,2);
        if isempty(c_val) == 0
            dsImg(ax, ay) = c_val;
        end
    end

end
figure('name', 'all samples')
dsImgShow = dsImg - min(dsImg(:));
dsImgShow = dsImgShow ./ max(dsImgShow(:));
imshow(im2double(dsImgShow));

labels = unique(label_cluster_assignments(:,2));
TL = -1;
lid = -1;
for k=1:length(labels)
    l = labels(k);
    lenL = length(find(dsImg== l));
    if lenL > TL
        TL = lenL;
        lid = l;
    end
end

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

if SIMPLIFIED_STATISTICS
    % samples classified as forground that actually also belong to the
    % forground.
    TP = forgroundSamples.*((forgroundSamples > 0) & (gtImg > 0));
    TP_Count = sum(sum(TP > 0));

    % samples classified to background that actually belong to forground
    FN = backgroundSamples .*(backgroundSamples > 0) & (gtImg > 0);
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
    
    forgroundLabels = unique(forgroundSamples(:,:));
    forgroundLabels = forgroundLabels(find(forgroundLabels > 0));

    backgroundLabel = unique(backgroundSamples(:,:));
    backgroundLabel = backgroundLabel(find(backgroundLabel > 0));
    
    gtLabels = unique(gtImg(:,:));
    gtLabels = gtLabels(find(gtLabels > 0));

    % count how many time a gt mask got assigned by a segmented cluster
    clusterPerMaskCount = zeros(size(gtLabels));
   
    % samples classified to background that actually belong to forground
    FN = backgroundSamples .*(backgroundSamples > 0) & (gtImg > 0);
    FN_Count = sum(sum(FN > 0));
    
    avg_F1_score = 0;
    avg_precission = 0;
    avg_recall = 0;
    for k=1:length(forgroundLabels)
        curr_flabel = forgroundLabels(k);
        
        
        current_fg_mask = (forgroundSamples == curr_flabel);
        
        % find best matching gt mask label
        % note that several (detected) clusters clusers can be assigned to
        % the same gt mask.
        bestVotingScore = -1;
        best_gt_Label = -1;
        for t = 1: length(gtLabels)
            curr_gt_label = gtLabels(t);
            votingScore = sum(sum((gtImg == curr_gt_label & current_fg_mask) > 0));
            if votingScore > bestVotingScore
                bestVotingScore = votingScore;
                best_gt_Label = curr_gt_label;
            end
        end
        
        % increment cluster count
        gt_mask_idx = find(gtLabels == best_gt_Label);
        clusterPerMaskCount(gt_mask_idx) = clusterPerMaskCount(gt_mask_idx) + 1;
        
        TP = forgroundSamples.*(current_fg_mask & (gtImg == best_gt_Label));
        TP_Count = sum(sum(TP > 0));

        % samples classified to forground that do belong to the background
        FP = (forgroundSamples == curr_flabel) - (TP > 0);
        FP_Count = sum(sum(FP > 0));

        avg_precission = avg_precission + (TP_Count / (TP_Count + FP_Count));
        avg_recall = avg_recall + (TP_Count / (TP_Count + FN_Count));
        avg_F1_score = avg_F1_score + 2*((precission*recall) / (precission+recall));
    end
    
    precission = avg_precission / length(forgroundLabels);
    recall = avg_recall / length(forgroundLabels);
    F1_score = avg_F1_score / length(forgroundLabels);
    
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
