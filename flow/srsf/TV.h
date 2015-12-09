#ifndef TV_INCLUDED
#define TV_INCLUDED

using namespace cv;

void TV_ROF(Mat &P, int iter, float tau, float teta);
void TV_Moreno(Mat &Px, Mat &Py, Mat &Pz, int iter, float teta);
void chambolle(Mat &P, int iter, float tau, float teta);
void divergence(const Mat &Gx, const Mat &Gy, Mat &DivG);
void gradiente(const Mat &I, Mat &Ix, Mat &Iy);
void projectionK(float &a11, float &a12, float &a21, float &a22, float &a31, float &a32);
void normaliza(Mat &dGx, Mat &dGy, Mat &Gx, Mat &Gy);

#endif 
