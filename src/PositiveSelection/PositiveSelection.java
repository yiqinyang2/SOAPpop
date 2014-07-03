/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PositiveSelection;

import Main.Genotype;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */

public class PositiveSelection extends Genotype{
    static public String subpopulation;
    static public File subpopulationFile;
    static public double[][] Thetas;
    static public double[][] Pis;
    static public int numberOfWindows;
    static public int binsize = 50000;
    static StringBuilder sub;
    static double theta;
    static double a1;
    static int m;
    static int S;
    static int n;
    static double P;
    static double pi;
    static double diff;
    static double c;
    static double[] D;
    static int numberOfDifferentSnp;
    static StringBuilder[] teamInfo;
    static int teamNo;
    static String[] teamName;
    static DecimalFormat df = new DecimalFormat("0.00000"); 
    
    @Override
    public void writeToTarget()throws IOException{
        target.write("Value of theta is "); 
        target.write(String.valueOf(theta));
        target.write("\n");
        target.write("Value of pi is "); 
        target.write(String.valueOf(pi));
        target.write("\n");
        
    }
    
    
    
    public void getSnpPi(){
        numberOfDifferentSnp = 0;
        for(int i = 0; i < numberOfSnp; i++){
            //System.out.println("i is "+i);
            //System.out.println("a");
            for(int j = 0; j < numberOfSample-1; j++){
               // System.out.println("b");
                for(int k =j+1; k < numberOfSample; k++){
                    //System.out.println("k is "+k);
                 //   System.out.println("c");
                    
                    if(snpSeq[j].charAt(2*i)!=snpSeq[k].charAt(2*i))
                        numberOfDifferentSnp++;
                    else if (snpSeq[j].charAt(2*i) == '-'){
                        numberOfSample --;
                        break;
                    }else if (snpSeq[k].charAt(2*i) == '-'){
                        continue;
                    }
                }
                //System.out.println("number of diff"+numberOfDifferentSnp);
            }
        }
        
        diff = numberOfDifferentSnp;
        
    }
    
    public void getPi(){
        diff = 0;
        getSnpPi();
        pi = diff/c/binsize;
        if(String.valueOf(pi).equals("NaN"))
            pi = 0;
        pi = Double.parseDouble(df.format(pi));
        //System.out.println("c is "+c);
    }
    
    public void getTheta(){
        theta = P/a1/binsize;
        if(String.valueOf(theta).equals("NaN"))
            theta = 0;
        theta = Double.parseDouble(df.format(theta));
    }
    
    public void getC(){
        c = (m * (m-1)) / 2;
        //System.out.println("c is "+ c);
    }
    
    public void getA(){
        a1 = 0;
        for(int i = 1; i < m; i++){
            a1 += Math.pow(i,-1);
        }
    }
    
    public void getP(int teamNo){
        int count = 0;
        String snp;
        String nextSample;
        boolean check;
        int sampleNo = 0;
        StringTokenizer[] sampleSnp = new StringTokenizer[numberOfSample];
        StringTokenizer sample = new StringTokenizer(teamInfo[teamNo].toString(), "\n");
        
        for(int i = 0;sample.hasMoreTokens(); i++){
            nextSample = sample.nextToken();
            //System.out.println(nextSample);
            sampleSnp[i] =new StringTokenizer(nextSample, "\t");
            sampleNo++;
        }
        
        for(int i = 0; i < numberOfSnp; i++){
            check = false;
            snp = sampleSnp[0].nextToken();
            for(int j = 1; j < sampleNo; j++){
                if(snp.equals(sampleSnp[j].nextToken())==false){
                    check = true;
                }
            }
            if(check == true)
                count++;
        }
        
        P = count;
        //System.out.println("number Of p "+P);
        //System.out.println("number Of Snp "+numberOfSnp);
        
    }
    
}
