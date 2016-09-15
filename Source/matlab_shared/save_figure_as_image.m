function save_figure_as_image(fig, fpname, m, n)
%SAVE_FIGURE_AS_IMAGE Summary of this function goes here
%   Usage example
%   save a given figure as a (480 x 640) 'foo.png' image in the current
%   directory:
%   fig = figure
%   ...
%   imshow(an_img)
%   ...
%   save_figure_as_image(fig, './foo', 480, 640)
%
    set(fig,'paperunits','inches','papersize',[n, m],'paperposition',[0 0 n m]); %do
    set(gca,'position',[0 0 1 1], 'units','normalized')
    print(fig, fpname,'-dpng',['-r',num2str(1)],'-opengl')
end

