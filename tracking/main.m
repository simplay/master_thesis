clc;
clear all;
close all;
addpath('../libs/flow-code-matlab');

 %% 
 
% global variable used for assigning unique label indices
set_global_label_idx(1);

STEP_SIZE = 8; % tracking density
BASE_FILE_PATH = '../data/ldof/cars1/'; % dataset that should be used
IM_EXT = '.ppm'; % input img file extension
DISPLAY = true; % show tracking points
MODE = 5; % display mode
START_END_IDX = 1; % inital index 1
FRAME_END_IDX = 4; % for car example max 4

%% working example
% fix naming of files: since image naming indices start counting by 1 and
% flow field by zero, there is a potential confusion.
% for the first image k, we have to assign flow k-1 
% img1 = imread('../data/ldof/cars1/01.ppm');
% img2 = imread('../data/ldof/cars1/02.ppm');
% foreward_flow = readFlowFile('../data/ldof/cars1/ForwardFlow000.flo');
% backward_flow = readFlowFile('../data/ldof/cars1/BackwardFlow000.flo');

[m,n,~] = size(imread(strcat(BASE_FILE_PATH,'01', IM_EXT)));
start_mask = ones(m,n);
prev_tacked_pixels = zeros(m,n,5);
% initially, there are no tracked to positions
tracked_to_positions = zeros(m,n);
for t=START_END_IDX:FRAME_END_IDX,
    frame_t = strcat(BASE_FILE_PATH,'0',num2str(t), IM_EXT);
    im_tp1 = strcat(BASE_FILE_PATH,'0',num2str(t+1), IM_EXT);
    fw_flow_t = strcat(BASE_FILE_PATH, 'ForwardFlow','00',num2str(t-1),'.flo');
    bw_flow_t = strcat(BASE_FILE_PATH, 'BackwardFlow','00',num2str(t-1),'.flo');
    [ tracked_pixels, trackable_pixels, invalid_regions, old_start_mask ] = ...
        process_frame_pair( frame_t, fw_flow_t, bw_flow_t, STEP_SIZE, start_mask, tracked_to_positions, prev_tacked_pixels);

    t_idx = t; tp1_idx = t+1;
    if DISPLAY
        display_tracking_figures(frame_t, im_tp1, trackable_pixels, tracked_pixels, t_idx, tp1_idx, MODE, prev_tacked_pixels);
    end
    
    % overwrite data after having plotted them
    start_mask = old_start_mask;
    tracked_to_positions = tracked_pixels(:,:,1);
    prev_tacked_pixels = tracked_pixels;
end

