# Trajectory Similarities

## Usage

Enter `./run.sh` in your terminal

### Supported arguments
+ `-d` an **Integer** that allows to run the computation in a certain debugging mode.
 + `-d 0` default value, runs the app in normal mode.
 + `-d 1` Force the app to use stepsizes of 1 in all forward differences schemes (for computing the motion distance **d_motion**)
 + `-d 2` Runs the app in a special interactive shell mode which allows to compute the similarities of specified trajectories (according to their label). I.e. an interactive coding environment is loaded.
+ `-v` an **Intefer** determining which flow variance should be used for normalization.
 + `-v 0` use the global optical flow variance variance (recommended as long as we are still not sure about whether we are doing in correctly).
 + `-v 1` use the loacal variance (default selection).
+ `-t` an **Integer** that allow to specify whether depth information should be used as a cue for the compuation. Keep in mind that not every dataset contains depth information. Thus, only datasets including depth maps can make use of this option.
 + `-t 0` Don't use any depth information. 
 + `-t 1` Use the depth maps that are associated with this dataset. In addition, if present, also the camera callibration data is used, such that all computations are performed in a metric space.
+ `-x` Allows to select the affinity compuation method. Currently, both methods achieve almost the same quality. However, the sum of affinities is much slower.
 + `-x 0` Use the Product affinity method. Distanced are multiplied (default method). 
 + `-x 1` Use the sum affinity method. Distances are summed. In addition this method makes use of color information.

### Examples

### Advanced mode
