function [W, U_small, S_small, U_full, S_full, label_assignments] = run_min_cut(DATASET, METHODNAME, RUN_MODE, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, W, SELECTED_ENTITY_IDX, frame_idx, USE_CLUSER_EW_COUNT, num_of_iters, FORCE_EW_COUNT, U_full, S_full, COMPUTE_FULL_RANGE, SAVE_FIGURES, SHOW_SEGMENTATION, PREFIX_OUTPUT_FILENAME, PREFIX_INPUT_FILENAME, NU, FILTER_ZERO_EIGENVALUES, REUSE_LABEL_ASSIGNMENT, label_assignments, USE_SIMPLE_COLORS, SHOULD_EXCLUDE_U_IDXS, EXCLUDED_U_IDXS)
%RUN_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here

    % the following flag define what data should be displayed.
    % USE_CLUSTERING_CUE true => display segmentation
    % USE_CLUSTERING_CUE false && USE_W_VEC true => display affinities
    % USE_CLUSTERING_CUE false && USE_W_VEC => display eigenvectors
    %     USE_W_VEC = false;
    %     USE_CLUSTERING_CUE = true;
    
    % load dataset input file paths
    pr = '';
    if isempty(PREFIX_INPUT_FILENAME) == 0
        pr = strcat(PREFIX_INPUT_FILENAME, '_');
    end
    
    
    [BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET); 
    
    % prepare output directories, name prefixes.
    path = '';
    if RUN_MODE == 1
        path = make_segmentation_dir(DATASET, METHODNAME, PREFIX_OUTPUT_FILENAME );
    end

    %% load appropriate data
    [W, U_full, S_full, U_small, S_small] = extract_eigendecomp_data(BASE, DATASET, pr, W, U_full, S_full, THRESH, USE_EIGS, COMPUTE_EIGS, USE_CLUSER_EW_COUNT, FORCE_EW_COUNT, FILTER_ZERO_EIGENVALUES);
    if SHOULD_EXCLUDE_U_IDXS
        selected_u_idx = ones(1, size(U_small, 2));
        orig_u_sel_idxs = 1:size(U_small, 2);
        for k=1:length(EXCLUDED_U_IDXS)
            excluded_idx = EXCLUDED_U_IDXS(k);
            selected_u_idx(excluded_idx) = 0;
        end
        orig_u_sel_idxs = orig_u_sel_idxs(logical(selected_u_idx));
        U_small = aggregate_mat_cols(U_small, orig_u_sel_idxs);
        S_small = S_small(logical(selected_u_idx));
    end
    
    
    %% display segmentation and its data.

    % load label vector indices mappings
    % TODO export this method to own function

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
    
    img_index = SELECTED_ENTITY_IDX;

    nn_fpath = strcat('../output/similarities/',DATASET, '_', pr,'spnn.txt');
    disp(['Loading spnn file: ' nn_fpath]);
    
    if num_of_iters > 0
        spnn_indices = extract_spatial_neighbors(nn_fpath, label_mappings);
    end
    % initial assignments
    if REUSE_LABEL_ASSIGNMENT == 0
        label_assignments = zeros(length(W), 1);
    end
    rgb_values = rgb_list(CLUSTER_CENTER_COUNT, USE_SIMPLE_COLORS);
    %%
    N = length(W);
    for K=CLUSTER_CENTER_COUNT:CLUSTER_CENTER_COUNT%4%min(20,2*m)
    
    % kmeans(X, K) returns the K cluster centroid locations in the K-by-P matrix centroids.
        
        % define initial cluster assignment
        % label_assignments
        if REUSE_LABEL_ASSIGNMENT == 0
            ks = 1:K;
            fK = (ks-1).*(N./K);
            tK = ks.*(N./K);
            for idx=1:length(fK)
                [~,tp,~] = find(fK(idx) <= 1:N & 1:N <= tK(idx));
                label_assignments(tp) = idx;
            end
        end
        
        % repeat until convergence, i.e. error is small
        for t=1:num_of_iters,  
            [ centroids ] = find_cluster_centers(label_assignments, U_small);
            [label_assignments, energy] = min_multi_graph_cut( U_small, S_small, label_assignments, centroids, K, spnn_indices, NU);
            disp(['Iteration ', num2str(t),' Remaining energy: ',num2str(energy)]);
            
            %visualize_segmentation(frames, imgs, label_assignments, label_mappings, img_index, rgb_values);
        end
        write_label_clustering_file(label_assignments, label_mappings, path);
        for img_index = range
            
            if SAVE_FIGURES
                fig = figure('name', strcat('Frame ', num2str(img_index)));
            end  
            disp(['Processing frame ',num2str(img_index), '...']);
      
            fpname = strcat(path, 'seg_f_', num2str(img_index), '.jpg');
        
            visualize_segmentation(frames, imgs, label_assignments, label_mappings, img_index, rgb_values);
            
            if SAVE_FIGURES
                save_segmentation(fig, fpname, imgs);
            end
            if SHOW_SEGMENTATION == 0
                close(fig);
            end
        end
        
        % compute new best label assignents via graph cut using gcmex
    end
    
end