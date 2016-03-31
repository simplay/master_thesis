#ifndef RIGID_INCLUDED
#define RIGID_INCLUDED

using namespace cv;

void creaGrilla(Mat &grillaX, Mat &grillaY);
void warp(const Mat &P, const Mat &O, CvPoint2D32f punto[], CvPoint2D32f Npunto[], const Mat &D, int bandSF[], int bandOF[], float fcx, float fcy, float cx, float cy);
void warp_pyr(const Mat &P, const Mat &O, const Mat &Px, const Mat &Py, const Mat &Pz, Mat &DZ, Mat &newX, Mat &newY, const Mat &D, float fcx, float fcy, float cx, float cy);
void jacobian_pyr(Mat &Jx1, Mat &Jy2, Mat &Jx3, Mat &Jy3, Mat &J11, Mat &J12, Mat &J13, Mat &J21, Mat &J22, Mat &J23, const Mat &newX, const Mat &newY, const Mat &D, float fcx, float fcy, float cx, float cy );
void trackPoints(const Mat &gray0, const Mat &Z0, const Mat &a_gray0, const Mat &a_Z0, const Mat &a_D0, const Mat &Ix0, const Mat &Iy0, const Mat &Zx0, const Mat &Zy0, int Niter, CvPoint2D32f punto[], Mat &P, Mat &O, const Mat &Px, const Mat &Py, const Mat &Pz, int pyr, int dd, float m_rgb[3][3], int WW, float kI, float kZ, int Npx, int Npy, int band[]);
void update(Mat &P, Mat &O, Mat &d);
void skew3(Mat &R, const Mat &w);
void expRODRI(Mat &R, const Mat &w);
void logRODRI(const Mat &R, Mat &w);
void expRigid(Mat &SE, const Mat &P, const Mat &w);
void logRigid( const Mat &SE, Mat &P, Mat &w);

#endif 
