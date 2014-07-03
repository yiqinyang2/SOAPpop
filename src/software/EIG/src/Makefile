CC = gcc
FC = gfortran 

# ----- 
CFLAGS = -I../include  -g 
FFLAGS = -I../include  -g
LDFLAGS = -I../include -pthread 

ND=nicksrc
ED=eigensrc
NLIB = $(ND)/libnick.a
MATHLIB = -lm

LAPACK = -llapack
ATLAS_LAPACK = -L/usr/lib64/atlas -llapack -lf77blas -lcblas -latlas

EXE = convertf mergeit smartpca smartrel pcatoy smarteigenstrat twstats eigenstrat

TWTAB = \"../src/smarttables/twtable\"

NLIBOBJS = $(ND)/gauss.o $(ND)/gds.o $(ND)/getpars.o $(ND)/linsubs.o $(ND)/sortit.o $(ND)/statsubs.o \
        $(ND)/strsubs.o $(ND)/vsubs.o $(ND)/xsearch.o


# ----- default 
%:%.o
	$(CC) $(LDFLAGS) $^ -o $@ $(MATHLIB)

%.o: %.c
	$(CC) -c $(CFLAGS) -o $@ $<

%.o: %.f
	$(FC) -c $(FFLAGS) -o $@ $<


.PHONY: all clean clobber install



# ----- define TW table in statsubs
$(ND)/statsubs.o: $(ND)/statsubs.c
	$(CC) -c $(CFLAGS) -DTWTAB=$(TWTAB) -o $@ $<

# ----- build nicksrc/libnick.a
$(NLIB):	$(NLIBOBJS)
	ar -r $(NLIB) $(NLIBOBJS)





# ----- build EIGENSOFT 
convertf:	convertf.o mcio.o egsubs.o admutils.o h2d.o $(ED)/exclude.o $(NLIB)
	$(CC) $(LDFLAGS) $^ -o $@ $(MATHLIB)

mergeit:	mergeit.o mcio.o admutils.o $(NLIB)
	$(CC) $(LDFLAGS) $^ -o $@ $(MATHLIB) 

pcatoy: 	eigensrc/pcatoy.o eigensrc/eigsubs.o eigensrc/eigx.o $(NLIB)
	$(FC) $(LDFLAGS) -o $@ $(LAPACK) $^ $(MATHLIB)

smartpca:	$(ED)/smartpca.o twsubs.o mcio.o qpsubs.o admutils.o egsubs.o regsubs.o \
		$(ED)/eigsubs.o $(ED)/eigx.o \
		$(ED)/workqueue.o $(ED)/exclude.o $(NLIB)
	$(FC) $(LDFLAGS) -o $@ $(LAPACK) $^ $(MATHLIB)

smartrel:	$(ED)/smartrel.o twsubs.o mcio.o qpsubs.o admutils.o egsubs.o regsubs.o \
		$(ED)/eigsubs.o $(ED)/eigx.o $(NLIB)    
	$(FC) $(LDFLAGS) -o $@ $(LAPACK) $^ $(MATHLIB)

smarteigenstrat:	$(ED)/smarteigenstrat.o mcio.o admutils.o $(NLIB)
	$(FC) $(LDFLAGS) -o $@ $(LAPACK) $^ $(MATHLIB)

twstats:	$(ED)/twstats.o $(NLIB) 
	$(CC) $(LDFLAGS) $^ -o $@  $(MATHLIB)

eigenstrat:	$(ED)/eigenstrat.o 
	$(CC) $(LDFLAGS) $^ -o $@  $(MATHLIB)




# ----- phony targets
all:    $(EXE)

install:  all
	mv $(EXE) ../bin

clobber:
	rm -f *.o core core.* $(ND)/*.o $(ED)/*.o
	rm -f $(EXE)
	cd ../bin/ ; rm -f $(EXE) ; cd ../src

clean:
	rm -f *.o core core.* *.o





