This program is for scientific use only. Any commercial use of this
package or parts of it is prohibited.
________________________________________________________________

Motion segmentation binary for 64 bit Linux
________________________________________________________________

(c) Margret Keuper 2015

If you use this program, you should cite the following paper:

M. Keuper, B. Andres, T. Brox: Motion Trajectory Segmentation via Minimum Cost Multicuts, 
IEEE International Conference on Computer Vision (ICCV), 2015 

---------------

Usage: 

./motionseg_release bmfFile startFrame numberOfFrames sampling prior

bmfFile is a text file with a very short header, comprising the 
number of images in the sequence and 1. After the header all 
image files of the sequences are listed separated by line breaks. 
See marple2.bmf for an example. All input files must be in the 
PPM format (P6). 

startFrame is the frame where the computation is started. 
Usually this is frame 0. If you want to start later in the 
sequence you may specify another value.

numberOfFrames is the number of frames for which you want to 
run the computation. Make sure that the value is not larger than 
the total number of frames of your sequence.

sampling specifies the subsampling parameter. If you specify 8 (a 
good default value), only every 8th pixel in x and y direction is 
taken into account. If you specify 1, the sampling will be dense 
(be careful, memory consumption and computation time will be very 
large in this setting). 

prior specifies the prior cut probability. The higher this value is 
chosen, the more segments will be generated. For good performance, choose 0.5.


The output can be found in a subdirectory of the directory where 
the input sequence is stored. It comprises a text file 
TracksNumberOfFrames.dat with all the tracks and their labels. 


Please report bugs to keuper@informatik.uni-freiburg.de

