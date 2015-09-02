function [img_yuv] = rgb2yuv(img_rgb)
%RGB2YUV convertes an RGB image into a YUV image.
%   @param img_rgb is supposed to be a mxnx3 rgb image.
%   @return img_rgb is a mxnx3 yuv image.

    disp('rgb2yuv was called');
    
    % transformation matrix from rgb to yuv
    rgb2yuv_transform = [0.299 0.587 0.114; 
                        -0.14713 -0.28886 0.436; 
                         0.615 -0.51499 -0.10001];
    
    % apply color-space transformation.
    img_yuv = transformImg3(img_rgb, rgb2yuv_transform);    
end

