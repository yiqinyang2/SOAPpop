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
public class SplitSlidingNotFromZero extends GenotypeOriginal{

    
    static int distancePerFile = 0;
    static SplitSlidingNotFromZero sample = new SplitSlidingNotFromZero();
    static double numberOfOutputFile;
    static String outputHead;
    static String outFilePath;
    static int slideDistance;
    static StringBuilder overlap = new StringBuilder();

    
    public SplitSlidingNotFromZero(){}
    
    public SplitSlidingNotFromZero(String inputFileName,String outFilePath, int distance, String head, int distanceOfSlide)throws IOException{
        
        inputF = inputFileName;
        this.outFilePath=outFilePath;
        inputFile = new File(inputF);
   
        //get number of sample per file
        distancePerFile = distance;
        
        slideDistance = distanceOfSlide;
        
        outputHead = head;
        
        mainMethod();
        //close input stream
//        inputStream.close();
    
    }
    
    public static void mainMethod()throws IOException{
        Scanner input;
        
        //count total sample length
        input = new Scanner(inputFile);
        sample.countNumberOfSnp(input);
        
        //print snp and snp names
        sample.getSnpTypeAndChromAndId(inputFile);
        input.close();
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
    
    public void getSnpTypeAndChromAndId(File inputFile)throws IOException{
        Scanner input = new Scanner(inputFile);
        FileWriter outputFile;
        int firstDistance;
        String nextLine = input.nextLine();
        StringTokenizer nextSnp = new StringTokenizer(nextLine, "	");
        int number = 1;
        String next = "";
        String chrom;
        String nextChrom = "";
        
        output = new File(outFilePath+"/"+outputHead + "_"+number+".genotype");
        sample.checkFile(output);
        outputFile = new FileWriter(output);
        
        chrom = nextSnp.nextToken();
        firstDistance = Integer.parseInt(nextSnp.nextToken());
        
        nextSnp = new StringTokenizer(nextLine, "	");
        //System.out.println("First distance is "+firstDistance);
        
        for (int i = 0; nextLine!=null; i++){
            if(i ==0){
                nextChrom = chrom;
                next = String.valueOf(firstDistance);
            }
            
          
            while(true){
                //System.out.println("next is "+ next);
                //System.out.println("1 "+(firstDistance + distancePerFile));
                
                
                if(Integer.parseInt(next) < firstDistance + distancePerFile &&nextChrom.equals(chrom)){
                	if(checkSnp(nextLine))
                    {
                    	outputFile.append(nextLine);
                        outputFile.append("\n");
                    }
                	
                	if( Integer.parseInt(next) >= firstDistance + distancePerFile - slideDistance)
                        overlap.append(nextLine+"\n");

                    if(input.hasNextLine()){
                        nextLine = input.nextLine();
                        nextSnp = new StringTokenizer(nextLine, "	");
                        nextChrom = nextSnp.nextToken();
                        next = nextSnp.nextToken();
                    }else{
                        outputFile.close();
                        nextLine = null;
                    }
                    //System.out.println("next is "+ next);
                    break;
                }else if(Integer.parseInt(next) >= firstDistance + distancePerFile){
                    outputFile.close();
                    if(output.length() == 0)
                        output.delete();
                    
                    number ++;
                    output = new File(outFilePath+"/"+outputHead + "_"+number+".genotype");
                    sample.checkFile(output);
                    
                    outputFile = new FileWriter(output);
                    input.close();
                    input = new Scanner(inputFile);
                    nextLine = input.nextLine();
                    nextSnp = new StringTokenizer(nextLine, "	");
                    nextChrom = nextSnp.nextToken();
                    next = nextSnp.nextToken();
                    
                    outputFile.append(overlap);
                    overlap = new StringBuilder();

                    while(true){
                        
                    	if(Integer.parseInt(next) >= firstDistance + distancePerFile - slideDistance ){
                            firstDistance = firstDistance + distancePerFile - slideDistance;
                            break;
                        }
                        nextLine = input.nextLine();
                        nextSnp = new StringTokenizer(nextLine, "	");
                        nextChrom = nextSnp.nextToken();
                        next = nextSnp.nextToken();
                    
                    }
                    
                    break;
                }else{
                    outputFile.close();
                    if(output.length() == 0)
                        output.delete();
                    number++;
                    chrom = nextChrom;
                    firstDistance = Integer.parseInt(next);
                    
                    output = new File(outFilePath+"/"+outputHead + "_"+number+".genotype");
                    sample.checkFile(output);
                    outputFile = new FileWriter(output);
                }
            }
            
        }
        input.close();
        outputFile.close();
        //System.out.println("numberOfFile is "+ number);
        
    }
}


