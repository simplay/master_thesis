function [ ew2_avg ] = avg_eigenvalue( img_x, img_y )
%AVG_EIGENVALUE Summary of this function goes here
%   Detailed explanation goes here
    % Fetch image resolution
    [m,n, ~] = size(img_x);    
    D1x = img_x(:,:,1); D1y = img_y(:,:,1);
    D2x = img_x(:,:,2); D2y = img_y(:,:,2);
    D3x = img_x(:,:,3); D3y = img_y(:,:,3);
    
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

end

