clc;
%clear all;
%close all;

addpath('src');
addpath('../matlab_shared');
addpath('../libs/flow-code-matlab');
addpath('../libs/GCMex/');

DATASET = 'alley2small';
METHODNAME = 'ldof';
PREFIX_OUTPUT_FILENAME = 'mc_d';
PREFIX_INPUT_FILENAME = 'md_nn_1000_best';

% should the eigendecomposition be computed
COMPUTE_EIGS = false;

% should the numerical fast eigs method be used
USE_EIGS = true;

% iterate over all existing images in sequence
COMPUTE_FULL_RANGE = false;
% 

% use a prespecified number of eigenvectors
USE_CLUSER_EW_COUNT = true;
FORCE_EW_COUNT = 12;

THRESH = 0.0000;

% weight of smoothness term.
% well working smoothness weight: NU = 0.000000001
NU = 0.000000001;
NU = 0.000000001;


% number of clusters we want to segment the given sequence
CLUSTER_CENTER_COUNT = 8;

%
% RUN_MODE = 1 => vis segmentation
% RUN_MODE = 2 => vis affinities
% RUN_MODE = 3 => vis eigenvector
RUN_MODE = 1;

SHOW_LOCAL_VAR = false;

% show the segmentation figure
SHOW_SEGMENTATION = false;

% saves the figure as an image and also opens a new figure per image
SAVE_FIGURES = true;

% filter all eigenvectors that belong to eigenvalues <= 0
FILTER_ZERO_EIGENVALUES = false;

SELECT_AFFINITY_IDX = false;
SELECTED_ENTITY_IDX = 64;
SELECTED_ENTITY_IDX = 1;
frame_idx = 40;

% number of iterations that should be performed for computing the approx.
num_of_iters = 3;

PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_iters_', num2str(num_of_iters), '_c_', num2str(CLUSTER_CENTER_COUNT));
if USE_CLUSER_EW_COUNT
    PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_ev_', num2str(FORCE_EW_COUNT));
end
PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_nu_', num2str(NU));
%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U_full = 1; S_full = 1;
end
[W, U, S, U_full, S_full] = run_min_cut(DATASET, METHODNAME, RUN_MODE, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, W, SELECTED_ENTITY_IDX, frame_idx, USE_CLUSER_EW_COUNT, num_of_iters, FORCE_EW_COUNT, U_full, S_full, COMPUTE_FULL_RANGE, SAVE_FIGURES, SHOW_SEGMENTATION, PREFIX_OUTPUT_FILENAME, PREFIX_INPUT_FILENAME, NU, FILTER_ZERO_EIGENVALUES);