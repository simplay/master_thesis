function generate_flow(in_path, fname, out_path)
%GENERATE_FLOW Summary of this function goes here
%   Detailed explanation goes here
    
    
    identifier = strsplit(fname, '.xml');
    xml_flow_file = xml_read(strcat(in_path,fname));

    flow_x = xml_flow_file.SFx;
    flow_data = flow_x.data;
    modifiedStr = strrep(flow_data, '0.', '0');
    parsed_flow = strsplit(modifiedStr);
    flowX = reshape(str2num(char(parsed_flow)), flow_x.cols, flow_x.rows)';

    flow_y = xml_flow_file.SFy;
    flow_data = flow_y.data;
    modifiedStr = strrep(flow_data, '0.', '0');
    parsed_flow = strsplit(modifiedStr);
    flowY = reshape(str2num(char(parsed_flow)), flow_y.cols, flow_y.rows)';

    img = mat2img(flowX, flowY);
    out_fname = strcat(out_path,char(identifier(1)),'.flo');
    writeFlowFile(img(:,:,1:2), out_fname);

end

