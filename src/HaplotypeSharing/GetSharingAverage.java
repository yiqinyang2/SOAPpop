package HaplotypeSharing;

import java.util.*;
import java.io.*;

public class GetSharingAverage {
	private String infilePath;
	private String outFilePath;
	private double[][] result; 
	private String[] name;
	private int count;
	public GetSharingAverage(String infilePath,String outFilePath){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.count=0;
	}
	
	
	public void getResult(){
		initResult(infilePath);
		File file=new File(infilePath);
		File[] f=file.listFiles();
			
		for(int i=0;i<f.length;i++)
		{
			try{
				Scanner scanner=new Scanner(new FileReader(f[i]));
				String line;
				
				line=scanner.nextLine();
				String[] s=line.split("\\s+");
				for(int j=0;j<name.length;j++)
					name[j]=s[j+1];
				int index1=0;
				while(scanner.hasNextLine())
				{
					line=scanner.nextLine();
					String[] number=line.split(" ");
					int index2=0;
					for(int j=0;j<number.length;j++)
					{
						if(isDouble(number[j]))
						{
							result[index1][index2]+=Double.parseDouble(number[j])/100;
							index2++;
						}
					}
					index1++;
				}
			}catch(Exception e){
				System.out.println(e.getMessage());
			}	
			count++;
		}
	}
	
	public void printRsultToOutputFile(){
		try{
			PrintWriter outputStream=new PrintWriter(new FileWriter(outFilePath));
			outputStream.print("          ");
			for(int i=0;i<result.length;i++)
				outputStream.printf("%-12s",name[i]);
			outputStream.println();
			for(int i=0;i<result.length;i++)
			{
				outputStream.printf("%-10s",name[i]);
				for(int j=0;j<result[0].length;j++)
				{
					outputStream.printf("%-12.5f",(result[i][j]*100)/count);
				}			
				outputStream.println();
			}
			outputStream.close();
			System.out.println("Done!");
		}catch(IOException e){
			System.out.println(2);
			System.out.println("IOException " + e.getMessage());
		}
	}
	
	private boolean isDouble(String s){
		double temp=0.0;
		try{
			temp=Double.parseDouble(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	private String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-2];
	}
	
	private void initResult(String infile){
		File file=new File(infile);
		File[] files=file.listFiles();
		
		for(int i=0;i<files.length;i++)
		{
			try{
				Scanner scanner=new Scanner(new FileReader(files[i]));
				String line=scanner.nextLine();
				int index=0;
				while(scanner.hasNextLine())
				{
					scanner.nextLine();
					index++;
				}
				result=new double[index][index];
				name=new String[index];
				scanner.close();
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			break;
		}
	}
}
