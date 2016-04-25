clear all;
close all;
clc;

addpath('../libs/flow-code-matlab');
addpath('src');
addpath('../matlab_shared');

DATASETNAME = 'alley2small';
METHODNAME = 'ldof';
STEP_SIZE = 4;
PRECISSION = 12;

COMPUTE_TRACKING_DATA = false; % compute tracking candidates, valid regions, flows
COMPUTE_FLOW_VARIANCES = false; % compute local and global flow variance
COMPUTE_CIE_LAB = true; % compute cie lab colors from given input seq
EXTRACT_DEPTH_FIELDS = false; % add check: only if depth fields do exist
COMPUTE_DEPTH_VARIANCE = false;

% encoding of own depth files: qRgb(0,(depth[idx]>>8)&255,depth[idx]&255);
% i.e. real depth value is d = 255*G + B
USE_OWN_DEPTHS = false;
DEPTH_SCALE = 0.0002;

VAR_SIGMA_S = 5;
VAR_SIGMA_R = 0.3; %apply to appropriate quiver region in flow field


%% 
BASE_OUTPUT_PATH = strcat('../output/tracker_data/',DATASETNAME,'/');
DATASET = strcat(DATASETNAME,'/');
BASE_FILE_PATH = strcat('../../Data/',DATASET);

% Create the folder if it doesn't exist already.
if ~exist(BASE_OUTPUT_PATH, 'dir')
    mkdir(BASE_OUTPUT_PATH);
end

[boundaries, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH, METHODNAME);
[m,n,~] = size(imread(imgs{1}));
START_FRAME_IDX = boundaries(1); 
END_FRAME_IDX = boundaries(2); 

%% extract flows, trackable regions, flow consistency regions
if COMPUTE_TRACKING_DATA
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
        [ tracking_candidates ] = findTrackingCandidates( img, STEP_SIZE );
        [trackable_row, trackable_col, ~] = find(tracking_candidates == 1);
        datasets = [trackable_row, trackable_col]';

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
        disp(strcat('Processed Frame', num2str(t), '...'));
    end
end

%% save metadata into file
fname = strcat('../output/tracker_data/',DATASETNAME,'/metainfo.txt');
fid = fopen(fname,'w');

% fileformat
data = [m, n, STEP_SIZE];
if fid ~= -1
    a_row = strcat(...
        num2str(data(1)), ',', ...
        num2str(data(2)), ',', ...
        num2str(data(3))...
    );
    fprintf(fid,'%s\r\n', a_row);
end
fclose(fid);
%%
% IMPORTANT: DEPTH FIELD VALUES HAVE TO BE IN METER SCALE
%
% A single-channel uint16 depth image. 
% Each pixel gives the depth in millimeters, 
% with 0 denoting missing depth. 
% The depth image can be read using MATLAB with the standard function (imread), 
% and in OpenCV by loading it into an image of type IPL_DEPTH_16U.
if EXTRACT_DEPTH_FIELDS
    path = ['../../Data/', DATASETNAME, '/depth/'];
    listing = dir(strcat('../../Data/', DATASETNAME, '/depth/*.png'));
    
    minFilenameIndex = 1000000;
    maxFilenameIndex = -1;
    for k=1:length(listing)
        f = listing(k);
        tillDot = strfind(f.name,'.png');
        fileNr = str2num(f.name(1:tillDot-1));
        
        if (fileNr < minFilenameIndex)
            minFilenameIndex = fileNr;
        end
        
        if (fileNr > maxFilenameIndex)
            maxFilenameIndex = fileNr;
        end
        
    end
    minFilenameIndex = minFilenameIndex - 1;
    
    for k=1:length(imgs)
        f = listing(k);
        fpath = strcat(path, f.name);
        lv = double(imread(fpath)); %* DEPTH_SCALE; % scale factor to tranform to meters
        
        % own extracted depths provided by code 3dDataAcquisition
        % stored depth images are 16 bit depth values, using the red and
        % blue color channel. See the method 3dDataAcquisition /
        % fileWriteTask.cpp#fileWriteTask::run()
        % depth = 0*R + 255*G + B => gives depths in [mm]
        % but we want to work in meter scales, therefore divide by 1000
        if USE_OWN_DEPTHS
            lv = (lv(:,:,2)*255 + lv(:,:,3)) / 1000;
        else
            lv = lv * DEPTH_SCALE; 
        end
        tillDot = strfind(f.name,'.png');
        fileNr = f.name(1:tillDot-1);
        fileNr = num2str(str2num(fileNr) - minFilenameIndex);
        fname = strcat('depth_',fileNr,'.txt');
        disp(strcat(num2str(k), '. Iteration - extracted depth field: ', fname));
        fnamePath = strcat('../output/tracker_data/',DATASETNAME,'/', fname);
        fid = fopen(fnamePath,'w');
        if fid ~= -1
            for t=1:size(lv,1)
                a_row = mat2str(lv(t,:));
                fprintf(fid,'%s\r\n', a_row);
            end
        end
        fclose(fid);
        
        valid_depths = double(lv > 0);
        fname = strcat('../output/tracker_data/',DATASET,'/valid_depth_region_', fileNr, '.txt');
        imgfile = strcat('../output/tracker_data/',DATASET,'/valid_depth_region_', fileNr, '.png');
 
        imwrite(mat2img(valid_depths), imgfile);
        fid = fopen(fname,'w');
        if fid ~= -1
            for t=1:size(valid_depths,1)
                a_row = mat2str(valid_depths(t,:));
                fprintf(fid,'%s\r\n', a_row);
            end
        end
        fclose(fid);
        
        % compute depth variance
        if COMPUTE_DEPTH_VARIANCE
            
            % de-noising issue: wie noise sch?ten:
            %
            % mu looks almost like lv, i.e. lv is a good estiator for mu
            % variance via bilateral filter, dann entlang von kanten
            % problematisch, besser w?re die nutzung von 3d vektoren. dann
            % w?rde die annahme "lokal const flow fields" sinnvoller sein.
            % depth fields haben allerei probleme, daher macht es durchaus
            % sinn, lediglich depth in spatial dist. term zu verwenden.
            %
            % dif = ((lv2-lv1).^2) .*(lv1>0).*(lv2 > 0);
            % imshow(dif*1000)
            % imshow(1000*(mu-lv1).^2)
            % 
            % t = lv(50:80, 50:80);
            % 0.1 = std(t(:));
            
            % IMPORTANT: DEPTH FIELD VALUES HAVE TO BE IN METER SCALE
            [var, ~] = computeLocalDepthVar(lv, VAR_SIGMA_S, 0.1, lv > 0);
            
            fname = strcat('../output/tracker_data/',DATASET,'/local_depth_variances_', fileNr, '.txt');
            imgfile = strcat('../output/tracker_data/',DATASET,'/local_depth_variances_', fileNr, '.png');
            var_img = var ./ max(var(:));
            imwrite(var_img, imgfile);
            fid = fopen(fname,'w');
            if fid ~= -1
                for t=1:size(var,1)
                    a_row = mat2str(var(t,:));
                    fprintf(fid,'%s\r\n', a_row);
                end
            end
            fclose(fid);
            
        end
        
    end
