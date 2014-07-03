/////////////////////////////
//File Name: PG.java
//Author: FENG XIKANG
//Version: 1.0
//Date: 2013/8/14
//Copyright: FENG XIKANG
////////////////////////////

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;

import chromoPainter.ChromoPainter;
import chromoPainter.ChromoPainterIntrogression;
import chromoPainter.GetRemRate;

import Beagle.BeagleFormatChange;
import Beagle.ChangeToFastPhase;
import FastPhase.FastPHASEgeno;
import FastPhase.RandomSNP;
import HaplotypeAge.CalHaplotypeAge;
import HaplotypeDiversity.GetCorrectInputFiles;
import HaplotypeDiversity.GetDiversityAverage;
import HaplotypeDiversity.HaplotypeDiversity;
import HaplotypeSharing.AllHaplotypeSharing;
import HaplotypeSharing.DistinctHaplotypeSharing;
import HaplotypeSharing.GetSharingAverage;
import LDhat.LDhatGeno;
import LDhat.LDhatResultAccumulative;
import Main.FormatChange;
import Microsat.HaplotypeFormatChangeForMicrosat;
import Microsat.SNPFormaChangeForMicrosat;
import PositiveSelection.CSS;
import PositiveSelection.Fst;
import PositiveSelection.HaplotypeFisherExactTest;
import PositiveSelection.Pi;
import PositiveSelection.RunSnpWindowsPi;
import PositiveSelection.RunSnpWindowsTheta;
import PositiveSelection.SignificantFst;
import PositiveSelection.SnpFisherExactTest;
import PositiveSelection.TajimaD;
import PositiveSelection.Theta;
import PositiveSelection.ihsFormatChange;
import Shapeit.FormatChangeForShapeit;
import Shapeit.OutputFormatChangeToFastPhase;
import SmartPCA.SmartPCAgeno;
import Split.FastSplitSlidingNotFromZero;
import Split.SplitPositionBySnp;
import Split.SplitPositionBySnpNotFromZero;
import Split.SplitSlidingNotFromZero;
import Structure.StructureGeno;

public class PG {
	
