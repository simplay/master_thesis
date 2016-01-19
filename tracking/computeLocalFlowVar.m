function [variances] = computeLocalFlowVar(flowfield, method, combination_method, sig_s, sig_r, valid_regions)
%COMPUTELOCALFLOWVAR Summary of this function goes here
%   Detailed explanation goes here
    x = flowfield(:,:,1);
    y = flowfield(:,:,2);
    if method > 0
        [ff, w_u, w_v, wl] = bfiltmat2(flowfield, sig_s, sig_r, false);

        % bilarteral filtered flow field directions
        % act as 
        xx = ff(:,:,1);
        yy = ff(:,:,2);
        e_x = xx;
        e_y = yy;

    end
    
    if method == 0
        [sigmaSq, mu, cov_xy, wl] = localVariance( flowfield, sig_s, sig_r, true, valid_regions);
        var_x = sigmaSq(:,:,1);
        var_y = sigmaSq(:,:,2);
        if combination_method == 1
            variances = (var_x + var_y)/2;
        else
            cov_xy = cov_xy.*(cov_xy > 0);
            variances = var_x + var_y + 2*cov_xy;
        end
           
    elseif method == 1
        [m, n, ~] = size(flowfield);
        var_x = zeros(m,n);
        var_y = zeros(m,n);
        c_idx = 1;
        for i=1:m-wl,
            for j=1:n-wl,
                mu_x_ij = e_x(i,j);
                mu_y_ij = e_y(i,j);
                
                w_x_ij = w_v(:,:,c_idx);
                w_y_ij = w_u(:,:,c_idx);
                
                sum_w_x = sum(w_x_ij(:));
                sum_w_y = sum(w_y_ij(:));
                
                tmp_x = 0;
                tmp_y = 0;
                for w_i=1:wl,
                    for w_j=1:wl
                        idx = i + w_i - 1;
                        idy = j + w_j - 1;
                        
                        % squared shifted centeral value
                        ssc_x = (x(idx, idy)-mu_x_ij)^2;
                        ssc_y = (y(idx, idy)-mu_y_ij)^2;
                        
                        tmp_x = tmp_x + (w_x_ij(w_i, w_j)*ssc_x);
                        tmp_y = tmp_y + (w_y_ij(w_i, w_j)*ssc_y);
                     
                        
                    end
                end
                c_idx = c_idx + 1;
                var_x(i,j) = tmp_x/sum_w_x;
                var_y(i,j) = tmp_y/sum_w_y;
            end
        end
        variances = (var_x + var_y)/2;    
    else
        t = 12;
        kernel = ones(t,t)/(t*t);

        % var(x+y) = var(x) + var(y) + 2*cov(x,y)
        e_x = xx; %conv2(x, kernel, 'same') / (t*t);
        e_y = yy; %conv2(y, kernel, 'same') / (t*t);

        % var(x) = E((x-e(x))^2)
        var_x = conv2((x-e_x).^2, kernel, 'same');
        var_y = conv2((y-e_y).^2, kernel, 'same');

        % cov(x,y) = E((x-e(x))*(y-e(y)))
        cov_xy = conv2((x-e_x).*(y-e_y), kernel, 'same');

        % var(x+y) = var(x) + var(y) + 2*cov(x,y)
        variances = var_x + var_y + 2*cov_xy;
    end
end

function [sigma, mu, cov_xy, windowLength] = localVariance( img, sigma_s, sigma_r, remove_center, valid_regions)
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
    out2 = zeros(m,n);
    mu_x = zeros(m,n);
    mu_y = zeros(m,n);
    cov_xy = zeros(m,n);
    tic;
    parfor i = 1:m,
        for j = 1:n,

            
            [rowIndices, columnIndices] = getRanges(i, j, m, n, windowLength);
            
            neighboordhoodValues1 = img(rowIndices, columnIndices, 1);
            
            valid_region_ij = valid_regions(rowIndices,columnIndices);
            
            
            DeltaNValues1 = (neighboordhoodValues1-img(i,j, 1));
            DeltaNValues1 = (DeltaNValues1.^2) /(-2*sigma_r*sigma_r);
            
            neighboordhoodValues2 = img(rowIndices, columnIndices, 2);
            DeltaNValues2 = (neighboordhoodValues2-img(i,j, 2));
            DeltaNValues2 = (DeltaNValues2.^2) /(-2*sigma_r*sigma_r);
            
            
            
            deltaNIdx = getScaledIdxDistanceMat2(rowIndices, columnIndices, ...
                                                [i,j], -2*sigma_s^2);
            
                    
                                            % unterscheide filter weights
                                            % nach dimension
                       %  EV1 = exp(DeltaNValues1+deltaNIdx);
            %EV2 = exp(DeltaNValues2+deltaNIdx);
                                           
 
            
            EV1 = valid_region_ij.*exp(DeltaNValues1+deltaNIdx+DeltaNValues2);
            EV2 = valid_region_ij.*exp(DeltaNValues2+deltaNIdx+DeltaNValues1);
            
            % set ourself to zero: fix
            if remove_center
                idx = find(rowIndices == i);
                idj = find(columnIndices == j);
                EV1(idx,idj) = 0;
                EV2(idx,idj) = 0;
            end
            

            
            mu_x_ij = (EV1(:)'*neighboordhoodValues1(:))/sum(EV1(:));
            mu_y_ij = (EV2(:)'*neighboordhoodValues2(:))/sum(EV2(:));
            
            mu_x(i,j) = mu_x_ij;
            mu_y(i,j) = mu_y_ij;
            
            
            % compute bilateral covariance cov(x,y)
            c_x = neighboordhoodValues1-mu_x_ij;
            c_y = neighboordhoodValues2-mu_y_ij;
            EV_xy = EV1.*EV2;
            n_xy = neighboordhoodValues1.*neighboordhoodValues2;
            cov_xy_ij = (EV_xy(:)'*n_xy(:))/sum(EV_xy(:));
            cov_xy(i,j) = cov_xy_ij - mu_x_ij*mu_y_ij;
            
            ssc_x = (neighboordhoodValues1-mu_x_ij).^2;
            ssc_y = (neighboordhoodValues2-mu_y_ij).^2;
            
            out1(i,j) = (EV1(:)'*ssc_x(:))/sum(EV1(:));
            out2(i,j) = (EV2(:)'*ssc_y(:))/sum(EV2(:));
            
        end
        waitbar(i/m)
    end
    close(h);
    toc
    
    sigma = mat2img(out1, out2, out2);
    sigma = sigma(:,:,1:2);
    
    mu = mat2img(mu_x, mu_y, mu_y);
    mu = mu(:,:,1:2);
    
end

