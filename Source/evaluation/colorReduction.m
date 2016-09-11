function [ rgb_uint8 ] = colorReduction( segmentationImg, colorCount )
%COLORREDUCTION Summary of this function goes here
%   Detailed explanation goes here
[~, map] = rgb2ind(segmentationImg, colorCount, 'nodither');
X = rgb2ind(segmentationImg, map);
segmentationImg=ind2rgb(X,map);
rgb_uint8=uint8(segmentationImg*255+0.5);

end

