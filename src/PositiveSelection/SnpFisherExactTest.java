package PositiveSelection;

import java.util.*;
import java.io.*;
import java.math.BigDecimal;

public class SnpFisherExactTest {
	private ArrayList<ArrayList<Character>> snp;
	private ArrayList<String> label;
	private PrintWriter outputStream;
	private String choromosomeID;
	private String position;
	private int a, b, c , d;
	private double p;
	private Character mark;
	private String infile;
	private String outFilePath;
	private String labelFile;
	
	public SnpFisherExactTest(String infile, String outFilePath, String labelFile){
		this.infile=infile;
		this.outFilePath=outFilePath;
		this.labelFile=labelFile;
		this.label=new ArrayList<String>();
		a=b=c=d=-1;
		p=0.0;
		mark=null;
	}
	
	public void mainFunction(){
		File file=new File(infile);
		String path=file.getPath();
		
		try{
			outputStream=new PrintWriter(new FileWriter(outFilePath+"/"+file.getName()+".txt"));
			Scanner input=new Scanner(new FileReader(path));				
			
			while(input.hasNextLine())
			{
				Scanner popID=new Scanner(new FileReader(labelFile));							
				String id;
				String line=input.nextLine();
				String[] s1=line.split("	");
				String[] s2=s1[s1.length-1].split(" ");
				choromosomeID=s1[0];
				position=s1[1];
				
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
				
				getValue();
				
				BigDecimal numerator=(doFactorial(a+b).multiply(doFactorial(c+d)).multiply(doFactorial(a+c)).multiply(doFactorial(b+d)));
				BigDecimal denominator=(doFactorial(a).multiply(doFactorial(b)).multiply(doFactorial(c)).multiply(doFactorial(d)).multiply(doFactorial(a+b+c+d)));
//				System.out.println(numerator+" "+denominator);
//				System.out.println(numerator.divide(denominator,10, BigDecimal.ROUND_HALF_UP));

				p=Double.parseDouble(numerator.divide(denominator,10, BigDecimal.ROUND_HALF_UP).toString());	
				
				outputStream.println(choromosomeID+" "+position+(" ")+p);
				
				snp=new ArrayList<ArrayList<Character>>();
				label=new ArrayList<String>();
				mark=null;
				a=-1;
				b=-1;
				c=-1;
				d=-1;
				p=0.0;
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
	
	public void getValue(){
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
			if(mark==null)
			{
				if(!snp.get(i).isEmpty())
				{
					c2=snp.get(i).get(0);
					mark=c2;
				}
				else
					c2=' ';
			}
			else 
				c2=mark;
			while(!snp.get(i).isEmpty()&&snp.get(i).contains(c2))
			{
				count2+=2;
				snp.get(i).remove(c2);
			}
			
			if(a==-1)
				a=count1+count2;
			else
				b=count1+count2;
			
			if(!snp.get(i).isEmpty())
			{
				c2=snp.get(i).get(0);
				count2=0;
				while(!snp.get(i).isEmpty()&&snp.get(i).contains(c2))
				{
					count2+=2;
					snp.get(i).remove(c2);
				}
				if(c==-1)
					c=count1+count2;
				else
					d=count1+count2;
			}
			else
			{	
				if(c==-1)
					c=count1;
				else
					d=count1;
			}
		}
	}
	
	
	public BigDecimal doFactorial(Integer number){
	    if(number == 1||number == 0){
	        return new BigDecimal("1");
	    }else{
	    	BigDecimal temp=new BigDecimal(number.toString());
	        return doFactorial((Integer)(number - 1)).multiply(temp);
	    }
	}
}
