addpath('../libs/flow-code-matlab');

%% load ground truth img
DS = 'car2';
imgName = '10.pgm';
DS_BASE_PATH = strcat('../data/ldof/',DS,'/gt/',imgName);
gtImg = imread(DS_BASE_PATH);
figure('name', 'ground truth')
imshow(gtImg);

%% load eval img
% load label

CF_PATH = '../output/clustering/';
suffix = 'labels_1.txt';

cf_fname = strcat(CF_PATH,DS,'_',suffix);
fid = fopen(cf_fname);
label_cluster_assignments = dlmread(cf_fname);

BASE = '../output/similarities/';
label_mappings = labelfile2mat(strcat(BASE,DS));
img_index = 10;
STEPSIZE_DATA = 8;
DATASET = DS;
PREFIX_FRAME_TENSOR_FILE = [DATASET,'_step_',num2str(STEPSIZE_DATA),'_frame_'];
pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(img_index),'.mat'));
pixeltensor = pixeltensor.tracked_pixels;
[row_inds, col_inds, ~] = find(pixeltensor(:,:,2) > 0);

dsImg = zeros(size(gtImg));
for k=1:length(row_inds)
    pixel_label = pixeltensor(row_inds(k), col_inds(k), 2);
    ax = pixeltensor(row_inds(k), col_inds(k), 3);
    ay = pixeltensor(row_inds(k), col_inds(k), 4);
    
    
    % since the first label is 2 but the first lookup index is 1
    %transformed_pixel_label = label_to_vector_index(label_mappings, pixel_label); % pixel_label - 1;
    if isempty(pixel_label) == 0
        [a,~,~] = find(label_cluster_assignments(:,1) == pixel_label);
        c_val = label_cluster_assignments(a,2);
        if isempty(c_val) == 0
            dsImg(ax, ay) = c_val;
        end
    end

end
figure
dsImgShow = dsImg - min(dsImg(:));
dsImgShow = dsImgShow ./ max(dsImgShow(:));
imshow(im2double(dsImgShow));

labels = unique(label_cluster_assignments(:,2));
TL = -1;
lid = -1;
for k=1:length(labels)
    l = labels(k);
    lenL = length(find(dsImg== l));
    if lenL > TL
        TL = lenL;
        lid = l;
    end
end

[a,b,c] = find(dsImg == lid);
for k=1:length(a)
    dsImg(a(k),b(k)) = 0;
end

dsImgShow = dsImg - min(dsImg(:));
dsImgShow = dsImgShow ./ max(dsImgShow(:));
figure('name', 'sparse clusters');
imshow(im2double(dsImgShow));


matchMask = (dsImg > 0) & (gtImg > 0);
totalHits = sum(sum(dsImg > 0));
matchedHits = sum(sum(matchMask > 0));

disp(['ratio machtes of total hits:', num2str(matchedHits/totalHits)])

