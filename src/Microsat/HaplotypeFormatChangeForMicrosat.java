package Microsat;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class HaplotypeFormatChangeForMicrosat {
	private String infilePath;
	private String outFilePath;
	private String labelFile;
	private ArrayList<ArrayList<String>> haplotypesOfPops;
	private ArrayList<String> label;
	
	public HaplotypeFormatChangeForMicrosat(String infilePath,String outFilePath,String labelFile){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.labelFile=labelFile;
		this.haplotypesOfPops=new ArrayList<ArrayList<String>>();
		this.label=new ArrayList<String>();
	}
	
	public void storeInputToArray(){
		try{
			Scanner input=new Scanner(new FileReader(infilePath));
			Scanner popID=new Scanner(new FileReader(labelFile));
			String line;
			
			while (input.hasNextLine())
			{
				line=input.nextLine();
				if(line.equals("BEGIN GENOTYPES"))
				{
					while(input.hasNextLine())
					{
						String id;
						line=input.nextLine();
						if(line.charAt(0)=='#')
						{
							id=popID.nextLine();
							if(label.size()==0||!label.contains(id))
							{
								label.add(id);
								haplotypesOfPops.add(new ArrayList<String>());
							}
								
								
							for(int i=0;i<2;i++)
							{
								line=input.nextLine();
								haplotypesOfPops.get(label.indexOf(id)).add(line);
							}
						}
					}
				}
			}
		}catch (IOException e){
			System.out.println("False");
			System.out.println("IOException " + e.getMessage());
		}catch (Exception e){
			System.out.println(1);
			System.out.println("Exception " + e.getMessage());
		}
	}
	
	public void changeFormat(){
		try{
			PrintWriter outputStream=new PrintWriter(new FileWriter(outFilePath+"/"+getName(infilePath)+".txt"));
			ArrayList<String> container=new ArrayList<String>();	
			for(int i=0;i<haplotypesOfPops.size();i++)
			{
				int count;
				String s;
				while(!haplotypesOfPops.get(i).isEmpty())
				{
					count=1;
					s=haplotypesOfPops.get(i).get(0);
					if(container.isEmpty()||!container.contains(s))
						container.add(s);
					haplotypesOfPops.get(i).remove(0);
					if(!haplotypesOfPops.get(i).isEmpty())
					{
						for(int j=0;j<haplotypesOfPops.get(i).size();j++)
						{
							if(haplotypesOfPops.get(i).get(j).equals(s))
								count++;
						}
						while(!haplotypesOfPops.get(i).isEmpty()&&haplotypesOfPops.get(i).contains(s))
							haplotypesOfPops.get(i).remove(s);
					}
					
					outputStream.println("#"+label.get(i)+" "+getName(infilePath)+" "+(container.indexOf(s)+1)+" "+count);
				}
				outputStream.println();
			}
			outputStream.close();
			}catch(Exception e){
				System.out.println(2);
				System.out.println("IOException " + e.getMessage());
		}
	}
	
	private String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1];
	}
	
	private boolean isInteger(String s){
		Integer temp=0;
		try{
			temp=Integer.parseInt(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
}
