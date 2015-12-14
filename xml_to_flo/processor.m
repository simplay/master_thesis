addpath('util');
addpath('../libs/flow-code-matlab');

dataset = 'foo/';
in_path = strcat('input/',dataset);
out_path = 'output/foo/';

filenames = dir(strcat('input/', dataset, '*.xml'));
for k=1:length(filenames)
    fname = filenames(k).name;
    generate_flow(in_path, fname, out_path );
end


