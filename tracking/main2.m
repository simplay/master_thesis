clc;
clear all;
close all;

addpath('../libs/flow-code-matlab');


img1 = imread('../data/ldof/cars1/02.ppm');
img2 = imread('../data/ldof/cars1/03.ppm');
img1 =im2double(img1);
img2 =im2double(img2);

foreward_flow = readFlowFile('../data/ldof/cars1/ForwardFlow001.flo');
backward_flow = readFlowFile('../data/ldof/cars1/BackwardFlow001.flo');

[ tracked_pixels, trackable_pixels ] = perform_tracking_step( img1, foreward_flow, backward_flow );



%%
figure
imshow(img1)
hold on
[idx, idy, ~] = find(trackable_pixels(:,:,1) == 1);
plot(idy, idx, '.r')

%%
figure
imshow(img2)
hold on
[idx, idy, ~] = find(tracked_pixels(:,:,1) == 1);
plot(idy, idx, '.r')


