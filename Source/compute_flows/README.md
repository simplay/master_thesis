# Compute Flows

Generate the forward-and backward flow fields of a provided dataset using a certain flow method. 
The dataset has to be structured as described in the README at `.Data/README.md`.

+ Method 1: [Large Displacement Optical Flow (linux)](http://lmb.informatik.uni-freiburg.de/resources/binaries/pami2010Linux64.zip) - T. Brox, J. Malik
+ Method 2: [Scene flow from RGB-D sequences](https://drive.google.com/file/d/0B7IdP8eYshy8bUhUVmd0UTVZTXM/view) - J. Quiroga, Thomas Brox, F. Devernay, J. Crowley

## Usage

Run `./run.sh -guided` and you will be guided through the genration process.

First you will be asked to select a flow method, followed by another question which dataset located at `../../Data/` should be used.

Manual Usage: `./run.sh REL_DS_PATH FLOW_METHOD START_FR END_FR SKIP`

+ **REL_DS_PATH**: The relative path to the target dataset. E.g. `../../Data/my_dataset/`
+ **FLOW_METHOD**: An Integer defining mapping to a flow method, 0 = LDOF, 1 = SRSF.
+ **START_FR**: First frame index that should be used from a well-enumerated, given dataset.
+ **END_FR**: Last frame index that should be used from a given, well-enumerated dataset.
+ **SKIP**: Integer, encoding a boolean, indicating whether the computation of the flows be skipped. True = 1, 0 = false.

The flow fields for all frames with and in between the starting and ending frame will be computed.

Run `./run.sh -guided -skip` will skip the flow generation but will generate the `used_input.txt` file.

## Outputs

The generated output will directly be writting into the root of of the chosen dataset directory.
In particular the following output is generated:

+ If not initially given, **.ppm** versions of the initially provided image sequence are generated (named the same as the initial images).
+ The flow fields: According to the selected flow method (during the guidance), a subdirectory called either **ldof** or **srsf** will be generated, containing the frame-wise backward-and forward flow fields. The flow files are in the file format .flo. Please have a look at [here](http://vision.middlebury.edu/flow/code/flow-code/README.txt) for futher information about the _.flo_ file extension format:

```
".flo" file format used for optical flow evaluation

Stores 2-band float image for horizontal (u) and vertical (v) flow components.
Floats are stored in little-endian order.
A flow value is considered "unknown" if either |u| or |v| is greater than 1e9.

 bytes  contents

 0-3     tag: "PIEH" in ASCII, which in little endian happens to be the float 202021.25
         (just a sanity check that floats are represented correctly)
 4-7     width as an integer
 8-11    height as an integer
 12-end  data (width*height*2*4 bytes total)
         the float values for u and v, interleaved, in row order, i.e.,
         u[row0,col0], v[row0,col0], u[row0,col1], v[row0,col1], ...
```

+ A text file called **used_input.txt**: An association file listing the *ppm, forward, and backward flow in their correct order. The file looks like the following:

```
#use
1
4
#imgs
01.ppm
02.ppm
03.ppm
04.ppm
#fwf
ForwardFlow000.flo
ForwardFlow001.flo
ForwardFlow002.flo
#bwf
BackwardFlow000.flo
BackwardFlow001.flo
BackwardFlow002.flo
```

## Example Usages

+ Running method 1:

 + Given a dataset in data/chair, having enumerated images from index 1 to 10, we want to compute all flows of its images:
Run `./run.sh chair 1`

 + Given a dataset in data/chair, having enumerated images from index 1 to 10, we want to compute the flows from image 3 to image 7:
Run `./run.sh chair 1 3 7`

 + Given a dataset in data/chair3, having enumerated images from index 250 to 351, we want to compute all flows flows of its images:
Run `./run.sh 1 chair3`

+ Running method 2:
 + `./run.sh foo 2`
