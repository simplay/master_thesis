function display_tracking_figures( im_t, im_tp1, trackable_pixels, tracked_pixels )
%DISPLAY_TRACKING_FIGURES Summary of this function goes here
%   Detailed explanation goes here
        img1 = imread(im_t);
        img2 = imread(im_tp1);
        img1 =im2double(img1);
        img2 =im2double(img2);
        
        %% trackable refers to points that can be tracked
        figure('name', 'trackable points (blue) on 1st img');
        imshow(img1);
        hold on;
        [cidx, cidy, ~] = find(trackable_pixels(:,:,1) == 1);
        plot(cidy, cidx, '.b')

        %% tracked points refers to points in frame t+1 that a trackable point lands on. 
        figure('name', 'tracked to points (red) on 2nd img');
        imshow(img2);
        hold on;
        [tidx, tidy, ~] = find(tracked_pixels(:,:,1) == 1);
        plot(tidy, tidx, '.r')

        %% 
        figure('name', 'blue: trackable points, red: tracked to points');
        imshow(img2);
        hold on;
        plot(tidy, tidx, '.r')
        plot(cidy, cidx, '.b')

end

