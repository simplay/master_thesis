#include <opencv2/imgproc/imgproc_c.h>
#include "opencv2/highgui/highgui.hpp"

#include <opencv2/features2d/features2d.hpp>
#include <opencv2/legacy/legacy.hpp>
#include <opencv2/opencv.hpp>

#include <stdio.h>
#include <math.h>
#include "basic.h"
#include "TV.h"
#include "rigid.h"
#include "twist.h"
#include "flow.h"
#include "depth.h"
#include <fstream>

float m_rgb[3][3]= {0};
static float NaN = 0.0/0.0;

// Settings
static int Npyr = 2;            // Levels of the pyramid
static int PyrLow = 0;	        // Stop-level of the pyramid
int bSEL = 0;  			// Selection: 		0 <- Non-Rigid, 1 <- Rigid, 2 <- Rigid + Non-Rigid
int constZ = 10;                // Depth constante:	depth in cm
int bTVvector = 1;		// TV Rot-component:	0 < channel-by-channel, 1 <- vectorial
static int Wext = 1;            // Rigid/Non-Rigid alternations
static float maxZ = 100;        // Max depth in cm
static float minZ = 50;         // Min depth in cm
int cropX = 0;			// Optional X cropping (in pixels)
int cropY = 0;			// Optional Y cropping (in pixels)
bool ViewOutput = 1;		// Visualization of output images 
int ViewOF = 12;		// Constant for the optical flow color code 

// Rigid parameters
static int Nrigid = 100;      	// Iterations per level
static int stepR = 1;		// pixel step (it considers every "stepR" pixel)

// Non-Rigid parameters
static int Witer = 5;           // Gauss-Newton/TV solver alternations
static int Niter = 5;		// Gauss-Newton iterations
static int WW = 2;		// Local ridity: 	(2xWW+1)x(2xWW+1) window
static int TViter = 50;		// TV solver iterations
static int stepNR = 1;		// Gauss-Newton pixel step (it considers every "stepRN" pixel)

// Weights
static float kI = 1;   		// Brightness term weight
static float kZ = 0.1;		// Depth term weight
float alfa = 10;		// Regularization constant
float kRot = 1000;		// Rotational component constant
float alfaR = kRot*alfa;	// Regularization for the rotational component
static float Kappa = 0.05;	// Linking term

// Constants
static float Gauss[3][3] =  {0.0751,0.1238,0.0751,0.1238,0.2042,0.1238,0.0751,0.1238,0.0751};
int height[] = {480,240,120,60,30,15};	// Pyramid size (height)
int width[] = {640,320,160,80,40,20};	// Pyramid size (width)
static int Npx = width[0];		// Width (original resolution)
static int Npy = height[0];		// Height (original resolution)
static int Npoints = Npx*Npy;		// Number of points (original resolution)

#include <vector>

using namespace cv;
using namespace std;

Mat gray0 = Mat(height[0],width[0],CV_8U);
Mat gray1 = Mat(height[0],width[0],CV_8U);

Mat P = Mat(3,1,CV_32FC1);
Mat O = Mat(3,1,CV_32FC1);

Mat Vx = Mat::zeros(height[0],width[0],CV_32FC1);
Mat Vy = Mat::zeros(height[0],width[0],CV_32FC1);
Mat Vz = Mat::zeros(height[0],width[0],CV_32FC1);

Mat DEPTH0,DEPTH1;

CvPoint2D32f *point = new CvPoint2D32f[Npoints];
CvPoint2D32f *NEWpointRig = new CvPoint2D32f[Npoints];
CvPoint2D32f *NEWpoint = new CvPoint2D32f[Npoints];

float fcx,fcy,cx,cy;
int im,im_i,im_f;

