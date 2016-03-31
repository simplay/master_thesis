#include <opencv2/imgproc/imgproc_c.h>
#include "opencv2/highgui/highgui.hpp"
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/legacy/legacy.hpp>
#include <opencv2/opencv.hpp>

#include <stdio.h>
#include <math.h>
#include "rigid.h"
#include "flow.h"
#include "basic.h"

static float NaN = 0.0/0.0;
static int Npx = width[0];
static int Npy = height[0];

void SFrigidGLOBAL(Mat & Px, Mat & Py, Mat & Pz, const Mat & P, Mat & O, const Mat & D, CvPoint2D32f punto[], int band[], float fcx, float fcy, float cx, float cy, int pyr)
{
	int w = D.cols;

	Mat X = Mat(3, 1, CV_32FC1);
	float *verX = (float *) X.data;

	float *verP = (float *) P.data;
	float *verO = (float *) O.data;

	float *DD = (float *) D.data;
	float *PPx = (float *) Px.data;
	float *PPy = (float *) Py.data;
	float *PPz = (float *) Pz.data;
	float Z;

	float pot = pow(2.0, pyr);
	fcx = fcx / pot;
	fcy = fcy / pot;
	cx = cx / pot;
	cy = cy / pot;

	Mat Rot = Mat(3, 3, CV_32FC1);
	expRODRI(Rot, O);

	Mat nX = Mat(3, 1, CV_32FC1);
	float *vernX = (float *) nX.data;

	int cont = 0;

	for (int i = 0; i < height[pyr]; i++) {

		int ii = i * width[0] * pot;

		for (int j = 0; j < width[pyr]; j++) {

			int point = ii + j * pot;

			if (band[point] == 1) {
				float px = (punto[point].x / pot);
				float py = (punto[point].y / pot);

				Z = 1 / DD[point];
				verX[0] = Z * (px - cx) / fcx;
				verX[1] = Z * (py - cy) / fcy;
				verX[2] = Z;

				nX = Rot * X + P;

				PPx[cont] = vernX[0] - verX[0];
				PPy[cont] = vernX[1] - verX[1];
				PPz[cont] = vernX[2] - verX[2];

			}
			cont++;
		}
	}
}

void SFrigidLOCAL(Mat & Px, Mat & Py, Mat & Pz, const Mat & Tx, const Mat & Ty, const Mat & Tz, const Mat & Wx, const Mat & Wy, const Mat & Wz, const Mat & D, CvPoint2D32f punto[], int band[], float fcx, float fcy, float cx, float cy, int pyr)
{
	Mat X = Mat(3, 1, CV_32FC1);
	Mat nX = Mat(3, 1, CV_32FC1);
	float *verX = (float *) X.data;
	float *vernX = (float *) nX.data;

	float *DD = (float *) D.data;
	float *px = (float *) Px.data;
	float *py = (float *) Py.data;
	float *pz = (float *) Pz.data;
	float *tx = (float *) Tx.data;
	float *ty = (float *) Ty.data;
	float *tz = (float *) Tz.data;
	float *ox = (float *) Wx.data;
	float *oy = (float *) Wy.data;
	float *oz = (float *) Wz.data;
	float Z;

	Mat O = Mat(3, 1, CV_32FC1);
	Mat P = Mat(3, 1, CV_32FC1);
	float *o = (float *) O.data;
	float *p = (float *) P.data;

	Mat Rot = Mat(3, 3, CV_32FC1);


	float pot = pow(2.0, pyr);
	fcx = fcx / pot;
	fcy = fcy / pot;
	cx = cx / pot;
	cy = cy / pot;

	int cont = 0;
	int Npoint;

	for (int i = 0; i < height[pyr]; i++) {

		int ii = i * width[0] * pot;

		for (int j = 0; j < width[pyr]; j++) {

			int point = ii + j * pot;

			if (band[point] == 1) {

				float x = (punto[point].x / pot);
				float y = (punto[point].y / pot);

				Z = 1 / DD[point];
				verX[0] = Z * (x - cx) / fcx;
				verX[1] = Z * (y - cy) / fcy;
				verX[2] = Z;

				Mat Rot = Mat(3, 3, CV_32FC1);
				o[0] = ox[cont];
				o[1] = oy[cont];
				o[2] = oz[cont];
				expRODRI(Rot, O);
				p[0] = tx[cont];
				p[1] = ty[cont];
				p[2] = tz[cont];

				nX = Rot * X + P;

				px[cont] = vernX[0] - verX[0];
				py[cont] = vernX[1] - verX[1];
				pz[cont] = vernX[2] - verX[2];

			}
			cont++;
		}
	}
}

void LocalRigid(Mat & Tx, Mat & Ty, Mat & Tz, Mat & Wx, Mat & Wy, Mat & Wz, const Mat & P,const Mat & O, int band[], int pyr)
{
	float *p = (float *) P.data;
	float *o = (float *) O.data;
	float *tx = (float *) Tx.data;
	float *ty = (float *) Ty.data;
	float *tz = (float *) Tz.data;
	float *wx = (float *) Wx.data;
	float *wy = (float *) Wy.data;
	float *wz = (float *) Wz.data;

	float pot = pow(2.0, pyr);
	int cont = 0;

	for (int i = 0; i < height[pyr]; i++) {

		int ii = i * width[0] * pot;

		for (int j = 0; j < width[pyr]; j++) {

			int point = ii + j * pot;

			if (band[point] == 1) {

				tx[cont] = p[0];
				ty[cont] = p[1];
				tz[cont] = p[2];
				wx[cont] = o[0];
				wy[cont] = o[1];
				wz[cont] = o[2];

			}
			else {

				tx[cont] = 0;
				ty[cont] = 0;
				tz[cont] = 0;
				wx[cont] = 0;
				wy[cont] = 0;
				wz[cont] = 0;
			}
			cont++;
		}
	}
}

