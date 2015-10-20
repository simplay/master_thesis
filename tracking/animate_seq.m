function animate_seq(imgs, from_idx, to_idx)
%ANIMATE_SEQ animate a given sequence of image within a given index
%boundary.

    numFrames = to_idx-from_idx+1;
    frames = repmat(struct('cdata', 1, 'colormap', 2), numFrames, 1);
    for k=from_idx:to_idx
        frames(k) = im2frame(imread(imgs{k}));
    end
    implay(frames);
end