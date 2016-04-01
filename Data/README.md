# Dataset Structure

This README describes the expected structure of any dataset such that it can be used by the pipeline.

## Quick start

+ Minimal dataset: 

## Detailed explanation

Every available dataset has to be contained in its own subfolder within `./Data/`. 

A **minimal** valid **dataset** consists of a coherently **well-enumerated** (color) image sequence (i.e the frames of a video).
In our case, Well-enumerated means that **n-th frame** of the image sequence has the **name** **n**.
Note that the pipeline offers a renaming script which renames the images files that they are _well-enumerated_.
This script can be found at `.Source/normalize_sensor_data/`.

Currently, **.png** and **.ppm** images are supported.

**Example**: Assume we have a dataset called _foo_, consiting of 3 png images, called `1.png, 2.png, 3.png`. 
Then we have to:

1. Create a subfolder `./Data/foo/`.
2. Put the png images into `foo/`.
