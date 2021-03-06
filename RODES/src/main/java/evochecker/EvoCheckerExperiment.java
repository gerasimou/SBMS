package evochecker;

import java.util.Arrays;

import evochecker.auxiliary.Constants;
import evochecker.auxiliary.Utility;
import evochecker.exception.EvoCheckerException;

public class EvoCheckerExperiment {

	private final static int RUNS=1;
	private static int run=1;
	
	public EvoCheckerExperiment() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {		
		try {

			String tolerance		= Utility.getProperty(Constants.TOLERANCES_KEYWORD).replaceAll("\\s+","");
			String epsilon		= Utility.getProperty(Constants.EPSILONS_KEYWORD).replaceAll("\\s+","");
			String algorithm 	= Utility.getProperty(Constants.ALGORITHM_KEYWORD).toUpperCase();

			String tolerances[]		= tolerance.split(",");
			String epsilons[]		= epsilon.split(",");
			String fileNamesFUN[]  	= new String[epsilons.length * tolerances.length]; 
			String fileNamesVAR[]  	= new String[epsilons.length * tolerances.length]; 
			
			System.out.println("Tolerances:" + Arrays.toString(tolerances));
			System.out.println("Leniencies:" + Arrays.toString(epsilons));
			System.out.println("Algorithm:" + algorithm);
			
			
			for (; run<=RUNS; run++){
				System.out.println("Run: " + run);
				int i=0;
				for (String t : tolerances){
					for (String l : epsilons){
						Utility.setProperty(Constants.TOLERANCE_KEYWORD, t);
						Utility.setProperty(Constants.EPSILON_KEYWORD,  l);
						System.out.println("Tolerance: " +Utility.getProperty("TOLERANCE") + "\tLeniency: " +Utility.getProperty("LENIENCY"));
						Thread.sleep(1000);
						EvoChecker.main(null);
						fileNamesFUN[i] = "FUN_REGION_"+t.replace(".", "")+"_"+l.replace(".", "")+"_"+run;
						fileNamesVAR[i++] = "VAR_REGION_"+t.replace(".", "")+"_"+l.replace(".", "")+"_"+run;
					}
				}
				createOutputFile(fileNamesFUN, "FUN_REGION_all");
				createOutputFile(fileNamesVAR, "VAR_REGION_all");
			}
			
//			for (int i=0; i<fileNamesFUN.length; i++){
//				System.out.println(fileNamesFUN[i] +"\t"+ fileNamesVAR[i]);
//			}
			System.exit(0);			
		} catch (EvoCheckerException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	
	public static void createRegionsCombinedFile(String[] fileNames, String endFile){
		String dataPath = "data/";
		StringBuilder str = new StringBuilder();
		
		for (int i=fileNames.length-1; i>=0; i--){
			String fileName = fileNames[i];			
			str.append(Utility.readFile(dataPath + fileName));
			str.append("\n\n");
		}
		Utility.exportToFile(dataPath + endFile, str.toString(), false);
	}
	
	
	
	public static void createOutputFile(String[] fileNames, String endFile){
		String dataPath = "data/";
		StringBuilder str = new StringBuilder();
		
		for (int i=0; i<fileNames.length; i++){
			String fileName = fileNames[i];			
			str.append(Utility.readFile(dataPath + fileName));
			str.append("\n\n");
		}
		Utility.exportToFile(dataPath + endFile, str.toString(), false);
	}
	
	
	
	public static int getRun(){
		return run;
	}
	
	

}
