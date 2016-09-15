function [ out ] = bfiltImg3( img, sigma_s, sigma_r )
%BFILTIMG3 Summary of this function goes here
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
    out3 = zeros(m,n);
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
            
            neighboordhoodValues3 = img(rowIndices, columnIndices, 3);
            DeltaNValues3 = (neighboordhoodValues2-img(i,j, 3));
            DeltaNValues3 = (DeltaNValues3.^2) /(-2*sigma_r*sigma_r);
            
            
            deltaNIdx = getScaledIdxDistanceMat2(rowIndices, columnIndices, ...
                                                [i,j], -2*sigma_s^2);
            
            EV1 = exp(DeltaNValues1+deltaNIdx);
            EV2 = exp(DeltaNValues2+deltaNIdx);
            EV3 = exp(DeltaNValues3+deltaNIdx);
            
            
            out1(i,j) = (EV1(:)'*neighboordhoodValues1(:))/sum(EV1(:));
            out2(i,j) = (EV2(:)'*neighboordhoodValues2(:))/sum(EV2(:));
            out3(i,j) = (EV3(:)'*neighboordhoodValues3(:))/sum(EV3(:));
        end
        waitbar(i/m)
    end
    close(h);
    toc
    
    out = zeros(m, n, 3);
    out(:,:,1) = out1;
    out(:,:,2) = out2;
    out(:,:,3) = out3;
end

