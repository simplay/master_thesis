addpath('../libs/flow-code-matlab');

DATASETNAME = 'c14';

% Output this info as a meta information
step_size = 8;
PRECISSION = 12;

% global variable used for assigning unique label indices
set_global_label_idx(1);

BASE_OUTPUT_PATH = strcat('../output/tracker_data/',DATASETNAME,'/');
METHODNAME = 'ldof';
DATASET = strcat(DATASETNAME,'/');
BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASET); % dataset that should be used

[boundaries, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH);
START_FRAME_IDX = boundaries(1); % inital index 1
END_FRAME_IDX = boundaries(2); % for car example max 4


for t=START_FRAME_IDX:END_FRAME_IDX
    
    % Save the forward and backward flows, 
    % decomposes into their scalar fields formed by their direcional
    % components u and v
    fw_flow_t = fwf{t};
    bw_flow_t = bwf{t};
    
    forward_flow = readFlowFile(fw_flow_t);
    forward_flow_u = forward_flow(:,:,2);
    forward_flow_v = forward_flow(:,:,1);
    
    backward_flow = readFlowFile(bw_flow_t);
    backward_flow_u = backward_flow(:,:,2);
    backward_flow_v = backward_flow(:,:,1);
    
    fwuName = strcat(BASE_OUTPUT_PATH,'fw_u_',num2str(t),'.mat');
    bwuName = strcat(BASE_OUTPUT_PATH,'bw_u_',num2str(t),'.mat');
    fwvName = strcat(BASE_OUTPUT_PATH,'fw_v_',num2str(t),'.mat');
    bwvName = strcat(BASE_OUTPUT_PATH,'bw_v_',num2str(t),'.mat');
    
    dlmwrite(fwuName,forward_flow_u, 'delimiter',' ','precision',PRECISSION);
    dlmwrite(bwuName,backward_flow_u, 'delimiter', ' ','precision',PRECISSION);
    dlmwrite(fwvName,forward_flow_v, 'delimiter',' ','precision',PRECISSION);
    dlmwrite(bwvName,backward_flow_v, 'delimiter',' ','precision',PRECISSION);
    
    % Save a (m x n) matrix that contains all invalid pixel loations
    diffName = strcat(BASE_OUTPUT_PATH,'flow_consistency_',num2str(t),'.mat'); 
    invalid_regions = consistency_check( forward_flow, backward_flow );
    dlmwrite(diffName, invalid_regions, 'delimiter',' ','precision',PRECISSION);
    
    % Save row and column indicess of trackable pixel locations
    frame_t = imgs{t};
    img = im2double(imread(frame_t));
    [ tracking_candidates ] = findTrackingCandidates( img, step_size );
    [trackable_row, trackable_col, ~] = find(tracking_candidates == 1);
    datasets = [trackable_row, trackable_col]';
    
    % Create the folder if it doesn't exist already.
    if ~exist(BASE_OUTPUT_PATH, 'dir')
        mkdir(BASE_OUTPUT_PATH);
    end
    
    % save trackable row and col in text file
    fName = strcat(BASE_OUTPUT_PATH,'candidates_',num2str(t),'.txt');
    fid = fopen(fName, 'w');
    if fid ~= -1
        for k=1:size(datasets,1)
            row_k = datasets(k,:);

            % print only tracked pixels locations
            if row_k(1) ~= 0
            fprintf(fid,'%s\r\n',mat2str(row_k));
            end
        end    
        fclose(fid);
    end
    
 end


