%% init data
clear all;
close all;
clc;

addpath('../matlab_shared/')

DATASET = 'cars';
METHODNAME = 'ldof';
PREFIX_OUTPUT_FILENAME = 'pd_top_filtered_flows';
PREFIX_INPUT_FILENAME = 'pd_top_100_dense';
img_index = 1;

% load dataset input file paths
[BASE, BASE_FILE_PATH] = parse_input_file_path(DATASET); 

% prepare output directories, name prefixes.
path = make_segmentation_dir(DATASET, METHODNAME, PREFIX_OUTPUT_FILENAME );

pr = '';
if isempty(PREFIX_INPUT_FILENAME) == 0
    pr = strcat(PREFIX_INPUT_FILENAME, '_');
end
    
%% display segmentation and its data.

% load label vector indices mappings

label_mappings = labelfile2mat(strcat(BASE, DATASET, '_', pr));
[boundaries, imgs, ~, ~] = read_metadata(BASE_FILE_PATH, METHODNAME);
frames = loadAllTrajectoryLabelFrames(DATASET, boundaries(1), boundaries(2), PREFIX_INPUT_FILENAME);

% TODO do not hardcode this path
[FileName, PathName, FilterIndex] = uigetfile('*.txt');
strcat(PathName, FileName)
[~, label_assignments] = label_file_reader(strcat(PathName, FileName));

label_values = unique(label_assignments);

filename = imgs{1};
[m, n, ~] = size(imread(filename));

segments = zeros(m, n, length(label_values));
for k = 1:length(label_values)
    label = label_values(k);

    img = sparse_segmentation(frames, imgs, label_assignments, label_mappings, img_index, label);
    img = im2double(img);
    mask = 1 - (img == 0);

    iter = 4000; % 400;
    FIND_OPTIMUM = false;
    verbose = false;

    f = 0.2;
    mask = imresize(mask, f);
    img = imresize(img, f);

    mask = double(mask);
    Omega = mat2img(mask, mask, mask);

    % demosaiced imgle
    mosaiced = mat2img(img, img, img);

    %% find best lambda
    if FIND_OPTIMUM
        disp('starting finding best lambda');
        [ bestLambda ] = findBestLambda(im, mosaiced, Omega, 100 );
        disp('determined best lambda value');
    else
        bestLambda = 1; % found by running find best lambda script
    end

    %% compute demosaiced img
    demosaicedImg = demosaicing(mosaiced, Omega, bestLambda, iter, mosaiced, verbose);
    demosaicedImg = imresize(demosaicedImg, 1/f);

    d = demosaicedImg(:,:,1);
    d = d - min(d(:));
    d = d ./ max(d(:));
    segments(:,:,k) = (d > 0.2);

    %% display results
    figure('name', strcat('Segmentation of iteration ', num2str(k)))
    imshow(segments(:,:,k));
end

%%
index_of_largest = -1;
top_count = -1;
for k=1:size(segments,3)
    ones_count = sum(sum((segments(:,:,k) == 1)));
    if ones_count > top_count
        top_count = ones_count;
        index_of_largest = k;
    end
end
selection = 1:size(segments,3);
selection = logical(1-(selection == index_of_largest));
dense_forground_segments = segments(:,:,selection); 

%% merge dense forground segments
dense_img = zeros(size(dense_forground_segments, 1), size(dense_forground_segments, 2));
for k=1:size(dense_forground_segments, 3)
    current_fg_segment = dense_forground_segments(:,:,k);
    dense_img = dense_img + dense_forground_segments(:,:,k)*k;
end
