function label_assignments = run_spectral_clustering(W, U_small, S_small, RUN_MODE, CLUSTER_CENTER_COUNT, frames, imgs, label_mappings, range, path, SAVE_FIGURES, SHOW_SEGMENTATION, col_sel, REUSE_ASSIGNMENTS, label_assignments)
%RUN_SPECTRAL_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here
    rgb_values = rgb_list(CLUSTER_CENTER_COUNT);
    
    if RUN_MODE == 1
        if REUSE_ASSIGNMENTS == 0
            [label_assignments] = spectral_custering( U_small, CLUSTER_CENTER_COUNT, 200, true);  
            write_label_clustering_file(label_assignments, label_mappings, path);
        end
    end
    
    % For each frame under consideration, perform appropriate segmentation
    for img_index = range
       
        if SAVE_FIGURES
            fig = figure('name', strcat('Frame ', num2str(img_index)));
        end
        
        disp(['Processing frame ',num2str(img_index), '...']);
        
        fpname = strcat(path, 'seg_f_', num2str(img_index), '.png');

        if RUN_MODE == 1
            visualize_segmentation(frames, imgs, label_assignments, label_mappings, img_index, rgb_values);
            
            if SAVE_FIGURES
                save_segmentation(fig, fpname, imgs);
            end
            if SHOW_SEGMENTATION == 0
                close(fig);
            end
        elseif RUN_MODE == 2
            visualize_affinities(W, col_sel, frames, imgs, label_mappings, img_index);
        elseif RUN_MODE == 3
            eigenvalue = S_small(col_sel);
            visualize_eigenvector(eigenvalue, U_small, col_sel, frames, imgs, label_mappings, img_index);
        end
    end

end