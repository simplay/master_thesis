function [ other, pm ] = generate_tracking_vectors( tp1_x_candidates, tp1_y_candidates)
%GENERATE_TRACKING_VECTORS Summary of this function goes here
%   Detailed explanation goes here

[m,n] = size(tp1_x_candidates);
other = zeros(m,n);
pm = zeros(m,n);
counter = 0;
for k=1:m,
    for l=1:n,
        idx2 = tp1_x_candidates(k,l);
        idy2 = tp1_y_candidates(k,l);
        if idx2 > 0 && idy2 > 0 && idx2<=m && idy2 <= n
            counter = counter + 1;
            other(idx2, idy2) = 1;
            pm(k, l) = 1;
        end
    end
end

end

