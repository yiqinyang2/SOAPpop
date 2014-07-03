/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PositiveSelection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class EHH {

    static ArrayList<Integer> position;
    static ArrayList<Vector<Character>> fasta;
    static String fastaFilePath;
    static String positionFilePath;
    static FileWriter errorFile;
    static FileWriter EhhOutputFile;
    static int extendSize;
    static double cutOffValue;
    public EHH() {}

    /*
     * here startPos and endPos indicate the area where the customer wants to calculate
     * EHH value, positionFile is the genotype format input file and fastaFile is the output
     * of genotype input file after using fastphase. errorFile is path or to say name of file 
     * indicating in which positions because of limitation of length of snp data, EHH cannot
     * reach value less than 0.01. outputFile is the path(name) of expected EHH output file.
     */
    public EHH(String positionFile, String fastaFile, String errorFile, String outputFile,double cutOffValue) throws IOException {

        positionFilePath = positionFile;
        fastaFilePath = fastaFile;
        this.errorFile = new FileWriter(new File(errorFile));
        EhhOutputFile = new FileWriter(new File(outputFile));
        Scanner scan = new Scanner(new File(positionFile));
        this.cutOffValue=cutOffValue;
        readPosition();
        readFasta();

        double d;
        for (int i = 0; scan.hasNextLine(); i++) {
            
            extendSize = 100;
            d = calEHH(position.get(i));
            
            while (d > cutOffValue) {
                extendSize += 100;
                d = calEHH(position.get(i));
            }
            
            if(d > 0){
                EhhOutputFile.write(position.get(i) + "\t" + d);
                if(d < 0.25){
                    EhhOutputFile.write("\t" + String.valueOf(calDis(position.get(i))));
                }
                EhhOutputFile.write("\n");
            }
            //System.out.println(position.get(i)+ " "+ d);
            scan.nextLine();
        }

        this.errorFile.close();
        EhhOutputFile.close();
    }

    public static int calArrange(int n, int x) {
        int a = 1, b = 1;

        for (int i = n; i > n - x; i--) {
            a = a * i;
        }

        for (int i = 0; i < x; i++) {
            b = b * (i + 1);
        }

        return a / b;
    }

    public static int calStart(int start) {
        for (int i = 0; i < position.size(); i++) {
            if (position.get(i) >= start) {
                
                return i;
            }
        }

        return position.get(position.size() - 1);
    }

    public static int calEnd(int end) {
        for (int i = 0; i < position.size(); i++) {
            if (position.get(i) >= end) {
                
                return i - 1;
            }
        }

        return position.get(position.size() - 1);
    }

    public static boolean isSameSubvector(int s, int e, Vector<Character> v1, Vector<Character> v2) {

        for (int i = s; i <= e; i++) {
            if (v1.get(i) != v2.get(i)) {
                return false;
            }
        }

        return true;
    }

    public static double calEHH(int px) throws IOException {

        double result = 0;

        int p = 0;
        for (int i = 0; i < position.size(); i++) {
            if (px == position.get(i)) {
                p = i;
            }
        }

        int disSeqCount = 0;
        int aCount = 0, ACount = 0;
        int start = calStart(px - extendSize / 2);
        int end = calEnd(px + extendSize / 2);

        if ((px - extendSize / 2) < position.get(0) || (px + extendSize / 2) > position.get(position.size() - 1)) {
            errorFile.write(px + "\n");
            return -1;
        }

        Character refChar = fasta.get(0).get(p);

        for (int i = 0; i < fasta.size(); i++) {
            if (fasta.get(i).get(p).equals(refChar)) {
                aCount++;
            } else {
                ACount++;
            }
        }

        for (int i = 0; i < fasta.size(); i++) {
            boolean isDis = true;

            for (int j = 0; j < fasta.size(); j++) {
                if (i != j) {
                    if (isSameSubvector(start, end, fasta.get(i), fasta.get(j))) {
                        isDis = false;
                        break;
                    }
                }
            }

            if (isDis) {
                disSeqCount++;
            }
        }

        result = (disSeqCount * (double) calArrange(disSeqCount, 2)) / ((double) calArrange(aCount, 2) + (double) calArrange(ACount, 2));

        return result;
    }
    
    public static int calDis(int px) throws IOException{
        int p = 0;
        for (int i = 0; i < position.size(); i++) {
            if (px == position.get(i)) {
                p = i;
            }
        }
        System.out.println(px);
        int start = position.get(calStart(px - extendSize / 2));
        int end = position.get(calEnd(px + extendSize / 2));
        
        return end - start;
    }

    public static void readPosition() throws FileNotFoundException {
        //skip the header... add later
        position = new ArrayList<Integer>();

        Scanner in = new Scanner(new File(positionFilePath));
        String reader;

        while (in.hasNext()) {
            reader = in.nextLine();

            StringTokenizer ts = new StringTokenizer(reader);

            if (ts.hasMoreTokens()) {
                ts.nextToken();
            } else {
                break;
            }

            String temp = ts.nextToken();
            // System.out.println(temp);
            position.add(Integer.parseInt(temp));

        }
    }

    public static void readFasta() throws FileNotFoundException {
        //skip the header.. add later
        fasta = new ArrayList<Vector<Character>>();

        Scanner in = new Scanner(new File(fastaFilePath));
        int isHeadCount = 0;

        while (in.hasNext()) {
            String s = in.nextLine();
            if (s.equals("BEGIN GENOTYPES")) {
                break;
            }
        }

        while (in.hasNext()) {
            String s = in.nextLine();
            if (isHeadCount != 0) {

                if (isHeadCount == 2) {
                    isHeadCount = 0;
                } else {
                    isHeadCount++;
                }


                Vector<Character> v = new Vector<Character>();
                for (int i = 0; i < s.length(); i++) {
                    if (s.charAt(i) != ' ') {
                        v.add(s.charAt(i));
                    }
                }

                fasta.add(v);

            } else {
                isHeadCount++;
            }

        }

    }
}
