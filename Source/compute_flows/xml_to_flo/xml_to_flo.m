function xml_to_flo(dataset)
%XML_TO_FLO Summary of this function goes here
%   Detailed explanation goes here
% dataset = 'dummy';
    addpath('util');
    addpath('../../libs/flow-code-matlab');
    addpath('../../matlab_shared');

    in_path = strcat('../srsf/output/', dataset, '/');
    out_path = strcat('../../../Data/',dataset, '/srsf/');

    filenames = dir(strcat(in_path, '*.xml'));
    for k=1:length(filenames)
        disp(strcat('Iteration: ', num2str(k)))
        fname = filenames(k).name;
        generate_flow(in_path, fname, out_path );
    end
end

