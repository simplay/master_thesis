# Initialize Data

Generates and extracts all relevant data from a chosen dataset,
used for peforming the _feature tracking_, _trajectory extraction_ and _similarity matrix_ compuation stages of the pipeline.

## Usage

Run the script `./init_data.m` in matlab and set the corresponding flags accordingly.
In particular, the following variables can be set:

+ **DATASETNAME**: The name of a valid dataset directory located at `../Data/`.
+ **METHODNAME**: the name of the subdirectory in the dataset folder, containing the flow fields. By convention, the name of this sudirectory corresponds to the used flow method.
+ **STEP_SIZE**: Integer, defining the sampling rate: Use every n-th pixel. Good values are obtain when setting it equal to 8.
+ **PRECISSION**: Number of digits that should be used for all the computations.
+ **COMPUTE_TRACKING_DATA**: Boolean value, if set to true, it computes tracking candidates, valid regions, flows.
+ **COMPUTE_FLOW_VARIANCES** Boolean, if set to true it computes local and global flow variances.
+ **COMPUTE_CIE_LAB**: Boolean, if set to true, it computes cie lab colors from given input seq
+ **EXTRACT_DEPTH_FIELDS** Boolean, if set to true, it will extract depth field data.Only apply this, if depth fields do exist for the selected dataset.
+ **USE_OWN_DEPTHS**: Defines how the values are encoded, if set to true, we use the convention: RRB(0,(depth[idx]>>8)&255,depth[idx]&255), i.e. real depth value is d = 255*G + B
+ **VAR_SIGMA_S**: Spatial variance value in pixel units, good values are obtained when setting it to 5.
+ **VAR_SIGMA_R**: Response variance value in pixel units, good values are obtained when setting it to 0.3

## Generated Output

In the following a short description of the generated output files:

+ The directions of the flow fields as a (M x N) matrix (M,N are correspond to the image dimensions), stored in a text file.
 + For a frame pair, there are 4 text files generated: The forward u and v flow directions and the backward u and v flow directions.
 + These generated files are stored at `../output/tracker_data/my_dataset/`
+ A matrix defining trackable pixel locations (indicated occluded regions) saved stored in a text file . Acts as a boolean mask used for the tracking stage.
 + These generated files are stored at `../output/tracker_data/my_dataset/` 
+ A matrix defining tracking candidates per frame stored as a text file.
+ The color images if the appropriate flag is set.
+ The flow variance values using a special bilateral filter.
+ Depth fields, if present in the given dataset.
