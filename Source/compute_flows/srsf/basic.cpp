#include <opencv2/imgproc/imgproc_c.h>
#include "opencv2/highgui/highgui.hpp"
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/legacy/legacy.hpp>
#include <opencv2/opencv.hpp>

#include <stdio.h>
#include <math.h>
#include "rigid.h"
#include "basic.h"

static float NaN = 0.0/0.0;
int ncols = 0;
#define MAXCOLS 60
int colorwheel[MAXCOLS][3];

using namespace cv;
using namespace std;

void escalar(Mat &I, float s)
{
	int w = I.cols;
	int h = I.rows;
	float *dataI = (float*)I.data;
	int wh = w*h;
	float ss = 1/s;

	for(int i=0; i< wh; i++ ){

		dataI[i] = ss*dataI[i];
	}

}

void revisa(Mat &I)
{
	int w = I.cols;
	int h = I.rows;
	float *dataI = (float*)I.data;
	int wh = w*h;

	for(int i=0; i< wh; i++ ) {

		if (dataI[i] != dataI[i]) {

			dataI[i] = 0;
		}
	}

}

void phi(const Mat &I, Mat &Iphi, const float eps, const float clip)
{
	int w = I.cols;
	int h = I.rows;
	float *dataI = (float*)I.data;
	float *data = (float*)Iphi.data;
	float aux;
	int wh = w*h;
	float val = 0.5/sqrt(eps);

	for(int i=0; i< wh; i++ )
	{
		if (dataI[i] != dataI[i]) {

			data[i] = 0;

		} else {    

			aux = dataI[i]*dataI[i];

			if (aux != 0) {

				if (aux > clip) {

					data[i] = 0;

				} else {

					data[i] = 0.5/sqrt(aux+eps);
				}
			}
			else {

				data[i] = val;
			}	
		}
	}
}

void setcols(int r, int g, int b, int k)
{
	colorwheel[k][0] = r;
	colorwheel[k][1] = g;
	colorwheel[k][2] = b;
}

void makecolorwheel()
{
	int RY = 15;
	int YG = 6;
	int GC = 4;
	int CB = 11;
	int BM = 13;
	int MR = 6;
	ncols = RY + YG + GC + CB + BM + MR;
	if (ncols > MAXCOLS)
		exit(1);
	int i;
	int k = 0;
	for (i = 0; i < RY; i++) setcols(255,	   255*i/RY,	 0,	       k++);
	for (i = 0; i < YG; i++) setcols(255-255*i/YG, 255,		 0,	       k++);
	for (i = 0; i < GC; i++) setcols(0,		   255,		 255*i/GC,     k++);
	for (i = 0; i < CB; i++) setcols(0,		   255-255*i/CB, 255,	       k++);
	for (i = 0; i < BM; i++) setcols(255*i/BM,	   0,		 255,	       k++);
	for (i = 0; i < MR; i++) setcols(255,	   0,		 255-255*i/MR, k++);
}

CvScalar computeColor(float fx, float fy)
{
	CvScalar color;
	if (ncols == 0)
		makecolorwheel();

	float rad = sqrt(fx * fx + fy * fy);
	float a = atan2(-fy, -fx) / M_PI;
	float fk = (a + 1.0) / 2.0 * (ncols-1);
	int k0 = (int)fk;
	int k1 = (k0 + 1) % ncols;
	float f = fk - k0;
	//f = 0; // uncomment to see original color wheel
	for (int b = 0; b < 3; b++) {
		float col0 = colorwheel[k0][b] / 255.0;
		float col1 = colorwheel[k1][b] / 255.0;
		float col = (1 - f) * col0 + f * col1;
		if (rad <= 1)
			col = 1 - rad * (1 - col); // increase saturation with radius
		else
			col *= .75; // out of range
		color.val[2-b] = (int)(255.0 * col);
	}
	return color;
}

