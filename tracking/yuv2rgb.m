function [img_rgb] = yuv2rgb(img_yuv)
%RGB2YUV convertes an YUV image into a RGB image.
%   @param img_yuv is supposed to be a mxnx3 yuv image.
%   @return img_rgb is a mxnx3 rgb image.

    % transformation matrix from yuv to rgb
    yuv2rgb_t = [1.0 0.0 1.13983; 
                1.0 -0.39465 -0.58060; 
                1.0 2.03211 0.0];
            
            
    % apply color-space transformation.
    img_rgb = transformImg3(img_yuv, yuv2rgb_t);  
end