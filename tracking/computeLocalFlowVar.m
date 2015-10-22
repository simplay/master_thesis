function [variances] = computeLocalFlowVar(flowfield, method)
%COMPUTELOCALFLOWVAR Summary of this function goes here
%   Detailed explanation goes here
    [ff, w_u, w_v, wl] = bfiltmat2(flowfield, 3, 5, false);
    
    % bilarteral filtered flow field directions
    % act as 
    xx = ff(:,:,1);
    yy = ff(:,:,2);
    e_x = xx;
    e_y = yy;
    x = flowfield(:,:,1);
    y = flowfield(:,:,2);
    
    if method == 1
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