CvScalar colormap(float val,float max)
{
	CvScalar color;
	float nivel = val / max;

	if ( val != val )
	{
		color.val[0] = 0;
		color.val[1] = 0;
		color.val[2] = 0;
	}
	else
	{
		if (nivel >= 1 )
		{
			color.val[0] = 0;
			color.val[1] = 0;
			color.val[2] = 255;
		}
		else if (nivel >= 0.5 )
		{
			nivel = 2*(nivel - 0.5);
			color.val[0] = 0;
			color.val[1] = 255*(1-nivel);
			color.val[2] = 255;
		}
		else if (nivel > 0 )
		{
			nivel = 2*nivel;
			color.val[0] = 0;
			color.val[1] = 255;
			color.val[2] = 255*nivel;
		}
		else if (nivel == 0 )
		{
			color.val[0] = 0;
			color.val[1] = 255;
			color.val[2] = 0;
		}
		else if (nivel >= -0.5 )
		{
			nivel = 2*(nivel + 0.5);
			color.val[0] = 255*(1-nivel);
			color.val[1] = 255;
			color.val[2] = 0;
		}
		else if (nivel >= -1 )
		{
			nivel = 2*(nivel + 1);
			color.val[0] = 255;
			color.val[1] = 255*nivel;
			color.val[2] = 0;
		}
		else
		{
			color.val[0] = 255;
			color.val[1] = 0;
			color.val[2] = 0;
		}
	}
	return color;
}

float findMAX3D(float X, float Y, float Z, float min)
{
	float max;

	if (X > Y  && X > Z)
	{
		max = X;	

	} else if (Y > X && Y > Z)
	{
		max = Y;

	} else
	{
		max = Z;
	}

	if (min > max)
	{
		max = min;
	}
	return max;
}

void coloreaSF(Mat &RGB, const Mat &P, CvPoint2D32f punto[], int Npuntos, int band[], float max)
{
	float *verP = (float*)(P.data);
	int cont = 0;

	while (cont < Npuntos) {

		CvScalar color;

		if (band[cont] == 1) {

			color = colormap(verP[cont],max);
		}
		else if (band[cont] == 0) {

			color = cvScalar(0,0,0);
		}

		else {

			color = cvScalar(0,0,0);
		}

		int i = int(punto[cont].y);
		int j = int(punto[cont].x);


		unsigned char* tmp_apun = (unsigned char*) (RGB.data);

		int row = 3*i*width[0];

		tmp_apun[row + 3*j + 0] = int(color.val[0]);
		tmp_apun[row + 3*j + 1] = int(color.val[1]);
		tmp_apun[row + 3*j + 2] = int(color.val[2]);
		cont++;
	}
}

void coloreaOF(Mat &RGB, CvPoint2D32f punto[], CvPoint2D32f NEWpunto[], int Npuntos, float max, int band[])
{
	float M = 0.5*max;
	float MM = -M;
	int w = RGB.cols;
	int cont = 0;

	while (cont < Npuntos) {


		float vx = NEWpunto[cont].x - punto[cont].x;
		float vy = NEWpunto[cont].y - punto[cont].y;

		CvScalar color;

		if (band[cont]== 0) {

			color.val[0] = 255;
			color.val[1] = 255;
			color.val[2] = 255;
		}

		else {

			if (vx > M) {

				vx = M;
			}

			else if (vx < MM) {
				vx = MM;
			}

			if (vy > M) {

				vy = M;
			}

			else if (vy < MM) {

				vy = MM;
			}

			vx = vx/max;
			vy = vy/max;
			color = computeColor(vx,vy);

		}


		int i = int(punto[cont].y);
		int j = int(punto[cont].x);

		unsigned char* tmp_apun = (unsigned char*)RGB.data;

		int row = 3*i*w;
		tmp_apun[row + 3*j + 0] = int(color.val[0]);
		tmp_apun[row + 3*j + 1] = int(color.val[1]);
		tmp_apun[row + 3*j + 2] = int(color.val[2]);        
		cont++;

	}

}
float maximo(const Mat &P, int band[])
{
	float *verP = (float*)P.data;
	int w = P.cols;
	int h = P.rows;
	int wh = w*h;
	float M = 0;
	int cont = wh;
	while (cont >0)
	{
		if (band[cont] == 1) {
			if (verP[cont]>0)
			{
				if(verP[cont]> M)
				{
					M = verP[cont];
				}
			}
			else 
			{
				if(verP[cont] < -M)
				{
					M = -verP[cont]; 
				}
			}}
		cont--;
	}
	return M;
}


