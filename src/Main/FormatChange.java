/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import java.io.File;
import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */
public class FormatChange {
    static int numberOfSample = 0;
    static long sampleLength = 0;
    static int countForSampleLength = 0;
    static String inputF;//input file
    static String outputF;//output file
    static String ref;
    static String alt;
    static File inputFile;
    static File targetFile;
    static FileWriter target;
    static StringBuffer snpSeq[];
    static StringBuilder chromSeq;
    static StringBuilder refSeq;
    static StringBuilder posSeq;
    static StringBuffer genotype = new StringBuffer();
    
    public FormatChange(){}
    
    public FormatChange(String inputFileName,String outFilePath)throws IOException{
        
        inputF = inputFileName;
        inputFile = new File(inputF);
  
        outputF = outFilePath+".genotype";
        targetFile = new File(outputF);
        checkFile(targetFile);
        targetFile = new File(outputF);
        
        mainMethod();
    }
    
    public static void mainMethod()throws IOException{
        
        target = new FileWriter(outputF);
        
        Scanner input;
        
        input = new Scanner(inputFile);
        countSampleLength(input);

        //get NumberOfSample as result
        input = new Scanner(inputFile);
        countNumberOfSample(input);//must occurs after countSampleLength() because some values are needed to be counted.
        
        //list genotypes and snp
        input = new Scanner(inputFile);
        getSnpName(input);
        
        input = new Scanner(inputFile);
        printGenotypeAndSng(input, target);
        input.close();
        
        writeToTarget();
        target.close();
        
        //System.out.println("Done!");
    }
    
    public static void checkFile(File inputFile){
        //System.out.println("check");
        if (inputFile.exists()) {
            System.out.println("Target file " + inputFile.getName() + " already exists");
            System.out.print("Choose to cover the existing file type 'C', re-enter please type 'E': ");
            Scanner scan;
            //System.out.println("a");
            while(true){
                scan = new Scanner(System.in);
                String next = scan.next();
                if(next.equalsIgnoreCase("C")){
                    scan.close();
                    break;
                }else if(next.equalsIgnoreCase("E")){
                    System.out.print("Please input the new file name: ");
                    outputF = scan.next();
                    scan.close();
                    //System.out.println(outputF);
                    break;
                }else{
                    scan.close();
                    break;
                }
            }
        }  
    }
    
    public static void writeToTarget()throws IOException{
        StringTokenizer nextChrom = new StringTokenizer(chromSeq.toString(), "\t");
        StringTokenizer nextPos = new StringTokenizer(posSeq.toString(), "\t");
        StringTokenizer nextRef = new StringTokenizer(refSeq.toString(), "\t");
        
        
        
        for(int i = 0; i< sampleLength; i++){
            //System.out.println(i);
            target.write(nextChrom.nextToken());
            target.write('\t');
            target.write(nextPos.nextToken());
            target.write('\t');
            target.write(nextRef.nextToken());
            target.write('\t');
            for(int j = 0; j < numberOfSample ; j++){
                target.write(snpSeq[j].substring(i, i+1)+' ');
            }
            target.write('\n');
        }
    }
    
