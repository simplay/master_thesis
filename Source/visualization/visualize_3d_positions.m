
clc
DATASET = 'chair_3_cast';

basePath = strcat('../../Data/', DATASET, '/');

IMG_IDX = 1;

depthFilePath = strcat(basePath, 'depth/', num2str(IMG_IDX), '.png');
imgFilePath = strcat(basePath, num2str(IMG_IDX), '.png');

depths = imread(depthFilePath);
depths = double(depths) * 0.0002;

img = imread(imgFilePath);

% principal point
p = [364.272, 364.272]; 

% focal lengths
f = [259.726, 203.094];

t = figure;
map = depths ./ max(depths(:));
depthmap = map.*(1-(depths == 0));
image = double(img(:,:,1)).*(1-(depths == 0));
image = image ./ max(image(:));

alpha = 0.85;
I = mat2img((alpha*depthmap+(1-alpha)*image));
imshow(I);

pflag=0;
sflag=0;
uicontrol('String','Pause','CallBack','pflag=1;');
while (~sflag)
   %do smth
    [x, y] = ginput(2);
    disp('Image coordinates:')

    % depths in meters
    d1 = depths(floor(y(1)), floor(x(1)));
    d2 = depths(floor(y(2)), floor(x(2)));
    
    disp(['1. Selection: (', num2str(x(1)), ' ', num2str(y(1)), ')', ' depth=', num2str(d1), ' m']);
    disp(['2. Selection: (', num2str(x(2)), ' ', num2str(y(2)), ')', ' depth=', num2str(d2), ' m']);
    
    p1 = [d1*((x(1)-p(1))/(f(1))), d1*((y(1)-p(2))/(f(2))), d1 ];
    p2 = [d2*((x(2)-p(1))/(f(1))), d2*((y(2)-p(2))/(f(2))), d2 ];
    
    x1 = num2str(p1(1)); y1 = num2str(p1(2)); z1 = num2str(p1(3));
    x2 = num2str(p2(1)); y2 = num2str(p2(2)); z2 = num2str(p2(3));
    
    strp1 = strcat('p1=(', x1, ',', y1, ',', z1, ')');
    strp2 = strcat('p2=(', x2, ',', y2, ',', z2, ')');
    
    disp(strp1);
    disp(strp2);
    
    dist = sqrt(sum((p1-p2).^2));
    disp(['dist= ', num2str(dist), ' meters']);
    
   if (pflag)
      close(t);
      pflag=0;
      break;
   end
   pause(0.0);
end

