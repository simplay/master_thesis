clc;
clear all;
close all;
addpath('../libs/flow-code-matlab');
W = load('../output/similarities/cars1_sim.dat');
d_a = sum(W,2);
D = diag(d_a);

D12 = D^(-0.5);
B = D12*(D-W)*D12;
[U,S,V] = eig(B);