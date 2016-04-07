function [labels, ax, ay] = trajectoryLabelsAtFrame(dataset, frame_idx, file_prefix)
%TRAJECTORYLABELSATFRAME read all trajectory labels active in a given frame
%   returns the label value and the tracked point of an active trajectory
%   in a given frame.
    
    basePath = '../output/trajectory_label_frame/';
    prefix = file_prefix;
    if isempty(file_prefix) == 0
        prefix = strcat(file_prefix, '_');
    end
    fname = strcat(prefix, 'active_tra_f_',num2str(frame_idx),'.txt');
    filepath = strcat(basePath, dataset, '/', fname);
    
    [labels, ax, ay] = textread(filepath, '%d %f %f');
end

