function [ ev ] = extract_vector( U, W, col_idx, USE_W_VEC )
%EXTRACT_VECTOR Summary of this function goes here
%   Detailed explanation goes here
    if USE_W_VEC
        ev = W(:,col_idx);
    else
        ev = U(:,col_idx);
        ev = ev-min(ev(:));
        ev = ev ./ max(ev(:));
    end


end

