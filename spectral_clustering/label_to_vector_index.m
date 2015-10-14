function transformed = label_to_vector_index(label_mappings, label)
%LABEL_AS_LOCAL find the index in label_mappings vector where the given
%label is placed.
%   used to lookup values in vectors that contain label related information
%   the index of such a vector maps to a certain label value.
%   assumption: labels are ordered, then the k-th vector index corresponds
%   to the k-th sorted label value (ascending). 
%
%   not that this function is needed, since some label indices do not exist
%   i.e. there are holes.
%
%   use case example: the label_assignments vector
%   its indices map to labels. since some labels were filtered, we have to
%   use this function.

    [transformed,~,~] = find(label_mappings == label);
end

