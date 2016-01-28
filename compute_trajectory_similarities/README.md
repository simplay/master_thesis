# Trajectory Similarities

Executing `java -jar -Xmx20000m package/jar/compute_trajectory_similarities.jar -l 1 t 1 -v 0` on a Windows 8 machine, using 16 Threads takes 825 seconds to compute the affinity matrix over a set of trajectories with 100 frames (chair dataset).

## Output Data and data formats

The script yields 3 different output files:

+ The similarity values: A dat file that contains a **m x m** matrix readable by matlab via load('file.dat'). The dimension **m** denotes the number of trajectories that were used. Note that not every extracted trajectory was actually used due to filtering resons. E.g. too short trajectories are filtered. Consult the code for more info about the filtering steps. Since trajectories are well enumerated by their unque label but filtering introduceds holes, we ensure the well-enumeration by returning a label mapping that tells us to which trajectory label each matrix row and column belong to. 

+ The trajectory labels: A text file that contains a mapping, indicating to which label any matrix row and column (of the reported .dat file) belongs. The file contains a sequence of numbers. The index of a number denotes the column,row index in the returned similarity matrix, its corresponding value denotes the lable of a trajectory.

+ The 12 spatially nearest trajectory neighbors: a file that contains a sequence of rows of numbers. each row corresponds to the nearest neighbors of a particular trajectory. file row index corresponds to the index in the label file. i.e. in order to obtain the trajectory index, to which the numbers belong to, we have to lookup the corresponding index in the **_labels.txt** file. 

In short (tl;dr):

+ Not every initially extraced trajectory is returned (some are filtered).
+ trajectory (values) are sorted by their label value (a unique natural number)
+ these values are returned in their sorting order.
+ The index-trajectory-label mapping ins defined in the `labels.txt` file.
+ Affinity values are stored in `sim.dat` file.

Example: When running the script on a dataset called **c14**, the following files will be generated: 
+ `../output/similarities/c14_sim.dat`
+ `../output/similarities/c14_labels.txt`
+ `../output/similarities/c14_spnn.txt`

Assume that the first line of file `c14_spnn.tx` contains the following sequence:

`57, 58, 90, 91, 92, 93, 131, 132, 133, 134, 169, 170, 171`. These are the label values of the nearest neighbors of the first trajectory. The label value of the first trajectory can be obtained by performing a lookup in the file `c14_labels.txt`. Assume that the first six elements of this file have the following value: `56 57 58 59 60 61`. This means, that the first extracted trajectory has the label value **56**, the 2nd the value **57**, ... , and the 6th the label value **61**. Note that the index of such a label value also corresponds to the similarity matrix column and row index.

## Running

+ Run ruby code: `./run.sh` (runs as a jruby application)
+ Run compiled code: `./run2.sh` (faster)

## Compilation

Makes use of [rawr](https://github.com/rawr/rawr)

Execute `rawr install` followed by `rake rawr:jar` in your terminal.
The compiled jar is located at `./packages/jar/`. Note that is a shell-script called `run2.sh` that assigns all relevant runtime parameters to start the program. 

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
 + `-n <FNAME>` names the output files according to a provided filename. By default the files are prefixes by their input dataset.  

### Examples

### Advanced mode


