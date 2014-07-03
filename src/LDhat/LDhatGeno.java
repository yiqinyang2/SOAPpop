/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LDhat;

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
public class LDhatGeno extends Genotype{
    static String[] firstSnpKind;
    static String[] secondSnpKind;
    static String outputSites = "LDhat.sites"; 
    static String outputLocs = "LDhat.locs";
    static FileWriter sitesFile;
    static FileWriter locsFile;
    static LDhatGeno sample = new LDhatGeno();
    static String firstKind;
    static String secondKind;
    static int numberOfLine = 0;
    static int windowLength;
    static String snpPos[];
    static String pos;
    
    public LDhatGeno(){}
    
    public LDhatGeno(String inputFileName, String outputSitesName, String outputLocsName, int windowLength)throws IOException{
        
        this.windowLength = windowLength;
        
        Scanner scanner = new Scanner(System.in);
        inputFile = new File(inputFileName);
        
        while(inputFile.exists()!= true){
            System.out.print("File not exists. Re-enter file name: ");
            inputF = scanner.nextLine();
            inputFile = new File(inputF);
        }
        
        outputSites = outputSitesName;
        outputLocs = outputLocsName;
        
        targetFile = new File(outputSites);
        checkFile(targetFile, outputSites);
        
        targetFile = new File(outputLocs);
        checkFile(targetFile, outputLocs);
        
        //set FileWriters for output files.
        sitesFile = new FileWriter(outputSites);
        locsFile = new FileWriter(outputLocs);
        
        mainMethod();
        scanner.close();
        
    }
    
    public LDhatGeno(String inputFileName, int windowLength)throws IOException{
        
        this.windowLength = windowLength;
        
        Scanner scanner = new Scanner(System.in);
        inputFile = new File(inputFileName);
        
        while(inputFile.exists()!= true){
            System.out.print("File not exists. Re-enter file name: ");
            inputF = scanner.nextLine();
            inputFile = new File(inputF);
        }
        
        targetFile = new File(outputSites);
        checkFile(targetFile, outputSites);
        
        targetFile = new File(outputLocs);
        checkFile(targetFile, outputLocs);
        
        //set FileWriters for output files.
        sitesFile = new FileWriter(outputSites);
        locsFile = new FileWriter(outputLocs);
        
        mainMethod();
        scanner.close();
        
    }
    
    public static void mainMethod() throws IOException{
        
        Scanner input = new Scanner(inputFile);
        
        sample.countNumberOfLine(input);
        firstSnpKind = new String[numberOfLine];
        secondSnpKind = new String[numberOfLine];
        snpPos = new String[numberOfLine];
        input.close();
        
        //get sampleLength as result
        input = new Scanner(inputFile);
        sample.countNumberOfSnp(input);
        input.close();
        
        //get NumberOfSample as result
        input = new Scanner(inputFile);
        sample.countNumberOfSample(input);//must occurs after countSampleLength() because some values are needed to be counted.
        numberOfSample ++ ;
        input.close();
        
        //get chrom, id, snp sequence for each snp saved.
        input  = new Scanner(inputFile);
        sample.getSnpTypeAndChromAndId(input);
        input.close();
        
        writeToSitesFile(sitesFile);
        writeToLocsFile(locsFile);
        
        // Close streams
        sample.closeOutputStream();
        
        //System.out.println("Done!");
        
    }
    
    public static void writeToSitesFile(FileWriter sites) throws IOException{
    
        sites.write(String.valueOf(numberOfSample) + " " + String.valueOf(numberOfSnp) + " 2" + "\n");
        for(int i = 0; i < numberOfSample; i++){
            sites.write(">Genotype" + String.valueOf(i+1) + "\n");
            sites.write(snpSeq[i].toString() + "\n");
        }
        
        sites.close();
    }
    
    public static void writeToLocsFile(FileWriter locs) throws IOException{
    
        locs.write(String.valueOf(numberOfSnp) + " " + snpPos[numberOfSnp - 1] + " L" + "\n");
        for(int i = 0; i < numberOfSnp; i++){
            locs.write(String.valueOf(Double.parseDouble(snpPos[i])/1000) + "\n");
        }
        locs.close();
    }
    
    public static void countNumberOfLine(Scanner input){
        while(input.hasNextLine()){
            numberOfLine ++;
            input.nextLine();
        }
    }
    
    @Override
    public void countNumberOfSnp(Scanner input){
        String nextLine;
        numberOfSnp = 0;
        idOfSnp = new StringBuilder();
        for(int i = 0;input.hasNextLine(); i++){
            nextLine = input.nextLine();
            //System.out.println("nextLine is "+nextLine);
            if(checkSnp(nextLine, i) == true){
                idOfSnp.append(i);
                idOfSnp.append("\t");
                snpPos[numberOfSnp] = pos;
                numberOfSnp ++;
            }
        }
    }
      
