/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PositiveSelection;

import Main.Genotype;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author yangyiqing
 */

//window number should be between the first "_" and "."
//input name format: chrom_windowNo.pca.evec
public class CSS extends Genotype{
    
    static CSS css = new CSS();
    static double CSS;
    static double[] Css;
    static Scanner cssInput;
    static int numberOfTeam;
    static String subpopulation;
    static File subpopulationFile;
    static int firstCountedTeamNo;
    static int secondCountedTeamNo;
    static String[] nextS;
    static String[] nextSub;
    static double[][][] teamInfo;
    static int teamNo;
    static String[] teamName;
    static int m;
    static int n;
    static String[] sampleInfo;
    static File[] inputOriginFiles;
    static int numberOfWindows;
    static Scanner scanner;
    static int binsize;
    static String outputPath;
    public CSS(){}
    
    public CSS(String path, String outputPath, String subpopulationName, int teamNoOne, int teamNoTwo, int binsize)throws IOException{

        scanner = new Scanner(System.in);
        String[] buffer;
        String buff;
        int count = 0;
        int[] order;
        boolean[] check;
        int startNo;
        int endNo;
        numberOfTeam = 0;
        teamNo = 0;
        this.outputPath=outputPath;
        File filePath = new File(path);
        inputOriginFiles = filePath.listFiles();
        numberOfWindows = inputOriginFiles.length;
        buffer = new String[numberOfWindows];
        
        //check postfix!
        for(int i = 0; i < numberOfWindows; i++){
            
            if((inputOriginFiles[i].getName()).endsWith(".evec")){
                buffer[count] = new String();
                
                buffer[count] = inputOriginFiles[i].getPath();
                
                count++;
            }
        }
        
        numberOfWindows = count;
        Css = new double[numberOfWindows];
        
        order = new int[numberOfWindows];
        
        inputOriginFiles = new File[numberOfWindows];
        check = new boolean[numberOfWindows];
        
        //System.out.println(numberOfWindows);
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(i);
            check[i] = true;
            
            startNo = buffer[i].lastIndexOf("_");
            //System.out.println(startNo);
            endNo = buffer[i].lastIndexOf(".");
            buff = buffer[i].substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);
            
