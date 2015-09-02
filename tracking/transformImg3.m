function [transformed_img] = transformImg3(img, transformation)
%TRANSFORMIMG3 transforms a given image by a provided transformation
%matrix.
%   @param img is a m x n x 3 image.
%   @param transformation is a 3 x p transformation matrix.
%   @return transformed_img is a m x n x 3 transformation image.
    
    % get dimension (m, n, 3) of given image
    dimensions = size(img);
    m = dimensions(1);
    n = dimensions(2);
    
    % shift dims from [m x n x 3] to [3 * m x n]
    shifted_img = (shiftdim(img, 2));
    
    % restructure [3 x m x n] tensor to a [3, m*n] matrix
    % remember that elements are processed column-wise from tensor.
    tensorAsMatrix = reshape(shifted_img, 3, m*n);
    
    % apply transformation to restructured tensor.
    % results in a [p x m*n] dimensional matrix
    transformed_matrix = transformation*tensorAsMatrix;
    
    % restructure [3 x m*n] matrix to a [m x n x 3] tensor.
    % remember that elements are processed column-wise from tensor.
    % and not row-wise thus we have to apply a transpose operator.
    transformed_img = reshape(transformed_matrix', m, n, 3);
end

