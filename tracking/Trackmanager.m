classdef Trackmanager
    %TRACKMANAGER Summary of this class goes here
    %   Detailed explanation goes here
    
    properties
        tracks
    end
    
    methods
        
        function this = Trackmanager()
            this.tracks = [];
        end
        
        function this = appendTrack(this, track)
            if size(this.tracks, 1) == 0
                this.tracks = [track];
            else
                this.tracks = [this.tracks.tracks; track];
            end
             % necessary, otherwise state of object will not be overridden.
        end
        
        % find a track in this Trackmanager's tracks list
        % having a given label assigned.
        %
        % @param label [Integer] label id of target track
        % @return [Track] track having the given label. If
        %   no such track is contained in this Trackmanager's track
        %   list, return the value -1.
        function sol = findTrack(this, label)
            sol = -1;
            for k=1:length(this.tracks)
                current = this.tracks.tracks(k);
                if current.label == label
                    sol = current;
                    break;
                end
            end
        end
    end
    
end

