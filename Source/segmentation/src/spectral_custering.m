function [idx, C, sumd, D] = spectral_custering( U, cluster_count, iterationCount, verbose)
%SPECTRAL_CUSTERING run k-means on a set of eigenvectors.
%   @param U eigenvectors of dimension m x c, where m is the number of
%       trajectories and c the number of relevant eigenvectors.
%   @pararm cluster_count number of clusters
%   @pararm iterationCount number of replications that should be performed.
%       Run with the smallest error will be selected.
    
    if isreal(U)
        opts = statset('Display','final');
        [idx,C,sumd,D] = kmeans(U, cluster_count, 'MaxIter', 200, 'Replicates', iterationCount, 'Start', 'sample', 'Options', opts);
        if verbose
            disp(C);
        end
    else
        if verbose
            disp('U is not real');
        end
            %u = real(U);
            %u = u / norm(u);
    end
end

