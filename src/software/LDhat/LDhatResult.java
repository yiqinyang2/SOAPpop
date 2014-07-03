package software.LDhat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */
public class LDhatResult extends Genotype{
    static File inputGenotype;
    static File inputLDhat;
    static String outputMatchingFile = "LDhatResult.txt"; 
    static FileWriter matchingFile;
    static Scanner genotype;
    static Scanner LDhat;
    static LDhatResult sample = new LDhatResult();
    
    public LDhatResult(){}
    
    public LDhatResult(String inputGenotypeFileName, String inputLDhatFileName, String outputMatchingFile)throws IOException{
        
        inputGenotype = new File(inputGenotypeFileName);
        inputLDhat = new File(inputLDhatFileName);
        
        genotype = new Scanner(inputGenotype);
        LDhat = new Scanner(inputLDhat);

        targetFile = new File(outputMatchingFile);
        matchingFile = new FileWriter(targetFile);
        
        LDhatResult.mainMethod();
        
        genotype.close();
        LDhat.close();
        matchingFile.close();
    }
    
    public static void mainMethod() throws IOException{
        StringTokenizer chrom;
        StringTokenizer pos;
        String previousPos = "0";
        String currentPos;
        String LDhatCurrentLine;
        StringTokenizer LDhatValue;
        double value;
        NumberFormat df = NumberFormat.getNumberInstance();
        df.setMaximumFractionDigits(2);
        
        if(LDhat.hasNextLine()){
            LDhat.nextLine();
        }
        else{
            matchingFile.write("\"res.txt\" file is empty!");
            return;
        }
        
        matchingFile.write("Pos" + "\t" + "CM" + "\t" + "CM"+ "\\" + "MB" + "\n");
        
        //get sampleLength as result
        sample.countNumberOfSnp(genotype);
        genotype.close();
        
        //get NumberOfSample as result
        genotype = new Scanner(inputGenotype);
        sample.countNumberOfSample(genotype);//must occurs after countSampleLength() because some values are needed to be counted.
        genotype.close();
        
        //get chrom, id, snp sequence for each snp saved.
        genotype  = new Scanner(inputGenotype);
        sample.getSnpTypeAndChromAndId(genotype);
        genotype.close();
        
        pos = new StringTokenizer(posSeq.toString(), "\t");
        
        for(int i = 0; i < numberOfSnp; i++){
            
            if(!LDhat.hasNextLine()){
                return;
            }
            
            LDhatCurrentLine = LDhat.nextLine();
            
            LDhatValue = new StringTokenizer(LDhatCurrentLine);
            LDhatValue.nextToken();
            value = Double.parseDouble(LDhatValue.nextToken());
            
            if(i == 0){
                currentPos = pos.nextToken();
                matchingFile.write(currentPos + "\t");
                matchingFile.write("0" + "\t");
                matchingFile.write("0" + "\n");
            }
            else{
                currentPos = pos.nextToken();
                matchingFile.write(currentPos + "\t");
                matchingFile.write(df.format(100*value) + "\t");
                matchingFile.write(df.format(100*value/(Double.parseDouble(currentPos)-Double.parseDouble(previousPos))) + "\n");
            }
            
            previousPos = currentPos;
        }
    
    }
    
    
}
