function run_spectral_clustering( W, U_small, CLUSTER_CENTER_COUNT, frames, imgs, label_mappings, range, path, USE_CLUSTERING_CUE, SAVE_FIGURES, SHOW_SEGMENTATION, USE_W_VEC, col_sel)
%RUN_SPECTRAL_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here
    
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
                visualize_affinities(W, col_sel, frames, imgs, label_mappings, img_index);
            else
                eigenvalue = S_small(col_sel);
                visualize_eigenvector(eigenvalue, U_small, col_sel, frames, imgs, label_mappings, img_index);
            end
        end
    end

end