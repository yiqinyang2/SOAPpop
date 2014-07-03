package HaplotypeDiversity;

import java.util.*;
import java.io.*;

public class GetDiversityAverage {
	private String infilePath;
	private String outFilePath;
	private double[] averageValue;
	private double[] variance;
	private String[] name;
	private int count;
	private ArrayList<ArrayList<Integer>> value;
	
	public GetDiversityAverage(String infilePath,String outFilePath){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.count=0;
		this.value=new ArrayList<ArrayList<Integer>>();
	}
	
	public void getResult(){
		initResultArray(infilePath);
		File file=new File(infilePath);
		File[] f=file.listFiles();
		
		for(int i=0;i<f.length;i++)
		{
			try{
				Scanner scanner=new Scanner(new FileReader(f[i]));
				String line;
				int index=0;
				while(scanner.hasNextLine())
				{
					line=scanner.nextLine();
					if(line.charAt(0)=='#')
					{
//						System.out.println(name.length);
//						System.out.println(value.size());
//						System.out.println(averageValue.length);
						name[index]=line.split(" ")[0];
//						System.out.println(name[index]);
//						System.out.println(Integer.parseInt(line.split(" ")[1]));
						averageValue[index]+=Integer.parseInt(line.split(" ")[1]);
						value.get(index).add(Integer.parseInt(line.split(" ")[1]));
//						System.out.println(name[index]);
						index++;
					}
				}
				scanner.close();
			}catch(Exception e){
				System.out.println("there is a bug");
				System.out.println(e.getMessage());
			}	
			count++;
		}
		
		for(int i=0;i<value.size();i++)
		{
			Double temp=0.0;
			for(int j=0;j<value.get(i).size();j++)
				temp+=Math.pow((value.get(i).get(j)-averageValue[i]/count),2);
			variance[i]=temp/value.get(i).size();
		}
	}
	
	public void printRsultToOutputFile(){
		try{
			PrintWriter outputStream=new PrintWriter(new FileWriter(outFilePath+"/diversityResult.txt"));
			outputStream.println("Average Value:");
			for(int i=0;i<averageValue.length;i++)
			{				
				outputStream.printf("%-5s","#"+name[i]+": ");
				outputStream.printf("%.5f",(averageValue[i]/count));
				outputStream.println();
			}
			outputStream.println();
			outputStream.println("Variance:");
			for(int i=0;i<variance.length;i++)
			{	
				outputStream.printf("%-5s","#"+name[i]+": ");
				outputStream.printf("%.5f",(variance[i]));
				outputStream.println();
			}
			
			outputStream.close();
			System.out.println("Done!");
		}catch(IOException e){
			System.out.println(2);
			System.out.println("IOException " + e.getMessage());
		}
	}
	
	public void initResultArray(String infile){
		File file=new File(infilePath);
		File[] f=file.listFiles();
		
		for(int i=0;i<f.length;i++)
		{
			try{
				Scanner scanner=new Scanner(new FileReader(f[i]));
				String line;
				int index=0;
				while(scanner.hasNextLine())
				{
					line=scanner.nextLine();
					if(line.charAt(0)=='#')
					{
						index++;
					}
				}
				this.averageValue=new double[index];
				this.variance=new double[index];
				this.name=new String[index];
				
				for(int j=0;j<averageValue.length;j++)
				{	
					this.value.add(new ArrayList<Integer>());
					averageValue[j]=0.0;
					variance[j]=0.0;
				}
				scanner.close();
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
			break;
		}
		
	}
}
