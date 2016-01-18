function displayFlow(flowfield, tx, ty)
%DISPLAYFLOW display normalized flow field directions u,v
    figure('name', 'v flow direction')
    imshow(normalize(flowfield(:,:,1)));
    hold on
    plot(ty,tx,'Color',[1,0,0],'Marker','O')
    figure('name', 'u flow direction')
    imshow(normalize(flowfield(:,:,2)));
    hold on
    plot(ty,tx,'Color',[1,0,0],'Marker','O')
end

