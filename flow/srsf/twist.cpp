#include <opencv2/imgproc/imgproc_c.h>
#include "opencv2/highgui/highgui.hpp"
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/legacy/legacy.hpp>
#include <opencv2/opencv.hpp>

#include <stdio.h>
#include <math.h>
#include "twist.h"
#include "TV.h"
#include "rigid.h"
#include "basic.h"

static float NaN = 0.0/0.0;
static float Itr = 1;
static float Ztr = 1;

using namespace cv;

void creaGrillaLK(Mat &grillaX, Mat &grillaY, CvPoint2D32f punto, int w2)
{
	int h = grillaX.cols;
	int w = grillaX.rows;
	float x = punto.x - w2 ;
	float y = punto.y - w2 ;
	float xx;
	float yy;
	float *dataX = (float*) grillaX.data;
	float *dataY = (float*) grillaY.data;

	yy = y;

	for(int i=0; i< h; i++ ) {

		int row = i*w;
		xx = x;

		for(int j=0; j< w; j++ ) {

			dataX[row+j] = xx;
			dataY[row+j] = yy;
			xx = xx+1;
		}

		yy = yy+1;
	}

}

void warpLK(float Px, float Py, float Pz, CvPoint2D32f punto, CvPoint2D32f &Npunto, const Mat &DD, int &bandSF, int &bandOF, float fcx, float fcy, float cx, float cy)
{

	int w = DD.cols;
	float *D = (float*)DD.data;
	float dx,dy;
	float plus;
	float X[3];  
	float newX[3];

	int pos = cvRound(punto.x)+w*cvRound(punto.y);

	if (Px != Px || Py != Py || Pz != Pz || D[pos] != D[pos])
	{
		Npunto.x = punto.x;
		Npunto.y = punto.y;
		bandOF = 0;
	}
	else
	{
		float xx = punto.x - cx;
		float yy = punto.y - cy;
		float Z = 1/D[pos];
		X[0] = Z*xx/ fcx;
		X[1] = Z*yy/ fcy;
		X[2] = Z;

		newX[0] = X[0] + Px;
		newX[1] = X[1] + Py;
		newX[2] = X[2] + Pz;

		Npunto.x = fcx*newX[0]/newX[2] + cx;
		Npunto.y = fcy*newX[1]/newX[2] + cy;
		bandOF = 1;

	}
}

void warpLKrig_pyr(const Mat &P, const Mat &O, Mat &DZ, const Mat &oldX, const Mat &oldY, Mat &newX, Mat &newY, const Mat &D, float fcx, float fcy, float cx, float cy)
{
	int w = newX.cols;
	int h = newX.rows;
	int wh = w*h;
	float dx,dy,dz;
	float *dataX = (float*)oldX.data;
	float *dataY = (float*)oldY.data;
	float *deltaZ = (float*)DZ.data;
	float *dataXX = (float*)newX.data;
	float *dataYY = (float*)newY.data;
	float *dataD = (float*)D.data;
	float *PP = (float*)P.data;
	float *OO = (float*)O.data;

	Mat Rot = Mat(3,3,CV_32FC1);    
	expRODRI(Rot,O);
	Mat X = Mat(3,1,CV_32FC1);  
	Mat nX = Mat(3,1,CV_32FC1); 
	float *verX = (float*)X.data;
	float *vernX = (float*)nX.data;

	int cont = 0;
	while(cont < wh)
	{
		if (dataX[cont] != dataX[cont] || dataY[cont] != dataY[cont] || dataD[cont] != dataD[cont])
		{
			dataXX[cont] = -1;
			dataYY[cont] = -1;
		}
		else
		{
			float xx = dataX[cont] - cx;
			float yy = dataY[cont] - cy;
			float Z = 1/dataD[cont];
			verX[0] = Z*xx/ fcx;
			verX[1] = Z*yy/ fcy;
			verX[2] = Z;

			nX = Rot*X + P;

			dataXX[cont] = fcx*vernX[0]/vernX[2] + cx;
			dataYY[cont] = fcy*vernX[1]/vernX[2] + cy;
			deltaZ[cont] = vernX[2] - verX[2];
		}
		cont++;
	}
}

