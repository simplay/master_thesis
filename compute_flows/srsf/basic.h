#ifndef BASIC_INCLUDED
#define BASIC_INCLUDED

using namespace cv;

void escalar(Mat &I, float s);			// scale by s
void revisa(Mat &I);				// change NaN by 0s
void revisa3(Mat &I1, Mat &I2, Mat &I3);	// change Nan by 0s
void sqrtM(Mat &I);
void phi(const Mat &I, Mat &Iphi, const float eps, const float clip);		// Robust norm
float suma(const Mat &I);			// sum of matrix elements
void setcols(int r, int g, int b, int k);
void makecolorwheel();
CvScalar computeColor(float fx, float fy);
CvScalar colormap(float val,float max);
float findMAX3D(float X, float Y, float Z, float min);
void coloreaSF(Mat &RGB, const Mat &P, CvPoint2D32f punto[], int Npuntos, int band[], float max);
void coloreaOF(Mat &RGB, CvPoint2D32f punto[], CvPoint2D32f NEWpunto[], int Npuntos, float max, int band[]);
float maximo(const Mat &P, int band[]);
void reduce(Mat & P0, Mat & P1, int band[], int pyr);
void UpSize(Mat & P1, Mat & P0, int band[], int pyr1, int pyr0);
void ConvertBand(int band2[], int band[], int pyr);
void DownSize(Mat & P0, Mat & P1, int band[], int pyr);
void ComputeMask(int band[], Mat &P, int pyr);
extern int width[6];
extern int height[6];
#endif 


