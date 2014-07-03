package HaplotypeDiversity;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class HaplotypeDiversity {
	private String infilePath;
	private String outFilePath;
	private String labelFile;
	private ArrayList<ArrayList<String>> haplotypesOfPops;
	private ArrayList<ArrayList<Boolean>> distinct;
	private ArrayList<String> label;
	
	public HaplotypeDiversity(String infilePath,String outFilePath,String labelFile){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.labelFile=labelFile;
		this.haplotypesOfPops=new ArrayList<ArrayList<String>>();
		this.distinct=new ArrayList<ArrayList<Boolean>>();
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
								distinct.add(new ArrayList<Boolean>());
							}
								
								
							for(int i=0;i<2;i++)
							{
								line=input.nextLine();
								haplotypesOfPops.get(label.indexOf(id)).add(line);
								distinct.get(label.indexOf(id)).add(true);
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
	}
						
	public void findDistinctHaplotype(){
		try{
			PrintWriter outputStream = new PrintWriter(new FileWriter(outFilePath+"/"+getName(infilePath)+".div"));
			for(int i=0;i<haplotypesOfPops.size();i++)
			{
				int count=0;
				for(int j=0;j<haplotypesOfPops.get(i).size();j++)
				{
					if(distinct.get(i).get(j)==true)
					{
						for(int k=j+1;k<haplotypesOfPops.get(i).size();k++)
						{
							if(haplotypesOfPops.get(i).get(j).equals(haplotypesOfPops.get(i).get(k)))
							{
								distinct.get(i).set(j, false);
								distinct.get(i).set(k,false);
							}
						}
						if(distinct.get(i).get(j)==true)
							count++;
					}
				}
				outputStream.println("#"+label.get(i)+" "+count);
				for(int index=0;index<distinct.get(i).size();index++)
				{
					if(distinct.get(i).get(index)==true)
						outputStream.println(haplotypesOfPops.get(i).get(index));
				}
			}
			outputStream.close();
		}catch(IOException e){
			System.out.println("IOException " + e.getMessage());
		}
	}		
	
	private String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1];
	}
}
