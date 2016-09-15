function save_segmentation( fig, fpname, imgs)
%SAVE_SEGMENTATION saves a figure with its exact resolution at a given path
    [m, n, ~] = size(imread(imgs{1}));
    set(fig,'paperunits','inches','papersize',[n, m],'paperposition',[0 0 n m]); %do
    set(gca,'position',[0 0 1 1], 'units','normalized')
    print(fig, fpname,'-dpng',['-r',num2str(1)],'-opengl')
end

