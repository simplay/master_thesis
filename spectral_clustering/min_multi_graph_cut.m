function [label_assignments,energy] = min_multi_graph_cut(v, pa, mu, K, W)
%MIN_MULTI_GRAPH_CUT compute new cluster assignments of a given set of N
%nodes solving a MRF minimization problem.
%
%   @param v (N x m) eigenvectors
%   @param pa (1 x N) node assignments
%   @param mu (K x m) cluster centroids
%   @param K number of expected clusters
%   @return label_assignments (1 x N) node assignments


% working example:
% for a 480 by 640 pixel img
% we get a class dim equal (480*640) x 1
% unary (C=2) x (480*640)
% pairwise 480*640 x 480*640

    % A CxN matrix specifying the potentials (data term) for each of the C
    % possible classes at each of the N nodes.
    unary = computeDataTerm(v, pa, mu, K);

    % A NxN sparse matrix sparse matrix specifiying the graph structure and
    % cost for each link between nodes in the graph.
    pairwise = computeSmoothnessTerm(v, pa, mu, K);

    % A CxC matrix specifiying the label cost for the labels of each adjacent
    % node in the graph.
    labelcost = [0,1; 1,0];       
 
    % A 0-1 flag which that determines if the 'swap of expansion' method is used to
    % solve the minimization. 
    % 0 == swap, 1 == expansion.
    expansion = 0;
    
    T = W;
    T = T-min(T(:));
    T = T ./max(T(:));
    pairwise = sparse(T);
    % See http://vision.ucla.edu/~brian/gcmex.html
    [label_assignments, energy, ~] = GCMex(pa, single(unary), pairwise, single(labelcost), expansion);
end

function data_term = computeDataTerm(v, pa, mu, K)
% COMPUTE_DATA_TERM a CxN matrix specifying the potentials (data term) for each of the C
% possible classes at each of the N nodes.



        % 1. fetch the i-th row of matrix centroid to obtain mu_i
        % 2. assemble all a-th components of all m eigenvectors v to a row
        % vector
    % foreach 1:K    





    data_term = zeros(K, length(pa));
end

function smoothness_term = computeSmoothnessTerm(v, pa, mu, K)
% COMPUTE_SMOOTHNESS_TERM A NxN sparse matrix sparse matrix specifiying the graph structure and
% cost for each link between nodes in the graph.
    N = length(pa);
    smoothness_term = zeros(N,N);
end

