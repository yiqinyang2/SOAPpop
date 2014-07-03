package PositiveSelection;


import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigDecimal;

public class HaplotypeFisherExactTest {
	
	private Hashtable<String,String> windowInformation;
	private ArrayList<ArrayList<String>> holder;
	private ArrayList<String> label;
	private int size1,size2;
	private int a, b, c , d;
	private double p;
	private String infile;
	private String outFilePath;
	private String labelFile;
	private String windowInformationPath;
	
	public HaplotypeFisherExactTest(String infile, String outFilePath, String labelFile, String windowInformationPath){
		this.infile=infile;
		this.outFilePath=outFilePath;
		this.labelFile=labelFile;
		this.windowInformationPath=windowInformationPath;
		this.windowInformation=new Hashtable<String,String>();
		this.holder=new ArrayList<ArrayList<String>>();
		this.label=new ArrayList<String>();
		a=b=c=d=0;
		p=0.0;
	}
	
	public void mainFunction(){
		try{
			PrintWriter outputStream=new PrintWriter(new FileWriter(outFilePath+"/outfile"));
			File file=new File(infile);
			File[] files2=file.listFiles();
			
			storeWindowsInformation();
			
			for(int j=0;j<files2.length;j++)
			{
				String path=files2[j].getPath();
				if(path.endsWith(".out"))
				{
					Scanner scanner=new Scanner(new FileReader(path));
					Scanner popID=new Scanner(new FileReader(labelFile));
					while(scanner.hasNextLine())
					{
						String line=scanner.nextLine();
						if(line.equals("BEGIN GENOTYPES"))
						{
							while(scanner.hasNextLine())
							{
								String id;
								line=scanner.nextLine();
								if(line.charAt(0)=='#')
								{
									id=popID.nextLine();
									if(label.size()==0||!label.contains(id))
									{
										label.add(id);
										holder.add(new ArrayList<String>());
									}
										
										
									for(int i=0;i<2;i++)
									{
										line=scanner.nextLine();
										holder.get(label.indexOf(id)).add(line);
									}
								}
							}
						}
					}
					scanner.close();
					popID.close();
//					System.out.println("before is no bug");
					size1=holder.get(0).size();
					size2=holder.get(1).size();
//					System.out.println("before is no bug");
//					System.out.println(holder.size());
//					
					while(!holder.get(0).isEmpty())
					{
						String line=holder.get(0).get(0);
						while(!holder.get(0).isEmpty()&&holder.get(0).contains(line))
						{
							holder.get(0).remove(line);
							a++;
						}
						c=size1-a;
						while(!holder.get(1).isEmpty()&&holder.get(1).contains(line))
						{
							holder.get(1).remove(line);
							b++;
						}
						d=size2-b;
//						System.out.println("There is no bug");
//						System.out.println(a+" "+b+" "+c+" "+d);
//						System.out.println(size1+ " "+size2);
//						System.out.println(doFactorial(a)+" "+doFactorial(b)+" "+doFactorial(13)+" "+doFactorial(d)+" "+doFactorial(a+b+c+d));
						
						BigDecimal numerator=(doFactorial(a+b).multiply(doFactorial(c+d)).multiply(doFactorial(a+c)).multiply(doFactorial(b+d)));
						BigDecimal denominator=(doFactorial(a).multiply(doFactorial(b)).multiply(doFactorial(c)).multiply(doFactorial(d)).multiply(doFactorial(a+b+c+d)));
//						System.out.println(numerator+" "+denominator);
//						System.out.println(numerator.divide(denominator,10, BigDecimal.ROUND_HALF_UP));
//						if(!((a/size1)<0.1||(b/size2)<0.1))
//						{
							p=Double.parseDouble(numerator.divide(denominator,10, BigDecimal.ROUND_HALF_UP).toString());
							outputStream.println(windowInformation.get(getWindow(files2[j].getName().toString()))+" "+p);
							outputStream.println(line);
//						}
//						else
//						{
//							outputStream.println(line);
//							System.out.println(getWindow(files2[j].getName().toString()));
//							outputStream.println(windowInformation.get(getWindow(files2[j].getName().toString()))+" -");
//							System.out.println("fusk");
//						}
					}
//					System.out.println("before is no bug");

					while(!holder.get(1).isEmpty())
					{
						String line=holder.get(1).get(0);
						outputStream.println(windowInformation.get(getWindow(files2[j].getName().toString()))+" -");
						outputStream.println(line);
						holder.get(1).remove(line);
					}
					
				}
				
				holder=new ArrayList<ArrayList<String>>();
				label=new ArrayList<String>();
				a=0;b=0;c=0;d=0;
				p=0.0;
			}
			
			windowInformation=new Hashtable<String,String>();
				
				
			
			outputStream.close();
		}catch(Exception e){
			System.out.println("bug");
			System.out.println(e.getMessage());
		}
	}
	

	public void storeWindowsInformation(){
		File file =new File(windowInformationPath);
		File[] files=file.listFiles();
		
		for(int i=0;i<files.length;i++)
		{
			try{
				Scanner scanner=new Scanner(new FileReader(files[i]));
				
				while(scanner.hasNextLine())
				{
					String line=scanner.nextLine();
					String[] s=line.split("\t");
					windowInformation.put(s[0],s[1]+" "+s[2]+" "+s[3]);
				}
				
				scanner.close();
			}catch(Exception e){
				System.out.println("wewei");
				System.out.println(e.getMessage());
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
	
	public String getWindow(String s){
		String[] s1=s.split("\\.");
		return s1[0]+"."+s1[1];
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
