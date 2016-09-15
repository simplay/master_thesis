function generateFlow(fname1, fname2, saveToPath, outFname)
%GENERATEFLOW Summary of this function goes here
%   Detailed explanation goes here
    disp(pwd);
    addpath('../../libs/flow-code-matlab/');
    
    img1 = imread(fname1);
    img2 = imread(fname2);
    flow = estimate_flow_hs(img1, img2);
    outPathName = strcat(saveToPath, outFname, '.flo');
    writeFlowFile(flow, outPathName)
end