int main(int argc, char **argv)
{

	if (argc == 4) {

		im_i = atoi(argv[1]);
		im_f = atoi(argv[2]);
		bSEL = atoi(argv[3]);

	} else if (argc == 10) {



		im_i = atoi(argv[1]);
		im_f = atoi(argv[2]);	
		bSEL = atoi(argv[3]);

		if (bSEL != 0) {

			printf ("Set Sel = 0 (Non-Rigid) for 9 parameters \n");
			return(0);

		}
		Npyr =  atoi(argv[4]);
		Niter = atoi(argv[5]);
		WW = atoi(argv[6]);
		stepNR = atoi(argv[7]);
		maxZ = atof(argv[8]);
		alfa = atof(argv[9]);

	} else if (argc == 11) {

		im_i = atoi(argv[1]);
		im_f = atoi(argv[2]);	
		bSEL = atoi(argv[3]);

		if (bSEL != 2) {

			printf ("Set Sel = 2 (Rigid+NonRigid) for 10 parameters \n");
			return(0);

		}

		Npyr =  atoi(argv[4]);
		Wext =  atoi(argv[5]);
		Witer = atoi(argv[6]);
		WW = atoi(argv[7]);
		stepNR = atoi(argv[8]);
		stepR = stepNR;
		maxZ = atof(argv[9]);
		alfa = atof(argv[10]);

	} else if (argc == 7) {

		im_i = atoi(argv[1]);
		im_f = atoi(argv[2]);	
		bSEL = atoi(argv[3]);

		if (bSEL != 1) {

			printf ("Set Sel = 1 (Rigid) for 6 parameters \n");
			return(0);

		}

		Npyr =  atoi(argv[4]);
		stepR = atoi(argv[5]);
		maxZ = atof(argv[6]);

	} else  {

		printf ("Number of parameter should be 3, 6, 9 or 10 \n");
		return(0);

	}


	int cont, pyr, warps;
	char name[100];
	FileStorage SFlow;
	SFlow.open("./output/SFlow.xml", FileStorage::WRITE);

	Mat Mrgb = Mat(3,3,CV_32FC1);
	Mat Rq = Mat(4,1,CV_32FC1);
	Mat Tq = Mat(3,1,CV_32FC1);
	float *verRq = (float*)Rq.data;
	float *verTq = (float*)Tq.data;

	FileStorage fs("./settings/IntrinsicsRGB.xml",FileStorage::READ);
	fs["IntrinsicsRGB"] >> Mrgb;
	fs.release();

	float *m_m = (float *) (Mrgb.data);
	m_rgb[0][0] = m_m[0];
	m_rgb[1][1] = m_m[4];
	m_rgb[0][2] = m_m[2];
	m_rgb[1][2] = m_m[5];

	fcx = m_rgb[0][0];
	fcy = m_rgb[1][1];
	cx = m_rgb[0][2];
	cy = m_rgb[1][2];

	Mat gray = Mat(height[PyrLow], width[PyrLow], CV_8U);
	Mat RGBx = Mat(height[0], width[0], CV_8UC3);
	Mat RGBy = Mat(height[0], width[0], CV_8UC3);
	Mat RGBz = Mat(height[0], width[0], CV_8UC3);
	Mat Iwarped = Mat(height[0], width[0], CV_8U);
	Mat Dwarped = Mat(height[0], width[0], CV_8U);

	Mat OFimg = Mat(height[0], width[0], CV_8UC3);

	vector < Mat > OF(2);
	OF[0] = Mat::zeros(height[0], width[0], CV_32FC1);
	OF[1] = Mat::zeros(height[0], width[0], CV_32FC1);
	float *OFx = (float *) (OF[0].data);
	float *OFy = (float *) (OF[1].data);

	vector < Mat > OFrig(2);
	OFrig[0] = Mat::zeros(height[0], width[0], CV_32FC1);
	OFrig[1] = Mat::zeros(height[0], width[0], CV_32FC1);
	float *OFxRig = (float *) (OFrig[0].data);
	float *OFyRig = (float *) (OFrig[1].data);

	Mat Mask = Mat(height[0], width[0], CV_8U);

	Mat aux0 = Mat(height[0], width[0], CV_32FC1);
	Mat aux1 = Mat(height[0], width[0], CV_32FC1);

	Mat diffI = Mat(height[0], width[0], CV_8U);
	Mat diffD = Mat(height[0], width[0], CV_8UC3);

	vector < Mat > p_gray(Npyr + 1);
	vector < Mat > p_D(Npyr + 1);
	vector < Mat > p_Z(Npyr + 1);
	vector < Mat > a_gray(Npyr + 1);
	vector < Mat > a_D(Npyr + 1);
	vector < Mat > a_Z(Npyr + 1);
	vector < Mat > Ix(Npyr + 1);
	vector < Mat > Iy(Npyr + 1);
	vector < Mat > Zx(Npyr + 1);
	vector < Mat > Zy(Npyr + 1);
	vector < Mat > T(3 * (Npyr + 1));
	vector < Mat > W(3 * (Npyr + 1));
	vector < Mat > SF(3 * (Npyr + 1));
	vector < Mat > SFrig(3 * (Npyr + 1));
	
	for (int pyr = 0; pyr <= Npyr; pyr++) {

		p_gray[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		p_D[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		p_Z[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		a_gray[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		a_D[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		a_Z[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		Ix[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		Iy[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		Zx[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		Zy[pyr] = Mat(height[pyr], width[pyr], CV_32FC1);
		T[3 * pyr] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		T[3 * pyr + 1] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		T[3 * pyr + 2] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		W[3 * pyr] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		W[3 * pyr + 1] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		W[3 * pyr + 2] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		SF[3 * pyr] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		SF[3 * pyr + 1] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		SF[3 * pyr + 2] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		SFrig[3 * pyr] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		SFrig[3 * pyr + 1] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
		SFrig[3 * pyr + 2] = Mat::zeros(height[pyr], width[pyr], CV_32FC1);
	}

	P.setTo(0);
	O.setTo(0);

	Mat G = Mat(3, 3, CV_32FC1);
	float *verG = (float*)G.data;

	verG[0] = Gauss[0][0];
	verG[1] = Gauss[0][1];
	verG[2] = Gauss[0][2];
	verG[3] = Gauss[1][0];
	verG[4] = Gauss[1][1];
	verG[5] = Gauss[1][2];
	verG[6] = Gauss[2][0];
	verG[7] = Gauss[2][1];
	verG[8] = Gauss[2][2];

	Mat Ikappa = Mat::zeros(3, 3, CV_32FC1);
	float *VERkappa = (float *) (Ikappa.data);

	int *bandOF = new int[Npoints];
	int *bandSF0 = new int[Npoints];
	int *bandSF1 = new int[Npoints];
	int *bandSF2 = new int[Npoints];
	int *bandPYR = new int[Npoints];

	cont = 0;

	for (int i = 0; i < Npy; i++) {
		for (int j = 0; j < Npx; j++) {

			point[cont].x = j;
			point[cont].y = i;
			cont++;

		}
	}

	im = im_i;
	int Bframe = 0;
	Mat RGB;
	Mat DEPTH;

	while (Bframe < 2) {

		sprintf(name, "./Images/color_%04d.png",im);
		RGB = imread(name);

		sprintf(name, "./Images/depth_%04d.png",im);
		DEPTH = imread(name, CV_LOAD_IMAGE_UNCHANGED);

		if (RGB.empty() || DEPTH.empty()) {

			printf ("Invalid RGB or depth image \n");
			return(0);

		} else {

			if (im == im_i) {

				readyDepth(DEPTH, a_Z[0], a_D[0], constZ, maxZ, minZ);
				buenPunto(a_Z[0], point, bandSF0, Npoints, maxZ, minZ, cropX, cropY);

				cvtColor(RGB, gray0, CV_BGR2GRAY);
				pyr = 0;
				gray0.convertTo(aux0, CV_32FC1);
				filter2D(aux0, a_gray[0], -1, G, Point(-1, -1), 0, BORDER_DEFAULT);

				while (pyr < Npyr) {

					resize(a_gray[0], a_gray[pyr + 1], a_gray[pyr + 1].size(), 0, 0, CV_INTER_CUBIC);
					resize(a_Z[0], a_Z[pyr + 1], a_Z[pyr + 1].size(), 0, 0, CV_INTER_NN);
					resize(a_D[0], a_D[pyr + 1], a_D[pyr + 1].size(), 0, 0, CV_INTER_NN);
					pyr++;

				}
				while (pyr >= 0) {
					escalar(a_gray[pyr], 255);
					pyr--;
				}

				DEPTH.copyTo(DEPTH0);			

			} else {
				pyr = Npyr;
				while (pyr >= 0) {
					Sobel(a_Z[pyr], Zx[pyr], -1, 1, 0, 1);
					Sobel(a_Z[pyr], Zy[pyr], -1, 0, 1, 1);
					pyr--;
				}

				readyDepth(DEPTH, p_Z[0], p_D[0], constZ, maxZ, minZ);
				buenPunto(p_Z[0], point, bandSF1, Npoints, maxZ, minZ, cropX, cropY);

				cvtColor(RGB, gray1, CV_BGR2GRAY);
				pyr = 0;
				gray1.convertTo(aux0, CV_32FC1);
				filter2D(aux0, p_gray[0], -1, G, Point(-1, -1), 0, BORDER_DEFAULT);

				while (pyr < Npyr) {

					resize(p_gray[0], p_gray[pyr + 1], p_gray[pyr + 1].size(), 0, 0, CV_INTER_CUBIC);
					resize(p_Z[0], p_Z[pyr + 1], p_Z[pyr + 1].size(), 0, 0, CV_INTER_NN);
					resize(p_D[0], p_D[pyr + 1], p_D[pyr + 1].size(), 0, 0, CV_INTER_NN);
					pyr++;

				}
				while (pyr >= 0) {

					escalar(p_gray[pyr], 255);
					Sobel(p_gray[pyr], Ix[pyr], -1, 1, 0, 1);
					Sobel(p_gray[pyr], Iy[pyr], -1, 0, 1, 1);
					Sobel(p_Z[pyr], Zx[pyr], -1, 1, 0, 1);
					Sobel(p_Z[pyr], Zy[pyr], -1, 0, 1, 1);
					pyr--;

				}

				DEPTH.copyTo(DEPTH1);

				pyr = Npyr;

				while (pyr >= PyrLow) {

					float eta  = alfa  * Kappa;
					float etaR = alfaR * Kappa;

					int ext = Wext;

					while (ext > 0) {

						if (bSEL != 0) {

							trackPoints(p_gray[pyr], p_Z[pyr], a_gray[pyr], a_Z[pyr], a_D[pyr], Ix[pyr], Iy[pyr], Zx[pyr], Zy[pyr], Nrigid, point, P, O, SF[3 * pyr], SF[3 * pyr + 1], SF[3 * pyr + 2], pyr,stepR, m_rgb, 0, kI, kZ, width[pyr], height[pyr], bandSF0);
							SFrigidGLOBAL(SFrig[3*pyr], SFrig[3*pyr + 1], SFrig[3*pyr + 2],P,O, a_D[0], point, bandSF0, fcx, fcy, cx, cy, pyr);

						}

						warps = Witer;


						while (warps > 0 && bSEL != 1) {

							trackLKrig(p_gray[pyr], p_Z[pyr], a_gray[pyr], a_Z[pyr], a_D[pyr], Ix[pyr], Iy[pyr], Zx[pyr], Zy[pyr], Niter, point, T[3 * pyr], T[3 * pyr + 1], T[3 * pyr + 2], W[3 * pyr], W[3 * pyr + 1], W[3 * pyr + 2], SFrig[3 * pyr], SFrig[3 * pyr + 1], SFrig[3 * pyr + 2], pyr, m_rgb, WW, kI, kZ, Kappa, kRot*Kappa, width[pyr], height[pyr], bandSF0, stepNR);

							if (bTVvector == 1) {

								TV_Moreno(W[3 * pyr], W[3 * pyr + 1], W[3 * pyr + 2], TViter, etaR);
								TV_ROF(T[3 * pyr + 0],TViter,0.225,eta);
								TV_ROF(T[3 * pyr + 1],TViter,0.225,eta);
								TV_ROF(T[3 * pyr + 2],TViter,0.225,eta);					

							} else {

								TV_ROF(W[3 * pyr + 0],TViter,0.225,etaR);
								TV_ROF(W[3 * pyr + 1],TViter,0.225,etaR);
								TV_ROF(W[3 * pyr + 2],TViter,0.225,etaR);
								TV_ROF(T[3 * pyr + 0],TViter,0.225,eta);
								TV_ROF(T[3 * pyr + 1],TViter,0.225,eta);
								TV_ROF(T[3 * pyr + 2],TViter,0.225,eta);

							}

							if (warps == 1 && pyr != PyrLow) {

								resize(T[3 * pyr + 0], T[3 * (pyr - 1) + 0], T[3 * (pyr - 1) + 0].size(), 0, 0, CV_INTER_CUBIC);
								resize(T[3 * pyr + 1], T[3 * (pyr - 1) + 1], T[3 * (pyr - 1) + 1].size(), 0, 0, CV_INTER_CUBIC);
								resize(T[3 * pyr + 2], T[3 * (pyr - 1) + 2], T[3 * (pyr - 1) + 2].size(), 0, 0, CV_INTER_CUBIC);
								resize(W[3 * pyr + 0], W[3 * (pyr - 1) + 0], W[3 * (pyr - 1) + 0].size(), 0, 0, CV_INTER_CUBIC);
								resize(W[3 * pyr + 1], W[3 * (pyr - 1) + 1], W[3 * (pyr - 1) + 1].size(), 0, 0, CV_INTER_CUBIC);
								resize(W[3 * pyr + 2], W[3 * (pyr - 1) + 2], W[3 * (pyr - 1) + 2].size(), 0, 0, CV_INTER_CUBIC);

								SFrigidLOCAL(SF[3*(pyr-1)],SF[3*(pyr-1)+1],SF[3*(pyr-1)+2],T[3*(pyr-1)],T[3*(pyr-1)+1],T[3*(pyr-1)+2],W[3*(pyr-1)],W[3*(pyr-1)+1],W[3*(pyr-1)+2], a_D[0],point,bandSF0,fcx,fcy,cx,cy,pyr-1);


							}

							printf("Processing Warp:%i Pyr:%i \n", warps, pyr);
							warps--;

						}

						ext--;
					}

					pyr--;
				}


				if (PyrLow != 0) {

					resize(T[3 * PyrLow + 0], T[0], T[0].size(), 0, 0, CV_INTER_CUBIC);
					resize(T[3 * PyrLow + 1], T[1], T[1].size(), 0, 0, CV_INTER_CUBIC);	
					resize(T[3 * PyrLow + 2], T[2], T[2].size(), 0, 0, CV_INTER_CUBIC);
					resize(W[3 * PyrLow + 0], W[0], W[0].size(), 0, 0, CV_INTER_CUBIC);
					resize(W[3 * PyrLow + 1], W[1], W[1].size(), 0, 0, CV_INTER_CUBIC);
					resize(W[3 * PyrLow + 2], W[2], W[2].size(), 0, 0, CV_INTER_CUBIC);

					if (Nrigid != 0) {

						SFrigidGLOBAL(SFrig[0], SFrig[1], SFrig[2],P,O, a_D[0], point, bandSF0, fcx, fcy, cx, cy, 0);

					}
				}


				SFrigidLOCAL(SF[0], SF[1], SF[2], T[0], T[1], T[2], W[0], W[1], W[2], a_D[0], point, bandSF0, fcx, fcy, cx, cy, 0);

				cont = 0;

				OF[0].setTo(0);
				OF[1].setTo(0);

				OFrig[0].setTo(0);
				OFrig[1].setTo(0);

				while (cont < Npoints) {

					float *Rx = (float *) SFrig[0].data;
					float *Ry = (float *) SFrig[1].data;
					float *Rz = (float *) SFrig[2].data;

					float rx = Rx[cont];
					float ry = Ry[cont];
					float rz = Rz[cont];

					if (bandSF0[cont] == 1) {

						warpLK(rx, ry, rz, point[cont], NEWpointRig[cont], a_D[0], bandSF0[cont], bandOF[cont], fcx, fcy, cx, cy);

						if (bandOF[cont] == 1) {

							OFxRig[cont] = NEWpointRig[cont].x - point[cont].x;
							OFyRig[cont] = NEWpointRig[cont].y - point[cont].y;
						} 	

					} else {

						NEWpointRig[cont].x = point[cont].x;
						NEWpointRig[cont].y = point[cont].y;
						bandOF[cont] = 0;
					}

					float *PPx = (float *) SF[0].data;
					float *PPy = (float *) SF[1].data;
					float *PPz = (float *) SF[2].data;

					float px = PPx[cont];
					float py = PPy[cont];
					float pz = PPz[cont];

					if (bandSF0[cont] == 1) {

						warpLK(px, py, pz, point[cont], NEWpoint[cont], a_D[0], bandSF0[cont], bandOF[cont], fcx, fcy, cx, cy);

						if (bandOF[cont] == 1) {

							OFx[cont] = NEWpoint[cont].x - point[cont].x;
							OFy[cont] = NEWpoint[cont].y - point[cont].y;
						} 	

					} else {

						NEWpoint[cont].x = point[cont].x;
						NEWpoint[cont].y = point[cont].y;
						bandOF[cont] = 0;
					}

					unsigned char *tmp = (uchar *) (Mask.data);
					float *gris = (float *) a_gray[0].data;

					if (bandSF0[cont] == 1) {
						tmp[cont] = 255;		

					} else {
						tmp[cont] = 0;
					}
					cont++;
				}

				Vx.setTo(0);
				Vy.setTo(0);
				Vz.setTo(0);

				Vx = SF[0] + SFrig[0];
				Vy = SF[1] + SFrig[1];
				Vz = SF[2] + SFrig[2];

				if (ViewOutput == 1) {

					consistencySF(Vx,Vy,Vz, point, gray0, gray1, DEPTH0, DEPTH1, diffI, diffD, Iwarped, Dwarped, bandSF0, bandSF2, fcx, fcy, cx, cy, constZ);

					sprintf(name, "./output/Iwarped.png");
					imwrite(name, Iwarped);
					sprintf(name, "./output/Dwarped.png");
					imwrite(name, Dwarped);
					sprintf(name, "./output/Idiff.png");
					imwrite(name, diffI);
					sprintf(name, "./output/Ddiff.png");
					imwrite(name, diffD);

					sprintf(name, "./output/Mask.png");
					imwrite(name, Mask);

					if (bSEL != 1) {

						coloreaOF(OFimg, point, NEWpoint, Npoints, ViewOF, bandOF);
						sprintf(name, "./output/OF.png");
						imwrite(name, OFimg);

						float maxX = maximo(SF[0],bandSF0);
						float maxY = maximo(SF[1],bandSF0);
						float maxZ = maximo(SF[2],bandSF0);
						float max3D = findMAX3D(maxX,maxY,maxZ,0.25);

						coloreaSF(RGBx, SF[0], point, Npoints, bandSF0, max3D);
						coloreaSF(RGBy, SF[1], point, Npoints, bandSF0, max3D);
						coloreaSF(RGBz, SF[2], point, Npoints, bandSF0, max3D);

						sprintf(name, "./output/SFx-max%i.png", int (10 * maxX));
						imwrite(name, RGBx);
						sprintf(name, "./output/SFy-max%i.png", int (10 * maxY));
						imwrite(name, RGBy);
						sprintf(name, "./output/SFz-max%i.png", int (10 * maxZ));
						imwrite(name, RGBz);

					}

					if (bSEL != 0) {

						coloreaOF(OFimg, point, NEWpointRig, Npoints, ViewOF, bandOF);
						sprintf(name, "./output/OF_Rig.png");
						imwrite(name, OFimg);

						float maxX = maximo(SFrig[0],bandSF0);
						float maxY = maximo(SFrig[1],bandSF0);
						float maxZ = maximo(SFrig[2],bandSF0);
						float max3D = findMAX3D(maxX,maxY,maxZ,0.25);

						coloreaSF(RGBx, SFrig[0], point, Npoints, bandSF0,max3D);
						coloreaSF(RGBy, SFrig[1], point, Npoints, bandSF0,max3D);
						coloreaSF(RGBz, SFrig[2], point, Npoints, bandSF0,max3D);

						sprintf(name, "./output/SFx_Rig-max%i.png", int (10 * maxX));
						imwrite(name, RGBx);
						sprintf(name, "./output/SFy_Rig-max%i.png", int (10 * maxY));
						imwrite(name, RGBy);
						sprintf(name, "./output/SFz_Rig-max%i.png", int (10 * maxZ));
						imwrite(name, RGBz);

					}

				}

				SFlow << "Frame" << im;
				SFlow << "Rotation" << O;
				SFlow << "Translation" << P;
				SFlow << "SFx" << Vx;
				SFlow << "SFy" << Vy;
				SFlow << "SFz" << Vz;
				SFlow << "Mask" << Mask;


			}

			printf("Frame %i \n",im);
			im = im_f;  
			Bframe++;       
		}    	
	}

	SFlow.release();
	// cvNamedWindow("Iw");
	// imshow("Iw", Iwarped);
	// cvNamedWindow( "Idiff" );
	// imshow("Idiff",diffI);
	// cvWaitKey(0);
	return 0;
}
