% c25 W = (exp(-w*(LAMBDA/f)*0.3)); works best


clc;
%clear all;
%close all;
addpath('src');
addpath('../matlab_shared');
addpath('../libs/flow-code-matlab');

DATASET = 'c14';
METHODNAME = 'ldof';

USE_SPECIAL_NAMING = false;

COMPUTE_EIGS = true;
USE_EIGS = true;
STEPSIZE_DATA = 8;
LAMBDA = 0.1%1% 7000%0.1;
USE_T = false;
SHOULD_LOAD_W = true;
PERFORM_AUTO_RESCALE = false;

% 
USE_BF_BOUND = false;
BOUNDARY = [1,9];

% use a prespecified number of eigenvectors
USE_CLUSER_EW_COUNT = true;
FORCE_EW_COUNT = 8;

THRESH = 0.0000;
CLUSTER_CENTER_COUNT = 3;


USE_W_VEC = false;
USE_CLUSTERING_CUE = true;

SHOW_LOCAL_VAR = false;
VAR_IMG = 1;

SELECT_AFFINITY_IDX = false
SELECTED_ENTITY_IDX = 64
SELECTED_ENTITY_IDX = 1
frame_idx = 1;

%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U = 1; S=1; WW = 1;
end
[W, U, S, WW] = run_clustering(DATASET, METHODNAME, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U, S, SELECTED_ENTITY_IDX, USE_T, frame_idx, WW, SHOULD_LOAD_W, PERFORM_AUTO_RESCALE, LAMBDA, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, SHOW_LOCAL_VAR, VAR_IMG, FORCE_EW_COUNT, USE_SPECIAL_NAMING, USE_BF_BOUND, BOUNDARY);

