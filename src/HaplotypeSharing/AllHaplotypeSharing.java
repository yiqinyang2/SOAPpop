package HaplotypeSharing;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class AllHaplotypeSharing {
	private String infilePath;
	private String outFilePath;
	private String labelFile;
	private ArrayList<ArrayList<String>> haplotypesOfPops;
	private ArrayList<ArrayList<Boolean>> equal;
	private ArrayList<ArrayList<Double>> result;
	private ArrayList<String> label;
	private ArrayList<Integer> number;
	
	public AllHaplotypeSharing(String infilePath,String outFilePath,String labelFile){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.labelFile=labelFile;
		this.haplotypesOfPops=new ArrayList<ArrayList<String>>();
		this.equal=new ArrayList<ArrayList<Boolean>>();
		this.result=new ArrayList<ArrayList<Double>>();
		this.label=new ArrayList<String>();
		this.number=new ArrayList<Integer>();
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
								equal.add(new ArrayList<Boolean>());
								result.add(new ArrayList<Double>());
							}
								
								
							for(int i=0;i<2;i++)
							{
								line=input.nextLine();
								haplotypesOfPops.get(label.indexOf(id)).add(line);
								equal.get(label.indexOf(id)).add(false);
							}
						}
					}
				}
			}
			input.close();
			popID.close();
		}catch (IOException e){
			System.out.println("False");
			System.out.println("IOException " + e.getMessage());
		}catch (Exception e){
			System.out.println(1);
			System.out.println("Exception " + e.getMessage());
		}
		
		for(int i=0;i<haplotypesOfPops.size();i++)
			number.add(haplotypesOfPops.get(i).size());
	}
				
				
	public void getResult(){
		
		for(int i=0;i<haplotypesOfPops.size();i++)
		{
			for(int i1=i+1;i1<equal.size();i1++)
				for(int i2=0;i2<equal.get(i1).size();i2++)
					equal.get(i1).set(i2,false);
			
			result.get(i).add((double)1);
			
			for(int j=i+1;j<haplotypesOfPops.size();j++)
			{
				int countSameNumber=0;
				for(int index1=0;index1<haplotypesOfPops.get(i).size();index1++)
				{
					for(int index2=0;index2<haplotypesOfPops.get(j).size();index2++)
					{
						if(equal.get(j).get(index2)==false)
						{
							if(haplotypesOfPops.get(i).get(index1).equals(haplotypesOfPops.get(j).get(index2)))
							{
								countSameNumber++;
								equal.get(j).set(index2,true);
								break;
							}
						}		
					}		
				}
				
				result.get(i).add(((double)countSameNumber)/number.get(i));
				result.get(j).add(((double)countSameNumber)/number.get(j));
			}
		}
	}
					
	public void printResultToOutputFile(){
		try{
			PrintWriter outputStream=new PrintWriter(new FileWriter(outFilePath+"/"+getName(infilePath)+".as"));
			outputStream.print("        ");
			for(int i=0;i<result.size();i++)
				outputStream.printf("%-12s","#"+label.get(i));
			outputStream.println();
			for(int i=0;i<result.size();i++)
			{
				outputStream.printf("%-8s","#"+label.get(i));
				for(int j=0;j<result.get(i).size();j++)
				{
					outputStream.printf("%-12.5f",result.get(i).get(j));
				}			
				outputStream.println();
			}
			outputStream.close();
		}catch(Exception e){
			System.out.println(2);
			System.out.println("IOException " + e.getMessage());
		}
	}
	
	private static String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1].split("\\.")[0];
	}
}
