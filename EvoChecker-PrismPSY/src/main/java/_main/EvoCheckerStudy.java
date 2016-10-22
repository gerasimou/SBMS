package _main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import evochecker.auxiliary.Utility;
import evochecker.genetic.GenotypeFactory;
import evochecker.genetic.genes.AbstractGene;
import evochecker.genetic.jmetal.GeneticProblemPSY;
import evochecker.genetic.jmetal.experiments.Experiment;
import evochecker.genetic.jmetal.metaheuristics.NSGAII_Settings;
import evochecker.genetic.jmetal.metaheuristics.RandomSearch_Settings;
import evochecker.parser.ParserEngine;
import evochecker.prism.Property;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.experiments.Settings;
import jmetal.util.JMException;

public class EvoCheckerStudy extends Experiment{
	
	private List<Property> propertyList;
	private Problem problem;
	private List<AbstractGene> genes = new ArrayList<AbstractGene>();	
	private ParserEngine parserEngine;
	private String 		modelFilename;		
	private String 		propertiesFilename;

	
	  /**
	   * Configures the algorithms in each independent run
	   * @param problemName The problem to solve
	   * @param problemIndex
	   * @param algorithm Array containing the algorithms to run
	   * @throws ClassNotFoundException 
	   */
	
	  private int times = 0;
		
	  public synchronized void algorithmSettings(String problemName, int problemIndex, Algorithm[] algorithm) 
			  		throws ClassNotFoundException {  	
	      try {
		  
	    	  
	    	  
		      int numberOfAlgorithms = algorithmNameList_.length;
	
		      HashMap[] parameters = new HashMap[numberOfAlgorithms];
	
		      for (int i = 0; i < numberOfAlgorithms; i++) {
		          parameters[i] = new HashMap();
		        } // for
		      
		      	if (!(paretoFrontFile_[problemIndex] == null) && !paretoFrontFile_[problemIndex].equals("")) {
		      		for (int i = 0; i < numberOfAlgorithms; i++)
		      			parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
          		} // if
	
		      //set the algorithms
		      	switch (times % numberOfAlgorithms){
		      		case 0:{ 	algorithm[0] = new NSGAII_Settings(problemName, problem).configure();
		      			   		break;}
		      		
		      		case 1:{ 	algorithm[1] = new RandomSearch_Settings(problemName, problem).configure();
  			   					break;}  		
  			      		default:{throw new JMException("Error in algorithmSettings()");}
		      	}
		      	times++;
	      } catch (IllegalArgumentException ex) {
	          Logger.getLogger(EvoCheckerStudy.class.getName()).log(Level.SEVERE, null, ex);  
	      } catch  (JMException ex) {
	          Logger.getLogger(EvoCheckerStudy.class.getName()).log(Level.SEVERE, null, ex);  
	      }    
	  } // algorithmSettings
	  
	  
	  
	  
	  
	  public static void main(String[] args) throws IOException{
		  int runs = 30;//Integer.parseInt(Utility.getProperty("RUNS"));
		  
//		  for (int runNum=0; runNum<10; runNum++){
//			  long start = System.currentTimeMillis();
		  
			  EvoCheckerStudy evoStudy = new EvoCheckerStudy();
			  try {
				
				  //init evoChecker
//				  evoStudy.initialize();
				  //give a name for the study
				  evoStudy.experimentName_ 	= args[0];//Utility.getProperty("EXPERIMENT");
				  //name the algorithms
				  evoStudy.algorithmNameList_ = new String[]{"NSGAII", "RS"};
//				  evoStudy.algorithmNameList_ = new String[]{Utility.getProperty("ALGORITHM").toUpperCase()};
				  //name the problem
				  evoStudy.problemList_		= new String[]{"GeneticProblem"};
				  //name the pareto front - empty if unknown
				  evoStudy.paretoFrontFile_	= new String[1];//Space allocation for 1 front;
				  //name the indicators
				  evoStudy.indicatorList_	= new String[]{"HV", "IGD", "EPSILON", };		  
				  
				  //output directory
				  String outputDir			= "/Users/sgerasimou/Documents/Git/search-based-model-synthesis/Experiments/FinalExperiments/Google/StatisticalAnalysis/";//Utility.getProperty("OUTPUTDIR") ;
				  if (outputDir == null)
					  throw new FileNotFoundException("Output directory not specified");
				  evoStudy.experimentBaseDirectory_	= outputDir + evoStudy.experimentName_;
				  //pareto front directory - empty if unknown
				  evoStudy.paretoFrontDirectory_ 	= evoStudy.experimentBaseDirectory_+"/referenceFronts";
				  evoStudy.paretoFrontFile_[0]		= "GeneticProblem.rf";
				  
				  //init the array containing the settings of the algorithms
				  int numOfAlgorithms			= evoStudy.algorithmNameList_.length;
				  evoStudy.algorithmSettings_	= new Settings[numOfAlgorithms];
				  
				  //set the runs
				  evoStudy.independentRuns_		= runs;
				  
				  //init the experiment
				   evoStudy.initExperiment();
				  
				  //Run the experiments - number of threads
				  
				  //Generate quality indicators
				  evoStudy.generateQualityIndicators();
	
				  //Generate latex tables
				  evoStudy.generateLatexTables();				  
			} 
			  catch (Exception e) {
				e.printStackTrace();
			}
			  
//			long end = System.currentTimeMillis();
				
//			//export time taken to file
//			long timeUsed = (end - start)/1000;
//			System.err.println("Run " + runNum +":\t" + timeUsed);
//			String fileName = Utility.getProperty("OUTPUTDIR") + Utility.getProperty("EXPERIMENT") + "/run.csv";
//			Utility.exportToFile(fileName, timeUsed+"", true);
//		  }
	  }

	  
	  
	  
	@Deprecated
	public void initialize() throws Exception {
		modelFilename 		= Utility.getProperty("MODEL_TEMPLATE_FILE","models/DPM/dpm.pm");
		propertiesFilename	= Utility.getProperty("PROPERTIES_FILE", "models/DPM/dpm.pctl");
	
		parserEngine 		= new ParserEngine(modelFilename, propertiesFilename);
		genes				= GenotypeFactory.createChromosome(parserEngine.getEvolvableList());
		parserEngine.createMapping();
		
		propertyList = new ArrayList<Property>();
		//FX
//		propertyList.add(new Property(true));
//		propertyList.add(new Property(false));
//		propertyList.add(new Property(false));
//		propertyList.add(new Property(true));
//		int numOfConstraints = 1;
		
		//DPM properties (true for maximisation)
		// Also change to  evaluateConstraintsDPM function in parallelleEvaluate
		propertyList.add(new Property(false));
		propertyList.add(new Property(false));
		propertyList.add(new Property(false));
		propertyList.add(new Property(false));
		propertyList.add(new Property(false));
		int numOfConstraints = 2;

		
		//Zeroconf
//			propertyList.add(new Property(false));
//			propertyList.add(new Property(false));
//			propertyList.add(new Property(true));

		problem = new GeneticProblemPSY(genes, propertyList, parserEngine, numOfConstraints, "GeneticProblem");
		
	}
	
	
}