	private static Scanner scanner;//scanner to get input
	private static String choiceOfSoftware;//the choice of software
	private static String choiceOfSharing;//the choice of Sharing type
	private static String choiceOfMicrosat;//the choice of type of Microsat(snp or halotype)
	private static String choiceOfPositiveSelection;//the choice of statistics of Positive Selection
	private static String choiceOfInfileFormat;//the choice of infile type(genotype or VCF)
	private static File workFile;//the work directory which is given by user
	private static File genotypeInfile;//the input genotype file
	private static File vcfInfile;//the input vcf file
	private static ArrayList<String> CID=new ArrayList<String>();//the chromosome id
	private static String windowLength;//the windows length
	private static String overlapLength;//the overlap length between two adjacent windows
	private static File labelFile;
	/**
	 *the main function
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException{
		makeSoftwareExecutable();
		scanner=new Scanner(new FileReader(args[0]));
		//ask user to give work directory
		do{
			System.out.println();
			System.out.print("Please give your work directory(All output data will be stored in this directory): ");
			workFile=new File(scanner.nextLine());
		}while(!(workFile.exists()&&workFile.isDirectory()));
		
		//ask user to choose infile type
		do{
			System.out.println();
			System.out.println("1  PGF format");
			System.out.println("2  VCF format");
			System.out.print("Choose your format of input file: ");
			choiceOfInfileFormat=scanner.nextLine();
		}while(!(choiceOfInfileFormat.equals("1")||choiceOfInfileFormat.equals("2")));
		
		//splitting infile according to chromosome ID and splitting windows according windows length
		if(choiceOfInfileFormat.equals("1"))
		{
			//get the genotype infile name
			do{
				System.out.println();
				System.out.print("Input genotype infile name: ");
				genotypeInfile=new File(scanner.nextLine());
			}while(!(genotypeInfile.exists()&&!genotypeInfile.isDirectory()));	
		}
		else
		{
			do{
				System.out.println();
				System.out.print("Input vcf infile name: ");
				vcfInfile=new File(scanner.nextLine());
			}while(!(vcfInfile.exists()&&!vcfInfile.isDirectory()));
			
			new FormatChange(vcfInfile.toString(),workFile.toString()+"/"+getName(vcfInfile.toString()));
		}
		
		
		//run the program repeatedly until user decided to exit
		do{	
			do{
				choicemenu();
				choiceOfSoftware=scanner.nextLine();
			}while(!isCorrectInput(choiceOfSoftware));
			
			do{
				System.out.println();
				System.out.println("Input the label file for subpopulation: ");
				labelFile=new File(scanner.nextLine());
			}while(!labelFile.exists()||labelFile.isDirectory());
			
			if(Integer.parseInt(choiceOfSoftware)==1){
				String choice;
				String splitMode;
				String randomSNPNumber;
				
				do{
					System.out.println();
					System.out.println("  1 Chromosome");
					System.out.println("  2 Sliding window");
					System.out.println("Input your choice: ");
					splitMode=scanner.nextLine();
				}while(!(splitMode.equals("1")||splitMode.equals("2")));
				
				do{
					System.out.println();
					System.out.println("  1 FastPhase");
					System.out.println("  2 Beagle");
					System.out.println("  3 SHAPEIT");
					System.out.print("Input your choice: ");
					choice=scanner.nextLine();
				}while(!(choice.equals("1")||choice.equals("2")||choice.equals("3")));
				
				do{
					System.out.println();
					System.out.println("Input the number of markrers choosen from each window/chromosome: ");
					randomSNPNumber=scanner.nextLine();
				}while(!isInteger(randomSNPNumber));
				
				if(choice.equals("1"))
					fastPhase(splitMode,randomSNPNumber);
				else if(choice.equals("2")) 
					beagle(splitMode,randomSNPNumber);
				else
					shapeit(splitMode,randomSNPNumber);
				
				haplotypeDiversity();
				
				do{
					System.out.println();
					menuOfHaplotypeSharing();
					choiceOfSharing=scanner.nextLine();
				}while(!(choiceOfSharing.equals("1")||choiceOfSharing.equals("2")));
				
				if(Integer.parseInt(choiceOfSharing)==1)
					distinctHaplotypeSharing();
				else
					allHaplotypeSharing();
				
			}
			else if(Integer.parseInt(choiceOfSoftware)==2){
				//String effectivePopulationSize;
				String numberOfIndividuals;
				String randomSNPNumber;
				do{
					System.out.println();
					System.out.println("Input the random snp number: ");
					randomSNPNumber=scanner.nextLine();
				}while(!isInteger(randomSNPNumber));
//				do{
//					System.out.println();
//					System.out.println("Input the effective population size: ");
//					effectivePopulationSize=scanner.nextLine();
//				}while(!isInteger(effectivePopulationSize));
				do{
					System.out.println();
					System.out.println("Input the number of individuals your input file contains: ");
					numberOfIndividuals=scanner.nextLine();
				}while(!isInteger(numberOfIndividuals));
				
				LDhat(Integer.parseInt(numberOfIndividuals),randomSNPNumber);
			}
			else if(Integer.parseInt(choiceOfSoftware)==3){
				
				do{
					System.out.println();
					menuOfMicrosat();
					choiceOfMicrosat=scanner.nextLine();
				}while(!(choiceOfMicrosat.equals("1")||choiceOfMicrosat.equals("2")));
				
				microsat();
				phylip();
			}
			else if(Integer.parseInt(choiceOfSoftware)==4){	
				String choice;
				String numberOfComponents;
				File inputReferenceFile;
				
				do{
					System.out.println();
					System.out.println("  1 PCA");
					System.out.println("  2 Structure");
//					System.out.println("  3 fineStructure");
					System.out.print("Input your choice: ");
					choice=scanner.nextLine();
				}while(!(choice.equals("1")||choice.equals("2")));
				
				do{
					System.out.println();
					System.out.print("Input the number of principal components to output: ");
					numberOfComponents=scanner.nextLine();
				}while(!isInteger(numberOfComponents));
				
				do{
					System.out.println();
					System.out.print("Input your property file: ");
					inputReferenceFile=new File(scanner.nextLine());
				}while(!(inputReferenceFile.exists()&&!inputReferenceFile.isDirectory()));
				
				if(choice.equals("1"))
				{
					smartPCA(Integer.parseInt(numberOfComponents));
					KendallStatistics(inputReferenceFile);
					TWStatistics();
				}				
				else if(choice.equals("2"))
					structure();
//				else
//					fineStructure();
			}
			
			else if(Integer.parseInt(choiceOfSoftware)==5){
				chromosomePainter();
			}
			else if(Integer.parseInt(choiceOfSoftware)==6){
				String choice;
				File mapFile;
				do{
					MenuOfPositiveSelection();
					choice=scanner.nextLine();
				}while(isCorrectChoiceForPositiveSelection(choice));
				
				do{
					System.out.println();
					System.out.print("Input your map file: ");
					mapFile=new File(scanner.nextLine());
				}while(!(mapFile.exists()&&!mapFile.isDirectory()));
				
				if(choice.equals("1"))
					theta();
				else if(choice.equals("2"))
					pi();
				else if(choice.equals("3"))
					tajimaD();
				else if(choice.equals("4"))
					fst();
				else if(choice.equals("5"))
				{
					String type;
					do{
						System.out.println();
						System.out.println("  1 Based on Haplotype");
						System.out.println("  2 Based on SNP");
						System.out.print("Input your choice: ");
						type=scanner.nextLine();
					}while(!(choice.equals("1")||choice.equals("2")));
					
					if(type.equals("1"))
						HaplotypeFisherExactTest();
					else
						SnpFisherExactTest();
				}
				else if(choice.equals("6"))
					iHS(mapFile);
				else if(choice.equals("7"))
					XPEhh(mapFile);
				else if(choice.equals("8"))
					EHH();
				else 
					CSS();	
			}
			else if(Integer.parseInt(choiceOfSoftware)==7){
				haplotypeAge();
			}
			
		}while(isContinue());	
		scanner.close();
	}
	
	
	public static void splitFiles(File file) throws FileNotFoundException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		Scanner infile=new Scanner(new FileReader(file));
		
		System.out.println("Splitting file...");
		
		//get chromosome id
		while(infile.hasNextLine())
		{
			String line=infile.nextLine();
			String[] s=line.split("	");
			//System.out.println(CID.size());
			//System.out.println(CID.contains(s[0]));
			if(CID.size()==0||!CID.contains(s[0]))
				CID.add(s[0]);
		}
		infile.close();
		
		
		try{
			PrintWriter outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/splitFile.sh"));
			for(int i=0;i<CID.size();i++)
			{	
				String commond="grep -w "+CID.get(i)+" "+genotypeInfile.toString()+" > "+workFile.toString()+"/genotypeInfile/"+CID.get(i)+".genotype";
				outputStream.println(commond);
				System.out.println(commond);
			}
			outputStream.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		try{
			Process process=Runtime.getRuntime().exec("sh "+workFile.toString()+"/splitFile.sh");
			Scanner output=new Scanner(process.getInputStream());
			while(output.hasNextLine())
				System.out.println(output.nextLine());
			output.close();
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		System.out.println("Splitting file is finished!");
	}
	
	public static void splitWindows(){
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/windowInformation");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		do{
			System.out.println();
			System.out.print("Please give the window length: ");
			windowLength=scanner.nextLine();
		}while(!isInteger(windowLength));
		
		String setOverlap;
		do{
			System.out.println();
			System.out.print("Set overlap between two adjacent windows?[Y/N]: ");
			setOverlap=scanner.nextLine();
		}while(!(setOverlap.equals("Y")||setOverlap.equals("N")));
		
		if(setOverlap.equals("Y"))
		{
			do{
				System.out.println();
				System.out.print("Please give overlap length: ");
				overlapLength=scanner.nextLine();
			}while(!isInteger(overlapLength));
			
			System.out.println();
			System.out.println("Splitting windows...");
			
			for(int i=0;i<CID.size();i++)
			{
				try{
					Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile/"+CID.get(i));
					new FastSplitSlidingNotFromZero(workFile.toString()+"/genotypeInfile/"+CID.get(i)+".genotype",workFile.toString()+"/genotypeInfile/"+CID.get(i),workFile.toString()+"/windowInformation", Integer.parseInt(windowLength),CID.get(i), Integer.parseInt(overlapLength));
				}catch(Exception e){
					System.out.println("shdio"+e.getMessage());
				}
			}
			
			System.out.println();
			System.out.println("Splitting windows is finished!");
		}
		else
		{
			System.out.println();
			System.out.println("Splitting windows...");
	
			for(int i=0;i<CID.size();i++)
			{
				try{
					Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile/"+CID.get(i));
					new SplitPositionBySnpNotFromZero(workFile.toString()+"/genotypeInfile/"+CID.get(i)+".genotype",Integer.parseInt(windowLength),CID.get(i),workFile.toString()+"/genotypeInfile/"+CID.get(i),workFile.toString()+"/windowInformation");
				}catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
			
			System.out.println();
			System.out.println("Splitting windows is finished!");
		}
	}
	
	public static void fastPhase(String splitMode,String randomSNPNumber) throws IOException{
		File file=new File(workFile.toString()+"/genotypeInfile");
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(new File(vcfInfile.toString()+".genotype"));
			
			splitWindows();
			file=new File(workFile.toString()+"/genotypeInfile");
		}
		
		
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile/RandomSNPData");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData");
			if(splitMode.equals("1"))
			{
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/FastPhase/chromosomeInput");
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/FastPhase/chromosomeOutput");
			}
			else
			{
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/FastPhase/windowsInput");
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/FastPhase/windowsOutput");
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		File[] files1=file.listFiles();
		
		if(splitMode.equals("1"))
		{
			for(int i=0;i<files1.length;i++)
			{
				if(!files1[i].isDirectory())
				{
					String path=files1[i].toString();
					if(path.endsWith(".genotype"))
					{
						RandomSNP randomSNP=new RandomSNP(path,workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData",Integer.parseInt(randomSNPNumber));
						randomSNP.getRandomNumber();
						randomSNP.productOutFile();
						
					}
				}
			}
			
			file=new File(workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".genotype"))
					new FastPHASEgeno(path,workFile.toString()+"/FastPhase/chromosomeInput/"+getName(path));
			}
	
			file=new File(workFile.toString()+"/FastPhase/chromosomeInput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".fastphase.inp"))
				{
					try{
						String commond="./sotware/./fastphase -K40 -u"+labelFile+" -o"+workFile.toString()+"/FastPhase/chromosomeOutput/"+getName(path)+" "+ path;
						System.out.println(commond);
						Process process = Runtime.getRuntime().exec(commond);
						Scanner output=new Scanner(process.getInputStream());
						while(output.hasNextLine())
							System.out.println(output.nextLine());
						output.close();
					}catch(Exception e){
						System.out.println("Exception"+e.getMessage());
					}
				}	
			}	
		}
		else
		{
			for(int i=0;i<files1.length;i++)
			{	
				if(validDirectory(files1[i].getName()))
				{
					File[] files2=files1[i].listFiles();
					for(int j=0;j<files2.length;j++)
					{
						String path=files2[j].toString();
						if(path.endsWith(".genotype"))
						{
							RandomSNP randomSNP=new RandomSNP(path,workFile.toString()+"/genotypeInfile/RandomSNPData",Integer.parseInt(randomSNPNumber));
							randomSNP.getRandomNumber();
							randomSNP.productOutFile();
						}
					}
				}	
			}
			
			file=new File(workFile.toString()+"/genotypeInfile/RandomSNPData");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".genotype"))
					new FastPHASEgeno(path,workFile.toString()+"/FastPhase/windowsInput/"+getName(path));
			}
			
			file=new File(workFile.toString()+"/FastPhase/windowsInput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".fastphase.inp"))
				{
					try{
						String commond="./software/./fastphase -K40 -u"+labelFile+" -o"+workFile.toString()+"/FastPhase/windowsOutput/"+getName(path)+" "+ path;
						System.out.println(commond);
						Process process = Runtime.getRuntime().exec(commond);
						Scanner output=new Scanner(process.getInputStream());
						while(output.hasNextLine())
							System.out.println(output.nextLine());
						output.close();
					}catch(Exception e){
						System.out.println("Exception"+e.getMessage());
					}
				}	
			}	
		}
	}
	
	public static void beagle(String splitMode,String randomSNPNumber) throws FileNotFoundException{
		File file=new File(workFile.toString()+"/genotypeInfile");
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(new File(vcfInfile.toString()+".genotype"));
			
			splitWindows();
			file=new File(workFile.toString()+"/genotypeInfile");
		}
		
		
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile/RandomSNPData");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData");
			if(splitMode.equals("1"))
			{
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Beagle/chromosomeInput");
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Beagle/chromosomeOutput");
				File temp=new File(workFile.toString()+"/FastPhase/chromosomeOutput");
				if(!temp.exists())
					process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/FastPhase/chromosomeOutput");
			}
			else
			{
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Beagle/windowsInput");
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Beagle/windowsOutput");
				File temp=new File(workFile.toString()+"/FastPhase/windowsOutput");
				if(!temp.exists())
					process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/FastPhase/windowsOutput");
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File[] files1=file.listFiles();
		
		if(splitMode.equals("1"))
		{
			for(int i=0;i<files1.length;i++)
			{
				if(!files1[i].isDirectory())
				{
					String path=files1[i].toString();
					if(path.endsWith(".genotype"))
					{
						RandomSNP randomSNP=new RandomSNP(path,workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData",Integer.parseInt(randomSNPNumber));
						randomSNP.getRandomNumber();
						randomSNP.productOutFile();
						
					}
				}
			}
			
			file=new File(workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".genotype"))
				{
					BeagleFormatChange beagleFormatChange=new BeagleFormatChange(path,workFile.toString()+"/Beagle/chromosomeInput");
					beagleFormatChange.mainFunction();				}
			}
			
			file=new File(workFile.toString()+"/Beagle/chromosomeInput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".bgl"))
				{
					try{
						String commond="./software/Beagle/java -Xmx<2000>m -jar beagle.jar unphased="+path+" missing=_ out="+workFile.toString()+"/Beagle/chromosomeOutput";
						System.out.println(commond);
						Process process = Runtime.getRuntime().exec(commond);
						Scanner output=new Scanner(process.getInputStream());
						while(output.hasNextLine())
							System.out.println(output.nextLine());
						output.close();
					}catch(Exception e){
						System.out.println("Exception"+e.getMessage());
					}
				}	
			}
			file=new File(workFile.toString()+"/Beagle/chromosomeOutput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".phased.gz"))
				{
					ChangeToFastPhase changeToFastPhase=new ChangeToFastPhase(path, workFile.toString()+"/FastPhase/chromosomeOutput");
					changeToFastPhase.mainFunction();
				}	
			}
		}
		else
		{
			for(int i=0;i<files1.length;i++)
			{	
				if(validDirectory(files1[i].getName()))
				{
					File[] files2=files1[i].listFiles();
					for(int j=0;j<files2.length;j++)
					{
						String path=files2[j].toString();
						if(path.endsWith(".genotype"))
						{
							RandomSNP randomSNP=new RandomSNP(path,workFile.toString()+"/genotypeInfile/RandomSNPData",Integer.parseInt(randomSNPNumber));
							randomSNP.getRandomNumber();
							randomSNP.productOutFile();
						}
					}
				}	
			}
			
			file=new File(workFile.toString()+"/genotypeInfile/RandomSNPData");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".genotype"))
				{
					BeagleFormatChange beagleFormatChange=new BeagleFormatChange(path,workFile.toString()+"/Beagle/windowsInput");
					beagleFormatChange.mainFunction();
				}
			}
			
			file=new File(workFile.toString()+"/Beagle/windowsInput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".bgl"))
				{
					try{
						String commond="./software/Beagle/java -Xmx<2000>m -jar beagle.jar unphased="+path+" missing=_ out="+workFile.toString()+"/Beagle/chromosomeOutput";
						System.out.println(commond);
						Process process = Runtime.getRuntime().exec(commond);
						Scanner output=new Scanner(process.getInputStream());
						while(output.hasNextLine())
							System.out.println(output.nextLine());
						output.close();
					}catch(Exception e){
						System.out.println("Exception"+e.getMessage());
					}
				}	
			}
			file=new File(workFile.toString()+"/Beagle/windowsOutput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".phased.gz"))
				{
					ChangeToFastPhase changeToFastPhase=new ChangeToFastPhase(path, workFile.toString()+"/FastPhase/windowsOutput");
					changeToFastPhase.mainFunction();
				}	
			}
		}
		
	}
	
	public static void shapeit(String splitMode,String randomSNPNumber) throws FileNotFoundException{
		File file=new File(workFile.toString()+"/genotypeInfile");
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(new File(vcfInfile.toString()+".genotype"));
			
			splitWindows();
			file=new File(workFile.toString()+"/genotypeInfile");
		}
		
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile/RandomSNPData");//RandomSNPData");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData");
			if(splitMode.equals("1"))
			{
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Shapeit/chromosomeInput");
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Shapeit/chromosomeOutput");
				File temp=new File(workFile.toString()+"/FastPhase/chromosomeOutput");
				if(!temp.exists())
					process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/FastPhase/chromosomeOutput");
			}
			else
			{
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Shapeit/windowsInput");
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Shapeit/windowsOutput");
				File temp=new File(workFile.toString()+"/FastPhase/windowsOutput");
				if(!temp.exists())
					process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/FastPhase/windowsOutput");
			}
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File[] files1=file.listFiles();
		
		if(splitMode.equals("1"))
		{
			for(int i=0;i<files1.length;i++)
			{
				if(!files1[i].isDirectory())
				{
					String path=files1[i].toString();
					if(path.endsWith(".genotype"))
					{
						RandomSNP randomSNP=new RandomSNP(path,workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData",Integer.parseInt(randomSNPNumber));
						randomSNP.getRandomNumber();
						randomSNP.productOutFile();						
					}
				}
			}
			
			file=new File(workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".genotype"))
				{
					FormatChangeForShapeit shapeitFormatChange=new FormatChangeForShapeit(path,workFile.toString()+"/Shapeit/chromosomeInput");
					shapeitFormatChange.mainFunction();
				}
			}
			
			file=new File(workFile.toString()+"/Shapeit/chromosomeInput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String pedFile=files1[i].toString();
				String mapFile;
				if(pedFile.endsWith(".ped"))
				{
					if(validPedFile(pedFile))
					{
						mapFile=pedFile.split(".ped")[0]+".map";
						try{
							String commond="./software/Shapeit/./shapeit --input-ped "+pedFile+" "+mapFile+" -M ./software/Shapeit/genetic_map.txt -O "+workFile.toString()+"/Shapeit/chromosomeOutput/"+getName(pedFile);
							System.out.println(commond);
							Process process = Runtime.getRuntime().exec(commond);
							Scanner output=new Scanner(process.getInputStream());
							while(output.hasNextLine())
								System.out.println(output.nextLine());
							output.close();
						}catch(Exception e){
							System.out.println("Exception"+e.getMessage());
						}
					}
				}	
			}
			
			file=new File(workFile.toString()+"/Shapeit/chromosomeOutput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".haps"))
				{
					OutputFormatChangeToFastPhase changeToFastPhase=new OutputFormatChangeToFastPhase(path, workFile.toString()+"/FastPhase/chromosomeOutput");
					changeToFastPhase.mainFunction();
				}	
			}
		}
		else
		{
			for(int i=0;i<files1.length;i++)
			{	
				if(validDirectory(files1[i].getName()))
				{
					File[] files2=files1[i].listFiles();
					for(int j=0;j<files2.length;j++)
					{
						String path=files2[j].toString();
						if(path.endsWith(".genotype"))
						{
							RandomSNP randomSNP=new RandomSNP(path,workFile.toString()+"/genotypeInfile/RandomSNPData",Integer.parseInt(randomSNPNumber));
							randomSNP.getRandomNumber();
							randomSNP.productOutFile();
						}
					}
				}	
			}
			
			file=new File(workFile.toString()+"/genotypeInfile/RandomSNPData");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".genotype"))
				{
					FormatChangeForShapeit formatChangeForShapeit=new FormatChangeForShapeit(path,workFile.toString()+"/Shapeit/windowsInput");
					formatChangeForShapeit.mainFunction();
				}
			}
			
			file=new File(workFile.toString()+"/Shapeit/windowsInput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String pedFile=files1[i].toString();
				String mapFile;
				if(pedFile.endsWith(".ped"))
				{
					if(validPedFile(pedFile))
					{
						mapFile=pedFile.split(".ped")[0]+".map";
						try{
							String commond="./software/Shapeit/./shapeit --input-ped "+pedFile+" "+mapFile+" -M ./software/Shapeit/genetic_map.txt -O "+workFile.toString()+"/Shapeit/windowsOutput/"+getName(pedFile);
							System.out.println(commond);
							Process process = Runtime.getRuntime().exec(commond);
							Scanner output=new Scanner(process.getInputStream());
							while(output.hasNextLine())
								System.out.println(output.nextLine());
							output.close();
						}catch(Exception e){
							System.out.println("Exception"+e.getMessage());
						}
					}					
				}	
			}
			
			file=new File(workFile.toString()+"/Shapeit/windowsOutput");
			files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				String path=files1[i].toString();
				if(path.endsWith(".haps"))
				{
					OutputFormatChangeToFastPhase changeToFastPhase=new OutputFormatChangeToFastPhase(path, workFile.toString()+"/FastPhase/windowsOutput");
					changeToFastPhase.mainFunction();
				}	
			}	
		}
		
		try{
			Process process=Runtime.getRuntime().exec("rm shapeit*");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void haplotypeDiversity() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/HaplotypeDiversity/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/HaplotypeDiversity/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		
		File file=new File(workFile.toString()+"/FastPhase/windowsOutput");;
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must run Haplotype Phasing first!");
			return;
		}
		if(validLabelFile(labelFile))
			file=new File(workFile.toString()+"/FastPhase/windowsOutput");
		else
		{
			GetCorrectInputFiles  getCorrectInputFiles=new GetCorrectInputFiles(workFile.toString()+"/FastPhase/windowsOutput",workFile.toString()+"/HaplotypeDiversity/input",labelFile.toString());
			getCorrectInputFiles.mainFunction();
			
			file=new File(workFile.toString()+"/HaplotypeDiversity/input");
			labelFile=new File(workFile.toString()+"/HaplotypeDiversity/input/label.txt");
		}
		
		File[] f=file.listFiles();
		
		for(int index1=0;index1<f.length;index1++)
		{
			String path=f[index1].toString();
			if(path.endsWith(".out"))
			{
				HaplotypeDiversity haplotypeDiversity=new HaplotypeDiversity(path,workFile.toString()+"/HaplotypeDiversity/output",labelFile.toString());
				haplotypeDiversity.storeInputToArray();
				haplotypeDiversity.findDistinctHaplotype();
			}
		}
	
		GetDiversityAverage getDiversityAverage=new GetDiversityAverage(workFile.toString()+"/HaplotypeDiversity/output",workFile.toString()+"/HaplotypeDiversity");
		getDiversityAverage.getResult();
		getDiversityAverage.printRsultToOutputFile();
	}
	
	private static boolean validLabelFile(File labelFile2) throws FileNotFoundException {
		// TODO Auto-generated method stub
		Hashtable<String,Integer> popID=new Hashtable<String,Integer>();
		Scanner input=new Scanner(new FileReader(labelFile2));
		
		while(input.hasNextLine())
		{
			String line=input.nextLine();
			if(!popID.containsKey(line))
				popID.put(line, 1);
			else
				popID.put(line, popID.get(line)+1);
		}
		
		Collection values= popID.values();
		Iterator it=values.iterator();
		Object temp=it.next();
		while( it.hasNext())
		{
			if(!it.next().equals(temp))
				return false;
		}
		
		return true;
	}


	public static void distinctHaplotypeSharing() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/HaplotypeSharing/DistinctHaplotypeSharing/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		
		File file=new File(workFile.toString()+"/HaplotypeDiversity/output");
		
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must run HaplotypeDiversity first!");
			return;
		}
		
		File[] f=file.listFiles();
		
		for(int index=0;index<f.length;index++)
		{
			String path=f[index].toString();
			DistinctHaplotypeSharing distinctHaplotypeSharing=new DistinctHaplotypeSharing(path,workFile.toString()+"/HaplotypeSharing/DistinctHaplotypeSharing/output");
			distinctHaplotypeSharing.storeInputToArray();
			distinctHaplotypeSharing.getResult();
			distinctHaplotypeSharing.printResultToOutputFile();
		}

		GetSharingAverage getSharingAverage=new GetSharingAverage(workFile.toString()+"/HaplotypeSharing/DistinctHaplotypeSharing/output",workFile.toString()+"/HaplotypeSharing/DistinctHaplotypeSharing/sharingResult_unique.txt");
		getSharingAverage.getResult();
		getSharingAverage.printRsultToOutputFile();
	}
	
	public static void allHaplotypeSharing() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/HaplotypeSharing/AllHaplotypeSharing/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/HaplotypeSharing/AllHaplotypeSharing/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File file=new File(workFile.toString()+"/FastPhase/windowsOutput");;
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must run Haplotype Phasing first!");
			return;
		}
		if(validLabelFile(labelFile))
			file=new File(workFile.toString()+"/FastPhase/windowsOutput");
		else
		{
			GetCorrectInputFiles  getCorrectInputFiles=new GetCorrectInputFiles(workFile.toString()+"/FastPhase/windowsOutput",workFile.toString()+"/HaplotypeSharing/AllHaplotypeSharing/input",labelFile.toString());
			getCorrectInputFiles.mainFunction();
			
			file=new File(workFile.toString()+"/HaplotypeSharing/AllHaplotypeSharing/input");
			labelFile=new File(workFile.toString()+"/HaplotypeSharing/AllHaplotypeSharing/input/label.txt");
		}
		File[] f=file.listFiles();
		
		for(int index1=0;index1<f.length;index1++)
		{
			String path=f[index1].toString();
			if(path.endsWith(".out"))
			{
				AllHaplotypeSharing allHaplotypeSharing=new AllHaplotypeSharing(path,workFile.toString()+"/HaplotypeSharing/AllHaplotypeSharing/output",labelFile.toString());
				allHaplotypeSharing.storeInputToArray();
				allHaplotypeSharing.getResult();
				allHaplotypeSharing.printResultToOutputFile();
			}
		}
	
		GetSharingAverage getSharingAverage=new GetSharingAverage(workFile.toString()+"/HaplotypeSharing/AllHaplotypeSharing/output",workFile.toString()+"/HaplotypeSharing/AllHaplotypeSharing/sharingResult_all.txt");
		getSharingAverage.getResult();
		getSharingAverage.printRsultToOutputFile();
	}
	
	public static void microsat() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Microsat/HaplotypeInput");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Microsat/HaplotypeOutput");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Microsat/HaplotypeConfiguration");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Microsat/SnpInput");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Microsat/SnpOutput");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Microsat/SnpConfiguration");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		if(choiceOfMicrosat.equals("1"))
		{
			File file=new File(workFile.toString()+"/FastPhase/windowsOutput");
			if(!file.exists())
			{
				System.out.println();
				System.out.println("You must run Haplotype Phasing first!");
				return;
			}
			File[] f=file.listFiles();
			
			for(int index1=0;index1<f.length;index1++)
			{
				String path=f[index1].toString();
				if(path.endsWith(".out"))
				{
					HaplotypeFormatChangeForMicrosat formatChangeForMicrosat=new HaplotypeFormatChangeForMicrosat(path,workFile.toString()+"/Microsat/HaplotypeInput",labelFile.toString());
					formatChangeForMicrosat.storeInputToArray();
					formatChangeForMicrosat.changeFormat();
				}
			}
			
			file=new File(workFile.toString()+"/Microsat/HaplotypeInput");
			f=file.listFiles();
			try{
				String commond="cat ";
				for(int i=0;i<f.length;i++)
					commond+=f[i].toString()+" ";
				commond+="> "+workFile.toString()+"/Microsat/HaplotypeInput/population";
				
				PrintWriter outputStream=new PrintWriter(workFile.toString()+"/Microsat/HaplotypeMerge.sh");
				outputStream.println(commond);
				outputStream.close();
				Process process = Runtime.getRuntime().exec ("sh "+workFile.toString()+"/Microsat/HaplotypeMerge.sh");
				Scanner output=new Scanner(process.getInputStream());
				while(output.hasNextLine())
					System.out.println(output.nextLine());
				output.close();
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}
		else
		{
			File file=new File(workFile.toString()+"/genotypeInfile");
			
			if(!file.exists())
			{
				System.out.println();
				System.out.println("You must split file and windows first!");
				if(choiceOfInfileFormat.equals("1"))
					splitFiles(genotypeInfile);
				else 
					splitFiles(vcfInfile);
				
				splitWindows();
				file=new File(workFile.toString()+"/genotypeInfile");
			}
			File[] files1=file.listFiles();
			
			for(int i=0;i<files1.length;i++)
			{
				if(files1[i].isDirectory()&&validDirectory(files1[i].getName()))
				{
					File[] files2=files1[i].listFiles();
					for(int j=0;j<files2.length;j++)
					{
						String path=files2[j].toString();
						if(path.endsWith(".genotype"))
						{
							SNPFormaChangeForMicrosat snpFormaChangeForMicrosat=new SNPFormaChangeForMicrosat(path,workFile.toString()+"/Microsat/SnpInput",labelFile.toString());
							snpFormaChangeForMicrosat.storeInputToArray();
							snpFormaChangeForMicrosat.formatChange();
						}
					}			
				}
			}
			
			file=new File(workFile.toString()+"/Microsat/SnpInput");
			files1=file.listFiles();
			try{
				String commond="cat ";
				for(int i=0;i<files1.length;i++)
					commond+=files1[i].toString()+" ";
				commond+="> "+workFile.toString()+"/Microsat/SnpInput/population";
				
				PrintWriter outputStream=new PrintWriter(workFile.toString()+"/Microsat/snpMerge.sh");
				outputStream.println(commond);
				outputStream.close();
				Process process = Runtime.getRuntime().exec ("sh "+workFile.toString()+"/Microsat/snpMerge.sh");
				Scanner output=new Scanner(process.getInputStream());
				while(output.hasNextLine())
					System.out.println(output.nextLine());
				output.close();
			}catch(Exception e){
				System.out.println(e.getMessage());
			}
		}		
		
		File file;
		if(choiceOfMicrosat.equals("1"))
			file=new File(workFile.toString()+"/Microsat/HaplotypeInput");
		else
			file=new File(workFile.toString()+"/Microsat/SnpInput");
		File[] f=file.listFiles();
		
		for(int index=0;index<f.length;index++)
		{
			String path=f[index].toString();
			try{
				PrintWriter outputStream;
				if(choiceOfMicrosat.equals("1"))
					outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/Microsat/HaplotypeConfiguration/"+getName(path)+"configuration.txt"));
				else
					outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/Microsat/SnpConfiguration/"+getName(path)+"configuration.txt"));
				outputStream.println("A");
				outputStream.println("D");
				outputStream.println("P");
				outputStream.println("M");
				outputStream.println("I");
				outputStream.println(path);
				outputStream.println("O");
				if(choiceOfMicrosat.equals("1"))
					outputStream.println(workFile.toString()+"/Microsat/HaplotypeOutput/"+getName(path)+".txt");
				else
					outputStream.println(workFile.toString()+"/Microsat/SnpOutput/"+getName(path)+".txt");
				outputStream.println("!");
				outputStream.close();
			}catch(Exception e){
				System.out.println("Exception"+e.getMessage());
			}
		}
		
		try{
			Process process = Runtime.getRuntime().exec ("touch "+workFile.toString()+"/Microsat/HaplotypeMicrosat.sh");
			process=Runtime.getRuntime().exec ("touch "+workFile.toString()+"/Microsat/SnpMicrosat.sh");
			
			PrintWriter outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/Microsat/HaplotypeMicrosat.sh"));
			outputStream.println("#!/bin/bash");
			outputStream.println("for file in "+workFile.toString()+"/Microsat/HaplotypeConfiguration/*");
			outputStream.println("do");
			outputStream.println("./software/microsat < $file;");
			outputStream.println("done");
			outputStream.close();
			
			outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/Microsat/SnpMicrosat.sh"));
			outputStream.println("#!/bin/bash");
			outputStream.println("for file in "+workFile.toString()+"/Microsat/SnpConfiguration/*");
			outputStream.println("do");
			outputStream.println("./software/microsat < $file;");
			outputStream.println("done");
			outputStream.close();
			
			if(choiceOfMicrosat.equals("1"))
				process=Runtime.getRuntime().exec("sh "+workFile.toString()+"/Microsat/HaplotypeMicrosat.sh");
			else
				process=Runtime.getRuntime().exec("sh "+workFile.toString()+"/Microsat/SnpMicrosat.sh");
			Scanner output=new Scanner(process.getInputStream());
			while(output.hasNextLine())
				System.out.println(output.nextLine());
			output.close();
			
		}catch (IOException e){
			System.out.println("IOException " + e.getMessage());
		}catch (Exception e){
			System.out.println("Exception " + e.getMessage());
		}
	}
	
	public static void phylip() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Phylip/HaplotypeConfiguration");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Phylip/HaplotypeOutput");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Phylip/SnpConfiguration");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Phylip/SnpOutput");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File file;
		
		if(choiceOfMicrosat.equals("1"))
			file=new File(workFile.toString()+"/Microsat/HaplotypeOutput");
		else
			file=new File(workFile.toString()+"/Microsat/SnpOutput");
		
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must run Microsat first!");
			microsat();
			if(choiceOfMicrosat.equals("1"))
				file=new File(workFile.toString()+"/Microsat/HaplotypeOutput");
			else
				file=new File(workFile.toString()+"/Microsat/SnpOutput");
		}
		File[] f=file.listFiles();
		
		for(int index=0;index<f.length;index++)
		{
			String path=f[index].toString();
			try{
				PrintWriter outputStream;
				if(choiceOfMicrosat.equals("1"))
					outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/Phylip/HaplotypeConfiguration/"+getName(path)+"configuration.txt"));
				else
					outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/Phylip/SnpConfiguration/"+getName(path)+"configuration.txt"));
				outputStream.println(path);
				outputStream.println("F");
				if(choiceOfMicrosat.equals("1"))
					outputStream.println(workFile.toString()+"/Phylip/HaplotypeOutput/"+getWindowName(path)+".outfile");
				else
					outputStream.println(workFile.toString()+"/Phylip/SnpOutput/"+getWindowName(path)+".outfile");
				outputStream.println("L");
				outputStream.println("Y");
				outputStream.println("F");
				if(choiceOfMicrosat.equals("1"))
					outputStream.println(workFile.toString()+"/Phylip/HaplotypeOutput/"+getWindowName(path)+".outtree");
				else
					outputStream.println(workFile.toString()+"/Phylip/SnpOutput/"+getWindowName(path)+".outtree");
				outputStream.close();
			}catch(Exception e){
				System.out.println("Exception"+e.getMessage());
			}
		}				
			
		try{
			Process process = Runtime.getRuntime().exec ("touch "+workFile.toString()+"/Phylip/HaplotypePhylip.sh");
			process=Runtime.getRuntime().exec ("touch "+workFile.toString()+"/Phylip/SnpPhylip.sh");
			
			PrintWriter outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/Phylip/HaplotypePhylip.sh"));
			outputStream.println("#!/bin/bash");
			outputStream.println("for file in "+workFile.toString()+"/Phylip/HaplotypeConfiguration/*");
			outputStream.println("do");
			outputStream.println("./software/phylip/exe/neighbor < $file;");
			outputStream.println("done");
			outputStream.close();
			
			outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/Phylip/SnpPhylip.sh"));
			outputStream.println("#!/bin/bash");
			outputStream.println("for file in "+workFile.toString()+"/Phylip/SnpConfiguration/*");
			outputStream.println("do");
			outputStream.println("./software/phylip/exe/neighbor < $file;");
			outputStream.println("done");
			outputStream.close();
			
			if(choiceOfMicrosat.equals("1"))
				process=Runtime.getRuntime().exec("sh "+workFile.toString()+"/Phylip/HaplotypePhylip.sh");
			else
				process=Runtime.getRuntime().exec("sh "+workFile.toString()+"/Phylip/SnpPhylip.sh");
			
			Scanner output=new Scanner(process.getInputStream());
			while(output.hasNextLine())
				System.out.println(output.nextLine());
			output.close();
		}catch (IOException e){
			System.out.println("IOException " + e.getMessage());
		}catch (Exception e){
			System.out.println("Exception " + e.getMessage());
		}	
	}
	
	public static void smartPCA(int numberOfComponents) throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/SmartPCA/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/SmartPCA/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		if(choiceOfInfileFormat.equals("1"))
			new SmartPCAgeno(genotypeInfile.toString(),labelFile.toString(),workFile.toString()+"/SmartPCA/input/"+getName(genotypeInfile.toString())+".snp",workFile.toString()+"/SmartPCA/input/"+getName(genotypeInfile.toString())+".geno",workFile.toString()+"/SmartPCA/input/"+getName(genotypeInfile.toString())+".ind");
		else
			new SmartPCAgeno(workFile.toString()+"/"+getName(vcfInfile.toString())+".genotype",labelFile.toString(),workFile.toString()+"/SmartPCA/input/"+getName(vcfInfile.toString())+".genotype.snp",workFile.toString()+"/SmartPCA/input/"+getName(vcfInfile.toString())+".genotype.geno",workFile.toString()+"/SmartPCA/input/"+getName(vcfInfile.toString())+".genotype.ind");
		try{
			String commond;
			if(choiceOfInfileFormat.equals("1"))
			{
				commond="./software/EIG/bin/./smartpca.perl -i ";
				commond+=workFile.toString()+"/SmartPCA/input/"+getName(genotypeInfile.toString())+".geno";
				commond+=" -a "+workFile.toString()+"/SmartPCA/input/"+getName(genotypeInfile.toString())+".snp";
				commond+=" -b "+workFile.toString()+"/SmartPCA/input/"+getName(genotypeInfile.toString())+".ind";
				commond+=" -k 2 -o "+workFile.toString()+"/SmartPCA/output/"+getName(genotypeInfile.toString())+".pca";
				commond+=" -p "+workFile.toString()+"/SmartPCA/output/"+getName(genotypeInfile.toString())+".plot";
				commond+=" -e "+workFile.toString()+"/SmartPCA/output/"+getName(genotypeInfile.toString())+".eval";
				commond+=" -l "+workFile.toString()+"/SmartPCA/output/"+getName(genotypeInfile.toString())+".log -m 5  -t "+numberOfComponents+"  -s 6.0";
			}
			else
			{
				commond="./software/EIG/bin/./smartpca.perl -i ";
				commond+=workFile.toString()+"/SmartPCA/input/"+getName(vcfInfile.toString())+".genotype.geno";
				commond+=" -a "+workFile.toString()+"/SmartPCA/input/"+getName(vcfInfile.toString())+".genotype.snp";
				commond+=" -b "+workFile.toString()+"/SmartPCA/input/"+getName(vcfInfile.toString())+".genotype.ind";
				commond+=" -k 2 -o "+workFile.toString()+"/SmartPCA/output/"+getName(vcfInfile.toString())+".pca";
				commond+=" -p "+workFile.toString()+"/SmartPCA/output/"+getName(vcfInfile.toString())+".plot";
				commond+=" -e "+workFile.toString()+"/SmartPCA/output/"+getName(vcfInfile.toString())+".eval";
				commond+=" -l "+workFile.toString()+"/SmartPCA/output/"+getName(vcfInfile.toString())+".log -m 5  -t "+numberOfComponents+"  -s 6.0";
			}
			
			System.out.println(commond);
			Process process = Runtime.getRuntime().exec (commond);
			Scanner input=new Scanner(process.getInputStream());
			String line;
			while (input.hasNextLine())
			{
				line=input.nextLine();
				System.out.println(line);
			}
			
			if(choiceOfInfileFormat.equals("1"))
				process=Runtime.getRuntime().exec("ps2pdf "+workFile.toString()+"/SmartPCA/output/"+getName(genotypeInfile.toString())+".plot.ps "+workFile.toString()+"/SmartPCA/output/"+getName(genotypeInfile.toString())+".plot.pdf");
			else
				process=Runtime.getRuntime().exec("ps2pdf "+workFile.toString()+"/SmartPCA/output/"+getName(vcfInfile.toString())+".plot.ps "+workFile.toString()+"/SmartPCA/output/"+getName(vcfInfile.toString())+".plot.pdf");
			
			input=new Scanner(process.getInputStream());
			while (input.hasNextLine())
			{
				line=input.nextLine();
				System.out.println(line);
			}
			input.close();
		}catch (IOException e){
			System.out.println("IOException " + e.getMessage());
		}catch (Exception e){
			System.out.println("Exception " + e.getMessage());
		}
		
		try{
			Process process=Runtime.getRuntime().exec("rm *.pdf");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/SmartPCA/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void structure() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Structure/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/Structure/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File subpopulationFile;

		do{
			System.out.println();
			System.out.print("Input your subpopulation file: ");
			subpopulationFile=new File(scanner.nextLine());
		}while(!(subpopulationFile.exists()&&!subpopulationFile.isDirectory()));
		
		if(choiceOfInfileFormat.equals("1"))
			new StructureGeno(genotypeInfile.toString(),workFile.toString()+"/Structure/input/"+getName(genotypeInfile.toString()),workFile.toString()+"/Structure/output/"+getName(genotypeInfile.toString()),"./",subpopulationFile.toString());
		else
			new StructureGeno(workFile.toString()+"/"+getName(vcfInfile.toString())+".genotype",workFile.toString()+"/Structure/input/"+getName(vcfInfile.toString()),workFile.toString()+"/Structure/output/"+getName(vcfInfile.toString()),"./",subpopulationFile.toString());
		try{
			Process process = Runtime.getRuntime().exec("./software/structure/./structure2.3.4");
			
			Scanner output=new Scanner(process.getInputStream());
			while(output.hasNextLine())
				System.out.println(output.nextLine());
			output.close();
		}catch (Exception e){
			System.out.println("Exception " + e.getMessage());
		}
	}
	
	public static void fst() throws FileNotFoundException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/Fst/output");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/Fst/GenoInformation");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/Fst/FstNotation/output");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/Fst/SignificantFst/output");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/Fst/GenoStatistics/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/Fst/GenoStatistics/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File genoInformationFile;
		File infile=new File(workFile.toString()+"/genotypeInfile");
		if(!infile.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(vcfInfile);
			
			splitWindows();
			infile=new File(workFile.toString()+"/genotypeInfile");
		}
		File[] files=infile.listFiles();
				
		for(int index=0;index<files.length;index++)
		{
			if(!files[index].isDirectory())
			{
				String path=files[index].toString();
				if(path.endsWith(".genotype"))
				{
					Fst fst=new Fst(path,workFile.toString()+"/PositiveSelection/Fst/output",labelFile.toString());
					fst.StoreInputAndGetFst();
				}
			}
		}
	
//		do{
//			System.out.print("Input your gene information file: ");
//			genoInformationFile=new File(scanner.nextLine());
//		}while(!(genoInformationFile.exists()&&!genoInformationFile.isDirectory()));
//		
//		
//		try{
//			PrintWriter outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/PositiveSelection/Fst/splitFile.sh"));
//			for(int i=0;i<CID.size();i++)
//			{	
//				String commond="grep -w "+CID.get(i)+" "+genoInformationFile.toString()+" > "+workFile.toString()+"/PositiveSelection/Fst/GenoInformation/"+CID.get(i)+".genoInformation";
//				outputStream.println(commond);
//				System.out.println(commond);
//			}
//			outputStream.close();
//		}catch(Exception e){
//			System.out.println(e.getMessage());
//		}
//		
//		try{
//			Process process=Runtime.getRuntime().exec("sh "+workFile.toString()+"/PositiveSelection/Fst/splitFile.sh");
//			Scanner output=new Scanner(process.getInputStream());
//			while(output.hasNextLine())
//				System.out.println(output.nextLine());
//			output.close();
//		}catch(Exception e){
//			System.out.println(e.getMessage());
//		}
//		
//		infile=new File(workFile.toString()+"/PositiveSelection/Fst/output");
//		files=infile.listFiles();
//		
//		for(int index=0;index<files.length;index++)
//		{
//			String path=files[index].toString();
//			String id=((getName(path)).split("\\."))[0];
//			FstNotation fstNotation=new FstNotation(path,workFile.toString()+"/PositiveSelection/Fst/FstNotation/output",workFile.toString()+"/PositiveSelection/Fst/GenoInformation/"+id+".genoInformation");
//			fstNotation.storeFstMap();
//			fstNotation.genoMatch();
//			fstNotation.disPlayResult();
//		}
//		
//		infile=new File(workFile.toString()+"/PositiveSelection/Fst/output");
//		SignificantFst significantFst=new SignificantFst(infile.toString(),workFile.toString()+"/PositiveSelection/Fst/SignificantFst/output",24);
//		significantFst.mainFunction();
//		
//		for(int i=0;i<CID.size();i++)
//		{
//			Genostatistics genostatistics=new Genostatistics(workFile.toString()+"/PositiveSelection/Fst/FstNotation/output/"+CID.get(i)+".genotype.fst.FstNotation",workFile.toString()+"/PositiveSelection/Fst/SignificantFst/output/"+CID.get(i)+".significantFst",workFile.toString()+"/PositiveSelection/Fst/GenoStatistics/input/"+CID.get(i));
//			genostatistics.mainFunction();
//		}
//		
//		GenostatisticsResult genostatisticsResult=new GenostatisticsResult(workFile.toString()+"/PositiveSelection/Fst/GenoStatistics/input",workFile.toString()+"/PositiveSelection/Fst/GenoStatistics/output");
//		genostatisticsResult.mainFunction();
	}
	
	public static void theta() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/Theta/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	
		File file=new File(workFile.toString()+"/genotypeInfile");
		
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(vcfInfile);
			
			splitWindows();
			file=new File(workFile.toString()+"/genotypeInfile");
		}
		File[] files1=file.listFiles();
		
		for(int i=0;i<files1.length;i++)
		{
			if(files1[i].isDirectory()&&validDirectory(files1[i].getName()))
			{
				String path=files1[i].toString();
				new RunSnpWindowsTheta(path, workFile.toString()+"/PositiveSelection/Theta/output", labelFile.toString(), Integer.parseInt(windowLength));
			}
		}		
	}
	
	public static void pi() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/PI/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File file=new File(workFile.toString()+"/genotypeInfile");
		
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(vcfInfile);
			
			splitWindows();
			file=new File(workFile.toString()+"/genotypeInfile");
		}
		File[] files1=file.listFiles();
		
		for(int i=0;i<files1.length;i++)
		{
			if(files1[i].isDirectory()&&validDirectory(files1[i].getName()))
			{
				String path=files1[i].toString();
				new RunSnpWindowsPi(path, workFile.toString()+"/PositiveSelection/PI/output", labelFile.toString(), Integer.parseInt(windowLength));
			}
		}		
	}
	
	public static void tajimaD() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/TajimaD/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File file=new File(workFile.toString()+"/genotypeInfile");
		
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(vcfInfile);
			
			splitWindows();
			file=new File(workFile.toString()+"/genotypeInfile");
		}
		File[] files1=file.listFiles();
		
		for(int i=0;i<files1.length;i++)
		{
			if(files1[i].isDirectory()&&validDirectory(files1[i].getName()))
			{
				String path=files1[i].toString();
				System.out.println(path);
				new TajimaD(path,workFile.toString()+"/PositiveSelection/TajimaD/output", labelFile.toString(), Integer.parseInt(windowLength),true);
			}
		}		
	}
	
	public static void HaplotypeFisherExactTest() throws FileNotFoundException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/FisherExactTest/Haplotype");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File infile=new File(workFile.toString()+"/FastPhase/windowsOutput");
		if(!infile.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(vcfInfile);
			
			splitWindows();
			infile=new File(workFile.toString()+"/genotypeInfile");
		}
		
		HaplotypeFisherExactTest haplotypeFisherExactTest=new HaplotypeFisherExactTest(infile.toString(), workFile.toString()+"/PositiveSelection/FisherExactTest/Haplotype", labelFile.toString(), workFile.toString()+"/windowInformation");
		haplotypeFisherExactTest.mainFunction();
	}
	
	public static void iHS(File mapFile) throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/iHS/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/iHS/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File infile=new File(workFile.toString()+"/FastPhase/chromosomeOutput");
		String popName;
		
		if(!infile.exists())
		{
			System.out.println();
			System.out.println("You must run Haplotype Phasing first!");
			return;
		}
	
		File[] haplotypeFile=infile.listFiles();
		
		for(int i=0;i<haplotypeFile.length;i++)
		{
			String path=haplotypeFile[i].toString();
			if(path.endsWith(".out"))
			{
				File genotypeFile=new File(workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData/"+getName(path).split("\\.")[0]+".genotype");
				GetRemRate getRemRate=new GetRemRate(genotypeFile, workFile.toString()+"/PositiveSelection/iHS/input" , mapFile.toString());
				getRemRate.mainFunction();
				ihsFormatChange ihsFormatChange=new ihsFormatChange(labelFile.toString(), workFile.toString()+"/PositiveSelection/iHS/input/"+genotypeFile.getName()+".remrate", path, workFile.toString()+"/PositiveSelection/iHS/input");
				ihsFormatChange.mainFunction();
			}
		}
		
	
		System.out.println();
		System.out.print("Input the population name you want to do ihs: ");
		popName=scanner.nextLine();
		
		infile=new File(workFile.toString()+"/PositiveSelection/iHS/input");
		File[] inputFile=infile.listFiles();
		
		for(int i=0;i<inputFile.length;i++)
		{
			String path=inputFile[i].toString();
			if(path.endsWith(".ihsmap"))
			{
				String hapFile=path.split(".ihsmap")[0]+"_"+popName+".ihshap";
				System.out.println(hapFile);
				System.out.println(path);
				String commond="./software/ihs/./ihs "+path+" "+hapFile;
				System.out.println(commond);
				try{
					Process process=Runtime.getRuntime().exec(commond);
					Scanner input=new Scanner(process.getInputStream());
					PrintWriter outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/PositiveSelection/iHS/output/"+getName(path.split(".ihsmap")[0])+".ihsout.txt"));
					while(input.hasNextLine())
					{
						String line=input.nextLine();
						outputStream.println(line);
						System.out.println(line);
					}
					input.close();
				}catch(Exception e){
					System.out.println("Exception"+e.getMessage());
				}
			}
		}
	}
	
	public static void XPEhh(File mapFile) throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/XPEHH/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/XPEHH/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File infile=new File(workFile.toString()+"/FastPhase/chromosomeOutput");
		String popName1, popName2;
		
		if(!infile.exists())
		{
			System.out.println();
			System.out.println("You must run Haplotype Phasing first!");
			return;
		}
		
	
		File[] haplotypeFile=infile.listFiles();
		
		for(int i=0;i<haplotypeFile.length;i++)
		{
			String path=haplotypeFile[i].toString();
			if(path.endsWith(".out"))
			{
				File genotypeFile=new File(workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData/"+getName(path).split("\\.")[0]+".genotype");
				GetRemRate getRemRate=new GetRemRate(genotypeFile, workFile.toString()+"/PositiveSelection/XPEHH/input" , mapFile.toString());
				getRemRate.mainFunction();
				ihsFormatChange ihsFormatChange=new ihsFormatChange(labelFile.toString(), workFile.toString()+"/PositiveSelection/XPEHH/input/"+genotypeFile.getName()+".remrate", path, workFile.toString()+"/PositiveSelection/XPEHH/input");
				ihsFormatChange.mainFunction();
			}
		}
		
	
		System.out.println();
		System.out.print("Input the first population name you want to do XPEHH: ");
		popName1=scanner.nextLine();
		
		System.out.println();
		System.out.print("Input the second population name you want to do XPEHH: ");
		popName2=scanner.nextLine();
		
		infile=new File(workFile.toString()+"/PositiveSelection/XPEHH/input");
		File[] inputFile=infile.listFiles();
		
		for(int i=0;i<inputFile.length;i++)
		{
			String path=inputFile[i].toString();
			if(path.endsWith(".ihsmap"))
			{
				String hapFile1=path.split(".ihsmap")[0]+"_"+popName1+".ihshap";
				String hapFile2=path.split(".ihsmap")[0]+"_"+popName2+".ihshap";
				String commond="./software/xpehh/./xpehh -m "+path+" -h "+hapFile1+" "+hapFile2;
				
				try{
					Process process=Runtime.getRuntime().exec(commond);
					Scanner input=new Scanner(process.getInputStream());
					PrintWriter outputStream=new PrintWriter(new FileWriter(workFile.toString()+"/PositiveSelection/XPEHH/output/"+getName(path.split(".ihsmap")[0])+".XPEHHout.txt"));
					while(input.hasNextLine())
					{
						String line=input.nextLine();
						outputStream.println(line);
						System.out.println(line);
					}
					outputStream.close();
					input.close();
				}catch(Exception e){
					System.out.println("Exception"+e.getMessage());
				}
			}
		}
		
	}
	public static void SnpFisherExactTest() throws FileNotFoundException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/FisherExactTest/SNP");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File file=new File(workFile.toString()+"/genotypeInfile");
		
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(vcfInfile);
			
			splitWindows();
			file=new File(workFile.toString()+"/genotypeInfile");
		}
		File[] files1=file.listFiles();
		
		
		for(int i=0;i<files1.length;i++)
		{
			if(files1[i].isDirectory()&&validDirectory(files1[i].toString()))
			{
				File[] files2=files1[i].listFiles();
				for(int j=0;j<files2.length;j++)
				{
					String path=files2[j].toString();
					if(path.endsWith(".genotype"))
					{
						SnpFisherExactTest snpFisherExactTest=new SnpFisherExactTest(path, workFile.toString()+"/PositiveSelection/FisherExactTest/SNP", labelFile.toString());
						snpFisherExactTest.mainFunction();
					}
				}
			}
		}
	}
	
	public static void EHH() throws NumberFormatException, IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/EHH/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/EHH/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		File infile=new File(workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData");
		
//		String startPosition;
//		String endPosition;
		String cutOffValue;
		
		if(!infile.exists())
		{
			System.out.println();
			System.out.println("You must run Haplotype Phasing first!");
			return;
		}
		File[] files=infile.listFiles();
		
//		do{
//			System.out.println();
//			System.out.print("Input the start position: ");
//			startPosition=scanner.nextLine();
//		}while(!(isInteger(startPosition)));
//		
//		do{
//			System.out.println();
//			System.out.print("Input the end position: ");
//			endPosition=scanner.nextLine();
//		}while(!(isInteger(endPosition)));
		
		do{
			System.out.println();
			System.out.print("Input the EHH cutoff value: ");
			cutOffValue=scanner.nextLine();
		}while(!(isDouble(cutOffValue)));
		
		for(int i=0;i<files.length;i++)
		{
			String path=files[i].getPath();
			
			//new PositiveSelection.EHH(Integer.parseInt(startPosition), Integer.parseInt(endPosition), path, workFile.toString()+"/FastPhase/chromosomeOutput/"+getName(path)+".ped.haps.switch.out", workFile.toString()+"/PositiveSelection/EHH/output/"+genotypeInfile.getName()+".error.txt", workFile.toString()+"/PositiveSelection/EHH/output/"+genotypeInfile.getName()+".EHH.txt", Double.parseDouble(cutOffValue));
			new PositiveSelection.EHH(path, workFile.toString()+"/FastPhase/chromosomeOutput/"+getName(path)+".ped.haps.switch.out", workFile.toString()+"/PositiveSelection/EHH/output/"+genotypeInfile.getName()+".error.txt", workFile.toString()+"/PositiveSelection/EHH/output/"+genotypeInfile.getName()+".EHH.txt", Double.parseDouble(cutOffValue));

		}
	}
	public static void CSS() throws NumberFormatException, IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/CSS/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/CSS/output");
			for(int i=0;i<CID.size();i++)
				process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i));
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File file;
		File[] files;
		String firstTeamNumber;
		String secondTeamNumber;
		file=new File(workFile.toString()+"/genotypeInfile");
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must split file and windows first!");
			if(choiceOfInfileFormat.equals("1"))
				splitFiles(genotypeInfile);
			else 
				splitFiles(vcfInfile);
			
			splitWindows();
			file=new File(workFile.toString()+"/genotypeInfile");
		}
		
		for(int i=0;i<CID.size();i++)
		{
			file=new File(workFile.toString()+"/genotypeInfile/"+CID.get(i));
			files=file.listFiles();
			
			for(int j=0;j<files.length;j++)
			{
				String path=files[j].toString();
				if(path.endsWith(".genotype"))
				{
					System.out.println(path);
					new SmartPCAgeno(path,labelFile.toString(),workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".snp",workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".geno",workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".ind");
					System.out.println("good");
					try{
						String commond;
						commond="./software/EIG/bin/./smartpca.perl -i ";
						commond+=workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".geno";
						commond+=" -a "+workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".snp";
						commond+=" -b "+workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".ind";
						commond+=" -k 2 -o "+workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".pca";
						commond+=" -p "+workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".plot";
						commond+=" -e "+workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".eval";
						commond+=" -l "+workFile.toString()+"/PositiveSelection/CSS/input/"+CID.get(i)+"/"+getName(path)+".log -m 5  -t 2  -s 6.0";
						
						Process process = Runtime.getRuntime().exec (commond);
						Scanner input=new Scanner(process.getInputStream());
						String line;
						while (input.hasNextLine())
						{
							line=input.nextLine();
							System.out.println(line);
						}
						input.close();
						process=Runtime.getRuntime().exec ("rm *.pdf");
					}catch (IOException e){
						System.out.println("IOException " + e.getMessage());
					}catch (Exception e){
						System.out.println("Exception " + e.getMessage());
					}
				}
			}
		}
		
		
		do{
			System.out.println();
			System.out.print("Please give the first population name: ");
			firstTeamNumber=scanner.nextLine();
		}while(!isInteger(firstTeamNumber));
		
		do{
			System.out.println();
			System.out.print("Please give the second population names: ");
			secondTeamNumber=scanner.nextLine();
		}while(!isInteger(secondTeamNumber));
		
		file=new File(workFile.toString()+"/PositiveSelection/CSS/input");
		files=file.listFiles();
		
		for(int i=0;i<files.length;i++)
		{
			if(files[i].isDirectory())
			{
				String path=files[i].toString();
				new CSS(path,workFile.toString()+"/PositiveSelection/CSS/output/"+getName(path), labelFile.toString(), Integer.parseInt(firstTeamNumber), Integer.parseInt(secondTeamNumber), Integer.parseInt(windowLength));
			}
		}
	}
	
	public static void LDhat(int numberOfIndividuals, String randomSNPNumber) throws NumberFormatException, IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/LDhat/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/LDhat/RandomChromosomeSNPData");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/LDhat/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		if(choiceOfInfileFormat.equals("1"))
		{	
			RandomSNP randomSNP=new RandomSNP(genotypeInfile.toString(),workFile.toString()+"/LDhat/RandomChromosomeSNPData",Integer.parseInt(randomSNPNumber));
			randomSNP.getRandomNumber();
			randomSNP.productOutFile();
		}
		else
		{
			RandomSNP randomSNP=new RandomSNP(workFile.toString()+vcfInfile.toString()+".genotype",workFile.toString()+"/LDhat/RandomChromosomeSNPData",Integer.parseInt(randomSNPNumber));
			randomSNP.getRandomNumber();
			randomSNP.productOutFile();
		}
		File file=new File(workFile.toString()+"/LDhat/RandomChromosomeSNPData");
		
		File[] files1=file.listFiles();
		
		for(int i=0;i<files1.length;i++)
		{
			
			String path=files1[i].toString();
			if(path.endsWith(".genotype"))
			{
				new LDhatGeno(path, workFile.toString()+"/LDhat/input/"+getName(path)+".sites", workFile.toString()+"/LDhat/input/"+getName(path)+".locs", Integer.parseInt(windowLength));
			}
			
		}
		
		file=new File( workFile.toString()+"/LDhat/input");
		files1=file.listFiles();
		for(int i=0;i<files1.length;i++)
		{
			String siteFile=files1[i].toString();
			if(siteFile.endsWith(".sites"))
			{
				String locsFile=siteFile.split(".sites")[0]+".locs";
				try{
					int temp=(numberOfIndividuals+1)*2;
					String commond="./software/LDhat/./lkgen -lk ./software/LDhat/old_lk.txt -nseq "+temp;//(number of ind +1)*2
					System.out.println(commond);
					Process process=Runtime.getRuntime().exec(commond);
					Scanner input=new Scanner(process.getInputStream());
					String line;
					while (input.hasNextLine())
					{
						line=input.nextLine();
						System.out.println(line);
					}
					input.close();
				}catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
				
				try{
					String commond="./software/LDhat/./interval -seq "+siteFile+" -loc "+locsFile+" -lk new_lk.txt -its 10000000 -bpen 5 -samp 5000";//(rates.txt)
					System.out.println(commond);
					Process process=Runtime.getRuntime().exec(commond);
					Scanner input=new Scanner(process.getInputStream());
					String line;
					while (input.hasNextLine())
					{
						line=input.nextLine();
						System.out.println(line);
					}
					input.close();

				}catch(Exception e)
				{
					System.out.println(e.getMessage());
				}	
					
				try{
					String commond="./software/LDhat/./stat -input rates.txt";//(res.txt)		
					System.out.println(commond);
					Process process=Runtime.getRuntime().exec(commond);
					Scanner input=new Scanner(process.getInputStream());
					String line;
					while (input.hasNextLine())
					{
						line=input.nextLine();
						System.out.println(line);
					}
					input.close();
				}catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
				try{
					PrintWriter shell=new PrintWriter(new FileWriter("move.sh"));
					shell.println("mv rates.txt "+siteFile.split(".sites")[0]+".rates.txt");
					shell.println("mv res.txt "+siteFile.split(".sites")[0]+".res.txt");
					shell.println("mv bounds.txt "+siteFile.split(".sites")[0]+".bounds.txt");
					shell.println("mv new_lk.txt "+siteFile.split(".sites")[0]+".new_lk.txt");
					shell.println("mv type_table.txt "+siteFile.split(".sites")[0]+"type_table.txt");
					shell.println("mv "+siteFile.split(".sites")[0]+".rates.txt "+workFile.toString()+"/LDhat/output");
					shell.println("mv "+siteFile.split(".sites")[0]+".res.txt "+workFile.toString()+"/LDhat/output");
					shell.println("mv "+siteFile.split(".sites")[0]+".bounds.txt "+workFile.toString()+"/LDhat/output");
					shell.println("mv "+siteFile.split(".sites")[0]+".new_lk.txt "+workFile.toString()+"/LDhat/output");
					shell.println("mv "+siteFile.split(".sites")[0]+".type_table.txt "+workFile.toString()+"/LDhat/output");
					shell.close();
					Process process=Runtime.getRuntime().exec("sh move.sh");
					Scanner input=new Scanner(process.getInputStream());
					String line;
					while (input.hasNextLine())
					{
						line=input.nextLine();
						System.out.println(line);
					}
					input.close();
				}catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			}
		}
		
		file=new File(workFile.toString()+"/genotypeInfile/RandomChromosomeSNPData");
		files1=file.listFiles();
		
		for(int i=0;i<files1.length;i++)
		{
			
			String path=files1[i].toString();
			if(path.endsWith(".genotype"))
			{
				new LDhatResultAccumulative(path, workFile.toString()+"/LDhat/output/"+getName(path)+".res.txt", workFile.toString()+"/LDhat/output/genetic_map.txt");
			}
			
		}
	}
	
	public static void chromosomePainter() throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/ChromosomePainter/input");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/ChromosomePainter/output");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/ChromosomePainter/rateFile");
			process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/ChromosomePainter/introgression");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File donorReceptLabelFile, recomRatesInfile;
		File file=new File(workFile.toString()+"/genotypeInfile/RandomSNPData");
		File[] files1=file.listFiles();
		
		do{
			System.out.println();
			System.out.print("Input your donor receipt label file: ");
			donorReceptLabelFile=new File(scanner.nextLine());
		}while(!(donorReceptLabelFile.exists()&&!donorReceptLabelFile.isDirectory()));
		
		do{
			System.out.println();
			System.out.print("Input your recom rate infile: ");
			recomRatesInfile=new File(scanner.nextLine());
		}while(!(recomRatesInfile.exists()&&!recomRatesInfile.isDirectory()));
		
		for(int i=0;i<files1.length;i++)
		{	
			File path=files1[i];
			if(path.getPath().endsWith(".genotype"))
			{
				System.out.println(path);
				GetRemRate getRemRate=new GetRemRate(path,workFile.toString()+"/ChromosomePainter/rateFile", recomRatesInfile.toString());
				getRemRate.mainFunction();
			}
				
		}
		
		new ChromoPainter(file.toString(), workFile.toString()+"/FastPhase/windowsOutput", workFile.toString()+"/ChromosomePainter/rateFile", donorReceptLabelFile.toString(),labelFile.toString(),workFile.toString()+"/ChromosomePainter/input/");
		
		file=new File(workFile.toString()+"/ChromosomePainter/input");
		files1=file.listFiles();
		for(int i=0;i<files1.length;i++)
		{
			String path=files1[i].toString();
			if(path.endsWith(".haplotype_infile"))
			try{
				String commond="./chromoPainter -g "+path+" -r "+path.split(".haplotype_infile")[0]+".remrate"+" -f "+workFile.toString()+"/ChromosomePainter/input/donor_list_infile -o "+workFile.toString()+"/ChromosomePainter/output/"+getName(path);
				System.out.println(commond);
				Process process = Runtime.getRuntime().exec(commond);
				Scanner output=new Scanner(process.getInputStream());
				while(output.hasNextLine())
					System.out.println(output.nextLine());
				output.close();
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		
		file=new File(workFile.toString()+"/ChromosomePainter/output");
		String donorName;
		Scanner donor=new Scanner(new FileReader(donorReceptLabelFile));
		donorName=donor.nextLine();
		
		new ChromoPainterIntrogression(workFile.toString()+"/ChromosomePainter/output",labelFile.toString(),donorName,workFile.toString()+"/ChromosomePainter/introgression",Integer.parseInt(windowLength));
	}
	
//	public static void fineStructure() throws IOException{
//		File file=new File(workFile.toString()+"/ChromosomePainter/output");
//		
//		if(!file.exists())
//		{
//			System.out.println();
//			System.out.println("You must run ChromoPainter first!");
//			chromosomePainter();
//			file=new File(workFile.toString()+"/ChromosomePainter/output"); 
//		}
//		
//		File[] infile=file.listFiles();
//		
//		for(int i=0;i<infile.length;i++)
//		{
//			String path=infile[i].toString();
//			if(path.endsWith(".prop.out"))
//			{
//				
//			}
//		}
//	}
	
	public static void KendallStatistics(File inputReferenceFile) throws IOException{
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/KendallStatistics/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File file=new File(workFile.toString()+"/SmartPCA/output"); 
		
		
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must run SmartPCA first!");
			return;
		}
		File[] infile=file.listFiles();
		
		for(int i=0;i<infile.length;i++)
		{
			String path=infile[i].toString();
			if(path.endsWith(".pca"))
			{
				new KendallStatistics.KendallStatistics(path, inputReferenceFile.toString(), workFile.toString()+"/KendallStatistics/output/"+getName(path),2);
			}
		}
		
	}
	
	public static void TWStatistics() throws IOException{
		try{
			Process	process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/TWStatistics/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		File file=new File(workFile.toString()+"/SmartPCA/output");
		
		
		if(!file.exists())
		{
			System.out.println();
			System.out.println("You must run SmartPCA first!");
			return;
		}
		File[] infile=file.listFiles();
		for(int i=0;i<infile.length;i++){
			String path=infile[i].toString();
			if(path.endsWith(".eval"))
			{
				try{
					String commond="./software/EIG/bin/./twstats -t ./software/EIG/POPGEN/twtable -i "+path+" -o "+workFile.toString()+"/TWStatistics/output/"+getName(path)+".out";
					System.out.println(commond);
					Process process = Runtime.getRuntime().exec(commond);
					Scanner output=new Scanner(process.getInputStream());
					while(output.hasNextLine())
						System.out.println(output.nextLine());
					output.close();
				}catch(Exception e){
					System.out.println("Exception"+e.getMessage());
				}
			}
		}
	}
	
	public static void haplotypeAge(){
		try{
			Process process=Runtime.getRuntime().exec("mkdir -p "+workFile.toString()+"/HaplotypeAge/output");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		
		File infile=new File(workFile.toString()+"/PositiveSelection/EHH/output");
		
		if(!infile.exists())
		{
			System.out.println("You must run EHH first!");
			return;
		}
		
		File[] file=infile.listFiles();
		for(int i=0;i<file.length;i++)
		{
			String path=file[i].getPath();
			CalHaplotypeAge calHaplotypeAge=new CalHaplotypeAge(path,workFile.toString()+"/HaplotypeAge/output/"+file[i].getName()+".age.out");
			calHaplotypeAge.mainFunction();
		}
	}
	
	public static void makeSoftwareExecutable(){
		try{
			Process process=Runtime.getRuntime().exec("chmod 777 -R ./software");
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	/**
	 * the choice menu of software
	 */
	public static void choicemenu(){
		System.out.println();
		System.out.println("The choice of function module:");
		System.out.println("  1 Haplotype analysis");
		System.out.println("  2 Linkage disequilibrium evaluation");
		System.out.println("  3 Phylogenetic tree construction");
		System.out.println("  4 Population structure");
		System.out.println("  5 Introgression");
		System.out.println("  6 Positive selection ");
		System.out.println("  7 Haplotype age");
		System.out.print("Input your choice: ");
	}
	
