function [ pixel_trackings, trackable_pixels, invalid_regions ] = perform_tracking_step( img, foreward_flow, backward_flow, step_size )

    fw_u_flow = foreward_flow(:,:,2);
    fw_v_flow = foreward_flow(:,:,1);
    
    % candidate_indices logical mxn matrix that indicates/hints pixels 
    % that depict good trackable candidates.
    [ tracking_candidates ] = findTrackingCandidates( img, step_size );
    
    % Check consistency of forward flow via backward flow: See paper
    [ invalid_regions ] = consistency_check( foreward_flow, backward_flow );
    trackable_pixels = tracking_candidates .* (1-invalid_regions);

    % tracking step
    pixel_trackings = track_points(trackable_pixels, fw_u_flow, fw_v_flow);

end