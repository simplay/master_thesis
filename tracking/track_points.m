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
    pixel_trackings = zeros(m,n,5);
    
    
    
    % only iterate over trackable pixels
    [idx, idy, ~] = find(trackable_pixels == 1);
    for k = 1:length(idx),
        
        % tracked from positions
        ax = idx(k);
        ay = idy(k);
        
        % real tracked to positions (floats)
        bx = ax + fw_u_flow(ax,ay);
        by = ay + fw_v_flow(ax,ay);

        % tracked to positions (rounded)
        ibx = round(bx);
        iby = round(by);
        

        % keyboard;
        
        % case: tracked to out of image viewport
        if (ibx <= 0 || iby <= 0 || ibx > m || iby > n)
            
            % remeber that track has finished here
            % prev_tacked_pixels(ax, ay, 5) = 0;
            continue;
            
            

            
            
        % case: in viewport    
        else
            % case: working with tracking candidates
            
            % remember prev. position
            pixel_trackings(ibx, iby, 3) = ax;
            pixel_trackings(ibx, iby, 4) = ay;
            
            if is_continuing_tracking

                % use prev label that was assigned
                prev_label_value = prev_tacked_pixels(ax, ay, 2);
                pixel_trackings(ibx, iby, 2) = prev_label_value;
                
                % mark track as continuing
                pixel_trackings(ibx, iby, 5) = 1;
            
            % case: working with tracking candidates
            else
                inc_global_label_idx;
                label_value = get_global_label_idx;
                pixel_trackings(ibx, iby, 2) = label_value;
            end

            pixel_trackings(ibx, iby, 1) = 1;
        end 
    end


end

