/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PositiveSelection;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */

//suppose for gene match files the results are from the same chromosome
public class TajimaD extends SplitTeams{
    
    static TajimaD ta = new TajimaD();
    static Scanner tajimaInput;
    static double[][] Tajimas;
    static double[] denominator;
    static File currentWindowFile;
    static File[] inputOriginFiles;
    static FileReader read;
    static BufferedReader readline;
    static StringTokenizer chrom;
    static Double[] piDiff;
    static Double[] thetaDiff;
    static Double[] tajimaDiff;
    static int[] piFileSeq;
    static int[] thetaFileSeq;
    static int[] tajimaFileSeq;
    static boolean check[];
    static String chromosome;
    static int piSnpNo;
    static int thetaSnpNo;
    static int tajimaSnpNo;
    static int firstCountedTeamSeq;
    static int secondCountedTeamSeq;
    static String geneLabelFile;
    static String outFilePath;
    static boolean writeToTarget;
    
   
    
    public TajimaD(){}
    
    //for output tajima output
    public TajimaD(String inputPath,String outFilePath, String subpopulationName, int windowLength,boolean writeTarget)throws IOException{
        String buffer[];
        int order[];
        int startNo;
        int endNo;
        FileWriter outputFileSeq = new FileWriter(new File("inputFileSeq.txt"));
        int count = 0;
        File filePath = new File(inputPath);
        this.outFilePath=outFilePath;
        inputOriginFiles = filePath.listFiles();
        
        //System.out.println(inputOriginFiles.length);
        numberOfWindows = inputOriginFiles.length;
        buffer = new String[numberOfWindows];
        
        //check postfix!
        for(int i = 0; i < numberOfWindows; i++){
            if((inputOriginFiles[i].getName()).endsWith(".genotype")){
                buffer[count] = new String();
                
                buffer[count] = inputOriginFiles[i].getName();
                
                count++;
            }
        }
        writeToTarget=writeTarget;
        numberOfWindows = count;
        
        order = new int[numberOfWindows];
        
        inputOriginFiles = new File[numberOfWindows];
        check = new boolean[numberOfWindows];
        
        //System.out.println(numberOfWindows);
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(i);
            check[i] = true;
            
            startNo = buffer[i].indexOf("_");
            //System.out.println(startNo);
            endNo = buffer[i].lastIndexOf(".");
            
            order[i] = Integer.parseInt(buffer[i].substring(startNo+1,endNo));
        }
        
