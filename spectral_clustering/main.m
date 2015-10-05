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
pixeltensor = load('../output/trackingdata/cars1_step_8_frame_1.mat');
pixeltensor = pixeltensor.tracked_pixels;

[idi,idj,idk] = find(pixeltensor(:,:,1) == 1);

% evcolors = eigenvector_to_color( U_small, 1 );
% evc_to_color( f )
idx = 10;
USE_W_VEC = true;

if USE_W_VEC
    ev = W(:,idx);
else
    ev = U_small(:,idx);
end
%ev = ev / norm(ev);
ev = ev-min(ev(:));
ev = ev ./ max(ev(:));
USE_CLUSTERING_CUE = false;
imshow('../data/ldof/cars1/01.ppm');
hold on
for k=1:length(idk),
    label = pixeltensor(idi(k), idj(k), 2);
    ax = pixeltensor(idi(k), idj(k), 3);
    ay = pixeltensor(idi(k), idj(k), 4);
    
    if USE_CLUSTERING_CUE
        labelcolor = idx(label);
        if labelcolor == 1
            color_value = [1,0,0];
        elseif labelcolor == 2
            color_value = [0,1,0];
        elseif labelcolor == 3
            color_value = [0,0,1];
        else
            color_value = [1,1,1];
        end
    else    
        color_value = evc_to_color(ev(label));
    end
    
    %plot(ay, ax, 'Color', color_value);
    plot(ay,ax,'Color',color_value,'Marker','.');
    hold on
   
end
