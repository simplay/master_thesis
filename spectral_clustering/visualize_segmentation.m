function visualize_segmentation(frames, imgs, label_assignments, label_mappings, img_index);
%VISUALIZE_SEGMENT Summary of this function goes here
%   Detailed explanation goes here
    filename = imgs{img_index};
    img = imread(filename);
    I = mat2img(img(:,:,1));
    imshow(I);
    hold on
    
    frame = frames{img_index};
    
    for idx=1:length(frame.ax)
        f_label = frame.labels(idx);
        w_idx = label_idx_to_W_lookup_idx(f_label, label_mappings);
        if isempty(w_idx)
            continue;
        end
        tra_cluster_assignment = label_assignments(w_idx);
        color = get_cluster_color_of(tra_cluster_assignment);
        plot(frame.ay(idx), frame.ax(idx), 'Color', color, 'Marker', '*');
        hold on
    end

end

function color_value = get_cluster_color_of(assignment)
    if assignment == 1
        color_value = [1,0,0];
    elseif assignment == 2
        color_value = [0,1,0];
    elseif assignment == 3
        color_value = [0,0,1];
    elseif assignment == 4
        color_value = [1,0,1];
    elseif assignment == 5
        color_value = [1,1,0];
    elseif assignment == 6
        color_value = [0,1,1];
    elseif assignment == 7
        color_value = [0.5,0,0.5];
    elseif assignment == 8
        color_value = [0,0.5,0.5];
    elseif assignment == 9
        color_value = [0.5,0.5,0];            
    else
        color_value = [1,1,1];
    end
end

