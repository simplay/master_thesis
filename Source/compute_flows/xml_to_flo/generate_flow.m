function generate_flow(in_path, fname, out_path)
%GENERATE_FLOW Summary of this function goes here
%   Detailed explanation goes here
    
    identifier = strsplit(fname, '.xml');
    xml_flow_file = xml_read(strcat(in_path,fname));
    
    M = xml_flow_file.Mask.rows;
    N = xml_flow_file.Mask.cols;
    
    parsed_flowX = parsed_xml_member(xml_flow_file.SFx);
    parsed_flowY = parsed_xml_member(xml_flow_file.SFy);
    parsed_flowZ = parsed_xml_member(xml_flow_file.SFz);
    parsed_flow_ofx = parsed_xml_member(xml_flow_file.OFx);
    parsed_flow_ofy = parsed_xml_member(xml_flow_file.OFy);
    
    t = xml_flow_file.Translation.data;
    q = xml_flow_file.Rotation.data;
    
    % write flow directions
    flowY = zeros(M, N);
    flowX = zeros(M, N);
    flowZ = zeros(M, N);
    OFx = zeros(M, N);
    OFy = zeros(M, N);
    
    idx = 1;
    for k=1:M
        for l=1:N
            OFx(k, l) = str2double(parsed_flow_ofx{idx});
            OFy(k, l) = str2double(parsed_flow_ofy{idx});
            flowX(k, l) = str2double(parsed_flowX{idx});
            flowY(k, l) = str2double(parsed_flowY{idx});
            flowZ(k, l) = str2double(parsed_flowZ{idx});
            idx = idx + 1;
        end
    end

    img = mat2img(OFx, OFy);
    out_fname = strcat(out_path, char(identifier(1)), '.flo');
    writeFlowFile(img(:,:,1:2), out_fname);
    
    % write scene flows if existent
    if strfind(out_path, '/srsf/') > 0
        base_path = strsplit(out_path, '/srsf/');
        base_path = base_path(1);
        base_path = base_path{1};
        
        scene_flow_path = strcat(base_path, '/', 'scene_flows/');
        if ~exist(scene_flow_path, 'dir')
            mkdir(scene_flow_path);
        end
        
        x_name = strcat(scene_flow_path, fname, '_x_sf.mat');
        y_name = strcat(scene_flow_path, fname, '_y_sf.mat');
        z_name = strcat(scene_flow_path, fname, '_z_sf.mat');
        
        dlmwrite(x_name, flowX, 'delimiter', ' ', 'precision', 12);
        dlmwrite(y_name, flowY, 'delimiter', ' ', 'precision', 12);
        dlmwrite(z_name, flowZ, 'delimiter', ' ', 'precision', 12);
    end
    
end

function parse = parsed_xml_member(member)
    modifiedStr = strrep(member.data, '0.', '0');
    parse = strsplit(modifiedStr);
end

