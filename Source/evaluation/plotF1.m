function plotF1(fs, range, plotLabels)
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

    [count, n] = size(fs);
    as = [];
    for c=1:count,
        selectedColor = colors(c, :);
        f1 = fs(c, :);
        plot(range, f1,'Color', selectedColor, 'MarkerSize', 15, 'Marker', '.')
        hold on
        set(gca,'FontSize',15);
        a = plot(range, f1, 'Color', selectedColor);
        as = [as, a];
    end
    
    xlabel('Cluster Count', 'fontsize', 20)
    ylabel('F1 Score', 'fontsize', 20)
    grid on
    if ~skip
        leg = legend(as, plotLabels);
        set(leg,'location','northwest', 'fontsize', 15);
    end
    
end