            order[i] = Integer.parseInt(buffer[i].substring(startNo+1,endNo));
            //System.out.println(order[i]);
        }
        
        Arrays.sort(order);
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println(order[i]);
            for(int j = 0; j < numberOfWindows; j++){
                startNo = buffer[j].lastIndexOf("_");
                //System.out.println(startNo);
                endNo = buffer[j].lastIndexOf(".");
                buff = buffer[j].substring(0, endNo);
                endNo = buff.lastIndexOf(".");
                buff = buff.substring(0, endNo);
                endNo = buff.lastIndexOf(".");
                buff = buff.substring(0, endNo);
                
                if(check[i] = true && Integer.parseInt(buff.substring(startNo+1,endNo)) == order[i]){
                    
                    inputOriginFiles[i] = new File(buffer[j]);
                    check[i] = false;
                    break;
                }
            
            }
        }
        
        subpopulation = subpopulationName;
        subpopulationFile = new File(subpopulation);
        while(subpopulationFile.exists()!= true){
            System.out.print("Subpopulation file not exists. Re-enter file name: ");
            subpopulation = scanner.nextLine();
            subpopulationFile = new File(subpopulation);
        }
        
        firstCountedTeamNo = teamNoOne;
        secondCountedTeamNo = teamNoTwo;
        this.binsize = binsize;
        cssMainMethod();
        scanner.close();
    }
    
    public static void getTeamInfo(File inputFile)throws IOException{
        int count;
        String line;
        Scanner scan;
        String current;
        StringTokenizer nextValue;
        FileReader fileReader = new FileReader(subpopulationFile);
        BufferedReader reader = new BufferedReader(fileReader);
        
        css.countNumberOfSample(new Scanner(inputFile));
        
        nextS = new String[numberOfSample+1];
        nextSub = new String[numberOfSample+1];
        sampleInfo = new String[numberOfSample];
        teamName = new String[numberOfSample];
        teamNo = 0;
        m = 0;
        n = 0;
        
        line = reader.readLine();
        
        for(int i = 0; line!=null; i++){
            nextS[i] = new String();
            nextS[i] = line;
            nextSub[i] = new String();
            nextSub[i] = line;
            line = reader.readLine();
        }
        
        reader.close();
        fileReader.close();
        
        scan = new Scanner(inputFile);
        scan.nextLine();
        for(int i = 0; scan.hasNextLine(); i++){
            sampleInfo[i] = scan.nextLine();
        }
        
        for(int i = 0; i < numberOfSample; i++){
            
            //System.out.println("i "+i);
            if(nextS[i].equals("\t")==false){
                //System.out.println("i+1 "+(i+1));
                teamName[teamNo] = nextS[i];
                current = nextS[i];
                
                //avoid repeatedly recording the same team name
                for(int j = i+1; j < numberOfSample; j++){
                    //System.out.println(nextS[j]);
                    if(nextS[j].equals(current)){
                        nextS[j] = "\t";
                    }
                }
                
                numberOfTeam ++;
                teamNo++;
                //System.out.println(numberOfTeam);
            }
            
        }
        
        teamInfo = new double[numberOfTeam][numberOfSample][2];
        
        for(int i = 0; i < numberOfTeam; i++){
            count = 0;
            
            if(i == firstCountedTeamNo-1||i == secondCountedTeamNo-1){
                //System.out.println(teamName[i]);
                
                for(int j = 0; j < numberOfSample; j++){
                    //System.out.println(j+" "+nextSub[j]);
                    if(nextSub[j].equals(teamName[i])){
                        nextValue = new StringTokenizer(sampleInfo[j]);
                        nextValue.nextToken();
                        teamInfo[i][count][0] = Double.parseDouble(nextValue.nextToken());
                        teamInfo[i][count][1] = Double.parseDouble(nextValue.nextToken());
                        
                        count ++;
                        
                    }
                }
                if(i == firstCountedTeamNo-1)
                    m = count;
                else
                    n = count;
                //System.out.println("m "+m);
                //System.out.println("n "+n);
            }
        }
        
    }
    
    public static void cssMainMethod()throws IOException{
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println("i "+i);
            getTeamInfo(inputOriginFiles[i]);
            calculateCSS(i);
        }
        
        outputF =outputPath+ ".CSS.txt";
        // Check if target file exists
        targetFile = new File(outputF);
        css.checkFile(targetFile);
        target = new FileWriter(outputF);
        
        css.writeToTarget();
        //close output stream
        target.close();
    }

    @Override
    public void writeToTarget()throws IOException{
        int startNo;
        int endNo;
        String chromosome;
        String buff;
        String inputFileName;
        target.write("Chr"+"\t"+"Start"+"\t"+"End"+"\t"+"CSS");
        target.write("\n");
        chromosome = new StringTokenizer(inputOriginFiles[0].getName(),"_").nextToken();
        
        for(int i = 0; i < numberOfWindows; i++){
            //System.out.println("File "+inputOriginFiles[i].getName());
            target.write(chromosome+"\t");
            inputFileName = inputOriginFiles[i].getPath();
            startNo = inputFileName.lastIndexOf("_");
            //System.out.println(startNo);
            endNo = inputFileName.lastIndexOf(".");
            buff = inputFileName.substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);
            endNo = buff.lastIndexOf(".");
            buff = buff.substring(0, endNo);
            
            target.write((1+binsize*(Integer.parseInt(inputFileName.substring(startNo+1,endNo))-1))+"\t");
            target.write((binsize*Integer.parseInt(inputFileName.substring(startNo+1,endNo)))+"\t");
            target.write(Css[i]+"\t");
            target.write("\n");
            
        }
        
    }

    public static void calculateCSS(int windowNo){
        double firstPart = 0;
        double secondPart = 0;
        double secondPartM = 0;
        double secondPartN = 0;
        
        for(int i = 0; i < m; i++){
            for(int j = 0; j < n; j++){
                firstPart += calculateDistance(teamInfo[firstCountedTeamNo-1][i][0], teamInfo[firstCountedTeamNo-1][i][1],teamInfo[secondCountedTeamNo-1][j][0], teamInfo[secondCountedTeamNo-1][j][1]);
                //System.out.println(firstPart);
            }
        }
        firstPart /= m*n;
        
        if(m!=1){
            for(int i = 0; i < m-1; i++){
                secondPartM += calculateDistance(teamInfo[firstCountedTeamNo-1][i][0], teamInfo[firstCountedTeamNo-1][i][1],teamInfo[firstCountedTeamNo-1][i+1][0], teamInfo[firstCountedTeamNo-1][i+1][1]);  
                //System.out.println("second"+secondPartM);
            }
            secondPartM /= m*m*(m-1);
        }else
            secondPartM = 0;
        if(n!=1){
            for(int i = 0; i < n-1; i++){
                secondPartN += calculateDistance(teamInfo[secondCountedTeamNo-1][i][0], teamInfo[secondCountedTeamNo-1][i][1],teamInfo[secondCountedTeamNo-1][i+1][0], teamInfo[secondCountedTeamNo-1][i+1][1]);  
                //System.out.println("second"+secondPartN);
            }
            secondPartN /= n*n*(n-1);
        }else
            secondPartN = 0;
        secondPart = (m+n)*(secondPartM+secondPartN);
        
        CSS = firstPart - secondPart;
        Css[windowNo] = CSS;
        
    }
    
    public static double calculateDistance(double va1, double va2, double vb1, double vb2){
        double distance;
        distance = Math.pow(Math.pow((va1-vb1),2)+Math.pow((va2-vb2),2),0.5);
        return distance;
    }
    
    @Override
    public void countNumberOfSample(Scanner scan){
        numberOfSample = 0;
        scan.nextLine();
        while(scan.hasNextLine()){
            numberOfSample++;
            scan.nextLine();
        }
    }
}
