function [ label_mapping ] = labelfile2mat
%LABELFILE2MAT return label mapping
%   Detailed explanation goes here
fid = fopen('../output/similarities/cars1_labels.txt');
tline = fgets(fid);
labels_as_cells = strsplit(tline, ' ');
fclose(fid);
len = length(labels_as_cells);
label_mapping = zeros(len, 1);
for k=1:len
    label_mapping(k,1) = str2double(cell2mat(labels_as_cells(k)));
end







end

