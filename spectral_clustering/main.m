clc;
%clear all;
close all;

DATASET = 'car2';
COMPUTE_EIGS = true;
USE_EIGS = true;
STEPSIZE_DATA = 8;
LAMBDA = 0.1;
USE_T = false;
SHOULD_LOAD_W = true;
PERFORM_AUTO_RESCALE = true;
USE_CLUSER_EW_COUNT = false;

THRESH = 0%0.0001;
CLUSTER_CENTER_COUNT =5;
RESACLE_W = false;

USE_W_VEC = false;
USE_CLUSTERING_CUE = false;

SELECTED_ENTITY_IDX = 5;
frame_idx = 1;

%% autorescale
% len = size(W,1);
% scale = sum(W(:))/(len*len)
% f = (0.2/scale)*(len*len);
% W_rescaled = W*f;

% if (RESACLE_W)
%     w = -(log(W)/7000);
%     WWW = W;
%     W = (exp(-w*18000));
% end
%%
if exist('W','var') == 0
    disp('setting initial values...')
    W = 1; U = 1; S=1; WW = 1;
end
[W, U, S, WW] = run_clustering(DATASET, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U, S, SELECTED_ENTITY_IDX, USE_T, frame_idx, WW, SHOULD_LOAD_W, PERFORM_AUTO_RESCALE, LAMBDA, USE_CLUSER_EW_COUNT);


