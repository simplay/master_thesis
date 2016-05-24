# Core

Core of the pipeline. Is responsible for tracking points, extracting trajectories and computing the trajectory affinity matrix.

## Runtime arguments

VM options: `-Xmx16000m`

## Usage example

+ Compute similarities using the MD similarity task using local variances for normalization with depth cues, using the cut probability p=0.99 on the dataset c14 and exporting the output with a custom prefix called _foobar_:
 + `-d c14 -task 1 -var 1 -nn 200 -depth 1 -prob 0.99 -prefix foobar`

### Program arguments: 
+ **-d NAME**: (**req**) The NAME of the target dataset. Has to correspond to a dataset directory name that is located at `../../Data/`.

+ **-task ID**: (**req**) The target similarity task given by a number ID.
 + `ID == 1`: Summed distances (SD) similarity task.
 + `ID == 2`: Product distances (PD) similarity task.
 + `ID == 3`: Product of Euclidian distances (PED) similarity task. Uses 3d trajectory points for computing the spatial distances.
 + `ID == 4`: Sum of Euclidian distances (SED) similarity task. Uses 3d trajectory points for computing the spatial distances.
 + `ID == 5`: Product of Distances all 3d (PAED)
 
Whenever we want to make use of depth cues but the color and depth camera are already aligned, 
the target similarity task is re-assign to one that used 3d trajectory points (i.e. to its alternative task).
 
+ **-var BOOL**: Indicates whether the local flow variance values should be used for normalizing similarity values. By default, the global variance values are used.
 + `BOOL == 0`: Use global flow variance values for normalization.
 + `BOOL == 1`: Use local flow variance values for normalization.
 
+ **-ct BOOL**: Indicates whether we want to apply a continuation of the extracted trajectories. This allows to compare
trajectory sequences that are not overlapping. By default this mode is disabled.
 + `BOOL == 0`: Do not apply trajectory continuation.
 + `BOOL == 1`: Apply trajectory continuation. Allows to compare short-gap trajectories.
 
+ **-nn NUM**: The number NUM of spatial nearest neighbors that should be exported as output. By default the 200 nearest neighbors are exported.

+ **-nnm STRING**: An identifier specifying which nearest neighbors should be returned. By default, all nearest neighbors will be returned.
 + `STRING == top`: Return the top N neighbors
 + `STRING == both`: Return the top N/2 and worst N/2 neighbors
 + `STRING == all`: Return the complete neighborhood of every trajectory (default mode).

+ **-debug BOOL**:  should the program run in the debug mode. If so, it will dumb intermediate calculated data. By default, the debug mode is disabled.

+ **-prob FLOAT**: Set value FLOAT of cut probability used for the PD similarity task. FLOAT is a supposed to be a value: FLOAT > 0 AND FLOAT < 1. By default the probability value 0.5 is used.

+ **-prefix NAME** custom prefix **NAME** appended to all generated output files. By default it is equal to the empty string.

+ **-lambda FLOAT** scale factor in affinity function used for all MD tasks. 
The **smaller** the value for **lambda** gets, the **higher** the values in the `W` matrix become (W gets **brighter**). 
By default it is either 1000 (when using depth cues) or 0.1 (otherwise).

+ **-dscale FLAT** scale of depth field values to have those values in meters.

## Generated Output Files

When running the code on a dataset called **DATASET**, the following output is generated:

+ The most important output is exported to `output/similarities/`:
 + `DATASET_sim.dat`: The affinity matrix, encoding all similarities between all valid trajectories.
 + `DATASET_labels.txt`: The affinity matrix index trajectory label mapping.
 + `DATASET_spnn.txt`: The set of nearest neighbor indices per trajectory.
 
+ A list of files named `active_tra_f_FRAMEINDEX` located at `../output/trajectory_label_frame/DATASET/`, encoding all active trajectories (their tracking points) in a given frame. This data is used to visualize the tracking points of the trajectories.

+ A log-file located at `../output/logs/` named `core_<TIMESTAMP>.txt` containing the console output after running the pipeline. 
 
### Details about the output data and their format:
 
 + The similarity values: A **.dat** file that contains a **m x m** matrix readable by matlab via load('file.dat'). 
 The dimension **m** denotes the number of trajectories that were used. 
 Note that not every extracted trajectory was actually used due to filtering reasons. 
  + E.g. too short trajectories are filtered. Consult the code for more info about the filtering steps. 
  + Since trajectories are well enumerated by their unique label but filtering introduced holes, 
 we ensure the well-enumeration by returning a label mapping that tells us to which trajectory 
 label each matrix row and column belong to. 
 
 + The trajectory labels: A text file that contains a mapping, indicating to which label any matrix row / column 
 (of the reported .dat file) belongs to. The file contains a sequence of numbers. 
 The index of a number denotes the column, row index in the returned similarity matrix, 
 its corresponding value denotes the label of a trajectory.
 
 + The **NUM** (given as user argument) spatially nearest trajectory neighbors: a file that contains a sequence of rows of numbers. 
 each row corresponds to the nearest neighbors of a particular trajectory. 
 file row index corresponds to the index in the label file. i.e. in order to obtain the trajectory index, 
 to which the numbers belong to, we have to lookup the corresponding index in the **_labels.txt** file. 
 
 In short (tl;dr):
 
 + Not every initially extracted trajectory is returned (some are filtered).
 + trajectory (values) are sorted by their label value (a unique natural number)
 + these values are returned in their sorting order.
 + The index-trajectory-label mapping ins defined in the `labels.txt` file.
 + Affinity values are stored in `sim.dat` file.
 
### Generated output filenames

The core pipelines uses a generic and common naming scheme that consists of:

+ the used dataset name `<DS>`
+ the selected similarity task `<TN>`
+ the number of nearest neighbors `<N>` and what type `<NNT>` they are (_best/both_)
+ the user given prefix `<P>` which is optional.

The generated files will be named as the follows: `<DS>_<TN>_<NNT>_<N>_<P>_<SUFFIX>`
where `<SUFFIX>` is either:

+ `_sim.data`
+ `_labels.txt`
+ `_spnn.txt`

Example: 

when running: `-d c14 -task 2 -nn 1000 -nnm top -prefix foobar` the following should be generated: 

+ `c14_md_top_1000_foobar_sim.data`
+ `c14_md_top_1000_foobar_labels.txt`
+ `c14_md_top_1000_foobar_spnn.txt`


### Optional output data

When providing the runtime argument `-debug 1`, the following additional debugging data is dumbed:

+ A file called `traj_out_DATASET_fc_FRAMECOUNT` located `../output/trajectories/`, encoding the extracted trajectories in a format readable by the old similarity computation code written in ruby.
