function [ pixel_trackings, trackable_pixels, invalid_regions, disoccluded_regions ] = perform_tracking_step( img, foreward_flow, backward_flow, step_size, start_mask, tracked_to_positions, prev_tacked_pixels)

    fw_u_flow = foreward_flow(:,:,2);
    fw_v_flow = foreward_flow(:,:,1);
    
    
    bw_u_flow = backward_flow(:,:,2);
    bw_v_flow = backward_flow(:,:,1);
    
    % candidate_indices logical mxn matrix that indicates/hints pixels 
    % that depict good trackable candidates.
    [ tracking_candidates ] = findTrackingCandidates( img, step_size );
    
    % start new tracks only for disoccluded regions.
    % initially, all regions are supposed to be disoccluded.
    tc_before_debug = tracking_candidates;
    tracking_candidates = tracking_candidates.*start_mask;
    
    % Check consistency of forward flow via backward flow: See paper
    [ invalid_regions ] = consistency_check( foreward_flow, backward_flow );
    trackable_pixels = tracking_candidates .* (1-invalid_regions);
    %keyboard;
    % tracking step
    pixel_trackings_from_disocclusion = track_points(trackable_pixels, fw_u_flow, fw_v_flow, false, prev_tacked_pixels, bw_u_flow, bw_v_flow);
    % similar as tracking step but for prev.trackings.
    pixel_trackings_from_known = track_points(tracked_to_positions, fw_u_flow, fw_v_flow, true, prev_tacked_pixels, bw_u_flow, bw_v_flow);
    
    %% no overlapping pixels
    % TODO: simplify this
    same_tracked_to_pixels = pixel_trackings_from_disocclusion(:,:,1).*pixel_trackings_from_known(:,:,1);
    same_tracked_to_pixels = 1 - same_tracked_to_pixels;
    pixel_trackings_from_disocclusion(:,:,1) = pixel_trackings_from_disocclusion(:,:,1).*same_tracked_to_pixels;
    pixel_trackings_from_disocclusion(:,:,2) = pixel_trackings_from_disocclusion(:,:,2).*same_tracked_to_pixels;
    pixel_trackings_from_disocclusion(:,:,3) = pixel_trackings_from_disocclusion(:,:,3).*same_tracked_to_pixels;
    pixel_trackings_from_disocclusion(:,:,4) = pixel_trackings_from_disocclusion(:,:,4).*same_tracked_to_pixels;
    pixel_trackings_from_disocclusion(:,:,5) = pixel_trackings_from_disocclusion(:,:,5).*same_tracked_to_pixels;
    
    

    
    pixel_trackings = pixel_trackings_from_disocclusion + pixel_trackings_from_known;

    % OLD % pixel_trackings = pixel_trackings_from_disocclusion;
    
    disoccluded_regions = (1-pixel_trackings(:,:,1));
end