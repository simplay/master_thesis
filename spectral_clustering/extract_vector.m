function [ ev ] = extract_vector( U, W, col_idx, USE_W_VEC, label_mappings)
%EXTRACT_VECTOR Summary of this function goes here
%   Detailed explanation goes here

    if USE_W_VEC
        % ev = extract_row_from_w(col_idx);
        idx = label_as_local(label_mappings, col_idx);
        ev = W(:,idx);
    else
        ev = U(:,col_idx);
        %ev = ev-min(ev(:));
        %ev = ev ./ max(ev(:));
    end


end

