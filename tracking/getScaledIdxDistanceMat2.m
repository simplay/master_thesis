function [ deltaIdx ] = getScaledIdxDistanceMat2( rowRange, columnRange, shift,scaleF )
%GETSCALEDIDXDISTANCEMAT2 Summary of this function goes here
%   Detailed explanation goes here
    R = repmat(rowRange', 1, length(columnRange))-shift(1);
    C = repmat(columnRange, length(rowRange), 1)-shift(2);
    delta = R.^2 + C.^2;
    deltaIdx = delta / scaleF;
end