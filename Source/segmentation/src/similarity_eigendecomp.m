function [ U_small, S_small] = similarity_eigendecomp(W, THRESH, USE_EIGS)
%SIMILARITY_EIGENDECOMP Summary of this function goes here
%   Detailed explanation goes here

        WW = W + ones(size(W))*THRESH;

        d_a = sum(WW,2);
        D = diag(d_a);
        D12 = diag(sqrt(1./d_a));
        B = D12*(D-WW)*D12;
        if USE_EIGS
            [U_small, S_small, ~] = eigs(B, 50, 1e-6);
        else
            [U_small, S_small] = eig(B);
        end
        

end

