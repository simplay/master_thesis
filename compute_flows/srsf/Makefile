PACKAGE=SEMIRIGSF
VERSION=1.0

SRCDIR = .

-include ./config.mk

CXX				?= c++
LD				= g++

CXXFLAGS_OPT	?= -O99
LDFLAGS			?= 

OPENCV_DIR  	?= $(shell pkg-config opencv --variable=prefix)

CONFIG			?= debug

OPENCV_CPPFLAGS ?= $(shell pkg-config opencv --cflags)

OPENCV_LDFLAGS 	?= $(shell pkg-config opencv --libs-only-L)

OPENCV_LIBS	?= $(shell pkg-config opencv --libs)

CPPFLAGS= \
	$(OPENCV_CPPFLAGS) 

CXXFLAGS=$(CXXFLAGS_OPT) $(CFLAGS_EXTRA) 
CFLAGS=$(CFLAGS_OPT) $(CFLAGS_EXTRA)
LDFLAGS=$(CFLAGS_OPT) $(CFLAGS_EXTRA) $(LDFLAGS_EXTRA)

COMPILE.c=$(CC) -c $(CFLAGS) $(CPPFLAGS)
COMPILE.cpp=$(CXX) -c $(CXXFLAGS) $(CPPFLAGS)

PROGRAMS = semirigSF

LIBRARY = libRGBD.a

all: $(LIBRARY) $(PROGRAMS)

lib: $(LIBRARY)

RGBD_SOURCES_CPP= twist.cpp \
	rigid.cpp \
	depth.cpp \
	flow.cpp \
	basic.cpp \
	TV.cpp \

SEMIRIGSF_SOURCE_CPP= mainSRSF.cpp 

RGBD_OBJS =  $(RGBD_SOURCES_CPP:.cpp=.o)
SEMIRIGSF_OBJS =  $(SEMIRIGSF_SOURCE_CPP:.cpp=.o)

SEMIRIGSF_LIBS =  $(OPENCV_LDFLAGS) $(OPENCV_LIBS)

$(LIBRARY): $(RGBD_OBJS)
	$(AR) rvu $@ $^
	
semirigSF: $(SEMIRIGSF_OBJS) $(LIBRARY)
	 $(LD) -o $@ $^ $(LDFLAGS) $(SEMIRIGSF_LIBS) $(LDADD)

SRCS_CPP = $(SEMIRIG_SOURCES_CPP) $(RGBD_SOURCES_CPP)

.SUFFIXES: .c .o .cpp

## gcc-only version:
%.o : %.c
	$(COMPILE.c) -MD -o $@ $<
	@cp $*.d $*.P; \
	    sed -e 's/#.*//' -e 's/^[^:]*: *//' -e 's/ *\\$$//' \
	        -e '/^$$/ d' -e 's/$$/ :/' < $*.d >> $*.P; \
	    rm -f $*.d

%.o : %.cpp
	$(COMPILE.cpp) -MD -o $@ $<
	@cp $*.d $*.P; \
	    sed -e 's/#.*//' -e 's/^[^:]*: *//' -e 's/ *\\$$//' \
	        -e '/^$$/ d' -e 's/$$/ :/' < $*.d >> $*.P; \
	    rm -f $*.d

## general version:
#MAKEDEPEND = gcc -M $(CPPFLAGS) -o $*.d $<
# %.o : %.c
# 	@$(MAKEDEPEND); \
# 	    cp $*.d $*.P; \
# 	    sed -e 's/#.*//' -e 's/^[^:]*: *//' -e 's/ *\\$$//' \
# 		-e '/^$$/ d' -e 's/$$/ :/' < $*.d >> $*.P; \
# 	    rm -f $*.d
# 	$(COMPILE.c) -o $@ $<

# %.o : %.cpp
# 	@$(MAKEDEPEND); \
# 	    cp $*.d $*.P; \
# 	    sed -e 's/#.*//' -e 's/^[^:]*: *//' -e 's/ *\\$$//' \
# 		-e '/^$$/ d' -e 's/$$/ :/' < $*.d >> $*.P; \
# 	    rm -f $*.d
# 	$(COMPILE.cpp) -o $@ $<

.PHONY: clean distclean
clean:
	-rm -rf $(PROGRAMS) $(LIBRARY) *.o  *~ *.dSYM

distclean: clean
	-rm -f $(SRCS_CPP:.cpp=.P) $(SRCS_C:.c=.P)

count:
	 wc -l $(SRCS_CPP) $(HEADERS)
	 
dist:
	mkdir $(PACKAGE)-$(VERSION)
	env COPYFILE_DISABLE=true COPY_EXTENDED_ATTRIBUTES_DISABLE=true tar --exclude-from dist-exclude --exclude $(PACKAGE)-$(VERSION) -cf - . | (cd $(PACKAGE)-$(VERSION); tar xf -)
	tar zcvf $(PACKAGE)-$(VERSION).tar.gz $(PACKAGE)-$(VERSION)
	rm -rf $(PACKAGE)-$(VERSION)

-include $(SRCS_CPP:.cpp=.P) $(SRCS_C:.c=.P)

