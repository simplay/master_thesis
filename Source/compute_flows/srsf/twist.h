#ifndef TWIST_INCLUDED
#define TWIST_INCLUDED

using namespace cv;

void creaGrillaLK(Mat &grillaX, Mat &grillaY, CvPoint2D32f punto, int w2);
void warpLK(float Px, float Py, float Pz, CvPoint2D32f punto, CvPoint2D32f &Npunto, const Mat &DD, int &bandSF, int &bandOF, float fcx, float fcy, float cx, float cy);
void warpLKrig_pyr(const Mat &P, const Mat &O, Mat &DZ, const Mat &oldX, const Mat &oldY, Mat &newX, Mat &newY, const Mat &D, float fcx, float fcy, float cx, float cy);
void trackLKrig(const Mat &gray0, const Mat &Z0, const Mat &a_gray0, const Mat &a_Z0, const Mat &a_D0, const Mat &Ix0, const Mat &Iy0, const Mat &Zx0, const Mat &Zy0, int Niter, CvPoint2D32f punto[], Mat &Px, Mat &Py, Mat &Pz, Mat &Ox, Mat &Oy, Mat &Oz, Mat &RigX, Mat &RigY, Mat &RigZ, int pyr, float m_rgb[3][3], int WW, float kI, float kZ, float tetaP, float tetaO, int Npx, int Npy, int band[], int step);

#endif 