    public static void checkFile(File inputFile, String fileName){
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
                    if(inputFile.getName().equals(outputSites))
                        outputSites = scan.next();
                    else if(inputFile.getName().equals(outputLocs))
                        outputLocs = scan.next();
                    break;
                }else{
                    break;
                }
            }
        }  
    }
    
    @Override
    public void getSnpTypeAndChromAndId(Scanner input)throws IOException{
        input.useDelimiter("	");
        String nextSnpInfo;
        String snpInfo;
        StringTokenizer snp;
        StringBuffer snpType[] = new StringBuffer[numberOfSample];
        String nextLine;
        String ref;
        
        for(int i = 0; i< numberOfSample; i++){
            snpType[i] = new StringBuffer();
        }
        //get chrom, id, ref, alt, snpType
        //System.out.println("sample number "+numberOfSample);
        
        for (int i = 0; i < numberOfSnp; i++){
            //System.out.println("i "+i);
            nextLine = input.nextLine();
            if(checkSnp(nextLine) == false){
                i --;
                continue;
            }
            //System.out.println("first snp kind "+firstKind);
            //System.out.println("second snp kind "+secondKind);
        
            snp = new StringTokenizer(nextLine, "	");
            chromSeq.append(snp.nextToken());
            chromSeq.append("\t");
            posSeq.append(snp.nextToken());
            posSeq.append("\t");
            ref = snp.nextToken();
            refSeq.append(ref);
            refSeq.append("\t");
            
            snpInfo = snp.nextToken();
            snp = new StringTokenizer(snpInfo, " ");
            changeSnpFormat(ref,snpType[0]);
            for(int j = 1; j < numberOfSample; j++){
                nextSnpInfo = snp.nextToken();
                //System.out.println("next snp is "+nextSnpInfo);
                changeSnpFormat(nextSnpInfo, snpType[j]);
            }
        }
        //System.out.println("snpType[0]"+snpType[0]);
       snpSeq = snpType;
        //System.out.println("sngType"+snpType[0]);
        //System.out.println("finish");
        
    }
    
    public static void changeSnpFormat(String snpInfo, StringBuffer s){
        //System.out.println("first snp kind "+firstKind);
        //System.out.println("second snp kind "+secondKind);
        
        String info;
        StringTokenizer seperateSnp;
        String first;
        String second;
        info = getSnpSeqs(snpInfo);
        //System.out.println(info);
        seperateSnp = new StringTokenizer(info);
        first = seperateSnp.nextToken();
        second = seperateSnp.nextToken();
        
        if(first.equals(firstKind)){
            if(second.equals(secondKind)){
                s.append("2");
                //System.out.println("a1");
            }
            else if(second.equals(firstKind)){
                s.append("0");
                //System.out.println("0");
            }
        }else if(first.equals(secondKind)){
            if(second.equals(secondKind)){
                s.append("1");
                //System.out.println("2");
            }
            else if(second.equals(firstKind)){
                s.append("2");
                //System.out.println("b1");
            }
        }else{
                s.append("?");
        }
        //System.out.println(s);
    }
    
    public boolean checkSnp(String snp, int snpNo){
        
        firstKind = "";
        secondKind = "";
        boolean isSnp = false;
        String nextsnp;
        StringTokenizer next = new StringTokenizer(snp, "	");
        StringTokenizer divisionSnp;
        String first;
        String snps;
        String ref;
        String sepFirst;
        String sepSecond;
        
        nextsnp = next.nextToken();
        pos = next.nextToken();
        ref = getSnpSeqs(next.nextToken());
        snps = next.nextToken();
        next = new StringTokenizer(snps);
        
        if(ref.equals("?")){
            first = getSnpSeqs(next.nextToken());
            while(first.equals("?")){
                first = next.nextToken();
            }
        }else{
            first = ref;
        }
        
        divisionSnp = new StringTokenizer(first);
        sepFirst = divisionSnp.nextToken();
        sepSecond = divisionSnp.nextToken();
        
        if(sepFirst.equals(sepSecond))
            firstKind = sepFirst;
        else{
            firstKind = sepFirst;
            secondKind = sepSecond;
        }
        
        //System.out.println("f "+firstKind);
        //System.out.println("S "+secondKind);
        
        while(next.hasMoreTokens()){
            nextsnp = next.nextToken();
            //System.out.println("nextsnp is "+nextsnp);
            //System.out.println("first is "+ first);
            
            if(nextsnp.equals("-") == false){
                
                first = getSnpSeqs(nextsnp);
                divisionSnp = new StringTokenizer(first);
                sepFirst = divisionSnp.nextToken();
                sepSecond = divisionSnp.nextToken();
        
                if(sepFirst.equals(sepSecond)){
                    if(sepFirst.equals(firstKind) == false){
                        if(secondKind.equals("")){
                            secondKind = sepFirst;
                        }else if(sepFirst.equals(secondKind) == false){
                            isSnp = false;
                            return isSnp;
                        }
                    }
                }else{
                    if(secondKind.equals("")){
                        if(sepFirst.equals(firstKind) == false)
                            secondKind = sepFirst;
                        else if(sepSecond.equals(firstKind) == false)
                            secondKind = sepSecond; 
                    }
                    
                    if(((sepFirst.equals(firstKind)&&sepSecond.equals(secondKind))||(sepFirst.equals(secondKind)&&sepSecond.equals(firstKind)))==false){
                            isSnp = false;
                            return isSnp;
                    }
                        
                }
            }
        }
        
        //if all R , check condition not suitable!!!
        if(secondKind.equals(""))
            isSnp = false;
        else{
            boolean check = false;
            
            next = new StringTokenizer(snps);
            
            first = next.nextToken();
            
            while(next.hasMoreTokens()){
                if(next.nextToken().equals(first) == false){
                    check = true;
                    break;
                }
            }
            
            if(check == true){
                isSnp = true;
                firstSnpKind[snpNo] = firstKind;
                secondSnpKind[snpNo] = secondKind;
            }
        }
        return isSnp;
    }
    
    @Override
    public boolean checkSnp(String snp){
        
        firstKind = "";
        secondKind = "";
        boolean isSnp = false;
        String nextsnp;
        StringTokenizer next = new StringTokenizer(snp, "	");
        StringTokenizer divisionSnp;
        String first;
        String snps;
        String ref;
        String sepFirst;
        String sepSecond;
        
        nextsnp = next.nextToken();
        pos = next.nextToken();
        ref = getSnpSeqs(next.nextToken());
        snps = next.nextToken();
        next = new StringTokenizer(snps);
        
        if(ref.equals("?")){
            first = getSnpSeqs(next.nextToken());
            while(first.equals("?")){
                first = next.nextToken();
            }
        }else{
            first = ref;
        }
        
        divisionSnp = new StringTokenizer(first);
        sepFirst = divisionSnp.nextToken();
        sepSecond = divisionSnp.nextToken();
        
        if(sepFirst.equals(sepSecond))
            firstKind = sepFirst;
        else{
            firstKind = sepFirst;
            secondKind = sepSecond;
        }
            
        while(next.hasMoreTokens()){
            nextsnp = next.nextToken();
            //System.out.println("nextsnp is "+nextsnp);
            //System.out.println("first is "+ first);
            
            if(nextsnp.equals("-") == false){
                
                first = getSnpSeqs(nextsnp);
                divisionSnp = new StringTokenizer(first);
                sepFirst = divisionSnp.nextToken();
                sepSecond = divisionSnp.nextToken();
        
                if(sepFirst.equals(sepSecond)){
                    if(sepFirst.equals(firstKind) == false){
                        if(secondKind.equals("")){
                            secondKind = sepFirst;
                        }else if(sepFirst.equals(secondKind) == false){
                            isSnp = false;
                            return isSnp;
                        }
                    }
                }else{
                    if(secondKind.equals("")){
                        if(sepFirst.equals(firstKind) == false)
                            secondKind = sepFirst;
                        else if(sepSecond.equals(firstKind) == false)
                            secondKind = sepSecond; 
                    }
                    
                    if(((sepFirst.equals(firstKind)&&sepSecond.equals(secondKind))||(sepFirst.equals(secondKind)&&sepSecond.equals(firstKind)))==false){
                            isSnp = false;
                            return isSnp;
                    }
                        
                }
            }
        }
        
        //if all R , check condition not suitable!!!
        
        if(secondKind.equals(""))
            isSnp = false;
        else{
            boolean check = false;
            
            next = new StringTokenizer(snps);
            
            first = next.nextToken();
            
            while(next.hasMoreTokens()){
                if(next.nextToken().equals(first) == false){
                    check = true;
                    break;
                }
            }
            
            if(check == true){
                isSnp = true;
            }
        }
        return isSnp;
    }
    
    public static String getSnpSeqs(String snpInfo){
    	if(snpInfo.equals("A"))
    		return ("A A");
    	else if(snpInfo.equals("T"))
    		return ("T T");
    	else if(snpInfo.equals("C"))
    		return ("C C");
    	else if(snpInfo.equals("G"))
    		return ("G G");
    	else if(snpInfo.equals("R"))
    		return ("A G");    	
    	else if(snpInfo.equals("Y"))
    		return ("C T");
    	else if(snpInfo.equals("K"))
    		return ("G T");
    	else if(snpInfo.equals("M"))
    		return ("A C");
    	else if(snpInfo.equals("S"))
    		return ("C G");    	
    	else if(snpInfo.equals("W"))
    		return ("A T");
    	return "? ?";
//        switch (snpInfo) {
//            case "A":
//                return ("A A");
//            case "T":
//                return ("T T");
//            case "C":
//                return ("C C");
//            case "G":
//                return ("G G");
//            case "R":
//                return ("A G");
//            case "Y":
//                return ("C T");
//            case "K":
//                return ("G T");
//            case "M":
//                return ("A C");
//            case "S":
//                return ("C G");
//            case "W":
//                return ("A T");
//            default:
//                return ("? ?");
//        }
    
    }
}