	/**
	 * the menu of Haplotype Sharing
	 */
	public static void menuOfHaplotypeSharing(){
		System.out.println("  1 Distinct haplotype sharing");
		System.out.println("  2 All haplotype sharing");
		System.out.print("Input your choice: ");
	}
	
	/**
	 * the menu of Microsat
	 */
	public static void menuOfMicrosat(){
		System.out.println("  1 Haplotype as allele");
		System.out.println("  2 SNP as allele");
		System.out.print("Input your choice: ");
	}
	
	/**
	 * the menu of PositiveSelection
	 */
	public static void MenuOfPositiveSelection(){
		System.out.println();
		System.out.println("  1 Theta");
		System.out.println("  2 PI");
		System.out.println("  3 Tajima's D");
		System.out.println("  4 Fst");
		System.out.println("  5 FisherExactTest");
		System.out.println("  6 iHS");
		System.out.println("  7 xp-EHH");
		System.out.println("  8 EHH");
		System.out.println("  9 CSS");
		System.out.print("Input your choice: ");
	}
	
	/**
	 * check whether user's choice of software is correct
	 * @param choiceOfSoftware
	 * @return
	 */
	public static boolean isCorrectInput(String choiceOfSoftware){
		if(!isInteger(choiceOfSoftware))
			return false;
		else
		{
			int i=Integer.parseInt(choiceOfSoftware);
			if(!(i>=1&&i<=7))
					return false;
			else
				return true;
		}
	}
	
