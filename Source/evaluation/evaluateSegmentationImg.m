% evaluates graphically segmenation results, given the graphical
% segmentation, the gt mask and the amb. mask.

DATASET = 'cars';
FRAME_IDX = 90;
FILTER_AMBIGUOUS = true;

% load segmentation image
[FileName, FilePath, ~] = uigetfile('.ppm');
segmentationFilePathName = strcat(FilePath, FileName);

%% define gt images
BASE_PATH = strcat('../../Data/', DATASET, '/gt/');
gtFileName = strcat(BASE_PATH, num2str(FRAME_IDX), '.png');
if FILTER_AMBIGUOUS
    gAmbFileName = strcat(BASE_PATH, num2str(FRAME_IDX), '_amb.png');
    gtAmbImg = rgb2gray(im2double(imread(gAmbFileName)));
end

% load mask data
gtImg = rgb2gray(im2double(imread(gtFileName)));
segmentationImg = im2double(imread(segmentationFilePathName));
segmentationImg = rgb2gray(segmentationImg);
rawSegmentLabels = unique(segmentationImg);
% exclude white background
rawSegmentLabels = rawSegmentLabels(rawSegmentLabels ~= 1);

%exclude amb. regions from masks
if FILTER_AMBIGUOUS
    invalidRegions = (gtAmbImg == 0);
    validSegments = (segmentationImg ~= 1).*(1-invalidRegions);
    segmentationImg = segmentationImg.*validSegments;
    [rows, cols] = find(segmentationImg == 0);
    for k=1:length(rows)
        segmentationImg(rows(k), cols(k)) = 1;
    end
end

% extract background and foreground labels
gtLabels = unique(gtImg);
backgroundLabel = -1;
maxLabelCount = -1;
for k=1:length(gtLabels)
    labelCount = find(gtImg == gtLabels(k));
    if length(labelCount) > maxLabelCount
        maxLabelCount = length(labelCount);
        backgroundLabel = gtLabels(k);
    end
end
forgroundLabels = gtLabels(gtLabels ~= backgroundLabel);

% merge generate segment labels to closest gt label
for k=1:length(rawSegmentLabels)
    segmentationLabel = rawSegmentLabels(k);
    
    
    segmentMask = (segmentationImg == segmentationLabel);
    
    % intersect with gt and count
    maxIntersectionCount = -1;
    correspondingGTLabel = -1;
    for l=1:length(gtLabels)
        gtLabel = gtLabels(l);
        gtMask = (gtImg == gtLabel);
        intersectionCount = sum(sum(segmentMask & gtMask));
        if intersectionCount > maxIntersectionCount
            maxIntersectionCount = intersectionCount;
            correspondingGTLabel = gtLabel;
        end
    end
    
    % update old img
    [rows, cols] = find(segmentationImg == segmentationLabel);
    for idx=1:length(rows)
        segmentationImg(rows(idx), cols(idx)) = correspondingGTLabel;
    end
    
    % update old label
    rawSegmentLabels(k) = correspondingGTLabel;
end
rawSegmentLabels = unique(rawSegmentLabels);



% all samples and background samples
allSamples = segmentationImg ~= 1;
backgroundSamples = (segmentationImg == backgroundLabel);
forgroundSampleMask = (allSamples > 0)-(backgroundSamples > 0);
forgroundSamples = segmentationImg.*forgroundSampleMask;


% compute false negatives (FN)
combinedFGMasks = zeros(size(gtImg));
for k = 1:length(forgroundLabels)
    combinedFGMasks = combinedFGMasks | (gtImg == forgroundLabels(k));
end
FN = backgroundSamples .*(backgroundSamples > 0) & combinedFGMasks;
FN_Count = sum(sum(FN > 0));

% foreach forground label
avg_precission = 0;
avg_recall = 0;
for k=1:length(forgroundLabels)
    fgLabel = forgroundLabels(k);
    current_fg_mask = (forgroundSamples == fgLabel);

    if sum(current_fg_mask(:)) == 0
        TP = zeros(size(current_fg_mask));
    else
        TP = forgroundSamples.*(current_fg_mask & (gtImg == fgLabel));
    end
    TP_Count = sum(sum(TP > 0));

    FP = (forgroundSamples == fgLabel) - (TP > 0);
    FP_Count = sum(sum(FP > 0));

    if ~(TP_Count + FP_Count == 0)
        avg_precission = avg_precission + (TP_Count / (TP_Count + FP_Count));
    end

    if ~(TP_Count + FN_Count == 0)
        avg_recall = avg_recall + (TP_Count / (TP_Count + FN_Count));
    end
    
end
precission = avg_precission / length(forgroundLabels);
recall = avg_recall / length(forgroundLabels);
F1_score = 2 * ((precission * recall) / (precission + recall));

disp(['precission: ', num2str(100*precission), '%'])
disp(['recall: ', num2str(100*recall), '%'])
disp(['F1 score: ', num2str(100*F1_score), '%'])
