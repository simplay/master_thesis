% adapted harris
% HARRIS - Harris corner detector
%
% Usage:  [cim, r, c] = harris(im, sigma, thresh, radius, disp)
%
% Arguments:   
%            im     - image to be processed.
%            sigma  - standard deviation of smoothing Gaussian. Typical
%                     values to use might be 1-3.
%            thresh - threshold (optional). Try a value ~1000.
%            radius - radius of region considered in non-maximal
%                     suppression (optional). Typical values to use might
%                     be 1-3.
%
% Returns:
%            cim    - binary image marking corners.
%            r      - row coordinates of corner points.
%            c      - column coordinates of corner points.
%
% If thresh and radius are omitted from the argument list 'cim' is returned
% as a raw corner strength image and r and c are returned empty.

% Reference: 
% C.G. Harris and M.J. Stephens. "A combined corner and edge detector", 
% Proceedings Fourth Alvey Vision Conference, Manchester.
% pp 147-151, 1988.
%
% Author: 
% Peter Kovesi   
% Department of Computer Science & Software Engineering
% The University of Western Australia
% pk@cs.uwa.edu.au  www.cs.uwa.edu.au/~pk
%
% March 2002

function [cim, r, c] = harris3d(im, sigma, thresh, radius)
    
    error(nargchk(2,5,nargin));
    thresh = 0.5137;
    [cim_r] = harris1d(im(:,:,1), sigma, thresh, radius);
    [cim_g] = harris1d(im(:,:,2), sigma, thresh, radius);
    [cim_b] = harris1d(im(:,:,3), sigma, thresh, radius);
    
    cim = cim_r+cim_g+cim_b;
    
        if nargin > 2   % We should perform nonmaximal suppression and threshold
	
        % Extract local maxima by performing a grey scale morphological
        % dilation and then finding points in the corner strength image that
        % match the dilated image and are also greater than the threshold.
        sze = 2*radius+1;                   % Size of mask.
        mx = ordfilt2(cim,sze^2,ones(sze)); % Grey-scale dilate.
        mask = (cim==mx)&(cim>thresh);       % Find maxima.

        [r,c] = find(mask);                  % Find row,col coords.
        end
end


function [cim] = harris1d(im, sigma, thresh, radius)
    
    error(nargchk(2,5,nargin));
    
    dx = [-1 0 1; -1 0 1; -1 0 1]; % Derivative masks
    dy = dx';
    
    Ix = conv2(im, dx, 'same');    % Image derivatives
    Iy = conv2(im, dy, 'same');    

    % Generate Gaussian filter of size 6*sigma (+/- 3sigma) and of
    % minimum size 1x1.
    g = fspecial('gaussian',max(1,fix(6*sigma)), sigma);
    
    Ix2 = conv2(Ix.^2, g, 'same'); % Smoothed squared image derivatives
    Iy2 = conv2(Iy.^2, g, 'same');
    Ixy = conv2(Ix.*Iy, g, 'same');
    
    cim = (Ix2.*Iy2 - Ixy.^2)./(Ix2 + Iy2 + eps); % Harris corner measure

    % Alternate Harris corner measure used by some.  Suggested that
    % k=0.04 - I find this a bit arbitrary and unsatisfactory.
%   cim = (Ix2.*Iy2 - Ixy.^2) - k*(Ix2 + Iy2).^2; 


end