void trackLKrig(const Mat &gray, const Mat &Z, const Mat &a_gray, const Mat &a_Z, const Mat &a_D, const Mat &Ix, const Mat &Iy, const Mat &Zx, const Mat &Zy, int Niter, CvPoint2D32f punto[],Mat &Px, Mat &Py, Mat &Pz, Mat &Ox, Mat &Oy, Mat &Oz, Mat &RigX, Mat &RigY, Mat &RigZ, int pyr, float m_rgb[3][3], int WW, float kI, float kZ, float tetaP, float tetaO, int Npx, int Npy, int band[],int step)
{
	int W = 2*WW+1;
	int W2 = W*W;
	kI = kI/W2;
	kZ = kZ/W2;
	float epsi = 0.01;
	float clip = 0.005;
	float pot = pow (2.0,pyr);
	float fcx = m_rgb[0][0] / pot;
	float fcy = m_rgb[1][1] / pot;
	float cx = m_rgb[0][2] / pot;
	float cy = m_rgb[1][2] / pot;

	Mat P = Mat(3,1,CV_32FC1);
	Mat O = Mat(3,1,CV_32FC1);
	Mat T = Mat(3,1,CV_32FC1);
	Mat U = Mat(3,1,CV_32FC1);
	Mat Q = Mat(3,1,CV_32FC1);
	Mat Pdiff = Mat(3,1,CV_32FC1);
	Mat Odiff = Mat(3,1,CV_32FC1);
	float *p = (float*)P.data;
	float *o = (float*)O.data;
	float *t = (float*)T.data;
	float *u = (float*)U.data;
	float *q = (float*)Q.data;
	float *p_diff = (float*)Pdiff.data;
	float *o_diff = (float*)Odiff.data;

	Mat Ux = Mat(Npy,Npx,CV_32FC1);
	Mat Uy = Mat(Npy,Npx,CV_32FC1);
	Mat Uz = Mat(Npy,Npx,CV_32FC1);
	Px.copyTo(Ux);
	Py.copyTo(Uy);
	Pz.copyTo(Uz);
	float *ux = (float*)Ux.data;
	float *uy = (float*)Uy.data;
	float *uz = (float*)Uz.data;

	Mat Qx = Mat(Npy,Npx,CV_32FC1);
	Mat Qy = Mat(Npy,Npx,CV_32FC1);
	Mat Qz = Mat(Npy,Npx,CV_32FC1);
	Ox.copyTo(Qx);
	Oy.copyTo(Qy);
	Oz.copyTo(Qz);
	float *qx = (float*)Qx.data;
	float *qy = (float*)Qy.data;
	float *qz = (float*)Qz.data;

	float *PPx = (float*)Px.data;
	float *PPy = (float*)Py.data;
	float *PPz = (float*)Pz.data;
	float *OOx = (float*)Ox.data;
	float *OOy = (float*)Oy.data;
	float *OOz = (float*)Oz.data;

	float *Rx = (float*)RigX.data;
	float *Ry = (float*)RigY.data;
	float *Rz = (float*)RigZ.data;

	Mat Ti = Mat(W,W,CV_32FC1);
	Mat TnewI = Mat(W,W,CV_32FC1);
	Mat imDIF = Mat(W,W,CV_32FC1);
	Mat PhiI = Mat(W,W,CV_32FC1);
	Mat TIx = Mat(W,W,CV_32FC1);
	Mat TIy = Mat(W,W,CV_32FC1);

	Mat Td = Mat(W,W,CV_32FC1);
	Mat TZx = Mat(W,W,CV_32FC1);
	Mat TZy = Mat(W,W,CV_32FC1);
	Mat Tz = Mat(W,W,CV_32FC1);
	Mat TnewZ = Mat(W,W,CV_32FC1);
	Mat zDIF = Mat(W,W,CV_32FC1);
	Mat PhiZ = Mat(W,W,CV_32FC1);

	Mat grillaX = Mat(W,W,CV_32FC1);
	Mat grillaY = Mat(W,W,CV_32FC1);

	Mat newX = Mat(W,W,CV_32FC1);
	Mat newY = Mat(W,W,CV_32FC1);

	Mat newXX = Mat(W,W,CV_32SC2);
	Mat newYY = Mat(W,W,CV_16UC1);

	Mat Jx1 = Mat(W,W,CV_32FC1);
	Mat Jy2 = Mat(W,W,CV_32FC1);
	Mat Jx3 = Mat(W,W,CV_32FC1);
	Mat Jy3 = Mat(W,W,CV_32FC1);

	Mat J11 = Mat(W,W,CV_32FC1);
	Mat J12 = Mat(W,W,CV_32FC1);
	Mat J13 = Mat(W,W,CV_32FC1);
	Mat J21 = Mat(W,W,CV_32FC1);
	Mat J22 = Mat(W,W,CV_32FC1);
	Mat J23 = Mat(W,W,CV_32FC1);

	float IJ1,IJ2,IJ3,IJ4,IJ5,IJ6;
	float ZJ1,ZJ2,ZJ3,ZJ4,ZJ5,ZJ6;
	float H11,H12,H13,H14,H15,H16,H22,H23,H24,H25,H26,H33,H34,H35,H36,H44,H45,H46,H55,H56,H66;
	float d1,d2,d3,d4,d5,d6;

	Mat X = Mat(Npy,Npx,CV_32FC1);
	Mat Y = Mat(Npy,Npx,CV_32FC1);
	Mat DZ = Mat(W,W,CV_32FC1);
	Mat XX = Mat(W,W,CV_32FC1);
	Mat YY = Mat(W,W,CV_32FC1);
	float *vX = (float*)XX.data;
	float *vY = (float*)YY.data;

	Mat aux1 = Mat(W,W,CV_32FC1);
	Mat aux2 = Mat(W,W,CV_32FC1);

	float *vTIx = (float*)TIx.data;
	float *vTIy = (float*)TIy.data;
	float *vTZx = (float*)TZx.data;
	float *vTZy = (float*)TZy.data;
	float *vimDIF = (float*)imDIF.data;
	float *vzDIF = (float*)zDIF.data;
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
	float *vPhiI = (float*)PhiI.data;
	float *vPhiZ = (float*)PhiZ.data;

	Mat H = Mat(6,6,CV_32FC1);
	float* mH = (float*)H.data;

	Mat delta = Mat(6,1,CV_32FC1);
	float* mDelta = (float*)delta.data;

	Mat dP = Mat(6,1,CV_32FC1);
	float* deltaP = (float*)dP.data;

	int point;

	CvPoint2D32f punto2;

	creaGrilla(X,Y);
	subtract(X,cx,aux1);
	multiply(a_Z,aux1,X,1.0/fcx);	
	subtract(Y,cy,aux1);
	multiply(a_Z,aux1,Y,1.0/fcy);

	int Npoint;

	for (int i=step; i<Npy-step; i = i + step + 1) {

		int ii = i*width[0]*pot;

		for (int j=step; j<Npx-step; j = j + step + 1 ) {

			point = ii+j*pot;
			Npoint = i*Npx+j;

			if (band[point] == 1) {

				punto2.x = (punto[point].x/pot);
				punto2.y = (punto[point].y/pot);

				creaGrillaLK(grillaX,grillaY,punto2,WW);

				remap(a_gray,Ti,grillaX,grillaY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
				remap(a_D,Td,grillaX,grillaY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
				remap(a_Z,Tz,grillaX,grillaY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
				remap(X,XX,grillaX,grillaY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
				remap(Y,YY,grillaX,grillaY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);

				int cont = Niter;

				p[0] = PPx[Npoint];
				p[1] = PPy[Npoint];
				p[2] = PPz[Npoint];
				o[0] = OOx[Npoint];
				o[1] = OOy[Npoint];
				o[2] = OOz[Npoint];
				t[0] = Rx[Npoint];
				t[1] = Ry[Npoint];
				t[2] = Rz[Npoint];
				u[0] = ux[Npoint];
				u[1] = uy[Npoint];
				u[2] = uz[Npoint];
				q[0] = qx[Npoint];
				q[1] = qy[Npoint];
				q[2] = qz[Npoint];

				while (cont > 0) {

					H11=0;H12=0;H13=0;H14=0;H15=0;H16=0;H22=0;H23=0;H24=0;H25=0;H26=0;H33=0;H34=0;H35=0;H36=0;H44=0;H45=0;H46=0;H55=0;H56=0;H66=0;
					d1=0;d2=0;d3=0;d4=0;d5=0;d6=0;

					// 1) Compute warped image with current parameters
					warpLKrig_pyr(P+T,O,DZ,grillaX,grillaY,newX,newY,Td,fcx,fcy,cx,cy);
					convertMaps(newX,newY,newXX,newYY,CV_16SC2);


					remap(gray,TnewI,newXX,newYY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);
					remap(Z,TnewZ,newXX,newYY,CV_INTER_LINEAR,BORDER_CONSTANT,NaN);

					// 2) Compute error image

					subtract(Ti,TnewI,imDIF);
					phi(imDIF,PhiI,0.000001,Itr*0.1);
					revisa(imDIF);
					revisa(PhiI);

					add(Tz,DZ,aux1);
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
						jacobian_pyr(Jx1,Jy2,Jx3,Jy3,J11,J12,J13,J21,J22,J23,grillaX,grillaY,Td,fcx,fcy,cx,cy);
					}

					int pos = 0;

					while (pos < W2)
					{
						IJ1 = vTIx[pos]*vJx1[pos];
						IJ2 =                       vTIy[pos]*vJy2[pos];
						IJ3 = vTIx[pos]*vJx3[pos] + vTIy[pos]*vJy3[pos];
						IJ4 = vTIx[pos]*vJ11[pos] + vTIy[pos]*vJ21[pos];
						IJ5 = vTIx[pos]*vJ12[pos] + vTIy[pos]*vJ22[pos];
						IJ6 = vTIx[pos]*vJ13[pos] + vTIy[pos]*vJ23[pos];

						ZJ1 = vTZx[pos]*vJx1[pos];
						ZJ2 =                       vTZy[pos]*vJy2[pos];
						ZJ3 = vTZx[pos]*vJx3[pos] + vTZy[pos]*vJy3[pos] - 1;
						ZJ4 = vTZx[pos]*vJ11[pos] + vTZy[pos]*vJ21[pos] - vY[pos];
						ZJ5 = vTZx[pos]*vJ12[pos] + vTZy[pos]*vJ22[pos] + vX[pos];
						ZJ6 = vTZx[pos]*vJ13[pos] + vTZy[pos]*vJ23[pos];			

						float eI = vimDIF[pos];
						float eZ = vzDIF[pos];

						if (!(IJ1 != IJ1 || IJ2 != IJ2 || IJ3 != IJ3 || IJ4 != IJ4 || IJ5 != IJ5 || IJ6 != IJ6 || ZJ1 != ZJ1 || ZJ2 != ZJ2 || ZJ3 != ZJ3 || ZJ4 != ZJ4 || ZJ5 != ZJ5 || ZJ6 != ZJ6  || eI != eI  || eZ  != eZ))
						{

							float phiI = vPhiI[pos]*IJ1*kI;
							float phiZ = vPhiZ[pos]*ZJ1*kZ;		

							H11 = H11 + phiI *IJ1 +phiZ*ZJ1;
							H12 = H12 + phiI *IJ2 +phiZ*ZJ2;
							H13 = H13 + phiI *IJ3 +phiZ*ZJ3;
							H14 = H14 + phiI *IJ4 +phiZ*ZJ4;
							H15 = H15 + phiI *IJ5 +phiZ*ZJ5;
							H16 = H16 + phiI *IJ6 +phiZ*ZJ6;
							d1 = d1 + phiI*eI + phiZ*eZ; 

							phiI = vPhiI[pos]*IJ2*kI;
							phiZ = vPhiZ[pos]*ZJ2*kZ;				
							H22 = H22 + phiI *IJ2 +phiZ*ZJ2;
							H23 = H23 + phiI *IJ3 +phiZ*ZJ3;
							H24 = H24 + phiI *IJ4 +phiZ*ZJ4;
							H25 = H25 + phiI *IJ5 +phiZ*ZJ5;
							H26 = H26 + phiI *IJ6 +phiZ*ZJ6;
							d2 = d2 + phiI*eI + phiZ*eZ; 

							phiI = vPhiI[pos]*IJ3*kI;
							phiZ = vPhiZ[pos]*ZJ3*kZ;				
							H33 = H33 + phiI *IJ3 +phiZ*ZJ3;
							H34 = H34 + phiI *IJ4 +phiZ*ZJ4;
							H35 = H35 + phiI *IJ5 +phiZ*ZJ5;
							H36 = H36 + phiI *IJ6 +phiZ*ZJ6;
							d3 = d3 + phiI*eI + phiZ*eZ; 

							phiI = vPhiI[pos]*IJ4*kI;
							phiZ = vPhiZ[pos]*ZJ4*kZ;				
							H44 = H44 + phiI *IJ4 +phiZ*ZJ4;
							H45 = H45 + phiI *IJ5 +phiZ*ZJ5;
							H46 = H46 + phiI *IJ6 +phiZ*ZJ6;
							d4 = d4 + phiI*eI + phiZ*eZ; 

							phiI = vPhiI[pos]*IJ5*kI;
							phiZ = vPhiZ[pos]*ZJ5*kZ;				
							H55 = H55 + phiI *IJ5 +phiZ*ZJ5;
							H56 = H56 + phiI *IJ6 +phiZ*ZJ6;
							d5 = d5 + phiI*eI + phiZ*eZ; 

							phiI = vPhiI[pos]*IJ6*kI;
							phiZ = vPhiZ[pos]*ZJ6*kZ;				
							H66 = H66 + phiI *IJ6 +phiZ*ZJ6;
							d6 = d6 + phiI*eI + phiZ*eZ; 


						}	 
						pos++;
					}

					mH[0*6+0] = H11+tetaP; mH[0*6+1] = H12; mH[0*6+2] = H13; mH[0*6+3] = H14; mH[0*6+4] = H15; mH[0*6+5] = H16; 
					mH[1*6+0] = H12; mH[1*6+1] = H22+tetaP; mH[1*6+2] = H23; mH[1*6+3] = H24; mH[1*6+4] = H25; mH[1*6+5] = H26; 
					mH[2*6+0] = H13; mH[2*6+1] = H23; mH[2*6+2] = H33+tetaP; mH[2*6+3] = H34; mH[2*6+4] = H35; mH[2*6+5] = H36; 
					mH[3*6+0] = H14; mH[3*6+1] = H24; mH[3*6+2] = H34; mH[3*6+3] = H44+tetaO; mH[3*6+4] = H45; mH[3*6+5] = H46; 
					mH[4*6+0] = H15; mH[4*6+1] = H25; mH[4*6+2] = H35; mH[4*6+3] = H45; mH[4*6+4] = H55+tetaO; mH[4*6+5] = H56;
					mH[5*6+0] = H16; mH[5*6+1] = H26; mH[5*6+2] = H36; mH[5*6+3] = H46; mH[5*6+4] = H56; mH[5*6+5] = H66+tetaO; 

					mDelta[0] = d1 + tetaP*(u[0]-p[0]); 
					mDelta[1] = d2 + tetaP*(u[1]-p[1]); 
					mDelta[2] = d3 + tetaP*(u[2]-p[2]);
					mDelta[3] = d4 + tetaO*(q[0]-o[0]); 
					mDelta[4] = d5 + tetaO*(q[1]-o[1]); 
					mDelta[5] = d6 + tetaO*(q[2]-o[2]);  

					dP = H.inv()*delta;
					update(P,O,dP);

					cont--;
				} 


				for(int i2 = -step/2; i2 <= (step+1)/2; i2++){
					for(int j2 = -step/2; j2 <= (step+1)/2; j2++){

						int posi = Npoint -i2*Npx - j2;   

						ux[posi] = p[0];
						uy[posi] = p[1];
						uz[posi] = p[2];
						qx[posi] = o[0];
						qy[posi] = o[1];
						qz[posi] = o[2];

					}
				}

			} 
		}
	}

	Ux.copyTo(Px);
	Uy.copyTo(Py);
	Uz.copyTo(Pz);
	Qx.copyTo(Ox);
	Qy.copyTo(Oy);
	Qz.copyTo(Oz);
}