end

% generate cie lab imgs
if COMPUTE_CIE_LAB
    disp('Generating CIE L*a*b* files...')
    for t=START_FRAME_IDX:END_FRAME_IDX+1
        img = imread(imgs{t});
        colorTransform = makecform('srgb2lab');
        lab = applycform(img, colorTransform);
        [rows, columns, ~] = size(lab);
        fname = strcat('color_lab_',num2str(t),'.txt');
        disp(['Computing ', fname]);
        fpname = strcat('../output/tracker_data/',DATASETNAME,'/', fname);
        fid = fopen(fpname, 'wt');
        for col = 1 : columns
            for row = 1 : rows
                fprintf(fid, '%d,%d = (%d,%d,%d)\n', ...
                    row, col, ...
                    lab(row, col, 1),...
                    lab(row, col, 2),...
                    lab(row, col, 3));
            end
        end
        fclose(fid);
    end
    disp('CIE L*a*b* files generated.')
end

%% compute local and global variance
if COMPUTE_FLOW_VARIANCES
    
    global_variances = [];
    local_flow_variances = zeros(m,n,END_FRAME_IDX);
    for t=START_FRAME_IDX:END_FRAME_IDX
        fw_flow_t = fwf{t};
        fw_flow = readFlowFile(fw_flow_t);
        
        % store local variance data
        fname = strcat('../output/tracker_data/',DATASETNAME,'/flow_consistency_',num2str(t),'.mat');
        invalid_regions = load(fname, '-ASCII');
        disp(['Flow Variance Iteration ', num2str(t), '...']);
        local_flow_variances(:,:,t) = computeLocalFlowVar(fw_flow, 0, 0, VAR_SIGMA_S, VAR_SIGMA_R, (1.0-invalid_regions));
        
        global_variances = [global_variances, var(fw_flow(:))];
    end
    fName = strcat('../output/tracker_data/',DATASET,'/global_variances','.txt');
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
        disp([num2str(k), '. flow variance iteration...']);
        lv = local_flow_variances(:,:,k);
        fname = strcat('../output/tracker_data/',DATASET,'/local_variances_',num2str(k),'.txt');
        imgfile = strcat('../output/tracker_data/',DATASET,'/local_variances_',num2str(k),'.png');
        imwrite(lv, imgfile);
        fid = fopen(fname,'w');
        if fid ~= -1
            for t=1:size(lv,1)
                a_row = mat2str(lv(t,:));
                fprintf(fid,'%s\r\n', a_row);
            end
        end
        fclose(fid);
    end
end
