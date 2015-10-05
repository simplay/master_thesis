clc;
clear all;
close all;

PERFORM_RECOMP = false;
RECOMP_EIGS = false;
CLUSTER_CENTER_COUNT = 3;

addpath('../libs/flow-code-matlab');

if PERFORM_RECOMP
    if RECOMP_EIGS
    W = load('../output/similarities/cars1_sim.dat');
    d_a = sum(W,2);
    D = diag(d_a);

    D12 = D^(-0.5);
    B = D12*(D-W)*D12;
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
    U_small = U_small(:,idx(:));
    V_small = V_small(:,idx(:));
    S_small = S_small(:,idx(:));

    s = (diag(S_small));
    s = s /norm(s);
    [idx,jdx,value] = find(s < 0.01);
    U_small = aggregate_mat_cols(U_small, idx);
    V_small = aggregate_mat_cols(V_small, idx);
    S_small = aggregate_mat_cols(S_small, idx);
else
    load('cars1_9k_small_usv.mat');
end
[idx,C,sumd,D] = kmeans(real(U_small),CLUSTER_CENTER_COUNT);

