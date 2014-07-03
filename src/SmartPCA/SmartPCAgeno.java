/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SmartPCA;

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

//? seen as 0
public class SmartPCAgeno extends Genotype{
    static SmartPCAgeno sample = new SmartPCAgeno();
    static String outputSnp;
    static String outputInd;
    static String outputGeno;
    static FileWriter genoFile;
    static FileWriter snpFile;
    static FileWriter indFile;
    static File subpopulationFile;
    static String[] subpopInfo;
    
    public static void main(String[] args)throws IOException{
        initialize();
        mainMethod();
    }
    
    public SmartPCAgeno(){}
    
    public SmartPCAgeno(String inputFileName,String subpopulation,String outputSnpName,String outputGenoName,String outputIndName)throws IOException{
        
        inputF = inputFileName;
        inputFile = new File(inputF);
       
        
        subpopulationFile = new File(subpopulation);
       
        outputSnp = outputSnpName;
        targetFile = new File(outputSnp);
  
        outputGeno = outputGenoName;
        targetFile = new File(outputGeno);
        
        outputInd = outputIndName;
        targetFile = new File(outputInd);
        

        //set FileWriters for output files.
        genoFile = new FileWriter(outputGeno);
        snpFile = new FileWriter(outputSnp);
        indFile = new FileWriter(outputInd);
        mainMethod();
    }
    
    public static void mainMethod()throws IOException{
        Scanner scan = new Scanner(subpopulationFile);
        Scanner input = new Scanner(inputFile);
        
        //get sampleLength as result
        sample.countNumberOfSnp(input);
        
        //get NumberOfSample as result
        input = new Scanner(inputFile);
        sample.countNumberOfSample(input);//must occurs after countSampleLength() because some values are needed to be counted.
        
        subpopInfo = new String[numberOfSample];
        for(int i = 0; i < numberOfSample; i++){
            subpopInfo[i] = scan.nextLine();
        }
        scan.close();
        
        //get chrom, id, snp sequence for each snp saved.
        input  = new Scanner(inputFile);
        sample.getSnpTypeAndChromAndId(input);
        
        writeToIndFile();
        writeToSnpFile();
        writeToGenoFile();
        
        // Close streams
        input.close();
        sample.closeOutputStream();
        
        //System.out.println("Done!");
    }
    
    @Override
    public void closeOutputStream()throws IOException{
        indFile.close();
        snpFile.close();
        genoFile.close();
    }
    
    public static void initialize()throws IOException{
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
        
        System.out.print("Please input output genotype file name: ");
        outputGeno = scanner.nextLine();
        targetFile = new File(outputGeno);
       
        
        System.out.print("Please input output individual file name: ");
        outputInd = scanner.nextLine();
        targetFile = new File(outputInd);
        
        
        //set FileWriters for output files.
        genoFile = new FileWriter(outputGeno);
        snpFile = new FileWriter(outputSnp);
        indFile = new FileWriter(outputInd);
        
        scanner.close();
    }
    
    public static void writeToIndFile()throws IOException{
        for(int i = 0; i< numberOfSample; i++){
            indFile.write(String.valueOf(i+1));
            indFile.write("\t");
            indFile.write("U");
            indFile.write("\t");
            indFile.write(subpopInfo[i]);
            indFile.write("\n");
        }
    }
    
    public static void writeToSnpFile()throws IOException{
        StringTokenizer nextPos = new StringTokenizer(posSeq.toString(), "\t");
        StringTokenizer nextChrom = new StringTokenizer(chromSeq.toString(), "\t");
        StringTokenizer nextRef = new StringTokenizer(refSeq.toString(), "\t");
        
        for(int i = 0; i< numberOfSnp; i++){
           snpFile.write("rs"+String.valueOf(i+1));//print id
           snpFile.write("\t");
           snpFile.write(nextChrom.nextToken());//print chromosome number
           snpFile.write("\t");
           snpFile.write("0.0");//if no genetic position
           snpFile.write("\t");
           snpFile.write(nextPos.nextToken());//print physical position
           snpFile.write("\t");
           snpFile.write(nextRef.nextToken());//print the reference allele
           snpFile.write('\n');
        }
    }
    
    //only print the genotype
    public static void writeToGenoFile()throws IOException{
         StringTokenizer[] nextSnp = new StringTokenizer[numberOfSample];
         for(int i = 0; i < numberOfSample; i++)
             nextSnp[i] = new StringTokenizer(snpSeq[i].toString(), "\t");
         for(int i = 0; i < numberOfSnp; i++){
             //System.out.println("snp: "+ i);
             for(int j = 0; j < numberOfSample; j++){
                 //System.out.println("number sample "+ j);
                 //System.out.println(snpSeq[j]);
                genoFile.write(nextSnp[j].nextToken());
            }
            genoFile.write("\n");
        }
    }
    
    @Override
    public void getSnpTypeAndChromAndId(Scanner input)throws IOException{
        input.useDelimiter("	");
        String nextSnpInfo;
        String snpInfo;
        StringTokenizer snp;
        StringBuffer snpType[] = new StringBuffer[numberOfSample];
        StringBuffer snpTypeSet[] = new StringBuffer[numberOfSample];
        
        for(int i = 0; i< numberOfSample; i++){
            snpType[i] = new StringBuffer();
            snpTypeSet[i] = new StringBuffer();
        }
        //get chrom, id, ref, alt, snpType
        for (long i = 0; i < numberOfSnp; i++){
            snp = new StringTokenizer(input.nextLine(), "	");
            
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
        
        for(int j = 0; j < numberOfSample; j++){
            sample.checkSnpType(snpTypeSet, snpType[j], j);
        }
        //System.out.println(snpTypeSet);
        snpSeq = snpTypeSet;
        //System.out.println("sngType"+snpType[0]);
        //System.out.println("finish");
    }
    
    public void checkSnpType(StringBuffer[] snpType, StringBuffer snp, int sampleNo){
        StringTokenizer nextSnp = new StringTokenizer(snp.toString(),"\t");
        StringTokenizer nextRef = new StringTokenizer(refSeq.toString(),"\t");
        String nextS;
        String nextR;
        for(int i = 0; i < numberOfSnp; i++){
            nextS = nextSnp.nextToken();
            nextR = nextRef.nextToken();
            if(nextS.equals(nextR)){
                snpType[sampleNo].append("2");
            }else if(nextS.equals("A")){
                if(nextR.equals("M")||nextR.equals("W")||nextR.equals("R")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("T")){
                if(nextR.equals("Y")||nextR.equals("K")||nextR.equals("W")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("C")){
                if(nextR.equals("Y")||nextR.equals("M")||nextR.equals("S")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("G")){
                if(nextR.equals("R")||nextR.equals("K")||nextR.equals("S")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("W")){
                if(nextR.equals("A")||nextR.equals("T")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("M")){
                if(nextR.equals("A")||nextR.equals("C")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("R")){
                if(nextR.equals("A")||nextR.equals("G")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("Y")){
                if(nextR.equals("C")||nextR.equals("T")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("S")){
                if(nextR.equals("C")||nextR.equals("G")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");      
            }else if(nextS.equals("K")){
                if(nextR.equals("T")||nextR.equals("G")){
                    snpType[sampleNo].append("1");
                }else
                    snpType[sampleNo].append("0");
            }else{
                snpType[sampleNo].append("9");
            }
            
            snpType[sampleNo].append("\t");
                   
        }
    }
}


