Mac-OS 64bit binaries for running Large Displacement Optical Flow 

Copyright (c) 2010 Thomas Brox

------------------------------
Terms of use
------------------------------

This program is provided for research purposes only. Any commercial
use is prohibited. If you are interested in a commercial use, please 
contact the copyright holder. 

If you used this program in your research work, you should cite the 
following publication:

Thomas Brox, Jitendra Malik. 
Large Displacement Optical Flow: Descriptor Matching in Variational
Motion Estimation, IEEE Transactions on Pattern Analysis and Machine 
Intelligence, 33(3):500-513, 2011. 

This program is distributed WITHOUT ANY WARRANTY.

------------------------------
Usage
------------------------------

This will run LDOF with the standard parameter setting sigma=0.8,
alpha=30, beta=300, gamma=5:

./ldof tennis492.ppm tennis493.ppm

This parameter setting works well on a variety of typical image 
sequences. If your setting is more special, you should try to adapt 
the parameters to your special needs:

./ldof image1.ppm image2.ppm sigma alpha beta gamma

Image1 and image2 must be images in the binary PPM format (P6). 
Please convert any other image formats into PPM using convert or
mogrify. 

See Middlebury flow page
http://vision.middlebury.edu/flow/submit/
for description of the flo-format.

The ppm result is for checking the result visually. Depending on the 
magnitude of the flow vectors, the visualization might not show all 
details. In this case you should use your own visualization method. 

------------------------------
Bugs
------------------------------

Please report any bugs to Thomas Brox

