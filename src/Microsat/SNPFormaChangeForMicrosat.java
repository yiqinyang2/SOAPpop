package Microsat;

import java.util.*;
import java.io.*;

public class SNPFormaChangeForMicrosat {
	private String infilePath;
	private String outFilePath;
	private String labelFile;
	private ArrayList<ArrayList<Character>> snp;
	private ArrayList<String> label;
	private PrintWriter outputStream;
	private String locus;
	
	public SNPFormaChangeForMicrosat(String infilePath,String outFilePath,String labelFile){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.labelFile=labelFile;
		this.snp=new ArrayList<ArrayList<Character>>();
		this.label=new ArrayList<String>();
	}
	
	public void storeInputToArray(){
		
		try{
			outputStream=new PrintWriter(new FileWriter(outFilePath+"/"+getName(infilePath)+".txt"));
			Scanner input=new Scanner(new FileReader(infilePath));				
			
			while(input.hasNextLine())
			{
				Scanner popID=new Scanner(new FileReader(labelFile));							
				String id;
				String line=input.nextLine();
				String[] s1=line.split("	");
				String[] s2=s1[s1.length-1].split(" ");
				locus=s1[1];
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
				}
				
				formatChange();
				
				snp=new ArrayList<ArrayList<Character>>();
				label=new ArrayList<String>();
				
				outputStream.println();
				outputStream.flush();
				popID.close();
			}
			input.close();	
			outputStream.close();
		}catch (IOException e){
			System.out.println("IOException " + e.getMessage());
		}catch (Exception e){
			System.out.println("Exception " + e.getMessage()+"Wrong");
		}
	}
	
	public void formatChange(){
		for(int i=0;i<snp.size();i++)
		{
			int count1=0;
			int count2=0;
			Character c1;
			Character c2;
			if(snp.get(i).contains('R'))
				c1='R';
			else if(snp.get(i).contains('Y'))
				c1='Y';
			else if(snp.get(i).contains('K'))
				c1='K';
			else if(snp.get(i).contains('M'))
				c1='M';
			else if(snp.get(i).contains('S'))
				c1='S';
			else if(snp.get(i).contains('W'))
				c1='W';
			else
				c1=' ';
			while(!snp.get(i).isEmpty()&&snp.get(i).contains(c1))
			{
				count1++;
				snp.get(i).remove(c1);
			}
			if(!snp.get(i).isEmpty())
				c2=snp.get(i).get(0);
			else
				c2=' ';
			while(!snp.get(i).isEmpty()&&snp.get(i).contains(c2))
			{
				count2+=2;
				snp.get(i).remove(c2);
			}
			if(c2=='A')
				c2='1';
			else if(c2=='T')
				c2='2';
			else if(c2=='G')
				c2='3';
			else
				c2='4';
			outputStream.println("#"+label.get(i)+" "+locus+" "+c2+" "+(count1+count2));
			if(!snp.get(i).isEmpty())
			{
				c2=snp.get(i).get(0);
				count2=0;
				while(!snp.get(i).isEmpty()&&snp.get(i).contains(c2))
				{
					count2+=2;
					snp.get(i).remove(c2);
				}
				if(c2=='A')
					c2='1';
				else if(c2=='T')
					c2='2';
				else if(c2=='G')
					c2='3';
				else
					c2='4';
				outputStream.println("#"+label.get(i)+" "+locus+" "+c2+" "+(count1+count2));
			}
			else
			{
				if(count1!=0)
				{
					if(c1=='R')
					{
						if(c2=='1')
							outputStream.println("#"+label.get(i)+" "+locus+" "+'3'+" "+count1);
						else
							outputStream.println("#"+label.get(i)+" "+locus+" "+'1'+" "+count1);
					}
					else if(c1=='Y')
					{
						if(c2=='4')
							outputStream.println("#"+label.get(i)+" "+locus+" "+'2'+" "+count1);
						else
							outputStream.println("#"+label.get(i)+" "+locus+" "+'4'+" "+count1);
					}
					else if(c1=='K')
					{
						if(c2=='3')
							outputStream.println("#"+label.get(i)+" "+locus+" "+'2'+" "+count1);
						else
							outputStream.println("#"+label.get(i)+" "+locus+" "+'3'+" "+count1);
					}
					else if(c1=='M')
					{
						if(c2=='4')
							outputStream.println("#"+label.get(i)+" "+locus+" "+'1'+" "+count1);
						else
							outputStream.println("#"+label.get(i)+" "+locus+" "+'4'+" "+count1);
					}
					else if(c1=='S')
					{
						if(c2=='4')
							outputStream.println("#"+label.get(i)+" "+locus+" "+'3'+" "+count1);
						else
							outputStream.println("#"+label.get(i)+" "+locus+" "+'4'+" "+count1);
					}
					else if(c1=='W')
					{
						if(c2=='1')
							outputStream.println("#"+label.get(i)+" "+locus+" "+'2'+" "+count1);
						else
							outputStream.println("#"+label.get(i)+" "+locus+" "+'1'+" "+count1);
					}
					else
					{
					}
				}
			}
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
