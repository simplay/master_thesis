function [ U_full, S_full, U_small, S_small ] = extract_eigendecomp_data(BASE, DATASET, U_full, S_full, COMPUTE_EIGS, SHOULD_LOAD_W, USE_CLUSER_EW_COUNT, FORCE_EW_COUNT)
%SELECT_EIGENDECOMP_DATA Summary of this function goes here
%   Detailed explanation goes here
    
    U_small = U_full;
    S_small = S_full;
    if COMPUTE_EIGS
        if SHOULD_LOAD_W
            fname = strcat(BASE, DATASET, '_sim.dat');
            W = load(fname);
        end
        [ U_small, S_small] = similarity_eigendecomp( W );
        
        U_full = U_small;
        S_full = S_small;

    end
    
    d = diag(S_small);
    [d, s_idx] = sort(d);
    U_small = aggregate_mat_cols(U_small, s_idx);
        
    if USE_CLUSER_EW_COUNT
        aa = 1:FORCE_EW_COUNT;
    else

    [aa,~,~] = find(d < 0.1);
    end
    UU = U_small;

    U_small = aggregate_mat_cols(U_small, aa);
    S_small = d(aa);

    S_small = S_small(S_small > 0);
    U_small = U_small(:,S_small > 0);

end

