function rescale_depths( input_filepath, img_ext, scale, output_path, match )
% rescale_bonn_depths allows to rescale a set of depth fields and save the
% rescaled images in a new target directory. Used to rescale Bonn depth
% fields.
% example usage:
% in = '~/repos/rgbd_layered_flow_code/data/our_data/chair/1_4/'
% out = '~/repos/rgbd_layered_flow_code/data/our_data/chair/1_4/rescaled/'
% match = 'depth' % only select files including this substring
% ext = '.png' % fileextension of files we'd like to rescale
% scale = 5; % divides image channels by this scale, bonn data 5 => mm
% scales.
% rescale_depths(in, ext, 5, out, match);
    % filepath #=> '~/foobar/'
    % img_ext #=> '.png'
    % scale #=> 3
    % output_path #=> '~/foobar/rescaled/'
%    
% @example Rescale a Bonn Dataset   
%   in = '../../Data/bonn_chairs_263_3_434_SRSF/depth';    
%   out = '../../Data/bonn_chairs_263_3_434_SRSF/depth/rescaled/';
%   match = '';
%   ext = '.png'
%   rescale_depths(in, ext, 5, out, match);
%   rescale_depths(in, ext, 5, out, match);

    % create target directory in case it does not exist yet
    if ~exist(output_path,'dir')
        mkdir(output_path);
    end
    
    files_to_select = strcat(input_filepath, match,'*', img_ext);
    src_files = dir(files_to_select);
    for i = 1 : length(src_files)
        filename = src_files(i).name;
        input_file_path_name = strcat(input_filepath, filename);
        img = imread(input_file_path_name);
        output_file_path_name = strcat(output_path, filename);
        imwrite(img/scale, output_file_path_name);
    end
end

