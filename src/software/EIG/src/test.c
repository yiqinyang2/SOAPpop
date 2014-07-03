#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include "nicksrc/nicklib.h"

int main(int argc, char **argv)  {


  int n = 5, i;
  double *X = (double *)malloc(n*sizeof(double));
  double mean, stdv;

  srand(time(NULL));

  printf("Hello, world\n");
  for(i=0;i<n;i++)  {
    X[i] = gauss();
  }

  mean = 0.0;
  for(i=0;i<n;i++)  {
    mean += X[i];
  }

  stdv = 0.0;
  for(i=0;i<n;i++)  {
    stdv += (X[i]-mean)*(X[i]-mean);
  }
  stdv = sqrt(stdv)/(n-1);

  printf("Mean = %10.6f, Stdv = %10.6f\n", mean, stdv);

}
