clc;
%clear all;
%close all;

addpath('src');
addpath('../matlab_shared');
addpath('../libs/flow-code-matlab');
addpath('../libs/GCMex/');

DATASET = 'bonn_watercan_713_3_884_LRGBD';
METHODNAME = 'lrgbd';
PREFIX_OUTPUT_FILENAME = 'knorke';
PREFIX_INPUT_FILENAME = 'ped_top_202';

% should the eigendecomposition be computed
COMPUTE_EIGS = true;

% should we use simple coloring scheme
USE_SIMPLE_COLORS = true;

% Should the label assignment be re-used
REUSE_LABEL_ASSIGNMENT = false;



% iterate over all existing images in sequence
COMPUTE_FULL_RANGE = false;
% 

% use a prespecified number of eigenvectors
USE_CLUSER_EW_COUNT = true;
FORCE_EW_COUNT = 18;

THRESH = 0.0000;

% weight of smoothness tem.
% well working smoothness weight: NU = 0.000000001
NU = 0.000000001;
NU = 0.00000000001;
NU = 0.00000001;
NU = 0.00000001;
%NU = 0.000001;

NU = 0.00000001;
NU = 0.00000001
% number of clusters we want to segment the given sequence
CLUSTER_CENTER_COUNT = 12;

SHOULD_EXCLUDE_U_IDXS = false;
EXCLUDED_U_IDXS = [3];


%
% RUN_MODE = 1 => vis segmentation
% RUN_MODE = 2 => vis affinities
% RUN_MODE = 3 => vis eigenvector
RUN_MODE = 1;

SHOW_LOCAL_VAR = false;

% show the segmentation figure
SHOW_SEGMENTATION = true;

% saves the figure as an image and also opens a new figure per image
SAVE_FIGURES = true;

% filter all eigenvectors that belong to eigenvalues <= 0
FILTER_ZERO_EIGENVALUES = false;

SELECT_AFFINITY_IDX = false;
SELECTED_ENTITY_IDX = 64;
SELECTED_ENTITY_IDX = 1;
frame_idx = 4;

% number of iterations that should be performed for computing the approx.
num_of_iters = 30;



%for cc=2:6
%for evc=2:6 
%CLUSTER_CENTER_COUNT = cc;
%FORCE_EW_COUNT = evc;


PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_iters_', num2str(num_of_iters), '_c_', num2str(CLUSTER_CENTER_COUNT));
if USE_CLUSER_EW_COUNT
    PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_ev_', num2str(FORCE_EW_COUNT));
end
PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_nu_', num2str(NU));
%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U_full = 1; S_full = 1; label_assignments = 1;
end

% should the numerical fast eigs method be used
USE_EIGS = true;

if (SHOULD_EXCLUDE_U_IDXS)
    FORCE_EW_COUNT = FORCE_EW_COUNT + length(EXCLUDED_U_IDXS);
end

[W, U, S, U_full, S_full, label_assignments] = run_min_cut(DATASET, METHODNAME, RUN_MODE, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, W, SELECTED_ENTITY_IDX, frame_idx, USE_CLUSER_EW_COUNT, num_of_iters, FORCE_EW_COUNT, U_full, S_full, COMPUTE_FULL_RANGE, SAVE_FIGURES, SHOW_SEGMENTATION, PREFIX_OUTPUT_FILENAME, PREFIX_INPUT_FILENAME, NU, FILTER_ZERO_EIGENVALUES, REUSE_LABEL_ASSIGNMENT, label_assignments, USE_SIMPLE_COLORS, SHOULD_EXCLUDE_U_IDXS, EXCLUDED_U_IDXS);
%end
%end