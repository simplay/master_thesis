function run_sc(dataset, input_prefix, eigenvector_count, eigenvalue_count, output_prefix)
%RUN_SPECTRAL_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here
clc;
%clear all;
%close all;

addpath('src');
addpath('../matlab_shared');
addpath('../libs/flow-code-matlab');

DATASET = dataset;
METHODNAME = 'ldof';
PREFIX_OUTPUT_FILENAME = output_prefix;
PREFIX_INPUT_FILENAME = input_prefix;

% should the eigendecomposition be computed
COMPUTE_EIGS = true;

% Should the previousely label assignments be re-used.
% if set to `true,` then when changing either the CLUSTER COUNT 
% or the EW count won't have any effect.
REUSE_ASSIGNMENTS = false;

% should all eigenvectors that belong to eigenvalues <= 0 be filtered.
FILTER_ZERO_EIGENVALUES = true;

% iterate over all existing images in sequence
COMPUTE_FULL_RANGE = true;
% 

% use a prespecified number of eigenvectors
USE_CLUSER_EW_COUNT = true;
FORCE_EW_COUNT = eigenvector_count;

% number of clusters we want to segment the given sequence
CLUSTER_CENTER_COUNT = eigenvalue_count;

%
% RUN_MODE = 1 => vis segmentation
% RUN_MODE = 2 => vis affinities
% RUN_MODE = 3 => vis eigenvector
RUN_MODE = 1;

SHOW_LOCAL_VAR = true;

% show the segmentation figure
SHOW_SEGMENTATION = false;

% saves the figure as an image and also opens a new figure per image
SAVE_FIGURES = true;

SELECT_AFFINITY_IDX = false;
SELECTED_ENTITY_IDX = 6;
frame_idx = 1;

PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_c_', num2str(CLUSTER_CENTER_COUNT));
if USE_CLUSER_EW_COUNT
    PREFIX_OUTPUT_FILENAME = strcat(PREFIX_OUTPUT_FILENAME, '_ev_', num2str(FORCE_EW_COUNT));
end
%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U_full = 1; S_full = 1; assignments = 1;
end

% Do not change these parameters set below in productive code, just for
% explorative/debugging purposes

% should the numerical fast eigs method be used
USE_EIGS = true;

% Additional additive bias to make computation more reliable
THRESH = 0.0000;

[W, U, S, U_full, S_full, assignments] = run_clustering(DATASET, METHODNAME, RUN_MODE, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, W, SELECTED_ENTITY_IDX, frame_idx, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, FORCE_EW_COUNT, U_full, S_full, COMPUTE_FULL_RANGE, SAVE_FIGURES, SHOW_SEGMENTATION, PREFIX_OUTPUT_FILENAME, PREFIX_INPUT_FILENAME, FILTER_ZERO_EIGENVALUES, REUSE_ASSIGNMENTS, assignments, false);
end

