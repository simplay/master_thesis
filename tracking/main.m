%clc;
%clear all;
close all;

DATASETNAME = 'c14';
STEP_SIZE = 8;
MODE = 5; % display mode
DISPLAY = false; % show tracking point
WRITE_TRACKINGS_INTO_FILES = true;


% 
VAR_SIGMA_S = 5;
VAR_SIGMA_R = 0.3; %apply to appropriate quiver region in flow field
SHOW_VIDEO = false;
RUN_BILAT_FILT = true;
COMPUTE_TRACKINGS = true;
COMPUTE_LOCAL_VAR = false; % global variance is still computed
COMPUTE_CIE_LAB = false; % compute cie lab colors from given input seq
%%
run_tracking( DATASETNAME, STEP_SIZE, COMPUTE_TRACKINGS, MODE, DISPLAY, WRITE_TRACKINGS_INTO_FILES, VAR_SIGMA_S, VAR_SIGMA_R, SHOW_VIDEO, COMPUTE_LOCAL_VAR, COMPUTE_CIE_LAB, RUN_BILAT_FILT);
