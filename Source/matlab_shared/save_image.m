function save_image(img, fpname)
%SAVE_SEGMENTATION saves a figure with its exact resolution at a given path
    fig = figure;
    imshow(img);
    [m, n, ~] = size(img);
    set(fig,'paperunits','inches','papersize',[n, m],'paperposition',[0 0 n m]); %do
    set(gca,'position',[0 0 1 1], 'units','normalized')
    print(fig, fpname,'-dpng',['-r',num2str(1)],'-opengl')
    close(fig);
end

