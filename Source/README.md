# Master Thesis Pipeline 

**Given**: A sequence of temporal coherent images (movie) and their depth maps.

**Goal**: Segment all objects that have a certain velocity such that objects moving equally are in the same segment. In oder to do so, perform the following steps:

1. Compute their Optical (foreward and backward) Flow.
 + The methods that are used can be found [here](https://github.com/simplay/ma_pipeline)
2. Track sparsely appropriate moveable points.
3. Extract corresponding trajectories
4. compute similarites between all determined trajectoires.
5. compute the segmentation using a special variant of spectral clustering using the trajectory similarities as a cue.

A detailed explanation of the whole pipeline can be found [here](https://github.com/simplay/master_thesis/blob/master/Source/pipeline.md).

## Requirements
+ Java >= 7
+ Matlab
+ JRuby
+ Bundler

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

## Usage

### Run the example Dataset

The example dataset is located at `./Data/example`.

1. Generate the flow fields
 1. Go to `./Source/compute_flows/`
 2. Run `./run.sh -guided`
 3. Follow the instructions given by the guided mode.
 4. The generatated flows will be loacted at `./Data/example/ldof/`
2. Extract core data
 + **a)** Go to `./Source/initialize_data/`
 + **b)** Open the matlab script `init_data.m`
 + **c)** Change set the following parameters as described: 
 ```
  DATASETNAME = 'example'
  COMPUTE_TRACKING_DATA = true;
  COMPUTE_FLOW_VARIANCES = true;
  COMPUTE_CIE_LAB = true; 
  EXTRACT_DEPTH_FIELDS = false;
  COMPUTE_DEPTH_VARIANCE = false;
 ```
 + **d)** Run this matlab script
 + **e)** The extracted data is dumped to `.Source/output/tracking_data/example`
3. Generate the affinity matrix by running the pipeline core
 1. Open `Source/core/` with an Java IDE (either Intellij or Eclipse)
 2. Run the Main class.
 3. Provide the following runtime arguments `-d example -task 2 -var 1 -nn 30 -nnm top -lambda 0.01` before executing `main.java`.
 4. The generated output is dumped to `.Source/output/similarity/`
4. Run a segmentation method (in this example, we use the spectral clustering method)
 + **a)** Go to `./Source/segmentation/`
 + **b)** Open the matlab script `main_spectral_clustering.m`
 + **c)** Set the following parameters:
 ```
  DATASET = 'example';
  PREFIX_OUTPUT_FILENAME = 'pd_top';
  PREFIX_INPUT_FILENAME = 'pd_top_30';
  COMPUTE_EIGS = true;
  REUSE_ASSIGNMENTS = false;
  COMPUTE_FULL_RANGE = true;
  FORCE_EW_COUNT = 2;
  CLUSTER_CENTER_COUNT = 3;
 ```
 + **d)** The generated segmentations are stored in `./Source/output/clustering/example_ldof_pd_top_30_c_3_ev_2/`
 
 ### Pipeline statge details

+ Define a dataset as described [here](https://github.com/simplay/master_thesis/blob/master/Data/README.md).
+ Generate flow field data as described [here](https://github.com/simplay/master_thesis/blob/reworking-precomp-flow/Source/compute_flows/README.md).
+ Extract important pipeline data by running the scripts located at `./initialize_data/`. Please read its [Readme](https://github.com/simplay/master_thesis/tree/reworking-precomp-flow/Source/initialize_data).
+ Extract trajectories and compute their similarity matrix by running the code located at `./core/`. Please Read its [README](https://github.com/simplay/master_thesis/blob/reworking-precomp-flow/Source/core/README.md) beforehand.
+ Compute the motion segmentations by applying either:
 + The **Spectral clustering** or **Min Cut** method. Their scripts are located at `./segmentation/`. or
 + The **Kernighan Lin** method, located at `./segmentation/kernighan_lin/`. Please read the provided README files beforehand to obtain further information about the usages and the generated output.
 + Run the evaluation of the generated dataset by running the scripts located at `./eval/`.
 
In general, please all provided README files carefully before running any code.


## Sources

### Large Distance Optical Flow: Can deal with occlusion
+ **LDOF**: Generated by T. Brox' freely available [Motion Segmenation Code](http://lmb.informatik.uni-freiburg.de/resources/binaries/eccv2010_mosegLinux64.zip). 

### Point Tracking that generate Trajectories 
Implementation of [this paper](http://lmb.informatik.uni-freiburg.de/people/brox/pub/sundaram_eccv10.pdf)

### Segmentation via Spectral clustering of Trajectories
Implementation of [this paper](http://ieeexplore.ieee.org/stamp/stamp.jsp?tp=&arnumber=6682905)
