cmake_minimum_required(VERSION 3.3)
project(srsf)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    Images/color/10.png
    Images/color/11.png
    Images/depth/10.png
    Images/depth/11.png
    Images/foobar/color/20.png
    Images/foobar/color/21.png
    Images/foobar/depth/20.png
    Images/foobar/depth/21.png
    Images/color_0030.png
    Images/color_0031.png
    Images/depth_0030.png
    Images/depth_0031.png
    settings/IntrinsicsRGB.xml
    basic.cpp
    basic.h
    basic.o
    basic.P
    depth.cpp
    depth.h
    depth.o
    depth.P
    dist-exclude
    flow.cpp
    flow.h
    flow.o
    flow.P
    libRGBD.a
    mainSRSF.cpp
    mainSRSF.o
    mainSRSF.P
    Makefile
    README.md
    rigid.cpp
    rigid.h
    rigid.o
    rigid.P
    semirigSF
    TV.cpp
    TV.h
    TV.o
    TV.P
    twist.cpp
    twist.h
    twist.o
    twist.P)

add_executable(srsf ${SOURCE_FILES})