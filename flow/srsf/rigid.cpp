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
static float Itr = 1;
static float Ztr = 1;

using namespace cv;

void creaGrilla(Mat &grillaX, Mat &grillaY)
{
	int w = grillaX.cols;
	int h = grillaX.rows;
	float *dataX = (float*) grillaX.data;
	float *dataY = (float*) grillaY.data;
	int cont = 0;

	for(int i=0; i< h; i++ ) {
		for(int j=0; j< w; j++ ) {

			dataX[cont] = j;
			dataY[cont] = i;
			cont++;

		} 
	}   
}

void warp_pyr(const Mat &P, const Mat &O, const Mat &Px, const Mat &Py, const Mat &Pz, Mat &DZ, Mat &newX, Mat &newY, const Mat &D, float fcx, float fcy, float cx, float cy)
{
	int w = newX.cols;
	int h = newX.rows;

	float dx,dy,dz;
	float *dataX = (float*)newX.data;
	float *dataY = (float*)newY.data;
	float *dataD = (float*)D.data;
	float *deltaZ = (float*)DZ.data;
	float *PP = (float*)P.data;
	float *OO = (float*)O.data;

	Mat Rot = Mat(3,3,CV_32FC1);    
	expRODRI(Rot,O);
	Mat X = Mat(3,1,CV_32FC1);  
	Mat nX = Mat(3,1,CV_32FC1); 
	Mat SF = Mat(3,1,CV_32FC1); 
	float *verX = (float*)X.data;
	float *vernX = (float*)nX.data;
	float *sf = (float*)SF.data;
	float *px = (float*)Px.data;
	float *py = (float*)Py.data;
	float *pz = (float*)Pz.data;

	for(int y=0; y< h; y++ )
	{
		int row = y*w;

		for(int x=0; x< w; x++ )
		{
			if (dataD[row+x] != dataD[row+x])
			{
				dataX[row+x] = -1;
				dataY[row+x] = -1;
			}
			else
			{
				float xx = x - cx;
				float yy = y - cy;
				float Z = 1/dataD[row+x];
				verX[0] = Z*xx/ fcx;
				verX[1] = Z*yy/ fcy;
				verX[2] = Z;

				sf[0] = px[row+x];
				sf[1] = py[row+x];
				sf[2] = pz[row+x];

				nX = Rot*X + P + SF;

				dataX[row+x] = fcx*vernX[0]/vernX[2] + cx;
				dataY[row+x] = fcy*vernX[1]/vernX[2] + cy;
				deltaZ[row+x] = vernX[2] - verX[2];

			}
		}
	}
}

void warp(const Mat &P, const Mat &O, CvPoint2D32f punto[], CvPoint2D32f Npunto[], const Mat &D, int bandSF[], int bandOF[], float fcx, float fcy, float cx, float cy)
{

	int w = D.cols;
	int h = D.rows;
	int wh = w*h;
	float *DD = (float*)D.data;
	float *PP = (float*)P.data;
	float *OO = (float*)O.data;
	float dx,dy,dz;

	Mat Rot = Mat(3,3,CV_32FC1);    
	expRODRI(Rot,O);
	Mat X = Mat(3,1,CV_32FC1);  
	Mat nX = Mat(3,1,CV_32FC1); 
	float *verX = (float*)X.data;
	float *vernX = (float*)nX.data;

	int cont = 0;

	while (cont < wh)
	{
		if(bandSF[cont] != 1)
		{
			Npunto[cont].x = punto[cont].x;
			Npunto[cont].y = punto[cont].y;
			bandOF[cont] = 0;   // We didnt want to estimate here we could but now we cant project

		}
		else
		{

			float xx = punto[cont].x - cx;
			float yy = punto[cont].y - cy;
			float Z = 1/DD[cont];
			verX[0] = Z*xx/ fcx;
			verX[1] = Z*yy/ fcy;
			verX[2] = Z;

			nX = Rot*X + P;

			Npunto[cont].x = fcx*vernX[0]/vernX[2] + cx;
			Npunto[cont].y = fcy*vernX[1]/vernX[2] + cy;

			bandOF[cont] = 1;              

		}
		cont++;
	}
}

