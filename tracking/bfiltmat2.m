function [ out ] = bfiltmat2( img, sigma_s, sigma_r )
%BFILTIMG2 Summary of this function goes here
%   Detailed explanation goes here

%BFILT Summary of this function goes here
%   Detailed explanation goes here
    h = waitbar(0, 'Applying Bilateral Filter...');
    set(h, 'Name', 'Bilateral Filter Progress Bar');
    w = ceil(1.5*sigma_s);
    windowLength = 2*w + 1;
    [m, n, ~] = size(img);
    out1 = zeros(m,n);
    out2 = zeros(m,n);
    %%%
    tic;
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
            idx = find(rowIndices == i);
            idj = find(columnIndices == j);
            EV1(idx,idj) = 0;
            EV2(idx,idj) = 0;
            
            
            out1(i,j) = (EV1(:)'*neighboordhoodValues1(:))/sum(EV1(:));
            out2(i,j) = (EV2(:)'*neighboordhoodValues2(:))/sum(EV2(:));
        end
        waitbar(i/m)
    end
    close(h);
    toc
    
    out = mat2img(out1, out2, out2);
    out = out(:,:,1:2);
end

