function [ tracking_candidates ] = findTrackingCandidates( img, step_size )
%FINDTRACKINGCANDIDATES Summary of this function goes here
%   Detailed explanation goes here
    [m,n, ~] = size(img);
    offset = 5;
    [ avg_eigenvalue, corners ] = computeCorners( img );
    scale = 0.1;
    tracking_candidates = zeros(m,n);
    for ax=offset:step_size:m-offset,
        for ay=offset:step_size:n-offset,
            distToImageBnd = exp(-scale*min([ax,ay,m-ax, n-ay]));
            if corners(ax,ay) < avg_eigenvalue*(0.1+distToImageBnd)
                continue;
            end

            %if corners(ax,ay) < 1.0+distToImageBnd 
            %    continue;
            %end
            tracking_candidates(ax,ay) = 1;
        end
    end

end

