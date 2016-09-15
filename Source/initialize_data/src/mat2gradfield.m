function [ dx, dy ] = mat2gradfield( matrix )
%MAT2GRADFIELD compute the gradient field along each direction dx, dy of a
%matrix.
%   @param matrix is a [m x n] matrix
%   @return dx a [(m-1)++zero-Row x n] matrix
%   @return dy a [m x (n-1)++zero-Column] matrix
%   Zeros row/column corresponds to the boundaries:
%   generally, we compute (ds)_k = s(k+1) - s(k) forall k=1:N-1 for s in x,y.
%   Notice that k=N is excluded, since s(N+1) is not defined. Thus, in
%   order to cope with this dimensionality issues

    dx = matrix(:, 2:end) - matrix(:, 1:end-1);
    dx = [matrix(:, 1)*0,dx];
    dy = matrix(2:end,:) - matrix(1:end-1,:);
    dy = [matrix(end,:)*0; dy];
    
end