# Kernighan–Lin algorithm

This program is a standalone executable application that performs a trajectory partitioning. 
For further information, please have a look at [here](https://en.wikipedia.org/wiki/Kernighan%E2%80%93Lin_algorithm). 

## Expected Structure

You can use this code without having to use the rest of the pipeline. To do so, you have to:

1. Create a toplevel directory that has any name you like.
2. Download the code in `./kernighan_lin` (from this repo) and store it (locally on your system) in a folder called **kernighan_lin** located in your toplevel directory.
3. Create in your top-level directory the following subfolder structure `./output/graph_part/` and `./output/similarities/`.
4. Store your similarity data in `./output/similarities/`.
5. Run this applciation for your given similarity dataset. 
 + Keep in mind to set the correct dataset by assigning `dataset` in ``src/com.ma/Main.java
 + A valid dataset expects 3 files: `YDSN_sim.dat`, `YDSN_labels.txt` and `YDSN_spnn.txt`, where **YDSN** stands for _your dataset name_.
 + E.g. given the dataset `c14`, the following files have to be put into `./output/similarities/`: `c14_sim.dat`, `c14_labels.txt` and `c14_spnn.txt`.
6. The computed partition will be stored in `./output/graph_part/` with the name `YDSN_part.txt`.
