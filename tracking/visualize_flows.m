addpath('../libs/flow-code-matlab');
addpath('fast_bfilt/');   
DATASETNAME1 = 'square';
DATASETNAME2 = 'car2';
METHODNAME = 'ldof';
index = 1;

DATASET = strcat(DATASETNAME1,'/');
BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASET);
[boundaries, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH);
fflow = readFlowFile(fwf{index});
displayFlow(fflow);

DATASET = strcat(DATASETNAME2,'/');
BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASET);
[boundaries2, imgs2, fwf2, bwf2] = read_metadata(BASE_FILE_PATH);
fflow2 = readFlowFile(fwf2{index});
displayFlow(fflow2);

displayFlow(fflow2-fflow);