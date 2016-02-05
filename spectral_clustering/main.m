% c25 W = (exp(-w*(LAMBDA/f)*0.3)); works best


clc;
%clear all;
%close all;

DATASET = 'c14';
USE_SPECIAL_NAMING = false;

COMPUTE_EIGS = false;
USE_EIGS = true;
STEPSIZE_DATA = 8;
LAMBDA = 0.1%1% 7000%0.1;
USE_T = false;
SHOULD_LOAD_W = true;
PERFORM_AUTO_RESCALE = false;

% use a prespecified number of eigenvectors
USE_CLUSER_EW_COUNT = true;
FORCE_EW_COUNT = 5;

THRESH = 0.00001;
CLUSTER_CENTER_COUNT =4;


USE_W_VEC = false;
USE_CLUSTERING_CUE = false;

SHOW_LOCAL_VAR = false;
VAR_IMG = 1;

SELECT_AFFINITY_IDX = false
SELECTED_ENTITY_IDX = 302
SELECTED_ENTITY_IDX = 5
frame_idx = 1;

%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U = 1; S=1; WW = 1;
end
[W, U, S, WW] = run_clustering(DATASET, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U, S, SELECTED_ENTITY_IDX, USE_T, frame_idx, WW, SHOULD_LOAD_W, PERFORM_AUTO_RESCALE, LAMBDA, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, SHOW_LOCAL_VAR, VAR_IMG, FORCE_EW_COUNT, USE_SPECIAL_NAMING);


