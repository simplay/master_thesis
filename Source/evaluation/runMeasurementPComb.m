%clear all


addpath('../libs/flow-code-matlab');
addpath('../matlab_shared');

%% load ground truth img
LABELS_FILE_NAME = 'labels.txt';

DATASET = 'cars';
%DATASET = 'bonn_chairs_263_3_434'
%DATASET = 'bonn_cerealbox_150_3_450';
DATASET = 'bonn_watercan_713_3_884'
%IMG_IDXS = [15, 30, 45]; % bonn_chairs_263_3_434
%IMG_IDXS = [40, 60, 80]; % bonn_cerealbox_150_3_450
IMG_IDXS = [4, 30, 54]; % bonn_watercan_713_3_884
%IMG_IDXS = [1]
STEPSIZE_DATA = 8;
%PREFIX_INPUT_FILENAME = 'ped_top_100_lambda_5';
METHODNAME = 'ldof';
FILTER_AMBIGUOUS = true;
MUTED = true;

%DS_PREFIX = 'pd_top_100_lambda_'
DS_PREFIX = 'pd_top_100_flows';

%[FileName, FilePath, ~] = uigetfile('.txt');
%LABELS_FILE_PATH = strcat(FilePath, FileName);
OUT_PATH = '../output/cluster_merges/';

%lambdas = {'1', '0_1', '0_01', '0_001', '0_0001'};
%lambdas = {'1'};
%lambdas = {'100', '50', '10', '5', '1', '0_1'};
FileName = 'labels.txt';
%stats = zeros(length(lambdas), 3);
stats = zeros(1, 3);
 %for t=1:length(lambdas)
%lambda = lambdas{t};
%PREFIX_INPUT_FILENAME = [DS_PREFIX, lambda];
PREFIX_INPUT_FILENAME = [DS_PREFIX];
FilePath = strcat('../output/clustering/', DATASET, '_', METHODNAME, '_', DS_PREFIX,lambda, '_c_10_ev_10/');
FilePath = strcat('../output/clustering/', DATASET, '_', METHODNAME, '_', DS_PREFIX, '_c_10_ev_10/');
LABELS_FILE_PATH = [FilePath, FileName];

t = 1;
for k = 1:length(IMG_IDXS)
    img_index = IMG_IDXS(k);
    [ figure_dir_name ] = runMerger( DATASET, PREFIX_INPUT_FILENAME, METHODNAME, img_index, FILTER_AMBIGUOUS, FilePath, FileName);
    mergedInputLabels = strcat(figure_dir_name, LABELS_FILE_NAME);
    [ precission, recall, F1_score ] = computeStatMeasures( DATASET, img_index, PREFIX_INPUT_FILENAME, METHODNAME, FILTER_AMBIGUOUS, mergedInputLabels);
    disp(['Using lambda=', lambda, ' img=', num2str(img_index)])
    stats(t, 1) = stats(t, 1) + precission;
    stats(t, 2) = stats(t, 2) + recall;
    stats(t, 3) = stats(t, 3) + F1_score;
end

%end

stats = stats / length(IMG_IDXS);