function [ rowIndices, columnIndices  ] = getRanges( i, j, maxI, maxJ, w )
%GETRANGES Summary of this function goes here
%   Detailed explanation goes here
    bottom = max(i-w, 1);
    top = min(i+w, maxI);
    left = max(j-w, 1);
    right = min(j+w, maxJ);
           
    rowIndices = bottom:top; 
    columnIndices = left:right;       
end
