package FastPhase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class RandomSNP {
	private String infilePath;
	private String outFilePath;
	private int snpNumber;
	private int lineNumber;
	private int[] randomNumber;
	
	public RandomSNP(String infilePath,String outFilePath,int snpNumber){
		this.infilePath=infilePath;
		this.outFilePath=outFilePath;
		this.snpNumber=snpNumber;
		this.lineNumber=0;
		this.randomNumber=new int[snpNumber];
		for(int i=0;i<randomNumber.length;i++)
			randomNumber[i]=0;
		
	}
	
	public void getRandomNumber() throws FileNotFoundException{
		Scanner scanner=new Scanner(new FileReader(infilePath));
		
		while(scanner.hasNextLine())
		{
			String line=scanner.nextLine();
			lineNumber++;
		}
		scanner.close();
		if(lineNumber>snpNumber)
		{
			for(int i=0;i<snpNumber;i++)
			{
				int n=(int) Math.ceil(Math.random()*lineNumber)+1;
				while(contains(n))
					n=(int) Math.ceil(Math.random()*lineNumber)+1;
				randomNumber[i]=n;
			}
		}		
	}
	
	public void productOutFile(){
		try{
			
			int count=0;
			int index=1;
			Scanner scanner1=new Scanner(new FileReader(infilePath));
			File file=new File(infilePath);
			PrintWriter outputStream=new PrintWriter(new FileWriter(outFilePath+"/"+file.getName()));
			if(lineNumber>snpNumber)
			{			
				while(scanner1.hasNextLine())
				{
					if(count==snpNumber)
						break;
					String line=scanner1.nextLine();
					if(contains(index))
					{	
						outputStream.println(line);
						count++;
					}
					index++;
				}			
			}
			else
			{
				while(scanner1.hasNextLine())
				{
					outputStream.println(scanner1.nextLine());
				}
			}
			outputStream.flush();
			scanner1.close();
			outputStream.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public boolean contains(int n){
		for(int i=0;i<randomNumber.length;i++)
		{
			if(randomNumber[i]==n)
				return true;
		}
		return false;
	}
}
