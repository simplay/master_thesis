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
num_el = 10;

% load relevant trajectory associated data
[imgs, fwf, bwf, boundaries, label_mappings, frames] = load_trajectory_data(DATASET, METHODNAME, PREFIX_INPUT_FILENAME, START_FRAME, END_FRAME);

%%
t = figure;
filename = imgs{1};
img = imread(filename);
I = mat2img(img(:,:,1));
imshow(I);

% plot all tracked points
[x, y] = ginput(1);
close(t);

frame = frames{img_index};
figure('name', 'tracked points');
img = imread(imgs{1});
imshow(img(:,:,1))
hold on;
for idx_k = 1:length(frame.ax),
    ax = frame.ax(idx_k);
    ay = frame.ay(idx_k);
    plot(ay, ax, '.r')
end

len = length(frame.ax);
row_idx = zeros(len, 1);
col_idx = zeros(len, 1);

row_ids = frame.ax;
col_ids = frame.ay;

distances = sum([row_ids-y,col_ids-x].^2, 2);

% find smallest num_el labels
[ASorted, AIdx] = sort(distances);
smallestNElements = ASorted(1:num_el);
smallestNIdx = AIdx(1:num_el);

labels = zeros(length(smallestNIdx),1);
for l_idx = 1:length(smallestNIdx)
    labels(l_idx) = frame.labels(smallestNIdx(l_idx));
end


%%
disp('the FROM position in img1 has to correspond to the TO position in img2.');

if SHOULD_PLOT3D
    img1 = imread(imgs{1});
    img1 = img1(:,:,1);
    img2 = imread(imgs{2});
    img2 = img2(:,:,1);
    figure
    surf(0*ones(size(img1)), img1,'EdgeColor','none')
    hold on
    surf(1*ones(size(img2)), img2,'EdgeColor','none')
    
end

for k=1:2,
    if SHOULD_PLOT3D == false
        figure('name', strcat('img ', num2str(k)));
        img = imread(imgs{k});
        imshow(img)
        hold on;
    end

    tracking_p_range = labels(labels > 0);

    drawArrow = @(x,y) quiver( x(1),y(1),x(2)-x(1), y(2)-y(1),0 );
    drawArrow3d = @(x,y,z) quiver3(x(1),y(1),z(1),x(2)-x(1),y(2)-y(1),z(2));

    for t=1:length(tracking_p_range),

        % find tracked pixels at frame 1
        label = tracking_p_range(t);

        data_idx = smallestNIdx(t);
        x0 = frame.ax(data_idx);
        y0 = frame.ay(data_idx);

        next_frame = frames{img_index+1};

        next_data_idx = find(next_frame.labels == label);
        x1 = next_frame.ax(next_data_idx);
        y1 = next_frame.ay(next_data_idx);

        if isempty(x1) || isempty(y1)
            continue;
        end

        %%
        x = [x0, x1];
        y = [y0, y1];
        if SHOULD_PLOT3D
            z = [0,1];
            plot3(y0,x0,0, '.r');
            plot3(y1,x1,1, '.b');
            drawArrow3d(y,x,z);   
        else
            plot(y0,x0, '.r');
            plot(y1,x1, '.b');
            drawArrow(y,x);
        end
        
    end
end