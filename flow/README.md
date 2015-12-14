# Flow
+ Method 1: [Large Displacement Optical Flow (linux)](http://lmb.informatik.uni-freiburg.de/resources/binaries/pami2010Linux64.zip) - T. Brox, J. Malik
+ Method 2: [Scene flow from RGB-D sequences](https://drive.google.com/file/d/0B7IdP8eYshy8bUhUVmd0UTVZTXM/view) - J. Quiroga, Thomas Brox, F. Devernay, J. Crowley
## Usage

+ Running method 1:

 + Given a dataset in data/chair, having enumerated images from index 1 to 10, we want to compute all flows of its images:
Run `./run.sh chair 1`

 + Given a dataset in data/chair, having enumerated images from index 1 to 10, we want to compute the flows from image 3 to image 7:
Run `./run.sh chair 1 3 7`

 + Given a dataset in data/chair3, having enumerated images from index 250 to 351, we want to compute all flows flows of its images:
Run `./run.sh 1 chair3`

+ Running method 2:
 + `./run.sh foo 2`
