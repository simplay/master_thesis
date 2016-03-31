#ifndef FLOW_INCLUDED
#define FLOW_INCLUDED

using namespace cv;

void SFrigidGLOBAL(Mat &Px, Mat &Py, Mat &Pz, const Mat &P, Mat &O, const Mat &D, CvPoint2D32f punto[],int band[], float fcx, float fcy, float cx, float cy, int pyr);
void SFrigidLOCAL(Mat &Px, Mat &Py, Mat &Pz, const Mat &Tx, const Mat &Ty, const Mat &Tz, const Mat &Wx, const Mat &Wy, const Mat &Wz, const Mat &D, CvPoint2D32f punto[],int band[], float fcx, float fcy, float cx, float cy, int pyr);
void LocalRigid(Mat &Tx, Mat &Ty, Mat &Tz, Mat &Wx, Mat &Wy, Mat &Wz, const Mat &P, const Mat &O, int band[], int pyr);
void consistencySF(const Mat &Px, const Mat &Py, const Mat &Pz, CvPoint2D32f punto[], const Mat &gray0, const Mat &gray1, const Mat &DEPTH0, const Mat &DEPTH1, Mat &diffI, Mat &diffD, Mat &Iwarped, Mat &Dwarped, int band[], int band2[], float fcx, float fcy, float cx, float cy, int constZ);
void ImChanges(Mat &I0, Mat &I1, Mat &Ic);
#endif 
