function [ out, weights_u, weights_v ] = bfiltmat2( img, sigma_s, sigma_r, remove_center)
%BFILTIMG2 apply a bilateral filter o a m x n x 2 tensor.
%   @param img flow field, m x n x 2 tensor
%   @param sigma_s special std [FLOAT], unit in pixel (pixel neighborhood)
%   @param sigma_r response std [FLOAT], unit in pixel (flow tolerance)
%   @param remove_center should we zerofy the center value, 
%       addresses peak-issues.
%
%   @return out bilateral filtered flow field
%   @return weights_u bilateral filter weights for u-direction
%   @return weights_v bilateral filter weights for v-direction

    h = waitbar(0, 'Applying Bilateral Filter...');
    set(h, 'Name', 'Bilateral Filter Progress Bar');
    w = ceil(1.5*sigma_s);
    windowLength = 2*w + 1;
    [m, n, ~] = size(img);
    out1 = zeros(m,n);
    out2 = zeros(m,n);
    wl = windowLength+1;
    weights_u = zeros(wl, wl, m*n);
    weights_v = zeros(wl, wl, m*n);
    tic;
    w_idx = 1;
    for i = 1:m,
        for j = 1:n,
            
            [rowIndices, columnIndices] = getRanges(i, j, m, n, windowLength);
            
            neighboordhoodValues1 = img(rowIndices, columnIndices, 1);
            DeltaNValues1 = (neighboordhoodValues1-img(i,j, 1));
            DeltaNValues1 = (DeltaNValues1.^2) /(-2*sigma_r*sigma_r);
            
            neighboordhoodValues2 = img(rowIndices, columnIndices, 2);
            DeltaNValues2 = (neighboordhoodValues2-img(i,j, 2));
            DeltaNValues2 = (DeltaNValues2.^2) /(-2*sigma_r*sigma_r);
            
            
            
            deltaNIdx = getScaledIdxDistanceMat2(rowIndices, columnIndices, ...
                                                [i,j], -2*sigma_s^2);
            
            EV1 = exp(DeltaNValues1+deltaNIdx);
            EV2 = exp(DeltaNValues2+deltaNIdx);
            
            % set ourself to zero: fix
            if remove_center
                idx = find(rowIndices == i);
                idj = find(columnIndices == j);
                EV1(idx,idj) = 0;
                EV2(idx,idj) = 0;
            end
            
            out1(i,j) = (EV1(:)'*neighboordhoodValues1(:))/sum(EV1(:));
            out2(i,j) = (EV2(:)'*neighboordhoodValues2(:))/sum(EV2(:));
            
            % use a fixed sized weight-neighborhood from 1 to wl
            weights_u(:,:,w_idx) = EV2(1:wl, 1:wl);
            weights_v(:,:,w_idx) = EV1(1:wl, 1:wl);
            w_idx = w_idx + 1;
        end
        waitbar(i/m)
    end
    close(h);
    toc
    
    out = mat2img(out1, out2, out2);
    out = out(:,:,1:2);
end

