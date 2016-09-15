# Segmentation

There are 3 different segmentation methods implemented in the pipeline, namely: 

+ **Spectral Clustering**: 
 + Given the trajectory affinities encoded as a symmetric matrix **W**, 
 + compute the normalized Laplacian `B = D^-1/2*(D-W)*D^-1/2;` where `D = diag(i=1:n| D_i = sum_k=1 W_ik)`
 + Compute the eigenvalue decomposition of W. Let **U** be denote the eigenvecotors of W.
 + Select the eigenvectors from U (its columns) that belong to the smallest t eigenvalues.
 + The sorted t columns found in the step before span a matrix **U_small**.
 + Perform a spectral clustering using a fixed number of clusters on the columns of **U_small**.
 
+ **Min-Cut**: 
+ **Kernighan Lin**: 

## Usage

### Spectral Clustering Segmentation

Run the matlab script `main_spectral_clustering.m`

### Min-Cut Segmentation

Run the matlab script `main_min_cut.m`

### Kernighan-Lin Segmentation

Run the **main.java** java code contained in `./kernighan_lin/`.

## Output

The generated output is located at `../output/clustering/GENERATED_DATA_DIR_NAME/`.
For every method, a text file `labels.txt` and an image showing the visual segmentation of every frame of the dataset is generated. The labels file contains a trajectory-label cluster-label mapping. Therefore, it is possible to find the cluster label for a tracked trajectory point in a certain frame by looking up the actual assigned values in this mapping file.
