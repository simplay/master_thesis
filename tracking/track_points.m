function [ pixel_trackings ] = track_points( trackable_pixels, fw_u_flow, fw_v_flow, is_continuing_tracking, prev_tacked_pixels, bw_u_flow, bw_v_flow, prev_foreward_flow, prev_backward_flow)
% Given trackable points, find their tracked to positions (the so called
% pixel trackings).

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
    
    TRACKED = 1;
    LABEL = 2;
    AX = 3;
    AY = 4;
    BX = 5;
    BY = 6;
    CONT = 7;
    % LEGEND pixel_trackings:
    % a point in pixel_trackings at location (x,y) has 
    % the following features assigned:
    %
    %   k=1: tracked [Integer]
    %       is either 1 (tracked) or 0 (untracked)
    %       can be 0 in case the tracked to position is not in the viewport
    %       anymore (i.e. out of frame).
    %   
    %   k=2: label [Integer]
    %       identifier of the track this point belongs to
    %       a tracked point has different image loactions, but corresponds
    %       to the same label. the set of all location coordinates 
    %       - over all frames - of a point, gives us a trajectory.
    %    
    %   k=3: ax Integer
    %       x coord of frame t
    %       x location of tracking candidate point we want to determine its 
    %       tracked to position.
    %    
    %   k=4: ay Integer
    %       y coord of frame t
    %       y location of tracking candidate point we want to determine its 
    %       tracked to position.
    %    
    %   k=5: bx Float
    %       x coord of frame t+1
    %    
    %   k=6: by Float
    %       y coord of frame t+1
    %    
    %   k=7: cont Integer
    %       is tracked continued
    pixel_trackings = zeros(m,n,7);
    
    
    
    % only iterate over trackable pixels
    [idx, idy, ~] = find(trackable_pixels == 1);
    for k = 1:length(idx),
        
        % tracked from positions
        ax = idx(k);
        ay = idy(k);
        
        % real tracked to positions (floats)
        bx = ax + fw_u_flow(ax,ay);
        by = ay + fw_v_flow(ax,ay);
        
        % when cont. trackings we know their exact location
        if is_continuing_tracking
            
            % new clean idea
            prev_fw_u_flow = prev_foreward_flow(:,:,2);
            prev_fw_v_flow = prev_foreward_flow(:,:,1);
            prev_bw_u_flow = prev_backward_flow(:,:,2);
            prev_bw_v_flow = prev_backward_flow(:,:,1);
            
            prev_ax = ax + prev_bw_u_flow(ax,ay);
            prev_ay = ay + prev_bw_v_flow(ax,ay);
            
            i_prev_ax = floor(prev_ax);
            i_prev_ay = floor(prev_ay);
            
            i2_prev_ax = i_prev_ax+1;
            i2_prev_ay = i_prev_ay+1;
            
            alpha_x = 1-(prev_ax-i_prev_ax);
            alpha_y = 1-(prev_ay-i_prev_ay);
            
            if (i_prev_ax <= 0 || i_prev_ay <= 0 || i_prev_ax > m || i_prev_ay > n)
                continue;
            end
            
            if (i2_prev_ax <= 0 || i2_prev_ay <= 0 || i2_prev_ax > m || i2_prev_ay > n)
                continue;
            end
            
            pew_x = alpha_x*prev_fw_u_flow(i_prev_ax,i_prev_ay) + (1-alpha_x)*prev_fw_u_flow(i2_prev_ax,i2_prev_ay);
            pew_y = alpha_y*prev_fw_v_flow(i_prev_ax,i_prev_ay) + (1-alpha_y)*prev_fw_v_flow(i2_prev_ax,i2_prev_ay);
            
            pp_x = prev_ax+pew_x; % t tilde p_k u
            pp_y = prev_ay+pew_y; % t tilde p_k v
            
            i_ax = floor(pp_x);
            i_ay = floor(pp_y);
            
            i2_ax = i_ax+1;
            i2_ay = i_ay+1;
            
            alpha_x = 1-(pp_x-i_ax);
            alpha_y = 1-(pp_y-i_ay);
            
            
            if (i_ax <= 0 || i_ay <= 0 || i_ax > m || i_ay > n)
                continue;
            end
            
            if (i2_ax <= 0 || i2_ay <= 0 || i2_ax > m || i2_ay > n)
                continue;
            end
            
            pew_x = alpha_x*fw_u_flow(i_ax,i_ay) + (1-alpha_x)*fw_u_flow(i2_ax,i2_ay);
            pew_y = alpha_y*fw_v_flow(i_ax,i_ay) + (1-alpha_y)*fw_v_flow(i2_ax,i2_ay);
            
            bx = pp_x + pew_x;
            by = pp_y + pew_y;
            
            
            
            
        end
        


        % tracked to positions (rounded)
        ibx = round(bx);
        iby = round(by);
        

        % keyboard;
        
        % case: tracked to out of image viewport
        if (ibx <= 0 || iby <= 0 || ibx > m || iby > n)
            continue;
         
        % case: in viewport    
        else
            % case: working with tracking candidates
            
            
            % remember tracked from position
            pixel_trackings(ibx, iby, AX) = ax;
            pixel_trackings(ibx, iby, AY) = ay;
            
            % remember unrounded tracked to position
            pixel_trackings(ibx, iby, BX) = bx;
            pixel_trackings(ibx, iby, BY) = by;
            
            if is_continuing_tracking

                % use prev label that was assigned
                prev_label_value = prev_tacked_pixels(ax, ay, LABEL);
                pixel_trackings(ibx, iby, LABEL) = prev_label_value;
                
                
%                 if prev_label_value == 2403
%                     keyboard;
%                 end
                
                % mark track as continuing
                pixel_trackings(ibx, iby, CONT) = 1;
            
            % case: working with tracking candidates
            else
                inc_global_label_idx;
                label_value = get_global_label_idx;
                pixel_trackings(ibx, iby, LABEL) = label_value;
            end

            pixel_trackings(ibx, iby, TRACKED) = 1;
        end 
    end


end

