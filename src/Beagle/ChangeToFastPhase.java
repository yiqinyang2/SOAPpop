package Beagle;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
public class ChangeToFastPhase {
	private String inputFile;
	private String outputFilePath;
	private ArrayList<ArrayList<String>> holder;
	
	public ChangeToFastPhase(String inputFile, String outputFilePath){
		this.inputFile=inputFile;
		this.outputFilePath=outputFilePath;
		this.holder=new ArrayList<ArrayList<String>>();
	}
	
	public void mainFunction(){
		try {
			Scanner scanner=new Scanner(new FileReader(inputFile));
			PrintWriter outputStream=new PrintWriter(new FileWriter(outputFilePath+"/"+getName(inputFile)+".switch.out"));
			String line;
			String[] s;
			
			line=scanner.nextLine();
			s=line.split(" ");
			
			for(int i=0;i<s.length-2;i++)
				holder.add(new ArrayList<String>());
			
			while(scanner.hasNextLine())
			{
				line=scanner.nextLine();
				s=line.split(" ");
				
				for(int i=2;i<s.length;i++)
					holder.get(i-2).add(s[i]);
			}
			
			outputStream.println("BEGIN GENOTYPES");
			for(int i=0;i<holder.size();i+=2)
			{
				outputStream.println("# id "+(i+1));
				for(int index=0;index<holder.get(i).size();index++)
				{
					if(index!=holder.get(i).size()-1)
						outputStream.print(holder.get(i).get(index)+" ");
					else
						outputStream.println(holder.get(i).get(index));
				}
				i++;
				for(int index=0;index<holder.get(i).size();index++)
				{
					if(index!=holder.get(i).size()-1)
						outputStream.print(holder.get(i).get(index)+" ");
					else
						outputStream.println(holder.get(i).get(index));
				}
			}
			
			scanner.close();
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1];
	}
}
