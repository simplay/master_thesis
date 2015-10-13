function [ tracked_pixels, trackable_pixels, invalid_regions, start_mask, prev_foreward_flow, prev_backward_flow] = process_frame_pair( frame_t, fw_flow_t, bw_flow_t, step_size, start_mask, tracked_to_positions, prev_tacked_pixels, prev_foreward_flow, prev_backward_flow)
%PROCESS_FRAME_PAIR Summary of this function goes here
%   Detailed explanation goes here

img1 = imread(frame_t);
img1 =im2double(img1);

% fix naming of files: since image naming indices start counting by 1 and
% flow field by zero, there is a potential confusion.
% for the first image k, we have to assign flow k-1 
foreward_flow = readFlowFile(fw_flow_t);
backward_flow = readFlowFile(bw_flow_t);

[ tracked_pixels, trackable_pixels, invalid_regions, start_mask ] = perform_tracking_step(img1, foreward_flow, backward_flow, step_size, start_mask, tracked_to_positions, prev_tacked_pixels,prev_foreward_flow, prev_backward_flow);
prev_foreward_flow = foreward_flow;
prev_backward_flow = backward_flow;
end

