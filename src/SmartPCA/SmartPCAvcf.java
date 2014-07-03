/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartPCA;

import Main.VcfOriginal;
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
public class SmartPCAvcf extends VcfOriginal{
    
    static SmartPCAvcf vcfPCA = new SmartPCAvcf();
    
    //supposedelimeter set as tab
    //suppose output file set.
    //suppose do not have genetic position
    //if wrong input when asking for cover the existing file or not, system will automatically cover the files.
    public static void main(String[] args) throws IOException, NoSuchElementException{
        vcfPCA.initialize();
        
        mainMethod();
    }
    
    public SmartPCAvcf(){}
    
    public SmartPCAvcf(String inputFileName, String outputSnpName,String outputGenoName,String outputIndName)throws IOException{
        
        Scanner scanner = new Scanner(System.in);
        
        inputF = inputFileName;
        inputFile = new File(inputF);
        while(inputFile.exists()!= true){
            System.out.print("File not exists. Re-enter file name: ");
            inputF = scanner.nextLine();
            inputFile = new File(inputF);
        }
        
        outputSnp = outputSnpName;
        targetFile = new File(outputSnp);
        vcfPCA.checkFile(targetFile, outputSnp);
        
        outputGeno = outputGenoName;
        targetFile = new File(outputGeno);
        vcfPCA.checkFile(targetFile, outputGeno);
        
        outputInd = outputIndName;
        targetFile = new File(outputInd);
        vcfPCA.checkFile(targetFile, outputInd);
        
        //set FileWriters for output files.
        genoFile = new FileWriter(outputGeno);
        snpFile = new FileWriter(outputSnp);
        indFile = new FileWriter(outputInd);
        
        mainMethod();
        scanner.close();
    }
    
    public static void mainMethod()throws IOException{
        Scanner input = new Scanner(inputFile);
        //get sampleLength as result
        vcfPCA.countNumberOfSnp(input);
        
        //get NumberOfSample as result
        input = new Scanner(inputFile);
        vcfPCA.countNumberOfSample(input);//must occurs after countSampleLength() because some values are needed to be counted.
        
        //get chrom, id, snp sequence for each snp saved.
        input  = new Scanner(inputFile);
        vcfPCA.getSnptypeAndChromAndId(input);
        
        vcfPCA.output();
        
        // Close streams
        input.close();
        vcfPCA.closeOutputStream();
        
        //System.out.println("Done!");
    }
    
    @Override
    public void closeOutputStream()throws IOException{
        genoFile.close();
        indFile.close();
        snpFile.close();
    }
    
    @Override
    public void output()throws IOException{
        writeToGenoFile();
        writeToIndFile();
        writeToSnpFile();
        
    }
    
    @Override
    public void initialize()throws IOException{
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
        System.out.println("1");
        targetFile = new File(outputSnp);
        vcfPCA.checkFile(targetFile, outputSnp);
        System.out.println(outputSnp);
        
        System.out.print("Please input output genotype file name: ");
        outputGeno = scanner.nextLine();
        targetFile = new File(outputGeno);
        vcfPCA.checkFile(targetFile, outputGeno);
        
        System.out.print("Please input output individual file name: ");
        outputInd = scanner.nextLine();
        targetFile = new File(outputInd);
        vcfPCA.checkFile(targetFile, outputInd);
        
        //set FileWriters for output files.
        genoFile = new FileWriter(outputGeno);
        snpFile = new FileWriter(outputSnp);
        indFile = new FileWriter(outputInd);
        
        scanner.close();
    }
        
    @Override
    public void writeToIndFile()throws IOException{
        for(long i = 0; i< numberOfSample; i++){
            indFile.write(String.valueOf(i+1));
            indFile.write("\t");
            indFile.write("U");
            indFile.write("\t");
            indFile.write("Case");
            indFile.write("\n");
        }
    }
    
