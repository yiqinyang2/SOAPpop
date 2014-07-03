/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package KendallStatistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
 *
 * @author yangyiqing
 */

/*
 * "smartPcaOutputFileName" in constructor is the name(path) of .pca output file
 * of smartPCA, "inputReferenceFile" is name of user given property file, and
 * "outputFile" is name(path) of output file containing kendall statistics result.
 * Users need to give property file with tab as string tokenizer.
 * 
 * No additional space lines at the end of property file!!
 */
public class KendallStatistics {
    static File outputFile;
    static File eigenFile;
    static File referenceFile;
    static int numberOfProperty = 0;
    static int numberOfIndividual = 0;
    static String[] propertyName;
    static double[][] eigenValue;
    static double[][] referenceValue;
    static double[][] kendallValue;
    static int numberOfEigenvector;
    
    public KendallStatistics(String smartPcaOutputFileName, String inputReferenceFile, String outputFile, int numberOfEigenVector) throws IOException{
        eigenFile = new File(smartPcaOutputFileName);
        referenceFile = new File(inputReferenceFile);
        numberOfEigenvector = numberOfEigenVector;
        
        readEigenValue();
        readReferenceValue();
        
        calculateKendall();
        
        FileWriter result= new FileWriter(new File(outputFile)); 
        for(int i = 0; i < numberOfProperty; i++)
        {
            result.write(propertyName[i] + "\t");
            for(int j = 0; j < numberOfEigenvector; j++){
                result.write(String.valueOf(kendallValue[i][j]));
                result.write("\t");
            }
            result.write("\n");
        }
        result.close();
        
    }
    
    public void readEigenValue()throws IOException{
        Scanner in = new Scanner(eigenFile);
        String line;
        StringTokenizer next;
        int currentInd = 0;
        while(in.hasNextLine()){
            numberOfIndividual ++;
            in.nextLine();
        }
        numberOfIndividual -= 3;
        eigenValue = new double[numberOfIndividual][numberOfEigenvector];
        
        in = new Scanner(eigenFile);
        
        for(int i = 0; i < 3; i++){
            in.nextLine();
        }
        
        line = in.nextLine();
        while(line != null){
            next = new StringTokenizer(line);
            for(int i = 0; i < numberOfEigenvector; i++){
                eigenValue[currentInd][i] = Double.parseDouble(next.nextToken());
            }
            currentInd++;
            if(in.hasNextLine())
                line = in.nextLine();
            else
                break;
        }
        //System.out.println(numberOfIndividual);
        if(currentInd != numberOfIndividual)
            numberOfIndividual = currentInd;
        
        in.close();
    }
    
    public void readReferenceValue()throws IOException{
        Scanner in = new Scanner(referenceFile);
        String line;
        StringTokenizer next;
        int numberOfLines = 0;
        int currentInd = 0;
        
        line = in.nextLine();
        next = new StringTokenizer(line, "\t");
        while(next.hasMoreTokens()){
            numberOfProperty ++;
            next.nextToken();
        }
        
        propertyName = new String[numberOfProperty];
        referenceValue = new double[numberOfIndividual][numberOfProperty];
        
        next = new StringTokenizer(line, "\t");
        for(int i = 0; i < numberOfProperty; i++){
            propertyName[i] = next.nextToken();
        }
        
        line = in.nextLine();
        while(line != null && numberOfLines <= numberOfIndividual){
            next = new StringTokenizer(line, "\t");
            
            for(int i = 0; i < numberOfProperty; i++){
                referenceValue[currentInd][i] = Double.parseDouble(next.nextToken());
            }
            currentInd++;
            if(in.hasNextLine())
                line = in.nextLine();
            else
                break;
            numberOfLines++;
        }
        in.close();
    }
    
    public void calculateKendall(){
        double numberOfCon = 0;
        double numberOfDis = 0;
        
        kendallValue = new double[numberOfProperty][numberOfEigenvector];
        for(int i = 0; i < numberOfProperty; i++){
            //System.out.println("property id " + (i+1));
            for(int j = 0; j < numberOfEigenvector; j++){
                numberOfCon = 0;
                numberOfDis = 0;
                
                for(int k = 0; k < numberOfIndividual; k++){
                    //System.out.println("individual id " + (k+1));
                    for(int l = k+1; l < numberOfIndividual; l++){
                        //System.out.println(eigenValue[l][j] + " " + referenceValue[l][i]);
                        if((eigenValue[k][j] < referenceValue[k][i])&&(eigenValue[l][j] < referenceValue[l][i])){
                            numberOfCon ++;
                        }
                        else if((eigenValue[k][j] > referenceValue[k][i])&&(eigenValue[l][j] > referenceValue[l][i])){
                            numberOfCon ++;
                        }
                        else if((eigenValue[k][j] == referenceValue[k][i])||(eigenValue[l][j] == referenceValue[l][i])){
                            continue;
                        }
                        else
                            numberOfDis ++;
                        //System.out.println(numberOfCon + " " + numberOfDis);
                        
                    }
                }
                //System.out.println(numberOfCon + " " + numberOfDis);
                kendallValue[i][j] = (numberOfCon - numberOfDis)/(0.5 * Double.parseDouble(String.valueOf(numberOfIndividual)) * (Double.parseDouble(String.valueOf(numberOfIndividual)) - 1))*10000/10000;
            }  
        }      
        
    }
    
}
