clc;
%clear all;
close all;

PERFORM_RECOMP = false;
RECOMP_EIGS = false;
PRELOAD_EIGS = false;
CLUSTER_CENTER_COUNT = 3;
THRESH = 0.0001;
RUN_EIGS = false;
addpath('../libs/flow-code-matlab');
if RUN_EIGS
    W = load('../output/similarities/cars1_sim.dat');
    WW = W + ones(size(W))*THRESH;
    d_a = sum(WW,2);
    D = diag(d_a);
    D12 = diag(sqrt(1./d_a));
    B = D12*(D-WW)*D12;
    [U_small,S_small,FLAG] = eigs(B,50, 'SM');
    d = diag(S_small);
    %d = d-min(d);
    %d = d ./max(d);
    [aa,bb,cc] = find(d > 0 & d < 0.2);
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
%%

% evcolors = eigenvector_to_color( U_small, 1 );
% evc_to_color( f )

USE_W_VEC = true;
USE_CLUSTERING_CUE = false;
for t=1:1
    figure

% nice indices for w
%   300 - shows car
col_sel = 300;

if ~exist('W','var') && USE_W_VEC
    W = load('../output/similarities/cars1_sim.dat');
end
displayed_vector = extract_vector( U_small, W, col_sel, USE_W_VEC );
%ev = ev / norm(ev);

for tt = 1:1
    
    pixeltensor = load(strcat('../output/trackingdata/cars1_step_8_frame_',num2str(tt),'.mat'));
    pixeltensor = pixeltensor.tracked_pixels;
    [row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);

        if USE_CLUSTERING_CUE    
            [label_assignments] = spectral_custering( U_small, CLUSTER_CENTER_COUNT);
            display_clustering(pixeltensor, label_assignments, row_ids, col_ids, tt);
        else    
           display_affinity_vec(pixeltensor, displayed_vector, row_ids, col_ids, tt, col_sel); 
        end
end
end