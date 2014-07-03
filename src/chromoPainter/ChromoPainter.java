/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chromoPainter;

import Main.Genotype;
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

//haplotype file and genotype file must be matching, which means for 10_10.genotype
//there must be a corresponding 10_10.genotype.ped.haps.switch.out

//assume snps in one file are all from a same chromosome
public class ChromoPainter extends Genotype{
    static int donorTeamNo;
    static int receptTeamNo;
    static int numberOfDonor = 0;
    static int numberOfRecept = 0;
    static File[] inputOriginFiles;
    static File[] inputHaplotypeFiles;
    static File[] remRate;
    static int numberOfWindows;
    static ChromoPainter chromoPainter = new ChromoPainter();
    static int numberOfLines;
    static String[] snpSeq1;
    static String[] snpSeq2;
    static File labelFile;
    static File subpopulationFile;
    static boolean isDonor[];
    static boolean isRecept[];
    static String teamInfo[];
    static String donorInfo[];
    static String receptInfo[];
    static int donorPopNo[];
    static String outputPath;
    static String genoPath;
    static String haploPath;
    static String ratePath;
    
    public ChromoPainter(){}
    
    public ChromoPainter(String geno_path, String haplo_path, String rate_path, String donorReceptLabelFile, String subpopulation, String output_path)throws IOException{
        String[] buffer;
        int count = 0;
        int[] order;
        boolean[] check;
        int startNo;
        int endNo;
        int popNo;
        int sampleNo = 0;
        Scanner label;
        Scanner subpop;
        String donorLine;
        String receptLine;
        StringTokenizer donor;
        StringTokenizer recept;
        String buff;
        
        outputPath = output_path;
        genoPath = geno_path;
        haploPath = haplo_path;
        ratePath = rate_path;
        
        subpopulationFile = new File(subpopulation);
        subpop = new Scanner(subpopulationFile);
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
        
        
        labelFile = new File(donorReceptLabelFile);
        label = new Scanner(labelFile);
        donorLine = label.nextLine();
        receptLine = label.nextLine();
        donor = new StringTokenizer(donorLine);
        recept = new StringTokenizer(receptLine);
        
        for(int i = 0; donor.hasMoreTokens(); i++){
            donor.nextToken();
            donorTeamNo++;
        }
        donorInfo = new String[donorTeamNo];
        donor = new StringTokenizer(donorLine);
        for(int i = 0; donor.hasMoreTokens(); i++){
            donorInfo[i] = donor.nextToken();
        }
        donorPopNo = new int[donorTeamNo];
        for(int i = 0; i < donorTeamNo; i ++){
            popNo = 0;
            for(int j = 0; j < sampleNo; j++){
                if(teamInfo[j].equals(donorInfo[i]))
                    popNo++;
            }
            donorPopNo[i] = popNo;
        }
        
        for(int i = 0; recept.hasMoreTokens(); i++){
            recept.nextToken();
            receptTeamNo++;
        }
        receptInfo = new String[receptTeamNo];
        recept = new StringTokenizer(receptLine);
        for(int i = 0; recept.hasMoreTokens(); i++){
            receptInfo[i] = recept.nextToken();
        }
        
        label.close();
        
        isDonor = new boolean[sampleNo];
        isRecept = new boolean[sampleNo];
        for(int i = 0; i < sampleNo; i++){
            isDonor[i] = false;
            isRecept[i] = false;
        }
        
        for(int i = 0; i < sampleNo; i++){
            
            for(int j = 0; j < donorTeamNo; j++){
                if(teamInfo[i].equals(donorInfo[j])){
                    isDonor[i] = true;
                    numberOfDonor ++;
                    break;
                }
            }
            
            if(isDonor[i] == false){
                for(int j = 0; j < receptTeamNo; j++){
                    if(teamInfo[i].equals(receptInfo[j])){
                        isRecept[i] = true;
                        numberOfRecept ++;
                        break;
                    }
                }
            }
        }
        
        File filePath = new File(genoPath);
        inputOriginFiles = filePath.listFiles();
        numberOfWindows = inputOriginFiles.length;
        buffer = new String[numberOfWindows];
        
        for(int i = 0; i < numberOfWindows; i++){
            
            if((inputOriginFiles[i].getName()).endsWith(".genotype")){
                buffer[count] = new String();
                
                buffer[count] = inputOriginFiles[i].getPath();
                
                count++;
            }
        }
        
        numberOfWindows = count;
        order = new int[numberOfWindows];
        
        inputOriginFiles = new File[numberOfWindows];
        check = new boolean[numberOfWindows];
        
        //System.out.println(numberOfWindows);
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(buffer[i]);
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
                    inputOriginFiles[i] = new File(buffer[j]);
                    check[i] = false;
                    break;
                }
            }
        }
   
        filePath = new File(haploPath);
        inputHaplotypeFiles = filePath.listFiles();
        numberOfWindows = inputHaplotypeFiles.length;
        count = 0;
        buffer = new String[numberOfWindows];
        
        for(int i = 0; i < numberOfWindows; i++){
            
            if((inputHaplotypeFiles[i].getName()).endsWith(".out")){
                buffer[count] = new String();
                
                buffer[count] = inputHaplotypeFiles[i].getPath();
                
                count++;
            }
        }
        
        numberOfWindows = count;
        order = new int[numberOfWindows];
        
        inputHaplotypeFiles = new File[numberOfWindows];
        check = new boolean[numberOfWindows];
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(buffer[i]);
            check[i] = true;
            startNo = buffer[i].lastIndexOf("_");
            //System.out.println(startNo);
            endNo = buffer[i].lastIndexOf(".");
            buff = buffer[i].substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);   
            
            order[i] = Integer.parseInt(buffer[i].substring(startNo+1,endNo));
        }
        
        Arrays.sort(order);
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(order[i]);
            for(int j = 0; j < numberOfWindows; j++){
                startNo = buffer[j].lastIndexOf("_");
                //System.out.println(startNo);
                endNo = buffer[j].lastIndexOf(".");
                buff = buffer[j].substring(0, endNo);
                endNo = buff.lastIndexOf(".");
                buff = buff.substring(0, endNo);
                endNo = buff.lastIndexOf(".");
                buff = buff.substring(0, endNo);
                endNo = buff.lastIndexOf(".");
                buff = buff.substring(0, endNo);
                endNo = buff.lastIndexOf(".");
                buff = buff.substring(0, endNo);
                //System.out.println(endNo);
                if(check[i] = true && Integer.parseInt(buffer[j].substring(startNo+1,endNo)) == order[i]){
                    inputHaplotypeFiles[i] = new File(buffer[j]);
                    check[i] = false;
                    break;
                }
            }
        }
   
        count = 0;
        filePath = new File(ratePath);
        remRate = filePath.listFiles();
        numberOfWindows = remRate.length;
        buffer = new String[numberOfWindows];
        
        for(int i = 0; i < numberOfWindows; i++){
            
            if((remRate[i].getName()).endsWith(".remrate")){
                buffer[count] = new String();
                
                buffer[count] = remRate[i].getPath();
                
                count++;
            }
        }
        
        numberOfWindows = count;
        order = new int[numberOfWindows];
        
        remRate = new File[numberOfWindows];
        check = new boolean[numberOfWindows];
        
        //System.out.println(numberOfWindows);
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(buffer[i]);
            check[i] = true;
            
            startNo = buffer[i].lastIndexOf("_");
            //System.out.println(startNo);
            endNo = buffer[i].lastIndexOf(".");
            buff = buffer[i].substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);
            
            order[i] = Integer.parseInt(buffer[i].substring(startNo+1,endNo));
        }
        
        Arrays.sort(order);
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(order[i]);
            for(int j = 0; j < numberOfWindows; j++){
                startNo = buffer[j].lastIndexOf("_");
                //System.out.println(startNo);
                endNo = buffer[j].lastIndexOf(".");
                buff = buffer[j].substring(0, endNo);
                endNo = buff.lastIndexOf(".");
                buff = buff.substring(0, endNo);
                
                //System.out.println(endNo);
                if(check[i] = true && Integer.parseInt(buffer[j].substring(startNo+1,endNo)) == order[i]){
                    remRate[i] = new File(buffer[j]);
                    check[i] = false;
                    break;
                }
            }
        }
        System.out.println(inputOriginFiles.length);
        System.out.println(inputHaplotypeFiles.length);
        System.out.println(remRate.length);

        chromoMainMethod();
    }
    
    public static void chromoMainMethod()throws IOException{
        Scanner scan;
        
        //System.out.println("chrom");
        for(int i = 0; i < numberOfWindows; i++){
            scan = new Scanner(inputOriginFiles[i]);
            chromoPainter.countNumberOfSnp(scan);
            scan.close();
            //System.out.println("snp "+numberOfSnp);
            
            scan = new Scanner(inputOriginFiles[i]);
            chromoPainter.countNumberOfSample(scan);
            scan.close();
            //System.out.println("sample "+numberOfSample);
            
            //System.out.println(inputOriginFiles[i].getName());
            scan = new Scanner(inputOriginFiles[i]);
            chromoPainter.getSnpPos(scan);
            scan.close();
            
            scan = new Scanner(inputHaplotypeFiles[i]);
            chromoPainter.getSnpType(scan);
            scan.close();
            
            scan = new Scanner(inputOriginFiles[i]);
            countNumberOfLines(scan);
            scan.close();
            //System.out.println("line "+numberOfLines);
            
            writeToHaplotypeTarget(i);
            writeToRateTarget(i);
            
        }
        
        writeToDonorTarget();
        
    }
    
    public static void writeToDonorTarget()throws IOException{
        
        String outputFileName = outputPath + "donor_list_infile";
        File outputFile = new File(outputFileName);
        FileWriter output = new FileWriter(outputFile);
        
        for(int i = 0; i < donorTeamNo; i ++){
            output.write("pop"+String.valueOf(i+1)+" ");
            output.write(String.valueOf(donorPopNo[i]*2));
            output.write("\n");
        }
        
        output.close();
    }
    
    public static void writeToHaplotypeTarget(int windowNo)throws IOException{
        
        StringTokenizer name = new StringTokenizer(inputOriginFiles[windowNo].getName(), ".genotype");
        StringTokenizer snpId = new StringTokenizer(idOfSnp.toString(), "\t");
        StringTokenizer pos = new StringTokenizer(posSeq.toString(), "\t");
        StringTokenizer nextSnp;
        String outputFileName = outputPath + name.nextToken() + ".haplotype_infile";
        int currentId = Integer.parseInt(snpId.nextToken());
        File outputFile = new File(outputFileName);
        FileWriter output = new FileWriter(outputFile);
        
        output.write(String.valueOf(numberOfDonor*2)+"\n");
        output.write(String.valueOf(numberOfRecept+numberOfDonor)+"\n");// divided by 2??
        output.write(String.valueOf(numberOfSnp)+"\n");
        
        output.write("P ");

        for(int i = 0; i < numberOfLines; i++){
            //System.out.println(i);
            //System.out.println(currentId);
            
            if(i == currentId){
                output.write(pos.nextToken());
                if(snpId.hasMoreTokens())
                    output.write(" ");
                if(snpId.hasMoreTokens())
                    currentId = Integer.parseInt(snpId.nextToken());
            }
        }
        output.write("\n");
        
        for(int i = 0; i < numberOfSnp; i++){
            output.write("S");
        }
        output.write("\n");
        
        //System.out.println(numberOfSample);
        //System.out.println("teamNo "+donorTeamNo);
        for(int i = 0; i < donorTeamNo; i++){
            for(int j = 0; j < numberOfSample; j++){
                if(donorInfo[i].equals(teamInfo[j])){
                    nextSnp = new StringTokenizer(snpSeq1[i].toString());
                    while(nextSnp.hasMoreTokens())
                        output.write(nextSnp.nextToken());
                    output.write("\n");
                    nextSnp = new StringTokenizer(snpSeq2[i].toString());
                    while(nextSnp.hasMoreTokens())
                        output.write(nextSnp.nextToken());
                    output.write("\n");
               }
            }
        }
        
        for(int i = 0; i < numberOfSample; i++){
            if(isRecept[i] == true){
                nextSnp = new StringTokenizer(snpSeq1[i].toString());
                while(nextSnp.hasMoreTokens())
                    output.write(nextSnp.nextToken());
                output.write("\n");
                nextSnp = new StringTokenizer(snpSeq2[i].toString());
                while(nextSnp.hasMoreTokens())
                    output.write(nextSnp.nextToken());
                output.write("\n");
            }
        }
        
        output.close();
    }
    
    public static void writeToRateTarget(int windowNo) throws IOException{
        Scanner s = new Scanner(remRate[windowNo]);
        FileWriter output = new FileWriter(new File(outputPath + remRate[windowNo].getName()));
        StringTokenizer pos = new StringTokenizer(posSeq.toString(), "\t");
        StringTokenizer rate_pos;
        String ratePos;
        String haplotypePos;
        String rateLine;
        
        output.write("0 0" + "\n");
        
        while(pos.hasMoreTokens()){
            haplotypePos = pos.nextToken();
            while(s.hasNextLine()){
                rateLine = s.nextLine();
                rate_pos = new StringTokenizer(rateLine);
                ratePos = rate_pos.nextToken();
                if(ratePos.equals(haplotypePos)){
                    output.write(rateLine + "\n");
                    break;
                }
            }
        }
        s.close();
        output.close();
    }
    
    public void getSnpPos(Scanner input)throws IOException{
        input.useDelimiter("	");
        StringTokenizer snp;
        String nextLine;
        
        //get chrom, id, ref, alt, snpType
        for (long i = 0; i < numberOfSnp; i++){
            nextLine = input.nextLine();
            
            snp = new StringTokenizer(nextLine, "	");
            
            snp.nextToken();
            posSeq.append(snp.nextToken());
            posSeq.append("\t");
            
        }
        
    }
    
    public void getSnpType(Scanner input){
        input.useDelimiter("	");
        StringTokenizer snp;
        String nextLine;
        
        String snpType1[] = new String[numberOfSample]; 
        String snpType2[] = new String[numberOfSample];
        
        if(input.hasNextLine())
            input.nextLine();
        
        //get chrom, id, ref, alt, snpType
        for (int i = 0; i < numberOfSample; i++){
            if(input.hasNextLine())
                input.nextLine();
            snpType1[i] = input.nextLine();
            snpType2[i] = input.nextLine();
            
        }
        
        snpSeq1 = snpType1;
        snpSeq2 = snpType2;
        //System.out.println("finish");
        
    }
    
    @Override
    public void countNumberOfSnp(Scanner input){
        String nextLine;
        numberOfSnp = 0;
        idOfSnp = new StringBuilder();
        for(int i = 0;input.hasNextLine(); i++){
            
            nextLine = input.nextLine();
            //System.out.println("nextLine is "+nextLine);
            
            numberOfSnp ++;
            idOfSnp.append(i);
            idOfSnp.append("\t");
        }
    }
    
    public static void countNumberOfLines(Scanner scan){
        numberOfLines = 0;
        while(scan.hasNextLine()){
            numberOfLines ++;
            scan.nextLine();
        }
    }
    
}
