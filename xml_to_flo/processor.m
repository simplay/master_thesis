addpath('util');
addpath('../libs/flow-code-matlab');

in_path = 'input/';
out_path = 'output/';

fname = 'flow_10_11.xml';
identifier = strsplit(fname, '.xml');
D = xml_read(strcat(in_path,fname));
C = D.SFx;
CC = C.data;
modifiedStr = strrep(CC, '0.', '0');
Q = strsplit(modifiedStr);
flowX = reshape(str2num(char(Q)), C.cols, C.rows)';
C = D.SFy;
CC = C.data;
modifiedStr = strrep(CC, '0.', '0');
QQ = strsplit(modifiedStr);
flowY = reshape(str2num(char(QQ)), C.cols, C.rows)';

img = mat2img(flowX, flowY);
out_fname = strcat(out_path,char(identifier(1)),'.flo');
writeFlowFile(img(:,:,1:2), out_fname);
