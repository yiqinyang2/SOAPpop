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
public class Theta extends SplitTeams{
    
    static Theta t = new Theta();
    static Scanner thetaInput;
    static String outFilePath;
    
    public Theta(){}    
    
    public Theta(String inputFileName,String outFilePath, String subpopulationName, int windowLength,boolean writeToTarget)throws IOException{
        
        numberOfTeam = 0;
        
        inputF = inputFileName;
        this.outFilePath=outFilePath;
        //check if input file exists
        inputFile = new File(inputF);
      
        
        subpopulation = subpopulationName;
        subpopulationFile = new File(subpopulation);
      
        
        binsize = windowLength;
        
        
        thetaMainMethod(inputFile, subpopulationFile, true, writeToTarget, 0);
       
    }
    
    public static void thetaMainMethod(File inputFile, File subpopulationFile, boolean checkTarget, boolean writeToTarget, int windowNo)throws IOException{
        t.mainMethod(inputFile);
        
        outputF = outFilePath+".Theta.txt";
        
        // Check if target file exists
        if(writeToTarget)
        {
        	targetFile = new File(outputF);
            if(checkTarget == true)
                t.checkFile(targetFile);
            target = new FileWriter(outputF);
        }
        
        
        for(int i = 0; i < numberOfTeam; i++){
            //System.out.println("number of team is " + numberOfTeam);
            //System.out.println("team "+(i+1));
            calculateTheta(i, writeToTarget, windowNo);
            //System.out.println("m is "+m);
            //System.out.println("a1 is"+a1);
            //System.out.println("p is "+P);
        }
        
        //close output stream
        if(writeToTarget)
        	target.close();
    }
    
    public static void calculateTheta(int teamNo, boolean writeToTarget, int windowNo)throws IOException{
        
        S = numberOfSnp;
        //System.out.println("S is "+ S);
        
        thetaInput = new Scanner(teamInfo[teamNo].toString());
        t.countNumberOfSample(thetaInput);
        m = numberOfSample;
        thetaInput.close();
        //System.out.println("m is "+m);
        
        t.getA();
        //System.out.println("a1 is "+ a1);
        
        t.getP(teamNo);
        //System.out.println("p is " + P);
        
        t.getTheta();
        //System.out.println("theta is "+ theta);
        
        if(writeToTarget)
            t.writeToTarget(teamNo);
        else
            saveThetaInMemory(windowNo, teamNo);
    }
    
    @Override
    public void countNumberOfSample(Scanner input){
        boolean isSample = false;
        numberOfSample = 0;
        StringTokenizer next;
        while(input.hasNextLine()){
            next = new StringTokenizer(input.nextLine(), "\t");
            while(next.hasMoreTokens()){
                if(next.nextToken().equals("-")==false){
                    isSample = true;
                    break;
                }
            }
            numberOfSample++;
        }
            
    }
    
    
    public void writeToTarget(int teamNo)throws IOException{
    	
        target.write("Team " );//!
        target.write(teamName[teamNo]);
        target.write("\n");
        target.write(String.valueOf(theta));
        target.write("\n");
    }
    
    public static void saveThetaInMemory(int windowNo, int teamNo){
        if(windowNo == 0 && teamNo == 0){
            Thetas = new double[numberOfWindows][numberOfTeam];
        }
        
        Thetas[windowNo][teamNo] = theta;
        
        //System.out.println("theta is "+theta);
    }
}
