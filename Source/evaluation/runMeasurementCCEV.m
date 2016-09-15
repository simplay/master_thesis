%clear all


addpath('../libs/flow-code-matlab');
addpath('../matlab_shared');

%% load ground truth img
LABELS_FILE_NAME = 'labels.txt';

DATASET = 'cars';
%DATASET = 'bonn_chairs_263_3_434'
%DATASET = 'bonn_cerealbox_150_3_450';
IMG_IDXS = [15, 30, 45]; % bonn_chairs_263_3_434
%IMG_IDXS = [40, 60, 80]; % bonn_cerealbox_150_3_450
IMG_IDXS = [4, 30, 54]; % bonn_watercan_713_3_884
IMG_IDXS = [1]
STEPSIZE_DATA = 8;
%PREFIX_INPUT_FILENAME = 'ped_top_100_lambda_5';
METHODNAME = 'ldof';
FILTER_AMBIGUOUS = false;
MUTED = true;

%DS_PREFIX = 'pd_top_100_lambda_'
DS_PREFIX = 'pd_top_100_lambda_0_01'

%[FileName, FilePath, ~] = uigetfile('.txt');
%LABELS_FILE_PATH = strcat(FilePath, FileName);
OUT_PATH = '../output/cluster_merges/';

cc = 3:7;
ev = 3:12;
FileName = 'labels.txt';
stats = zeros(length(cc),length(ev),3);
tCount = 1;
ttCount = 1;
for tt=cc
for t=ev
    
suffix = strcat('_ccev_c_',num2str(tt), '_ev_',num2str(t));    
PREFIX_INPUT_FILENAME = [DS_PREFIX];
FilePath = strcat('../output/clustering/', DATASET, '_ldof_', DS_PREFIX, suffix, '/');
LABELS_FILE_PATH = [FilePath, FileName];


for k = 1:length(IMG_IDXS)
    img_index = IMG_IDXS(k);
    [ figure_dir_name ] = runMerger( DATASET, PREFIX_INPUT_FILENAME, METHODNAME, img_index, FILTER_AMBIGUOUS, FilePath, FileName);
    mergedInputLabels = strcat(figure_dir_name, LABELS_FILE_NAME);
    [ precission, recall, F1_score ] = computeStatMeasures( DATASET, img_index, PREFIX_INPUT_FILENAME, METHODNAME, FILTER_AMBIGUOUS, mergedInputLabels);
    disp(['Using lambda=', lambda, ' img=', num2str(img_index)])
    stats(ttCount, tCount, 1) = stats(ttCount, tCount, 1) + precission;
    stats(ttCount, tCount, 2) = stats(ttCount, tCount, 2) + recall;
    stats(ttCount, tCount, 3) = stats(ttCount, tCount, 3) + F1_score;
    
end
tCount = tCount + 1;
end
tCount = 1;
ttCount = ttCount + 1;
end

stats = stats / length(IMG_IDXS);