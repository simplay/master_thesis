function [ frames ] = showDatasetMovie( imageSeq )
%GENERATELIGHTFIELDMOVIE Summary of this function goes here
%   Detailed explanation goes here
    
    [~,~,~,numFrames] = size(imageSeq);
    frames = repmat(struct('cdata', 1, 'colormap', 2), numFrames, 1 );
    for k=1:numFrames
        frames(k) = im2frame(imageSeq(:,:,:,k));
    end
    figure('name', 'Data Animation');
    imshow(imageSeq(:,:,:,1));
    movie(frames);
end