    public static void printGenotypeAndSng(Scanner input, FileWriter target)throws IOException{
        input.useDelimiter("	");
        StringBuffer snpType[] = new StringBuffer[numberOfSample]; 
        StringBuilder chrom = new StringBuilder();
        StringBuilder pos = new StringBuilder(); 
        StringBuilder refs = new StringBuilder(); 
        StringTokenizer next = new StringTokenizer("");
        String snpInfo;
        
        //initialize snpType
        for(int i = 0; i< numberOfSample; i++){
            snpType[i] = new StringBuffer();
            
        }
        
        //jump the instruction lines
        for(int i = 0; i < (countForSampleLength - 1); i++){
            input.nextLine();
        }
        
        //get chrom, id, ref, alt, snpType
        for (long i = 0; i < sampleLength; i++){
            
            if(i == 0){
                chrom.append(input.next());
                chrom.append("\t");
            }
            
            
            pos.append(input.next());
            pos.append("\t");
            
            input.next();
            
            ref = input.next();
            ref = ref.toUpperCase();
            refs.append(ref);
            refs.append("\t");
            //System.out.println("ref:"+ref);
            alt = input.next();
            alt = alt.toUpperCase();
            //System.out.println("alt: "+alt);
            for(int j = 0; j < 4; j++)
                input.next();
            
            for(int j = 0; j < numberOfSample; j++ ){
                if(j == numberOfSample - 1){
                    next = new StringTokenizer(input.next(),"\n");
                    snpInfo = next.nextToken().substring(0, 3);
                }else
                    snpInfo = input.next().substring(0, 3);
                
                //System.out.println(j+": sng info:" + snpInfo);
                if(snpInfo.substring(0, 1).equals("0") && snpInfo.substring(2, 3).equals("0")){
                    snpType[j].append(ref);
                    //System.out.println(snpType[j]);
                }else if((snpInfo.substring(0, 1).equals("0") && snpInfo.substring(2, 3).equals("1"))||(snpInfo.substring(0, 1).equals("1") && snpInfo.substring(2, 3).equals("0"))){
                    if((ref.equals("A")&&alt.equals("G"))||(ref.equals("G")&&alt.equals("A"))){
                        snpType[j].append("R");
                    }else if((ref.equals("C")&&alt.equals("T"))||(ref.equals("T")&&alt.equals("C"))){
                        snpType[j].append("Y");
                    }else if((ref.equals("G")&&alt.equals("T"))||(ref.equals("T")&&alt.equals("G"))){
                        snpType[j].append("K");
                    }else if((ref.equals("A")&&alt.equals("C"))||(ref.equals("C")&&alt.equals("A"))){
                        snpType[j].append("M");
                    }else if((ref.equals("C")&&alt.equals("G"))||(ref.equals("G")&&alt.equals("C"))){
                        snpType[j].append("S");
                    }else if((ref.equals("A")&&alt.equals("T"))||(ref.equals("T")&&alt.equals("A"))){
                        snpType[j].append("W");
                    }else{
                        snpType[j].append("?");
                    }
                    
                }else if(snpInfo.substring(0, 1).equals("1") && snpInfo.substring(2, 3).equals("1")){
                    snpType[j].append(alt);
                }else if(snpInfo.substring(0, 1).equals(".")||snpInfo.substring(2, 3).equals(".")){
                    snpType[j].append("-");
                }else{
                    snpType[j].append("?");
                }
            
                //System.out.println(next.nextToken());
                if(next.hasMoreTokens()){
                    chrom.append(next.nextToken());
                    chrom.append("\t");
                }
        
            }     
        snpSeq = snpType;
        chromSeq = chrom;
        posSeq = pos;
        refSeq = refs;
        }
    }
    
    public static void getSnpName(Scanner input){
        String nextGenotype;
        input.useDelimiter("	");
        for(int i = 0; i < (countForSampleLength - 2); i++){
            input.nextLine();
        }
        
        //System.out.println(input.next());
        for(int i = 0; i < 9; i++){
            input.next();
        }
        //System.out.println("finish");
        for(int i = 0; i < numberOfSample-1; i++){
            nextGenotype = input.next();
            nextGenotype = checkGenotype(nextGenotype);
            genotype.append(nextGenotype);
            //System.out.println(i+genotype[i]);
            genotype.append("\t");
        }
        nextGenotype = input.next();
        StringTokenizer next = new StringTokenizer(nextGenotype, "\n");
        nextGenotype = checkGenotype(next.nextToken());
        genotype.append(nextGenotype);
        
    }
    
    public static String checkGenotype(String input){
        //System.out.println(input);
        if(input.equals("A")||input.equals("T")||input.equals("C")||input.equals("G")||input.equals("R")||input.equals("Y")||input.equals("K")||input.equals("M")||input.equals("S")||input.equals("W")||input.equals("B")||input.equals("D")||input.equals("H")||input.equals("V")||input.equals("N"))
            input = input + "//";
        for(;input.length() < 10;){
            input = input+" ";
        }
        return input;
    }
    
    public static void countNumberOfSample(Scanner input){
        int totalLength = 0;
        StringTokenizer count;
        for(int i = 0; i < (countForSampleLength - 2); i++){
            input.nextLine();
        }
        String infoLine = input.nextLine();
        count = new StringTokenizer(infoLine, "	");
        while(count.hasMoreTokens()){
            totalLength ++;
            count.nextToken();
        }
        numberOfSample = (totalLength - 9);
        //System.out.println("Count for sample length:" + countForSampleLength);
        //System.out.println(numberOfSample);
    }
    
    public static void countSampleLength(Scanner input){
        String nextLine;
        for(countForSampleLength = 0; input.hasNextLine()==true; countForSampleLength++){
            nextLine = input.nextLine();
            if(nextLine.length()>= 45&&(nextLine.substring(0,45).equalsIgnoreCase("#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT"))){
                countForSampleLength += 2;
                break;
            }
        }
                
        for(int i = countForSampleLength; input.hasNextLine()==true; i++){
            sampleLength ++;
            input.nextLine();
        }
        
        //System.out.println("Sample length is "+sampleLength);
    }    

}


