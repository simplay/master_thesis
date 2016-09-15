addpath('../libs/flow-code-matlab');
addpath('../matlab_shared'); 
DATASETNAME1 = 'foo';
DATASETNAME2 = 'foo';
METHODNAME = 'ldof';
index = 1;

DATASET = strcat(DATASETNAME1,'/');
BASE_FILE_PATH = strcat('../../Data/', DATASET);
[boundaries, imgs, fwf, bwf] = read_metadata(BASE_FILE_PATH, METHODNAME);
fflow = readFlowFile(fwf{index});
displayFlow(fflow);

DATASET = strcat(DATASETNAME2,'/');
BASE_FILE_PATH = strcat('../../Data/', DATASET);
[boundaries2, imgs2, fwf2, bwf2] = read_metadata(BASE_FILE_PATH, METHODNAME);
fflow2 = readFlowFile(fwf2{index});
displayFlow(fflow2);

displayFlow(fflow2-fflow);