void jacobian_pyr(Mat &Jx1, Mat &Jy2, Mat &Jx3, Mat &Jy3, Mat &J11, Mat &J12, Mat &J13, Mat &J21, Mat &J22, Mat &J23, const Mat &newX, const Mat &newY, const Mat &D, float fcx, float fcy, float cx, float cy )
{
	int w = newX.cols;
	int h = newX.rows;
	int wh = w*h;

	float *dataX = (float*)newX.data;
	float *dataY = (float*)newY.data;
	float *dataD = (float*)D.data;
	float *datax1 = (float*)Jx1.data;
	float *datay2 = (float*)Jy2.data;
	float *datax3 = (float*)Jx3.data;
	float *datay3 = (float*)Jy3.data;
	float *data11 = (float*)J11.data;
	float *data12 = (float*)J12.data;
	float *data13 = (float*)J13.data;
	float *data21 = (float*)J21.data;
	float *data22 = (float*)J22.data;
	float *data23 = (float*)J23.data;

	int cont = 0; 

	while (cont < wh) {

		float xx = dataX[cont]-cx;
		float yy = dataY[cont]-cy;
		datax1[cont] = fcx*dataD[cont];
		datay2[cont] = fcy*dataD[cont];
		datax3[cont] = (-1)*xx*dataD[cont];
		datay3[cont] = (-1)*yy*dataD[cont];
		data11[cont] = -xx*yy/fcy;
		data12[cont] = fcx+xx*xx/fcx;
		data13[cont] = -yy*fcx/fcy;
		data21[cont] = -fcy-yy*yy/fcy;
		data22[cont] = xx*yy/fcx;
		data23[cont] = xx*fcy/fcx;
		cont++;
	}

}

void update(Mat &P, Mat &O, Mat &d)
{

	Mat dO = Mat(3,1,CV_32FC1);
	Mat dP = Mat(3,1,CV_32FC1);
	Mat aux = Mat(3,1,CV_32FC1);
	Mat oldSE = Mat(4,4,CV_32FC1);
	Mat dSE = Mat(4,4,CV_32FC1);
	Mat newSE = Mat(4,4,CV_32FC1);

	float *delta = (float*)d.data;
	float *vdO = (float*)dO.data;
	float *vdP = (float*)dP.data;

	if (!(delta[0] != delta[0] || delta[1] != delta[1] || delta[2] != delta[2] || delta[3] != delta[3] || delta[4] != delta[4] || delta[5] != delta[5]))
	{
		vdP[0] = delta[0]; vdP[1] = delta[1]; vdP[2] = delta[2];
		vdO[0] = delta[3]; vdO[1] = delta[4]; vdO[2] = delta[5];

		expRigid(dSE,dP,dO);
		expRigid(oldSE,P,O);
		newSE = dSE*oldSE;
		logRigid(newSE,P,O);
	}
}

void skew3(Mat &R, const Mat &w)
{
	float *RR = (float*)R.data;
	float *ww = (float*)w.data;

	RR[1] = -ww[2]; RR[2] = ww[1];
	RR[3] = ww[2]; RR[5] = -ww[0];
	RR[6] = -ww[1]; RR[7] = ww[0];

}

void expRODRI(Mat &R, const Mat &w)
{
	Mat omega = Mat::zeros(3,3,CV_32FC1);
	Mat I = Mat::eye(3,3,CV_32FC1);
	float *ww = (float*)w.data;
	float teta2 = ww[0]*ww[0] + ww[1]*ww[1] + ww[2]*ww[2];
	float teta = sqrt(teta2);
	float sinterm,costerm;

	if (teta < 0.00000149)
	{
		sinterm = 1-teta2/6;
		costerm = 1-teta2/24;
	}
	else
	{
		costerm=(1-cos(teta))/teta2;
		sinterm=sin(teta)/teta;
		//printf("%f %f \n",costerm,sinterm);
	}

	skew3(omega,w);
	R = I + omega*sinterm + omega*omega*costerm ;
}

