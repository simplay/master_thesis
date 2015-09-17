classdef Track
    %TRACK Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        label
        points = []
    end
    
    methods
        
        % constructor: assign a label to this track
        function obj = Track(label_id)
            obj.label = label_id;
        end
        
        % adds a point to to_point list and overwrites this instance
        function obj = append_point(obj, point)
            obj.points = [obj.points, point];
            obj; % necessary, otherwise state of object will not be overridden.
        end
        
        function len = length(obj)
            len = length(obj.points);
        end
    end
    
end

