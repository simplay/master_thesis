clc;
%clear all;
close all;

DATASETNAME = 'teacan';
STEP_SIZE = 8;
MODE = 5; % display mode
DISPLAY = false; % show tracking point
WRITE_TRACKINGS_INTO_FILES = true;
VAR_SIGMA_S = 8;
VAR_SIGMA_R = 5;
SHOW_VIDEO = false;
COMPUTE_TRACKINGS = false;
COMPUTE_LOCAL_VAR = false; % global variance is still computed
run_tracking( DATASETNAME, STEP_SIZE, COMPUTE_TRACKINGS, MODE, DISPLAY, WRITE_TRACKINGS_INTO_FILES, VAR_SIGMA_S, VAR_SIGMA_R, SHOW_VIDEO, COMPUTE_LOCAL_VAR);
