# Initialize Data

Generates and extracts all relevant data from a chosen dataset,
used for peforming the _feature tracking_, _trajectory extraction_ and _similarity matrix_ compuation stages of the pipeline. 


# Usage

Run the script `init_data.m` in matlab and set the corresponding flags accordingly.
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
