function display_affinity_vec( pixeltensor, vector, row_inds, col_inds, img_index, col_sel, label_mappings)
%DISPLAY_AFFINITIES Summary of this function goes here
%   Detailed explanation goes here
    filename = strcat('../data/ldof/cars1/0',num2str(img_index),'.ppm');
    img = imread(filename);
    I = mat2img(img(:,:,1));
    imshow(I);
    hold on
    
    vec = vector;
    vec = vec - min(vec(:));
    vec = vec ./ max(vec(:));
    for k=1:length(row_inds)
        pixel_label = pixeltensor(row_inds(k), col_inds(k), 2);
        ax = pixeltensor(row_inds(k), col_inds(k), 3);
        ay = pixeltensor(row_inds(k), col_inds(k), 4);
        % since the first label is 2 but the first lookup index is 1
        transformed_pixel_label = label_as_local(label_mappings, pixel_label); % pixel_label - 1;
        arg = vec(transformed_pixel_label);
        plot(ay,ax,'Color', evc_to_color(arg), 'Marker', '*');
        hold on

    end
    transformed_marker_label = label_as_local(label_mappings, col_sel);
    [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == transformed_marker_label);
    ax = pixeltensor(mark_row_idx, mark_col_idx, 3);
    ay = pixeltensor(mark_row_idx, mark_col_idx, 4);
    plot(ay,ax,'Color',[1,0,0],'Marker','O');
    hold on
    colorbar;

end

