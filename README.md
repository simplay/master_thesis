# Motion Segmentation on RGB-D Sequences using Optical Flow Fields

+ **Author**: Michael Single (**simplay**)
+ **Supervisor**: Prof. Dr. Matthis Zwicker
+ **Advisor**: Mr. Peter Bertholet
+ **Contact**: silent.simplay@gmail.com


| | | | |         
| :---: |:---:| :---:| :---:|
| ![alt tag](https://github.com/simplay/master_thesis/blob/master/Document/Results/segmentations/example/seg_f_1.png) | ![alt tag](https://github.com/simplay/master_thesis/blob/master/Document/Results/segmentations/example/seg_f_2.png) | ![alt tag](https://github.com/simplay/master_thesis/blob/master/Document/Results/segmentations/example/seg_f_3.png) | ![alt tag](https://github.com/simplay/master_thesis/blob/master/Document/Results/segmentations/example/seg_f_4.png)
| **Frame 1** | **Frame 2** | **Frame 3** | **Frame 4**

## Abstract
The task of an accurate detection and extraction of the moving objects in a video, captured by a moving camera, is nowadays still a very challenging problem. In this thesis, we present a method for producing spatio-temporal consistent motion segmentation from RGB-D videos by using optical flow. Our framework consists of an optical flow estimation, motion trajectory tracking, affinity matrix formation and a segmentation stage. Our implementation can produce sparse as well as dense motion segmentations. Furthermore, we implemented several flow methods, similarity measures and segmentation techniques and examined their influence on the final segmentation quality. Finally, we quantitatively evaluated the quality of our segmentation results and determined an optimal pipeline assignment. In particular, we could successfully demonstrate, that incorporating depth data into our pipeline produces best results. 

## Project Structure

+ `Animations/`:
+ `Applications/`:
+ `Data/`:
+ `Document/`:
+ `Presentation/`:
+ `Source/`: Contains the source code of the pipeline. For further information read the corresponding [README](https://github.com/simplay/master_thesis/blob/master/Source/README.md).

## License

This project is licensed under the [MIT License](https://github.com/simplay/master_thesis/blob/master/LICENSE).
