/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PositiveSelection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */


public class Pi extends SplitTeams{
    
    static Pi p = new Pi();
    static Scanner piInput;
    static String outFilePath;
    public Pi(){}
    
    public Pi(String inputFileName,String outFilePath, String subpopulationName, int windowLength,boolean writerToTarget)throws IOException{
       
        numberOfTeam = 0;
        
        inputF = inputFileName;
        this.outFilePath=outFilePath;
        //check if input file exists
        inputFile = new File(inputF);
       
        subpopulation = subpopulationName;
        subpopulationFile = new File(subpopulation);
        
        
        binsize = windowLength;
        
        piMainMethod(inputFile, subpopulationFile, true, writerToTarget, 0);
       
    }
    
    public static void piMainMethod(File inputFile, File subpopulationFile, boolean checkTarget, boolean writeToTarget, int windowNo)throws IOException{
        p.mainMethod(inputFile);
        outputF =outFilePath+ ".Pi.txt";
        
        // Check if target file exists
        if(writeToTarget)
        {
        	 targetFile = new File(outputF);
             if(checkTarget == true)
                 p.checkFile(targetFile);
             target = new FileWriter(outputF);
        }
         
        for(int i = 0; i < numberOfTeam; i++){
            //System.out.println("(pi)team "+(i+1));
            calculatePi(i,writeToTarget, windowNo);
            
        }

        //close input stream
        piInput.close();
        
        //close output stream
        if(writeToTarget)
        	target.close();
    }
    public static void calculatePi(int teamNo, boolean writeToTarget, int windowNo)throws IOException{
        
        piInput = new Scanner(teamInfo[teamNo].toString());
        p.countNumberOfSample(piInput);
        piInput.close();
        
        piInput = new Scanner(teamInfo[teamNo].toString());
        p.getSnpTypeAndChromAndId(piInput);
        m = numberOfSample;
        piInput.close();
        //System.out.println("m "+m);
        
        p.getC();
        //System.out.println("c is "+ c);
        
        p.getPi();
        
        if(writeToTarget)
            p.writeToTarget(teamNo);
        else
            savePiInMemory(windowNo, teamNo);
        
    }
    
    @Override
    public void getSnpTypeAndChromAndId(Scanner input){
        
        StringTokenizer nextSnpType;
        snpSeq = new StringBuffer[numberOfSample];
        
        for(int i = 0; i < numberOfSample; i++){
            snpSeq[i] = new StringBuffer();
        }
        
        for(int i = 0; i < numberOfSample; i++){
            
            nextSnpType = new StringTokenizer(input.nextLine(), "	");
            
            while(nextSnpType.hasMoreTokens()){
                snpSeq[i].append(nextSnpType.nextToken());
                snpSeq[i].append("\t");
            }
        }
    }

    @Override
    public void countNumberOfSample(Scanner input){
        
        numberOfSample = 0;
        
        while(input.hasNextLine()){
            input.nextLine();
            numberOfSample++;
        }
    }
    
    
    public void writeToTarget(int teamNo)throws IOException{
    	target.write("Team " );//!
        target.write(teamName[teamNo]);
        target.write("\n");
        target.write(String.valueOf(pi));
        target.write("\n");
    }
    
    public static void savePiInMemory(int windowNo, int teamNo){
        
        if(windowNo == 0 && teamNo == 0){
            Pis = new double[numberOfWindows][numberOfTeam];
        }
        
        Pis[windowNo][teamNo] = pi;
        //System.out.println("pi is "+pi);
        
    }
}
