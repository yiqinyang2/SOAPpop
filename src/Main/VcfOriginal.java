/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */

//check if all are snps
public class VcfOriginal {
    protected static int numberOfSample = 0;
    protected static long numberOfSnp = 0;
    protected static int countForSampleLength = 0;
    protected static String inputF;//input file
    protected static String outputGeno;//output file
    protected static String outputSnp;//output file
    protected static String outputInd;//output file
    protected static String ref;
    protected static String alt;
    protected static File inputFile;
    protected static File targetFile;
    protected static FileWriter genoFile;
    protected static FileWriter snpFile;
    protected static FileWriter indFile;
    protected static StringBuffer snpSeq[];
    protected static StringBuilder idSeq;
    protected static StringBuilder chromSeq;
    protected static StringBuilder refSeq;
    protected static StringBuilder pos;
    static VcfOriginal vcf = new VcfOriginal();
    
    
    protected void closeOutputStream()throws IOException{
        genoFile.close();
        indFile.close();
        snpFile.close();
    }
    
    protected void output()throws IOException{
        writeToGenoFile();
        writeToIndFile();
        writeToSnpFile();
        
    }
    
    protected void initialize()throws IOException{
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Please input input file name: ");
        inputF = scanner.nextLine();
        inputFile = new File(inputF);
        while(inputFile.exists()!= true){
            System.out.print("File not exists. Re-enter file name: ");
            inputF = scanner.nextLine();
            inputFile = new File(inputF);
        }
        
        
        System.out.print("Please input output snp file name: ");
        outputSnp = scanner.nextLine();
        targetFile = new File(outputSnp);
        checkFile(targetFile, outputSnp);
        System.out.print("Please input output genotype file name: ");
        outputGeno = scanner.nextLine();
        targetFile = new File(outputGeno);
        checkFile(targetFile, outputGeno);
        System.out.print("Please input output individual file name: ");
        outputInd = scanner.nextLine();
        targetFile = new File(outputInd);
        checkFile(targetFile, outputInd);
        
        //set FileWriters for output files.
        genoFile = new FileWriter(outputGeno);
        snpFile = new FileWriter(outputSnp);
        indFile = new FileWriter(outputInd);
        

    }
    protected void checkFile(File inputFile, String fileName){
        if (inputFile.exists()) {
            System.out.println("Target file " + inputFile.getName() + " already exists");
            System.out.print("Choose to cover the existing file type 'C', re-enter please type 'E': ");
            Scanner scan;
            
            while(true){
                scan = new Scanner(System.in);
                if((scan.next()).equalsIgnoreCase("C")){
                    break;
                }else if((scan.next()).equalsIgnoreCase("E")){
                    System.out.print("Please input the new file name: ");//can not be shown
                    fileName = scan.next();
                    break;
                }else{
                    break;
                }
            }
        }  
    }
    
    protected void writeToIndFile()throws IOException{
        for(long i = 0; i< numberOfSample; i++){
            indFile.write(String.valueOf(i));
            indFile.write("\t");
            indFile.write("U");
            indFile.write("\t");
            indFile.write("Case");
            indFile.write("\n");
        }
    }
    
    protected void writeToSnpFile()throws IOException{
        StringTokenizer nextId = new StringTokenizer(idSeq.toString(), "\t");
        StringTokenizer nextChrom = new StringTokenizer(chromSeq.toString(), "\n");
        StringTokenizer nextPos = new StringTokenizer(pos.toString(), "\t");
        
        for(int i = 0; i< numberOfSnp; i++){
           snpFile.write(nextId.nextToken());//print id
           snpFile.write("\t");
           snpFile.write(nextChrom.nextToken());//print chromosome number
           snpFile.write("\t");
           snpFile.write("0.0");//if no genetic position
           snpFile.write("\t");
           snpFile.write(nextPos.nextToken());//print physical position
           snpFile.write("\t");
           snpFile.write(ref);//print the reference allele
           snpFile.write("\t");
           snpFile.write(alt);//print the variant allele
           snpFile.write('\n');
        }
    }
    
