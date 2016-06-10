function mask = sparse_segmentation(frames, imgs, label_assignments, label_mappings, img_index, label)
%VISUALIZE_SEGMENT Summary of this function goes here
%   Detailed explanation goes here
    filename = imgs{img_index};
    img = imread(filename);
    [m, n, ~] = size(img);
    mask = zeros(m, n);
    frame = frames{img_index};
    
    color_step = 255;
    
    for idx=1:length(label_assignments)
        
        if label_assignments(idx) ~= label
            continue;
        end
        
        lm_idx = label_mappings(idx);
        
        fl_idx = find(frame.labels == lm_idx);
        if (isempty(fl_idx))
            continue;
        end
        


        color_value = label_assignments(idx) * color_step;
        mask(frame.ax(fl_idx), frame.ay(fl_idx)) = color_value;
   
    end
    
end

