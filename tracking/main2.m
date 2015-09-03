clc;
clear all;
close all;

addpath('../libs/flow-code-matlab');


img1 = imread('../data/ldof/cars1/01.ppm');
img1 =im2double(img1);
img2 = imread('../data/ldof/cars1/01.ppm');
img2 =im2double(img2);

foreward_flow = readFlowFile('../data/ldof/cars1/ForwardFlow000.flo');
backward_flow = readFlowFile('../data/ldof/cars1/BackwardFlow000.flo');

sigma = 1;
thresh_scale = 0.3;
variant = 1;

[m,n,~] = size(img2);

% The value at pixel location (i,j) indicates the corresponding tracking
% label.
% Default tracking label for every pixel is the value 0.
% Therefore every valid label is supposed to be grater than 0
% TODO: add another dimension for time steps
tracked_pixels = zeros(m,n);

% candidate_indices logical mxn matrix that indicates/hints pixels 
% that depict good trackable candidates.
[~, candidate_indices] = find_tracking_candidates(img1, sigma, thresh_scale, variant);

% Check consistency of forward flow via backward flow: See paper
[ valid_regions ] = flow_sanity_check( foreward_flow, backward_flow );

trackable_pixels = candidate_indices .* valid_regions;


%% tracking step

% only iterate over trackable pixels
[idx, idy, ~] = find(trackable_pixels == 1);
for k = length(idx),
    ax = idx(k);
    ay = idy(k);
    
            bx = ax + fw_u_flow(ax,ay);
        by = ax + fw_v_flow(ax,ay);
        
        ibx = round(bx);
        iby = round(by);
        
        if true
            continue;
        else
            tracked_pixels(ibx, iby)
        end 
end

for ax=1:m,
    for ay=1:n,
        
        bx = ax + fw_u_flow(ax,ay);
        by = ax + fw_v_flow(ax,ay);
        
        ibx = round(bx);
        iby = round(by);
        
        if true
            continue;
        else
            tracked_pixels(ibx, iby)
        end
        
        
    end
end



