function display_segmentation( pixeltensor, ev, idi, idj, labels, idk, col_sel, display_clustering_labels, S_small, tt )
%DISPLAY_SEGMENTATION Summary of this function goes here
%   Detailed explanation goes here
    filename = strcat('../data/ldof/cars1/0',num2str(tt),'.ppm');
    img = imread(filename);
    I = mat2img(img(:,:,1),img(:,:,1),img(:,:,1));
    imshow(I);

    hold on
    for k=1:length(idk),
        label_idx = pixeltensor(idi(k), idj(k), 2);
        ax = pixeltensor(idi(k), idj(k), 3);
        ay = pixeltensor(idi(k), idj(k), 4);
        if display_clustering_labels
            labelcolor = labels(label_idx-1);
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
            evv = ev;
            evv = evv - min(ev(:));
            evv = evv ./ max(evv(:));
            arg = evv(label_idx-1);
            %evv = evv - min(evv(:));
            %evv = evv ./ max(evv(:));
            color_value = evc_to_color(arg);
        end

        %plot(ay, ax, 'Color', color_value);
        plot(ay,ax,'Color',color_value,'Marker','*');
        hold on

    end
    [idii,idjj,~] = find(pixeltensor(:,:,2) == col_sel-1);
    ax = pixeltensor(idii(1), idjj(1), 3);
    ay = pixeltensor(idii(1), idjj(1), 4);
    plot(ay,ax,'Color',[1,0,0],'Marker','O');
    hold on
    colorbar;
        title(strcat('eigenwert ', num2str(S_small(col_sel,col_sel))) )
end

