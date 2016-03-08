DATASET = 'c14';
img_index = 4;
START_FRAME = 1;
END_FRAME = 4;

addpath('../libs/flow-code-matlab');
BASE = '../output/similarities/';
% 'cars1_step_8_frame_';
STEPSIZE_DATA = 8;
PREFIX_FRAME_TENSOR_FILE = [DATASET,'_step_',num2str(STEPSIZE_DATA),'_frame_'];

DATASETNAME = DATASET;
METHODNAME = 'ldof'; %other,ldof
DATASETP = strcat(DATASETNAME,'/');
BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASETP);

label_mappings = labelfile2mat(strcat(BASE,DATASET));
[~, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH);

fnpath = strcat('../output/tracking_tensor/');
savefile = strcat(fnpath, strcat(DATASET,'.mat'));

%%
t = figure;
filename = imgs{1};
img = imread(filename);
I = mat2img(img(:,:,1));
imshow(I);

% plot all tracked points

num_el = 3;
[x, y] = ginput(1);
close(t);


frames = loadAllTrajectoryLabelFrames(DATASET, START_FRAME, END_FRAME);
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
%for idx=1:len
    
%end
row_ids = frame.ax;
col_ids = frame.ay;

%pixeltensor = tracking_tensor(:,:,:,1);
%[row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);

distances = sum([row_ids-y,col_ids-x].^2, 2);
% [~,idx_pos] = min(distances);

% find smallest num_el labels
[ASorted, AIdx] = sort(distances);
smallestNElements = ASorted(1:num_el);
smallestNIdx = AIdx(1:num_el);

labels = zeros(length(smallestNIdx),1);
for l_idx = 1:length(smallestNIdx)
    %smallestNIdx(l_idx)
    %col_sel = pixeltensor(row_ids(smallestNIdx(l_idx)), col_ids(smallestNIdx(l_idx)), 2);
    
    %col_sel = pixeltensor(col_ids(smallestNIdx(l_idx)), row_ids(smallestNIdx(l_idx)), 2);
    labels(l_idx) = frame.labels(smallestNIdx(l_idx));
end


%%
disp('the FROM position in img1 has to correspond to the TO position in img2.');
for k=1:2,
figure('name', strcat('img ', num2str(k)));
img = imread(imgs{k});
imshow(img)
hold on;

tracking_p_range = labels(labels > 0);

drawArrow = @(x,y) quiver( x(1),y(1),x(2)-x(1), y(2)-y(1),0 );
for t=1:length(tracking_p_range),

    % find tracked pixels at frame 1
    %[u,v,w ] = find(tracking_tensor(:,:,1,1) == 1);

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
    plot(y0,x0, '.r')
    plot(y1,x1, '.b')
    drawArrow(y,x);
end
end