clc;
%clear all;
close all;

PERFORM_RECOMP = false;
RECOMP_EIGS = false;
CLUSTER_CENTER_COUNT = 2;

addpath('../libs/flow-code-matlab');

if PERFORM_RECOMP
    if RECOMP_EIGS
    W = load('../output/similarities/cars1_sim.dat');
    d_a = sum(W,2);
    D = diag(d_a);

    D12 = D^(-0.5);
    B = D12*(D-W)*D12;
    keyboard;
    [U,S,V] = eig(B);
    else
        load('cars1_9k_usv.mat');
    end

    U_small = real(U);
    V_small = real(V);
    S_small = real(S);
    s = (diag(S_small));
    s = s /norm(s);

    [idx,jdx,value] = find(s > 0.0);
    U_small = aggregate_mat_cols(U_small, idx);
    V_small = aggregate_mat_cols(V_small, idx);
    S_small = aggregate_mat_cols(S_small, idx);

    s = (diag(S_small));
    s = s /norm(s);
    [idx,jdx,value] = find(s < 0.005);
    U_small = aggregate_mat_cols(U_small, idx);
    V_small = aggregate_mat_cols(V_small, idx);
    S_small = aggregate_mat_cols(S_small, idx);
    
else
    load('cars1_9k_small_usv.mat');
end
%%
[labels,C,sumd,D] = spectral_custering( U_small, CLUSTER_CENTER_COUNT );
pixeltensor = load('../output/trackingdata/cars1_step_8_frame_1.mat');
pixeltensor = pixeltensor.tracked_pixels;

[idi,idj,idk] = find(pixeltensor(:,:,1) == 1);

% evcolors = eigenvector_to_color( U_small, 1 );
% evc_to_color( f )
col_sel = 4;
USE_W_VEC = false;
USE_CLUSTERING_CUE = true;
if ~exist('W','var') && USE_W_VEC
    W = load('../output/similarities/cars1_sim.dat');
end
ev = extract_vector( U_small, W, col_sel, USE_W_VEC );
%ev = ev / norm(ev);


display_segmentation( pixeltensor, ev, idi, idj, labels, idk, col_sel, USE_CLUSTERING_CUE);
