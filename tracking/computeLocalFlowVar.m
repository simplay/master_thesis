function [variances] = computeLocalFlowVar(flowfield)
%COMPUTELOCALFLOWVAR Summary of this function goes here
%   Detailed explanation goes here
    x = flowfield(:,:,1);
    y = flowfield(:,:,2);
    
    t = 20;
    kernel = ones(t,t);
    
    % var(x+y) = var(x) + var(y) + cov(x,y)
    e_x = conv2(x, kernel, 'same') / (t*t);
    e_y = conv2(y, kernel, 'same') / (t*t);
    
    % var(x) = E((x-e(x))^2)
    var_x = conv2((x-e_x).^2, kernel, 'same');
    var_y = conv2((y-e_y).^2, kernel, 'same');
    
    % cov(x,y) = E((x-e(x))*(y-e(y)))
    cov_xy = conv2((x-e_x).*(y-e_y), kernel, 'same');
    
    % var(x+y) = var(x) + var(y) + 2*cov(x,y)
    variances = var_x + var_y + 2*cov_xy;
end

