function [ aggregated ] = aggregate_mat_cols( Mat, col_set )
%AGGREGATE_MAT_COLS Summary of this function goes here
%   Detailed explanation goes here

    [m,~] = size(Mat);
    aggregated = zeros(m,length(col_set));
    for k=1:length(col_set),
        aggregated(:,k) = Mat(:,k);
    end
end

