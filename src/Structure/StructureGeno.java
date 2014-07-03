/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;

import Main.Genotype;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */

//subpopulation file has one pop name per line
//no extra lines after all
//pop name must be numbers

public class StructureGeno extends Genotype{
    static StructureGeno sample= new StructureGeno();
    static StringBuffer snpSeq1[];
    static StringBuffer snpSeq2[];
    static String[] chrom;
    static FileWriter mainParams;
    static File para;
    static String subpopulationF;
    static FileReader fileReader;
    static BufferedReader reader;
    static String sub[];
    static String infilePath;
    static String outFilePath;
    static String mainparamsPath;
    
    public static void main(String[] args)throws IOException{
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Please enter input file name: ");
        inputF = scanner.nextLine();
        //check if input file exists
        inputFile = new File(inputF);
        while(inputFile.exists()!= true){
            System.out.print("Input file not exists. Re-enter file name: ");
            inputF = scanner.nextLine();
            inputFile = new File(inputF);
        }
        
        System.out.print("Please enter subpopulation file name: ");
        subpopulationF = scanner.nextLine();
        //check if input file exists
        File subpopulationFile = new File(subpopulationF);
        while(subpopulationFile.exists()!= true){
            System.out.print("Subpopulation file not exists. Re-enter file name: ");
            subpopulationF = scanner.nextLine();
            subpopulationFile = new File(subpopulationF);
        }
        
        //get NumberOfSample as result
        Scanner input = new Scanner(inputFile);
        sample.countNumberOfSample(input);
        System.out.println(numberOfSample);
        
        fileReader = new FileReader(subpopulationFile);
        reader = new BufferedReader(fileReader);
        
        sub = new String[numberOfSample];
        
        String line = reader.readLine();
        
        //System.out.println(numberOfSample);
        for(int i = 0; line!=null; i++){
            sub[i] = new String();
            //System.out.println(split.nextToken());
            sub[i] = line;
            //System.out.println(nextS[i]);
            //System.out.println(i);
            line = reader.readLine();
        }
        reader.close();
        fileReader.close();
        
        mainMethod();
    }
    
    public StructureGeno(){}
    
    public StructureGeno(String inputFileName,String infilePath,String outFilePath,String mainparamsPath,String subpopulationName)throws IOException{
        Scanner scanner = new Scanner(System.in);
        
        inputF = inputFileName;
        //check if input file exists
        inputFile = new File(inputF);
        while(inputFile.exists()!= true){
            System.out.print("File not exists. Re-enter file name: ");
            inputF = scanner.nextLine();
            inputFile = new File(inputF);
        }
        
        subpopulationF = subpopulationName;
        this.infilePath=infilePath;
        this.outFilePath=outFilePath;
        this.mainparamsPath=mainparamsPath;
        //check if input file exists
        File subpopulationFile = new File(subpopulationF);
        while(subpopulationFile.exists()!= true){
            System.out.print("Subpopulation file not exists. Re-enter file name: ");
            subpopulationF = scanner.nextLine();
            subpopulationFile = new File(subpopulationF);
        }
        
        //get NumberOfSample as result
        Scanner input = new Scanner(inputFile);
        sample.countNumberOfSample(input);
        //System.out.println(numberOfSample);
        
        fileReader = new FileReader(subpopulationFile);
        reader = new BufferedReader(fileReader);
        
        sub = new String[numberOfSample];
        
        String line = reader.readLine();
        
        //System.out.println(numberOfSample);
        for(int i = 0; line!=null; i++){
            sub[i] = new String();
            //System.out.println(split.nextToken());
            sub[i] = line;
            //System.out.println(nextS[i]);
            //System.out.println(i);
            line = reader.readLine();
        }
        reader.close();
        fileReader.close();
        
        
        mainMethod();
    }
    
    public static void mainMethod()throws IOException{
        outputF = infilePath+".infile";
        
        // Check if target file exists
        targetFile = new File(outputF);
        sample.checkFile(targetFile);
        target = new FileWriter(outputF);
        
        para = new File(mainparamsPath+"/mainparams");
        sample.checkFile(para);
        mainParams = new FileWriter(para);
        
        //get number of snp
        Scanner input;
        input = new Scanner(inputFile);
        sample.countNumberOfSnp(input);
        chrom = new String[numberOfSnp];
        
        input = new Scanner(inputFile);
        sample.getSnpTypeAndChromAndId(input);
        
        //close input stream
        input.close();
        
        sample.writeToTarget();
        sample.writeToMainParams();
        
        //close output stream
        target.close();
        mainParams.close();
    
    }
    
