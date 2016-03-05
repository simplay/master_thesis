function [ frames ] = loadAllTrajectoryLabelFrames( dataset, from_idx, till_idx )
%LOADALLTRAJECTORYLABELFRAMES retrieve all frame labels encoded in a
%struct.
%   @example: access labels of first frame: frames{1}.labels 
    frames = {};
    for frame_idx=from_idx:till_idx
        [labels, ax, ay] = trajectoryLabelsAtFrame(dataset, frame_idx);
        frame = struct('labels',labels, 'ax', ax, 'ay', ay);
        frames{frame_idx} = frame;
    end

end

