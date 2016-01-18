function [ A_normalized ] = normalize(A)
%NORMALIZE normalizes the values of a given vector, matrix A to
%   the range [0,1] where the originally largest value corresponds to 1 
%   and the smallest value maps to 0. All other values are tranformed
%   lineraly accordingly.
    A_normalized = A - min(A(:));
    A_normalized = A_normalized ./ max(A_normalized(:));
end

