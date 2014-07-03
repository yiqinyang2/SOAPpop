package HaplotypeDiversity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;


public class  GetCorrectInputFiles{
	private String inputFilePath;
	private String outputFilePath;
	private String labelFile;
	private boolean[] choiced;
	private Hashtable<String,Integer> count; 
	
	public GetCorrectInputFiles(String inputFilePath, String outputFilePath, String labelFile){
		this.inputFilePath=inputFilePath;
		this.outputFilePath=outputFilePath;
		this.labelFile=labelFile;
		this.count=new Hashtable<String,Integer>();
	}
	
	public void mainFunction(){
		int minSize=getMinimunSize(labelFile);
		System.out.println(minSize);
		try{
			Scanner scanner=new Scanner(new FileReader(labelFile));
			PrintWriter outputStream=new PrintWriter(new FileWriter(outputFilePath+"/label.txt"));
			int index=0;
			while(scanner.hasNextLine())
			{
				String line=scanner.nextLine();
				if(count.get(line)<minSize)
				{
//					System.out.println(line);
//					System.out.println(count.get(line));
					outputStream.println(line);
					choiced[index]=true;
					count.put(line,count.get(line)+1);
				}
				index++;
			}
			
			scanner.close();
			outputStream.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		int temp=0;
		for(int i=0;i<choiced.length;i++)
			if(choiced[i]==true)
				temp++;
		System.out.println(temp);
		System.out.println(choiced.length);
		try{
			File infile=new File(inputFilePath);
			File[] files=infile.listFiles();
			
			for(int i=0;i<files.length;i++)
			{
				Scanner haplotypeFileReader=new Scanner(new FileReader(files[i]));
				PrintWriter outputStream=new PrintWriter(new FileWriter(outputFilePath+"/"+files[i].getName()));
				int index=0;
				String line;
				outputStream.println("BEGIN GENOTYPES");
				while(haplotypeFileReader.hasNextLine())
				{
					line=haplotypeFileReader.nextLine();
					if(line.charAt(0)=='#')
					{
						if(choiced[index]==true)
						{
							outputStream.println("#id"+(index+1));
							outputStream.println(haplotypeFileReader.nextLine());
							outputStream.println(haplotypeFileReader.nextLine());
						}
						index++;
					}
				}
				haplotypeFileReader.close();
				outputStream.close();
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public int getMinimunSize(String labelFile){
		Hashtable<String,Integer> popID=new Hashtable<String,Integer>();
		int totalLineNumber=0;
		try {
			Scanner scanner=new Scanner(new FileReader(labelFile));
			
			
			while(scanner.hasNextLine()){
				String line=scanner.nextLine();
				if(!popID.containsKey(line))
				{
					popID.put(line, 1);
					count.put(line,0);
				}
				else
					popID.put(line, popID.get(line)+1);
				totalLineNumber++;
			}			
			scanner.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		choiced=new boolean[totalLineNumber];
		
		for(int i=0;i<choiced.length;i++)
			choiced[i]=false;
		
		Collection<Integer> sizes=popID.values();
		Iterator<Integer> it=sizes.iterator();
		int minSize=it.next();
		while( it.hasNext())
		{
			int size=it.next();
			if(minSize>size)
				minSize=size;
		}
		return minSize;
	}
}
