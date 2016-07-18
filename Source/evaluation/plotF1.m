% @example
% f1 = [14.02, 28.47, 45.04, 51.59] / 100
% f2 = [19.5, 29.79, 44.63, 51.4] / 100
% f3 = [11.48, 30.89, 43.88, 40.69] / 100
% f4 = [21.97, 40.63, 40.12, 52.58] / 100
% f = [f1; f2; f3; f4]
% plotF1(f, [5, 10, 15, 20], {'pd sc', 'pd mc', 'ped sc', 'ped mc'})
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

    [count, ~] = size(fs);
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
    xlabel('Iterations', 'fontsize', 20)
    ylabel('F1 Score', 'fontsize', 20)
    grid on
    if ~skip
        leg = legend(as, plotLabels);
        set(leg,'location','northwest', 'fontsize', 15);
    end
    
end