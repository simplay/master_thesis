function [ tracking_candidates ] = findTrackingCandidates( img, step_size, more_hollow )
%FINDTRACKINGCANDIDATES Summary of this function goes here
%   Detailed explanation goes here
    [m,n, ~] = size(img);
    offset = 5;
    [avg_eigenvalue, corners] = computeCorners(img);

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

