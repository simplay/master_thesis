function [ centroids ] = find_cluster_centers( label_assignments, v )
%FIND_CLUSTER_CENTERS Summary of this function goes here
%   Detailed explanation goes here

    m = size(v,2);
    cluster_labels = unique(label_assignments);
    centroids = zeros(length(cluster_labels), m);
    for k=1:length(cluster_labels)
        [i,~, ~] = find(label_assignments == cluster_labels(k) );
        n = length(i);
        centroids(k, :) = sum(v(i,:))/n;
    end
    


end