	/**
	 * check whether user's choice of PositiveSelection is correct
	 * @param choice
	 * @return
	 */
	public static boolean isCorrectChoiceForPositiveSelection(String choice){
		if(choice.length()!=1)
			return false;
		else if(!(Character.isDigit(choice.charAt(0))))
			return false;
		else
		{
			try{
				int i=Integer.parseInt(choice);
				if(!(i>=1&&i<=9))
					return false;
				else
					return true;
			}catch(Exception e){
				return false;
			}
			
		}
	}
	
	/**
	 * ask use whether to continue to use other software
	 * @return
	 */
	public static boolean isContinue(){
		String toContinue;
		do{
			System.out.println();
			System.out.print("To continue[Y/N]: ");
			toContinue=scanner.nextLine();
		}while(!(toContinue.equals("Y")||toContinue.equals("N")));
		if(toContinue.equals("Y"))
			return true;
		else
			return false;
	}
	
	/**
	 * get the name of inputFile
	 * @param s
	 * @return
	 */
	public static String getName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1];
	}
	
	/**
	 * check whether String can be transformed to Integer
	 * @param s
	 * @return
	 */
	public static boolean isInteger(String s){
		Integer temp=0;
		try{
			temp=Integer.parseInt(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public static boolean isDouble(String s){
		Double temp=0.0;
		try{
			temp=Double.parseDouble(s);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public static boolean validDirectory(String s){
		if(isInteger(s))
			return true;
		else if(s.equals("X")||s.equals("Y"))
			return true;
		return false;
	}
	
	public static boolean validPedFile(String s) throws FileNotFoundException{
		File file=new File(s);
		Scanner reader=new Scanner(new FileReader(file));
		
		while(reader.hasNextLine())
		{
			String line=reader.nextLine();
			String[] snps=line.split(" ");
			if(!validLine(snps))
				return false;
		}
		return true;
	}
	
	public static boolean validLine(String[] s){
		for(int i=5;i<s.length;i++)
		{
			if(!s[i].equals("0"))
				return true;
		}
		return false;
	}
	public static String getWindowName(String s){
		String[] temp=s.split("/");
		return temp[temp.length-1].split("\\.")[0];
	}
}