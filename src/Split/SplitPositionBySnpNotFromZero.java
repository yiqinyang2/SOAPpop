/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Split;


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

//seperate window by giving distance per window
//first snp distance is distance of the first snp
public class SplitPositionBySnpNotFromZero extends GenotypeOriginal{

    
    static int distancePerFile = 0;
    static SplitPositionBySnpNotFromZero sample = new SplitPositionBySnpNotFromZero();
    static double numberOfOutputFile;
    static FileWriter recordFile;
    static String outputHead;
    static String outFilePath;

    public SplitPositionBySnpNotFromZero(){}
    
    public SplitPositionBySnpNotFromZero(String inputFileName, int distance, String head,String outFilePath,String windowInformationPath)throws IOException{
       
    	outputHead = head;
        this.outFilePath=outFilePath;

        output = new File(windowInformationPath+"/RecordFile.txt");
        checkFile(output);
        recordFile = new FileWriter(output);
        
        inputF = inputFileName;
        inputFile = new File(inputF);
       
        
        //get number of sample per file;
        distancePerFile = distance;
        
        mainMethod();
      
    }
    
    public static void mainMethod()throws IOException{
        Scanner input;
        
        //count total sample length
        input = new Scanner(inputFile);
        sample.countNumberOfSnp(input);
        
        //get NumberOfSample as result
        input = new Scanner(inputFile);
        sample.countNumberOfSample(input);//must occurs after countSampleLength() because some values are needed to be counted.
        
        //print snp and snp names
        input = new Scanner(inputFile);
        sample.getSnpTypeAndChromAndId(input);
        
        //close input stream
        input.close();
    }
   
    @Override
    public void getSnpTypeAndChromAndId(Scanner input)throws IOException{
        FileWriter outputFile;
        int firstDistance;
        //System.out.println(numberOfOutputFile);
        String nextLine = input.nextLine();
        StringTokenizer nextSnp = new StringTokenizer(nextLine, "	");
        int number = 1;
        String next;
        String chrom;
        chrom = nextSnp.nextToken();
        output = new File(outFilePath+"/"+outputHead + "_"+number+".genotype");
        outputFile = new FileWriter(output);
        firstDistance = Integer.parseInt(nextSnp.nextToken());
        nextSnp = new StringTokenizer(nextLine, "	");
        //System.out.println("First distance is "+firstDistance);
        
        while(nextLine!=null){
            chrom = nextSnp.nextToken();
            next = nextSnp.nextToken();
            
            while(true){
                //System.out.println("next is "+ next);
                if(Integer.parseInt(next) < firstDistance + number*distancePerFile ){
                    if(checkSnp(nextLine)){
                        outputFile.append(nextLine);
                        outputFile.append("\n");
                    }
                    //System.out.println("next is "+ next);
                    break;
                }else{
                    outputFile.close();
                    
                    if(output.length() == 0)
                        output.delete();
                    else{
                        recordFile.write(output.getName() + "\t");
                        recordFile.write(chrom + "\t");
                        recordFile.write(String.valueOf(firstDistance + + (number-1)*distancePerFile) + "\t");
                        recordFile.write(String.valueOf(firstDistance + number * distancePerFile - 1) + "\n");
                    }
                    
                    number ++;
                    output = new File(outFilePath+"/"+outputHead + "_"+number+".genotype");
                    outputFile = new FileWriter(output);
                }
            }
            if(input.hasNextLine()){
                nextLine = input.nextLine();
                nextSnp = new StringTokenizer(nextLine, "	");
            }else{
                nextLine = null;
            }
        }
        outputFile.close();
        if(output.length() == 0)
            output.delete();
        else{
            recordFile.write(output.getName() + "\t");
            recordFile.write(chrom + "\t");
            recordFile.write(String.valueOf(firstDistance + + (number-1)*distancePerFile) + "\t");
            recordFile.write(String.valueOf(firstDistance + number * distancePerFile - 1) + "\n");
        }
        recordFile.close();
        
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

