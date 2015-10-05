function [ colors ] = eigenvector_to_color( eigenvectors, ev_id )
%EIGENVECTOR_TO_COLOR Summary of this function goes here
%   Detailed explanation goes here
    [m,~] = size(eigenvectors);
    colors = zeros(m,3);
    for k=1:m
        colors(k,:) = evc_to_color(eigenvectors(k,ev_id));
    end
end

