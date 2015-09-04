clc;
clear all;
close all;

addpath('../libs/flow-code-matlab');


img1 = imread('../data/ldof/cars1/02.ppm');
img2 = imread('../data/ldof/cars1/03.ppm');

%img1 = imread('../data/teddy/im_001.png');
%img2 = imread('../data/teddy/im_002.png');

img1 =im2double(img1);
img2 =im2double(img2);

foreward_flow = readFlowFile('../data/ldof/cars1/ForwardFlow001.flo');
backward_flow = readFlowFile('../data/ldof/cars1/BackwardFlow001.flo');

%foreward_flow = readFlowFile('../data/teddy/fw001.flo');
%backward_flow = readFlowFile('../data/teddy/bw001.flo');

[ tracked_pixels, trackable_pixels, invalid_regions ] = perform_tracking_step(img1, foreward_flow, backward_flow);



%%
figure
imshow(img1)
hold on
[cidx, cidy, ~] = find(trackable_pixels(:,:,1) == 1);
plot(cidy, cidx, '.b')

%%
figure
imshow(img2)
hold on
[tidx, tidy, ~] = find(tracked_pixels(:,:,1) == 1);
plot(tidy, tidx, '.r')

%%
figure
imshow(img2)
hold on
plot(tidy, tidx, '.r')
plot(cidy, cidx, '.b')

%for k=1:4%length(idx),
%    quiver(cidx(k), cidy(k), tidx(k), tidy(k),  'g')
%end




