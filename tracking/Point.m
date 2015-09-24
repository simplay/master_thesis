classdef Point
    %POINT Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        ax  % frame t:   x coordinate
        ay  % frame t:   y coordinate
        bx  % frame t+1: x coordinate
        by  % frame t+1: y coordinate
        ibx % frame t+1: x coordinate (rounded)
        iby % frame t+1: y coordinate (rounded)
    end
    
    methods
        function obj = Point(ax, ay, bx, by, ibx, iby)
            obj.ax = ax;
            obj.ay = ay;
            obj.bx = bx;
            obj.by = by;
            obj.ibx = ibx;
            obj.iby = iby;
        end
    end
    
end

