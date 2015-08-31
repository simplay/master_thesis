clc;
clear all;
close all;

addpath('../libs/flow-code-matlab');

img1 = imread('../data/ldof/cars1_01.ppm');
img1 =im2double(img1);
img2 = imread('../data/ldof/cars1_02.ppm');
img2 =im2double(img2);

eps = 0.5; % threshold value
sigma = 1;

pixel_mask = find_tracking_candidates(img1, sigma, eps);
f_flow = readFlowFile('../data/ldof/ForwardFlow004.flo');


disp('done')
