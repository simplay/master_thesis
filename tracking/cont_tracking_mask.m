function save_from_oversampling_mask = cont_tracking_mask(tracked_to_positions, step_size)
%CONT_TRACKING_MASK Summary of this function goes here
%   Detailed explanation goes here
[m,n] = size(tracked_to_positions);
    [tracked_row, tracked_col, ~] = find(tracked_to_positions > 0);
    save_from_oversampling_mask = ones(m,n);
    mask_step = floor(step_size/2);
    for k=1:length(tracked_row)
        row_idx = tracked_row(k);
        col_idx = tracked_col(k);
        
        m_step1 = row_idx - mask_step;
        if m_step1 < 1
           m_step1 = 1; 
        end
        
        m_step2 = row_idx+mask_step;
        if m_step2 > m
            m_step2 = m;
        end
        
        n_step1 = col_idx - mask_step;
        if n_step1 < 1
           n_step1 = 1; 
        end
        
        n_step2 = col_idx+mask_step;
        if n_step2 > n
            n_step2 = n;
        end

        save_from_oversampling_mask(m_step1:m_step2, n_step1:n_step2) = 0;
    end

end

