function [ ev ] = extract_vector( U, W, col_idx, USE_W_VEC, label_mappings)
%EXTRACT_VECTOR Summary of this function goes here
%   Detailed explanation goes here
    if USE_W_VEC
        idx = label_to_vector_index(label_mappings, col_idx);
        ev = W(:,idx);
    else
        ev = U(:,col_idx);
    end


end
