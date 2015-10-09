function transformed = label_as_local(label_mappings, label)
%LABEL_AS_LOCAL Summary of this function goes here
%   Detailed explanation goes here

    [transformed,~,~] = find(label_mappings == label);

end

