function [W, U_full, S_full, U_small, S_small] = extract_eigendecomp_data(BASE, DATASET, PRE, W, U_full, S_full, THRESH, USE_EIGS, COMPUTE_EIGS, USE_CLUSER_EW_COUNT, FORCE_EW_COUNT, FILTER_ZERO_EIGENVALUES)
%SELECT_EIGENDECOMP_DATA Summary of this function goes here
%   Detailed explanation goes here
    
    U_small = U_full;
    S_small = S_full;
    if COMPUTE_EIGS
        fname = strcat(BASE, PRE, DATASET, '_sim.dat');
        disp(['Loading similarity file: ' fname]);
        W = load(fname);
        
        [U_small, S_small] = similarity_eigendecomp(W, THRESH, USE_EIGS);
        
        U_full = U_small;
        S_full = S_small;
    end
    d = diag(S_small);
    if FILTER_ZERO_EIGENVALUES
        U_small = U_small(:, d > 0);
        d = d(d > 0);
    end
    

    [d, s_idx] = sort(d);
    U_small = aggregate_mat_cols(U_small, s_idx);
    
    % TODO is this an appropriate threshold?
    [aa,~,~] = find(d < 0.1);
    if USE_CLUSER_EW_COUNT
        aa = 1:FORCE_EW_COUNT;
    end

    U_small = aggregate_mat_cols(U_small, aa);
    S_small = d(aa);
    
end

