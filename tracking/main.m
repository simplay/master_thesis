clc;
clear all;
close all;

addpath('../libs/flow-code-matlab');

dummyImg = imread('../data/ldof/cars1_01.ppm');
[m,n,~] = size(dummyImg);

img1 = imread('../data/ldof/cars1_01.ppm');
img1 =im2double(img1);
img2 = imread('../data/ldof/cars1_02.ppm');
img2 =im2double(img2);



sigma = 1;

[pixel_values, pixel_mask] = find_tracking_candidates(img1, sigma, 0.3);
optical_flow = readFlowFile('../data/ldof/ForwardFlow001.flo');
% [I,J,V] = find(pixel_mask > 0);

u_flow = optical_flow(:,:,1);
v_flow = optical_flow(:,:,2);

Idx = repmat((1:m)', 1, n);
Idy = repmat((1:n), m, 1);

pixel_mask = im2double(pixel_mask);

% perform bilinear interpolation instead of adding and cropping
tp1_x_candidates = (u_flow+Idx).*pixel_mask;
tp1_y_candidates = (v_flow+Idy).*pixel_mask;

tp1_x_candidates = round(tp1_x_candidates);
tp1_y_candidates = round(tp1_y_candidates);

[ix,jx,~] = find(tp1_x_candidates >= 1);
[iy,jy,~] = find(tp1_y_candidates >= 1);

disp('done')
