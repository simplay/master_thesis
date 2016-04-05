# Modified version of the dense Semi-Rigid Scene Flow (SRSF) V 1.0 method

This is a modified version of the optical flow generation method dense Semi-Rigid Scene Flow (SRSF) V 1.0. The whole path handling has been reworked in order to be conform to another pipeline, which uses this method for comparison purpsoses. All rights belong to the original authors mentioned in the original readme below.

## Usage

Run `./semirigSF path_to_dataset from_frame_idx to_frame_idx use_rigid_transform`

Where 
+ **path_to_dataset**: Is the relative path to a target dataset
+ **from_frame_idx**: Is the _from_ frame index of a well-enumerated dataset.
+ **to_frame_idx**: Is the _to_ frame index of a well-enumerated dataset.
+ **use_rigid_transform**: Should a rigit transformation be used. 0 = Non-Rigid, 1 = Rigid, 2 =  Rigid + Non-Rigid

for further information, please read the [original readme](https://github.com/simplay/master_thesis/blob/reworking-precomp-flow/Source/compute_flows/srsf/original_readme.txt).
The source code of this method can be found [here](https://github.com/simplay/modified_srsf_method).

Example: `./semirigSF ../../../Data/foo/ 11 10 0`
