function display_clustering(frames, pixeltensor, label_assignents, row_inds, col_inds, img_index, label_mappings, imgs)
%DISPLAY_CLUSTERING Summary of this function goes here
%   Detailed explanation goes here

    filename = imgs{img_index};
    img = imread(filename);
    I = mat2img(img(:,:,1));
    imshow(I);
    hold on
    for k=1:length(col_inds),
        pixel_label = pixeltensor(row_inds(k), col_inds(k), 2);
        ax = pixeltensor(row_inds(k), col_inds(k), 3);
        ay = pixeltensor(row_inds(k), col_inds(k), 4);
        % since the first label is 2 but the first lookup index is 1
        transformed_pixel_label = label_to_vector_index(label_mappings, pixel_label); % pixel_label - 1;
        assignment = label_assignents(transformed_pixel_label);
        % if assignment is not empty, it is empty iff point trajectory was
        % filtered duea to a too small length (in compute_similarity
        % script).
        if isempty(assignment) == 0
            plot(ay, ax, 'Color', get_cluster_color_of(assignment), 'Marker', '*');
            hold on
        end
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

