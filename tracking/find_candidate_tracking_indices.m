function [ tp1_x_candidates, tp1_y_candidates ] = find_candidate_tracking_indices( u_flow, v_flow, mask )
%FIND_CANDIDATE_TRACKING_INDICES Summary of this function goes here
%   Detailed explanation goes here
% perform bilinear interpolation instead of adding and cropping

    [m,n] = size(mask);

    Idx = repmat((1:m)', 1, n);
    Idy = repmat((1:n), m, 1);

    tp1_x_candidates = (u_flow+Idx).*mask;
    tp1_y_candidates = (v_flow+Idy).*mask;

    % perform bilinear interpolation instead of adding and cropping
    tp1_x_candidates = round(tp1_x_candidates);
    tp1_y_candidates = round(tp1_y_candidates);

end

