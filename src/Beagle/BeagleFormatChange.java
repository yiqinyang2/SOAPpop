package Beagle;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class BeagleFormatChange {
	private String inputFile;
	private String outputFilePath;
	
	public BeagleFormatChange(String inputFile, String outputFilepath){
		this.inputFile=inputFile;
		this.outputFilePath=outputFilePath;		
	}
	
	public void mainFunction(){
		try {
			Scanner scanner=new Scanner(new FileReader(inputFile));
			PrintWriter outputStream=new PrintWriter(new FileWriter(outputFilePath+"/"+getName(inputFile)+".bgl"));
			String line=null;
			
			while(scanner.hasNextLine())
			{
				line=scanner.nextLine();
				String[] s1=line.split("	");
				String[] s2=s1[s1.length-1].split(" ");
				outputStream.print("M "+s1[0]+"_"+s1[1]);
				for(int i=0;i<s2.length;i++)
				{
					if(s2[i].equals("A"))
						outputStream.print(" A A");
					else if(s2[i].equals("T"))
						outputStream.print(" T T");
					else if(s2[i].equals("G"))
						outputStream.print(" G G");
					else if(s2[i].equals("C"))
						outputStream.print(" C C");
					else if(s2[i].equals("R"))
						outputStream.print(" A G");
					else if(s2[i].equals("Y"))
						outputStream.print(" C T");
					else if(s2[i].equals("K"))
						outputStream.print(" G T");
					else if(s2[i].equals("M"))
						outputStream.print(" A C");
					else if(s2[i].equals("S"))
						outputStream.print(" G C");
					else if(s2[i].equals("W"))
						outputStream.print(" A T");
					else if(s2[i].equals("_"))
						outputStream.print(" _ _");
				}
				outputStream.println();
			}
			scanner.close();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1];
	}
}
