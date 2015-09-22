function display_tracking_figures( im_t, im_tp1, trackable_pixels, tracked_pixels, t_idx, tp1_idx, mode, prev_tacked_pixels)
%DISPLAY_TRACKING_FIGURES Summary of this function goes here
%   Detailed explanation goes here
        drawArrow = @(x,y) quiver( x(1),y(1),x(2)-x(1),y(2)-y(1),0 ); 
        img1 = imread(im_t);
        img2 = imread(im_tp1);
        img1 =im2double(img1);
        img2 =im2double(img2);
        [m,n,~] = size(img2);
        img_title = strcat(num2str(t_idx), ' to ', num2str(tp1_idx));
        % keyboard;
        
        % [pcidx, pcidy, ~] = find(prev_tacked_pixels(:,:,1) == 1);
        % [cidx, cidy, ~] = find([cidx, cidy, ~](:,:,1) == 1);
        keyboard;
        if mode == 5 && t_idx > 1
            % find all cont. tracks
            [ccidx, ccidy, ~] = find(tracked_pixels(:,:,5) == 1);
            [ccnidx, ccnidy, ~] = find(tracked_pixels(:,:,5) == 0 & tracked_pixels(:,:,1) == 1);
            figure('name', 'foobar');
            imshow(img1);
            hold on;
            
            % plot all new starting track positions in green.
            plot(ccnidy,ccnidx, '.g')

            hold on
            for k=1:1:length(ccidx)
                % keyboard;
                x0 = tracked_pixels(ccidx(k),ccidy(k),3);
                y0 = tracked_pixels(ccidx(k),ccidy(k),4);
                x1 = ccidx(k);
                y1 = ccidy(k);
                y = [x1, x0 ];
                x = [y1, y0];
  
                if tracked_pixels(x1, y1, 1) == 1 ...
                    && prev_tacked_pixels(x0, y0, 1) == 1
                    if tracked_pixels(x1, y1, 2) == prev_tacked_pixels(x0, y0, 2)
                        plot(y0,x0, '.r')
                        plot(y1,x1, '.b')
                        drawArrow(x,y);
                    end
                    hold on
                end
                
                

            end

            
            return;
        end
        if (mode == 5)
            return;
        end
        
        %% trackable refers to points that can be tracked
        if mode >= 0
            figure('name', strcat('trackable points (blue) on 1st img ', img_title));
            imshow(img1);
            hold on;
            [cidx, cidy, ~] = find(trackable_pixels(:,:,1) == 1);
            plot(cidy, cidx, '.b')
        end

        %% tracked points refers to points in frame t+1 that a trackable point lands on.
        if mode >= 1
            figure('name', strcat('tracked to points (red) on 2nd img', img_title));
            imshow(img2);
            hold on;
            [tidx, tidy, ~] = find(tracked_pixels(:,:,1) == 1);
            plot(tidy, tidx, '.r')
        end

        %% 
        if mode >= 2
            figure('name', strcat('blue: trackable points, red: tracked to points', img_title));
            imshow(img2);
            hold on;
            [cidx, cidy, ~] = find(trackable_pixels(:,:,1) == 1);
            [tidx, tidy, ~] = find(tracked_pixels(:,:,1) == 1);
            plot(tidy, tidx, '.r')
            plot(cidy, cidx, '.b')
        end
end