    //only print the genotype
    protected void writeToGenoFile()throws IOException{
        
         for(int i = 0; i < numberOfSnp; i++){
            for(int j = 0; j < numberOfSample; j++){
                genoFile.write((snpSeq[j].substring(i, i+1)).toString());
            }
            genoFile.write("\n");
        }
    }
    
    protected void getSnptypeAndChromAndId(Scanner input)throws IOException, NoSuchElementException, NullPointerException{
        input.useDelimiter("	");
        StringBuffer snpType[] = new StringBuffer[numberOfSample]; 
        StringBuilder id = new StringBuilder(); 
        StringBuilder chrom = new StringBuilder();
        StringBuilder position = new StringBuilder();
        String nextSnpInfo;
        StringTokenizer next;
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
        for (long i = 0; i < numberOfSnp; i++){
            
            if(i == 0){
                chrom.append(input.next());
                chrom.append("\n");
            }
            position.append(input.next());
            position.append("\t");
            
            id.append(input.next());
            id.append("\t");
            ref = input.next();
            ref = ref.toUpperCase();
            //System.out.println("ref:"+ref);
            alt = input.next();
            alt = alt.toUpperCase();
            //System.out.println("alt: "+alt);
            for(int j = 0; j < 4; j++)//change from 4 to 5
                input.next();
            
            for(int j = 0; j < numberOfSample - 1; j++ ){
                
                nextSnpInfo = input.next();
                
                snpInfo = nextSnpInfo.substring(0, 3);
                //System.out.println(j+": sng info:" + sngInfo);
                getSnpType(snpInfo, snpType, j);
                
            }
            
            nextSnpInfo = input.next();
            next = new StringTokenizer(nextSnpInfo, "\n");
            
            getSnpType(next.nextToken(), snpType, numberOfSample-1);
            //System.out.println(next.nextToken());
            if(next.hasMoreTokens()){
                chrom.append(next.nextToken());
                chrom.append("\n");
            }       
        }
        snpSeq = snpType;
        idSeq = id;
        pos = position;
        chromSeq = chrom;
        //System.out.println("sngType"+snpType[0]);
        System.out.println("finish");
        
    }
    
    protected void getSnpType(String snpInfo, StringBuffer snpType[], int j){
        if(snpInfo.substring(0, 1).equals("0") && snpInfo.substring(2, 3).equals("0")){
                    snpType[j].append(2);
                    //System.out.println(sngType[j]);
                }else if((snpInfo.substring(0, 1).equals("0") && snpInfo.substring(2, 3).equals("1"))||(snpInfo.substring(0, 1).equals("1") && snpInfo.substring(2, 3).equals("0"))){
                    
                    snpType[j].append(1);
                    
                    //System.out.println(sngType[j]);
                }else if(snpInfo.substring(0, 1).equals("1") && snpInfo.substring(2, 3).equals("1")){
                    snpType[j].append(0);
                    //System.out.println(sngType[j]);
                }else if(snpInfo.substring(0, 1).equals(".")||snpInfo.substring(2, 3).equals(".")){
                    snpType[j].append("9");
                }else{
                    snpType[j].append("?");
                }
    }
    
    //not used
    protected String checkGenotype(String input){
        //System.out.println(input);
        if(input.equals("A")||input.equals("T")||input.equals("C")||input.equals("G")||input.equals("R")||input.equals("Y")||input.equals("K")||input.equals("M")||input.equals("S")||input.equals("W")||input.equals("B")||input.equals("D")||input.equals("H")||input.equals("V")||input.equals("N"))
            input = input + "//";
        for(;input.length() < 10;){
            input = input+" ";
        }
        return input;
    }
    
    protected void countNumberOfSample(Scanner input){
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
        System.out.println(numberOfSample);
    }
    
    protected void countNumberOfSnp(Scanner input){
        String nextLine;
        for(countForSampleLength = 0; input.hasNextLine()==true; countForSampleLength++){
            nextLine = input.nextLine();
            if(nextLine.length()>= 45&&(nextLine.substring(0,45).equalsIgnoreCase("#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT"))){
                countForSampleLength += 2;
                break;
            }
        }
                
        for(int i = countForSampleLength; input.hasNextLine()==true; i++){
            numberOfSnp ++;
            input.nextLine();
        }
        
        //System.out.println("Number of snp is "+numberOfSnp);
    }    

}



