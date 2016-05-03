# Parameter Setup

datasets

alley2small
 + sampling every 6th pixel

+ `alley2small_ldof_md_dd_c_15_ev_18`
 + core: `-d alley2small -task 6 -var 1 -nn 1000 -depth 1 -prob 0.8 -prefix md_nn_1000_best -lambda 0.1 -dscale 1 -nnm top`

+ `alley2small_ldof_md_c_15_ev_18`
 + core: `-d alley2small -task 2 -var 1 -nn 1000 -depth 0 -prob 0.8 -prefix md_nn_1000_best -lambda 0.1 -dscale 1 -nnm top`


+ `-d alley2small -task 3 -var 1 -nn 3000 -lambda 0.1 -nnm top`
 + `alley2small_ldof_ped_c_7_ev_9`

In the following a list what parameter setups were used for generating the datasets to offer the oportunity to re-produce all generated results.

## cars
core:
+ `-d cars -task 2 -var 1 -nn 2000 -prefix foobar_md_nn_1000_best -lambda 0.01 -nnm top`
 + `cars_ldof_md_d_iters_20_c_3_ev_5_nu_1e-09` => error = 119.7973

## wh1

core:

+ `-nn 3000 -lambda 0.1 -task 2 -d chair_3_cast -var 1 -nnm top`
 + `chair_3_cast_ldof_pd_c_18_ev_25` 

+ `-d wh1 -task 2 -var 1 -nn 1200 -lambda 0.01 -nnm top`
 + `wh1_ldof_pd_c_8_ev_10`
 + 

+ `-d chair_3_cast -task 5 -var 1 -nn 3000 -lambda 0.0001 -nnm top`
 + `chair_3_cast_ldof_paed_c_20_ev_35`

## chair_3_cast

+ `-d chair_3_cast -task 3 -var 1 -nn 3000 -lambda 100 -nnm top`
 + `chair_3_cast_ldof_ped_c_25_ev_35`



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

