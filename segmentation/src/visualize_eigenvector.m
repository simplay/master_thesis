function visualize_eigenvector(eigenvalue, W, label_idx, frames, imgs, label_mappings, img_index);
%VISUALIZE_SEGMENT Summary of this function goes here
%   Detailed explanation goes here
    filename = imgs{img_index};
    img = imread(filename);
    I = mat2img(img(:,:,1));
    imshow(I);
    hold on
    
    frame = frames{img_index};
    
    w_values = W(:,label_idx);
    vec = w_values;
    vec = vec - min(vec(:));
    vec = vec ./ max(vec(:));
    
    for k=1:length(w_values),
        l_idx = label_mappings(k);
        idx = find(frame.labels == l_idx);
        plot(frame.ay(idx), frame.ax(idx), 'Color', evc_to_color(vec(k)), 'Marker', '*');
    end
    colorbar;
    title(strcat('Corresponding Eigenvalue ', num2str(eigenvalue)));

end