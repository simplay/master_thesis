clc;
%clear all;
%close all;

addpath('src');
addpath('../matlab_shared');
addpath('../libs/flow-code-matlab');

DATASET = 'chair_3_cast';
METHODNAME = 'ldof';
PREFIX_OUTPUT_FILENAME = 'md_spec_clust_foobar';
PREFIX_INPUT_FILENAME = 'md';

COMPUTE_EIGS = false;
USE_EIGS = true;

% iterate over all existing images in sequence
COMPUTE_FULL_RANGE = false;
% 

% use a prespecified number of eigenvectors
USE_CLUSER_EW_COUNT = true;
FORCE_EW_COUNT = 20;

THRESH = 0.0000;
CLUSTER_CENTER_COUNT = 8;

USE_W_VEC = false;
USE_CLUSTERING_CUE = true;

SHOW_LOCAL_VAR = false;

% show the segmentation figure
SHOW_SEGMENTATION = false;

% saves the figure as an image and also opens a new figure per image
SAVE_FIGURES = true;

SELECT_AFFINITY_IDX = false;
SELECTED_ENTITY_IDX = 64;
SELECTED_ENTITY_IDX = 1;
frame_idx = 40;

PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_c_', num2str(CLUSTER_CENTER_COUNT));
if USE_CLUSER_EW_COUNT
    PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_ev_', num2str(FORCE_EW_COUNT));
end
%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U_full = 1; S_full = 1;
end
[W, U, S, U_full, S_full] = run_clustering(DATASET, METHODNAME, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, SELECTED_ENTITY_IDX, frame_idx, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, FORCE_EW_COUNT, U_full, S_full, COMPUTE_FULL_RANGE, SAVE_FIGURES, SHOW_SEGMENTATION, PREFIX_OUTPUT_FILENAME, PREFIX_INPUT_FILENAME);