classdef Track
    properties
        points = [];
        label;
    end

    methods
        function this = Track(label)
            this.label = label;
        end

        function this = appendPoint(this, point)
            % loads the property
            this.points = [this.points, point];
        end
        
    end 
end