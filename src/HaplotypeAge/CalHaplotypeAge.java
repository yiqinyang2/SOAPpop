package HaplotypeAge;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class CalHaplotypeAge {
	private String infile;
	private String outfile;
	
	public CalHaplotypeAge(String infile,String outfile){
		this.infile=infile;
		this.outfile=outfile;
	}
	
	public void mainFunction(){
		try{
			Scanner scanner=new Scanner(new FileReader(infile));
			PrintWriter outputStream=new PrintWriter(new FileWriter(outfile));
			
			String position;
			double ehhValue;
			double cmValue;
			String line;
			double age;
			
			while(scanner.hasNextLine())
			{
				line=scanner.nextLine();
				String s[]=line.split("\t");
				position=s[0];
				ehhValue=Double.parseDouble(s[1]);
				cmValue=Double.parseDouble(s[2])/1000000;
				
				age=Math.log(ehhValue)/cmValue;
				
				outputStream.println(position+"\t"+age);
			}
			
			scanner.close();
			outputStream.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
}
