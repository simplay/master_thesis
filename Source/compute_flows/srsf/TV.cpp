#include <opencv2/imgproc/imgproc_c.h>
#include "opencv2/highgui/highgui.hpp"
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/legacy/legacy.hpp>
#include <opencv2/opencv.hpp>

#include <stdio.h>
#include <math.h>
#include "TV.h"
#include "basic.h"

static float NaN = 0.0/0.0;
static float Itr = 1;
static float Ztr = 1;

using namespace cv;

void TV_Moreno(Mat &Px, Mat &Py, Mat &Pz, int iter, float teta)
{
	int w = Px.cols;
	int h = Py.rows;  
	int wh = w*h; 

	float sigma = 0.225;

	if ( iter == 0 ) {

		return;
	}

	if (teta != 0) {

		sigma = 0.225/teta;

	} 
	else {

		return;   
	}

	Mat DivG = Mat(h,w,CV_32FC1);
	Mat G1x = Mat::zeros(h,w,CV_32FC1);
	Mat G1y = Mat::zeros(h,w,CV_32FC1);
	Mat G2x = Mat::zeros(h,w,CV_32FC1);
	Mat G2y = Mat::zeros(h,w,CV_32FC1);
	Mat G3x = Mat::zeros(h,w,CV_32FC1);
	Mat G3y = Mat::zeros(h,w,CV_32FC1);
	Mat Ux = Mat::zeros(h,w,CV_32FC1);
	Mat Uy = Mat::zeros(h,w,CV_32FC1); 
	Mat Uz = Mat::zeros(h,w,CV_32FC1); 

	Mat d1x = Mat(h,w,CV_32FC1);
	Mat d2x = Mat(h,w,CV_32FC1);   
	Mat d3x = Mat(h,w,CV_32FC1);   
	Mat d1y = Mat(h,w,CV_32FC1);   
	Mat d2y = Mat(h,w,CV_32FC1);   
	Mat d3y = Mat(h,w,CV_32FC1);

	float *g1x = (float*)G1x.data;
	float *g1y = (float*)G1y.data;
	float *g2x = (float*)G2x.data;
	float *g2y = (float*)G2y.data;   
	float *g3x = (float*)G3x.data;
	float *g3y = (float*)G3y.data;      

	Mat aux = Mat(h,w,CV_32FC1);

	int cont = 0;

	while(cont < iter) {

		// Primal update
		gradiente(Ux,d1x,d1y);
		gradiente(Uy,d2x,d2y);
		gradiente(Uz,d3x,d3y);

		G1x.copyTo(aux);	G1x = aux + sigma*d1x;  
		G1y.copyTo(aux);	G1y = aux + sigma*d1y;
		G2x.copyTo(aux);	G2x = aux + sigma*d2x;  
		G2y.copyTo(aux);	G2y = aux + sigma*d2y; 
		G3x.copyTo(aux);	G3x = aux + sigma*d3x;  
		G3y.copyTo(aux);	G3y = aux + sigma*d3y; 


		int cont2 = 0;

		while(cont2 < wh) {

			projectionK(g1x[cont2],g1y[cont2],g2x[cont2],g2y[cont2],g3x[cont2],g3y[cont2]);
			cont2++;
		}

		// Dual update
		divergence(G1x,G1y,DivG);
		Ux = Px + teta*DivG;
		divergence(G2x,G2y,DivG);
		Uy = Py + teta*DivG;
		divergence(G3x,G3y,DivG);
		Uz = Pz + teta*DivG;

		cont++;
	}

	Ux.copyTo(Px);
	Uy.copyTo(Py);
	Uz.copyTo(Pz);
}

void projectionK(float &a11, float &a12, float &a21, float &a22, float &a31, float &a32)
{

	float d11 = a11 * a11 + a21 * a21 + a31 * a31 ;
	float d12 = a12 * a11 + a22 * a21 + a32 * a31 ;
	float d22 = a12 * a12 + a22 * a22 + a32 * a32 ;

	float trace = d11 + d22;
	float det = d11*d22-d12*d12;
	float M = 0.25*trace*trace*det;
	float d = (0<M)?sqrt(M):0;

	float lmax = (0<0.5*trace+d)?sqrt(0.5*trace+d):0;
	float lmin = (0<0.5*trace-d)?sqrt(0.5*trace-d):0;
	float smax = sqrt(lmax);
	float smin = sqrt(lmin);


	if ( smax + smin > 1) {

		float v11,v12,v21,v22;

		if ( d12 == 0.0 ) {

			if ( d11 >= d22 ) { v11 = 1; v21 = 0; v12 = 0; v22 = 1; }
			else { v11 = 0; v21 = 1; v12 = 1; v22 = 0; }
		}
		else {
			v11 = lmax - d22; v21 = d12;
			float l1 = hypot(v11,v21);
			v11 /= l1 ; v21 /= l1;
			v12 = lmin - d22; v22 = d12;
			float l2 = hypot(v12,v22);
			v12 /= l2 ; v22 /= l2;
		}

		float tau = 0.5*(smax - smin + 1);
		float s1 = (1<tau)?1:tau;
		float s2 = 1 - s1;
		s1 /= smax;
		s2 = (smin > 0) ? s2 / smin : 0;

		float t11 = s1*v11*v11 + s2*v12*v12;
		float t12 = s1*v11*v21 + s2*v12*v22;
		float t21 = s1*v21*v11 + s2*v22*v12;
		float t22 = s1*v21*v21 + s2*v22*v22;

		a11 = a11*t11 + a12*t21;
		a21 = a21*t11 + a22*t21;
		a31 = a31*t11 + a32*t21;
		a12 = a11*t12 + a12*t22;
		a22 = a21*t12 + a22*t22;
		a32 = a31*t12 + a32*t22;
	}
}

