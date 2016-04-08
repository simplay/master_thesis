function rgb_values = rgb_list(cluser_count)
%RGB_LIST generate a list of rgb colors
%   Detailed explanation goes here
    
    for base=2:5
        if (cluser_count < base^3)
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

