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
            
            
            
            % rather a hack!
            % perform latest approach
            pax = prev_tacked_pixels(idx(k), idy(k), BX);
            pay = prev_tacked_pixels(idx(k), idy(k), BY);
            
            bx = pax + fw_u_flow(ax,ay);
            by = pay + fw_v_flow(ax,ay);
            
%                                     % previous (bx, by) is current (ax,ay)
%             pax = prev_tacked_pixels(idx(k), idy(k), BX);
%             pay = prev_tacked_pixels(idx(k), idy(k), BY);
%             
%                     % real tracked to positions (floats)
%                 bx = pax + fw_u_flow(ax,ay);
%                 by = pay + fw_v_flow(ax,ay);
%             
%             
%             
% 
%             
%             
%             
%             
%             % get prev. and next index from subpixel accurate index value.
%             x1 = floor(bx);
%             y1 = floor(by);
%             x2 = x1+1;
%             y2 = y1+1;
%             
%             
%             alpha_x = bx-x1;
%             alpha_y = by-y1;
%             
%             
%             if (x1 <= 0 || x2 <= 0 || x1 > m || x2 > n)
%                 continue;
%             end
%             
%             if (y1 <= 0 || y2 <= 0 || y1 > m || y2 > n)
%                 continue;
%             end
%             
%             % bilinar interpolation of flow making use of the backward flow
%             a = (1.0-alpha_x)*bw_u_flow(x1,y1) + alpha_x*bw_u_flow(x2,y1);
%             b = (1.0-alpha_x)*bw_u_flow(x1,y2) + alpha_x*bw_u_flow(x2,y2);
%             u = (1.0-alpha_y)*a + alpha_y*b;
%             
%             a = (1.0-alpha_x)*bw_v_flow(x1,y1) + alpha_x*bw_v_flow(x2,y1);
%             b = (1.0-alpha_x)*bw_v_flow(x1,y2) + alpha_x*bw_v_flow(x2,y2);
%             v = (1.0-alpha_y)*a + alpha_y*b;
%             
%             % interpolated flow positions
%             bx = bx + u;
%             by = by + v;
%             

            
            
            
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

