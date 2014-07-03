/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PositiveSelection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author yangyiqing
 */
public class RunSnpWindowsPi extends TajimaD{
    

    static RunSnpWindowsPi snpPi = new RunSnpWindowsPi();
    static String outFilePath;
    
    public RunSnpWindowsPi(){}
    
    public RunSnpWindowsPi(String path, String outFilePath, String subpopulation, int windowLength)throws IOException{
        this.outFilePath=outFilePath;
        new TajimaD(path,outFilePath,subpopulation,windowLength,false);
        
        for(int i = 0; i < numberOfTeam; i++){
            snpPi.writeToTarget(i);
        }
    }
    
    @Override
    public void writeToTarget(int teamNo)throws IOException{
        String inputFileName;
        int startNo;
        int endNo;
        chromosome = chrom.nextToken();
        outputF = outFilePath+"/"+chromosome + "_" + teamName[teamNo] +"_" +"Pi.txt";
        // Check if target file exists
        targetFile = new File(outputF);
        //snpPi.checkFile(targetFile);
        target = new FileWriter(outputF);
        
        target.write("Chr"+"\t"+"Start"+"\t"+"End"+"\t"+"pi"+"\t"+"#SNP");
        target.write("\n");
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println("File "+inputOriginFiles[i].getName());
            target.write(chromosome+"\t");
            inputFileName = inputOriginFiles[i].getName();
            startNo = inputFileName.indexOf("_");
            //System.out.println(startNo);
            endNo = inputFileName.lastIndexOf(".");
            //System.out.println(endNo);
            
            target.write((1+binsize*(Integer.parseInt(inputFileName.substring(startNo+1,endNo))-1))+"\t");
            target.write((binsize*Integer.parseInt(inputFileName.substring(startNo+1,endNo)))+"\t");
            target.write(Pis[i][teamNo]+"\t");
            target.write("\n");
            
        }
        target.close();
    }
    
    
}
    
    
    
