# Parameter Setup

datasets

alley2small
 + sampling every 6th pixel

+ `alley2small_ldof_md_dd_c_15_ev_18`
 + core: `-d alley2small -task 6 -var 1 -nn 1000 -depth 1 -prob 0.8 -prefix md_nn_1000_best -lambda 0.1 -dscale 1 -nnm top`

+ `alley2small_ldof_md_c_15_ev_18`
 + core: `-d alley2small -task 2 -var 1 -nn 1000 -depth 0 -prob 0.8 -prefix md_nn_1000_best -lambda 0.1 -dscale 1 -nnm top`

In the following a list what parameter setups were used for generating the datasets to offer the oportunity to re-produce all generated results.

## chair_3_cast

Resolution: `424 x 512`

Infos: 

+ Depth files do not have to be rescaled

### core

A lambda value equals **0.1** works the best.

+ Data for Spectral Clustering
+ Data for Spectral Clustering with depth
+ Data for Min-Cut
+ Data for Min-Cut using depth cues
+ Data for Kernighan Lin:
 + `-d chair_3_cast -task 1 -var 1 -nn 1600 -depth 0 -prob 0.8 -prefix sd_nn_1600_both -lambda 0.1 -dscale 1 -nnm both`

