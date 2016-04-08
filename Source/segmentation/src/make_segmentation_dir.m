function [ path ] = make_segmentation_dir(DATASET, METHODNAME, PREFIX_OUTPUT_FILENAME )
%MAKE_SEGMENTATION_DIR Summary of this function goes here
%   Detailed explanation goes here
        path = strcat('../output/clustering/');
        pref_meth = '';
        if isempty(PREFIX_OUTPUT_FILENAME) == 0
            pref_meth = strcat('_', PREFIX_OUTPUT_FILENAME);
        end
        method_id = strcat(DATASET, '_', METHODNAME, pref_meth);
        path = strcat(path, method_id, '/');
        mkdir(path);

        dir_exists = exist(path,'dir');
        if dir_exists
            disp(['Target directory `', path, '` already exists.'])
            prompt = 'Should content be overwritten? [y/n]';
            str = input(prompt,'s');
            if ~(str == 'y')
                error('Program exit');
            end
        end

end

