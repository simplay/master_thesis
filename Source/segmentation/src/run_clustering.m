function [W, U_small, S_small, WW, U_full, S_full] = run_clustering( DATASET, METHODNAME, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U_small, S_small, SELECTED_ENTITY_IDX, USE_T, frame_idx, WW, SHOULD_LOAD_W, PERFORM_AUTO_RESCALE, LAMBDA, USE_CLUSER_EW_COUNT, SELECT_AFFINITY_IDX, SHOW_LOCAL_VAR, VAR_IMG, FORCE_EW_COUNT, USE_SPECIAL_NAMING, USE_BF_BOUND, BOUNDARY, U_full, S_full, COMPUTE_FULL_RANGE, SAVE_FIGURES, SHOW_SEGMENTATION, PREFIX_OUTPUT_FILENAME, PREFIX_INPUT_FILENAME)
%RUN_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here

    % the following flag define what data should be displayed.
    % USE_CLUSTERING_CUE true => display segmentation
    % USE_CLUSTERING_CUE false && USE_W_VEC true => display affinities
    % USE_CLUSTERING_CUE false && USE_W_VEC => display eigenvectors
    %     USE_W_VEC = false;
    %     USE_CLUSTERING_CUE = true;
    
    % load dataset input file paths
    [BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET, USE_SPECIAL_NAMING); 
    
    % prepare output directories, name prefixes.
    [ path ] = make_segmentation_dir(DATASET, METHODNAME, PREFIX_OUTPUT_FILENAME );
    

    %% load appropriate data
    [U_full, S_full, U_small, S_small] = extract_eigendecomp_data(BASE, DATASET, ...
                                                                  U_full, S_full, ...
                                                                  COMPUTE_EIGS, SHOULD_LOAD_W, ... 
                                                                  USE_CLUSER_EW_COUNT, FORCE_EW_COUNT);
    
    %% display segmentation and its data.

    % load label vector indices mappings
    label_mappings = labelfile2mat(strcat(BASE, DATASET));
    [boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH, METHODNAME);

    % to help the user what values/index pairs can be displayed.
    show_usage_information(USE_W_VEC, USE_CLUSTERING_CUE, W, U_small);
    
    if SELECT_AFFINITY_IDX
        t = figure;
        filename = imgs{frame_idx};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        [x, y] = ginput(1);
        close(t);
    end
    col_sel = SELECTED_ENTITY_IDX;

    % display data
    if USE_BF_BOUND
        boundaries(1) = BOUNDARY(1);
        boundaries(2) = BOUNDARY(2);
    end

    frames = loadAllTrajectoryLabelFrames(DATASET, boundaries(1), boundaries(2), PREFIX_INPUT_FILENAME);
    
    range = frame_idx:frame_idx;
    if COMPUTE_FULL_RANGE
        range = boundaries(1):1:boundaries(2);
    end
    
    if USE_CLUSTERING_CUE
        [label_assignments] = spectral_custering( U_small, CLUSTER_CENTER_COUNT, 100, true);    
    end
    
    % For each frame under consideration, perform appropriate segmentation
    for img_index = range
       
        if SAVE_FIGURES
            fig = figure('name', strcat('Frame ', num2str(img_index)));
        end
        
        disp(['Processing frame ',num2str(img_index), '...']);
        
        fpname = strcat(path, 'seg_f_', num2str(img_index), '.jpg');

        if SELECT_AFFINITY_IDX
            frame = frames{img_index};
            row_ids = frame.ax;
            col_ids = frame.ay;

            distances = sum([row_ids-y,col_ids-x].^2, 2);
            num_el = 1;
            
            % find smallest num_el labels
            [~, AIdx] = sort(distances);
            smallestNIdx = AIdx(1:num_el);
            col_sel = frame.labels(smallestNIdx(1));
        end
        
        if USE_CLUSTERING_CUE

            visualize_segmentation(frames, imgs, label_assignments, label_mappings, img_index);
            write_label_clustering_file(label_assignments, label_mappings, img_index, path);
            if SAVE_FIGURES
                saveas(fig, fpname);
            end
            if SHOW_SEGMENTATION == 0
                close(fig);
            end
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
            else
                eigenvalue = S_small(col_sel);
                visualize_eigenvector(eigenvalue, U_small, col_sel, frames, imgs, label_mappings, img_index);
            end
        end
    end

end