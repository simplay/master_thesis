clc;
%clear all;
close all;

DATASET = 'car2';
COMPUTE_EIGS = true;
USE_EIGS = true;
STEPSIZE_DATA = 8;
USE_T = false;
SHOULD_LOAD_W = true;
THRESH = 0.00;01;
CLUSTER_CENTER_COUNT = 3;

USE_W_VEC = false;
USE_CLUSTERING_CUE = true;

SELECTED_ENTITY_IDX = 1;
frame_idx = 1;
%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U = 1; S=1; WW = 1;
end
[W, U, S, WW] = run_clustering(DATASET, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U, S, SELECTED_ENTITY_IDX, USE_T, frame_idx, WW, SHOULD_LOAD_W);


