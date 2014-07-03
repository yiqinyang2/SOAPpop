package Split;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import Main.GenotypeOriginal;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */
public class FastSplitSlidingNotFromZero extends GenotypeOriginal{

    
    static int distancePerFile = 0;
    static FastSplitSlidingNotFromZero sample = new FastSplitSlidingNotFromZero();
    static double numberOfOutputFile;
    static int slideDistance;
    static StringBuilder overlap = new StringBuilder();
    static String[] position;
    static String[] snpInfo;
    static String chrom;
    static boolean isSnp[];
    static FileWriter recordFile;
    static String outputHead;
    static String outFilePath;
    
    
    public FastSplitSlidingNotFromZero(){}
    
    public FastSplitSlidingNotFromZero(String inputFileName,String outFilePath,String windowInformationPath,int distance,String outputHead,int distanceOfSlide)throws IOException{
       
    	this.outFilePath=outFilePath;
        this.outputHead=outputHead;
        
        output = new File(windowInformationPath+"/RecordFile.txt");
        checkFile(output);
        
        
        recordFile = new FileWriter(output);
        
        inputF = inputFileName;
        inputFile = new File(inputF);
       
        
        //get number of sample per file
        distancePerFile = distance;
        
        slideDistance = distanceOfSlide;
        
        mainMethod();
        //close input stream
      
    
    }
    
    public static void mainMethod()throws IOException{
        Scanner input;
        //count total sample length
        input = new Scanner(inputFile);
        sample.countNumberOfSnp(input);
        
        position = new String[numberOfSnp];
        snpInfo = new String[numberOfSnp];
        isSnp = new boolean[numberOfSnp];
        
        //print snp and snp names
        sample.getSnpTypeAndChromAndId(inputFile);
        input.close();
        
        writeToTargets();
    }
     
    public static void writeToTargets()throws IOException{
        File outputFile;
        FileWriter outputWindow;
        int windowNo = 1;
        int firstDistance;
        int currentPos;
        int restartSnpNo = 0;
        boolean startPosSet = false;
        firstDistance = Integer.parseInt(position[0]);
        
        currentPos = firstDistance;
        //System.out.println(currentPos);
        
        outputFile = new File((outFilePath+"/"+outputHead+"_"+windowNo+".genotype"));
        outputWindow = new FileWriter(outputFile);
        
        for(int i = 0; i < numberOfSnp; i++){
            currentPos = Integer.parseInt(position[i]);
            
            if(currentPos < firstDistance + distancePerFile){
                
                if(isSnp[i]){
                    outputWindow.write(snpInfo[i]+"\n");
                }
                
                if(currentPos >= firstDistance + distancePerFile - slideDistance && startPosSet == false){
                    restartSnpNo = i - 1;
                    startPosSet = true;
                    //System.out.println(restartSnpNo);
                }
            }else{
                outputWindow.close();
                if(outputFile.length() == 0)
                    outputFile.delete();
                else{
                    recordFile.write(outputFile.getName() + "\t");
                    recordFile.write(chrom + "\t");
                    recordFile.write(String.valueOf(firstDistance) + "\t");
                    recordFile.write(String.valueOf(firstDistance + distancePerFile - 1) + "\n");
                }
                
                windowNo ++;
                
                firstDistance += distancePerFile - slideDistance;
                
                if(startPosSet)
                    i = restartSnpNo;
                
                startPosSet = false;
            
                outputFile = new File(outFilePath+"/"+outputHead+"_"+windowNo+".genotype");
                outputWindow = new FileWriter(outputFile);
        
            }
            
        }
        outputWindow.close();
        if(outputFile.length() == 0)
            outputFile.delete();
        else{
            recordFile.write(outputFile.getName() + "\t");
            recordFile.write(chrom + "\t");
            recordFile.write(String.valueOf(firstDistance) + "\t");
            recordFile.write(String.valueOf(firstDistance + distancePerFile - 1) + "\n");
        }
        recordFile.close();
    
    }
    
    public void getSnpTypeAndChromAndId(File inputFile)throws IOException{
        String line;
        Scanner file = new Scanner(inputFile);
        StringTokenizer pos;
        
        for(int i = 0; i < numberOfSnp; i++){
            line = file.nextLine();
            pos = new StringTokenizer(line, "	");
            chrom = pos.nextToken();
            position[i] = pos.nextToken();
            snpInfo[i] = line;
    
            if(checkSnp(line)){
                isSnp[i] = true;
            }else
                isSnp[i] = false;
        }
    }
    
    protected boolean checkSnp(String snp){
        boolean isSnp = false;
        String nextsnp;
        StringTokenizer next = new StringTokenizer(snp, "	");
        String first;
        String snps;
        
        for(int i = 0; i < 3; i++)
            nextsnp = next.nextToken();
    
        snps = next.nextToken();
        next = new StringTokenizer(snps);
        
        first = next.nextToken();
        while(first.equals("-")){
            first = next.nextToken();
        }
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
}


