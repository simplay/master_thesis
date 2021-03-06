function [W, U_small, S_small, U_full, S_full, assignments] = run_clustering(DATASET, METHODNAME, RUN_MODE, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, W, SELECTED_ENTITY_IDX, frame_idx, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, FORCE_EW_COUNT, U_full, S_full, COMPUTE_FULL_RANGE, SAVE_FIGURES, SHOW_SEGMENTATION, PREFIX_OUTPUT_FILENAME, PREFIX_INPUT_FILENAME, FILTER_ZERO_EIGENVALUES, REUSE_ASSIGNMENTS, assignments, USE_SIMPLE_COLORS)
%RUN_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here

    % the following flag define what data should be displayed.
    % USE_CLUSTERING_CUE true => display segmentation
    % USE_CLUSTERING_CUE false && USE_W_VEC true => display affinities
    % USE_CLUSTERING_CUE false && USE_W_VEC => display eigenvectors
    %     USE_W_VEC = false;
    %     USE_CLUSTERING_CUE = true;
    
    % load dataset input file paths
    [BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET); 
    
    % prepare output directories, name prefixes.
    path = '';
    if RUN_MODE == 1
        path = make_segmentation_dir(DATASET, METHODNAME, PREFIX_OUTPUT_FILENAME );
    end
    
    pr = '';
    if isempty(PREFIX_INPUT_FILENAME) == 0
        pr = strcat(PREFIX_INPUT_FILENAME, '_');
    end
    
    %% load appropriate data
    [W, U_full, S_full, U_small, S_small] = extract_eigendecomp_data(BASE, DATASET, pr, W, U_full, S_full, THRESH, USE_EIGS, COMPUTE_EIGS, USE_CLUSER_EW_COUNT, FORCE_EW_COUNT, FILTER_ZERO_EIGENVALUES);
    
    %% display segmentation and its data.

    % load label vector indices mappings

    label_mappings = labelfile2mat(strcat(BASE, DATASET, '_' ,pr));
    
    [boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH, METHODNAME);

    frames = loadAllTrajectoryLabelFrames(DATASET, boundaries(1), boundaries(2), PREFIX_INPUT_FILENAME);
    
    range = frame_idx:frame_idx;
    if COMPUTE_FULL_RANGE
        range = boundaries(1):1:boundaries(2);
    end
    
    %% run spectral clustering
       % to help the user what values/index pairs can be displayed.
    show_usage_information(RUN_MODE, W, U_small);
    
    col_sel = SELECTED_ENTITY_IDX;
    if SELECT_AFFINITY_IDX
        t = figure;
        filename = imgs{frame_idx};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        [x, y] = ginput(1);
        close(t);

        frame = frames{frame_idx};
        row_ids = frame.ax;
        col_ids = frame.ay;

        distances = sum([row_ids-y,col_ids-x].^2, 2);
        num_el = 1;

        % find smallest num_el labels
        [~, AIdx] = sort(distances);
        smallestNIdx = AIdx(1:num_el);
        col_sel = frame.labels(smallestNIdx(1));
    end
    
    % generate the actual segmentation data and run the corresponding
    % visualizations.
    assignments = run_spectral_clustering(W, U_small, S_small, RUN_MODE, CLUSTER_CENTER_COUNT, frames, imgs, label_mappings, range, path, SAVE_FIGURES, SHOW_SEGMENTATION, col_sel, REUSE_ASSIGNMENTS, assignments, USE_SIMPLE_COLORS);
    
end