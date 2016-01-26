function [W, U_small, S_small, WW] = run_clustering( DATASET, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U_small, S_small, SELECTED_ENTITY_IDX, USE_T, frame_idx, WW, SHOULD_LOAD_W, PERFORM_AUTO_RESCALE, LAMBDA, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, SHOW_LOCAL_VAR, VAR_IMG, FORCE_EW_COUNT, USE_SPECIAL_NAMING)
%RUN_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here

    % the following flag define what data should be displayed.
    % USE_CLUSTERING_CUE true => display segmentation
    % USE_CLUSTERING_CUE false && USE_W_VEC true => display affinities
    % USE_CLUSTERING_CUE false && USE_W_VEC => display eigenvectors
    %     USE_W_VEC = false;
    %     USE_CLUSTERING_CUE = true;

    addpath('../libs/flow-code-matlab');
    BASE = '../output/similarities/';
    % 'cars1_step_8_frame_';
    PREFIX_FRAME_TENSOR_FILE = [DATASET,'_step_',num2str(STEPSIZE_DATA),'_frame_'];
    
    if USE_SPECIAL_NAMING
        
        idx = regexp(DATASET,'v');
        DATASETNAME = DATASET(1:idx-1);
        PREFIX_FRAME_TENSOR_FILE = [DATASETNAME,'_step_',num2str(STEPSIZE_DATA),'_frame_'];
    else    
        DATASETNAME = DATASET;
    end
    METHODNAME = 'ldof'; %other,ldof
    DATASETP = strcat(DATASETNAME,'/');
    BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASETP);

    %% load appropriate data
    if COMPUTE_EIGS
        if SHOULD_LOAD_W %1 == 0
            fname = strcat(BASE,DATASET,'_sim.dat');
            W = load(fname);
        end
        
        if PERFORM_AUTO_RESCALE
            
            % a well performing example has for f:
            % when f = sum(W(:))/(length(W)^2)
            % then f == 0.0557
            well_scale = 0.0557
            len = size(W,1);
            scale = sum(W(:))/(len*len);
            f = (scale/well_scale);
            
            w = -(log(W)/LAMBDA);
            w(isinf(w)) = 1000000;
            WWW = W;
            %W = (exp(-w*(LAMBDA/f)));
            W = (exp(-w*(LAMBDA/f)*0.3));
        end
        
        WW = W + ones(size(W))*THRESH;

        %WW = W + diag(THRESH*ones(size(W,1),1));
        
        %sW = sort(W,2, 'descend'); ten = sW(:,100); thresh = repmat(ten, 1, size(W,2)); biggest = W.*(W>thresh); WW = max(biggest,biggest');
        d_a = sum(WW,2);
        D = diag(d_a);
        D12 = diag(sqrt(1./d_a));
        % keyboard;
        B = D12*(D-WW)*D12;
        if USE_EIGS
            [U_small,S_small,FLAG] = eigs(B,50,1e-6);
        else
            [U_small,S_small] = eig(B);
        end
        
        if USE_T
            T = zeros(size(U_small));
            for k=1:size(U_small,1)
            T(k,:) = U_small(k,:) ./ sqrt(sum(U_small(k,:).^2));
            end
            U_small = T;
        end
        
        
        d = diag(S_small);
        [d, s_idx] = sort(d);
        U_small = aggregate_mat_cols(U_small, s_idx);
        
        
        if USE_CLUSER_EW_COUNT
            aa = 1:FORCE_EW_COUNT;
        else
        
        [aa,~,~] = find(d < 0.1);
        end
        UU = U_small;

    %     if USE_SPECTRAL_GAP
    %         deltas = S_small(2:end)-S_small(1:end-1);
    %         dbm = deltas <= mean(deltas); % deltas below mean
    %         zero_idxs = find(dbm == 0);
    %         use_till = zero_idxs(1)-1;
    %         [aa, ~] = find(aa <= use_till);
    %     end

        U_small = aggregate_mat_cols(U_small, aa);
        S_small = d(aa)
        


    end

    %% display segmentation and its data.

    % load label vector indices mappings
    label_mappings = labelfile2mat(strcat(BASE,DATASET));
    [~, imgs, ~, ~] = read_metadata(BASE_FILE_PATH);

    % to help the user what values/index pairs can be displayed.
    show_usage_information(USE_W_VEC, USE_CLUSTERING_CUE, W, U_small);

    % NOTICE: col_sel stands for the target column of the dataset that should
    %   be plotted. Since eigenvectors, affinitires and label_assignments
    %   have different vector dimensionalities, this affects the possible 
    %   indexing (querry) range. Therefore, when
    %       plotting eigenvectors, then do not pass a bigger index than
    %           size(U_small,2)
    %       plotting affinities, then do not pass a bigger index than length(W)
    %   when plotting cluster assignments, the chosen index value has no
    %   effect.
    %
    % HINT: nice indices when working with affinities:
    %   300 - car in background
    %   2033 - right wheel front car (issue case: no neighboring assignments)
    %       cmp with 2000
    %   975 - front car car front (issue case: no neighboring assignments)
    
    if SELECT_AFFINITY_IDX
        t = figure
        filename = imgs{frame_idx};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        [x, y] = ginput(1);
        close(t);
    end
    
    col_sel = SELECTED_ENTITY_IDX;
    % load W in case it is needed.
    if ~exist('W','var') && USE_W_VEC
        W = load('../output/similarities/cars1_sim.dat');
    end

    % display data
    for img_index = frame_idx:frame_idx
        figure

        pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(img_index),'.mat'));
        pixeltensor = pixeltensor.tracked_pixels;
        [row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);
        
        if SELECT_AFFINITY_IDX
            [~,idx_pos] = min(sum([row_ids-y,col_ids-x].^2,2));
            col_sel = pixeltensor(row_ids(idx_pos), col_ids(idx_pos), 2);
            disp(['Selected label with index ',num2str(col_sel)])
        end
        
        if USE_CLUSTERING_CUE    
            [label_assignments] = spectral_custering( U_small, CLUSTER_CENTER_COUNT);
            display_clustering(pixeltensor, label_assignments, row_ids, col_ids, img_index, label_mappings, imgs);
            write_label_clustering_file(label_assignments, label_mappings, img_index, DATASET);
        else
            displayed_vector = extract_vector( U_small, W, col_sel, USE_W_VEC, label_mappings);
            if USE_W_VEC
                if SHOW_LOCAL_VAR
                    var_im_name = strcat('../output/trackings/',DATASET,'/local_variances_',num2str(VAR_IMG),'.png');
                    imv = imread(var_im_name);
                    imshow(imv);
                    figure;
                end
                display_affinity_vec(pixeltensor, displayed_vector, row_ids, col_ids, img_index, col_sel, label_mappings, imgs);
            else
                eigenvalue = S_small(col_sel);
                display_eigenvectors(pixeltensor, displayed_vector, row_ids, col_ids, img_index, eigenvalue, label_mappings, imgs);
            end
        end
    end

end

