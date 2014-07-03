package PositiveSelection;

import java.util.*;
import java.io.*;

public class Fst {
	private String infilePath;
	private String outFilePath;
	private String labelFile;
	private ArrayList<ArrayList<Character>> snp;
	private ArrayList<String> label;
	private ArrayList<Double> frequency;
	private double avgFrequency;
	private double Fst;
	private Character mark;
	
	public Fst(String infilePath,String outFilePath,String labelFile){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.labelFile=labelFile;
		this.snp=new ArrayList<ArrayList<Character>>();
		this.label=new ArrayList<String>();
		this.frequency=new ArrayList<Double>();
		this.avgFrequency=0.0;
		this.Fst=0.0;
		this.mark=null;
	}
	
	public void StoreInputAndGetFst(){
		try{
			PrintWriter outputStream=new PrintWriter(new FileWriter(outFilePath+"/"+getName(infilePath)+".fst"));
			Scanner input=new Scanner(new FileReader(infilePath));
			String line;
			String id;
			
			while (input.hasNextLine())
			{
				Scanner popID=new Scanner(new FileReader(labelFile));
				line=input.nextLine();
				String s1[]=line.split("	");
				String s2[]=s1[3].split("\\s+");
				ArrayList<Character> temp=new ArrayList<Character>();
			
				for(int i=0;i<s2.length;i++)
				{
					id=popID.nextLine();
					if(label.size()==0||!label.contains(id))
					{
						label.add(id);
						snp.add(new ArrayList<Character>());
					}
					if(s2[i].charAt(0)!='-')
						snp.get(label.indexOf(id)).add(s2[i].charAt(0));
					temp.add(s2[i].charAt(0));
					
				}

				avgFrequency=getFrequency(temp);
				popID.close();
				
				Fst=getFst();
				
				outputStream.println(s1[0]+" "+s1[1]+" "+Fst);
				outputStream.flush();
				snp=new ArrayList<ArrayList<Character>>();
				label=new ArrayList<String>();
				frequency=new ArrayList<Double>();
				avgFrequency=0.0;
				Fst=0.0;
				mark=null;
			}
			outputStream.close();
			input.close();
		}catch(Exception e){
			System.out.println("Exception"+e.getMessage());
		}
	}
	
	private double getFst(){
		double variance=0.0;
		double sum=0.0;
		for(int i=0;i<snp.size();i++)
			frequency.add(getFrequency(snp.get(i)));
		for(int i=0;i<frequency.size();i++)
		{
			sum+=(frequency.get(i)-avgFrequency)*(frequency.get(i)-avgFrequency);
//			System.out.println(frequency.get(i));
//			System.out.println(avgFrequency);
		}
		variance=sum/frequency.size();
		return variance/(avgFrequency*(1-avgFrequency));
	}
	
	private double getFrequency(ArrayList<Character> temp){
		int base=temp.size()*2;
		int count1=0;
		int count2=0;
		Character c1;
		Character c2;
		
		if(temp.contains('R'))
			c1='R';
		else if(temp.contains('Y'))
			c1='Y';
		else if(temp.contains('K'))
			c1='K';
		else if(temp.contains('M'))
			c1='M';
		else if(temp.contains('S'))
			c1='S';
		else if(temp.contains('W'))
			c1='W';
		else
			c1=' ';
		while(!temp.isEmpty()&&temp.contains(c1))
		{
			count1++;
			temp.remove(c1);
		}
		if(mark==null)
		{
			if(!temp.isEmpty())
			{
				c2=temp.get(0);
				mark=c2;
			}	
			else
				c2=' ';
		}
		else
			c2=mark;
		while(!temp.isEmpty()&&temp.contains(c2))
		{
			count2+=2;
			temp.remove(c2);
		}
		return (double)(count1+count2)/base;
	}
	
	public String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1];
	}
}
