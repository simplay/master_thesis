function displayFlow(flowfield)
%DISPLAYFLOW display normalized flow field directions u,v
    figure('name', 'v flow direction')
    imshow(normalize(flowfield(:,:,1)));
    figure('name', 'u flow direction')
    imshow(normalize(flowfield(:,:,2)));
end

