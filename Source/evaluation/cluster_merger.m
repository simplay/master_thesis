addpath('../matlab_shared');
addpath('../segmentation/src')

%%
DATASET = 'bonn_watercan_713_3_884';
PREFIX_INPUT_FILENAME = 'ped_top_3000';
METHODNAME = 'ldof';
FRAME_IDX = 4;

%%
[FileName, FilePath, ~] = uigetfile('.txt');
LABELS_FILE_PATH = strcat(FilePath, FileName);
OUT_PATH = '../output/cluster_merges/';
% LABELS_FILE_PATH = '/Users/simplay/repos/ma_my_pipeline/Document/Results/final/watercan/bonn_watercan_713_3_884_ldof_ped_s_12_ct_1_c_15_ev_15/labels.txt';
% LABELS_FILE_PATH = '/Users/simplay/repos/ma_my_pipeline/Document/Results/final/watercan/bonn_watercan_713_3_884_SRSF_srsf_ped_s_12_ct_1_l_5_c_12_ev_18/labels.txt';
fileID = fopen(LABELS_FILE_PATH);
C = textscan(fileID,'%d,%d');
label_assignments = cell2mat(C(2)) + 1;
fclose(fileID);
label_identifiers = unique(label_assignments);

%% load gt: white means invalid region, i.e. ignore those labels
gt_fname = strcat(num2str(FRAME_IDX), '.png');
gt_img = imread(strcat('../../Data/', DATASET, '/gt/', gt_fname));

[BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET); 
pr = '';
if isempty(PREFIX_INPUT_FILENAME) == 0
    pr = strcat(PREFIX_INPUT_FILENAME, '_');
end

label_mappings = labelfile2mat(strcat(BASE, DATASET, '_', pr));
[boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH, METHODNAME);
frames = loadAllTrajectoryLabelFrames(DATASET, boundaries(1), boundaries(2), PREFIX_INPUT_FILENAME);


 frame = frames{FRAME_IDX};

[m, n, ~] = size(gt_img);
gt_img = double(gt_img);
gt_sum = gt_img(:,:,1) + 8*gt_img(:,:,2) + 16*gt_img(:,:,3);
color_values = unique(gt_sum);
merged_labels = zeros(1, length(label_identifiers));
for k=1:length(label_identifiers)
    l_id = label_identifiers(k);
    sub_label_assignments = label_assignments(label_assignments == l_id);
    sub_label_mappings = label_mappings(label_assignments == l_id);
    color_label_count = zeros(1,length(color_values));
    for idx=1:length(sub_label_assignments)
            lm_idx = sub_label_mappings(idx);
            fl_idx = find(frame.labels == lm_idx);
            if (isempty(fl_idx))
                continue;
            end
            i_x = floor(frame.ax(fl_idx));
            i_y = floor(frame.ay(fl_idx));
            % find idx of this label and increment
            gt_idx = find(color_values == gt_sum(i_x, i_y));
            color_label_count(gt_idx) = color_label_count(gt_idx) + 1;
            assignment = sub_label_assignments(idx);
    end
    [value, max_idx] = max(color_label_count);
    merged_labels(k) = max_idx;
end

filename = imgs{FRAME_IDX};
img = imread(filename);
I = mat2img(img(:,:,1));
fig = figure;
imshow(I);
hold on
rgb_values = rgb_list(8);
for idx=1:length(label_assignments)
    lm_idx = label_mappings(idx);
    fl_idx = find(frame.labels == lm_idx);
    if (isempty(fl_idx))
        continue;
    end
    assignment = label_assignments(idx);
    if assignment == 13
        disp('');
    end
    color_id = merged_labels(find(label_identifiers == assignment));
    t = rgb_values(color_id, :);
    color_value = [t(1), t(2), t(3)];
    plot(frame.ay(fl_idx), frame.ax(fl_idx), 'Color', color_value, 'Marker', '*');
    hold on
end

for k=1:length(merged_labels)
    % used to set merged label value
    ml_id = merged_labels(k);
    
    % used to fetch label
    li_value = label_identifiers(k);
    
    label_assignments(label_assignments == li_value) = ml_id;
end
filepath = OUT_PATH;
matching_indices = strfind(FilePath, '/');
startMatchIdx = (matching_indices(end-1)+1);
figure_dir_name = strcat(OUT_PATH, FilePath(startMatchIdx:end-1), '/');

if exist(figure_dir_name, 'dir') == 0
    mkdir(figure_dir_name);
end

write_label_clustering_file(label_assignments, label_mappings, figure_dir_name)
save_figure_as_image(fig, strcat(figure_dir_name, 'merged_frame_', num2str(FRAME_IDX)), m, n)