    @Override
    public void writeToSnpFile()throws IOException{
        StringTokenizer nextId = new StringTokenizer(idSeq.toString(), "\t");
        StringTokenizer nextChrom = new StringTokenizer(chromSeq.toString(), "\n");
        StringTokenizer nextPos = new StringTokenizer(pos.toString(), "\t");
        StringTokenizer nextRef = new StringTokenizer(refSeq.toString(), "\t");
        
        for(int i = 0; i< numberOfSnp; i++){
           snpFile.write(nextId.nextToken());//print id
           snpFile.write("\t");
           snpFile.write(nextChrom.nextToken());//print chromosome number
           snpFile.write("\t");
           snpFile.write("0.0");//if no genetic position
           snpFile.write("\t");
           snpFile.write(nextPos.nextToken());//print physical position
           snpFile.write("\t");
           snpFile.write(nextRef.nextToken());//print the reference allele
           snpFile.write("\t");
           snpFile.write(alt);//print the variant allele
           snpFile.write('\n');
        }
    }
    
    //only print the genotype
    @Override
    public void writeToGenoFile()throws IOException{
        
         for(int i = 0; i < numberOfSnp; i++){
            for(int j = 0; j < numberOfSample; j++){
                genoFile.write((snpSeq[j].substring(i, i+1)).toString());
            }
            genoFile.write("\n");
        }
    }
    
    @Override
    public void checkFile(File inputFile, String fileName){
        if (inputFile.exists()) {
            System.out.println("Target file " + inputFile.getName() + " already exists");
            System.out.print("Choose to cover the existing file type 'C', re-enter please type 'E': ");
            Scanner scan;
            String nextChar;
            
            while(true){
                scan = new Scanner(System.in);
                nextChar = scan.nextLine();
                //System.out.println(scan.next());
                if(nextChar.equalsIgnoreCase("C")){
                    break;
                }else if(nextChar.equalsIgnoreCase("E")){
                    System.out.print("Please input the new file name: ");//can not be shown
                    if(inputFile.getName().equals(outputSnp))
                        outputSnp = scan.next();
                    else if(inputFile.getName().equals(outputGeno))
                        outputGeno = scan.next();
                    else if(inputFile.getName().equals(outputInd))
                        outputInd = scan.next();
                    break;
                }else{
                    break;
                }
            }
        }  
    }
    
    @Override
    public void getSnptypeAndChromAndId(Scanner input)throws IOException{
        input.useDelimiter("	");
        System.out.println(numberOfSample);
        StringBuffer snpType[] = new StringBuffer[numberOfSample]; 
        StringBuilder id = new StringBuilder(); 
        StringBuilder chrom = new StringBuilder();
        StringBuilder position = new StringBuilder();
        StringBuilder refs = new StringBuilder();
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
            refs.append(ref);
            refs.append("\t");
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
        refSeq = refs;
        pos = position;
        chromSeq = chrom;
        //System.out.println("sngType"+snpType[0]);
        //System.out.println("finish");
        
    }
    
    @Override
    public void getSnpType(String snpInfo, StringBuffer snpType[], int j){
        if(snpInfo.substring(0, 1).equals("0") && snpInfo.substring(2, 3).equals("0")){
                    snpType[j].append("2");
                    //System.out.println(sngType[j]);
                }else if((snpInfo.substring(0, 1).equals("0") && snpInfo.substring(2, 3).equals("1"))||(snpInfo.substring(0, 1).equals("1") && snpInfo.substring(2, 3).equals("0"))){
                    
                    snpType[j].append("1");
                    
                    //System.out.println(sngType[j]);
                }else if(snpInfo.substring(0, 1).equals("1") && snpInfo.substring(2, 3).equals("1")){
                    snpType[j].append("0");
                    //System.out.println(sngType[j]);
                }else if(snpInfo.substring(0, 1).equals(".")||snpInfo.substring(2, 3).equals(".")){
                    snpType[j].append("9");
                }else{
                    snpType[j].append("?");
    
                }
    }
}



