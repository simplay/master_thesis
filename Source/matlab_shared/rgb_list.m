function rgb_values = rgb_list(cluser_count, useSimpleColoring)
%RGB_LIST generate a list of rgb colors
%   Detailed explanation goes here

    if nargin > 1 && useSimpleColoring
        rgb_values = [
            1 0 0;
            0 1 0;
            0 0 1;
            1 1 0;
            0 1 1;
            1 0 1;
            0.5 0.5 0;
            0.5 0 0.5;
            0 0.5 0.5;
            0 0.5 1;
            1 0.5 0;
            0 1 0.5;
            1 0 0.5;
            0.5 1 0;
            0.5 0 1;
            0.5 0.5 1;
            0.5 1 0.5;
            1 0.5 0.5;
            1 0.5 0.25;
            1 0.25 0.5;
        ];
    else
        for base=3:6
            if (cluser_count < base^3 - 1)
                rgb_values = zeros(base^3, 3);
                for k=0:(base^3-1)
                    rgb_values(k+1, :) = asNaryRepresentation(base, k);
                end
                break;
            end
        end
        rgb_values = rgb_values ./(base-1);
        rgb_values = rgb_values(2:end, :);
        idxperm = randperm(length(rgb_values)-1);
        rgb_values = rgb_values(idxperm, :);
    end
end

