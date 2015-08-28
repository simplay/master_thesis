function [ candidate_pixels ] = find_tracking_candidates( img1, eps )
%FIND_TRACKING_CANDIDATES Summary of this function goes here
%   Detailed explanation goes here
    
    % Fetch image resolution
    [m,n, ~] = size(img1);    


    % compute image gradients for each color channel
    [D1x,D1y] = mat2gradfield(img1(:,:,1));
    [D2x,D2y] = mat2gradfield(img1(:,:,2));
    [D3x,D3y] = mat2gradfield(img1(:,:,3));

    % matrix containing all 2nd eigenvalues for every pixel
    ew2_mat = zeros(m,n); 
    
    % sum of all 2nd eigenvalues
    eigval_sum = 0; 
    
    % Determine average 2nd eigenvalue and all 2nd eigenvalues of Tensor
    % described in 
    % http://lmb.informatik.uni-freiburg.de/people/brox/pub/sundaram_eccv10.pdf
    % i.e. implementation of formular (3)
    for k=1:m,
        for l=1:n,
            d1x = D1x(k,l);
            d1y = D1y(k,l);
            L1 = [d1x.*d1x d1x.*d1y; d1y.*d1x,  d1y.*d1y];

            d2x = D2x(k,l);
            d2y = D2y(k,l);
            L2 = [d2x.*d2x d2x.*d2y; d2y.*d2x,  d2y.*d2y];

            d3x = D3x(k,l);
            d3y = D3y(k,l);
            L3 = [d3x.*d3x d3x.*d3y; d3y.*d3x,  d3y.*d3y];

            L = (L1+L2+L3);
            eigenvalues = eig(L);
            eigval_sum = eigval_sum + eigenvalues(2);
            ew2_mat(k,l) = eigenvalues(2);
        end
    end
    
    % avg of 2nd eigenvalue
    ew2_avg = eigval_sum / (m*n);

    mask = ew2_mat > eps*ew2_avg;
    tensor_mask = mat2img(mask, mask, mask);
    candidate_pixels = img1.*tensor_mask;

end

