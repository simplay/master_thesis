# Core

Core of the pipeline. Is responsible for tracking points, extracting trajectories and computing the trajectory affinity matrix.

## Runtime arguments

VM options: `-Xmx16000m`

## Usage example

+ Compute similarities using the MD similarity task using local variances for normalization with depth cues, using the cut probability p=0.99 on the dataset c14 and exporting the output with a custom prefix called _foobar_:
 + `-d c14 -task 1 -var 1 -nn 200 -depth 1 -prob 0.99 -prefix foobar`

### Program arguments: 
+ **-d NAME**: (**req**) The NAME of the target dataset. Has to correspond to a dataset located at `../data/ldof/`.

+ **-task ID**: (**req**) The target similarity task given by a number ID.
 + ID == 1: Summed distances (SD) similarity task.
 + ID == 2: Product distances (PD) similarity task.
 
+ **-var BOOL**: Indicates whether the local flow variance values should be used for normalizing similarity values. By default, the global variance values are used.
 + BOOL == 0: Use global flow variance values for normalization.
 + BOOL == 1: Use local flow variance values for normalization.
 
+ **-depth BOOL** should depth cues be used. By default no depth cues are used.
 + BOOL == 0: Do not use any depth cues.
 + BOOL == 1: Use depth field to transform tracked points to the Euclidian space to have a better measure for the average spatial distance.
 
+ **-nn NUM**: The number NUM of spatial nearest neighbors that should be exported as output. By default the 200 nearest neighbors are exported.

+ **-debug BOOL**:  should the program run in the debug mode. If so, it will dumb intermediate calculated data. By default, the debug mode is disabled.

+ **-prob FLOAT**: Set value FLOAT of cut probability used for the PD similarity task. FLOAT is a supposed to be a value: FLOAT > 0 AND FLOAT < 1. By default the probability value 0.5 is used.

+ **-prefix NAME** custom output file name prefix NAME, by default the empty string.

## Generated Output Files


