function [ tracking_candidates ] = findTrackingCandidates( img, step_size, more_hollow, RUN_INV_MODE)
%FINDTRACKINGCANDIDATES Summary of this function goes here
%   Detailed explanation goes here
    if nargin < 4
        RUN_INV_MODE = false;
    end
    [m,n, ~] = size(img);
    offset = 5;
    [avg_eigenvalue, corners] = computeCorners(img);
    
    if RUN_INV_MODE
        corners = 1-corners;
    end
    
    affinityScale = 0.1;
    shiftThreshold = 0.1;
    if more_hollow
        affinityScale = 0.01;
        shiftThreshold = 0.5;
    end
    
    tracking_candidates = zeros(m,n);
    for ax=offset:step_size:m-offset,
        for ay=offset:step_size:n-offset,
            distToImageBnd = exp(-affinityScale * min([ax, ay, m-ax, n-ay]));
            if corners(ax,ay) >= avg_eigenvalue * (shiftThreshold + distToImageBnd)
                tracking_candidates(ax,ay) = 1;
            end
        end
    end

end

