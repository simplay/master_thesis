addpath('util');
addpath('../libs/flow-code-matlab');

dataset = 'chair3/';
in_path = strcat('input/',dataset);
out_path = strcat('output/',dataset);

filenames = dir(strcat('input/', dataset, '*.xml'));
for k=1:length(filenames)
    disp(strcat('Iteration: ', num2str(k)))
    fname = filenames(k).name;
    generate_flow(in_path, fname, out_path );
end


