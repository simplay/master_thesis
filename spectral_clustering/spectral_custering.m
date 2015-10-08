function [idx,C,sumd,D] = spectral_custering( U, cluster_count )
%SPECTRAL_CUSTERING Summary of this function goes here
%   Detailed explanation goes here
u = real(U);
u = u / norm(u);
disp('foo');
opts = statset('Display','final');
[idx,C,sumd,D] = kmeans(u, cluster_count, 'Replicates',100, 'Start', 'sample', 'Options', opts);
disp(C);
end

