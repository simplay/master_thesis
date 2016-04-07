function images = readAnimationDataset(dataset_path, img_file_ext)
%READLIGHTFIELDDATA reads a coherent dataset, stored as a series of images
%into a image tensor. Order of images is flipped.
%   @param dataset_path String user specified path to a coherent image
%       dataset. Images are assumed to be a series of S (M x N) 
%       (default: .jpg) images.
%   @param img_file_ext: Custom image file extension. Is an optional
%       argument and by default it is assumed that we are dealing with
%       .jpg images.
%   @return an (M x N x 3 x S) tensor containing the image sequence.

    if nargin == 2
        directory = dataset_path;
        ext = img_file_ext;
    elseif nargin == 1
        directory = dataset_path;
        ext = 'png';
    else
        directory = uigetdir;
        ext = 'jpg';
    end
    
    % check whether a directory is specified.
    if ~(directory == 0)
         % get the names of all image files
         searchFor = strcat(directory, '/*.', ext);
         dirListing = dir(searchFor);
         
         % get number of images
         numFrames = length(dirListing);
         
         % open images and store it in array
         % first image
         % use full path because the folder may not be the active path
         fileName = fullfile(directory, dirListing(1).name);
         sentinelImg = imread(fileName);
         [M, N, C] = size(sentinelImg);
         images = zeros(M,N,C,numFrames);
         
         for k=1:numFrames
             fileName = fullfile(directory, dirListing(k).name);
             img = im2double(imread(fileName));
             idx = numFrames-k+1;
             images(:,:,:,idx) = img;
         end
         
    end    
end
