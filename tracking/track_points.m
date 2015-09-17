function [ pixel_trackings ] = track_points( trackable_pixels, fw_u_flow, fw_v_flow)
%TRACK_POINTS Summary of this function goes here
%   Detailed explanation goes here
    [m,n] = size(trackable_pixels);
        % The value at pixel location (i,j) indicates the corresponding tracking
    % label.
    % Default tracking label for every pixel is the value 0.
    % Therefore every valid label is supposed to be grater than 0
    % TODO: add another dimension for time steps
    % 3rd dimension denotes has track finished
    % track is supposed to be finished IFF label is > 0 AND
    % tracked_pixels(i,j,2) is 1
    pixel_trackings = zeros(m,n,2);
    
    
    
    % only iterate over trackable pixels
    [idx, idy, ~] = find(trackable_pixels == 1);
    for k = 1:length(idx),
        ax = idx(k);
        ay = idy(k);
        
        bx = ax + fw_u_flow(ax,ay);
        by = ay + fw_v_flow(ax,ay);

        ibx = round(bx);
        iby = round(by);

        if (ibx <= 0 || iby <= 0 || ibx > m || iby > n)
            % remeber that track has finished here
            pixel_trackings(ax, ay, 2) = 1;
            continue;
        else
            pixel_trackings(ibx, iby, 1) = 1;
        end 
    end


end

