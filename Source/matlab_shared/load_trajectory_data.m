function [imgs, fwf, bwf, boundaries, label_mappings, frames] = load_trajectory_data(DATASET, METHODNAME, PREFIX_INPUT_FILENAME, START_FRAME, END_FRAME)
%LOAD_TRAJECTORY_DATA loads all relevant data, associated with trajectories
%   for a given dataset and affinity matrix, identified by its commonly known,
%   unique naming scheme.
%
%   @example
%       DATASET = 'cars';
%       METHODNAME = 'ldof';
%       PREFIX_INPUT_FILENAME = 'pd_both_10';
%       [imgs, fwf, bwf, boundaries, label_mappings, frames] = 
%           load_trajectory_data(DATASET, METHODNAME, PREFIX_INPUT_FILENAME)
%
%   @param DATASET name of target dataset
%   @param METHODNAME name of used flow method
%   @param PREFIX_INPUT_FILENAME used similarity file prefix
%   @param START_FRAME (optional) first frame data should be loaded.
%   @param END_FRAME (optional) last frame data should be loaded.
%
%   @return imgs the image path names associated with the given dataset.
%   @return fwf the forward flow path-filenames associated with the given dataset
%       and flow method.
%   @return bwf the backward flow path-filenames associated with the given dataset
%       and flow method.
%   @return boundaries an array containing two elements: the first is
%       equals the first index value of the dataset images, the last equals the
%       last index value
%   @return label_mappings trajectory index label value mapping file.
%   @return frames an array of structs, each containing the labels and tracked positions in a target frame. 

    [BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET); 
    pr = '';
    if isempty(PREFIX_INPUT_FILENAME) == 0
        pr = strcat(PREFIX_INPUT_FILENAME, '_');
    end
    label_mappings = labelfile2mat(strcat(BASE, DATASET, '_', pr));
    [boundaries, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH, METHODNAME);
    
    if nargin == 3
        START_FRAME = boundaries(1);
        END_FRAME = boundaries(2);
    end
    
    frames = loadAllTrajectoryLabelFrames(DATASET, START_FRAME, END_FRAME, PREFIX_INPUT_FILENAME);
end

