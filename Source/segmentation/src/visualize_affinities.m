function visualize_affinities(W, label_idx, frames, imgs, label_mappings, img_index);
%VISUALIZE_SEGMENT Summary of this function goes here
%   Detailed explanation goes here
    filename = imgs{img_index};
    img = imread(filename);
    I = mat2img(img(:,:,1));
    imshow(I);
    hold on
    
    frame = frames{img_index};
    
    w_idx = label_idx_to_W_lookup_idx(label_idx, label_mappings);
    w_values = W(:,w_idx);
    vec = w_values;
    vec = vec - min(vec(:));
    vec = vec ./ max(vec(:));
    
    for k=1:length(w_values),
        l_idx = label_mappings(k);
        idx = find(frame.labels == l_idx);
        plot(frame.ay(idx), frame.ax(idx), 'Color', evc_to_color(vec(k)), 'Marker', '*');
    end
    
    row_idx = find(frame.labels == label_idx);
    disp([' => Selected label: ', num2str(label_idx)]);
    plot(frame.ay(row_idx), frame.ax(row_idx),'Color',[1,0,0],'Marker','O');
    hold on
    colorbar;
end