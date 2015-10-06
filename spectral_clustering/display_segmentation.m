function display_segmentation( pixeltensor, ev, idi, idj, labels, idk, col_sel, display_clustering_labels )
%DISPLAY_SEGMENTATION Summary of this function goes here
%   Detailed explanation goes here

    img = imread('../data/ldof/cars1/01.ppm');
    I = mat2img(img(:,:,1),img(:,:,1),img(:,:,1));
    imshow(I);

    hold on
    for k=1:length(idk),
        label_idx = pixeltensor(idi(k), idj(k), 2);
        ax = pixeltensor(idi(k), idj(k), 3);
        ay = pixeltensor(idi(k), idj(k), 4);
        if display_clustering_labels
            labelcolor = labels(label_idx);
            if labelcolor == 1
                color_value = [1,0,0];
            elseif labelcolor == 2
                color_value = [0,1,0];
            elseif labelcolor == 3
                color_value = [0,0,1];
            else
                color_value = [1,1,1];
            end
        else    
            color_value = evc_to_color(ev(label_idx));
        end

        %plot(ay, ax, 'Color', color_value);
        plot(ay,ax,'Color',color_value,'Marker','*');
        hold on

    end
    [idi,idj,~] = find(pixeltensor(:,:,2) == col_sel);
    ax = pixeltensor(idi(1), idj(1), 3);
    ay = pixeltensor(idi(1), idj(1), 4);
    plot(ay,ax,'Color',[1,0,0],'Marker','O');
    hold on
    colorbar;
end

