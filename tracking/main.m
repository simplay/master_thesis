clc;
clear all;
close all;

addpath('../libs/flow-code-matlab');

%%

FILTER_FOR_CANDIDATES = true;
sigma = 1;
thresh = 0.3;
img_range = 4:4;

%%

path = '../data/ldof/';
file = 'cars1/';
format = '.ppm';
prefix = '0';

%%

img1_filepath = [path,file,prefix,num2str(1),format];
dummyImg = imread(img1_filepath);
[m,n,~] = size(dummyImg);
pixel_mask = ones(m,n);
for base_img_idx=img_range,

    img1_filepath = [path,file,prefix,num2str(base_img_idx),format];
    img2_filepath = [path,file,prefix,num2str(base_img_idx+1),format];

    foreward_flow_filepath = [path,file,'ForwardFlow00',num2str(base_img_idx),'.flo'];
    backward_flow_filepath = [path,file,'BackwardFlow00',num2str(base_img_idx),'.flo'];

    img1 = imread(img1_filepath);
    img1 =im2double(img1);
    img2 = imread(img2_filepath);
    img2 =im2double(img2);

    if FILTER_FOR_CANDIDATES
        variant = 2;
        [pixel_values, pixel_mask] = find_tracking_candidates(img1, sigma, thresh, variant);
    end
    
    optical_flow = readFlowFile(foreward_flow_filepath);
    backward_optical_flow = readFlowFile(backward_flow_filepath);

    % get foreward-and backward flow directions.
    u_flow = optical_flow(:,:,1);
    v_flow = optical_flow(:,:,2);
    bw_u_flow = backward_optical_flow(:,:,1);
    bw_v_flow = backward_optical_flow(:,:,2);

    % get masks
    sanity_mask = flow_sanity_check(optical_flow, backward_optical_flow);
    pixel_mask = im2double(pixel_mask);

    [ tp1_x_candidates, tp1_y_candidates ] = find_candidate_tracking_indices( u_flow, v_flow, pixel_mask.*sanity_mask );
    [ other, pm ] = generate_tracking_vectors( tp1_x_candidates, tp1_y_candidates);

    %%
    dummy = zeros(m,n);
    figure;
    imshow(mat2img(pm,dummy,dummy)+mat2img(dummy,dummy,other));
    disp('red: features frame t AND blue: shift to same features in frame t + 1')
end