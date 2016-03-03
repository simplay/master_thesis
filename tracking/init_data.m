addpath('../libs/flow-code-matlab');

DATASETNAME = 'c14';
step_size = 8;

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
    
    % extract image data
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
    
    
    
    
    % extract flow data
    fw_flow_t = fwf{t};
    bw_flow_t = bwf{t};
    
    
    
 end


