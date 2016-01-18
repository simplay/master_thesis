DATASET = 'car2';
MODE = 4;
img_index = 1;
 SELECT_INPUT = true
col_sel = 302;



    addpath('../libs/flow-code-matlab');
    BASE = '../output/similarities/';
    % 'cars1_step_8_frame_';
    PREFIX_FRAME_TENSOR_FILE = [DATASET,'_step_',num2str(STEPSIZE_DATA),'_frame_'];

    DATASETNAME = DATASET;
    METHODNAME = 'ldof'; %other,ldof
    DATASETP = strcat(DATASETNAME,'/');
    BASE_FILE_PATH = strcat('../data/',METHODNAME,'/',DATASETP);

    label_mappings = labelfile2mat(strcat(BASE,DATASET));
    [~, imgs, ~, ~] = read_metadata(BASE_FILE_PATH);
    

        
    TIL = length(imgs)-1;
    TIL = 10;
    
if MODE == 1
    
    
    
     
    pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(img_index),'.mat'));
    pixeltensor = pixeltensor.tracked_pixels;

    for k=1:TIL-1,
        figure
        filename = imgs{k};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        hold on

        % pixeltensor(243,5,2) is lable 18

        % from post
        [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel);
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        plot(ay1,ax1,'Color',[1,0,0],'Marker','O');

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        plot(ay,ax,'Color',[0,0,1],'Marker','O');

        plot([ay1, ay],[ax1 ax]');
    end
    
elseif MODE == 2


    for k=1:TIL-1,
        pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(k),'.mat'));
        pixeltensor = pixeltensor.tracked_pixels;
    
 

        % pixeltensor(243,5,2) is lable 18

        % from post
        [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel);
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        
        if (isempty(ax) || isempty(ay))
            break;
        end
        figure
        filename = imgs{k};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        hold on
        
        plot(ay1,ax1,'Color',[1,0,0],'Marker','O');
        plot(ay,ax,'Color',[0,0,1],'Marker','O');
        plot([ay1, ay],[ax1 ax]');
    end
    
elseif MODE == 3
       figure
        filename = imgs{1};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        hold on

        
        
    for k=1:TIL-1,
        pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(k),'.mat'));
        pixeltensor = pixeltensor.tracked_pixels;
    
        f = (k-1)*(1/TIL);
 

        % pixeltensor(243,5,2) is lable 18

        % from post
        [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel);
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        
        if (isempty(ax) || isempty(ay))
            break;
        end
 
        
        plot(ay1,ax1,'Color',[1-f,0,0],'Marker','.');
    end

    
    elseif MODE == 4
        figure
        filename = imgs{1};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        hold on

        if SELECT_INPUT
        [x, y] = ginput(1);
        
                pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(1),'.mat'));
        pixeltensor = pixeltensor.tracked_pixels;
        
                         [row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);
                    [~,idx_pos] = min(sum([row_ids-y,col_ids-x].^2,2));
            col_sel = pixeltensor(row_ids(idx_pos), col_ids(idx_pos), 2);
            disp(['Selected label with index ',num2str(col_sel)])
            
        end
        
    for k=1:TIL-1,
        pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(k),'.mat'));
        pixeltensor = pixeltensor.tracked_pixels;
    
        f = (k-1)*(1/TIL);
        

 

        % pixeltensor(243,5,2) is lable 18

        % from post
        [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel);
        

        
        
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        
        if k == 1
        tx = ax1;
        ty = ay1;
        end

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        
        if (isempty(ax) || isempty(ay))
            break;
        end
 
        
        plot(ay1,ax1,'Color',[1-f,0,0],'Marker','x');
        
                [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel+1);
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        
        if (isempty(ax) || isempty(ay))
            break;
        end
 
        
        plot(ay1,ax1,'Color',[0,1-f,0],'Marker','x');
        
        
        
                        [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel-1);
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        
        if (isempty(ax) || isempty(ay))
            break;
        end
 
        
        plot(ay1,ax1,'Color',[0,0,1-f],'Marker','x');
    end
    
    
   elseif MODE == 5
        t = figure
        filename = imgs{1};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        hold on

        if SELECT_INPUT
            [x, y] = ginput(1);

                    pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(1),'.mat'));
            pixeltensor = pixeltensor.tracked_pixels;

                             [row_ids, col_ids, ~] = find(pixeltensor(:,:,2) > 0);
                        [~,idx_pos] = min(sum([row_ids-y,col_ids-x].^2,2));
                col_sel = pixeltensor(row_ids(idx_pos), col_ids(idx_pos), 2);
                disp(['Selected label with index ',num2str(col_sel)])
                close(t)
        end
        
    for k=1:TIL-1,
           pixeltensor = load(strcat('../output/trackingdata/',PREFIX_FRAME_TENSOR_FILE,num2str(k),'.mat'));
        pixeltensor = pixeltensor.tracked_pixels;
    
 

        % pixeltensor(243,5,2) is lable 18

        % from post
        [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel);
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        
        if k == 1
        tx = ax1;
        ty = ay1;
        end

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        
        if (isempty(ax) || isempty(ay))
            break;
        end
        figure
        filename = imgs{k};
        img = imread(filename);
        I = mat2img(img(:,:,1));
        imshow(I);
        hold on
        
        plot(ay1,ax1,'Color',[1,0,0],'Marker','O');
        plot(ay,ax,'Color',[0,0,1],'Marker','O');
        plot([ay1, ay],[ax1 ax]');
        
        
             [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel+1);
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        
        if (isempty(ax) || isempty(ay))
            break;
        end
        
        plot(ay1,ax1,'Color',[0,1,0],'Marker','O');
        plot(ay,ax,'Color',[0,0,1],'Marker','O');
        plot([ay1, ay],[ax1 ax]');
        
             [mark_row_idx,mark_col_idx,~] = find(pixeltensor(:,:,2) == col_sel-1);
        ax1 = pixeltensor(mark_row_idx, mark_col_idx, 3);
        ay1 = pixeltensor(mark_row_idx, mark_col_idx, 4);
        

        % to pos
        ax = pixeltensor(mark_row_idx, mark_col_idx, 5);
        ay = pixeltensor(mark_row_idx, mark_col_idx, 6);
        
        if (isempty(ax) || isempty(ay))
            break;
        end
        
        plot(ay1,ax1,'Color',[1,1,0],'Marker','O');
        plot(ay,ax,'Color',[0,0,1],'Marker','O');
        plot([ay1, ay],[ax1 ax]');
        
    end

end





    % display fw flow img
    figure('name', 'v flow')
    fw_flow = readFlowFile(fwf{img_index});
    imshow(fw_flow(:,:,1))
    hold on
    plot(ty,tx,'Color',[1,0,0],'Marker','O');
    
    figure('name', 'u flow')
    fw_flow = readFlowFile(fwf{img_index});
    imshow(fw_flow(:,:,2))
    hold on
    plot(ty,tx,'Color',[1,0,0],'Marker','O');
   
    % display normalized fw flow imgs
    displayFlow(fw_flow, tx, ty)
