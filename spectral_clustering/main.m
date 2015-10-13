clc;
%clear all;
close all;

PERFORM_RECOMP = false;
RECOMP_EIGS = false;
PRELOAD_EIGS = false;
CLUSTER_CENTER_COUNT = 4;
THRESH = 0.002;
RUN_EIGS = true;
addpath('../libs/flow-code-matlab');

%% load appropriate data
if RUN_EIGS
    W = load('../output/similarities/cars1_sim.dat');
    WW = W + ones(size(W))*THRESH;
    d_a = sum(WW,2);
    D = diag(d_a);
    D12 = diag(sqrt(1./d_a));
    B = D12*(D-WW)*D12;
    [U_small,S_small,FLAG] = eigs(B,50,1e-6);
    d = diag(S_small);
    [aa,~,~] = find(d < 0.2);
    U_small = aggregate_mat_cols(U_small, aa);
    S_small = d(aa);
elseif RUN_EIGS == false && PERFORM_RECOMP      
    if PERFORM_RECOMP
        if RECOMP_EIGS

        W = load('../output/similarities/cars1_sim.dat');
        W = W + ones(size(W))*THRESH;
        % W = W + ones(size(W))*THRESH
        % schaue alle 20 eigenvekoren an und plotte, identify komische
        % vektoren und porblempunkte wie 2033, schaue trajektorien an.
        d_a = sum(W,2);
        D = diag(d_a);

        %D12 = D^(-0.5);
        D12 = diag(sqrt(1./d_a))
        B = D12*(D-W)*D12;
        %keyboard;
        [U,S,V] = eig(B);

        else
            load('cars1_9k_usv.mat');
        end

    % TODO 1: change to.
    %     eigs(A,K,SIGMA) and eigs(A,B,K,SIGMA) return K eigenvalues. If SIGMA is:
    %   'LM' or 'SM' - Largest or Smallest Magnitude

    U_small = real(U);
    V_small = real(V);
    S_small = real(S);
    s = (diag(S_small));
    %s = s /norm(s);

    [idx,jdx,value] = find(s > 0.0);% Are there < 0 evs ? 
    U_small = aggregate_mat_cols(U_small, idx);
    V_small = aggregate_mat_cols(V_small, idx);
    S_small = S_small(idx,idx);%aggregate_mat_cols(S_small, idx);

    s = (diag(S_small));
    s = s /norm(s);
    [idx,jdx,value] = find(s < 0.005);
    U_small = aggregate_mat_cols(U_small, idx);
    V_small = aggregate_mat_cols(V_small, idx);
    S_small = S_small(idx,idx);%aggregate_mat_cols(S_small, idx);

    else
        load('cars1_9k_small_usv.mat');
    end
else
    if PRELOAD_EIGS
        load('cars1_9k_eigs_uv_small.mat');
    end
end

%% display segmentation and its data.

label_mappings = labelfile2mat;


% the following flag define what data should be displayed.
% USE_CLUSTERING_CUE true => display segmentation
% USE_CLUSTERING_CUE false && USE_W_VEC true => display affinities
% USE_CLUSTERING_CUE false && USE_W_VEC => display eigenvectors
USE_W_VEC = false;
USE_CLUSTERING_CUE = true;

figure

% NOTICE: col_sel stands for the target column of the dataset that should
%   be plotted. Since eigenvectors, affinitires and label_assignments
%   have different vector dimensionalities, this affects the possible 
%   indexing (querry) range. Therefore, when
%       plotting eigenvectors, then do not pass a bigger index than
%           size(U_small,2)
%       plotting affinities, then do not pass a bigger index than length(W)
%   when plotting cluster assignments, the chosen index value has no
%   effect.
%
% HINT: nice indices when working with affinities:
%   300 - car in background
%   2033 - right wheel front car (issue case: no neighboring assignments)
%       cmp with 2000
%   975 - front car car front (issue case: no neighboring assignments)

col_sel = 2363;
label_as_local(label_mappings, 2363)
% load W matrix in case it is needed.
if ~exist('W','var') && USE_W_VEC
    W = load('../output/similarities/cars1_sim.dat');
end

% display data
for img_index = 1:1
    figure
    pixeltensor = load(strcat('../output/trackingdata/cars1_step_8_frame_',num2str(img_index),'.mat'));
    pixeltensor = pixeltensor.tracked_pixels;
    [row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);

    if USE_CLUSTERING_CUE    
        [label_assignments] = spectral_custering( U_small, CLUSTER_CENTER_COUNT);
        display_clustering(pixeltensor, label_assignments, row_ids, col_ids, img_index, label_mappings);
        write_label_clustering_file(label_assignments, label_mappings, img_index);
    else
        displayed_vector = extract_vector( U_small, W, col_sel, USE_W_VEC, label_mappings);
       %displayed_vector = W(:,2808);
        if USE_W_VEC
            display_affinity_vec(pixeltensor, displayed_vector, row_ids, col_ids, img_index, col_sel, label_mappings);
        else
            eigenvalue = S_small(col_sel);
            display_eigenvectors(pixeltensor, displayed_vector, row_ids, col_ids, img_index, eigenvalue, label_mappings);
        end
    end
end
