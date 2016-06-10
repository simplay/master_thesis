% Convex Optimization - Project 1
% MICHAEL SINGLE
% 08-917-445
function [ bestLambda ] = findBestLambda(im, mosaiced, Omega, iter )
%FINDBESTLAMBDA finds best regularization parameter for solving
% the demosaicing optimization problem. This script performs some kind of
% bisection heuristics, i.e. it first finds the best lambda according to
% some step size. Once retrieved an optimum lambda according to a certain 
% step size, it searches the best lambda within the range [lambda-step,lambda+step]
% such a bisection heuristic is sound since all relevant lambda values are
% checked.
%   @param im ground truth image
%   @param mosaiced mosaiced image version of the given ground truth
%   @param Omega Bayer filter tensort (image) of same resolution as as the
%       given images (ground truth and demosaiced img).
%   @param iter number of iterations that should be performed 
%       for approximating the best lambda.
%   @return bestLambda Float approximated regularization term for given mosaiced image. 

    stepSize = 5;
    error = zeros(1,420);
    parfor k=1:420,
        l = stepSize*k
        current_demosaiced = demosaicing_michael_single(mosaiced, Omega, l, iter, im, false);
        ssd = (im-current_demosaiced).^2;
        error(k) = sum(abs(ssd(:)));
    end
    
    % show plot
    [~,ind] = min(error);
    plot((1:420)*stepSize, error);

    % find best candidate within retrieved range
    error2 = zeros(1,2*stepSize);
    parfor k=1:2*stepSize,
        l = ind*stepSize-stepSize+k
        current_demosaiced = demosaicing_michael_single(mosaiced, Omega, l, iter, im, false);
        ssd = (im-current_demosaiced).^2;
        error2(k) = sum(abs(ssd(:)));
    end
    [~,ind2] = min(error2);
    bestLambda = ind*stepSize-stepSize+ind2;
end

