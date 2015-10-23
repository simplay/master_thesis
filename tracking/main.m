clc;
%clear all;
close all;

DATASETNAME = 'cars1';
STEP_SIZE = 8;
MODE = 5; % display mode
DISPLAY = false; % show tracking point
WRITE_TRACKINGS_INTO_FILES = true;
run_tracking( DATASETNAME, STEP_SIZE, MODE, DISPLAY, WRITE_TRACKINGS_INTO_FILES );