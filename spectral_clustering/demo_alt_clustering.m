% c25 W = (exp(-w*(LAMBDA/f)*0.3)); works best

% works best for
% K=4:
% DATASET = 'c14'; using c14 -d 1 -v 1
% USE_CLUSER_EW_COUNT = true;
% FORCE_EW_COUNT = 3;

clc;
%clear all;
%close all;

addpath('../libs/GCMex/');

DATASET = 'chair3';
USE_SPECIAL_NAMING = false;




COMPUTE_EIGS = true;
USE_EIGS = true;
STEPSIZE_DATA = 8;
LAMBDA = 0.1%1% 7000%0.1;
USE_T = false;
SHOULD_LOAD_W = true;
PERFORM_AUTO_RESCALE = false;

% use a prespecified number of eigenvectors
SHOULD_FILTER_NEGATIVE_EW = false;
USE_CLUSER_EW_COUNT = false;
FORCE_EW_COUNT = 5;

THRESH = 0.0000;
USE_BF_BOUND = false;
BOUND = [1,9];


USE_W_VEC = false;
USE_CLUSTERING_CUE = true;

SHOW_LOCAL_VAR = true;
VAR_IMG = 1;

SELECT_AFFINITY_IDX = false
SELECTED_ENTITY_IDX = 302
SELECTED_ENTITY_IDX = 1
frame_idx = 1;
img_index = frame_idx;
%function [W, U_small, S_small, WW] = run_clustering( DATASET, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U_small, S_small, SELECTED_ENTITY_IDX, USE_T, frame_idx, WW, SHOULD_LOAD_W, PERFORM_AUTO_RESCALE, LAMBDA, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, SHOW_LOCAL_VAR, VAR_IMG, FORCE_EW_COUNT, USE_SPECIAL_NAMING)
%RUN_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here

    % the following flag define what data should be displayed.
    % USE_CLUSTERING_CUE true => display segmentation
    % USE_CLUSTERING_CUE false && USE_W_VEC true => display affinities
    % USE_CLUSTERING_CUE false && USE_W_VEC => display eigenvectors
    %     USE_W_VEC = false;
    %     USE_CLUSTERING_CUE = true;

    addpath('../libs/flow-code-matlab');
    BASE = '../output/similarities/';
    % 'cars1_step_8_frame_';
    PREFIX_FRAME_TENSOR_FILE = [DATASET,'_step_',num2str(STEPSIZE_DATA),'_frame_'];
    
    if USE_SPECIAL_NAMING
        
        idx = regexp(DATASET,'v');
        DATASETNAME = DATASET(1:idx-1);
        PREFIX_FRAME_TENSOR_FILE = [DATASETNAME,'_step_',num2str(STEPSIZE_DATA),'_frame_'];
    else    
        DATASETNAME = DATASET;
    end
    
    METHODNAME = 'ldof'; %other,ldof
    DATASETP = strcat(DATASETNAME,'/');
    BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASETP);

    %% load appropriate data
    if COMPUTE_EIGS
        if SHOULD_LOAD_W %1 == 0
            fname = strcat(BASE,DATASET,'_sim.dat');
            W = load(fname);
        end
        
        if PERFORM_AUTO_RESCALE
            
            % a well performing example has for f:
            % when f = sum(W(:))/(length(W)^2)
            % then f == 0.0557
            well_scale = 0.0557
            len = size(W,1);
            scale = sum(W(:))/(len*len);
            f = (scale/well_scale);
            
            w = -(log(W)/LAMBDA);
            w(isinf(w)) = 1000000;
            WWW = W;
            %W = (exp(-w*(LAMBDA/f)));
            W = (exp(-w*(LAMBDA/f)*0.3));
        end
        
        WW = W + ones(size(W))*THRESH;

        %WW = W + diag(THRESH*ones(size(W,1),1));
        
        %sW = sort(W,2, 'descend'); ten = sW(:,100); thresh = repmat(ten, 1, size(W,2)); biggest = W.*(W>thresh); WW = max(biggest,biggest');
        d_a = sum(WW,2);
        D = diag(d_a);
        D12 = diag(sqrt(1./d_a));
        % keyboard;
        B = D12*(D-WW)*D12;
        if USE_EIGS
            [U_small,S_small,FLAG] = eigs(B,50,1e-6);
        else
            [U_small,S_small] = eig(B);
        end
        
        if USE_T
            T = zeros(size(U_small));
            for k=1:size(U_small,1)
            T(k,:) = U_small(k,:) ./ sqrt(sum(U_small(k,:).^2));
            end
            U_small = T;
        end
        
        
        d = diag(S_small);
        [d, s_idx] = sort(d);
        U_small = aggregate_mat_cols(U_small, s_idx);
        
        
        if USE_CLUSER_EW_COUNT
            aa = 1:FORCE_EW_COUNT;
        else
        
        [aa,~,~] = find(d < 0.1);
        end
        UU = U_small;


        U_small = aggregate_mat_cols(U_small, aa);
        S_small = d(aa);
        
        % filter the eigenvector that belongs to eigenvalue 0
        if SHOULD_FILTER_NEGATIVE_EW
            S_small = S_small(S_small > 0);
            U_small = U_small(:,S_small > 0);
        end
    end

    
    %% display segmentation and its data.

    % load label vector indices mappings
    label_mappings = labelfile2mat(strcat(BASE,DATASET));
    [boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH);

    % to help the user what values/index pairs can be displayed.
    show_usage_information(USE_W_VEC, USE_CLUSTERING_CUE, W, U_small);
    
    if USE_BF_BOUND
        boundaries(1) = BOUND(1);
        boundaries(2) = BOUND(2);
    end
    
    frames = loadAllTrajectoryLabelFrames(DATASET, boundaries(1), boundaries(2));
    col_sel = SELECTED_ENTITY_IDX;

    
    %%
    
    
    nn_fpath = strcat('../output/similarities/',DATASET,'_spnn.txt');
    spnn_indices = extract_spatial_neighbors(nn_fpath, label_mappings);
    
    % display data
    figure('name', 'Motion Segmentation')

    pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(img_index),'.mat'));
    pixeltensor = pixeltensor.tracked_pixels;
    [row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);

    CLUSTER_CENTER_COUNT = 5;
    
    m = size(UU,2);
    
    % initial assignments
    label_assignments = zeros(length(W), 1);
    
    %%
    USE_MCM = false;
    N = length(W);
    for K=6:6%4%min(20,2*m)
    
    % kmeans(X, K) returns the K cluster centroid locations in the K-by-P matrix centroids.
        
        % define initial cluster assignment
        % label_assignments
        ks = 1:K;
        fK = (ks-1).*(N./K);
        tK = ks.*(N./K);
        for idx=1:length(fK)
            [~,tp,~] = find(fK(idx) <= 1:N & 1:N <= tK(idx));
            label_assignments(tp) = idx;
        end
        
        % repeat until convergence, i.e. error is small
        for t=1:5,  
            [ centroids ] = find_cluster_centers( label_assignments, U_small );
            centroids;
            
            if USE_MCM
                [label_assignments, energy] = mcm_solver( U_small, S_small, label_assignments, centroids, K, spnn_indices, W);
            else
                [label_assignments, energy] = min_multi_graph_cut( U_small, S_small, label_assignments, centroids, K, spnn_indices);
            end
            energy
            figure('name', num2str(t))
            visualize_segmentation(frames, imgs, label_assignments, label_mappings, img_index);
            % display_clustering(pixeltensor, label_assignments, row_ids, col_ids, img_index, label_mappings, imgs);
        end

        % compute new best label assignents via graph cut using gcmex
    end
    
    %display_clustering(pixeltensor, label_assignments, row_ids, col_ids, img_index, label_mappings, imgs);
    % store findings
    %write_label_clustering_file(label_assignments, label_mappings, img_index, DATASET);







