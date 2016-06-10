% Convex Optimization - Project 2
% MICHAEL SINGLE
% 08-917-445
function [demosaicedImg] = demosaicing(mosaiced, Omega, lambda, iterations, input, verbose)
% DEMOSAIC demosaices a given mosaiced image by solving a convex opt.
%          problem relying on primal dual solver 
%          and a fixed numer of iteration.
% for further information please have a look at section 4 or the complete
% report of project 2.
%
% @author: MICHAEL SINGLE
%          08-917-445
%
% @param mosaiced M x N x 3 Color Image encoded as a mosaiced image
%        according to bayer filter theory.
% @param Omega bayer filter tensor M x N x 3
%        Omega(:,:,1) is RED color mask
%        Omega(:,:,2) is GREEN color mask
%        Omega(:,:,3) is BLUE color mask
% @param lambda [Float] regularization factor to control exactness of result and the smoothness 
%        when solving the demosaicing convex minimization problem.
% @param iterations [Integer] number of iterations
% @return demosaicedImg M x N x 3 Color Image.

    eps = 1e-6;

    % parameter
    K_a = sqrt(4);
    tau = 10*0.2*1e-3; 
    sigma = 1/(tau*K_a);
    if(verbose) disp(['tau*sigma=',num2str(tau*sigma)]); end
    theta = 0.5;

    % initial guesses 
    Rx_n = mosaiced(:,:,1); 
    Gx_n = mosaiced(:,:,2);
    Bx_n = mosaiced(:,:,3);

    Ry_n = zeros([size(Rx_n), 2]);
    Gy_n = zeros([size(Gx_n), 2]);
    By_n = zeros([size(Bx_n), 2]);
    
    Rx_tilde_n = Rx_n;
    Gx_tilde_n = Gx_n;
    Bx_tilde_n = Bx_n;
    if(verbose) figure; end
    
    h = waitbar(0,'Progress Gradient Descend');
    for i = 1:iterations
       waitbar(i/iterations) 
       before_r_x = Rx_n;
       before_g_x = Gx_n;
       before_b_x = Bx_n;
       
       before_r_y = Ry_n;
       before_g_y = Gy_n;
       before_b_y = By_n;
       
       before_r_x_ = Rx_tilde_n;
       before_g_x_ = Gx_tilde_n;
       before_b_x_ = Bx_tilde_n;
       E_u_R_prev = energy_term_for(Rx_tilde_n, mosaiced(:,:,1), Omega(:,:,1), lambda);
       
       [Ry_n, Rx_n, Rx_tilde_n] = primal_dual_solver(before_r_y, before_r_x, before_r_x_, mosaiced(:,:,1), Omega(:,:,1), lambda, tau, sigma, theta);
       [Gy_n, Gx_n, Gx_tilde_n] = primal_dual_solver(before_g_y, before_g_x, before_g_x_, mosaiced(:,:,2), Omega(:,:,2), lambda, tau, sigma, theta);
       [By_n, Bx_n, Bx_tilde_n] = primal_dual_solver(before_b_y, before_b_x, before_b_x_, mosaiced(:,:,3), Omega(:,:,3), lambda, tau, sigma, theta);
       
       
       
       E_u_R = energy_term_for(Rx_tilde_n, mosaiced(:,:,1), Omega(:,:,1), lambda);
       if verbose
           %plot(i,log(norm(E_u_R(:)-E_u_R_prev(:))+eps),'.');
           plot(i,norm(E_u_R(:)),'.');
           hold on;
           %imagesc(Rx_n);
           drawnow
       end
    end
    close(h); 
    demosaicedImg = mat2Img(Rx_tilde_n, Gx_tilde_n, Bx_tilde_n);
    %demosaicedImg = mat2Img(Rx_tilde_n, Rx_tilde_n, Rx_tilde_n);
end

function E_u = energy_term_for(u, g, omega, lambda)
% See section 1 of the report.
    similarity = (u-g).*omega;
    smoothness = grad_of(u, 'fwd');
    l2 = @(f) sqrt(f(:,:,1).^2 + f(:,:,2).^2);
    E_u = (lambda*0.5)*norm(similarity(:)).^2 + l2(smoothness);
end

