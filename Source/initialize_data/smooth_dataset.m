function smooth_dataset( dataset_path, img_ext)
%UNTITLED Summary of this function goes here
%   @param dataset_path path to target dataset
%   @param img_ext image file extension of target image files
%   @example 
%       dataset_path = '../../Data/c14/'
%       smooth_dataset( dataset_path, '.ppm')

    addpath('src/');
    
    out_dir = strcat(dataset_path, 'filtered/'); 
    if ~exist(out_dir,'dir')
        mkdir(out_dir);
    end
    
    files_to_select = strcat(dataset_path,'*', img_ext);
    src_files = dir(files_to_select);
    parfor i = 1 : length(src_files)
        filename = src_files(i).name;
        input_file_path_name = strcat(dataset_path, filename);

        % yields super results for c14 dataset
        img = im2double(imread(input_file_path_name));
        filtered_img = bfiltImg3(img, 8, 0.25);
        
        output_file_path_name = strcat(out_dir, filename);
        imwrite(filtered_img, output_file_path_name);
        disp(['Finished iter ', num2str(i), '...'])
    end
    
end
