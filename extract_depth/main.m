START_IDX = 92;
END_IDX = 105;
DATASET = 'teacan';
path = ['../data/ldof/', DATASET, '/depth/'];

for k=START_IDX:END_IDX
    fpath = strcat(path, num2str(k), '.png');
    lv = imread(fpath);
    fname = strcat('../output/depths/',DATASET,'_depth_',num2str(k),'.txt');
    fid = fopen(fname,'w');
    if fid ~= -1
        for t=1:size(lv,1)
            a_row = mat2str(lv(t,:));
            fprintf(fid,'%s\r\n', a_row);
        end
    end
    fclose(fid);
end