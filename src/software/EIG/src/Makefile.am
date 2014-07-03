SUBDIRS = nicksrc eigensrc
bin_PROGRAMS = convertf mergeit smartpca smartrel
AM_CFLAGS = -O0

convertf_SOURCES = convertf.c mcio.c admutils.c egsubs.c h2d.c
convertf_LDADD = $(top_builddir)/src/nicksrc/libnicklib.a
convertf_CFLAGS = -I $(top_builddir)/src/nicksrc 
convertf_LDFLAGS = -lm

mergeit_SOURCES = mergeit.c mcio.c admutils.c
mergeit_LDADD = $(top_builddir)/src/nicksrc/libnicklib.a
mergeit_CFLAGS = -I $(top_builddir)/src/nicksrc
mergeit_LDFLAGS = -lm

smartpca_SOURCES = eigensrc/smartpca.c eigensrc/eigsubs.c eigensrc/eigx.f \
    twsubs.c mcio.c qpsubs.c admutils.c egsubs.c regsubs.c eigensrc/smartsubs.c \
    badpairs.c eigensrc/thread.c 
smartpca_LDADD = $(top_builddir)/src/nicksrc/libnicklib.a
smartpca_CFLAGS = -I $(top_builddir)/src/nicksrc -pthread 
smartpca_LDFLAGS = -llapack -pthread

smartrel_SOURCES = eigensrc/smartrel.c eigensrc/eigsubs.c eigensrc/eigx.f \
    twsubs.c mcio.c qpsubs.c admutils.c egsubs.c regsubs.c eigensrc/smartsubs.c badpairs.c 
smartrel_LDADD = $(top_builddir)/src/nicksrc/libnicklib.a
smartrel_CFLAGS = -I $(top_builddir)/src/nicksrc
smartrel_LDFLAGS = -llapack
