function show_usage_information(USE_W_VEC, USE_CLUSTERING_CUE, W, U)
%SHOW_USAGE_INFORMATION displays some helpful information to a user.
%   USE_W_VEC flag should affinities be displayed?
%   USE_CLUSTERING_CUE flag should clustering be displayed?
%       if true, the clusters will always be displayed, bypassing all other
%       set flags.
%   W affinity matrix
%   U smallest k eigenvectors

    if USE_CLUSTERING_CUE
        disp('Displaying clustering')
        disp('Note that the assignment of col_sel has no effect.')
    else
        if USE_W_VEC
            disp('Displaying affinities')
            disp(['There are ', num2str(size(W, 2)),' trajectory affinities.'])
            disp(['Thus, col_sel should be a value within that range.'])
            disp(['(col_sel is label that maps to a W index value)'])
        else
            disp('Displaying eigenvectors')
            disp(['There are ', num2str(size(U, 2)),' eigenvectors.'])
            disp(['Thus, col_sel should be a value within that range.'])
            disp(['(col_sel is a eigenvector index value)'])
        end
    end

end