    public void writeToMainParams()throws IOException{
        mainParams.write("Basic Program Parameters");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("#define MAXPOPS    1");
        mainParams.write("\n");
        mainParams.write("#define BURNIN   10000");
        mainParams.write("\n");
        mainParams.write("#define NUMREPS   20000");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("Input/Output files");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("#define INFILE    "+infilePath+".infile");
        mainParams.write("\n");
        mainParams.write("#define OUTFILE   "+outFilePath+".outfile");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("Data file format");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("#define NUMINDS   ");
        mainParams.write(String.valueOf(numberOfSample));
        mainParams.write("\n");
        mainParams.write("#define NUMLOCI   ");
        mainParams.write(String.valueOf(numberOfSnp));
        mainParams.write("\n");
        mainParams.write("#define PLOIDY    2");
        mainParams.write("\n");
        mainParams.write("#define MISSING   -9");
        mainParams.write("\n");
        mainParams.write("#define ONEROWPERIND   0");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("#define LABEL 1");
        mainParams.write("\n");
        mainParams.write("#define POPDATA   1");
        mainParams.write("\n");
        mainParams.write("#define POPFLAG   0");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("#define LOCDATA   0");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("#define PHENOTYPE   0");
        mainParams.write("\n");
        mainParams.write("#define EXTRACOLS   0");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("#define MARKERNAMES   0");
        mainParams.write("\n");
        mainParams.write("#define RECESSIVEALLELES   0");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("#define MAPDISTANCES   0");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("Advanced data file options");
        mainParams.write("\n");
        mainParams.write("\n");
        mainParams.write("define PHASED 0");
        mainParams.write("\n");
        mainParams.write("define PHASEINFO 0");
        mainParams.write("\n");
        mainParams.write("define MARKOVPHASE 0");
        mainParams.write("\n");
        mainParams.write("define NOTAMBIGUOUS -999");
        mainParams.write("\n");
        
        
    }
    
    @Override
    public void writeToTarget()throws IOException{
      
        for(int i = 0; i < numberOfSample; i++){
            //System.out.println(i);
            target.write(String.valueOf(i+1));
            target.write(" ");
            
            target.write(sub[i]);
            target.write(" ");
            
            target.write(snpSeq1[i].toString());
            target.write("\n");
            
            target.write(String.valueOf(i+1));
            target.write(" ");
            
            target.write(sub[i]);
            target.write(" ");
            
            target.write(snpSeq2[i].toString());
            target.write("\n");
        }
    }
    
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
        for (int i = 0; i < numberOfSnp; i++){
            nextLine = input.nextLine();
            if(checkSnp(nextLine) == false){
                i --;
                continue;
            }
            
            snp = new StringTokenizer(nextLine, "	");
            
            snp.nextToken();
            snp.nextToken();
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
            s1.append("1");
            s1.append(" ");
            s2.append("1");
            s2.append(" ");
        }else if(snpInfo.equals("T")){
            s1.append("2");
            s1.append(" ");
            s2.append("2");
            s2.append(" ");
        }else if(snpInfo.equals("C")){
            s1.append("3");
            s1.append(" ");
            s2.append("3");
            s2.append(" ");
        }else if(snpInfo.equals("G")){
            s1.append("4");
            s1.append(" ");
            s2.append("4");
            s2.append(" ");
        }else if(snpInfo.equals("R")){
            s1.append("1");
            s1.append(" ");
            s2.append("4");
            s2.append(" ");
        }else if(snpInfo.equals("Y")){
            s1.append("3");
            s1.append(" ");
            s2.append("2");
            s2.append(" ");
        }else if(snpInfo.equals("K")){
            s1.append("4");
            s1.append(" ");
            s2.append("2");
            s2.append(" ");
        }else if(snpInfo.equals("M")){
            s1.append("1");
            s1.append(" ");
            s2.append("3");
            s2.append(" ");
        }else if(snpInfo.equals("S")){
            s1.append("4");
            s1.append(" ");
            s2.append("3");
            s2.append(" ");
        }else if(snpInfo.equals("W")){
            s1.append("1");
            s1.append(" ");
            s2.append("2");
            s2.append(" ");
        }else{
            s1.append("-9");
            s1.append(" ");
            s2.append("-9");
            s2.append(" ");
        }
    
    }
            
    
}
