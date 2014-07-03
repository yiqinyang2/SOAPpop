/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chromoPainter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */
public class ChromoPainterIntrogression {
    static int chromNum;
    static int sampleNo;
    static int donorNo;
    static int numberOfWindows;
    static File[] inputFiles;
    static boolean isDonor[];
    static int chrom[];
    static FileWriter[] outputFiles;
    static double[] chromReceiptOutputValue;
    static int windowSize;
    
    public ChromoPainterIntrogression(){}
    
    public ChromoPainterIntrogression(String inputPath, String subpopulation, String donorName, String outputPath, int size)throws IOException{
        String[] teamInfo;
        File[] inputOriginFiles;
        int count = 0;
        
        windowSize = size;
        
        File subpopulationFile = new File(subpopulation);
        Scanner subpop = new Scanner(subpopulationFile);
        
        for(int i = 0; subpop.hasNextLine(); i++){
            subpop.nextLine();
            sampleNo++;
        }
        subpop.close();
        
        teamInfo = new String[sampleNo];
        //System.out.println(sampleNo);
        subpop = new Scanner(subpopulationFile);
        for(int i = 0; subpop.hasNextLine(); i++){
            teamInfo[i] = subpop.nextLine();
        }
        subpop.close();
        
        donorNo = 0;
        isDonor = new boolean[sampleNo];
        for(int i = 0; i < sampleNo; i++){
            if(teamInfo[i].equalsIgnoreCase(donorName)){
                isDonor[i] = true;
                donorNo++;
            }
            else{
                isDonor[i] = false;
            }
        }
        
        outputFiles = new FileWriter[sampleNo];
        for(int i = 0; i < sampleNo; i++){
            if(isDonor[i] == false){
                outputFiles[i] = new FileWriter(new File(outputPath + "Ind" + (i+1) +".txt"));
                outputFiles[i].write("Chrom" + "\t" + "start" + "\t" + "end" +"\n");
            }
        }
        
        File filePath = new File(inputPath);
        inputOriginFiles = filePath.listFiles();
        numberOfWindows = inputOriginFiles.length;
        inputFiles = new File[numberOfWindows];
        
        for(int i = 0; i < numberOfWindows; i++){
            
            if((inputOriginFiles[i].getName()).endsWith(".out")){
                inputFiles[count] = new File(inputOriginFiles[i].getPath());
                
                count++;
            }
        }
        
        numberOfWindows = count;
        
        
        int[] chromBuffer = new int[numberOfWindows];
        int end;
        int chromNo;
        int currentNumberOfChrom = 0;
        boolean repeated;
        for(int i = 0; i < numberOfWindows; i++){
            repeated = false;
            end = inputFiles[i].getName().indexOf("_");
            chromNo = Integer.parseInt(inputFiles[i].getName().substring(0, end));
            
            for(int j = i-1; j >= 0; j--){
                //System.out.println(i+" "+ chrom[i]+" "+chrom[j]);
                if(chromNo == chromBuffer[j]){
                    repeated = true;
                    break;
                }
            }
            if(repeated == false){
                chromBuffer[currentNumberOfChrom] = chromNo;
                currentNumberOfChrom++;
            }
        }
        
        chromNum = currentNumberOfChrom;
        chrom = new int[chromNum];
        
        for(int i = 0; i < chromNum; i++){
            chrom[i] = chromBuffer[i];
        }
        
        Arrays.sort(chrom);
        
        for(int i = 0; i< chromNum; i++){
            chromoIntrogressionMainMethod(i);
        }
        
        for(int i = 0; i < sampleNo; i++){
            if(isDonor[i] == false)
                outputFiles[i].close();
        }
        
    }
    
    
    public void chromoIntrogressionMainMethod(int chromId)throws IOException{
        int end;
        int windowEnd;
        int chromNo;
        double introgressionValue;
        
        for(int i = 0; i < numberOfWindows; i++){
            end = inputFiles[i].getName().indexOf("_");
            chromNo = Integer.parseInt(inputFiles[i].getName().substring(0, end));
            if(chrom[chromId] == chromNo){
                introgressionValue = calculateIntrogressionValue(inputFiles[i]);
                for(int j = 0; j < sampleNo; j++){
                    if(isDonor[j] == false){
                        if(chromReceiptOutputValue[j] > introgressionValue){
                            windowEnd = inputFiles[i].getName().indexOf(".");
                            outputFiles[j].write(chrom[chromId] + "\t");
                            outputFiles[j].write(String.valueOf((Integer.parseInt(inputFiles[i].getName().substring(end+1, windowEnd))-1) * windowSize));
                            outputFiles[j].write("\t" + String.valueOf(Integer.parseInt(inputFiles[i].getName().substring(end+1, windowEnd)) * windowSize - 1) + "\n");
                        }
                    }
                }
                
            }
        }
    }
    
    public double calculateIntrogressionValue(File inputWindow)throws IOException{
        Scanner scan= new Scanner(inputWindow);
        double[] chromDonorOutputValue = new double[donorNo];
        int count = 0;
        double result = 0;
        double mean = 0;
        double var = 0;
      
        StringTokenizer value;
        scan.nextLine();
        
        chromReceiptOutputValue = new double[sampleNo];
        
        for(int i = 0; i < sampleNo; i++){
            
            if(isDonor[i] == true){
                value = new StringTokenizer(scan.nextLine());
                value.nextToken();
                chromDonorOutputValue[count++] = Double.parseDouble(value.nextToken());
            }
            else{
                value = new StringTokenizer(scan.nextLine());
                value.nextToken();
                chromReceiptOutputValue[i] = Double.parseDouble(value.nextToken());
            }
        }
        
        mean = calculateMean(chromDonorOutputValue, donorNo);
        var = calculateVar(chromDonorOutputValue, mean, donorNo);
        
        //System.out.println("mean "+mean);
        //System.out.println("var "+var);
        scan.close();
        return mean + var;
    }
    
    double calculateMean(double[] a, int num){
        double sum = 0;
        double no = Double.parseDouble(String.valueOf(num));
        
        for(int i = 0; i < num; i++){
            sum += a[i];
        }
        
        //System.out.println("sum "+ sum);
        return sum/no;
    }
    
    double calculateVar(double[] a, double mean, int num){
        double sum = 0;
        
        for(int i = 0; i < num; i++){
            sum += Math.pow((a[i] - mean), 2);
        }
        
        return Math.pow(sum, 0.5);
    }
    
}
