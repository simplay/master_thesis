function [labels, ax, ay] = trajectoryLabelsAtFrame(dataset, frame_idx)
%TRAJECTORYLABELSATFRAME read all trajectory labels active in a given frame
%   returns the label value and the tracked point of an active trajectory
%   in a given frame.
    
    basePath = '../output/trajectory_label_frame/';
    fname = strcat('active_tra_f_',num2str(frame_idx),'.txt');
    filepath = strcat(basePath, dataset, '/', fname);
    
    [labels, ax, ay] = textscan(filepath, '%d %f %f');
end

