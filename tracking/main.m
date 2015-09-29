clc;
clear all;
close all;
addpath('../libs/flow-code-matlab');

 %% 
 
% global variable used for assigning unique label indices
set_global_label_idx(1);

STEP_SIZE = 4; % tracking density
DATASET = 'cars1/';
BASE_FILE_PATH = strcat('../data/ldof/',DATASET); % dataset that should be used
IM_EXT = '.ppm'; % input img file extension
DISPLAY = true; % show tracking points
MODE = 0; % display mode
START_FRAME_IDX = 1; % inital index 1
END_FRAME_IDX = 4; % for car example max 4
WRITE_TRACKINGS_INTO_FILES = true;

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
prev_tacked_pixels = zeros(m,n,7);
% initially, there are no tracked to positions
tracked_to_positions = zeros(m,n);
for t=START_FRAME_IDX:END_FRAME_IDX,
    frame_t = strcat(BASE_FILE_PATH,'0',num2str(t), IM_EXT);
    im_tp1 = strcat(BASE_FILE_PATH,'0',num2str(t+1), IM_EXT);
    fw_flow_t = strcat(BASE_FILE_PATH, 'ForwardFlow','00',num2str(t-1),'.flo');
    bw_flow_t = strcat(BASE_FILE_PATH, 'BackwardFlow','00',num2str(t-1),'.flo');
    [ tracked_pixels, trackable_pixels, invalid_regions, old_start_mask ] = ...
        process_frame_pair( frame_t, fw_flow_t, bw_flow_t, STEP_SIZE, start_mask, tracked_to_positions, prev_tacked_pixels);

    
    % write data into file
    if WRITE_TRACKINGS_INTO_FILES
        write_flow_data(tracked_pixels,t,DATASET);
    end
    
    t_idx = t; tp1_idx = t+1;
    if DISPLAY
        display_tracking_figures(frame_t, im_tp1, trackable_pixels, tracked_pixels, t_idx, tp1_idx, MODE, prev_tacked_pixels);
    end
    
    % overwrite data after having plotted them
    start_mask = old_start_mask;
    tracked_to_positions = tracked_pixels(:,:,1);
    prev_tacked_pixels = tracked_pixels;
end

