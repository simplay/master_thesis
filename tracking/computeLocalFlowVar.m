function [variances] = computeLocalFlowVar(flowfield, method)
%COMPUTELOCALFLOWVAR Summary of this function goes here
%   Detailed explanation goes here
    [ff, w_u, w_v] = bfiltmat2(flowfield, 3, 5, false);
    
    % bilarteral filtered flow field directions
    % act as 
    xx = ff(:,:,1);
    yy = ff(:,:,2);
    x = flowfield(:,:,1);
    y = flowfield(:,:,2);
    
    if method == 1
        
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