void reduce(Mat & P0, Mat & P1, int band[], int pyr)
{
	int w = P1.cols;
	int h = P1.rows;

	float *verP0 = (float *) P0.data;
	float *verP1 = (float *) P1.data;

	float pot = pow(2.0, pyr);

	int cont = 0;

	for (int i = 0; i < h; i++) {

		int ii = i * width[0] * pot;

		for (int j = 0; j < w; j++) {

			int point = ii + j * pot;

			if (band[point] == 1) {

				verP1[cont] = verP0[point];
			}

		}
		cont++;
	}
}

void UpSize(Mat & P1, Mat & P0, int band[], int pyr1, int pyr0)
{
	int w1 = P1.cols;
	int h1 = P1.rows;
	int w0 = P0.cols;
	int h0 = P0.rows;
	int N = width[0]*height[0];

	float *verP0 = (float *) P0.data;
	float *verP1 = (float *) P1.data;

	float pot = pow(2.0, pyr0);
	float delta = pow(2.0, pyr1-pyr0);

	int cont = 0;

	for (int i = 0; i < h0; i++) {

		int ii = i * width[0] * pot;
		int ii2 = i/delta;

		for (int j = 0; j < w0; j++) {

			int point = ii + j * pot;
			int point2 = ii2*w1 + j/delta;


			if (band[point] == 1) {
				verP0[cont] = verP1[point2];
			}
			else {
				//verP0[cont] = NaN;
				verP0[cont] = 0;
			}

			cont++;
		}

	}
}

void ConvertBand(int band2[], int band[], int pyr0)
{
	int cont;
	float pot = pow(2.0, pyr0);
	float check;
	int valor;
	int loop;
	if (pyr0 == 0)
	{	
		cont = 0;
		while (cont < width[0]*height[0])
		{
			if (band[cont] == 1) 
				band2[cont] = band[cont];
			cont++;
		}
	}
	else
	{
		for (int i = 0; i < height[0]; i=i+pot) {

			int ii = i * width[0];

			for (int j = 0; j < width[0]; j=j+pot) {

				int pos = ii + j;
				valor = 0;
				loop = 1;

				for (int f = 0; f < pot && loop == 1 ; f++) {
					for (int c = 0; c < pot && loop == 1; c++) {

						if (band[pos + f*width[0] + c] == 1)
						{
							check = 1;
							loop= 0;
							valor = 1;
						}   
					}
				}

				for (int f = 0; f < pot; f++) {
					for (int c = 0; c < pot; c++) {

						band2[pos + f*width[0] + c] = valor;

					}
				}


			}
		}
	}
}

void DownSize(Mat & P0, Mat & P, int band[], int pyr)
{
	int w = P.cols;
	int h = P.rows;
	int N = width[0]*height[0];

	float *verP0 = (float *) P0.data;
	float *verP = (float *) P.data;

	float pot = pow(2.0, pyr);
	int cont = 0;
	float suma;
	int L;

	for (int i = 0; i < h; i++) {

		int ii = i * width[0] * pot;

		for (int j = 0; j < w; j++) {

			int point = ii + j * pot;

			suma = 0;
			L = 0;

			for (int f = 0; f < pot; f++) {
				for (int c = 0; c < pot; c++) {

					if (band[point+f*width[0]+c] == 1) {
						suma = suma + verP0[point+f*width[0]+c]; 
						L++;
					}
				}
			}
			//printf("%i \n",L);
			if (L == 0) {
				verP[cont] = NaN;
				//verP[cont] = 0;
			}
			else {
				verP[cont] = suma/float(L);
			}	
			cont++;

		}
	}
}

void ComputeMask(int band[], Mat &P, int pyr)
{
	int w = P.cols;
	int h = P.rows;

	float *verP = (float *) P.data;

	float pot = pow(2.0, pyr);
	int cont = 0;

	for (int i = 0; i < h; i++) {

		int ii = i * width[0] * pot;

		for (int j = 0; j < w; j++) {

			int point = ii + j * pot;

			if (band[point] == 1) 
			{
				verP[cont] = 1;
			}
			else
			{
				verP[cont] = 0;
			}
			cont++;
		}
	}
}
