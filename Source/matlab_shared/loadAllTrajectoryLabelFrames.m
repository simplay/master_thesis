function [ frames ] = loadAllTrajectoryLabelFrames( dataset, from_idx, till_idx, file_prefix)
%LOADALLTRAJECTORYLABELFRAMES retrieve all frame labels encoded in a
%struct.
%   @example: access labels of first frame: frames{1}.labels
%   @example: get x-component that belongts to 
%       the k-th trajectory in frame t: frames{t}.ax(k)
    frames = {};
    basePath = '../output/trajectory_label_frame/';
    del = '';
    if isempty(file_prefix) == 0
        del = '_';
    end
    fname = strcat(file_prefix, del, 'active_tra_f_*.txt');
    filepath = strcat(basePath, dataset, '/', fname);
    disp(['Loading activity frames: ', filepath]);
    for frame_idx=from_idx:till_idx+1
        [labels, ax, ay] = trajectoryLabelsAtFrame(dataset, frame_idx, file_prefix);
        frame = struct('labels',labels, 'ax', ax, 'ay', ay);
        frames{frame_idx} = frame;
    end

end

