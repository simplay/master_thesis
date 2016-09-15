DATASETNAME = 'alley2small';
APPLY_RESCALING = true;
rescaledDim = [218, 512];

path = ['../../Data/', DATASETNAME, '/depth/'];
listing = dir(strcat('../../Data/', DATASETNAME, '/depth/*.dpt'));

for k=1:length(imgs)
    f = listing(k);
    fpath = strcat(path, f.name);
    lv = depth_read(fpath); %* DEPTH_SCALE; % scale factor to tranform to meters

    if APPLY_RESCALING
        lv = imresize(lv, rescaledDim);
    end

    tillDot = strfind(f.name,'.dpt');
    fileNr = f.name(1:tillDot-1);
    fname = strcat('depth_',fileNr,'.txt');
    disp(strcat(num2str(k), '. Iteration - extracted depth field: ', fname));
    fnamePath = strcat('../output/tracker_data/',DATASETNAME,'/', fname);
    fid = fopen(fnamePath,'w');
    if fid ~= -1
        for t=1:size(lv,1)
            a_row = mat2str(lv(t,:));
            fprintf(fid,'%s\r\n', a_row);
        end
    end
    fclose(fid);

    valid_depths = double(lv > 0);
    fname = strcat('../output/tracker_data/',DATASET,'/valid_depth_region_', fileNr, '.txt');
    imgfile = strcat('../output/tracker_data/',DATASET,'/valid_depth_region_', fileNr, '.png');

    imwrite(mat2img(valid_depths), imgfile);
    fid = fopen(fname,'w');
    if fid ~= -1
        for t=1:size(valid_depths,1)
            a_row = mat2str(valid_depths(t,:));
            fprintf(fid,'%s\r\n', a_row);
        end
    end
    fclose(fid);

end

