function rgb_values = rgb_list(cluser_count)
%RGB_LIST generate a list of rgb colors
%   Detailed explanation goes here
    
    if (cluser_count < 1 + 2^3)
        base = 2;
        rgb_values = zeros(base^3, 3);
        for k=0:(base^3-1)
            rgb_values(k+1, :) = asNaryRepresentation(base, k);
        end
    elseif (cluser_count < 1 + 3^3)
        base = 3;
        rgb_values = zeros(base^3, 3);
        for k=0:(base^3 - 1)
            rgb_values(k+1, :) = asNaryRepresentation(base, k);
        end
        
    end
    rgb_values = rgb_values ./(base-1);
end

