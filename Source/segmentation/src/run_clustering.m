function [W, U_small, S_small, WW, U_full, S_full] = run_clustering( DATASET, METHODNAME, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U_small, S_small, SELECTED_ENTITY_IDX, USE_T, frame_idx, WW, SHOULD_LOAD_W, PERFORM_AUTO_RESCALE, LAMBDA, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, SHOW_LOCAL_VAR, VAR_IMG, FORCE_EW_COUNT, USE_SPECIAL_NAMING, USE_BF_BOUND, BOUNDARY, U_full, S_full, COMPUTE_FULL_RANGE)
%RUN_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here

    % the following flag define what data should be displayed.
    % USE_CLUSTERING_CUE true => display segmentation
    % USE_CLUSTERING_CUE false && USE_W_VEC true => display affinities
    % USE_CLUSTERING_CUE false && USE_W_VEC => display eigenvectors
    %     USE_W_VEC = false;
    %     USE_CLUSTERING_CUE = true;
    
    U_small = U_full;
    S_small = S_full;
    
    KERNIGHAN_LIN = false;

    BASE = '../output/similarities/';
    if USE_SPECIAL_NAMING
        idx = regexp(DATASET,'v');
        DATASETNAME = DATASET(1:idx-1);
    else    
        DATASETNAME = DATASET;
    end

    DATASETP = strcat(DATASETNAME,'/');
    BASE_FILE_PATH = strcat('../../Data/', DATASETP);

    %% load appropriate data
    if COMPUTE_EIGS
        if SHOULD_LOAD_W %1 == 0
            fname = strcat(BASE,DATASET,'_sim.dat');
            W = load(fname);
        end
        
        %keyboard;
        WW = W + ones(size(W))*THRESH;

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
        
        U_full = U_small;
        S_full = S_small;
        
        if USE_T
            T = zeros(size(U_small));
            for k=1:size(U_small,1)
            T(k,:) = U_small(k,:) ./ sqrt(sum(U_small(k,:).^2));
            end
            U_small = T;
        end
        


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

    U_small = aggregate_mat_cols(U_small, aa);
    S_small = d(aa);

    S_small = S_small(S_small > 0);
    U_small = U_small(:,S_small > 0);
    
    %% display segmentation and its data.

    % load label vector indices mappings
    label_mappings = labelfile2mat(strcat(BASE, DATASET));
    [boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH, METHODNAME);

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
    if USE_BF_BOUND
        boundaries(1) = BOUNDARY(1);
        boundaries(2) = BOUNDARY(2);
    end

    frames = loadAllTrajectoryLabelFrames(DATASET, boundaries(1), boundaries(2));
    
    range = frame_idx:frame_idx;
    if COMPUTE_FULL_RANGE
        range = boundaries(1):1:boundaries(2);
    end
    
    if USE_CLUSTERING_CUE
        [label_assignments] = spectral_custering( U_small, CLUSTER_CENTER_COUNT, 100, true);    
    end
    
    path = strcat('../output/clustering/', DATASET, '/');
    mkdir(path);
    
    for img_index = range
        fig = figure('name', strcat('Frame ', num2str(img_index)));
        method_id = strcat(METHODNAME);
        disp('');
        
        fpname = strcat(path, 'seg_', method_id, '_f_', num2str(img_index), '.jpg');

        if SELECT_AFFINITY_IDX
            frame = frames{img_index};
            row_ids = frame.ax;
            col_ids = frame.ay;

            %pixeltensor = tracking_tensor(:,:,:,1);
            %[row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);

            distances = sum([row_ids-y,col_ids-x].^2, 2);
            % [~,idx_pos] = min(distances);
            num_el = 1;
            % find smallest num_el labels
            [~, AIdx] = sort(distances);
            smallestNIdx = AIdx(1:num_el);
            col_sel = frame.labels(smallestNIdx(1));
        end
        
        if USE_CLUSTERING_CUE
            visualize_segmentation(frames, imgs, label_assignments, label_mappings, img_index);
            write_label_clustering_file(label_assignments, label_mappings, img_index, DATASET);
            saveas(fig, fpname);
        else
            displayed_vector = extract_vector( U_small, W, col_sel, USE_W_VEC, label_mappings);
            if USE_W_VEC
                if SHOW_LOCAL_VAR
                    var_im_name = strcat('../output/trackings/',DATASET,'/local_variances_',num2str(VAR_IMG),'.png');
                    imv = imread(var_im_name);
                    imshow(imv);
                    figure;
                end
                label_idx = col_sel;
                visualize_affinities(W, label_idx, frames, imgs, label_mappings, img_index);
                % display_affinity_vec(pixeltensor, displayed_vector, row_ids, col_ids, img_index, col_sel, label_mappings, imgs);
            else
                eigenvalue = S_small(col_sel);
                visualize_eigenvector(eigenvalue, U_small, col_sel, frames, imgs, label_mappings, img_index);
                %display_eigenvectors(pixeltensor, displayed_vector, row_ids, col_ids, img_index, eigenvalue, label_mappings, imgs);
            end
        end
    end

end

