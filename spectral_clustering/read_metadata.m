function [boundaries, imgs, fwf, bwf] = read_metadata(filepath)
%READ_METADATA parse relevant input data information from the file
%'used_input.txt' located at the given 'filepath'. This file contains
% a list of all image file names, flow file names (forward-and backward
% flow field files (.flo files)) and a tuple of indices, determining which
% image is the starting and which is the last image that should be used.

    fid = fopen(strcat(filepath, 'used_input.txt'));
    tline = fgets(fid);
    boundaries = zeros(1,2);
    while ischar(tline)
        if strcmp(tline(1:end-1),'#use')
            flag = 1;
            k = 1;
        elseif strcmp(tline(1:end-1),'#imgs')
            flag = 2;
            k = 1;
        elseif strcmp(tline(1:end-1),'#fwf')
            flag = 3;
            k = 1;
        elseif strcmp(tline(1:end-1),'#bwf')
            flag = 4;
            k = 1;
        else
            if flag == 1
                boundaries(k) = str2double(tline);
            elseif flag == 2
                imgs{k} = appended_path(filepath, tline);
            elseif flag == 3
                fwf{k} = appended_path(filepath, tline);
            elseif flag == 4
                bwf{k} = appended_path(filepath, tline);
            end
            k = k + 1;
        end
        tline = fgets(fid);
    end
    fclose(fid);
end

function path = appended_path(basepath, fname)
    path = strcat(basepath, fname);
end
