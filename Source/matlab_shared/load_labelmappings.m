function [ label_mapping ] = load_labelmappings(dataset)
%LABELFILE2MAT read label to affinity matrix indices mapping file.
%   returns a list of labels. The label position (its row) in the returned
%   vector corresponds to its matrix index (in the affinity matrix W).

    fname = strcat('../output/similarities/',dataset, '_labels.txt');
    label_mapping = textread(fname)';

end

