function [ pixel_trackings ] = track_points( trackable_pixels, fw_u_flow, fw_v_flow, is_continuing_tracking, prev_tacked_pixels)
%TRACK_POINTS Summary of this function goes here
%   Detailed explanation goes here
%   @param is_continuing_tracking [Boolean] true if track is continued
%   otherwise false (i.e. using new tracking candidates)

    [m,n] = size(trackable_pixels);
        % The value at pixel location (i,j) indicates the corresponding tracking
    % label.
    % Default tracking label for every pixel is the value 0.
    % Therefore every valid label is supposed to be grater than 0
    % TODO: add another dimension for time steps
    % 3rd dimension denotes has track finished
    % track is supposed to be finished IFF label is > 0 AND
    % tracked_pixels(i,j,2) is 1
    pixel_trackings = zeros(m,n,4);
    
    
    
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
            if is_continuing_tracking
                prev_label_value = prev_tacked_pixels(ax, ay, 2);
                
                % lookup coords sind falsch
                pixel_trackings(ax, ay, 2) = prev_label_value; 
                inc_global_label_idx
            else
                label_value = get_global_label_idx;
                pixel_trackings(ax, ay, 2) = label_value;   
            end

            continue;
        else
            if is_continuing_tracking
                % use prev label that was assigned
                prev_idx = prev_tacked_pixels(ax, ay, 3);
                prev_idy = prev_tacked_pixels(ax, ay, 4);
                if prev_idx > 0 && prev_idy > 0
                    % lookup coords sind nicht best?tigt
                    prev_label_value = prev_tacked_pixels(prev_idx, prev_idy, 2);
                    pixel_trackings(ax, ay, 2) = prev_label_value;
                end
 
            else
                inc_global_label_idx
                label_value = get_global_label_idx;
                pixel_trackings(ax, ay, 2) = label_value;
                to_idx = ibx;
                to_idy = iby;
                pixel_trackings(ax, ay, 3) = to_idx;
                pixel_trackings(ax, ay, 4) = to_idy;
            end
            pixel_trackings(ibx, iby, 1) = 1;
        end 
    end


end

