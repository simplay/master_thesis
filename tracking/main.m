img1 = imread('../data/ldof/cars1_01.ppm');
img1 =im2double(img1);
img2 = imread('../data/ldof/cars1_02.ppm');
img2 =im2double(img2);

eps = 0.5; % threshold value

i1 = find_tracking_candidates(img1, eps);



disp('done')