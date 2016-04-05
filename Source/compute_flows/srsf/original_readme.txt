Binaries for running dense Semi-Rigid Scene Flow (SRSF) V 1.0

Copyright (c) 2014 Julian Quiroga

------------------------------
Terms of use
------------------------------

This program is provided for research purposes only. Any commercial
use is prohibited. If you are interested in a commercial use, please 
contact the copyright holder. 

If you used this program in your research work, you should cite the 
following publication:

@inproceedings{quirogaECCV2014,
year={2014},
booktitle={European Conference on Computer Vision},
title={Dense Semi-rigid Scene Flow Estimation from RGBD Images},
keywords={motion; scene flow; RGBD image},
author={Quiroga, Julian and Brox, Thomas and Devernay, Frederic and Crowley, James},
pages={567-582},
}

This program is distributed WITHOUT ANY WARRANTY.

------------------------------
Install
------------------------------

For compiling, go to the source folder and type make

------------------------------
Data
------------------------------

i) RGB images should be stored in the folder "Images", in png format and following the label shown in the examples.
ii) Depth images should be stored in the folder Images, in png format, 16 bits and following the label shown in the examples (Depth in mm).
iii) Camera matrix should be stored in the folder "settings".
iv) The 3 components of the Scene Flow {SFx,SFy,SFz} and the Optical Flow {OFx,OFy} are stored in the xml file SFlow in CvMat format. The matrix Flag takes the value 255 if a valid depth measure is available and so that an estimation for this pixel is available. 

------------------------------
Usage
------------------------------

a) To run SRSF with default parameters:

./semirigSF num1 num2 Sel

with

*num1: first image number
*num2: last image number
*Sel: Selection = 0 <- Non-Rigid, 1 <- Rigid, 2 <- Rigid + Non-Rigid

Example:

./semirigSF 10 11 0    -----/ Non-rigid Scene flow between frames 10 and 11

./semirigSF 10 11 1    -----/ Rigid Scene flow between frames 10 and 11

b) To run LGSF adjusting some parameters:

** NON-RIGID estimation

	./semirigSF num1 num2 0 Npyr Nwarps W Step MaxZ Alfa

	with

	Npyr: Levels of the pyramid
	Nwarps: Number of alternations between GN algorithm and TV solver at each level of the pyramid
	W: size of the window -- 2W+1 x 2W+1
	Step: the algorithm process every "Step + 1" pixel (to run faster)
	MaxZ: maximum depth to be processed (cm)
	Alfa: Regularization weight

	Example:

	./semirigSF 20 21 0 2 3 2 1 140 10

** RIGID estimation

	./semirigSF num1 num2 1 Npyr Step MaxZ

	with

	Npyr: Levels of the pyramid
	Step: the algorithm process every "Step + 1" pixel (to run faster)
	MaxZ: maximum depth to be processed (cm)

	Example:

	./semirigSF 10 11 1 2 1 100

** RIGID plus NON-RIGID estimation

	./semirigSF num1 num2 2 Npyr Nalter Nwarps W Step MaxZ Alfa

	with

	Npyr: Levels of the pyramid
	Nalter: Number of alternations between RIGID and NON-RIGID estimation at each level of the pyramid
	Nwarps: Number of alternations between GN algorithm and TV solver at each level of the pyramid
	W: size of the window -- 2W+1 x 2W+1
	Step: the algorithm process every "Step + 1" pixel (to run faster)
	MaxZ: maximum depth to be processed (cm)
	Alfa: Regularization weight
	
	Example:

	./semirigSF 20 21 2 2 3 1 2 1 140 10


Additional parameters can be set directly in mainSRSF.cpp

------------------------------
Bugs and Comments
------------------------------

Please report to Julian Quiroga (julian.quiroga@inria.fr)

