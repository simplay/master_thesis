% p = [22.08, 26.41, 26.95, 29.54, 20.88, 56.53, 48.55, 82.46, 95.97]
% r = [33.21, 33.21, 33.08, 32.96, 32.96, 49.51, 65.27, 93.58, 86.59]
% f = [26.53, 29.42, 29.70, 31.16, 25.57, 52.79, 55.68, 87.67 , 91.04]
% plot(2:10, f, 'r.')
function evalPlots(recalls, precisions, plotLabels)
    addpath('../matlab_shared');
    
    skip = false;
    if nargin < 3
        skip = true;
    end
    
    colors = [
        1 0 0;
        0 1 0;
        0 0 1;
        1 0 1;
        0.5 1 1;
        0.5 0.5 0.5;
    ];
    
    %colors = rgb_list(20);
    figure('name', 'F-score isobars')
    X = meshgrid(0:0.01:1);
    Y = X';
    Z = (X.*Y)./(X+Y);
    
    contourf(X,Y,Z)
    colormap(white)
    hold on
    
    [count, n] = size(recalls);
    as = [];
    for c=1:count
        selectedColor = colors(c, :);
        for k=1:n
            recall = recalls(c, k);
            precision = precisions(c, k);
            hold on
            plot(recall, precision, 'Color', selectedColor, 'MarkerSize', 15, 'Marker', '.')
            hold on
            set(gca,'FontSize',15);
        end
        hold on
        a = plot(recalls(c, :), precisions(c, :), 'Color', selectedColor);
        hold on
        as = [as, a];
        
    end
    
    if ~skip
        leg = legend(as, plotLabels);
        set(leg,'location','northwest', 'fontsize', 15);
    end

    xlabel('Recall', 'fontsize', 20)
    ylabel('Precision', 'fontsize', 20)
end