function [ candidate_pixels ] = find_tracking_candidates( img1, sigma, eps )
%FIND_TRACKING_CANDIDATES Summary of this function goes here
%   Detailed explanation goes here
    
    % Fetch image resolution
    [m,n, ~] = size(img1);    

    
    g = fspecial('gaussian', 1);
    
    
    dx = [-1 0 1; -1 0 1; -1 0 1];
    dy = dx';
    
    Ix = conv2(img1(:,:,1), dx, 'same');
    Iy = conv2(img1(:,:,1), dy, 'same');
    D1x = Ix; D1y = Iy;
    
    Ix = conv2(img1(:,:,2), dx, 'same');
    Iy = conv2(img1(:,:,2), dy, 'same');
    D2x = Ix; D2y = Iy;
        
    Ix = conv2(img1(:,:,3), dx, 'same');
    Iy = conv2(img1(:,:,3), dy, 'same');
    D3x = Ix; D3y = Iy;
    
    DX = mat2img(D1x,D2x,D3x);
    DY = mat2img(D1y,D2y,D3y);
    
    thresh = avg_eigenvalue(DX,DY);
    [candidate_pixels, ~, ~] = harris3d(img1, 1, thresh, 1);


end

