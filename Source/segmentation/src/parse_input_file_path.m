function [BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET, USE_SPECIAL_NAMING)
%PARSE_INPUT_FILE_PATH Summary of this function goes here
%   Detailed explanation goes here

    BASE = '../output/similarities/';
    if USE_SPECIAL_NAMING
        idx = regexp(DATASET, 'v');
        DATASETNAME = DATASET(1:idx-1);
    else    
        DATASETNAME = DATASET;
    end

    BASE_FILE_PATH = strcat('../../Data/', DATASETNAME, '/');  


end

