function display_tracking_figures( im_t, im_tp1, trackable_pixels, tracked_pixels, t_idx, tp1_idx, mode, prev_tacked_pixels)
%DISPLAY_TRACKING_FIGURES Summary of this function goes here
%   Detailed explanation goes here
        img1 = imread(im_t);
        img2 = imread(im_tp1);
        img1 =im2double(img1);
        img2 =im2double(img2);
        
        img_title = strcat(num2str(t_idx), ' to ', num2str(tp1_idx));
        % keyboard;
        
        % [pcidx, pcidy, ~] = find(prev_tacked_pixels(:,:,1) == 1);
        % [cidx, cidy, ~] = find([cidx, cidy, ~](:,:,1) == 1);
        
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