void logRODRI(const Mat &R, Mat &w)
{
	Mat s = Mat(3,1,CV_32FC1);
	float *ss = (float*)s.data;
	float *RR = (float*)R.data;

	float Tr = RR[0]+RR[4]+RR[8];
	float Tr2 = (Tr-1)/2;
	float tetaf;
	ss[0] = RR[7]-RR[5];
	ss[1] = RR[2]-RR[6];
	ss[2] = RR[3]-RR[1];

	if ((1-Tr2*Tr2) > 0.0000000000000002)
	{
		float teta = acos(Tr2);
		tetaf = teta/(2*sin(teta));
	}
	else
	{
		float teta = acos(Tr2);
		tetaf = 0.5/(1-teta/6);
	}

	w = tetaf*s;
}

void expRigid(Mat &SE, const Mat &P, const Mat &w)
{
	Mat R = Mat::zeros(3,3,CV_32FC1);

	float *vSE = (float*)SE.data;
	float *RR = (float*)R.data;
	float *PP = (float*)P.data;

	expRODRI(R,w);

	vSE[0] = RR[0]; vSE[1] = RR[1]; vSE[2] = RR[2]; vSE[3] = PP[0];
	vSE[4] = RR[3]; vSE[5] = RR[4]; vSE[6] = RR[5]; vSE[7] = PP[1];
	vSE[8] = RR[6]; vSE[9] = RR[7]; vSE[10] = RR[8]; vSE[11] = PP[2];
	vSE[12] = 0; vSE[13] = 0; vSE[14] = 0; vSE[15] = 1;

}

void logRigid(const Mat &SE, Mat &P, Mat &w)
{

	Mat R = Mat::zeros(3,3,CV_32FC1);

	float *vSE = (float*)SE.data;
	float *RR = (float*)R.data;
	float *PP = (float*)P.data;

	RR[0] = vSE[0]; RR[1] = vSE[1]; RR[2] = vSE[2]; PP[0] = vSE[3];
	RR[3] = vSE[4]; RR[4] = vSE[5]; RR[5] = vSE[6]; PP[1] = vSE[7];
	RR[6] = vSE[8]; RR[7] = vSE[9]; RR[8] = vSE[10]; PP[2] = vSE[11];

	logRODRI(R,w);

}

