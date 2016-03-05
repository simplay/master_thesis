function [ transformed_label_ids ] = labeI_idx_to_W_lookup_idx( label, label_mappings )
%LABEI_IDX_TO_W_LOOKUP_IDX transforms a given trajectory label to its
%corresponding index in its affinity matrix according to the given
%label mapping.
%   @example: given label_mappings = (57, 58, 59, 60, ...)'
%       labeI_idx_to_W_lookup_idx( 60, label_mappings)
%       ans = 4
    transformed_label_ids = find(label_mappings == label);
end

