DATASET = 'c14';
MODE = 4;
img_index = 3;

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

% loads the tracking tensor
load(savefile);
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

%%
t = figure;
filename = imgs{1};
img = imread(filename);
I = mat2img(img(:,:,1));
imshow(I);

% plot all tracked points

num_el = 5;
[x, y] = ginput(1);
close(t);

pixeltensor = tracking_tensor(:,:,:,1);
[row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);

distances = sum([row_ids-y,col_ids-x].^2, 2);
% [~,idx_pos] = min(distances);

% find smallest num_el labels
[ASorted AIdx] = sort(distances);
smallestNElements = ASorted(1:num_el);
smallestNIdx = AIdx(1:num_el);

labels = zeros(length(smallestNIdx),1);
for l_idx = 1:length(smallestNIdx)
    smallestNIdx(l_idx)
    %col_sel = pixeltensor(row_ids(smallestNIdx(l_idx)), col_ids(smallestNIdx(l_idx)), 2);
    col_sel = pixeltensor(col_ids(smallestNIdx(l_idx)), row_ids(smallestNIdx(l_idx)), 2);
    labels(l_idx) = col_sel;
end


%%

[m, n, features, timesteps] = size(tracking_tensor);

frame = imread(imgs{1});
imshow(frame)
hold on;

tracking_p_range = labels(labels > 0);

drawArrow = @(x,y) quiver( x(1),y(1),x(2)-x(1), y(2)-y(1),0 );
for t=1:length(tracking_p_range),

    % find tracked pixels at frame 1
    [u,v,w ] = find(tracking_tensor(:,:,1,1) == 1);

    t_id = tracking_p_range(t);

    % extract trajectory identifier
    x0 = tracking_tensor(u(t_id), v(t_id), 3, 1);
    y0 = tracking_tensor(u(t_id), v(t_id), 4, 1);


    label = tracking_tensor(u(t_id),v(t_id),2,1);

    [n_u, n_v, w_v] = find(tracking_tensor(:,:,2,2) == label);
    if isempty(n_u) || isempty(n_v)
        continue;
    end
    x1 = tracking_tensor(n_u, n_v, 3, 2);
    y1 = tracking_tensor(n_u, n_v, 4, 2);


    %%
    x = [x0, x1];
    y = [y0, y1];
    plot(y0,x0, '.r')
    plot(y1,x1, '.b')
    drawArrow(y,x);
end