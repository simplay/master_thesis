function [W, U_small, S_small] = run_clustering( DATASET, STEPSIZE_DATA, CLUSTER_CENTER_COUNT, THRESH, COMPUTE_EIGS, USE_EIGS, USE_W_VEC, USE_CLUSTERING_CUE, W, U_small, S_small, SELECTED_ENTITY_IDX, USE_T)
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

    DATASETNAME = DATASET;
    METHODNAME = 'ldof'; %other,ldof
    DATASETP = strcat(DATASETNAME,'/');
    BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASETP);

    %% load appropriate data
    if COMPUTE_EIGS
        fname = strcat(BASE,DATASET,'_sim.dat');
        W = load(fname);
        WW = W + ones(size(W))*THRESH;
        d_a = sum(WW,2);
        D = diag(d_a);
        D12 = diag(sqrt(1./d_a));
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
        [aa,~,~] = find(d < 0.1);
        UU = U_small;

    %     if USE_SPECTRAL_GAP
    %         deltas = S_small(2:end)-S_small(1:end-1);
    %         dbm = deltas <= mean(deltas); % deltas below mean
    %         zero_idxs = find(dbm == 0);
    %         use_till = zero_idxs(1)-1;
    %         [aa, ~] = find(aa <= use_till);
    %     end

        U_small = aggregate_mat_cols(U_small, aa);
        S_small = d(aa);
        


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


    col_sel = SELECTED_ENTITY_IDX;
    % load W in case it is needed.
    if ~exist('W','var') && USE_W_VEC
        W = load('../output/similarities/cars1_sim.dat');
    end

    % display data
    for img_index = 1:1
        figure

        pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(img_index),'.mat'));
        pixeltensor = pixeltensor.tracked_pixels;
        [row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);

        if USE_CLUSTERING_CUE    
            [label_assignments] = spectral_custering( U_small, CLUSTER_CENTER_COUNT);
            display_clustering(pixeltensor, label_assignments, row_ids, col_ids, img_index, label_mappings, imgs);
            write_label_clustering_file(label_assignments, label_mappings, img_index, DATASET);
        else
            displayed_vector = extract_vector( U_small, W, col_sel, USE_W_VEC, label_mappings);
            if USE_W_VEC
                display_affinity_vec(pixeltensor, displayed_vector, row_ids, col_ids, img_index, col_sel, label_mappings, imgs);
            else
                eigenvalue = S_small(col_sel);
                display_eigenvectors(pixeltensor, displayed_vector, row_ids, col_ids, img_index, eigenvalue, label_mappings, imgs);
            end
        end
    end

end

