/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package FastPhase;

import Main.Genotype;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */
public class FastPHASEgeno extends Genotype{
    static FastPHASEgeno sample = new FastPHASEgeno();
    static StringBuffer snpSeq1[];
    static StringBuffer snpSeq2[];
    static String outFilePath;
    
    public FastPHASEgeno(){}
    
    public FastPHASEgeno(String inputFileName,String outFilePath)throws IOException{
       
        
        inputF = inputFileName;
        //check if input file exists
        inputFile = new File(inputF);
        this.outFilePath=outFilePath;
        mainMethod();
    }
    
    public static void mainMethod()throws IOException{
        outputF = outFilePath+".fastphase.inp";
        
        // Check if target file exists
        targetFile = new File(outputF);
        sample.checkFile(targetFile);
        target = new FileWriter(outputF);
        
        //get number of snp
        Scanner input;
        input = new Scanner(inputFile);
        sample.countNumberOfSnp(input);
        input.close();
        
        //get NumberOfSample as result
        input = new Scanner(inputFile);
        sample.countNumberOfSample(input);//must occurs after countSampleLength() because some values are needed to be counted.
        input.close();
        
        input = new Scanner(inputFile);
        sample.getSnpTypeAndChromAndId(input);
        input.close();
        
        sample.writeToTarget();
        
        //close output stream
        target.close();
    
    }
    
    @Override
    public void writeToTarget()throws IOException{
        //System.out.println("aaa"+ numberOfSample);
        target.write(String.valueOf(numberOfSample));
        target.write("\n");
        target.write(String.valueOf(numberOfSnp));
        target.write("\n");
        for(int i = 0; i < numberOfSample; i++){
            target.write("# id ");
            target.write(String.valueOf(i+1));
            target.write("\n");
            
            target.write(snpSeq1[i].toString());
            target.write("\n");
            target.write(snpSeq2[i].toString());
            target.write("\n");
        }
    }
    
    @Override
    public void getSnpTypeAndChromAndId(Scanner input)throws IOException{
        input.useDelimiter("	");
        StringBuffer snpType1[] = new StringBuffer[numberOfSample]; 
        StringBuffer snpType2[] = new StringBuffer[numberOfSample];
        String nextSnpInfo;
        String snpInfo;
        StringTokenizer snp;
        String nextLine;
        
        //initialize snpType
        for(int i = 0; i< numberOfSample; i++){
            snpType1[i] = new StringBuffer();
            snpType2[i] = new StringBuffer();
        }
        
        //get chrom, id, ref, alt, snpType
        for (long i = 0; i < numberOfSnp; i++){
            nextLine = input.nextLine();
            if(checkSnp(nextLine) == false){
                i --;
                continue;
            }
            
            snp = new StringTokenizer(nextLine, "	");
            
            for(int j = 0; j < 3; j++)
                snp.nextToken();
            snpInfo = snp.nextToken();
            snp = new StringTokenizer(snpInfo, " ");
            for(int j = 0; j < numberOfSample; j++){
                nextSnpInfo = snp.nextToken();
                getSnpSeqs(nextSnpInfo, snpType1[j], snpType2[j]);
            }
        }
        snpSeq1 = snpType1;
        snpSeq2 = snpType2;
        //System.out.println("sngType"+snpType[0]);
        //System.out.println("finish");
        
    }
    //other situations
    public static void getSnpSeqs(String snpInfo, StringBuffer s1, StringBuffer s2){
        if(snpInfo.equals("A")) {
            s1.append("A");
            s2.append("A");
        }else if(snpInfo.equals("T")){
            s1.append("T");
            s2.append("T");
        }else if(snpInfo.equals("C")){
            s1.append("C");
            s2.append("C");
        }else if(snpInfo.equals("G")){
            s1.append("G");
            s2.append("G");
        }else if(snpInfo.equals("R")){
                s1.append("A");
                s2.append("G");
        }else if(snpInfo.equals("Y")){
            s1.append("C");
            s2.append("T");
        }else if(snpInfo.equals("K")){
            s1.append("G");
            s2.append("T");
        }else if(snpInfo.equals("M")){
            s1.append("A");
            s2.append("C");
        }else if(snpInfo.equals("S")){
            s1.append("G");
            s2.append("C");
        }else if(snpInfo.equals("W")){
            s1.append("A");
            s2.append("T");
        }else{
            s1.append("?");
            s2.append("?");
        }
    
    }
}
