function [ pixel_trackings, trackable_pixels, invalid_regions, disoccluded_regions ] = perform_tracking_step( img, foreward_flow, backward_flow, step_size, start_mask, tracked_to_positions )

    fw_u_flow = foreward_flow(:,:,2);
    fw_v_flow = foreward_flow(:,:,1);
    
    % candidate_indices logical mxn matrix that indicates/hints pixels 
    % that depict good trackable candidates.
    [ tracking_candidates ] = findTrackingCandidates( img, step_size );
    
    % start new tracks only for disoccluded regions.
    % initially, all regions are supposed to be disoccluded.
    tracking_candidates = tracking_candidates.*start_mask;
    
    % Check consistency of forward flow via backward flow: See paper
    [ invalid_regions ] = consistency_check( foreward_flow, backward_flow );
    trackable_pixels = tracking_candidates .* (1-invalid_regions);

    % tracking step
    pixel_trackings_from_disocclusion = track_points(trackable_pixels, fw_u_flow, fw_v_flow);
    % similar as tracking step but for prev.trackings.
    pixel_trackings_from_known = track_points(tracked_to_positions, fw_u_flow, fw_v_flow);
    
    % pixel_trackings = pixel_trackings_from_disocclusion + pixel_trackings_from_known;
    pixel_trackings = pixel_trackings_from_disocclusion;
    
    disoccluded_regions = (1-pixel_trackings(:,:,1));
end