        Arrays.sort(order);
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(order[i]);
            for(int j = 0; j < numberOfWindows; j++){
                startNo = buffer[j].indexOf("_");
                //System.out.println(startNo);
                endNo = buffer[j].lastIndexOf(".");
                //System.out.println(endNo);
                if(check[i] = true && Integer.parseInt(buffer[j].substring(startNo+1,endNo)) == order[i]){
                    inputOriginFiles[i] = new File(inputPath+"/"+buffer[j]);
                    check[i] = false;
                    break;
                }
            
            }
        }
        
        for(int i = 0; i < inputOriginFiles.length; i++){
            outputFileSeq.write(inputOriginFiles[i].getName());
            outputFileSeq.write("\n");
        }
        outputFileSeq.close();
        
        subpopulation = subpopulationName;
       
        subpopulationFile = new File(subpopulation);
        
        
        binsize = windowLength;
        
        ta.tajimaMainMethod();
       
       
    }
    
    //for gene matching
    public TajimaD(String inputPath, String subpopulationName, int windowLength, int firstTeamCounted, int secondTeamCounted, String geneLabelFileName)throws IOException{
        String buffer[];
        int order[];
        int startNo;
        int endNo;
        FileWriter outputFileSeq = new FileWriter(new File("inputFileSeq.txt"));
        int count = 0;
        File filePath = new File(inputPath);
        inputOriginFiles = filePath.listFiles();
        
        //System.out.println(inputOriginFiles.length);
        numberOfWindows = inputOriginFiles.length;
        buffer = new String[numberOfWindows];
        
        //check postfix!
        for(int i = 0; i < numberOfWindows; i++){
            if((inputOriginFiles[i].getName()).endsWith(".genotype")){
                buffer[count] = new String();
                
                buffer[count] = inputOriginFiles[i].getName();
                
                count++;
            }
        }
        
        numberOfWindows = count;
        
        order = new int[numberOfWindows];
        
        inputOriginFiles = new File[numberOfWindows];
        check = new boolean[numberOfWindows];
        
        //System.out.println(numberOfWindows);
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(i);
            check[i] = true;
            
            startNo = buffer[i].lastIndexOf("_");
            //System.out.println(startNo);
            endNo = buffer[i].lastIndexOf(".");
            
            order[i] = Integer.parseInt(buffer[i].substring(startNo+1,endNo));
        }
        
        Arrays.sort(order);
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(order[i]);
            for(int j = 0; j < numberOfWindows; j++){
                startNo = buffer[j].lastIndexOf("_");
                //System.out.println(startNo);
                endNo = buffer[j].lastIndexOf(".");
                //System.out.println(endNo);
                if(check[i] = true && Integer.parseInt(buffer[j].substring(startNo+1,endNo)) == order[i]){
                    inputOriginFiles[i] = new File(inputPath+"/"+buffer[j]);
                    System.out.println(inputOriginFiles[i].getName());
                    check[i] = false;
                    break;
                }
            
            }
        }
        
        for(int i = 0; i < inputOriginFiles.length; i++){
            outputFileSeq.write(inputOriginFiles[i].getName());
            outputFileSeq.write("\n");
        }
        outputFileSeq.close();
        
        subpopulation = subpopulationName;
       
        subpopulationFile = new File(subpopulation);
       
        
        binsize = windowLength;
        
        firstCountedTeamSeq = firstTeamCounted;
        secondCountedTeamSeq = secondTeamCounted;
        geneLabelFile = geneLabelFileName;
        File gene = new File(geneLabelFile);
       
        
        geneMainMethod();
       
        
    }
    
    public static void calculateTajima(int teamNo){
        for(int i = 0; i < numberOfWindows; i++){
            Tajimas[i][teamNo] = (Pis[i][teamNo]-Thetas[i][teamNo])/denominator[teamNo];
            if(String.valueOf(Tajimas[i][teamNo]).equals("NaN"))
                Tajimas[i][teamNo] = 0;
            Tajimas[i][teamNo] = Double.parseDouble(df.format(Tajimas[i][teamNo]));
        }
    }
    
    public void writeToTarget(int teamNo)throws IOException{
        String inputFileName;
        int startNo;
        int endNo;
        chromosome = chrom.nextToken();
        outputF = outFilePath+"/"+chromosome + "_" + teamName[teamNo] +"_" +"Tajima.txt";
        // Check if target file exists
        targetFile = new File(outputF);
        ta.checkFile(targetFile);
        target = new FileWriter(outputF);
        
        target.write("Chr"+"\t"+"Start"+"\t"+"End"+"\t"+"pi"+"\t"+"Theta"+"\t"+"Tajima's D");
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
            target.write(Thetas[i][teamNo]+"\t");
            
            target.write(Tajimas[i][teamNo]+"\t");
            target.write("\n");
            
            
        }
        target.close();
    }
        
    public static void writeToGeneFile()throws IOException{
        Scanner scan = new Scanner(new File(geneLabelFile));
        String line;
        StringTokenizer next;
        int startSnpPos;
        int endSnpPos;
        String labelChrom;
        String name;
        int count;
        
        chromosome = chrom.nextToken();
        outputF = chromosome +"_" +"gene.txt";
        targetFile = new File(outputF);
        ta.checkFile(targetFile);
        target = new FileWriter(outputF);
        
        while(scan.hasNextLine()){
            line = scan.nextLine();
            //System.out.println(line);
            next = new StringTokenizer(line);
            if(line.contains("mRNA")){
                //System.out.println("mRNA");
                 if(line.contains("Name")){
                     //System.out.println("name");
                     labelChrom = next.nextToken();
                     if(labelChrom.equals(chromosome)){
                         next.nextToken();
                         next.nextToken();
                         startSnpPos = Integer.parseInt(next.nextToken());
                         endSnpPos = Integer.parseInt(next.nextToken());
                         
                         for(int i = 0; i < 3; i++)
                             next.nextToken();
                         name = next.nextToken();
                         
                         next = new StringTokenizer(name,"=;");
                         for(int i = 0; i < 3; i++)
                             next.nextToken();
                         name = next.nextToken();
                         //System.out.println("name is "+name);
                         
                         count = 0;
                         for(int i = 0; i < piSnpNo; i++){
                             if((startSnpPos>1+binsize*(piFileSeq[i]-1)&&startSnpPos<binsize*piFileSeq[i])||(endSnpPos>1+binsize*(piFileSeq[i]-1)&&endSnpPos<binsize*piFileSeq[i]))
                                 count++;
                         }
                         //System.out.println("count "+count);
                         target.write(name+": "+String.valueOf(count)+" ");
                         
                         count = 0;
                         for(int i = 0; i < thetaSnpNo; i++){
                             if((startSnpPos>1+binsize*(thetaFileSeq[i]-1)&&startSnpPos<binsize*thetaFileSeq[i])||(endSnpPos>1+binsize*(thetaFileSeq[i]-1)&&endSnpPos<binsize*thetaFileSeq[i]))
                                 count++;
                         }
                         target.write(String.valueOf(count)+" ");
                         
                         count = 0;
                         for(int i = 0; i < tajimaSnpNo; i++){
                             if((startSnpPos>1+binsize*(tajimaFileSeq[i]-1)&&startSnpPos<binsize*tajimaFileSeq[i])||(endSnpPos>1+binsize*(tajimaFileSeq[i]-1)&&endSnpPos<binsize*tajimaFileSeq[i]))
                                 count++;
                         }
                         target.write(String.valueOf(count));
                         target.write("\n");
                     }
                     
                 }
             
            }
        }
        
//        target.write("pi:");
//        for(int i = 0; i < piSnpNo; i++){
//            target.write(String.valueOf(i+1)+":"+String.valueOf(binsize*(piFileSeq[i]-1)));
//            target.write("\n");
//        }
//        target.write("theta:");
//        for(int i = 0; i < thetaSnpNo; i++){
//            target.write(String.valueOf(i+1)+":"+String.valueOf(binsize*(thetaFileSeq[i]-1)));
//            target.write("\n");
//        }
//        target.write("tajima:");
//        for(int i = 0; i < tajimaSnpNo; i++){
//            target.write(String.valueOf(i+1)+":"+String.valueOf(binsize*(tajimaFileSeq[i]-1)));
//            target.write("\n");
//        }
        
        //close output stream
        target.close();
    }

    public static void geneMainMethod()throws IOException{
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println("Window "+i);
            currentWindowFile = inputOriginFiles[i];
            //System.out.println(currentWindowFile.getName());
            //System.out.println(subpopulationFile.getName());
            getTajimaInfo(currentWindowFile.getName(), i, currentWindowFile, subpopulationFile);
            
        }
        
        getDenominator();
        
        chrom = new StringTokenizer(chromSeq.toString(), "\t");
        
        Tajimas = new double[numberOfWindows][numberOfTeam];
        
        for(int i = 0; i < numberOfTeam; i++)
            calculateTajima(i);
        
        piDiff = new Double[numberOfWindows];
        thetaDiff = new Double[numberOfWindows];
        tajimaDiff = new Double[numberOfWindows];
        
        piFileSeq = new int[numberOfWindows];
        thetaFileSeq = new int[numberOfWindows];
        tajimaFileSeq = new int[numberOfWindows];
        
        getPiDiff();
        getThetaDiff();
        getTajimaDiff();
        
        chrom = new StringTokenizer(chromSeq.toString(), "\t");
        writeToGeneFile();
        
        
    }
    
    public void tajimaMainMethod()throws IOException{
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println("Window "+i);
            currentWindowFile = inputOriginFiles[i];
            //System.out.println(currentWindowFile.getName());
            //System.out.println(subpopulationFile.getName());
            getTajimaInfo(currentWindowFile.getName(), i, currentWindowFile, subpopulationFile);
            
        }
        
        getDenominator();
        
        chrom = new StringTokenizer(chromSeq.toString(), "\t");
        
        Tajimas = new double[numberOfWindows][numberOfTeam];
        
        for(int i = 0; i < numberOfTeam; i++)
            calculateTajima(i);
        
        if(writeToTarget)
        {
        	 for(int i = 0; i < numberOfTeam; i++)
                 writeToTarget(i);
        }
    }

   public static void getPiDiff(){
        Double[] buffer = new Double[numberOfWindows];
        Double[] buffer2 = new Double[numberOfWindows];
        int startNo;
        int endNo;
        int count;
        piSnpNo = 0;
        
        for(int i = 0; i < numberOfWindows; i++){
            piDiff[i] = Math.abs(Pis[i][secondCountedTeamSeq-1] - Pis[i][firstCountedTeamSeq-1]);
            buffer2[i] = piDiff[i];
            check[i] = true;
        }
        
        
        Arrays.sort(buffer2, Collections.reverseOrder());
        count = Integer.parseInt(new java.text.DecimalFormat("0").format((Math.ceil(piDiff.length*0.01))));
        
        for(int i = 0; i < numberOfWindows; i++){
            if(piDiff[i]>=buffer2[count-1]){
                buffer[piSnpNo] = piDiff[i];
                piSnpNo++;
            }
        }
        
        for(int i = 0; i < piSnpNo; i++){
            //System.out.println(thetaDiff[i]);
            for(int j = 0; j < numberOfWindows; j++){
                if(check[j] = true && piDiff[j] == buffer[i]){
                    startNo = inputOriginFiles[j].getName().lastIndexOf("_");
                    endNo = inputOriginFiles[j].getName().lastIndexOf(".");
                    piFileSeq[i] = Integer.parseInt(inputOriginFiles[j].getName().substring(startNo+1,endNo));
                    check[j] = false;
                    break;
                }
            }
        }
    }
    
    public static void getThetaDiff(){
        Double[] buffer = new Double[numberOfWindows];
        Double[] buffer2 = new Double[numberOfWindows];
        int startNo;
        int endNo;
        int count;
        thetaSnpNo = 0;
        
        for(int i = 0; i < numberOfWindows; i++){
            thetaDiff[i] = Math.abs(Thetas[i][secondCountedTeamSeq-1] - Thetas[i][firstCountedTeamSeq-1]);
            buffer2[i] = thetaDiff[i];
            check[i] = true;
        }
        
        
        Arrays.sort(buffer2, Collections.reverseOrder());
        count = Integer.parseInt(new java.text.DecimalFormat("0").format((Math.ceil(thetaDiff.length*0.01))));
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(thetaDiff[i]+"\n");
            if(thetaDiff[i]>=buffer2[count-1]){
                buffer[thetaSnpNo] = thetaDiff[i];
                //System.out.println("final buffer "+buffer[thetaSnpNo]);
                thetaSnpNo++;
            }
        }
        
        
        for(int i = 0; i < thetaSnpNo; i++){
            //System.out.println(thetaDiff[i]);
            for(int j = 0; j < numberOfWindows; j++){
                if(check[j] = true && thetaDiff[j] == buffer[i]){
                    startNo = inputOriginFiles[j].getName().indexOf("_");
                    endNo = inputOriginFiles[j].getName().lastIndexOf(".");
                    thetaFileSeq[i] = Integer.parseInt(inputOriginFiles[j].getName().substring(startNo+1,endNo));
                    check[j] = false;
                    break;
                }
            }
        }
        //System.out.println(thetaFileSeq[0]);
    }
    public static void getTajimaDiff(){
        Double[] buffer = new Double[numberOfWindows];
        Double[] buffer2 = new Double[numberOfWindows];
        int startNo;
        int endNo;
        int count;
        tajimaSnpNo = 0;
        
        for(int i = 0; i < numberOfWindows; i++){
            tajimaDiff[i] = Math.abs(Tajimas[i][secondCountedTeamSeq-1] - Tajimas[i][firstCountedTeamSeq-1]);
            buffer2[i] = tajimaDiff[i];
            check[i] = true;
        }
        
        
        Arrays.sort(buffer2, Collections.reverseOrder());
        count = Integer.parseInt(new java.text.DecimalFormat("0").format((Math.ceil(tajimaDiff.length*0.01))));
        
        for(int i = 0; i < numberOfWindows; i++){
            if(tajimaDiff[i]>=buffer2[count-1]){
                buffer[tajimaSnpNo] = tajimaDiff[i];
                tajimaSnpNo++;
            }
        }
        
        for(int i = 0; i < tajimaSnpNo; i++){
            for(int j = 0; j < numberOfWindows; j++){
                if(check[j] = true && tajimaDiff[j] == buffer[i]){
                    startNo = inputOriginFiles[j].getName().indexOf("_");
                    endNo = inputOriginFiles[j].getName().lastIndexOf(".");
                    tajimaFileSeq[i] = Integer.parseInt(inputOriginFiles[j].getName().substring(startNo+1,endNo));
                    check[j] = false;
                    break;
                }
            }
        }
    }
    
    
    //name of theta and pi output files are defined in Theta.java and Pi.java
    public static void getTajimaInfo(String fileName, int windowNo, File input, File sub)throws IOException{
        
        Pi.piMainMethod(input, sub, false, false, windowNo);
        
        Theta.thetaMainMethod(input, sub, false, false, windowNo);
        
    }
    
    public static void getDenominator()throws IOException{
       
        denominator = new double[numberOfTeam];
        double[] average = new double[numberOfTeam];
        double numerator = 0;
        
        for(int i = 0; i < numberOfWindows; i++){
            for(int j = 0; j < numberOfTeam; j++){
                average[j] = 0;
                //System.out.println("Pi "+Pis[0][0]);
                //System.out.println("Theta "+Thetas[0][0]);
                average[j] += Pis[i][j];
                average[j] -= Thetas[i][j];
                //System.out.println(average);
            }
        }
        
        for(int i = 0; i < numberOfTeam; i++){
            average[i] /= numberOfWindows;
            //System.out.println("average"+average[i]);
            //numerator = 0;
            for(int j = 0; j < numberOfWindows; j++){
                
                numerator += Math.pow((Pis[j][i] - Thetas[j][i] - average[i]),2);
                //System.out.println("Pis is "+ Pis[j][i]);
                //System.out.println("Thetas is "+ Thetas[j][i]);
                //System.out.println("average is "+ average[i]);
                //System.out.println("numerator is "+ numerator);
            }
            denominator[i] = Math.pow(numerator / numberOfWindows, 0.5);
            //System.out.println(denominator[i]);
        }
         
    }
   

}
