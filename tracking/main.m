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
        display_tracking_figures(im_t, im_tp1, trackable_pixels, tracked_pixels);
    end
end

