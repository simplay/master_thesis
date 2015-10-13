function display_tracking_figures( im_t, im_tp1, trackable_pixels, tracked_pixels, t_idx, tp1_idx, mode, prev_tacked_pixels)
%DISPLAY_TRACKING_FIGURES Summary of this function goes here
%   Detailed explanation goes here
        BASE_FILE_PATH = '../data/ldof/cars1/'; % dataset that should be used
        IM_EXT = '.ppm'; % input img file extension
        drawArrow = @(x,y) quiver( x(1),y(1),x(2)-x(1),y(2)-y(1),0 ); 
        img1 = imread(im_t);
        img2 = imread(im_tp1);
        img1 =im2double(img1);
        img2 =im2double(img2);
        img_title = strcat(num2str(t_idx), ' to ', num2str(tp1_idx));
        img_title2 = strcat(num2str(t_idx-1), ' to ', num2str(t_idx));

        if mode == 5 && t_idx > 1
            frame_t = strcat(BASE_FILE_PATH,'0',num2str(t_idx), IM_EXT);
            tm1_img = imread(frame_t);
            tm1_img =im2double(tm1_img); % from image next is img1
            tm1_img = mat2img(tm1_img(:,:,1));
            
            % find all cont. tracks
            %keyboard;
            [ccidx, ccidy, ~] = find(tracked_pixels(:,:,7) == 1) %& tracked_pixels(:,:,2) == 1311);%& tracked_pixels(:,:,2) == 50);
           
            [ccnidx, ccnidy, ~] = find(tracked_pixels(:,:,7) == 0 & tracked_pixels(:,:,1) == 1);
            figure('name', strcat('showing from img: ', img_title2));
            display('green: new started tracking point');
            display('blaue: from point');
            display('red: to point');
            imshow(tm1_img);
            hold on;
            
            % plot all new starting track positions in green.
            %plot(ccnidy,ccnidx, '.g')
            
            hold on
            for k=1:8:length(ccidx)
                % keyboard;
                x0 = tracked_pixels(ccidx(k),ccidy(k),3);
                y0 = tracked_pixels(ccidx(k),ccidy(k),4);
                x1 = tracked_pixels(ccidx(k),ccidy(k),5);
                y1 = tracked_pixels(ccidx(k),ccidy(k),6);
                idx = ccidx(k);
                idy = ccidy(k);
                x = [x1, x0];
                y = [y1, y0];
  
                if tracked_pixels(idx, idy, 1) == 1 ...
                    && prev_tacked_pixels(x0, y0, 1) == 1
                    if tracked_pixels(idx, idy, 2) == prev_tacked_pixels(x0, y0, 2)
                        %keyboard;
                        plot(y0,x0, '.r')
                        plot(y1,x1, '.b')
                        drawArrow(y,x);
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

