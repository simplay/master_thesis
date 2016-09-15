FrameCount = 10;
DISPLAY_IMGS = true;
M = 480;
N = 640;

% dimensions square
m = 80;
n = 80;

% zero of square
p0 = [100, 100];
p1 = [300, 200];
stepSize = 6;
H = fspecial('motion',20,45);
for k=1:FrameCount+1
    img = zeros(M,N);
    p0 = p0 + [stepSize, stepSize];
    
    img(p0(1):m+p0(1),p0(2):n+p0(2)) = 1;
    img(p1(1):m+p1(1),p1(2):n+p1(2)) = 1;
    

    
    img = imfilter(img,H,'replicate');
    
    imgName = strcat('square_', num2str(k) );
    
    imwrite(mat2img(img), strcat(imgName,'.png'));
    
    if DISPLAY_IMGS
        figure('name', imgName);
        imshow(img);
    end
end

