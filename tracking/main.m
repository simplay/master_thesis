img1 = imread('data/ldof/cars1_01.ppm');
img1 =im2double(img1);
img2 = imread('data/ldof/cars1_02.ppm');
img2 =im2double(img2);

[D1x,D1y] = mat2gradfield(img1(:,:,1));
[D2x,D2y] = mat2gradfield(img1(:,:,2));
[D3x,D3y] = mat2gradfield(img1(:,:,3));

[m,n, ~] = size(img1);
ew2_mat = zeros(m,n); % matrix containing all 2nd eigenvalues for every pixel
eps = 0.5; % threshold value
ew2_sum = 0; % sum of all 2nd eigenvalues
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
        t = eig(L);
        ew2_sum = ew2_sum + t(2);
        ew2_mat(k,l) = t(2);
    end
end
ew2_avg = ew2_sum / (m*n);

mask = ew2_mat > eps*ew2_avg;
tensor_mask = mat2img(mask, mask, mask);
i1 = img1.*tensor_mask;


disp('done')