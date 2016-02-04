function [ content, raw_content ] = extract_spatial_neighbors(filepath, label_mappings)
%EXTRACT_SPATIAL_NEIGHBORS Summary of this function goes here
%   Detailed explanation goes here

    raw_content = textread(filepath, '', 'delimiter', ',');
    [i,content] = ismember(raw_content, label_mappings);
    
    if size(i,1)*size(i,2) == sum(sum(i))
        disp('spnn index reassignment successful');
    else
        disp('ERROR: spnn index reassignment unsuccessful');
    end

end

