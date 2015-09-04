function [ avg_eigenvalue, corners ] = computeCorners( img )
%COMPUTECORNERS computes the smallest eigenvalue of a given image by relying on the harris corner
%detector feature descriptor.
% d_xx = d_x * d_x and dx is computed via central differences on every
% channel. sum up channels for a pixel.
% L = [d_xx d_xy, d_xy d_yy]
% compute eigenvalues of L for every pixel.
% trick to find this eigenvalue: 
% let a := d_xx; b := d_xy
% ux^2 + vx + t = 0 => x_1,2 = (-v +- sqrt(v^2 - 4ut) ) / 2u
% det(L-Id*x) = 0 <=> (a-x)^2 * (c-x)^2 - b^2
% <=> x^2 - x(a+c) + (ac-b^2) = 0
% thus solve by setting u = 1; v = -(a+c); t = ac - b^2
%
% @param img n x m x 3 color image
% @return avg_eigenvalue avg value of all smallest eigenvalues
% @return corners smallest eigenvalue for every pixel in the image

%   Detailed explanation goes here
        eps = 0.0;

        [D1x, D1y] = compute_smoothed_centraldiff(img(:,:,1));
        [D2x, D2y] = compute_smoothed_centraldiff(img(:,:,2));
        [D3x, D3y] = compute_smoothed_centraldiff(img(:,:,3));
        
        DX = mat2img(D1x,D2x,D3x);
        DY = mat2img(D1y,D2y,D3y);
        
        dxx = sum(DX.^2,3);
        dyy = sum(DY.^2,3);
        dxy = sum(DX.*DY,3);

        sigma = 3;
        g = fspecial('gaussian', sigma);
        dxx = imfilter(dxx, g);
        dyy = imfilter(dyy, g);
        dxy = imfilter(dxy, g);

        
        
        base = 0.5*(dxx+dyy);
        discrim = base.^2 + dxy.^2 - dxx.*dyy;
        
        % avoid taking square roots of negative numbers
        discrim = (discrim >= 0).*discrim;
        corners = base - sqrt(discrim);
        corners = corners .* (discrim >= 0);
        non_zeros = length(find(corners > eps));
        avg_eigenvalue = sum(corners(:))/non_zeros;
        
end

function [ Ix, Iy ] = compute_smoothed_centraldiff(mat)
        dx = [-1 0 1; -1 0 1; -1 0 1];
        dy = dx';
        Ix = conv2(mat, dx, 'same');
        Iy = conv2(mat, dy, 'same');
end


