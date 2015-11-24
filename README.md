# My Master Thesis Pipeline

**Given**: A sequence of temporal coherent images (movie) and their depth maps.

**Goal**: Segment all objects that have a certain velocity such that objects moving equally are in the same segment. In oder to do so, perform the following steps:

1. Compute their Optical (foreward and backward) Flow.
 + The methods that are used can be found [here](https://github.com/simplay/ma_pipeline)
2. Track sparsely appropriate moveable points.
3. Extract corresponding trajectories
4. compute similarites between all determined trajectoires.
5. compute the segmentation using a special variant of spectral clustering using the trajectory similarities as a cue.

A detailed explanation of the whole pipeline can be found [here](https://github.com/simplay/ma_my_pipeline/blob/master/pipeline.md).

## Information:
+ **.flo** file format [specification](http://vision.middlebury.edu/flow/code/flow-code/README.txt).

## Conventions

+ Images that belong to sequence should be enumerated, starting with index **1**.
+ Flow fields (forward-and backward flow files) should be enumerated, starting with index **0**.
 + The **forward flow** with index **t** corresponds to the flow from **image (t+1)** to **image (t+2)**.
 + The **backward flow** with index **t** corresponds to the flow from **image (t+2)** to **image (t+1)**.

## Sources

### Large Distance Optical Flow: Can deal with occlusion
+ **LDOF**: Generated by T. Brox' freely available [Motion Segmenation Code](http://lmb.informatik.uni-freiburg.de/resources/binaries/eccv2010_mosegLinux64.zip). 

### Point Tracking that generate Trajectories 
Implementation of [this paper](http://lmb.informatik.uni-freiburg.de/people/brox/pub/sundaram_eccv10.pdf)

### Segmentation via Spectral clustering of Trajectories
Implementation of [this paper](http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=6682905)

## Requirements
+ Matlab
+ JRuby
+ Bundler
+ Git
+ export matlab path to have be able to run it in your terminal of choice
 + e.g. add this line to your bash profile: `export PATH="/Applications/MATLAB_R2014b.app/bin:$PATH"` 

## Installation

### Windows 7 or higher:

1. Perform the steps described [here](https://github.com/simplay/wincygwinify/blob/master/README.md)

2. Clone this repository.

3. Run `bundle`

### Mac OS X / Linux

First, install a recent Matlab version. Next, open your terminal of choice and enter the following commands in the given order:

1. Install RVM: `\curl -sSL https://get.rvm.io | bash -s stable`
 + Ruby version manager to have clean ruby dependency management. Allows to have several ruby versions installed on your os, without having too much trouble. no need to manually install required plugin dependencies for every ruby version. in ruby, plugins/libraries are called **gems*.
2. Install Jruby: `rvm install jruby-9.0.1.0`
 + Special version of Ruby supporting real multi threading, running on java's vm.
3. Switch to your global gemset: `rvm gemset use global`
 + a gemset holds a set of ruby dependencies (plugins, dynlinks, etc). These dependencies are only active if and only if the gemset is loaded. I have added rvm hooks to dynamically load the appropriate dependencies, when cd-ing into a particular directory.
4. Install Bundler in global gemset: `gem install bundler`
 + Ruby Plugin manager: makes sure all required dependencies are installed. Dependencies are in the **Gemfile** defined.
5. Fetch source of this Repsitory: `git clone https://github.com/simplay/ma_my_pipeline.git`
6. Change path to target directory: `cd path_to_target_directory_in_cloned_repo`
7. Install required ruby plugins and setup their dependencies: `bundle`

## Usages
### Run complete pipeline

+ Generate forward and backward flows
 + cd to `flow/`
 + copy your image series to `data/my_image_series_folder_name`
 + run `ruby normalize my_image_series_folder_name/` (**optional**)
 + run `ruby generate_flow.rb my_image_series_folder_name START END` 
 
Note that **START** is first and **END** is the last image index that should be processed.

+ Generate Tracking points and Variances
+ Extract trajectories
+ Compute Affinity matrix
+ Compute Segmentation

### Details

#### extract trajectories script:
+ Enter `cd ./extract_trackings/`
+ Enter `ruby extract.rb cars1/` to extract all trajectories from the computed trackings files located at `./output/trajectories/cars1/`.

#### compute similarity script: 

+ Enter `cd ./compute_trajectory_similarities/`
+ Enter `ruby compute_similarities.rb -f DATASET -d DBUGMODE -v VARIANCETYPE` to compute the similarities between the trajectories in txt file **DATASET** located at `../output/trajectories/`. The script is using the a given variances associated with the given **VARIANCETYPE** for the computation and is running in debug mode **DEBUGMODE**.
+ E.g. `ruby compute_similarities.rb -f traj_out_cars1_fc_4.txt` computed the similarity matrix for the dataset `traj_out_cars1_fc_4.txt`.
+ When using jruby, pass the following jruby runtime argument to use 8gb memory while running the script: `ruby -J-Xmx8000m compute_similarities.rb -f traj_out_cars1_fc_4.txt`.
+ List of script arguments:
 +  **DATASET** a text file located at `../output/trajectories/`. Is required to run the computation.
 +  **DEBUGMODE** a integer, optional. default value is 0. 1 runt the script using forward differences with stepsize 1, 2 runs the script in test mode to explore trajectories. 
 +  **VARIANCETYPE** a integer, optional. Defines whether the local or global variance should be used. By default the local variance will be used. Passing a 0 will force the script to use the global variance. Passing anything else has no effect and the script will use the local variances during computation.
