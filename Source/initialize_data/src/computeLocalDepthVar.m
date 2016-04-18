
function [variances] = computeLocalDepthVar(flowfield, sig_s, sig_r, valid_regions)
%COMPUTELOCALFLOWVAR Summary of this function goes here
%   Detailed explanation goes here

    [sigmaSq, ~, ~] = localVariance( flowfield, sig_s, sig_r, true, valid_regions);

    var_x = sigmaSq(:,:,1);   
    var_x(isnan(var_x)) = 0;

    variances = var_x;
end

function [sigma, mu, windowLength] = localVariance( img, sigma_s, sigma_r, remove_center, valid_regions)
%BFILTIMG2 apply a bilateral filter o a m x n x 2 tensor.
%   @param img flow field, m x n x 2 tensor
%   @param sigma_s special std [FLOAT], unit in pixel (pixel neighborhood)
%   @param sigma_r response std [FLOAT], unit in pixel (flow tolerance)
%   @param remove_center should we zerofy the center value, 
%       addresses peak-issues.
%
%   @return out bilateral filtered flow field
%   @return weights_u bilateral filter weights for u-direction
%   @return weights_v bilateral filter weights for v-direction
%   @return wl used window length for storing filter weights.
    
    disp(['using sigma_s=', num2str(sigma_s)]);
    disp(['using sigma_r=', num2str(sigma_r)]);
    
    h = waitbar(0, 'Computing local Variance...');
    set(h, 'Name', 'local variance Progress Bar');
    
    w = ceil(1.5*sigma_s);
    windowLength = 2*w + 1;
    [m, n, ~] = size(img);
    out1 = zeros(m,n);
    mu_x = zeros(m,n);

    tic;
    parfor i = 1:m,
        for j = 1:n,   
            [rowIndices, columnIndices] = getRanges(i, j, m, n, windowLength);
            neighboordhoodValues1 = img(rowIndices, columnIndices, 1);         
            valid_region_ij = valid_regions(rowIndices, columnIndices);
                
            DeltaNValues1 = (neighboordhoodValues1-img(i,j, 1));
            DeltaNValues1 = (DeltaNValues1.^2) /(-2*sigma_r*sigma_r);
                   
            deltaNIdx = getScaledIdxDistanceMat2(rowIndices, columnIndices, ...
                                                [i,j], -2*sigma_s^2);    
                                            
            EV1 = valid_region_ij.*exp(DeltaNValues1+deltaNIdx);
            
            % set ourself to zero: fix
            if remove_center
                idx = find(rowIndices == i);
                idj = find(columnIndices == j);
                EV1(idx,idj) = 0;
            end            

            % if sum == 0, then assign -1 (define an assert for that case)
            mu_x_ij = (EV1(:)'*neighboordhoodValues1(:))/sum(EV1(:));
            mu_x(i,j) = mu_x_ij;
         
            % compute bilateral covariance cov(x,y)
            ssc_x = (neighboordhoodValues1-mu_x_ij).^2;
            out1(i,j) = (EV1(:)'*ssc_x(:))/sum(EV1(:));
        end
        waitbar(i/m)
    end
    close(h);
    toc
    
    sigma = out1;
    mu = mu_x;  
end


