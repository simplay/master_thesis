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
