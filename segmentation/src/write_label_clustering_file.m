function write_label_clustering_file(label_assignments, label_mappings, img_index, dataset)
%WRITE_LABEL_CLUSTERING_FILE create a file with all label cluster assignments
%   a file exhibiting a line per existing label
%   every line is formatted like the following: 
%       LABEL,CLUSTER_VALUE
%   i.e. a it consits of the label value and its cluster assignment,
%   separated by a ','.

    fName = strcat('../output/clustering/',dataset,'_labels_',num2str(img_index),'.txt');
    fid = fopen(fName,'w');
    if fid ~= -1
        for k=1:length(label_assignments)
            label = label_mappings(k);
            label_vector_idx = label_to_vector_index(label_mappings, label);
            cluster_value = label_assignments(label_vector_idx);
            a_line = strcat(num2str(label),',',num2str(cluster_value));
            if a_line ~= 0
                fprintf(fid, '%s\r\n', a_line);
            end
        end
        fclose(fid);
    end

end

