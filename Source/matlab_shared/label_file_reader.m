function [labels, assignments] = label_file_reader(path_to_file)
%LABEL_FILE_READER extracts the labels and ids from a target segmentation
%   labels.txt file. Note that each such file has to correspond to the 
%   common labels.txt format. That is, each line contains two intergers,
%   separated by a ,.
%
%   @param path_to_file path to labels.txt file
%   @example [labels, assignments] = label_file_reader('./labels.txt')
    [labels, assignments] = textread(path_to_file, '%d,%d');
end

