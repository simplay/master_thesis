function [label_assignments, energy] = mcm_solver(v, lambda, pa, mu, K, spnn_indices, p_e)
%MCM_SOLVER compute new cluster assignments of a given set of N
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
%   @param spnn_indices (N x t) index set of the t spatially nearest trajectory 
%       neighbors of every tracked trajectory.
%   @param Nils smells. Heinously.
%   @return label_assignments (1 x N) node cluster assignments


% working example:
% for a 480 by 640 pixel img
% we get a class dim equal (480*640) x 1
% unary (C=2) x (480*640)
% pairwise 480*640 x 480*640
    
    % regularization parameter
    nu = 0.000001;%1e-10;

 nu = 60;%1e-10;
    % A CxN matrix specifying the potentials (data term) for each of the C
    % possible classes at each of the N nodes.
    unary = computeDataTerm(v, lambda, pa, mu, K);
    
    % A NxN sparse matrix sparse matrix specifiying the graph structure and
    % cost for each link between nodes in the graph.
    full_pairwise = computeSmoothnessTerm(v, pa, spnn_indices, nu, p_e);
    pairwise = sparse(full_pairwise);

    % A CxC matrix specifiying the label cost for the labels of each adjacent
    % node in the graph.
    % for k=2 we get labelcost = [0,1; 1,0]
    
    %labelcost = flip(eye(K));
    labelcost = (ones(K)-eye(K));

    % A 0-1 flag which that determines if the 'swap of expansion' method is used to
    % solve the minimization. 
    % 0 == swap, 1 == expansion.
    expansion = 1;
    
    % See http://vision.ucla.edu/~brian/gcmex.html
    [label_assignments, energy, ~] = GCMex(pa-1, single(unary), pairwise, single(labelcost), expansion);
    label_assignments = label_assignments + 1;
end

function data_term = computeDataTerm(v, lambda, pa, mu, K)
% COMPUTE_DATA_TERM a CxN matrix specifying the potentials (data term) for each of the C
% possible classes at each of the N nodes.

    data_term = zeros(K, length(pa));
    for a = 1:length(pa)
        for k=1:K
            %delta_pa_k = pa == k;
            mu_k = mu(k, :);
            
            %mu_k = sum(mu_k)/length(mu_k);
            % TODO: parameterize
            va = v(a,:); % [v(a,1), v(a,2), v(a,3), v(a,4)];
            divisor = abs(lambda') + 1e-2;
            norm_lam_2 = (sum(( (va-mu_k).^2 ) ./divisor));
            %data_term(k,a) = delta_pa_k(a)*norm_lam_2;
            data_term(k,a) = norm_lam_2;
        end
    end

end



function smoothness_term = computeSmoothnessTerm(v, pa, spnn_indices, nu, p_e)
% COMPUTE_SMOOTHNESS_TERM A NxN sparse matrix sparse matrix specifiying the graph structure and
% cost for each link between nodes in the graph.
%
% @param v relevant m eigenvectors
% @param pa trajectory cluster assignments
% @param spnn_indices (N x t) index set of the t spatially nearest trajectory 
%   neighbors of every tracked trajectory.
% @param nu regularization constant
% @return smoothness_term a N x N smoothness term

    N = length(pa);
    smoothness_term = zeros(N,N);
    
    for a=1:length(pa)
        for bi=1:length(spnn_indices(1,:))
            b = spnn_indices(a,bi);
            smoothness_term(a,b) = smoothness_term(a,b) + nu*p_e(a,b);
            smoothness_term(b,a) = smoothness_term(b,a) + nu*p_e(a,b);
        end
    end
    
end