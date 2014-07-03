/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */

//assume tabs between chrom, id, ref and space between samples
public class Genotype {
        
    protected static int numberOfSample = 0;
    protected static int numberOfSnp = 0;
    protected static String inputF;//input file
    protected static String outputF;//output file
    protected static File inputFile;
    protected static File targetFile;
    protected static FileWriter target;
    protected static StringBuffer snpSeq[];
    protected static StringBuilder posSeq = new StringBuilder();
    protected static StringBuilder chromSeq = new StringBuilder();
    protected static StringBuilder refSeq = new StringBuilder();
    protected static StringBuilder idOfSnp = new StringBuilder();
    
    protected void checkFile(File inputFile){
        if (inputFile.exists()) {
            System.out.println("Target file " + inputFile.getName() + " already exists");
            System.out.print("Choose to cover the existing file type 'C', re-enter please type 'E': ");
            Scanner scan;
            
            while(true){
                scan = new Scanner(System.in);
                String next = scan.next();
                if(next.equalsIgnoreCase("C")){
                    break;
                }else if(next.equalsIgnoreCase("E")){
                    System.out.print("Please input the new file name: ");
                    outputF = scan.next();
                    //System.out.println(outputF);
                    break;
                }else{
                    break;
                }
            }
        }  
    }
      
    protected void writeToTarget()throws IOException{
        System.out.println("Write to target!");
    }
    
    protected void closeOutputStream()throws IOException{
    }
    
    protected boolean checkSnp(String snp){
        boolean isSnp = false;
        String nextsnp;
        StringTokenizer next = new StringTokenizer(snp, "	");
        String first;
        String snps;
        String ref;
        
        for(int i = 0; i < 2; i++)
            nextsnp = next.nextToken();
    
        ref = next.nextToken();
        snps = next.nextToken();
        next = new StringTokenizer(snps);
        
        if(ref.equals("-")){
            first = next.nextToken();
            while(first.equals("-")){
                first = next.nextToken();
            }
        }else
            first = ref;
        
        for(int i = 1; next.hasMoreTokens(); i++){
            nextsnp = next.nextToken();
            //System.out.println("nextsnp is "+nextsnp);
            //System.out.println("first is "+ first);
            
            if(nextsnp.equals(first) == false && nextsnp.equals("-") == false){
                isSnp = true;
                break;
            }
        }
        return isSnp;
    }
    //id changed to pos
    protected void getSnpTypeAndChromAndId(Scanner input)throws IOException{
        input.useDelimiter("	");
        String nextSnpInfo;
        String snpInfo;
        StringTokenizer snp;
        StringBuffer snpType[] = new StringBuffer[numberOfSample];
        String nextLine;
        
        for(int i = 0; i< numberOfSample; i++){
            snpType[i] = new StringBuffer();
        }
        //get chrom, id, ref, alt, snpType
        //System.out.println("snp number "+numberOfSnp);
        for (long i = 0; i < numberOfSnp; i++){
            //System.out.println("i "+i);
            nextLine = input.nextLine();
            if(checkSnp(nextLine) == false){
                i --;
                continue;
            }
            snp = new StringTokenizer(nextLine, "	");
            chromSeq.append(snp.nextToken());
            chromSeq.append("\t");
            posSeq.append(snp.nextToken());
            posSeq.append("\t");
            refSeq.append(snp.nextToken());
            refSeq.append("\t");
            
            snpInfo = snp.nextToken();
            snp = new StringTokenizer(snpInfo, " ");
            for(int j = 0; j < numberOfSample; j++){
                nextSnpInfo = snp.nextToken();
                snpType[j].append(nextSnpInfo);
                snpType[j].append("\t");
            }
        }
        //System.out.println("snpType[0]"+snpType[0]);
       snpSeq = snpType;
        //System.out.println("sngType"+snpType[0]);
        //System.out.println("finish");
        
    }
    
    protected String checkGenotype(String input){
        if(input.equals("A")||input.equals("T")||input.equals("C")||input.equals("G")||input.equals("R")||input.equals("Y")||input.equals("K")||input.equals("M")||input.equals("S")||input.equals("W")||input.equals("B")||input.equals("D")||input.equals("H")||input.equals("V")||input.equals("N"))
            input = input + "//";
        for(;input.length() < 10;){
            input = input+" ";
        }
        return input;
    }
    
    protected void countNumberOfSnp(Scanner input){
        String nextLine;
        numberOfSnp = 0;
        idOfSnp = new StringBuilder();
        for(int i = 0;input.hasNextLine(); i++){
            nextLine = input.nextLine();
            //System.out.println("nextLine is "+nextLine);
            if(checkSnp(nextLine)==true){
                numberOfSnp ++;
                idOfSnp.append(i);
                idOfSnp.append("\t");
            }
            //System.out.println("b");
        }
        
        //System.out.println("Number of snp is " + numberOfSnp);
    }
    
    protected void countNumberOfSample(Scanner input){
        numberOfSample = 0;
        String nextGeno;
        if(input.hasNextLine()==false)
            return;
        nextGeno = input.nextLine();
        StringTokenizer next = new StringTokenizer(nextGeno, " ");
        while(next.hasMoreTokens()){
            numberOfSample ++;
            next.nextToken();
        }
        
        //System.out.println("Number of sample is " + numberOfSample);
    }    

}
