package Shapeit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FormatChangeForShapeit {
	private ArrayList<ArrayList<String>> container=new ArrayList<ArrayList<String>>();
	private String infile;
	private String outputPath;
	public FormatChangeForShapeit(String infile, String outputPath){
		this.infile=infile;
		this.outputPath=outputPath;
	}
	
	public void mainFunction(){
		try{
			Scanner scanner=new Scanner(new FileReader(new File(infile)));
			PrintWriter pedFile=new PrintWriter(new FileWriter(outputPath+"/"+getName(infile)+".ped"));
			PrintWriter mapFile=new PrintWriter(new FileWriter(outputPath+"/"+getName(infile)+".map"));
			String line;
			int count=1;
			
			line=scanner.nextLine();
			String[] s1=line.split("	");
			String[] s2=s1[s1.length-1].split(" ");
			
			for(int i=0;i<s2.length;i++)
			{
				container.add(new ArrayList<String>());
				if(s2[i].equals("A"))
					container.get(i).add("A A");
				else if(s2[i].equals("T"))
					container.get(i).add("T T");
				else if(s2[i].equals("G"))
					container.get(i).add("G G");
				else if(s2[i].equals("C"))
					container.get(i).add("C C");
				else if(s2[i].equals("R"))
					container.get(i).add("A G");
				else if(s2[i].equals("Y"))
					container.get(i).add("C T");
				else if(s2[i].equals("K"))
					container.get(i).add("G T");
				else if(s2[i].equals("M"))
					container.get(i).add("A C");
				else if(s2[i].equals("S"))
					container.get(i).add("G C");
				else if(s2[i].equals("W"))
					container.get(i).add("A T");
				else
					container.get(i).add("0 0");
			}
			
			mapFile.println(s1[0]+" SNP"+count+" 0 "+s1[1]);
			count++;
			
			while(scanner.hasNextLine())
			{
				line=scanner.nextLine();
				s1=line.split("	");
				s2=s1[s1.length-1].split(" ");
				
				for(int i=0;i<s2.length;i++)
				{
					if(s2[i].equals("A"))
						container.get(i).add("A A");
					else if(s2[i].equals("T"))
						container.get(i).add("T T");
					else if(s2[i].equals("G"))
						container.get(i).add("G G");
					else if(s2[i].equals("C"))
						container.get(i).add("C C");
					else if(s2[i].equals("R"))
						container.get(i).add("A G");
					else if(s2[i].equals("Y"))
						container.get(i).add("C T");
					else if(s2[i].equals("K"))
						container.get(i).add("G T");
					else if(s2[i].equals("M"))
						container.get(i).add("A C");
					else if(s2[i].equals("S"))
						container.get(i).add("G C");
					else if(s2[i].equals("W"))
						container.get(i).add("A T");
					else
						container.get(i).add("0 0");
				}
				mapFile.println(s1[0]+" SNP"+count+" 0 "+s1[1]);
				
				count++;
			}
			
			int index=1;
			for(int i=0;i<container.size();i++)
			{
				pedFile.print("FAM"+index+" IND"+index+" 0 0 1 0 ");
				for(int j=0;j<container.get(i).size();j++)
				{
					pedFile.print(container.get(i).get(j)+" ");
				}
				pedFile.println();
				index++;
			}
			
			scanner.close();
			pedFile.close();
			mapFile.close();
		}catch(Exception e){
			System.out.println("error");
			System.out.println(e.getMessage());
		}
		
	}
	
	private String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1];
	}
}
