/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PositiveSelection;

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

//must be told numberOfSample before used, must be told snpSeq
public class SplitTeams extends PositiveSelection{
    static int numberOfTeam = 0;
    static FileReader fileReader;
    static BufferedReader reader;
    static StringBuffer readInSubpopulationString;
    static StringTokenizer split;
    static String[] nextSub;
    static String[] nextS;
    static String current;
    static FileWriter outputFile;
    static File output;
    static Scanner input;
    static SplitTeams sample = new SplitTeams();

    
    public static void main(String[]args)throws IOException{
        subpopulation = new Scanner(System.in).next();
        subpopulationFile = new File(subpopulation);
        new SplitTeams().mainMethod(new File(new Scanner(System.in).next()));
    }
    
    
    
    public void mainMethod(File inputFile) throws IOException{
        numberOfTeam = 0;
        teamNo = 0;
        
        //System.out.println(inputFile.getName());
        input = new Scanner(inputFile);
        sample.countNumberOfSnp(input);
        input.close();
        input = new Scanner(inputFile);
        sample.countNumberOfSample(input);
        input.close();
        input = new Scanner(inputFile);
        sample.getSnpTypeAndChromAndId(input);
        input.close();
        fileReader = new FileReader(subpopulationFile);
        reader = new BufferedReader(fileReader);
        readInSubpopulationString = new StringBuffer();
        
        nextS = new String[numberOfSample+1];
        nextSub = new String[numberOfSample+1];
        
        String line = reader.readLine();
        
        //System.out.println(numberOfSample);
        for(int i = 0; line!=null; i++){
            //System.out.println(i);
            nextS[i] = new String();
            //System.out.println(split.nextToken());
            nextS[i] = line;
            //System.out.println(nextS[i]);
            //System.out.println(i);
            line = reader.readLine();
        }
        reader.close();
        fileReader.close();
        
        teamInfo = new StringBuilder[numberOfSample];
        //System.out.println("number of teams" +numberOfTeam);
        teamName = new String[numberOfSample];
        
        for(int i = 0; i < numberOfSample; i++){
            
            //System.out.println("i "+i);
            if(nextS[i].equals("\t")==false){
                //System.out.println("i+1 "+(i+1));
                teamName[teamNo] = nextS[i];
                teamInfo[teamNo] = new StringBuilder();
                
                current = nextS[i];
                nextSub[numberOfTeam] = current;
                
                //outputFile.write(snpSeq[i].toString());
                writeToTeamInfo(teamInfo[teamNo], i);
                //System.out.println("i+1 "+(i+1));
                //System.out.println("idOfSnp "+idOfSnp);
                teamInfo[teamNo].append("\n");
                
                //System.out.println("numberOfSample "+numberOfSample);
                //System.out.println("i+1 "+(i+1));
                for(int j = i+1; j < numberOfSample; j++){
                    //System.out.println(nextS[j]);
                    if(nextS[j].equals(current)){
                        nextS[j] = "\t";
                        writeToTeamInfo(teamInfo[teamNo], j);
                        teamInfo[teamNo].append("\n");
                    }
                }
                
                numberOfTeam ++;
                teamNo++;
                //System.out.println(numberOfTeam);
            }
            
        }
    }
    public static void writeToTeamInfo(StringBuilder output, int windowNo)throws IOException{
        //System.out.println(idOfSnp.toString());
        StringTokenizer next = new StringTokenizer(idOfSnp.toString(),"\t");
        String nextToken = next.nextToken();
        StringTokenizer nextSnp = new StringTokenizer(snpSeq[windowNo].toString(),"\t");
        //System.out.println(idOfSnp);
        //System.out.println(snpSeq[windowNo]);
        
        for(int i = 0, j = 0; i < numberOfSnp; i++,j++){
            //System.out.println(i);
            //System.out.println(j);
            //System.out.println(nextToken);
            if(String.valueOf(j).equals(nextToken)){
                output.append(nextSnp.nextToken());
                //System.out.println(nextSnp.nextToken());
                output.append("\t");
                if(i < numberOfSnp-1)
                    nextToken = next.nextToken();
                
            }else{
                i--;
            }
        }
    }
    
}
