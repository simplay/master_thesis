clc;
%clear all;
close all;
addpath('../libs/flow-code-matlab');

 %% 
 
% global variable used for assigning unique label indices
set_global_label_idx(1);

STEP_SIZE = 8; % tracking density
DATASETNAME = 'chair';
METHODNAME = 'ldof';
DATASET = strcat(DATASETNAME,'/');
BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASET); % dataset that should be used
DISPLAY = true; % show tracking points
MODE = 5; % display mode

WRITE_TRACKINGS_INTO_FILES = true;


[boundaries, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH);
START_FRAME_IDX = boundaries(1); % inital index 1
END_FRAME_IDX = boundaries(2); % for car example max 4

%% working example
% fix naming of files: since image naming indices start counting by 1 and
% flow field by zero, there is a potential confusion.
% for the first image k, we have to assign flow k-1 
% img1 = imread('../data/ldof/cars1/01.ppm');
% img2 = imread('../data/ldof/cars1/02.ppm');
% foreward_flow = readFlowFile('../data/ldof/cars1/ForwardFlow000.flo');
% backward_flow = readFlowFile('../data/ldof/cars1/BackwardFlow000.flo');

[m,n,~] = size(imread(imgs{1}));
start_mask = ones(m,n);
prev_tacked_pixels = zeros(m,n,7);
prev_foreward_flow = 0;
prev_backward_flow = 0;
% initially, there are no tracked to positions
tracked_to_positions = zeros(m,n);
for t=START_FRAME_IDX:END_FRAME_IDX,
    frame_t = imgs{t};
    im_tp1 = imgs{t+1};
    fw_flow_t = fwf{t};
    bw_flow_t = bwf{t};
    [ tracked_pixels, trackable_pixels, invalid_regions, old_start_mask, prev_foreward_flow, prev_backward_flow ] = ...
        process_frame_pair( frame_t, fw_flow_t, bw_flow_t, STEP_SIZE, start_mask, tracked_to_positions, prev_tacked_pixels, prev_foreward_flow, prev_backward_flow);

    save(strcat('../output/trackingdata/',DATASETNAME,'_step_',num2str(STEP_SIZE),'_frame_',num2str(t),'.mat'),'tracked_pixels');
    % write data into file
    if WRITE_TRACKINGS_INTO_FILES
        write_flow_data(tracked_pixels,t,DATASET);
    end
    
    t_idx = t; tp1_idx = t+1;
    if DISPLAY
        display_tracking_figures(frame_t, im_tp1, trackable_pixels, tracked_pixels, t_idx, tp1_idx, MODE, prev_tacked_pixels);
    end
    
    % overwrite data after having plotted them
    start_mask = old_start_mask;
    tracked_to_positions = tracked_pixels(:,:,1);
    prev_tacked_pixels = tracked_pixels;
end

%% compute local and global variance
global_variances = [];
local_flow_variances = zeros(m,n,END_FRAME_IDX);
for t=START_FRAME_IDX:END_FRAME_IDX
    fw_flow_t = fwf{t};
    fw_flow = readFlowFile(fw_flow_t);
    local_flow_variances(:,:,t) = computeLocalFlowVar(fw_flow);
    global_variances = [global_variances, var(fw_flow(:))];
end
fName = strcat('../output/trackings/',DATASET,'global_variances','.txt');
fid = fopen(fName,'w');
if fid ~= -1
    for k=1:length(global_variances)
     row_k = global_variances(k);

     % print only tracked pixels locations
     if row_k(1) ~= 0
        fprintf(fid,'%s\r\n',num2str(row_k));
     end
    end    
    fclose(fid);
end

% write local flow variances into mat files.
for k=1:END_FRAME_IDX
    lv = local_flow_variances(:,:,k);
    fname = strcat('../output/trackings/',DATASET,'local_variances_',num2str(k),'.txt');
    fid = fopen(fname,'w');
    if fid ~= -1
        for t=1:size(lv,1)
            a_row = mat2str(lv(t,:));
            fprintf(fid,'%s\r\n', a_row);
        end
    end
    fclose(fid);
end