void consistencySF(const Mat & Px, const Mat & Py, const Mat & Pz, CvPoint2D32f punto[], const Mat & gray0, const Mat & gray1, const Mat & DEPTH0, const Mat & DEPTH1, Mat & diffI, Mat & diffD, Mat & Iwarped, Mat & Dwarped, int band[], int band2[], float fcx, float fcy, float cx, float cy, int constZ)
{

	int w = Px.cols;
	int h = Px.rows;
	int wh = w * h;

	float *PPx = (float *) Px.data;
	float *PPy = (float *) Py.data;
	float *PPz = (float *) Pz.data;
	float dx, dy, dz;

	ushort *D0 = (ushort *) (DEPTH0.data);
	ushort *D1 = (ushort *) (DEPTH1.data);

	unsigned char *I0 = (unsigned char *) gray0.data;
	unsigned char *I1 = (unsigned char *) gray1.data;

	unsigned char *dI = (unsigned char *) diffI.data;
	unsigned char *dD = (unsigned char *) diffD.data;
	int difI;

	unsigned char *Iw = (unsigned char *) Iwarped.data;
	unsigned char *Dw = (unsigned char *) Dwarped.data;

	float X[3];
	float nX[3];

	int cont = 0;

	while (cont < wh) {
		if (band[cont] == 1) {

			float xx = punto[cont].x - cx;
			float yy = punto[cont].y - cy;
			int d = D0[cont];

			float Z = float (d)/constZ;

			X[0] = Z * xx / fcx;
			X[1] = Z * yy / fcy;
			X[2] = Z;

			nX[0] = X[0] + PPx[cont];
			nX[1] = X[1] + PPy[cont];
			nX[2] = X[2] + PPz[cont];

			xx = fcx*nX[0]/nX[2] + cx;
			yy = fcy*nX[1]/nX[2] + cy;


			if (xx >= 0 && xx < (width[0] - 1) && yy >= 0 && yy < (height[0] - 1)) {
				
				int pos0 = cont;
				int pos1 = width[0] * round(yy) + round(xx);
				int x1 = int (xx);
				int x2 = x1 + 1;
				int y1 = int (yy);
				int y2 = y1 + 1;

				float R1 = (x2 - xx) * float (I1[y1 * width[0] + x1]) + (xx - x1) * float (I1[y1 * width[0] + x2]);
				float R2 = (x2 - xx) * float (I1[y2 * width[0] + x1]) + (xx - x1) * float (I1[y2 * width[0] + x2]);
				float P = (y2 - yy) * R1 + (yy - y1) * R2;
				float i1 = P;
				Iw[pos0] = int (P);
				float i0 = (float) I0[pos0];

				/*band2[y1 * width[0] + x1] = 3;
				  band2[y1 * width[0] + x2] = 3;
				  band2[y2 * width[0] + x1] = 3;
				  band2[y2 * width[0] + x2] = 3;*/

				Dw[pos0] = 255 - (int) ((float) D1[pos1] / constZ - PPz[cont]);  	// Ver Depth warped
							
				float d0 = ((float) D0[pos0]);			
				float d1 = ((float) D1[pos1]);

				difI = (int) (abs(i1 - i0));
				//dI[cont] = 2*difI;
				if (difI < 32) {

					dI[cont] = difI * 8;

				} else {

					dI[cont] = 255;
				}
				if (d1 == 0) {

					dD[3 * cont] = 255;
					dD[3 * cont + 1] = 255;
					dD[3 * cont + 2] = 0;

				} else {

					float diff = fabs(d1 / constZ - PPz[cont] - d0 / constZ);

					if (diff < 1){

						dD[3 * cont + 0] = (int) (255 * diff);
						dD[3 * cont + 1] = (int) (255 * diff);
						dD[3 * cont + 2] = (int) (255 * diff);

					} else {

						dD[3 * cont + 0] = (int) (255);
						dD[3 * cont + 1] = (int) (255);
						dD[3 * cont + 2] = (int) (255);

					}
					
					if (diff < 5) band2[cont] = 1;

				}

			} else {

				dI[cont] = 255;
				dD[3 * cont] = 255;
				dD[3 * cont + 1] = 255;
				dD[3 * cont + 2] = 0;
				Iw[cont] = 0;
				Dw[cont] = 0;

			}
		} else {

			dI[cont] = 0;
			dD[3 * cont] = 0;
			dD[3 * cont + 1] = 0*255;
			dD[3 * cont + 2] = 0;
			Iw[cont] = 255;
			Dw[cont] = 255;

		}

		cont++;
	}

}


void ImChanges(Mat &I0, Mat &I1, Mat &Ic)
{

	int cont = 0;
	int w = I0.cols;
	int h = I1.rows;
	int wh = w*h;

	unsigned char *i0 = (uchar *) (I0.data);
	unsigned char *i1 = (uchar *) (I1.data);
	unsigned char *ic = (uchar *) (Ic.data);

	float i;

	while (cont < wh) {

		i = (float)i0[cont]+(float)i1[cont];
		ic[cont] = (int)(i/2);

		cont++;
	}

}












