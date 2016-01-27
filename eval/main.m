addpath('../libs/flow-code-matlab');

%% load ground truth img
DS = 'cars1';
imgName = '01.pgm';
DS_BASE_PATH = strcat('../data/ldof/',DS,'/gt/',imgName);
gtImg = imread(DS_BASE_PATH);
figure('name', 'ground truth')
imshow(gtImg);

%% load eval img
% load label

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

