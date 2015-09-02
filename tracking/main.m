clc;
clear all;
close all;

addpath('../libs/flow-code-matlab');

base_img_idx = 1;

path = '../data/ldof/';
file = 'cars1_';
format = '.ppm';
prefix = '0';
img1_filepath = [path,file,prefix,num2str(base_img_idx),format];
img2_filepath = [path,file,prefix,num2str(base_img_idx+1),format];

foreward_flow_filepath = [path,'ForwardFlow00',num2str(base_img_idx),'.flo'];
backward_flow_filepath = [path,'BackwardFlow00',num2str(base_img_idx),'.flo'];

dummyImg = imread(img1_filepath);
[m,n,~] = size(dummyImg);

img1 = imread(img1_filepath);
img1 =im2double(img1);
img2 = imread(img2_filepath);
img2 =im2double(img2);



sigma = 1;

thresh = 0.3;

[pixel_values, pixel_mask] = find_tracking_candidates(img1, sigma, thresh);
optical_flow = readFlowFile('../data/ldof/ForwardFlow004.flo');
backward_optical_flow = readFlowFile('../data/ldof/BackwardFlow004.flo');
% [I,J,V] = find(pixel_mask > 0);

u_flow = optical_flow(:,:,1);
v_flow = optical_flow(:,:,2);

bw_u_flow = backward_optical_flow(:,:,1);
bw_v_flow = backward_optical_flow(:,:,2);

Idx = repmat((1:m)', 1, n);
Idy = repmat((1:n), m, 1);

pixel_mask = im2double(pixel_mask);


sanity_mask = flow_sanity_check(optical_flow, backward_optical_flow);

% perform bilinear interpolation instead of adding and cropping
tp1_x_candidates = (u_flow+Idx).*pixel_mask.*sanity_mask;
tp1_y_candidates = (v_flow+Idy).*pixel_mask.*sanity_mask;

tp1_x_candidates = round(tp1_x_candidates);
tp1_y_candidates = round(tp1_y_candidates);



%%
other = zeros(m,n);
pm = zeros(m,n);
counter = 0;
THRESH = 2; % pixel thresh
for k=1:m,
    for l=1:n,
        idx2 = tp1_x_candidates(k,l);
        idy2 = tp1_y_candidates(k,l);
        if idx2 > 0 && idy2 > 0 && idx2<=m && idy2 <= n
            %if idx2+k <= m && idy2+l <= n
                %cond1 = u_flow(k,l) + bw_u_flow(idx2+k,idy2+l);
                %cond2 = v_flow(k,l) + bw_v_flow(idx2+k,idy2+l);

                %if abs(cond1) < THRESH && abs(cond2) < THRESH
                    counter = counter + 1;
                    other(idx2, idy2) = 1;
                    pm(k, l) = 1;
                %end
                
            %end
        end
    end
end
disp('foobarized')

%%
dummy = zeros(m,n);
imshow(mat2img(pm,dummy,dummy)+mat2img(dummy,dummy,other));
disp('red: features frame t AND blue: shift to same features in frame t + 1')

disp('done')
