classdef Point
    %POINT Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        x % from x coordinate
        y % from y coordinate
        x_next % to x coordinate
        y_next % to y coordinate
        u_fw % u component fw flow
        v_fw % v component fw flow
        u_by % u component bw flow
        v_bw % v component bw flow
        
    end
    
    methods
        function obj = Point(x,y, x_n, y_n, )
            obj.x = x;
            obj.y = y;
        end
    end
    
end

