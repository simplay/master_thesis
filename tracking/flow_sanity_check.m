function [ mask ] = flow_sanity_check( foreward_flow, backward_flow )
%FLOW_SANITY_CHECK Summary of this function goes here
%   Detailed explanation goes here

    [m,n,~] = size(foreward_flow);

    %Idx = repmat((1:m)', 1, n);
    %Idy = repmat((1:n), m, 1);

    fw_u_flow = foreward_flow(:,:,1);
    fw_v_flow = foreward_flow(:,:,2);

    bw_u_flow = backward_flow(:,:,1);
    bw_v_flow = backward_flow(:,:,2);
    
    mask = zeros(m,n);
    for x_t=1:m,
        for y_t=1:n,
            u_t = fw_u_flow(x_t,y_t);
            v_t = fw_v_flow(x_t,y_t);
            
            idx = round(u_t+x_t);
            idy = round(v_t+y_t);
            
            if idx > 0 && idx <= m && idy > 0 && idy <= n
                %u_del = abs(bw_u_flow(idx,idy) + fw_u_flow(x_t, y_t));
                %v_del = abs(bw_v_flow(idx,idy) + fw_v_flow(x_t, y_t));
                %if u_del < 1 && v_del < 1
                %    mask(x_t, y_t) = 1;
                %end
                
                u_tilde = bw_u_flow(idx,idy);
                u = fw_u_flow(x_t, y_t);
                
                v_tilde = bw_v_flow(idx,idy);
                v = fw_v_flow(x_t, y_t);
                
                w = [u,v];
                w_tilde = [u_tilde, v_tilde];
                
                if norm(w+w_tilde)^2 < 0.01*(norm(w)^2 + norm(w_tilde)^2)+0.5
                    if x_t+1 <= m && y_t+1 <= n
                        u_next_x = fw_u_flow(x_t+1, y_t);
                        u_next_y = fw_u_flow(x_t, y_t+1);
                        v_next_x = fw_v_flow(x_t+1, y_t);
                        v_next_y = fw_v_flow(x_t, y_t+1);
                        
                        grad_u2 = (u-u_next_x)^2 + (u-u_next_y)^2;
                        grad_v2 = (v-v_next_x)^2 + (v-v_next_y)^2;
                        
                        if grad_u2 + grad_v2 <= 0.01*norm(w)^2 + 0.002
                            mask(x_t, y_t) = 1;
                        end
                         
                    end 
                end 
            end 
        end
    end
end

