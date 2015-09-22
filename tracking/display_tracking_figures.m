function display_tracking_figures( im_t, im_tp1, trackable_pixels, tracked_pixels, t_idx, tp1_idx, mode, prev_tacked_pixels)
%DISPLAY_TRACKING_FIGURES Summary of this function goes here
%   Detailed explanation goes here
        drawArrow = @(x,y) quiver( x(1),y(1),x(2)-x(1),y(2)-y(1),0 ); 
        img1 = imread(im_t);
        img2 = imread(im_tp1);
        img1 =im2double(img1);
        img2 =im2double(img2);
        
        img_title = strcat(num2str(t_idx), ' to ', num2str(tp1_idx));
        keyboard;
        
        % [pcidx, pcidy, ~] = find(prev_tacked_pixels(:,:,1) == 1);
        % [cidx, cidy, ~] = find([cidx, cidy, ~](:,:,1) == 1);
        
        if mode == 5
            % find all cont. tracks
            [ccidx, ccidy, ~] = find(tracked_pixels(:,:,5) == 1);
            
            figure('name', 'foobar');
            imshow(img2);
            hold on;
            for k=1:4:length(ccidx)
                x0 = tracked_pixels(ccidx(k),ccidy(k),3);
                y0 = tracked_pixels(ccidx(k),ccidy(k),4);
                x1 = ccidx(k);
                y1 = ccidy(k);
                x = [x0 x1];
                y = [y0 y1];
                drawArrow(x,y);
                hold on
            end

            
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
            plot(tidy, tidx, '.r')
            plot(cidy, cidx, '.b')
        end
end

