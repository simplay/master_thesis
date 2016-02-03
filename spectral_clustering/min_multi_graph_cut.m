function [label_assignments,energy] = min_multi_graph_cut(v, lambda, pa, mu, K, W)
%MIN_MULTI_GRAPH_CUT compute new cluster assignments of a given set of N
%nodes solving a MRF minimization problem.
%
% Bult a graph and find its min-cut. The graph vertices deptic either
% segmentation labels or trajectory identifiers.
%
%   @param v (N x m) eigenvectors sorted according to their eigenvalues.
%   @param lambda (1 x m) eigenvalues sorted according to their size.
%   @param pa (1 x N) node assignments
%   @param mu (K x m) cluster centroids computed via k-means.
%   @param K number of expected clusters
%   @return label_assignments (1 x N) node cluster assignments


% working example:
% for a 480 by 640 pixel img
% we get a class dim equal (480*640) x 1
% unary (C=2) x (480*640)
% pairwise 480*640 x 480*640
    
    % regularization parameter
    nu = 0.5;


    % A CxN matrix specifying the potentials (data term) for each of the C
    % possible classes at each of the N nodes.
    unary = computeDataTerm(v, lambda, pa, mu, K);

    % A NxN sparse matrix sparse matrix specifiying the graph structure and
    % cost for each link between nodes in the graph.
    pairwise = computeSmoothnessTerm(v, pa, nu);

    % A CxC matrix specifiying the label cost for the labels of each adjacent
    % node in the graph.
    % for k=2 we get labelcost = [0,1; 1,0]
    labelcost = flip(eye(K));
 
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

function data_term = computeDataTerm(v, lambda, pa, mu, K)
% COMPUTE_DATA_TERM a CxN matrix specifying the potentials (data term) for each of the C
% possible classes at each of the N nodes.

    data_term = zeros(K, length(pa));
    for a = 1:length(pa)
        for k=1:K
            delta_pa_k = pa == k;
            va = [v(a,1), v(a,2), v(a,3), v(a,4)];
            norm_lam_2 = (sum(( (va-mu(k,:)).^2 ) ./lambda'))^2;
            data_term(k,a) = delta_pa_k(a)*norm_lam_2;
        end
    end

        % 1. fetch the i-th row of matrix centroid to obtain mu_i
        % 2. assemble all a-th components of all m eigenvectors v to a row
        % vector
    % foreach 1:K    





    
end



function smoothness_term = computeSmoothnessTerm(v, pa, nu)
% COMPUTE_SMOOTHNESS_TERM A NxN sparse matrix sparse matrix specifiying the graph structure and
% cost for each link between nodes in the graph.
%
% @param v relevant m eigenvectors
% @param pa trajectory cluster assignments
% @param nu regularization constant
% @return smoothness_term a N x N smoothness term

    N = length(pa);
    smoothness_term = zeros(N,N);
end

function delta_value = delta_ab(pa, a, b)
% DELTA_AB kroneker delta funktion that checks whether two trajectories
% have the same label assignment
%
% @param pa (1 x N) all existing node assignments
% @param a integer an existing trajectory index contained in pa
% @param b integer an existing trajectory index contained in pa
% @return delta_value Integer 1 if trajectory label pa(a) matches the trajectory 
%   label pa(b) and 0 otherwise.

    delta_value = pa(a) == pa(b);
end


function delta_value = delta_ak(pa, a, k)
% DELTA_AK kroneker delta funktion that checks whether a trajectory
% has a given label value
%
% @param pa (1 x N) all existing node assignments
% @param a integer an existing trajectory index contained in pa
% @param k integer a target label value the trajectory is supposed to match
% @return delta_value Integer 1 if trajectory pa(a) matches the label value k and 0
%   otherwise.

    delta_value = pa(a) == k;
end

% retrieves all extracted spatial relevant neighbors for a given trajectory
function neighbor_indices = neighborhoodOf(pa, a)
end