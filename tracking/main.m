clc;
clear all;
close all;
addpath('../libs/flow-code-matlab');

 %% 
STEP_SIZE = 4; % tracking density
BASE_FILE_PATH = '../data/ldof/cars1/'; % dataset that should be used
IM_EXT = '.ppm'; % input img file extension
DISPLAY = true; % show tracking points

%% working example
% fix naming of files: since image naming indices start counting by 1 and
% flow field by zero, there is a potential confusion.
% for the first image k, we have to assign flow k-1 
% img1 = imread('../data/ldof/cars1/01.ppm');
% img2 = imread('../data/ldof/cars1/02.ppm');
% foreward_flow = readFlowFile('../data/ldof/cars1/ForwardFlow000.flo');
% backward_flow = readFlowFile('../data/ldof/cars1/BackwardFlow000.flo');
for t=1:1,
    im_t = strcat(BASE_FILE_PATH,'0',num2str(t), IM_EXT);
    im_tp1 = strcat(BASE_FILE_PATH,'0',num2str(t+1), IM_EXT);
    fw_flow_t = strcat(BASE_FILE_PATH, 'ForwardFlow','00',num2str(t-1),'.flo');
    bw_flow_t = strcat(BASE_FILE_PATH, 'BackwardFlow','00',num2str(t-1),'.flo');
    [ tracked_pixels, trackable_pixels, invalid_regions ] = process_frame_pair( im_t, fw_flow_t, bw_flow_t, STEP_SIZE );
    
    if DISPLAY
        img1 = imread(im_t);
        img2 = imread(im_tp1);
        img1 =im2double(img1);
        img2 =im2double(img2);
        
        %% trackable refers to points that can be tracked
        figure('name', 'trackable points (blue) on 1st img');
        imshow(img1);
        hold on;
        [cidx, cidy, ~] = find(trackable_pixels(:,:,1) == 1);
        plot(cidy, cidx, '.b')

        %% tracked points refers to points in frame t+1 that a trackable point lands on. 
        figure('name', 'tracked to points (red) on 2nd img');
        imshow(img2);
        hold on;
        [tidx, tidy, ~] = find(tracked_pixels(:,:,1) == 1);
        plot(tidy, tidx, '.r')

        %% 
        figure('name', 'blue: trackable points, red: tracked to points');
        imshow(img2);
        hold on;
        plot(tidy, tidx, '.r')
        plot(cidy, cidx, '.b')
    end
end

