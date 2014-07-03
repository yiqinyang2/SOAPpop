package chromoPainter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class GetRemRate {
	File infile;
	String outputPath;
	String ldhatFile;
	
	public GetRemRate(File infile, String outputPath, String ldhatFile){
		this.infile=infile;
		this.outputPath=outputPath;
		this.ldhatFile=ldhatFile;
	}
	
	public void mainFunction() throws IOException{
		Scanner infileReader=new Scanner(new FileReader(infile));
		Scanner ldhatFileReader=new Scanner(new FileReader(ldhatFile));
		PrintWriter outputStream=new PrintWriter(new FileWriter(outputPath+"/"+infile.getName()+".remrate"));
		String ldhatLine;
		String infileLine;
		int startPosition=0;
		int endPosition=0;
		int currentPosition=0;
		double startCMValue=0.0;
		double endCMValue=0.0;
		double ratio=0.0;
		int index=0;
		
		ldhatFileReader.nextLine();
		if(infileReader.hasNextLine())
		{
			infileLine=infileReader.nextLine();
			currentPosition=Integer.parseInt(infileLine.split("	")[1]);
		}
		
		while(ldhatFileReader.hasNextLine())
		{
			ldhatLine=ldhatFileReader.nextLine();
			endPosition=Integer.parseInt(ldhatLine.split("	")[0]);
			ratio=Double.parseDouble(ldhatLine.split("	")[1]);
			endCMValue=Double.parseDouble(ldhatLine.split("	")[2]);
			
			
			
			while(currentPosition<=endPosition)
			{
				if(index==0)
					outputStream.println(currentPosition+" 0");
				else if(startPosition==0||currentPosition==endPosition)
					outputStream.println(currentPosition+" "+endCMValue);
				else
				{
					double temp=((currentPosition-startPosition)/Math.pow(10,6))*ratio+startCMValue;
					outputStream.println(currentPosition+" "+temp);
				}
				
				if(infileReader.hasNextLine())
				{	
					infileLine=infileReader.nextLine();
					currentPosition=Integer.parseInt(infileLine.split("	")[1]);
					index++;
				}
				else
					break;			
			}
			
			
			startPosition=endPosition;
			startCMValue=endCMValue;
			if(!infileReader.hasNextLine())
				break;
		}
		if(!ldhatFileReader.hasNextLine()&&infileReader.hasNextLine())
		{
			while(infileReader.hasNextLine())
			{
				infileLine=infileReader.nextLine();
				currentPosition=Integer.parseInt(infileLine.split("	")[1]);
				index++;
				outputStream.println(currentPosition+" "+endCMValue);
			}
		}
		System.out.println("Finish!");
		infileReader.close();
		ldhatFileReader.close();
		outputStream.close();
	}
}
