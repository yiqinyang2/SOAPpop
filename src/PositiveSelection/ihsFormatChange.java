package PositiveSelection;

import java.io.*;
import java.util.*;

public class ihsFormatChange {
//	private int firstPosition;
//	private int lastPosition;
//	private double value;
//	private char c1,c2;
	private ArrayList<ArrayList<String>> haplotype;
	private ArrayList<String> label;
	private String[][] allele;
	private String mapFile;
	private String haplotypeFilePath;
	private String outFilePath;
	private String labelFile;
	
	public ihsFormatChange(String labelFile, String mapFile, String haplotypeFilePath, String outFilePath){
		this.labelFile=labelFile;
		this.mapFile=mapFile;
		this.haplotypeFilePath=haplotypeFilePath;
		this.outFilePath=outFilePath;
		haplotype=new ArrayList<ArrayList<String>>();
		label=new ArrayList<String>();
		allele=null;
//		firstPosition=0;
//		lastPosition=0;
	}
	
	public void mainFunction(){
		try{
			File haplotypeFile=new File(haplotypeFilePath);
			Scanner haplotypeFileReader=new Scanner(new FileReader(haplotypeFile));
			Scanner labelFileReader=new Scanner(new FileReader(labelFile));
			Scanner mapFileReader=new Scanner(new FileReader(mapFile));
			String line=null;
			int index=0;
			
			while(haplotypeFileReader.hasNextLine())
			{
				line=haplotypeFileReader.nextLine();
				if(line.equals("BEGIN GENOTYPES"))
				{
					while(haplotypeFileReader.hasNextLine())
					{
						line=haplotypeFileReader.nextLine();
						String id;
						
						if(line.charAt(0)=='#')
						{
							id=labelFileReader.nextLine();
							if(label.size()==0||!label.contains(id))
							{
								label.add(id);
								haplotype.add(new ArrayList<String>());
							}
								
								
							for(int i=0;i<2;i++)
							{
								line=haplotypeFileReader.nextLine();
								String[] temp=line.split(" ");
								if(allele==null)
									allele=new String[temp.length][2];
								
								for(int j=0;j<temp.length;j++)
								{
									if(allele[j][0]==null)
										allele[j][0]=temp[j];
									else
									{
										if(!temp[j].equals(allele[j][0]))
											allele[j][1]=temp[j];
									}
								}
								
								haplotype.get(label.indexOf(id)).add(line);
							}
						}
					}
				}
			}
			
			PrintWriter mapFileWriter=new PrintWriter(new FileWriter(outFilePath+"/"+haplotypeFile.getName()+".ihsmap"));
			mapFileReader.nextLine();
			while(mapFileReader.hasNextLine())
			{
				line=mapFileReader.nextLine();
				mapFileWriter.println("res"+line.split(" ")[0]+" "+line.split(" ")[0]+" "+line.split(" ")[1]+" "+allele[index][0]+" "+allele[index][1]);
				index++;
			}
			mapFileWriter.close();
			for(int i=0;i<haplotype.size();i++)
			{
				PrintWriter output=new PrintWriter(new FileWriter(outFilePath+"/"+haplotypeFile.getName()+"_"+label.get(i)+".ihshap"));
				for(int j=0;j<haplotype.get(i).size();j++)
				{
					String[] s=haplotype.get(i).get(j).split(" ");
					for(int k=0;k<s.length;k++)
					{
						if(s[k].charAt(0)==allele[k][0].charAt(0))
							output.print("0 ");
						else
							output.print("1 ");
					}
					output.println();
				}
				output.close();
			}
			
			haplotypeFileReader.close();
			labelFileReader.close();
			mapFileReader.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
//	public void mainFunction(){
//		try{
//			File haplotypeFile=new File(haplotypeFilePath);
//			Scanner scanner1=new Scanner(new FileReader(haplotypeFile));
//			Scanner scanner2=new Scanner(new FileReader(mapFile));
//			Scanner scanner3=new Scanner(new FileReader(labelFile));
//			PrintWriter outputStream1=new PrintWriter(new FileWriter(outFilePath+"/"+haplotypeFile.getName()+".ihshap"));
//			PrintWriter outputStream2=new PrintWriter(new FileWriter(outFilePath+"/"+haplotypeFile.getName()+".ihsmap"));
//			
//			while(scanner3.hasNextLine())
//			{
//				label.add(scanner3.nextLine());
//			}
//			
//			for(int i=0;i<label.size();i++)
//			{
//				String element=label.get(i);
//				
//			}
//			
//			while(scanner1.hasNextLine())
//			{
//				String line=scanner1.nextLine();
//				if(line.equals("BEGIN GENOTYPES"))
//				{
//					while(scanner1.hasNextLine())
//					{
//						line=scanner1.nextLine();
//						line=scanner1.nextLine();
//						String[] s=line.split(" ");
//						
//						if(snp.size()!=s.length)
//						{
//							for(int i=0;i<s.length;i++)
//								snp.add(new ArrayList<Character>());
//						}
//						for(int i=0;i<s.length;i++)
//							snp.get(i).add(s[i].charAt(0));
//						
//						line=scanner1.nextLine();
//						s=line.split(" ");
//						
//						for(int i=0;i<s.length;i++)
//							snp.get(i).add(s[i].charAt(0));
//					}
//				}
//			}
//			
//			for(int i=0;i<snp.size();i++)
//			{	
//				String line=scanner2.nextLine();
//				if(snp.get(i).contains('A')&&snp.get(i).contains('T'))
//					outputStream2.println(line+" A T");
//				else if(snp.get(i).contains('A')&&snp.get(i).contains('G'))
//					outputStream2.println(line+" A G");
//				else if(snp.get(i).contains('A')&&snp.get(i).contains('C'))
//					outputStream2.println(line+" A C");
//				else if(snp.get(i).contains('T')&&snp.get(i).contains('G'))
//					outputStream2.println(line+" T G");
//				else if(snp.get(i).contains('T')&&snp.get(i).contains('C'))
//					outputStream2.println(line+" T C");
//				else if(snp.get(i).contains('G')&&snp.get(i).contains('C'))
//					outputStream2.println(line+" G C");
//			}
//			
//			for(int j=0;j<snp.get(0).size();j++)
//			{
//				for(int i=0;i<snp.size();i++)
//				{
//					if(snp.get(i).contains('A')&&snp.get(i).contains('T'))
//					{
//						if(snp.get(i).get(j)=='A')
//							outputStream1.print("0 ");
//						else
//							outputStream1.print("1 ");
//					}
//					else if(snp.get(i).contains('A')&&snp.get(i).contains('G'))
//					{
//						if(snp.get(i).get(j)=='A')
//							outputStream1.print("0 ");
//						else
//							outputStream1.print("1 ");
//					}
//					else if(snp.get(i).contains('A')&&snp.get(i).contains('C'))
//					{
//						if(snp.get(i).get(j)=='A')
//							outputStream1.print("0 ");
//						else
//							outputStream1.print("1 ");
//					}
//					else if(snp.get(i).contains('T')&&snp.get(i).contains('G'))
//					{
//						if(snp.get(i).get(j)=='T')
//							outputStream1.print("0 ");
//						else
//							outputStream1.print("1 ");
//					}
//					else if(snp.get(i).contains('T')&&snp.get(i).contains('C'))
//					{
//						if(snp.get(i).get(j)=='T')
//							outputStream1.print("0 ");
//						else
//							outputStream1.print("1 ");
//					}
//					else if(snp.get(i).contains('G')&&snp.get(i).contains('C'))
//					{
//						if(snp.get(i).get(j)=='G')
//							outputStream1.print("0 ");
//						else
//							outputStream1.print("1 ");
//					}
//				}
//				outputStream1.println();
//			}
//					
//			
//			scanner1.close();
//			outputStream1.close();
//			outputStream2.close();
//		}catch(Exception e){
//			System.out.println(e.getMessage());
//		}
//	}
}
