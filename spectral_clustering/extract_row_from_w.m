function [ vec ] = extract_row_from_w(row_id )
%EXTRACT_ROW_FROM_W Summary of this function goes here
%   Detailed explanation goes here
fid = fopen('../output/similarities/cars1_sim.dat');
tline = fgets(fid);

for t=1:row_id-1
    tline = fgets(fid);
end
fclose(fid);
a_line = strsplit(tline, ',');

len = length(a_line);
vec = zeros(len, 1);
for k=1:len
    vec(k,1) = str2double(cell2mat(a_line(k)));
end


end