void trackPoints(const Mat &gray, const Mat &Z, const Mat &a_gray, const Mat &a_Z, const Mat &a_D, const Mat &Ix, const Mat &Iy, const Mat &Zx, const Mat &Zy, int Niter, CvPoint2D32f punto[], Mat &P, Mat &O, const Mat &Px, const Mat &Py, const Mat &Pz, int pyr, int dd, float m_rgb[3][3], int WW, float kI, float kZ, int Npx, int Npy, int band[])
{
	int W = 1000;
	kI = kI/W;
	kZ = kZ/W;
	float pot = pow (2.0,pyr);
	int Wpot = WW*pot;
	float fcx = m_rgb[0][0] / pot;
	float fcy = m_rgb[1][1] / pot;
	float cx = m_rgb[0][2] / pot;
	float cy = m_rgb[1][2] / pot;

	Mat TnewI = Mat::zeros(Npy,Npx,CV_32FC1);
	Mat imDIF = Mat::zeros(Npy,Npx,CV_32FC1);
	Mat PhiI = Mat::zeros(Npy,Npx,CV_32FC1);
	Mat TIx = Mat(Npy,Npx,CV_32FC1);
	Mat TIy = Mat(Npy,Npx,CV_32FC1);

	Mat TZx = Mat(Npy,Npx,CV_32FC1);
	Mat TZy = Mat(Npy,Npx,CV_32FC1);
	Mat TnewZ = Mat(Npy,Npx,CV_32FC1);
	Mat zDIF = Mat(Npy,Npx,CV_32FC1);
	Mat PhiZ = Mat(Npy,Npx,CV_32FC1);

	Mat grillaX = Mat(Npy,Npx,CV_32FC1);
	Mat grillaY = Mat(Npy,Npx,CV_32FC1);

	Mat newX = Mat(Npy,Npx,CV_32FC1);
	Mat newY = Mat(Npy,Npx,CV_32FC1);

	Mat newXX = Mat(Npy,Npx,CV_32SC2);
	Mat newYY = Mat(Npy,Npx,CV_16UC1);

	float *PP = (float*)P.data;
	float *OO = (float*)O.data;

	Mat Jx1 = Mat(Npy,Npx,CV_32FC1);
	Mat Jy2 = Mat(Npy,Npx,CV_32FC1);
	Mat Jx3 = Mat(Npy,Npx,CV_32FC1);
	Mat Jy3 = Mat(Npy,Npx,CV_32FC1);

	Mat J11 = Mat(Npy,Npx,CV_32FC1);
	Mat J12 = Mat(Npy,Npx,CV_32FC1);
	Mat J13 = Mat(Npy,Npx,CV_32FC1);
	Mat J21 = Mat(Npy,Npx,CV_32FC1);
	Mat J22 = Mat(Npy,Npx,CV_32FC1);
	Mat J23 = Mat(Npy,Npx,CV_32FC1);

	Mat aux1 = Mat(Npy,Npx,CV_32FC1);
	Mat aux2 = Mat(Npy,Npx,CV_32FC1);
	Mat DZ = Mat(Npy,Npx,CV_32FC1);
	Mat X = Mat(Npy,Npx,CV_32FC1);
	Mat Y = Mat(Npy,Npx,CV_32FC1);

	float IJ1,IJ2,IJ3,IJ4,IJ5,IJ6;
	float ZJ1,ZJ2,ZJ3,ZJ4,ZJ5,ZJ6;
	float H11,H12,H13,H14,H15,H16,H22,H23,H24,H25,H26,H33,H34,H35,H36,H44,H45,H46,H55,H56,H66;
	float d1,d2,d3,d4,d5,d6;

	float *vTIx = (float*)TIx.data;
	float *vTIy = (float*)TIy.data;
	float *vTZx = (float*)TZx.data;
	float *vTZy = (float*)TZy.data;
	float *vimDIF = (float*)imDIF.data;
	float *vzDIF = (float*)zDIF.data;
	float *XX = (float*)X.data;
	float *YY = (float*)Y.data;    
	float *vJx1 = (float*)Jx1.data;
	float *vJy2 = (float*)Jy2.data;
	float *vJx3 = (float*)Jx3.data;
	float *vJy3 = (float*)Jy3.data;
	float *vJ11 = (float*)J11.data;
	float *vJ12 = (float*)J12.data;
	float *vJ13 = (float*)J13.data;
	float *vJ21 = (float*)J21.data;
	float *vJ22 = (float*)J22.data;
	float *vJ23 = (float*)J23.data;
	float *vphiI = (float*)PhiI.data;
	float *vphiZ = (float*)PhiZ.data;

	float *verI= (float*)a_gray.data;

	float aux;

	Mat H = Mat(6,6,CV_32FC1);
	float* mH = (float*)H.data;

	Mat delta = Mat(6,1,CV_32FC1);
	float* mDelta = (float*)delta.data;

	Mat dP = Mat(6,1,CV_32FC1);
	float* deltaP = (float*)dP.data;

	int point;
	int cont = Niter;
	//int contP = 0;

	CvPoint2D32f punto2;

	creaGrilla(grillaX,grillaY);

	while (cont > 0) {

		H11=0;H12=0;H13=0;H14=0;H15=0;H16=0;H22=0;H23=0;H24=0;H25=0;H26=0;H33=0;H34=0;H35=0;H36=0;H44=0;H45=0;H46=0;H55=0;H56=0;H66=0;
		d1=0;d2=0;d3=0;d4=0;d5=0;d6=0;

		// 1) Compute warped image with current parameters

		warp_pyr(P,O,Px,Py,Pz,DZ,newX,newY,a_D,fcx,fcy,cx,cy);

		subtract(newX,cx,aux1);
		multiply(a_Z,aux1,X,1.0/fcx);	

		subtract(newY,cy,aux1);
		multiply(a_Z,aux1,Y,1.0/fcy);

		convertMaps(newX,newY,newXX,newYY,CV_16SC2);

		remap(gray,TnewI,newXX,newYY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
		remap(Z,TnewZ,newXX,newYY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);

		// 2) Compute error image

		subtract(a_gray,TnewI,imDIF);
		phi(imDIF,PhiI,0.000001,Itr*0.1);
		revisa(imDIF);
		revisa(PhiI);

		add(a_Z,DZ,aux1);
		subtract(aux1,TnewZ,zDIF);
		phi(zDIF,PhiZ,0.000001,Ztr*10);
		revisa(zDIF);
		revisa(PhiZ);

		// 3) EvaLate gradient

		remap(Ix,TIx,newXX,newYY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
		remap(Iy,TIy,newXX,newYY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
		remap(Zx,TZx,newXX,newYY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
		remap(Zy,TZy,newXX,newYY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);

		// 4) EvaLate Jacobian (dW/dP = [Jx1 0 Jx3; 0 Jy2 Jy3])

		if (cont == Niter)
		{
			jacobian_pyr(Jx1,Jy2,Jx3,Jy3,J11,J12,J13,J21,J22,J23,grillaX,grillaY,a_D,fcx,fcy,cx,cy);
		}

		Wpot = WW*pot;

		for (int i = Wpot; i < Npy - Wpot; i = i+1 +dd) {

			int ii = i*width[0]*pot;

			for (int j = Wpot; j < Npx - Wpot; j = j+1 +dd) {

				point = ii+j*pot;

				if (band[point] == 1) {

					punto2.x = (punto[point].x/pot);
					punto2.y = (punto[point].y/pot);

					for (int i2 = punto2.y - WW; i2 <= punto2.y + WW; i2++) {

						int row = i2*Npx;

						for (int j2 = punto2.x - WW; j2 <= punto2.x + WW; j2++) {

							int pos = row + j2;

							IJ1 = vTIx[pos]*vJx1[pos];
							IJ2 =                       vTIy[pos]*vJy2[pos];
							IJ3 = vTIx[pos]*vJx3[pos] + vTIy[pos]*vJy3[pos];
							IJ4 = vTIx[pos]*vJ11[pos] + vTIy[pos]*vJ21[pos];
							IJ5 = vTIx[pos]*vJ12[pos] + vTIy[pos]*vJ22[pos];
							IJ6 = vTIx[pos]*vJ13[pos] + vTIy[pos]*vJ23[pos];

							ZJ1 = vTZx[pos]*vJx1[pos];
							ZJ2 =                       vTZy[pos]*vJy2[pos];
							ZJ3 = vTZx[pos]*vJx3[pos] + vTZy[pos]*vJy3[pos] - 1;
							ZJ4 = vTZx[pos]*vJ11[pos] + vTZy[pos]*vJ21[pos] - YY[pos];
							ZJ5 = vTZx[pos]*vJ12[pos] + vTZy[pos]*vJ22[pos] + XX[pos];
							ZJ6 = vTZx[pos]*vJ13[pos] + vTZy[pos]*vJ23[pos];

							float eI = vimDIF[pos];
							float eZ = vzDIF[pos];

							if (!(IJ1 != IJ1 || IJ2 != IJ2 || IJ3 != IJ3 || IJ4 != IJ4 || IJ5 != IJ5 || IJ6 != IJ6 || ZJ1 != ZJ1 || ZJ2 != ZJ2 || ZJ3 != ZJ3 || ZJ4 != ZJ4 || ZJ5 != ZJ5 || ZJ6 != ZJ6  || eI != eI  || eZ  != eZ))
							{

								float phiI = vphiI[pos]*IJ1*kI;
								float phiZ = vphiZ[pos]*ZJ1*kZ;		

								H11 = H11 + phiI *IJ1 +phiZ*ZJ1;
								H12 = H12 + phiI *IJ2 +phiZ*ZJ2;
								H13 = H13 + phiI *IJ3 +phiZ*ZJ3;
								H14 = H14 + phiI *IJ4 +phiZ*ZJ4;
								H15 = H15 + phiI *IJ5 +phiZ*ZJ5;
								H16 = H16 + phiI *IJ6 +phiZ*ZJ6;
								d1 = d1 + phiI*eI + phiZ*eZ; 

								phiI = vphiI[pos]*IJ2*kI;
								phiZ = vphiZ[pos]*ZJ2*kZ;				
								H22 = H22 + phiI *IJ2 +phiZ*ZJ2;
								H23 = H23 + phiI *IJ3 +phiZ*ZJ3;
								H24 = H24 + phiI *IJ4 +phiZ*ZJ4;
								H25 = H25 + phiI *IJ5 +phiZ*ZJ5;
								H26 = H26 + phiI *IJ6 +phiZ*ZJ6;
								d2 = d2 + phiI*eI + phiZ*eZ; 

								phiI = vphiI[pos]*IJ3*kI;
								phiZ = vphiZ[pos]*ZJ3*kZ;				
								H33 = H33 + phiI *IJ3 +phiZ*ZJ3;
								H34 = H34 + phiI *IJ4 +phiZ*ZJ4;
								H35 = H35 + phiI *IJ5 +phiZ*ZJ5;
								H36 = H36 + phiI *IJ6 +phiZ*ZJ6;
								d3 = d3 + phiI*eI + phiZ*eZ; 

								phiI = vphiI[pos]*IJ4*kI;
								phiZ = vphiZ[pos]*ZJ4*kZ;				
								H44 = H44 + phiI *IJ4 +phiZ*ZJ4;
								H45 = H45 + phiI *IJ5 +phiZ*ZJ5;
								H46 = H46 + phiI *IJ6 +phiZ*ZJ6;
								d4 = d4 + phiI*eI + phiZ*eZ; 

								phiI = vphiI[pos]*IJ5*kI;
								phiZ = vphiZ[pos]*ZJ5*kZ;				
								H55 = H55 + phiI *IJ5 +phiZ*ZJ5;
								H56 = H56 + phiI *IJ6 +phiZ*ZJ6;
								d5 = d5 + phiI*eI + phiZ*eZ; 

								phiI = vphiI[pos]*IJ6*kI;
								phiZ = vphiZ[pos]*ZJ6*kZ;				
								H66 = H66 + phiI *IJ6 +phiZ*ZJ6;
								d6 = d6 + phiI*eI + phiZ*eZ; 

							} 
						}
					}
				}
			}
		}

		mH[0*6+0] = H11; mH[0*6+1] = H12; mH[0*6+2] = H13; mH[0*6+3] = H14; mH[0*6+4] = H15; mH[0*6+5] = H16; 
		mH[1*6+0] = H12; mH[1*6+1] = H22; mH[1*6+2] = H23; mH[1*6+3] = H24; mH[1*6+4] = H25; mH[1*6+5] = H26; 
		mH[2*6+0] = H13; mH[2*6+1] = H23; mH[2*6+2] = H33; mH[2*6+3] = H34; mH[2*6+4] = H35; mH[2*6+5] = H36; 
		mH[3*6+0] = H14; mH[3*6+1] = H24; mH[3*6+2] = H34; mH[3*6+3] = H44; mH[3*6+4] = H45; mH[3*6+5] = H46; 
		mH[4*6+0] = H15; mH[4*6+1] = H25; mH[4*6+2] = H35; mH[4*6+3] = H45; mH[4*6+4] = H55; mH[4*6+5] = H56;
		mH[5*6+0] = H16; mH[5*6+1] = H26; mH[5*6+2] = H36; mH[5*6+3] = H46; mH[5*6+4] = H56; mH[5*6+5] = H66; 

		mDelta[0] = d1; mDelta[1] = d2; mDelta[2] = d3; mDelta[3] = d4; mDelta[4] = d5; mDelta[5] = d6;  

		dP = H.inv()*delta;
		update(P,O,dP);
		cont --;		
	}      
}
