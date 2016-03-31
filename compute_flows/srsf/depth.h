#ifndef DEPTH_INCLUDED
#define DEPTH_INCLUDED

using namespace cv;

void readyDepth(Mat &DEPTHalign, Mat &Z0, Mat &D0, int constZ, int MAXZ, int MINZ);
void buenPunto(Mat Z, CvPoint2D32f punto[], int band[], int Npuntos, int MAXZ, int MINZ, int Wx, int Wy);

#endif 


