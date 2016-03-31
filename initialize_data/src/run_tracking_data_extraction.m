function run_tracking_data_extraction( DATASETNAME, STEP_SIZE, COMPUTE_TRACKINGS, MODE, DISPLAY, WRITE_TRACKINGS_INTO_FILES, VAR_SIGMA_S, VAR_SIGMA_R, SHOW_VIDEO, COMPUTE_LOCAL_VAR, COMPUTE_CIE_LAB, RUN_BILAT_FILT)
    % DATASETNAME = 'cars1';
    % STEP_SIZE = 8
    % MODE = 5 % display mode
    % DISPLAY = false; % show tracking point
    % WRITE_TRACKINGS_INTO_FILES = true;
    addpath('../libs/flow-code-matlab');
    %% 

    % global variable used for assigning unique label indices
    %set_global_label_idx(1);

    METHODNAME = 'ldof';
    DATASET = strcat(DATASETNAME,'/');
    BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASET); % dataset that should be used

    [boundaries, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH);
    START_FRAME_IDX = boundaries(1); % inital index 1
    END_FRAME_IDX = boundaries(2); % for car example max 4
    
    if SHOW_VIDEO
        animate_seq(imgs, 1, length(imgs));
    end
    
    %% 
    if RUN_BILAT_FILT == false
        n = (END_FRAME_IDX - START_FRAME_IDX + 1);
        k = 1;
        varMinima = zeros(1,n);
        varMaxima = zeros(1,n);
        lfv_fName = strcat(BASE_FILE_PATH,'sub/extrema.txt');
        fid = fopen(lfv_fName);     
        tline = fgets(fid);
        
        while ischar(tline)
            if k > n
                break;
            end
            tline = fgets(fid);
            idx = regexp(tline,',');
            k
            varMinima(k) = str2num(tline(1:idx-1));
            varMaxima(k) = str2num(tline(idx+1:end));
            k = k + 1;
        end
        fclose(fid);
    end
    %%
    

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
    
    [m,n,~] = size(imread(imgs{1}));
    %% compute local and global variance
    global_variances = [];
    local_flow_variances = zeros(m,n,END_FRAME_IDX);
    for t=START_FRAME_IDX:END_FRAME_IDX
        fw_flow_t = fwf{t};
        fw_flow = readFlowFile(fw_flow_t);
        if COMPUTE_LOCAL_VAR
            if RUN_BILAT_FILT
                fname = strcat('../output/tracker_data/',DATASETNAME,'/flow_consistency_',num2str(t),'.mat');
                invalid_regions = load(fname, '-ASCII');
                local_flow_variances(:,:,t) = computeLocalFlowVar(fw_flow, 0, 0, VAR_SIGMA_S, VAR_SIGMA_R, (1.0-invalid_regions));
            else
                lfv_fName = strcat(BASE_FILE_PATH,'sub/');
                base = 'localFlowVariation';
                ext = '.pgm';
                idx = END_FRAME_IDX-START_FRAME_IDX+t;
                fName = strcat(lfv_fName,base,num2str(t-1),ext);
                lfvImg = imread(fName);
                lfvImg = im2double(lfvImg);
                
                % rescale variances of current frame by its extreme values
                % since brox' implementation actually returns the std, we
                % have to square the values element-wise.
                local_flow_variances(:,:,t) = (lfvImg*varMaxima(t)+varMinima(t)).^2;
            end
        end
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
    
    if COMPUTE_LOCAL_VAR
        % write local flow variances into mat files.
        for k=1:END_FRAME_IDX
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
    




end

