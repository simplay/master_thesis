function write_flow_data(data, idx, output_dir)
%WRITE_FLOW_DATA Summary of this function goes here
%   Detailed explanation goes here

    [m,n,k] = size(data);
    
    % Serialized column-wise
    % Eg. Tensor T in D^(2x2x3):
    %
    %     T(:,:,1) = [1 4; 7 10];
    %     T(:,:,2) = [2 5; 8 11];
    %     T(:,:,3) = [3 6; 9 12];
    %     reshape(T, [4,3])
    % 
    %     ans =
    % 
    %          1     2     3
    %          7     8     9
    %          4     5     6
    %         10    11    12
    D = reshape(data, [m*n,k]);
    
    fName = strcat('../output/trackings/',output_dir,'tracking_t_',num2str(idx),'.txt');
    fid = fopen(fName,'w');
    if fid ~= -1
        for k=1:m*n
         row_k = D(k,:);

         % print only tracked pixels locations
         if row_k(1) ~= 0
            fprintf(fid,'%s\r\n',mat2str(row_k));
         end
        end    
        fclose(fid);
    end

end

