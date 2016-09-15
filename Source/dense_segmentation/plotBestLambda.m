% Convex Optimization - Project 1
% MICHAEL SINGLE
% 08-917-445
function plotBestLambda( error, stepSize, bestLambda, min_r, max_r, doPlotLog )
%PLOTBESTLAMBDA show error lambda plot to determine best lambda value.
%   to have all required plotting data either run the script findBestLmabda
%   or load the precomputed data from 'Input/fruitsBigBestLambdaData.mat'
%   (only available for the fruits data set).
%
%   @param error 1:420 vector containing all ssd error for the lambda
%       values.
%   @param stepSize Integer value of lambda step sizes used for computing
%       the errors (errors computed at those positions).
%   @param bestLambda best lambda computed by findBestLambda for given
%       dataset.
%   @param min_r first index in dataset we want to use for plotting.
%   @param max_r last index in dataset we want to use for plotting.
%   @param doPlotLog Boolean do we want to show the ssd error in log scale?
%          show in log scale if true otherwise plot its plain values.
figure
if doPlotLog
    plot((min_r:max_r)*stepSize, (error(min_r:max_r)));
    ylabel('log SSD')
else
    plot((min_r:max_r)*stepSize, (error(min_r:max_r)));
    ylabel('SSD')
end
xlabel('lambda')
title('Fruits')
hold on
plot([bestLambda, bestLambda], get(gca, 'ylim'))
end

