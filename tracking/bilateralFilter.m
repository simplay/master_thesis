function [ out ] = bilateralFilter( img, sigma_s, sigma_r)
%BFILT bilateral filter applied on img using sigma_s, sigma_r.
%   img image, spatial, range deviation sigma_s, sigma_r

    h = waitbar(0, 'Applying Bilateral Filter...');
    set(h, 'Name', 'Bilateral Filter Progress Bar');
    
    % compute window size - depends on sigma_s
    w = ceil(1.5*sigma_s);
    windowLength = 2*w + 1;
    [m, n, p] = size(img);
    out = zeros(m,n);
    %%%
    tic;
    % for each pixel compute its new color value
    for i = 1:m,
        for j = 1:n,
            
            % get neighborhood indices - depends on window size
            [rowIndices, columnIndices] = getRanges(i, j, m, n, windowLength);
            
            % get neighborhood colour values
            neighboordhoodValues = img(rowIndices, columnIndices);
            DeltaNValues = (neighboordhoodValues-img(i,j));
            DeltaNValues = (DeltaNValues.^2) /(-2*sigma_r*sigma_r);
            
            deltaNIdx = getScaledIdxDistanceMat2(rowIndices, columnIndices, ...
                                                [i,j], -2*sigma_s^2);
            
            % compute color weights
            EV = exp(DeltaNValues+deltaNIdx);
            
            % relatively scale according to weight
            out(i,j) = (EV(:)'*neighboordhoodValues(:))/sum(EV(:));
        end
        waitbar(i/m)
    end
    close(h);
    toc
end



