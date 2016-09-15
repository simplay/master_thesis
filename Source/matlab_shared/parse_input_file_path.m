function [BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET)
%PARSE_INPUT_FILE_PATH Summary of this function goes here
%   Detailed explanation goes here

    BASE = '../output/similarities/';
    BASE_FILE_PATH = strcat('../../Data/', DATASET, '/');  
end

