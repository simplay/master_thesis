function [ aggregated ] = aggregate_mat_cols( Mat, col_set )
%AGGREGATE_MAT_COLS Collect all columns from a given matrix that have a col
%index included in a given index list.
%   @param Mat a n x m matrix
%   @param col_set a k x 1 vector containing all column indices.
%       we want to select in Mat.
 
    [m,~] = size(Mat);
    aggregated = zeros(m,length(col_set));
    for k=1:length(col_set),
        aggregated(:,k) = Mat(:,k);
    end
end

