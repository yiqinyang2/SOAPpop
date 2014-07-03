package PositiveSelection;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import JSci.maths.statistics.TDistribution;

public class SignificantFst {
	private ArrayList<String> container;
	private ArrayList<Integer> position;
	private ArrayList<Double> fst;
	private double[][] G;
	private int[][] N;
	private int[][] minPosition;
	private int[][] maxPosition;
	private int count;
	private int CID;
	private int chromosomeNumber;
	private String infilePath;
	private String outFilePath;
	
	public SignificantFst(String infilePath,String outFilePath,int chromosomeNumber){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.chromosomeNumber=chromosomeNumber;
		container=new ArrayList<String>();
		position=new ArrayList<Integer>();
		fst=new ArrayList<Double>();
		G=new double[chromosomeNumber][];
		N=new int[chromosomeNumber][];
		minPosition=new int[chromosomeNumber][];
		maxPosition=new int[chromosomeNumber][];
	}
	
	public void mainFunction(){
		File file=new File(infilePath);
		File[] f=file.listFiles();
		
		for(int index1=0;index1<f.length;index1++)
		{
			String path=f[index1].toString();
			if(path.endsWith(".fst"))
			{
				CID=Integer.parseInt(getName(path).split("\\.")[0]);
				
				getGValueAndNValue(path);
					
				container=new ArrayList<String>();
				position=new ArrayList<Integer>();
			}
		}
		ShowResult();		
	}

	public void getGValueAndNValue(String path){
		double mean=0.0;
		double tValue=0.0;
		double maxAbsoluteValue=0.0;
		double s=0.0;
		int min=0;
		int middle=0;
		int max=0;
		int index=0;
		int size=0;
		
		try{
			Scanner scanner=new Scanner(new FileReader(path));
			while(scanner.hasNextLine())
			{
				String line=scanner.nextLine();
				container.add(line);
				position.add(Integer.parseInt(line.split(" ")[1]));
			}
			scanner.close();
		}catch(Exception e){
			System.out.println("Exception1"+e.getMessage());
		}
		
		
		int i=0;	
		
		while(i<position.size())
		{
			min=position.get(i);
			for(int j=i;j<position.size()&&position.get(j)<=(min+50000);j++)
			{
				if(middle==0&&position.get(j)>=(min+25000))
					middle=position.get(j);
				max=position.get(j);
			}
			if(middle==0)
				i=position.indexOf(max)+1;
			else
				i=position.indexOf(middle);
			middle=0;
			size++;
		}
		i=0;
		minPosition[CID-1]=new int[size];
		maxPosition[CID-1]=new int[size];
		N[CID-1]=new int[size];
		G[CID-1]=new double[size];
		while(i<position.size())
		{
			min=position.get(i);
			for(int j=i;j<position.size()&&position.get(j)<=(min+50000);j++)
			{
				if(!container.get(j).split(" ")[2].equals("NA"))
					fst.add(Double.parseDouble(container.get(j).split(" ")[2]));
				if(middle==0&&position.get(j)>=(min+25000))
					middle=position.get(j);
				max=position.get(j);
			}
			
			
			minPosition[CID-1][index]=min;
			maxPosition[CID-1][index]=max;
			N[CID-1][index]=fst.size();
			double sum=0;
			for(int j=0;j<fst.size();j++)		
				sum+=fst.get(j);
				
			mean=sum/fst.size();
			for(int j=0;j<fst.size();j++)
			{
				if(maxAbsoluteValue<Math.abs(fst.get(j)-mean))
					maxAbsoluteValue=Math.abs(fst.get(j)-mean);
			}
			
			s=getStandardDeviation(mean);
			
			G[CID-1][index]=(maxAbsoluteValue/s);
			
//			System.out.print(N[CID-1][index]+"	");
//			System.out.println(G[CID-1][index]);
			
			fst=new ArrayList<Double>();
			
			if(middle==0)
				i=position.indexOf(max)+1;
			else
				i=position.indexOf(middle);
			middle=0;
			maxAbsoluteValue=0.0;
			count++;
			index++;
		}
	}

	public void ShowResult(){
		double tValue;
		for(int i=0;i<G.length;i++)
		{
			try{
				PrintWriter outputStream=new PrintWriter(new FileWriter(outFilePath+"/"+(i+1)+".significantFst"));			
				for(int j=0;j<G[i].length;j++)
				{
					tValue=getTValue(N[i][j]);

					if(G[i][j]>(((N[i][j]-1)/Math.sqrt(N[i][j]))*Math.sqrt(tValue*tValue/(N[i][j]-2+tValue*tValue))))
						outputStream.println("SignificantWindow:"+(j+1)+" MinPositon:"+minPosition[i][j]+" MaxPosition:"+maxPosition[i][j]);
//					System.out.print(tValue+"	");
//					System.out.print(G[i][j]+"	");
//					System.out.print((((N[i][j]-1)/Math.sqrt(N[i][j]))*Math.sqrt(tValue*tValue/(N[i][j]-2+tValue*tValue))));
//					System.out.println();
				}
				outputStream.close();
			}catch(Exception e){
				System.out.println("Exception2"+e.getMessage());
			}
		}
		
	}
	public double getTValue(int n){
		int df=n-2;
		double p=1-(0.05/count)/(n*2);
		System.out.println(count);
		if(df>0)
		{
			TDistribution t=new TDistribution(df);
			return t.inverse(p);
		}
		else
			return (double)0;
	}
	
    public double getStandardDeviation(double mean){
    	double sum=0.0;
    	
    	for(int i=0;i<fst.size();i++)
    		sum+=(fst.get(i)-mean)*(fst.get(i)-mean);
    	
    	return Math.sqrt(sum/fst.size());
    }
    
    public String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1];
	}
}
