function [ output_args ] = bilinear_interpolate_flow( input_args )
%BILINEAR_INTERPOLATE_FLOW Summary of this function goes here
%   Detailed explanation goes here

            a = (1.0-alpha_x)*bw_u_flow(x1,y1) + alpha_x*bw_u_flow(x2,y1);
            b = (1.0-alpha_x)*bw_u_flow(x1,y2) + alpha_x*bw_u_flow(x2,y2);
            u = (1.0-alpha_y)*a + alpha_y*b;
            
            a = (1.0-alpha_x)*bw_v_flow(x1,y1) + alpha_x*bw_v_flow(x2,y1);
            b = (1.0-alpha_x)*bw_v_flow(x1,y2) + alpha_x*bw_v_flow(x2,y2);
            v = (1.0-alpha_y)*a + alpha_y*b;
            
            cx = bx + u;
            cy = by + v;
end

