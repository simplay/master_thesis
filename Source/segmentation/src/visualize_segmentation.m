function visualize_segmentation(frames, imgs, label_assignments, label_mappings, img_index, rgb_values)
%VISUALIZE_SEGMENT Summary of this function goes here
%   Detailed explanation goes here
    filename = imgs{img_index};
    img = imread(filename);
    I = mat2img(img(:,:,1));
    imshow(I);
    hold on
    
    frame = frames{img_index};
    
    for idx=1:length(label_assignments)
        lm_idx = label_mappings(idx);
        fl_idx = find(frame.labels == lm_idx);
        if (isempty(fl_idx))
            continue;
        end
        assignment = label_assignments(idx);
        color = get_cluster_color_of(rgb_values, assignment);
        plot(frame.ay(fl_idx), frame.ax(fl_idx), 'Color', color, 'Marker', '*');
        hold on
    end
    
end

function color_value = get_cluster_color_of(rgb_values, assignment)
    t = rgb_values(assignment, :);
    color_value = [t(1), t(2), t(3)];
end