void TV_ROF(Mat &P, int iter, float tau, float teta)
{

	chambolle(P,iter,tau,teta);

}

void chambolle(Mat &P, int iter, float tau, float teta)
{
	int w = P.cols;
	int h = P.rows;

	int cont = iter;
	float kappa = 0;

	if ( iter == 0 ) {

		return;

	}

	if (teta != 0) {

		kappa = tau/teta;

	} 
	else {

		return;

	}

	Mat Gx = Mat::zeros(h,w,CV_32FC1);
	Mat Gy = Mat::zeros(h,w,CV_32FC1);
	Mat dGx = Mat(h,w,CV_32FC1);
	Mat dGy = Mat(h,w,CV_32FC1);
	Mat DivG = Mat(h,w,CV_32FC1);
	Mat aux = Mat(h,w,CV_32FC1);
	Mat auxX = Mat(h,w,CV_32FC1);
	Mat auxY = Mat(h,w,CV_32FC1);

	while (cont > 0) {

		divergence(Gx,Gy,DivG);
		aux = P + teta*DivG;
		gradiente(aux,dGx,dGy);
		auxX = Gx + kappa*dGx;
		auxY = Gy + kappa*dGy;
		normaliza(auxX,auxY,Gx,Gy);
		cont--;
	}
	divergence(Gx,Gy,DivG);
	P.copyTo(aux);
	P = aux + teta*DivG;
}

void divergence(const Mat &Gx, const Mat &Gy, Mat &DivG)
{
	int w = Gx.cols;
	int h = Gx.rows;
	float *dataGx = (float*)Gx.data;
	float *dataGy = (float*)Gy.data;
	float *dataG = (float*)DivG.data;

	for(int i=1; i< h-1; i++ )
	{
		for(int j=1; j< w-1; j++ )
		{

			const int p  = i * w + j;
			const int p1 = p - 1;
			const int p2 = p - w;

			const float vx = dataGx[p] - dataGx[p1];
			const float vy = dataGy[p] - dataGy[p2];

			dataG[p] = vx + vy;

			if (dataG[p] != dataG[p])
			{
				dataG[p] = 0;
			}

		}
	}


	//first and last row
	for(int j=1; j< w-1; j++ )
	{

		const int p = (h-1) * w + j;

		dataG[j] = dataGx[j] - dataGx[j-1] + dataGy[j];
		dataG[p] = dataGx[p] - dataGx[p-1] - dataGy[p-w];
	}

	//first and last column
	for (int i = 1; i < h-1; i++)
	{
		const int p1 = i * w;
		const int p2 = (i+1) * w - 1;

		dataG[p1] =  dataGx[p1]  + dataGy[p1] - dataGy[p1 - w];
		dataG[p2] = -dataGx[p2-1] + dataGy[p2] - dataGy[p2 - w];

	}

	dataG[0] = dataGx[0] + dataGy[0];
	dataG[w-1] = -dataGx[w - 2] + dataGy[w - 1];
	dataG[(h-1)*w] =  dataGx[(h-1)*w] - dataGy[(h-2)*w];
	dataG[h*w-1] = -dataGx[h*w - 2] - dataGy[(h-1)*w - 1];

}

void gradiente(const Mat &I, Mat &Ix, Mat &Iy)
{
	int w = I.cols;
	int h = I.rows;
	float *dataI = (float*)I.data;
	float *dataIx = (float*)Ix.data;
	float *dataIy = (float*)Iy.data;

	for(int i=0; i< h-1; i++ )
	{
		for(int j=0; j< w-1; j++ )
		{

			const int p  = i * w + j;
			const int p1 = p + 1;
			const int p2 = p + w;

			dataIx[p] = dataI[p1] - dataI[p];
			dataIy[p] = dataI[p2] - dataI[p];
		}
	}

	//last row
	for(int j=0; j< w-1; j++ )
	{

		const int p = (h-1) * w + j;

		dataIx[p] = dataI[p+1] - dataI[p];
		dataIy[p] = 0;
	}

	//last column
	for(int i=1; i< h; i++ )
	{
		const int p = i*w -1;

		dataIx[p] = 0;
		dataIy[p] = dataI[p+w] - dataI[p];
	}

	dataIx[w*h-1] = 0;
	dataIy[w*h-1] = 0;
}

void normaliza(Mat &dGx, Mat &dGy, Mat &Gx, Mat &Gy)
{
	int w = Gx.cols;
	int h = Gx.rows;
	int wh = w*h;

	float *verdGx = (float*)dGx.data;
	float *verdGy = (float*)dGy.data;
	float *verGx = (float*)Gx.data;
	float *verGy = (float*)Gy.data;

	float px;
	float py;
	float norm;

	for (int i=0; i < wh; i++) {

		px = verdGx[i]*verdGx[i];
		py = verdGy[i]*verdGy[i];
		norm = sqrt(px+py);

		if (norm < 1) {

			verGx[i] = verdGx[i];
			verGy[i] = verdGy[i];
		}

		else {

			verGx[i] = verdGx[i]/norm;
			verGy[i] = verdGy[i]/norm;
		}
	}

}







