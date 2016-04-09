clc

addpath('src');
addpath('../matlab_shared');

DATASET = 'chair_3_cast';
METHODNAME = 'ldof';
PREFIX_INPUT_FILENAME = 'md_d_nn';

graph_cuts_dir = '../output/graph_part/';
PART_DS = 'c14';
LABELS_FILE_PATH = strcat(graph_cuts_dir, PART_DS, '_part.txt');

PREFIX_OUTPUT_FILENAME = 'foobarbaz';
COMPUTE_FULL_RANGE = false;
CLUSTER_CENTER_COUNT = 20;

frame_idx = 1;

SAVE_FIGURES = true;
SHOW_SEGMENTATION = true;

%% load relevant data
[BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET); 

% prepare output directories, name prefixes.
path = make_segmentation_dir(DATASET, METHODNAME, PREFIX_OUTPUT_FILENAME );

%% display segmentation and its data.

% load label vector indices mappings
fileID = fopen(LABELS_FILE_PATH);
C = textscan(fileID,'%d,%d');
label_assignments = cell2mat(C(2));
fclose(fileID);

label_mappings = labelfile2mat(strcat(BASE, DATASET));
[boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH, METHODNAME);

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