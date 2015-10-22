function [ output_args ] = display_eigenvectors( pixeltensor, vector, row_inds, col_inds, img_index, eigenvalue, label_mappings, imgs)
%DISPLAY_EIGENVECTORS Summary of this function goes here
%   Detailed explanation goes here
    filename = imgs{img_index};
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
        transformed_pixel_label = label_to_vector_index(label_mappings, pixel_label); % pixel_label - 1;
        arg = vec(transformed_pixel_label);
        if isempty(arg) == 0
            plot(ay,ax,'Color', evc_to_color(arg), 'Marker', '*');
            hold on
        end

    end
    colorbar;
    title(strcat('Corresponding Eigenvalue ', num2str(eigenvalue)));

end

