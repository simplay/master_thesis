function [ pixel_trackings, trackable_pixels, invalid_regions ] = perform_tracking_step( img, foreward_flow, backward_flow )

    sigma = 1;
    thresh_scale = 0.3;
    variant = 1;
    step_size = 4;
    
    [m,n,~] = size(img);

    fw_u_flow = foreward_flow(:,:,2);
    fw_v_flow = foreward_flow(:,:,1);
    
    % bw_u_flow = backward_flow(:,:,1);
    % bw_v_flow = backward_flow(:,:,2);

    % The value at pixel location (i,j) indicates the corresponding tracking
    % label.
    % Default tracking label for every pixel is the value 0.
    % Therefore every valid label is supposed to be grater than 0
    % TODO: add another dimension for time steps
    % 3rd dimension denotes has track finished
    % track is supposed to be finished IFF label is > 0 AND
    % tracked_pixels(i,j,2) is 1
    pixel_trackings = zeros(m,n,2);

    % candidate_indices logical mxn matrix that indicates/hints pixels 
    % that depict good trackable candidates.
    %[~, candidate_indices] = find_tracking_candidates(img, sigma, thresh_scale, variant);
    [ tracking_candidates ] = findTrackingCandidates( img, step_size );
    % Check consistency of forward flow via backward flow: See paper
    % [ invalid_regions ] = flow_sanity_check( foreward_flow, backward_flow );
    [ invalid_regions ] = consistency_check( foreward_flow, backward_flow );
    trackable_pixels = tracking_candidates .* (1-invalid_regions);
    %imshow(mat2img(1-invalid_regions, candidate_indices, zeros(m,n)));
    %imshow(mat2img((1-invalid_regions),(1-invalid_regions),(1-invalid_regions)).*img);
% for ax=1:m,
%     for ay=1:n,
%         
%         
%         if trackable_pixels(ax,ay) == 0
%             
%         else
% 
% 
%             bx = ax + fw_u_flow(ax,ay);
%             by = ay + fw_v_flow(ax,ay);
% 
%             ibx = round(bx);
%             iby = round(by);
% 
%             if (ibx <= 0 || iby <= 0 || ibx > m || iby > n)
%                 % remeber that track has finished here
%                 pixel_trackings(ax, ay, 2) = 1;
%                 continue;
%             else
% 
%                 pixel_trackings(ibx, iby, 1) = 1;
%             end 
%         end
%     end
% end

    %% tracking step
    % only iterate over trackable pixels
    [idx, idy, ~] = find(trackable_pixels == 1);
    for k = 1:length(idx),
        ax = idx(k);
        ay = idy(k);
        
        bx = ax + fw_u_flow(ax,ay);
        by = ay + fw_v_flow(ax,ay);

        ibx = round(bx);
        iby = round(by);

        if (ibx <= 0 || iby <= 0 || ibx > m || iby > n)
            % remeber that track has finished here
            pixel_trackings(ax, ay, 2) = 1;
            continue;
        else
            
            pixel_trackings(ibx, iby, 1) = 1;
        end 
    end

end

