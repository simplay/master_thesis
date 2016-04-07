# Core

Core of the pipeline. Is responsible for tracking points, extracting trajectories and computing the trajectory affinity matrix.

## Runtime arguments

VM options: `-Xmx16000m`

## Usage example

+ Compute similarities using the MD similarity task using local variances for normalization with depth cues, using the cut probability p=0.99 on the dataset c14 and exporting the output with a custom prefix called _foobar_:
 + `-d c14 -task 1 -var 1 -nn 200 -depth 1 -prob 0.99 -prefix foobar`

### Program arguments: 
+ **-d NAME**: (**req**) The NAME of the target dataset. Has to correspond to a dataset located at `../data/ldof/`.

+ **-task ID**: (**req**) The target similarity task given by a number ID.
 + ID == 1: Summed distances (SD) similarity task.
 + ID == 2: Product distances (PD) similarity task.
 + ID == 3: Product of Euclidian distances (PED) similarity task. Uses 3d trajectory points for computing the spatial distances.
 + ID == 4: Sum of Euclidian distances (SED) similarity task. Uses 3d trajectory points for computing the spatial distances.
 
Whenever we want to make use of depth cues but the color and depth camera are already aligned, 
the target similarity task is re-assign to one that used 3d trajectory points (i.e. to its alternative task).
 
+ **-var BOOL**: Indicates whether the local flow variance values should be used for normalizing similarity values. By default, the global variance values are used.
 + BOOL == 0: Use global flow variance values for normalization.
 + BOOL == 1: Use local flow variance values for normalization.
 
+ **-depth BOOL** should depth cues be used. By default no depth cues are used.
 + BOOL == 0: Do not use any depth cues.
 + BOOL == 1: Use depth field to transform tracked points to the Euclidian space to have a better measure for the average spatial distance.
 
+ **-nn NUM**: The number NUM of spatial nearest neighbors that should be exported as output. By default the 200 nearest neighbors are exported.

+ **-debug BOOL**:  should the program run in the debug mode. If so, it will dumb intermediate calculated data. By default, the debug mode is disabled.

+ **-prob FLOAT**: Set value FLOAT of cut probability used for the PD similarity task. FLOAT is a supposed to be a value: FLOAT > 0 AND FLOAT < 1. By default the probability value 0.5 is used.

+ **-prefix NAME** custom output file name prefix NAME, by default the empty string.

## Generated Output Files

When running the code on a dataset called **DATASET**, the following output is generated:

+ The most important output is exported to `output/similarities/`:
 + `DATASET_sim.dat`: The affinity matrix, encoding all similarities between all valid trajectories.
 + `DATASET_labels.txt`: The affinity matrix index trajectory label mapping.
 + `DATASET_spnn.txt`: The set of nearest neighbor indices per trajectory.
 
### Details about the output data and their format:
 
 + The similarity values: A dat file that contains a **m x m** matrix readable by matlab via load('file.dat'). The dimension **m** denotes the number of trajectories that were used. Note that not every extracted trajectory was actually used due to filtering resons. E.g. too short trajectories are filtered. Consult the code for more info about the filtering steps. Since trajectories are well enumerated by their unque label but filtering introduceds holes, we ensure the well-enumeration by returning a label mapping that tells us to which trajectory label each matrix row and column belong to. 
 
 + The trajectory labels: A text file that contains a mapping, indicating to which label any matrix row and column (of the reported .dat file) belongs. The file contains a sequence of numbers. The index of a number denotes the column,row index in the returned similarity matrix, its corresponding value denotes the lable of a trajectory.
 
 + The 12 spatially nearest trajectory neighbors: a file that contains a sequence of rows of numbers. each row corresponds to the nearest neighbors of a particular trajectory. file row index corresponds to the index in the label file. i.e. in order to obtain the trajectory index, to which the numbers belong to, we have to lookup the corresponding index in the **_labels.txt** file. 
 
 In short (tl;dr):
 
 + Not every initially extraced trajectory is returned (some are filtered).
 + trajectory (values) are sorted by their label value (a unique natural number)
 + these values are returned in their sorting order.
 + The index-trajectory-label mapping ins defined in the `labels.txt` file.
 + Affinity values are stored in `sim.dat` file.

### Optional output data

When providing the runtime argument `-debug 1`, the following additional debugging data is dumbed:

+ A list of files named `active_tra_f_FRAMEINDEX` located at `../output/trajectory_label_frame/DATASET/`, encoding all active trajectories (their tracking points) in a given frame. This data is used to visualize the tracking points of the trajectories.
+ A file called `traj_out_DATASET_fc_FRAMECOUNT` located `../output/trajectories/`, encoding the extracted trajectories in a format readable by the old similarity computation code written in ruby.
