START_IDX = 1% 250;
END_IDX = 104% 351;
DATASET = 'wh1';
path = ['../data/ldof/', DATASET, '/depth/'];

for k=START_IDX:END_IDX
    disp(strcat('Iteration: ',num2str(k-START_IDX+1)))
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