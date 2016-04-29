clc

addpath('src');
addpath('../matlab_shared');

DATASET = 'cars';
METHODNAME = 'ldof';
PREFIX_INPUT_FILENAME = 'foobar_md_nn_1000_best';

graph_cuts_dir = '../output/graph_part/';
PART_DS_PREF = 'pewpew';

PART_DS = strcat(PREFIX_INPUT_FILENAME, '_', DATASET, '_', PART_DS_PREF);
LABELS_FILE_PATH = strcat(graph_cuts_dir, PART_DS, '_part.txt');

PREFIX_OUTPUT_FILENAME = 'aaa';
COMPUTE_FULL_RANGE = true;
CLUSTER_CENTER_COUNT = 5;


USE_CUSTOM_TILL_BOUND = false;
frame_idx = 40;
TILL_FRAME = 4;

SAVE_FIGURES = true;
SHOW_SEGMENTATION = false;

%% load relevant data
[BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET); 

% prepare output directories, name prefixes.
path = make_segmentation_dir(DATASET, METHODNAME, PREFIX_OUTPUT_FILENAME );

%% display segmentation and its data.

% load label vector indices mappings
fileID = fopen(LABELS_FILE_PATH);
C = textscan(fileID,'%d,%d');
label_assignments = cell2mat(C(2)) + 1;
fclose(fileID);


pr = '';
if isempty(PREFIX_INPUT_FILENAME) == 0
    pr = strcat(PREFIX_INPUT_FILENAME, '_');
end

label_mappings = labelfile2mat(strcat(BASE, pr, DATASET));
[boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH, METHODNAME);

if USE_CUSTOM_TILL_BOUND
    boundaries(2) = TILL_FRAME;
end

frames = loadAllTrajectoryLabelFrames(DATASET, boundaries(1), boundaries(2), PREFIX_INPUT_FILENAME);

range = frame_idx:frame_idx;
if COMPUTE_FULL_RANGE
    range = boundaries(1):1:boundaries(2);
end

write_label_clustering_file(label_assignments, label_mappings, path);
rgb_values = rgb_list(CLUSTER_CENTER_COUNT);
for img_index = range
    if SAVE_FIGURES
        fig = figure('name', strcat('Frame ', num2str(img_index)));
    end

    disp(['Processing frame ',num2str(img_index), '...']);

    fpname = strcat(path, 'seg_f_', num2str(img_index), '.jpg');

    visualize_segmentation(frames, imgs, label_assignments, ...
                           label_mappings, img_index, rgb_values);
    if SAVE_FIGURES
        saveas(fig, fpname);
    end
    
    if SHOW_SEGMENTATION == 0
        close(fig);
    end
end