function [y_n_p_1, x_n_p_1, x_tilde_n_p_1] = primal_dual_solver(y_n, x_n, x_tilde_n, g, Omega, lambda, tau, sigma, theta)
% Primal-Dual Convex solver to find an optimum for any masked, convex optimization
% problem relying on a l2 regularization.
% In this work used to solve a demosaicing formulation.
%
% For further information please refer to the delivered report.
% See section 3 of the report.
%
% @param y_n current partial y iterative solution of primal dual formulation of dim. MxNx2.
% @param x_n current partial x iterative solution of primal dual formulation of dim. MxN.
% @param x_tilde_n current iterative solution of primal dual formulation of dim. MxN.
% @param g initial mosaiced image of color channel corresponding to given u.
% @param Omega bayer filter mask M x N for a certain color channel
% @param lambda [Float] regularization factor to control exactness of result and the smoothness 
%        when solving the demosaicing convex minimization problem.
% @param tau x regularization parameter.
% @param sigma y regularization parameter.
% @param d_tilde descend regularization theta
% @return y_n_p_1 iteration of color channel (current gradient field from previous x solution).
% @return x_n_p_1 iteration of color channel (partial, uncombined x solution).
% @return x_tilde_n_p_1 current approximated colorchannel solution (demosaiced chnnel).
	
    y_n_p_1 = y_n_plus_1_for(y_n, x_tilde_n, sigma);
    x_n_p_1 = x_n_plus_1_for(x_n, y_n_p_1, lambda, tau, Omega, g);
    x_tilde_n_p_1 = x_tilde_n_plus_1_for(x_n_p_1, x_n, theta);
end

function y_n_p_1 = y_n_plus_1_for(y_n, x_tilde, sigma)
% See section 3.1 of the report.
    grad_tilde_x = grad_of(x_tilde, 'fwd');
    y_nominator = y_n - sigma*grad_tilde_x;
    norm_y_nominator = sqrt(y_nominator(:,:,1).^2 + y_nominator(:,:,2).^2);
    y_denominator = max(1, norm_y_nominator);

    y_n_p_1 = zeros(size(y_n));
    y_n_p_1(:,:,1) = y_nominator(:,:,1)./y_denominator;
    y_n_p_1(:,:,2) = y_nominator(:,:,2)./y_denominator;
end

function x_n_p_1 = x_n_plus_1_for(x_n, y_n_p_1, lambda, tau, Omega, g)
% See section 3.2 of the report.
    div_y_n_p_1 = div_of(y_n_p_1);
    x_n_p_1_nominator = x_n - tau*div_y_n_p_1 + tau*lambda*(Omega.*g);
    x_n_p_1_denominator = 1 + tau*lambda*Omega;
    x_n_p_1 = x_n_p_1_nominator./x_n_p_1_denominator;
end

function x_tilde_n_p_1 = x_tilde_n_plus_1_for(x_n_p_1, x_n, theta)
% See section 3 of the report.
    x_tilde_n_p_1 = x_n_p_1 + theta*(x_n_p_1 - x_n);
end

function grad_f = grad_of(f, type)
% compute discrete gradient of a 2-dim function f
% relying on a certain finite difference approximation scheme.
% See section 4 of the report.
% @param f 2d function (values) encoded as a MxN matrix
% @param type String type of finite difference approx. schemme:
%        type == 'fwd' for a foreward diff. scheme
%        type == 'bwd' for a backward diff. scheme
%        type == 'cd' for a central diff. scheme
% @return grad_f MxNx2 matrix storing [df/dx, df/dy].

    grad_f = zeros([size(f), 2]);
    if (strcmp(type,'fwd') == 1)
        % foreward differences   
        df_dx = f([2:end, end],:)-f;
        df_dy = f(:,[2:end, end])-f;
        
    elseif(strcmp(type,'bwd') == 1)
        % backward differences
        df_dx = f-f([1,1:end-1],:);
        df_dy = f-f(:,[1,1:end-1]);
        
    elseif (strcmp(type,'cd') == 1)
        %central differences
        df_dx = ( f([2:end,end],:) - f([1,1:end-1],:) )/2;
        df_dy = ( f(:,[2:end,end]) - f(:,[1,1:end-1]) )/2;
    end    
    
    grad_f(:,:,1) = df_dx;
    grad_f(:,:,2) = df_dy;
end

function div_v = div_of(v)
% compute divergence of a given 2d-vectorfield v
% relying on a backward difference approximation scheme
% in order to avoid gradient shifting issues (assuming the vector
% components of v were generated by using a foreward difference
% approximation scheme).
% See section 4 of the report.
% @param v 2d vectorfield v (computed by foreward differences).
% @return divergence for every vector of v (a scalar field).

   grad_v_x = grad_of(v(:,:,1), 'bwd');
   grad_v_y = grad_of(v(:,:,2), 'bwd');
   div_v = grad_v_x(:,:,1) + grad_v_y(:,:,2);
end
