function [ myColor ] = evc_to_color( f )
%EVV_TO_COLOR Summary of this function goes here
%   Detailed explanation goes here

    cm = colormap; % returns the current color map

    colorID = max(1, sum(f > [0:1/length(cm(:,1)):1])); 

    myColor = cm(colorID, :); % returns your colo
    %close
end

