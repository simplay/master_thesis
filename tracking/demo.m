close all;
addpath('../libs/flow-code-matlab');
DATASETNAME = 'cars1';
METHODNAME = 'ldof';
DATASET = strcat(DATASETNAME,'/');
BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASET); 
[boundaries, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH);
fw_flow_t = fwf{1};
fw_flow = readFlowFile(fw_flow_t);
%%
vars = computeLocalFlowVar(fw_flow, 0, 0, 6, 20);
figure('name', 'var raw')
imshow(vars)
colorbar
figure('name', 'normalized')
imshow(normalize(vars))
colorbar