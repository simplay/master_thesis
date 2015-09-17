clc;
clear all;
close all;

addpath('../libs/flow-code-matlab');

step_size = 4;

img1 = imread('../data/ldof/cars1/01.ppm');
img2 = imread('../data/ldof/cars1/02.ppm');
img1 =im2double(img1);
img2 =im2double(img2);

% fix naming of files: since image naming indices start counting by 1 and
% flow field by zero, there is a potential confusion.
% for the first image k, we have to assign flow k-1 
foreward_flow = readFlowFile('../data/ldof/cars1/ForwardFlow000.flo');
backward_flow = readFlowFile('../data/ldof/cars1/BackwardFlow000.flo');

% foreward_flow = readFlowFile('../data/teddy/fw001.flo');
% backward_flow = readFlowFile('../data/teddy/bw001.flo');

[ tracked_pixels, trackable_pixels, invalid_regions ] = perform_tracking_step(img1, foreward_flow, backward_flow, step_size);


%%
figure('name', 'tracking points (blue) on 1st img');
imshow(img1);
hold on;
[cidx, cidy, ~] = find(trackable_pixels(:,:,1) == 1);
plot(cidy, cidx, '.b')

%%
figure('name', 'tracked to points (red) on 2nd img');
imshow(img2);
hold on;
[tidx, tidy, ~] = find(tracked_pixels(:,:,1) == 1);
plot(tidy, tidx, '.r')

%%
figure('name', 'blue: tracking points, red: tracked to points');
imshow(img2);
hold on;
plot(tidy, tidx, '.r')
plot(cidy, cidx, '.b')
