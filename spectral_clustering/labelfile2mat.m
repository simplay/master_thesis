function [ label_mapping ] = labelfile2mat(PATH)
%LABELFILE2MAT return label mapping
%   Detailed explanation goes here
fname = strcat(PATH, '_labels.txt');
fid = fopen(fname);
tline = fgets(fid);
labels_as_cells = strsplit(tline, ' ');
fclose(fid);
len = length(labels_as_cells);
label_mapping = zeros(len, 1);
for k=1:len
    label_mapping(k,1) = str2double(cell2mat(labels_as_cells(k)));
end







end

