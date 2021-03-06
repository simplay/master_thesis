# Kernighan–Lin algorithm

This program is a standalone executable application that performs a trajectory partitioning. 
For further information, please have a look at [here](https://en.wikipedia.org/wiki/Kernighan%E2%80%93Lin_algorithm). 

## Usage

The following runtime arguments are currently supported:

+ `-d STRING`: Name of the target dataset.
+ `-cc INTEGER`: Number of cluster **INTEGER** the graph should be partitioned into
+ `-dc INTEGER`: Number of dummy vertices **INTEGER** put into a cluster set. The more we have, the smaller a segment can get.
+ `-ipm STRING`: The name of the initial set partition mode (i.e. its abbreviation) that should be used.
 + `STRING == mn`: Splits sets into modulo n.
 + `STRING == m2`: Splits sets into modulo 2.
 + `STRING == ef`: Splits sets empty full.
 + `STRING == ebo`: Splits sets such that all are empty but one.
 + `STRING == slr`: Splits sets left/right.
 
+ `-mic INTEGER`: The maximal number of iterations **INTEGER** per optimization step.
+ `-rc INTEGER`: The number of repetitions **INTEGER** of the same optimization approach using the previous computed date.
+ `-prefix STRING`: The prefix in the filename of the generated output data.

### Example: 

`-d c14 -cc 2 -dc 0 -mic 4 -rc 1 -prefix foobar`
Will run the dataset c14 and partition it into two sets. The output file will be prefixed by _foobar_.


## Download a dataset

Download the following files and store them in the location as described in the section **Expected structure**:

+ [c14_sim.dat](https://www.dropbox.com/s/ah7h5ff9307geud/c14_sim.dat?dl=0)
+ [c14_labels](https://www.dropbox.com/s/11mfizvxw39mf7o/c14_labels.txt?dl=0)
+ [c14_spnn](https://www.dropbox.com/s/hvf1rfqb49lc96m/c14_spnn.txt?dl=0)
 
The datasets should be put into `../output/similarity/`.


## Expected structure

You can use this code without having to use the rest of the pipeline. 
In oder to do so, you have to:

1. Create a toplevel directory that has any name you like.
2. Download the code in `./kernighan_lin` (from this repo) and store it (locally on your system) in a folder called **kernighan_lin** located in your toplevel directory.
3. Create in your top-level directory the following subfolder structure `./output/graph_part/` and `./output/similarities/`.
4. Store your similarity data in `./output/similarities/`.
5. Run this applciation for your given similarity dataset. 
 + Keep in mind to set the correct dataset by assigning `dataset` in ``src/com.ma/Main.java
 + A valid dataset expects 3 files: `YDSN_sim.dat`, `YDSN_labels.txt` and `YDSN_spnn.txt`, where **YDSN** stands for _your dataset name_.
 + E.g. given the dataset `c14`, the following files have to be put into `./output/similarities/`: `c14_sim.dat`, `c14_labels.txt` and `c14_spnn.txt`.
6. The computed partition will be stored in `./output/graph_part/` with the name `YDSN_part.txt`.
