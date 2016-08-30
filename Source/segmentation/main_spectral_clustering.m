clc;
%clear all;
%close all;

addpath('src');
addpath('../matlab_shared');
addpath('../libs/flow-code-matlab');

DATASET = 'bonn_chairs_263_3_434';
METHODNAME = 'ldof';
BASE_PREFIX_OUTPUT_FILENAME = 'pd_top_100_flows';
PREFIX_INPUT_FILENAME = 'pd_top_100_flows';

% should the eigendecomposition be computed
COMPUTE_EIGS = true;

% Should the previousely label assignments be re-used.
% if set to `true,` then when changing either the CLUSTER COUNT 
% or the EW count won't have any effect.
REUSE_ASSIGNMENTS = false;

% should all eigenvectors that belong to eigenvalues <= 0 be filtered.
FILTER_ZERO_EIGENVALUES = false;

% iterate over all existing images in sequence
COMPUTE_FULL_RANGE = false;

% should we use simple coloring scheme
USE_SIMPLE_COLORS = true;

% use a prespecified number of eigenvectors
USE_CLUSER_EW_COUNT = true;
FORCE_EW_COUNT = 10;

% number of clusters we want to segment the given sequence
CLUSTER_CENTER_COUNT = 10;

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

SELECT_AFFINITY_IDX = false;
% 265
SELECTED_ENTITY_IDX = 1;
frame_idx = 1;

for CLUSTER_CENTER_COUNT=10:10
for FORCE_EW_COUNT=10:10

        
    
PREFIX_OUTPUT_FILENAME = strcat(BASE_PREFIX_OUTPUT_FILENAME, '_c_', num2str(CLUSTER_CENTER_COUNT));
if USE_CLUSER_EW_COUNT
    PREFIX_OUTPUT_FILENAME = strcat(BASE_PREFIX_OUTPUT_FILENAME, '_c_', num2str(CLUSTER_CENTER_COUNT), '_ev_', num2str(FORCE_EW_COUNT));
end
%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U_full = 1; S_full = 1; label_assignments = 1;
end

% Do not change these parameters set below in productive code, just for
% explorative/debugging purposes

% should the numerical fast eigs method be used
USE_EIGS = true;

% Additional additive bias to make computation more reliable
THRESH = 0.0000;

[W, U, S, U_full, S_full, label_assignments] = run_clustering(DATASET, METHODNAME, RUN_MODE, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, W, SELECTED_ENTITY_IDX, frame_idx, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, FORCE_EW_COUNT, U_full, S_full, COMPUTE_FULL_RANGE, SAVE_FIGURES, SHOW_SEGMENTATION, PREFIX_OUTPUT_FILENAME, PREFIX_INPUT_FILENAME, FILTER_ZERO_EIGENVALUES, REUSE_ASSIGNMENTS, label_assignments, USE_SIMPLE_COLORS);

end
end