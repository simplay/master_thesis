function [ unreliables ] = consistency_check( fw_flow, bw_flow )
%UNTITLED Summary of this function goes here
%   Detailed explanation goes here

    [m,n] = size(fw_flow);
    
    % extract forward u,v flow
    fw_u_flow = fw_flow(:,:,1);
    fw_v_flow = fw_flow(:,:,2);
    
    % extract backward u,v flow
    bw_u_flow = bw_flow(:,:,1);
    bw_v_flow = bw_flow(:,:,2);
    
    % compute |du|^2 + |dv|^2 from fw flow.
    d_fw_u_flow = mat2gradfield(fw_u_flow);
    d_fw_v_flow = mat2gradfield(fw_v_flow); 
    d_fw_flow_nsq = (d_fw_u_flow.^2+d_fw_v_flow.^2);
   
    unreliables = zeros(m,n);
    
    for ax=1:m,
        for ay=1:n,
            
            % coordinate at frame t+1
            % is subpixel accurate since.
            bx = ax + fw_u_flow(ax,ay);
            by = ay + fw_v_flow(ax,ay);
            
            
            % get prev. and next index from subpixel accurate index value.
            x1 = floor(bx);
            y1 = floor(by);
            x2 = x1+1;
            y2 = y1+1;
            
            % sanity check
            if (x1 < 0 || x2 >= m || y1 < 0 || y2 >= n)
                unreliables(ax,ay) = 1.0;
                continue;
            end
            
            alpha_x = bx-x1;
            alpha_y = by-y1;
            
            % bilinar interpolation of flow making use of the backward flow
            a = (1.0-alpha_x)*bw_u_flow(x1,y1) + alpha_x*bw_u_flow(x2,y1);
            b = (1.0-alpha_x)*bw_u_flow(x1,y2) + alpha_x*bw_u_flow(x2,y2);
            u = (1.0-alpha_y)*a + alpha_y*b;
            
            a = (1.0-alpha_x)*bw_flow_v(x1,y1) + alpha_x*bw_v_flow(x2,y1);
            b = (1.0-alpha_x)*bw_flow_v(x1,y2) + alpha_x*bw_v_flow(x2,y2);
            v = (1.0-alpha_y)*a + alpha_y*b;
            
            cx = bx + u;
            cy = by + v;
            u2 = fw_u_flow(ax,ay);
            v2 = fw_v_flow(ax,ay);
            
            % |w-w_t| <= ...
            if (cx-ax)*(cx-ax)+(cy-ay)*(cy-ay) >= 0.01*(u2*u2+v2*v2+u*u+v*v)+0.5
                unreliables(ax,ay) = 1.0; 
                continue;
            end
            
            
            if d_fw_flow_nsq(ax,ay) > 0.01*(u2*u2+v2*v2)+0.002
                unreliables(ax,ay) = 1.0; 
                continue;
            end
            
        end
    end

end

