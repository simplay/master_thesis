clc

addpath('../libs/flow-code-matlab');
addpath('../matlab_shared');

DATASET = 'cars';
METHODNAME = 'ldof';
PREFIX_INPUT_FILENAME = 'pd_both_10';
STEPSIZE_DATA = 8;
SHOULD_PLOT3D = true;

img_index = 1;
START_FRAME = 1;
END_FRAME = 4;
num_el = 3;
selection_count = 3;

% constants
MARKER_THICKNESS = 15;

% load relevant trajectory associated data
[imgs, fwf, bwf, boundaries, label_mappings, frames] = load_trajectory_data(DATASET, METHODNAME, PREFIX_INPUT_FILENAME, START_FRAME, END_FRAME);

%%
t = figure;
filename = imgs{1};
img = imread(filename);
I = mat2img(img(:,:,1));
imshow(I);

% plot all tracked points
[xx, yy] = ginput(selection_count);
close(t);

TILL = img_index+2;
SHOW_CANDIDATES = false;
count = 0;
for u=img_index:TILL
    iter = 1;
    
    if u < TILL
        prevFrameIdcs = zeros(2, selection_count*num_el);
    end
    
    frame = frames{u};
    if SHOW_CANDIDATES
        figure('name', 'tracked points');
        img = imread(imgs{1});
        imshow(img(:,:,1))
        hold on;
        for idx_k = 1:length(frame.ax),
            ax = frame.ax(idx_k);
            ay = frame.ay(idx_k);
            plot(ay, ax, '.r')
        end
    end

    len = length(frame.ax);
    row_idx = zeros(len, 1);
    col_idx = zeros(len, 1);

    row_ids = frame.ax;
    col_ids = frame.ay;

    % TODO iterate over for loop here

    for k=1:length(xx)
        if count == 0
            if k == 1
                %next_lookups = zeros(length(xx), length(tracking_p_range));
            end
            x = xx(k);
            y = yy(k);
            distances = sum([row_ids-y,col_ids-x].^2, 2);

            % find smallest num_el labels
            [ASorted, AIdx] = sort(distances);
            smallestNElements = ASorted(1:num_el);
            smallestNIdx = AIdx(1:num_el);

            labels = zeros(length(smallestNIdx),1);
            for l_idx = 1:length(smallestNIdx)
                labels(l_idx) = frame.labels(smallestNIdx(l_idx));
            end
        else
            smallestNIdx = next_lookups(k, :);
            if smallestNIdx ~= 0
                labels = zeros(length(smallestNIdx),1);
                for l_idx = 1:length(smallestNIdx)
                    labels(l_idx) = frame.labels(smallestNIdx(l_idx));
                end
            end
        end

        %%
        disp('the FROM position in img1 has to correspond to the TO position in img2.');
        if SHOULD_PLOT3D
            img1 = imread(imgs{1});
            img1 = img1(:,:,1);
            img2 = imread(imgs{2});
            img2 = img2(:,:,1);
            if k == 1 && count == 0
                figure
            end
            direction = [1 0 0];
            degrees = 90;
            if count == 0
                surf((count)*ones(size(img1)), img1,'EdgeColor','none');
            end
            hold on
            % if mod(count, 3) == 2
                surf((count+1)*ones(size(img2)), img2,'EdgeColor','none');
                
            % end
            grid off
            axis off
            alpha(.35)
            set(gca,'YDir','reverse');
            colormap(gray)
            %set(gca,'Xdir','reverse','Ydir','reverse')
        end

        for r=1:1,
            if SHOULD_PLOT3D == false
                figure('name', strcat('img ', num2str(r)));
                img = imread(imgs{r});
                imshow(img)
                hold on;
            end

            tracking_p_range = labels(labels > 0);
            if k == 1 && mod(count, 2) == 0 
                next_lookups = zeros(length(xx), length(tracking_p_range));
            end

            drawArrow = @(x,y) quiver( x(1),y(1),x(2)-x(1), y(2)-y(1),0 );
            drawArrow3d = @(x,y,z) quiver3(x(1),y(1),z(1),x(2)-x(1),y(2)-y(1),z(2));

            for t=1:length(tracking_p_range),

                % find tracked pixels at frame 1
                label = tracking_p_range(t);

                data_idx = smallestNIdx(t);
                if data_idx == 0
                    continue
                end

                x0 = frame.ax(data_idx);
                y0 = frame.ay(data_idx);

                next_frame = frames{img_index+1};

                next_data_idx = find(next_frame.labels == label);
                x1 = next_frame.ax(next_data_idx);
                y1 = next_frame.ay(next_data_idx);
                
                if u < TILL
                    prevFrameIdcs(1, iter) = x1;
                    prevFrameIdcs(2, iter) = y1;
                    iter = iter + 1;
                end

                next_lookups(k, t) = next_data_idx;

                if isempty(x1) || isempty(y1)
                    continue;
                end

                %%
                x = [x0, x1];
                y = [y0, y1];
                if SHOULD_PLOT3D
                    z = [count,(count + 1)];
                    plot3(y1, x1, (count + 1), 'Color', [1 1 0], 'MarkerSize', MARKER_THICKNESS, 'Marker', '.');
                    if count < length(img_index:TILL) - 1
                        plot3(y0, x0, count, 'Color', [1 1 0], 'MarkerSize', MARKER_THICKNESS, 'Marker', '.');
                        hold on
                        drawArrow3d(y, x, z);
                    end
                else
                    plot(y0, x0, '.r');
                    plot(y1, x1, '.b');
                    drawArrow(y, x);
                end

            end
        end
    end
    prevFrame = frame;
count = count + 1;
end

for t=1:length(prevFrameIdcs),
    idxs = prevFrameIdcs(:, t);
    x1 = idxs(1);
    y1 = idxs(2);
    plot3(y1, x1, (count), 'Color', [1 1 0], 'MarkerSize', MARKER_THICKNESS, 'Marker', '.');
    hold on
end
