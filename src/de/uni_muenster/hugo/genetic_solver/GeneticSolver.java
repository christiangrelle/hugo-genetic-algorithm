package de.uni_muenster.hugo.genetic_solver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.TimeZone; 
import java.util.Vector;
import java.util.Arrays;
import java.io.File;

/**
 * The Class GeneticSolver. This class provides a solver to the Rubik's Cube using a genetic algorithm.
 * The genetic algorithm incorporates a human cube solving strategy called two-generator method.
 * Currently as selection operators, stochastic universal sampling and linear ranking are implemented exchangeable.
 * As crossover operator, uniform crossover is  used.
 * 
 * @author Christian Grelle
 */
public class GeneticSolver extends Thread{
	
	/** The 2x2x3 generations. */
	int generations2x2x3 = 20;
	
	/** The transform to two-generator generations. */
	int generations2GenTransform = 300;
	
	/** The solve two-generator generations. */
	int generations2GenSolve = 51;
	
	/** The 2x2x3 population size. */
	int populationSize2x2x3 = 51; // the first is the elite gene that is conserved, 50 proceed in the recombination and mutation
	
	/** The transform to two-generator population size. */
	int populationSize2GenTransform = 51;
	
	/** The solve two-generator population size. */
	int populationSize2GenSolve = 51;
	
	/** The 2x2x3 individual size. */
	int individualSize2x2x3 = 30;
	
	/** The transform to two-generator individual size. */
	int individualSize2GenTransform = 30;
	
	/** The solve two-generator individual size. */
	int individualSize2GenSolve = 30;
	
	/** The 2x2x3 crossover probability. */
	double crossoverProbability2x2x3 = 0.6; // probability of recombination
	
	/** The transform to two-generator crossover probability. */
	double crossoverProbability2GenTransform = 0.6;
	
	/** The solve two-generator crossover probability. */
	double crossoverProbability2GenSolve = 0.6;
	
	/** The 2x2x3 mutation probability. */
	double mutationProbability2x2x3 = 0.03; // probability of mutation
	
	/** The transform to two-generator mutation probability. */
	double mutationProbability2GenTransform = 0.03;
	
	/** The solve two-generator mutation probability. */
	double mutationProbability2GenSolve = 0.03;
	
	/** CSV enabled. */
	boolean csvEnabled = true;
	
	/** The CSV file. */
	File csvFile = new File("hugo_statistics.csv");
	
	/** The repetitions. */
	int repetitions = 1;
	
	/** The repetition. */
	int repetition = 1;
	
	/** The cube representation. */
	int[] cubeSolved, cubeScrambled, cubeScrambledCopy = new int[54]; //cube status

	/** The face. */
	private Vector<Integer> f = new Vector<Integer>();
	
	/** The quarter turn. */
	private Vector<Integer> q = new Vector<Integer>();
	
	/** The delegate. */
	private GeneticSolverDelegate delegate;
	
	/** The optimized solution. */
	private String[] optimizedSolution = {"no moves"};
	
	/** The scramble sequence. */
	private int[] scrambleSequence = null;

	// Constructor
	/**
	 * Instantiates a new genetic solver.
	 * 
	 * @param delegate the delegate
	 */
	public GeneticSolver(GeneticSolverDelegate delegate) {
		this.delegate = delegate;
	}
	
	/**
	 * Initialize scramble sequence.
	 * 
	 * @param scrambleSequence the scramble sequence
	 */
	public void initialize(int[] scrambleSequence) {
		this.scrambleSequence = scrambleSequence;
	}
	
	/**
	 * Gets the solution.
	 * 
	 * @return the solution
	 */
	public String[] getSolution() {
		return optimizedSolution;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		delegate.pb2ChangeState();
		delegate.enableControls(false);
		delegate.enableEdit(false);
		for(repetition=1; repetition<= repetitions; repetition++){
			delegate.printTextLn("----------------------------------------------------------------------");
			delegate.printTextLn("Cube restoration "+repetition+" of "+repetitions);
			delegate.printTextLn("----------------------------------------------------------------------");
			delegate.printTextLn("");
			optimizedSolution = solve();
			delegate.setSolution();
			String solutionToSet = "";
				for(int i=0; i<optimizedSolution.length; i++) {
					solutionToSet += optimizedSolution[i];
				}
				delegate.setSolutionTextField(solutionToSet);
				delegate.updateProgressBar1(repetition);
		}
		delegate.enableControls(true);
		delegate.enableEdit(true);
		delegate.pb2ChangeState();
		delegate.updateProgressBar2(4);
	}
	
	/**
	 * Sets the algorithm parameters.
	 * 
	 * @param gen1 the phase 1 generations 
	 * @param gen2 the phase 2 generations 
	 * @param gen3 the phase 3 generations 
	 * @param pop1 the phase 1 population size 
	 * @param pop2 the phase 2 population size 
	 * @param pop3 the phase 3 population size
	 * @param ind1 the phase 1 individual size
	 * @param ind2 the phase 2 individual size
	 * @param ind3 the phase 3 individual size
	 * @param cro1 the phase 1 crossover probability
	 * @param cro2 the phase 2 crossover probability
	 * @param cro3 the phase 3 crossover probability
	 * @param mut1 the phase 1 mutation probability
	 * @param mut2 the phase 2 mutation probability
	 * @param mut3 the phase 3 mutation probability
	 * @param csv enable CSV file
	 * @param csvF the CSV file
	 * @param rep the repetition
	 */
	public void setParam(int gen1, int gen2, int gen3, int pop1, int pop2, int pop3, int ind1,int ind2,int ind3, double cro1, double cro2, double cro3, double mut1, double mut2, double mut3, boolean csv, File csvF, int rep) {
		generations2x2x3 = gen1;
		generations2GenTransform = gen2;	
		generations2GenSolve = gen3;
		populationSize2x2x3 = pop1;
		populationSize2GenTransform = pop2;	
		populationSize2GenSolve = pop3;
		individualSize2x2x3 = ind1;
		individualSize2GenTransform = ind2;	
		individualSize2GenSolve = ind3;
		crossoverProbability2x2x3 = cro1;
		crossoverProbability2GenTransform = cro2;	
		crossoverProbability2GenSolve = cro3;
		mutationProbability2x2x3 = mut1;
		mutationProbability2GenTransform = mut2;	
		mutationProbability2GenSolve = mut3;
		csvEnabled = csv;
		csvFile = csvF;
		repetitions = rep;
	}
	
	/**
	 * Sets the scrambled cube.
	 * 
	 * @param cubeScrambled the new cube scrambled
	 */
	public void setCubeScrambled(int[] cubeScrambled) {
		this.cubeScrambled = cubeScrambled;
	}

	/**
	 * The solver incorporating the human two-generator method in a genetic algorithm.
	 * The solver traverses the three phases sove 2x2x3, transform to two-generator as well as
	 * solve two-generator and calculates a solution for every phase.
	 * In the end, the part solutions are composed and transformed from an Integer representation to a String
	 * before displayed.
	 * 
	 * @return the string[]
	 */
	public String[] solve() {
		
		f.removeAllElements();
		q.removeAllElements();

		// Parameters
		// Charbuffer if GUI is not used, to use console
		// char[] charBuffer = new char[2]; //one scramble as two dimensional char representation (e.g. F')
		String[] scrambleNotation; //sequence of scrambles as letters notation (F, U,...,L')
		String[] sequenceNotation = new String[1]; 

		int loc2x2x3; //location of the 2x2x3 cube (0,...,11)
		int[] solutionhelper; //output sequence

		int movesTotal = 0; // total number of moves
		int optimizedMovesTotal = 0;
		int moves2x2x3 = 0;
		int moves2GenTransform = 0;
		int moves2GenSolve = 0;

		String[] solution1 = {"no moves"}; // first part (solve 2x2x3) of the solution
		String[] solution2 = {"no moves"}; // second part (transfotm to twogen) of the solution
		String[] solution3 = {"no moves"}; // third part (solve twogen) of the solution
		String[] solution = {"no moves"};

		int[] intSolution1 = {-1};
		int[] intSolution2 = {-1};
		int[] intSolution3 = {-1};
		int[] intSolution = {-1};
		int[] optimizedIntSolution = {-1};
		
		long startTime2x2x3 = 0, endTime2x2x3 = 0, wholeTime2x2x3 = 0;
		long startTime2GenTransform = 0, endTime2GenTransform = 0, wholeTime2GenTransform = 0; 
		long startTime2GenSolve = 0, endTimeCubeSolve = 0, wholeTimeCubeSolve = 0;
		long wholeTimeSolution = 0;

	    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
	    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	    
	    int xCount = 0; // # x-turns
		int yCount = 0; // # y-turns
		int xyCount = 0; // # xy-turns
		
		int loc2x2x3Test;
		
		int alreadyIn2x2x3 = 0;
		int alreadyIn2GenTransform = 0; // checks two-generator in getting into 2-generator phase
		int alreadyIn2GenSolve = 0;
		
		int alreadyIn2x2x3AtPosition = -1;

		int[][] population = new int[populationSize2x2x3][individualSize2x2x3];
		int[][] fitness = new int[populationSize2x2x3][2];
		int[] fitnessValues = new int[fitness.length]; // only fitnessFunctionvalues
		int[][][] populationStorage = new int[12][populationSize2x2x3][individualSize2x2x3];

		int isContinuing = 0;
		int generationsUsed2x2x3 = 0;
		int generationsUsed2GenTransform = 0;
		int generationsUsed2GenSolve = 0;
		int totalGenerations = 0;
		
        int allowed2x2x3moves = 20;
		boolean noGood2x2x3solution = true; // search until good solution is found

		int[] best2x2x3location = new int[12];
		int[][] best2x2x3elites= new int[12][population[0].length];
		int[] best2x2x3fitnessFunction = new int[12];

		int[][] bestTemp= new int[12][2];

		cubeSolved = initCube(); //initialize cube

	/*	// If the GUI is not used, but only a simple console, use these keyboard inputs for initialization 
	    // Read number of moves from keyboard input
	    System.out.println("Enter number of moves of the scramble:");
	    InputStreamReader inStream = new InputStreamReader( System.in );
	    BufferedReader inputMoves = new BufferedReader( inStream ); //needs InputStreamReader from above
		String stringBuffer = inputMoves.readLine();
	    int numberMoves = Integer.parseInt(stringBuffer);

	    //read moves from keyboard input
	    System.out.println("Enter the scramble moves. One move per line, Confirm with Enter.");

        scrambleSequence = new int[numberMoves]; //scramble sequence has size of number of moves

		for(int i=0; i<numberMoves; i++){ 
			charBuffer = inputMoves.readLine().toCharArray(); //read char from keyboard
			scrambleSequence[i] = toNumbers(charBuffer); //convert char to integer and store in sequence
		}

		// test output
		for(int i=0; i<numberMoves; i++){
			System.out.print(scrambleSequence[i]+" ");
		}
		System.out.println();
	*/	

		if(scrambleSequence != null) {
			scrambleNotation = toString(scrambleSequence);
		} else {
			scrambleNotation = new String[]{"edit"};
		}

		// Settings output
		delegate.printTextLn("Settings:");
		delegate.printTextLn("Phase 1 / Phase 2 / Phase 3");
		delegate.printTextLn("Generations: "+generations2x2x3+" / "+generations2GenTransform+" / "+generations2GenSolve);
		delegate.printTextLn("Population size: "+populationSize2x2x3+" / "+populationSize2GenTransform+" / "+populationSize2GenSolve);
		delegate.printTextLn("Individual size: "+individualSize2x2x3+" / "+individualSize2GenTransform+" / "+individualSize2GenSolve);
		delegate.printTextLn("Crossover probability: "+crossoverProbability2x2x3+" / "+crossoverProbability2GenTransform+" / "+crossoverProbability2GenSolve);
		delegate.printTextLn("Crossover probability: "+mutationProbability2x2x3+" / "+mutationProbability2GenTransform+" / "+mutationProbability2GenSolve);
		delegate.printTextLn("");
		delegate.printTextLn("Scramble:");
		for(int i=0; i<scrambleNotation.length; i++){
			delegate.printText(scrambleNotation[i]);
		}
		delegate.printTextLn("");
		
		delegate.printTextLn("");
		delegate.printTextLn("Representation of a solved Cube:");
		printCube(cubeSolved);
		delegate.printTextLn("");

		if(scrambleSequence != null) { 
			cubeScrambled = doSequence(cubeSolved,scrambleSequence);
		}

		delegate.printTextLn("Representation of the scrambled Cube:");
		printCube(cubeScrambled);
		cubeScrambledCopy = cubeScrambled.clone(); // rotations change cubeScrambled

/*

* --------------------------------------------------------------------------------------- 

* Solve 2x2x3

* ----------------------------------------------------------------------------------------

*/

		// Set Solve 2x2x3 start time
		startTime2x2x3=System.currentTimeMillis(); 
		delegate.updateProgressBar2(1);
		delegate.printTextLn("");
		delegate.printTextLn("");
		delegate.printTextLn("----------------------------------------------------------------------");
		delegate.printTextLn("Solve 2x2x3 phase");
		delegate.printTextLn("----------------------------------------------------------------------");
		
		int[] cube2x2x3Test = cubeScrambled.clone();

		for(loc2x2x3Test = 0; loc2x2x3Test<=11; loc2x2x3Test++){
			cube2x2x3Test = cubeScrambled.clone();
			
			// Test following code for all 4 sides
			for(int i=1; i<=loc2x2x3Test/3; i++){ //loc2x2x3/3 is 0 for loc2x2x3 = 0,...,2 , 1 for loc2x2x3 = 3,...,5 etc.
				cube2x2x3Test = rotateCubeY(cube2x2x3Test);
			}

			// Do nothing, XY, or XX rotation for each of the 4 sides

			switch(loc2x2x3Test%3){
			case 1: 
				cube2x2x3Test = rotateCubeXY(cube2x2x3Test);
					break;
			case 2: 
					for(int i=1; i<=2; i++){
						cube2x2x3Test = rotateCubeX(cube2x2x3Test);
					} break;
			}

		if(is2x2x3(cube2x2x3Test)==16){
			alreadyIn2x2x3 = 1;
			alreadyIn2x2x3AtPosition = loc2x2x3Test;
		}
		}
		
		if(alreadyIn2x2x3 == 1){
		delegate.printTextLn("");
		delegate.printTextLn("Cube is already in 2x2x3");
		}

	if(alreadyIn2x2x3==0){
		while(noGood2x2x3solution){ // search until good solution is found
			generationsUsed2x2x3 = generationsUsed2x2x3 + generations2x2x3;

		// Test all 12 (4*3: 3 tests from all sides except U and D tests which are included in the 4 other sides) possible 2x2x3 locations from BD (BackDown) (if BD 2x2x3 goes to all locations, then also all locations go ones to BD for tests in BD)
		// 0 = BD, 1 = XY = BL, 2 = X2 = FU, 3 = Y = DL, 4 = YXY = FL, 5 = YX2 = UR
		// 6 = Y2 = FD, 7 = Y2XY = FR, 8 = Y2X2 = UB, 9 = Y3 = RD, 10 = Y3XY = RB, 11 = Y3X2 = UL
		for(loc2x2x3 = 0; loc2x2x3<=11; loc2x2x3++){
			cubeScrambled = cubeScrambledCopy.clone(); // rotations change cubeScrambled
			delegate.printTextLn("");
			delegate.printTextLn("Working on 2x2x3 position "+loc2x2x3);

			// test following code for all 4 sides
			for(int i=1; i<=loc2x2x3/3; i++){ //loc2x2x3/3 is 0 for loc2x2x3 = 0,...,2 , 1 for loc2x2x3 = 3,...,5 etc.
				cubeScrambled = rotateCubeY(cubeScrambled);
				delegate.printTextLn("clockwise cube rotation around Y axis");		
			}

			// Do nothing, XY, or XX rotation for each of the 4 sides
			switch(loc2x2x3%3){
			case 1: 
					cubeScrambled = rotateCubeXY(cubeScrambled);
					delegate.printTextLn("clockwise cube rotation around X and then Y axis");
					break;

			case 2: 
					for(int i=1; i<=2; i++){
						cubeScrambled = rotateCubeX(cubeScrambled);
						delegate.printTextLn("clockwise cube rotation around X axis");
					} break;
			}

			// initialization of the first generation of the population for genetic treatment
			if(isContinuing == 0){
				for (int i = 0; i < populationSize2x2x3; i++){
					for (int j = 0; j < individualSize2x2x3; j++){
						population[i][j] = (int)(Math.random()*18);
					} 
				}
			}

			// If one wants to continue former calculations contribute by using the last population as new start population
			if(isContinuing == 1){
				population = populationStorage[loc2x2x3].clone();				
			}

			for (int generation = 0; generation < generations2x2x3; generation++ ){

				// Optimization of sequence
				for (int i = 0; i < populationSize2x2x3; i++ ){
					population[i] = optimization(population[i]);
				}

				// evaluation of the fitness function
				// 1=integrity, 2=2x2x3, 3=is2gen
				for (int i = 0; i < populationSize2x2x3; i++){
					fitness[i] = fitnessFunction(cubeScrambled, population[i], 2);
				}

				// fitnessValues contains 1st column of fitness (fitnessFunctionvalues)
				for(int i=0; i<fitness.length; i++){
					fitnessValues[i] = fitness[i][0];
				}

				// sorting of the population by fitness
				population = populationSort(population, fitnessValues);

				// sorting of the fitness array
				fitness = populationSort(fitness, fitnessValues);

				// print current result
				if(generation+1 % 5000 == 0 || ((generation+1) == generations2x2x3)){ // each 5000th generation
					delegate.printTextLn("Generation / Best fitness / At move / Average population fitness / Total population fitness:");
					delegate.printTextLn((generationsUsed2x2x3-generations2x2x3+generation+1)+" / "+fitness[0][0]+" / "+(fitness[0][1]+1)+" / "+((float)sumOfFitnessValues(fitness)/(float)populationSize2x2x3) +" / "+(float)sumOfFitnessValues(fitness));
				}	
				
				
				// Selection (0=Linear Ranking, 1=Stochastic Universal Sampling)
				population = selection(population, fitness, 1);

				// Crossover
				population = crossover(population, crossoverProbability2x2x3);

				// Mutation
				population = mutate(population, mutationProbability2x2x3);
			}

			populationStorage[loc2x2x3]=population.clone();

			delegate.printTextLn("Best sequence: ");

			solutionhelper = new int[fitness[0][1]+1];

			for(int i = 0; i<= fitness[0][1]; i++){
				solutionhelper[i]=population[0][i];
			}

			sequenceNotation = toString(solutionhelper);

			// output
			for(int i=0; i<sequenceNotation.length; i++){
				delegate.printText(sequenceNotation[i]);
			}
			delegate.printTextLn("");
			
			if((fitness[0][0]+(fitness[0][1]+1))/10==16){
				delegate.printTextLn("This 2x2x3 is solved");	
			}
			
			best2x2x3location[loc2x2x3]=loc2x2x3; 
			best2x2x3elites[loc2x2x3]=population[0].clone();
			best2x2x3fitnessFunction[loc2x2x3]=-(fitness[0][1]+1) - (16-(fitness[0][0]+(fitness[0][1]+1))/10)*100; //penalty if 2x2x3 is not solved
		}                                     

		// Sorting of the elites depending on their fitnessFunction
		best2x2x3elites=populationSort(best2x2x3elites, best2x2x3fitnessFunction);

		for(int i=0; i<bestTemp.length; i++){
			bestTemp[i][0]=best2x2x3location[i];
			bestTemp[i][1]=best2x2x3fitnessFunction[i];
		}

		// Sorting of bestTemp (location and fitnessFunction) 
		bestTemp = populationSort(bestTemp, best2x2x3fitnessFunction);

		for(int i=0; i<best2x2x3location.length; i++){
			best2x2x3location[i]=bestTemp[i][0];
		}

		// if no 2x2x3 is solved, or more than allowed2x2x3moves moves are used: restart
		if(bestTemp[0][1] >= allowed2x2x3moves *(-1)){ 
			noGood2x2x3solution = false;
		}else{
			isContinuing=1; // population will be loaded from storage to work on
		}
		}

		// otherwise continue
		delegate.printTextLn("");
		delegate.printTextLn("2x2x3 cube is solved!");
		delegate.printTextLn("");
		delegate.printTextLn("The 2x2x3 locations in descending order of fitness:");
		for(int i=0; i<best2x2x3location.length; i++){
			delegate.printText(best2x2x3location[i]+" ");
		}
		delegate.printTextLn("");
		delegate.printTextLn("Location "+best2x2x3location[0]+" will be put in back-down for the next phase");
		loc2x2x3 = best2x2x3location[0];
		cubeScrambled = cubeScrambledCopy.clone();

		/* put best 2x2x3 in BD */
		for(int i=1; i<=loc2x2x3/3; i++){ //loc2x2x3/3 is 0 for loc2x2x3 = 0,...,2 , 1 for loc2x2x3 = 3,...,5 etc.
			cubeScrambled = rotateCubeY(cubeScrambled);
			delegate.printTextLn("Clockwise cube rotation around Y axis");
			yCount = yCount+1; // counts # turns
		}

		// do nothing, XY, or XX rotation for each of the 4 sides
		switch(loc2x2x3%3){
		case 1: 
				cubeScrambled = rotateCubeXY(cubeScrambled);
				delegate.printTextLn("clockwise cube rotation around X and then Y axis");
				xyCount = xyCount+1; // counts # turns
				break;
		case 2: 
				for(int i=1; i<=2; i++){
					cubeScrambled = rotateCubeX(cubeScrambled);
					delegate.printTextLn("clockwise cube rotation around X axis");
					xCount = xCount+1; // counts # turns
				} break;
		}

		
		// put 2x2x3 position no. 0 elite in bestTemp
		bestTemp[0] = fitnessFunction(cubeScrambled, best2x2x3elites[0], 3);
		
		// helper to calculate optimal number of moves for the doSequence
		int[] best2x2x3elitesTemp = new int[bestTemp[0][1]+1];
		for(int i=0; i<best2x2x3elitesTemp.length; i++){
			best2x2x3elitesTemp[i]=best2x2x3elites[0][i];
		}

		// solve 2x2x3
		cubeScrambled = doSequence(cubeScrambled, best2x2x3elitesTemp);
		moves2x2x3 = bestTemp[0][1]+1; 
		movesTotal = bestTemp[0][1]+1;  // calculates the total no. of moves

		solution1 = toString(best2x2x3elitesTemp);
		intSolution1 = best2x2x3elitesTemp.clone();
		delegate.printTextLn("");
		delegate.printText("Number of moves to solve 2x2x3: ");
		delegate.printTextLn(moves2x2x3+"");
		delegate.printTextLn("Solution for solving 2x2x3:");

		for(int i=0; i<solution1.length;i++){
			delegate.printText(solution1[i]);
		}
		delegate.printTextLn("");
	}

	if(alreadyIn2x2x3==1){

		for(int i=1; i<=alreadyIn2x2x3AtPosition/3; i++){ //loc2x2x3/3 is 0 for loc2x2x3 = 0,...,2 , 1 for loc2x2x3 = 3,...,5 etc.
			cubeScrambled = rotateCubeY(cubeScrambled);
			delegate.printTextLn("clockwise cube rotation around Y axis");
			yCount = yCount+1; // counts # turns
		}

		// do nothing, XY, or XX rotation for each of the 4 sides
		switch(alreadyIn2x2x3AtPosition%3){
		case 1: 
				cubeScrambled = rotateCubeXY(cubeScrambled);
				delegate.printTextLn("clockwise cube rotation around X and then Y axis");
				xyCount = xyCount+1; // counts # turns
				break;
		case 2: 
				for(int i=1; i<=2; i++){
					cubeScrambled = rotateCubeX(cubeScrambled);
					delegate.printTextLn("clockwise cube rotation around X axis (alreadyin2x2x3)");
					xCount = xCount+1; // counts # turns
				} break;
		}
	}

	if(is2gen(cubeScrambled)==1){
		alreadyIn2GenTransform = 1;
	}

	// set 2x2x3 end time
	endTime2x2x3=System.currentTimeMillis(); 
	wholeTime2x2x3=endTime2x2x3-startTime2x2x3; 
	delegate.printTextLn("2x2x3 time: " + sdf.format(wholeTime2x2x3)+"h");

/*------------------------------------------------------------------------------
 * Transform to 2-Generator
 * -----------------------------------------------------------------------------
 */

	// set 2-Generator start time
	startTime2GenTransform=System.currentTimeMillis();
	delegate.updateProgressBar2(2);

	delegate.printTextLn("");
	delegate.printTextLn("");
	delegate.printTextLn("----------------------------------------------------------------------");
	delegate.printTextLn("Transform to two-generator phase");
	delegate.printTextLn("----------------------------------------------------------------------");
	delegate.printTextLn("");
	
	if(alreadyIn2GenTransform == 0){

		// values of the genetic algorithm for the getting into 2-Generator phase
		population = new int[populationSize2GenTransform][individualSize2GenTransform];
		fitness = new int[populationSize2GenTransform][2];

		// initialization of the first generation of the population for genetic treatment
		for (int i = 0; i < populationSize2GenTransform; i++){
			for (int j = 0; j < individualSize2GenTransform; j++){
				population[i][j] = (int)(Math.random()*18);
			} 
		}

	do{
		generationsUsed2GenTransform = generationsUsed2GenTransform + generations2GenTransform;
		for (int generation = 0; generation < generations2GenTransform; generation++ ){
			// optimization of sequence
			for (int i = 0; i < populationSize2GenTransform; i++ ){
				population[i] = optimization(population[i]);
			}

			// evaluation of the fitness function
			// 1=integrity, 2=2x2x3, 3=is2gen
			for (int i = 0; i < populationSize2GenTransform; i++){
				fitness[i] = fitnessFunction(cubeScrambled, population[i], 3);
			}

			// fitnessValues contains 1st column of fitness (fitnessFunctionvalues)
			for(int i=0; i<fitness.length; i++){
				fitnessValues[i] = fitness[i][0];
			}

			// sorting of the population by fitness
			population = populationSort(population, fitnessValues);

			// sorting of the fitness array
			fitness = populationSort(fitness, fitnessValues);

			// print current result
			if(generationsUsed2GenTransform-generations2GenTransform+generation == 0){
			delegate.printTextLn("Calculating "+generations2GenTransform+" generations...");
			}
			if(generation+1  == generations2GenTransform){
				delegate.printTextLn("Generation / Best fitness / At move / Average population fitness / Total population fitness:");
				delegate.printTextLn((generationsUsed2GenTransform-generations2GenTransform+generation+1) +" / "+fitness[0][0]+" / "+(fitness[0][1]+1)+" / "+((float)sumOfFitnessValues(fitness)/(float)populationSize2GenTransform) +" / "+(float)sumOfFitnessValues(fitness));
				delegate.printTextLn("");
			}
			if(((generation % 5000) == 0) && (generation > 0)){ // each 5000th generation
				delegate.printTextLn("Generation / Best fitness / At move / Average population fitness / Total population fitness:");
				delegate.printTextLn((generationsUsed2GenTransform-generations2GenTransform+generation) +" / "+fitness[0][0]+" / "+(fitness[0][1]+1)+" / "+((float)sumOfFitnessValues(fitness)/(float)populationSize2GenTransform) +" / "+(float)sumOfFitnessValues(fitness));
				delegate.printTextLn("");
				delegate.printTextLn("Calculating next 5000 generations...");
			}

			// selection (0=Linear Ranking, 1=Stochastic Universal Sampling)
			population = selection(population, fitness, 1);

			//crossover
			population = crossover(population, crossoverProbability2GenTransform);

			// mutation
			population = mutate(population, mutationProbability2GenTransform);
		}

		// already in 2-generator group?
		if((fitness[0][0] + fitness[0][1]+1) == 1700){
			delegate.printTextLn("Cube is transformed to two-generator!");

			// helper to calculate optimal number of moves for the notationoutput
			int[] populationTemp = new int[fitness[0][1]+1];
			for(int i=0; i<populationTemp.length; i++){
				populationTemp[i]=population[0][i];
			}
			sequenceNotation = toString(populationTemp);
			intSolution2 = populationTemp.clone();
			if(fitness[0][1]>=0){
				cubeScrambled = doSequence(cubeScrambled, populationTemp);
				moves2GenTransform = fitness[0][1]+1;
				movesTotal = movesTotal + fitness[0][1]+1;
				delegate.printTextLn("");
				delegate.printText("Number of moves to transform to two-generator: ");
				delegate.printTextLn(String.valueOf(fitness[0][1]+1));
			}
		}else{
			delegate.printTextLn("Adding "+generations2GenTransform+" generations...");
		}
	}while((fitness[0][0] + fitness[0][1]+1) != 1700);

		if(alreadyIn2GenTransform==0){
			delegate.printTextLn("Solution for transforming to two-generator:");
			solution2 = sequenceNotation.clone();
			for(int i=0; i<solution2.length;i++){
				delegate.printText(solution2[i]);
			}
		}delegate.printTextLn("");	
	}

	if(alreadyIn2GenTransform==1){
		delegate.printTextLn("Cube is already in two-generator:");
	}
	
	// set 2-Generator end time
	endTime2GenTransform=System.currentTimeMillis();  
	wholeTime2GenTransform=endTime2GenTransform-startTime2GenTransform; 
	delegate.printTextLn("Transform to two-generator time: " + sdf.format(wholeTime2GenTransform)+"h");

/*-------------------------------------------------------------------------

* Solve 2-Generator

* ------------------------------------------------------------------------

*/

	// set Cube Solve start time
	startTime2GenSolve=System.currentTimeMillis();
	delegate.updateProgressBar2(3);

	if(integrity(cubeScrambled)==48){
		alreadyIn2GenSolve = 1;
	}

	delegate.printTextLn("");
	delegate.printTextLn("");
	delegate.printTextLn("----------------------------------------------------------------------");
	delegate.printTextLn("Solve two-generator phase");
	delegate.printTextLn("----------------------------------------------------------------------");
	delegate.printTextLn("");
	
	if(alreadyIn2GenSolve==0){

		// values of the genetic algorithm for solving the 2-Generator phase
		population = new int[populationSize2GenSolve][individualSize2GenSolve];
		fitness = new int[populationSize2GenSolve][2];
		int generations2GenSolve2 = generations2GenSolve;

		// initialization of the first generation of the population for genetic treatment
		for (int i = 0; i < populationSize2GenSolve; i++){
			for (int j = 0; j < individualSize2GenSolve; j++){
				population[i][j] = (int)(Math.random()*18);
			} 
		}

		do{
			generationsUsed2GenSolve = generationsUsed2GenSolve + generations2GenSolve2;
		for (int generation = 0; generation < generations2GenSolve2; generation++ ){

			// each gene value is transformed to 0 or 1 (F or U) and former extensions (2 or ') are kept in case of other values were created
			for(int i = 0; i < populationSize2GenSolve; i++){
				for (int j = 0; j < individualSize2GenSolve; j++){	
					population[i][j] = (population[i][j]%2) + 6 * (population[i][j]/6);
				}
			}

			// optimization of sequence
			for (int i = 0; i < populationSize2GenSolve; i++ ){
				population[i] = optimization(population[i]);
			}
			
			// evaluation of the target function
			// 1=integrity, 2=2x2x3, 3=is2gen
			for (int i = 0; i < populationSize2GenSolve; i++){
				fitness[i] = fitnessFunction(cubeScrambled, population[i], 1);
			}

			// fitnessValues contains 1st column of fitness (fitnessFunctionvalues)
			for(int i=0; i<fitness.length; i++){
				fitnessValues[i] = fitness[i][0];
			}

			// sorting of the population by fitness
			population = populationSort(population, fitnessValues);

			// sorting of the fitness array
			fitness = populationSort(fitness, fitnessValues);

			// print current result
			if(generationsUsed2GenSolve-generations2GenSolve2+generation == 0){
			delegate.printTextLn("Calculating "+generations2GenSolve2+" generations...");
			}
			if(generation+1  == generations2GenSolve2){
				delegate.printTextLn("Generation / Best fitness / At move / Average population fitness / Total population fitness:");
				delegate.printTextLn((generationsUsed2GenSolve-generations2GenSolve2+generation+1) +" / "+fitness[0][0]+" / "+(fitness[0][1]+1)+" / "+((float)sumOfFitnessValues(fitness)/(float)populationSize2GenSolve) +" / "+(float)sumOfFitnessValues(fitness));
				delegate.printTextLn("");
			}
			if(((generation % 10000) == 0) && (generation > 0)){ // each 10000th generation
				delegate.printTextLn("Generation / Best fitness / At move / Average population fitness / Total population fitness:");
				delegate.printTextLn((generationsUsed2GenSolve-generations2GenSolve2+generation) +" / "+fitness[0][0]+" / "+(fitness[0][1]+1)+" / "+((float)sumOfFitnessValues(fitness)/(float)populationSize2GenSolve) +" / "+(float)sumOfFitnessValues(fitness));
				delegate.printTextLn("");
				delegate.printTextLn("Calculating next 10000 generations...");
			}

			// selection (0=Linear Ranking, 1=Stochastic Universal Sampling)
			population = selection(population, fitness, 1);

			//crossover
			population = crossover(population, crossoverProbability2GenSolve);

			// mutation
			population = mutate(population, mutationProbability2GenSolve);
		}

		if((((double)fitness[0][0]) + ((double)fitness[0][1])+1.0)/10.0 == 48.0){
			delegate.printTextLn("Two-generator is solved!");
			delegate.printTextLn("");
			delegate.printText("Number of moves to solve two-generator: ");
			delegate.printTextLn((fitness[0][1]+1)+"");
		}else{
			generations2GenSolve2 = generations2GenSolve2 + 500;
			delegate.printTextLn("Adding "+generations2GenSolve2+" generations...");
		}
		}while((((double)fitness[0][0]) + ((double)fitness[0][1])+1.0)/10.0 != 48.0);
	}

	// helper to calculate optimal number of moves for the two-generator
	int[] populationTemp = new int[fitness[0][1]+1];
	
	for(int i=0; i<populationTemp.length; i++){
		populationTemp[i]=population[0][i];
	}
	
	sequenceNotation = toString(populationTemp);
	intSolution3 = populationTemp.clone();
	solution3 = sequenceNotation.clone();
	if(alreadyIn2GenSolve==0){
	delegate.printTextLn("Solution for solving two-generator:");

	for(int i=0; i<solution3.length;i++){
		delegate.printText(solution3[i]);
	}
	delegate.printTextLn("");
	}
	moves2GenSolve = fitness[0][1]+1;
	movesTotal = movesTotal + fitness[0][1]+1;
	
	if(alreadyIn2GenSolve==1){
		delegate.printTextLn("Two-generator is already solved");
	}
	
	// restoring cube rotations
	for(int i=0; i<xyCount; i++){
		delegate.printTextLn("Counter-clockwise cube rotation around Y and then X axis");
		if(intSolution1[0] != -1){
			intSolution1 = restoreTurnXY(intSolution1);
		}

		if(alreadyIn2GenTransform==0){
			intSolution2 = restoreTurnXY(intSolution2);
		}

		if(alreadyIn2GenSolve==0){
			intSolution3 = restoreTurnXY(intSolution3);
		}
	}

	for(int i=0; i<xCount; i++){
		delegate.printTextLn("Counter-clockwise cube rotation around X axis");
		
		if(intSolution1[0] != -1){
			intSolution1 = restoreTurnX(intSolution1);
		}

		if(intSolution2[0] != -1){
			intSolution2 = restoreTurnX(intSolution2);
		}

		if(intSolution3[0] != -1){
			intSolution3 = restoreTurnX(intSolution3);
		}
	}

	for(int i=0; i<yCount; i++){
		delegate.printTextLn("Counter-clockwise cube rotation around Y axis");
		if(intSolution1[0] != -1){
			intSolution1 = restoreTurnY(intSolution1);
		}

		if(intSolution2[0] != -1){
			intSolution2 = restoreTurnY(intSolution2);
		}

		if(intSolution3[0] != -1){
			intSolution3 = restoreTurnY(intSolution3);
		}
	}

	int intSolutionLength=0;

	// transforming int solutions to string and counting intsolution length
	if(alreadyIn2x2x3==0){
		solution1 = toString(intSolution1);
		intSolutionLength = intSolutionLength + intSolution1.length;
	}

	if(alreadyIn2GenTransform==0){
		solution2 = toString(intSolution2);
		intSolutionLength = intSolutionLength + intSolution2.length;
	}

	if(alreadyIn2GenSolve==0){
		solution3 = toString(intSolution3);
		intSolutionLength = intSolutionLength + intSolution3.length;
	}

	// create solution array out of part solutions

	if((alreadyIn2x2x3==0) || (alreadyIn2GenTransform==0) || (alreadyIn2GenSolve==0)){
		intSolution = new int[intSolutionLength];
	}

	if(alreadyIn2x2x3==0){
		System.arraycopy(intSolution1, 0, intSolution, 0, intSolution1.length);
	}

	if(alreadyIn2GenTransform==0){
		if(alreadyIn2x2x3==0){
			System.arraycopy(intSolution2, 0, intSolution, intSolution1.length, intSolution2.length);
		}else{
			System.arraycopy(intSolution2, 0, intSolution, 0, intSolution2.length);
		}
	}

	if(alreadyIn2GenSolve==0){	
		if(alreadyIn2GenTransform==0){
			if(alreadyIn2x2x3==0){
				System.arraycopy(intSolution3, 0, intSolution, intSolution1.length+intSolution2.length, intSolution3.length);
			}else{
				System.arraycopy(intSolution3, 0, intSolution, intSolution2.length, intSolution3.length);
			}
		}

		if(alreadyIn2GenTransform==1){
			if(alreadyIn2x2x3==0){
				System.arraycopy(intSolution3, 0, intSolution, intSolution1.length, intSolution3.length);
			}else{
				System.arraycopy(intSolution3, 0, intSolution, 0, intSolution3.length);
			}
		}
	}	

	//creating solution String
	if(intSolution[0] != -1){
		solution=toString(intSolution);
	}

	//creating optimized solution int and string
	if(intSolution[0] != -1){
		optimizedIntSolution = optimizeEndSequence(intSolution);
		while((Arrays.equals(optimizedIntSolution,  optimizeEndSequence(optimizedIntSolution)))!=true){
		optimizedIntSolution = optimizeEndSequence(optimizedIntSolution);
		}

		optimizedMovesTotal=optimizedIntSolution.length;
		if(optimizedMovesTotal != 0){
		optimizedSolution=toString(optimizedIntSolution);
		}
	}

	// set CubeSolve end time
	endTimeCubeSolve=System.currentTimeMillis();
	wholeTimeCubeSolve=endTimeCubeSolve-startTime2GenSolve; 
	delegate.printTextLn("Solve two-generator time: " + sdf.format(wholeTimeCubeSolve)+"h");

	delegate.printTextLn("");
	delegate.printTextLn("");

/* 

 *  -----------------------------------------------------------------------

 *  printing out results

 *  -----------------------------------------------------------------------

 */
	delegate.printTextLn("----------------------------------------------------------------------");
	delegate.printTextLn("Results");
	delegate.printTextLn("----------------------------------------------------------------------");

	if((alreadyIn2GenSolve==1)&&(alreadyIn2x2x3==1)&&(alreadyIn2GenTransform==1)){
		delegate.printTextLn("");
		delegate.printTextLn("Cube is already solved");
		delegate.printTextLn("");
		if (repetition != repetitions){
			delegate.printTextLn("");
		}
	}else{
		delegate.printTextLn("");
		delegate.printTextLn("Solve 2x2x3 phase");
		delegate.printText("Cube was already in 2x2x3: ");
			if(alreadyIn2x2x3==1){
				delegate.printTextLn("yes");
			}else{
				delegate.printTextLn("no");
			}
		delegate.printText("Generations needed: ");
		delegate.printTextLn(String.valueOf(generationsUsed2x2x3));
		delegate.printText("Time needed: ");
	    delegate.printTextLn(sdf.format(wholeTime2x2x3)+"h");
		if(alreadyIn2x2x3==0){
			for(int i=0; i<solution1.length;i++){
				delegate.printText(solution1[i]);
			}	
			delegate.printTextLn("");
		}
		delegate.printTextLn("");
		delegate.printTextLn("Transform to 2-Generator phase");
		delegate.printText("Cube was already in 2-Generator: ");
			if(alreadyIn2GenTransform==1){
				delegate.printTextLn("yes");
			}else{
				delegate.printTextLn("no");
			}
		delegate.printText("Generations needed: ");
		delegate.printTextLn(String.valueOf(generationsUsed2GenTransform));
		delegate.printText("Time needed: ");
	    delegate.printTextLn(sdf.format(wholeTime2GenTransform)+"h");
		if(alreadyIn2GenTransform==0){
			for(int i=0; i<solution2.length;i++){
				delegate.printText(solution2[i]);
			}
			delegate.printTextLn("");
		}	
		delegate.printTextLn("");
		delegate.printTextLn("Solve 2-Generator phase");
		delegate.printText("Cube was already solved: ");
			if(alreadyIn2GenSolve==1){
				delegate.printTextLn("yes");
			}else{
				delegate.printTextLn("no");
			}
		delegate.printText("Generations needed: ");
		delegate.printTextLn(String.valueOf(generationsUsed2GenSolve));
		delegate.printText("Time needed: ");
	    delegate.printTextLn(sdf.format(wholeTimeCubeSolve)+"h");
		if(alreadyIn2GenSolve==0){
			for(int i=0; i<solution3.length;i++){
				delegate.printText(solution3[i]);
			}
			delegate.printTextLn("");
		}

		delegate.printTextLn("");
		delegate.printTextLn("----------------------------------------------------------------------");
		delegate.printText("Total number of moves: ");
		delegate.printTextLn(String.valueOf(movesTotal));
		wholeTimeSolution=wholeTime2x2x3+wholeTime2GenTransform+wholeTimeCubeSolve;
	    delegate.printTextLn("Total time: " + sdf.format(wholeTimeSolution)+"h");
		totalGenerations = generationsUsed2x2x3+generationsUsed2GenTransform+generationsUsed2GenSolve;		
	    delegate.printTextLn("Total generations: " + totalGenerations);
		delegate.printTextLn("Solution:");

		// output results
		if(alreadyIn2x2x3==0){
			for(int i=0; i<solution1.length;i++){
				delegate.printText(solution1[i]);
			}
			delegate.printTextLn("");
		}	

		if(alreadyIn2GenTransform==0){
			for(int i=0; i<solution2.length;i++){
				delegate.printText(solution2[i]);
			}
			delegate.printTextLn("");
		}	

		if(alreadyIn2GenSolve==0){
			for(int i=0; i<solution3.length;i++){
				delegate.printText(solution3[i]);
			}
			delegate.printTextLn("");
		}
		if (repetition != repetitions){
			delegate.printTextLn("");
		}
	}

	// Write test results to csv file
	if(csvEnabled){

	  //boolean exists = new File("hugo_statistics.csv").exists();
		boolean exists = csvFile.exists();
	  try{
		  FileWriter writer1 = new FileWriter(csvFile.getAbsolutePath(), true);
		  PrintWriter print1=new PrintWriter(writer1);
		  if(!exists){
			  print1.println("No.,Scramble,MovesScramble,MinGenerations2x2x3,MinGenerations2GenTransform,MinGenerations2GenSolve,AlreadyIn2x2x3,Generations2x2x3,Time2x2x3,Time2x2x3Ms,Moves2x2x3,Solution2x2x3,AlreadyIn2GenTransform,Generations2GenTransform,Time2GenTransform,Time2GenTransformMs,Moves2GenTransform,Solution2GenTransform,AlreadyIn2GenSolve,Generations2GenSolve,Time2GenSolve,Time2GensolveMs,Moves2GenSolve,Solution2GenSolve,TotalGenerations,TotalTime,TotalTimeMs,TotalMoves,Solution,OptimizedTotalMoves,OptimizedSolution");
		  }
		  print1.println(repetition+","+arrayToString(scrambleNotation)+","+(scrambleNotation.length/3)+","+generations2x2x3+","+generations2GenTransform+","+generations2GenSolve+","+alreadyIn2x2x3+","+generationsUsed2x2x3+","+sdf.format(wholeTime2x2x3)+","+wholeTime2x2x3+","+moves2x2x3+","+arrayToString(solution1)+","+alreadyIn2GenTransform+","+generationsUsed2GenTransform+","+sdf.format(wholeTime2GenTransform)+","+wholeTime2GenTransform+","+moves2GenTransform+","+arrayToString(solution2)+","+alreadyIn2GenSolve+","+generationsUsed2GenSolve+","+sdf.format(wholeTimeCubeSolve)+","+wholeTimeCubeSolve+","+moves2GenSolve+","+arrayToString(solution3)+","+totalGenerations+","+sdf.format(wholeTimeSolution)+","+wholeTimeSolution+","+movesTotal+","+arrayToString(solution)+","+optimizedMovesTotal+","+arrayToString(optimizedSolution));		  
		  writer1.close();
	  } catch(Exception e) {
		  // do nothing
	  }
	}
	  translateSequence(optimizedIntSolution);
	  return optimizedSolution;
	}	

	/* -----------------------------------------------------------------------------------------
	 * functions 
	 * -----------------------------------------------------------------------------------------
	 */
	
	/**
	 * Translates sequence from 0-17 notation to f and q (face and quarter turn) notation.
	 * 
	 * @param sequence the sequence
	 */
	private void translateSequence(int[] sequence) {
		for(int i=0; i<sequence.length; i++) {
			
			// calc q
			if(sequence[i] > 11) {
				q.add(3);
			} else if(sequence[i] > 5) {
				q.add(2);
			} else {
				q.add(1);
			}
			
			// calc f
			if(sequence[i] % 6 == 0) {
				f.add(2);
			} else if(sequence[i] % 6 == 1) {
				f.add(1);
			} else if(sequence[i] % 6 == 2) {
				f.add(3);
			} else if(sequence[i] % 6 == 3) {
				f.add(5);
			} else if(sequence[i] % 6 == 4) {
				f.add(4);
			} else if(sequence[i] % 6 == 5) {
				f.add(0);
			}
		}
	}
	
	// Fetches faces
	/**
	 * Gets the face.
	 * 
	 * @return the f
	 */
	public int[] getF() {
		int[] result = new int[f.size()];
		for(int i=0; i<f.size(); i++) {
			result[i] = f.get(i);
		}
		return result;
	}
	
	// Fetches quarter turns
	/**
	 * Gets the quarter turn.
	 * 
	 * @return the q
	 */
	public int[] getQ() {
		int[] result = new int[q.size()];
		for(int i=0; i<q.size(); i++) {
			result[i] = q.get(i);
		}
		return result;
	}

	/**
	 * Converts String Arrays to a String.
	 * 
	 * @param array the array
	 * 
	 * @return the string
	 */
	public static String arrayToString(String[] array){
		if(array==null){
			return "null";
		}
		if(array.length==0){
			return "";
		}
		StringBuilder buf = new StringBuilder();
		for(int i=0; i<array.length; i++){
			buf.append(array[i]);
		}
		return buf.toString();
	}

	/**
	 * Converts the sequence numbers to the corresponding char values for visualization.
	 * 
	 * @param sequenceNumbers the sequence numbers
	 * 
	 * @return the string[]
	 */
	public static String[] toString(int[] sequenceNumbers){
		String[] numbersToString = new String[sequenceNumbers.length*3]; //for each number there are 3 array spaces in char-metric: side, ' or 2, and an empty space
		int sequenceSize=sequenceNumbers.length;

		for(int a=1; a<=sequenceSize;a++){
			switch(sequenceNumbers[a-1]%6){   
		     case  0: numbersToString[3*a-2-1] = "F"; break;
		     case  1: numbersToString[3*a-2-1] = "U"; break;
		     case  2: numbersToString[3*a-2-1] = "R"; break;
		     case  3: numbersToString[3*a-2-1] = "B"; break;
		     case  4: numbersToString[3*a-2-1] = "D"; break;
		     case  5: numbersToString[3*a-2-1] = "L"; break;
			}

			numbersToString[3*a-1-1] = " ";
			if (sequenceNumbers[a-1]>5) numbersToString[3*a-1-1]="2";
			if (sequenceNumbers[a-1]>11) numbersToString[3*a-1-1]="'";
			numbersToString[3*a-1]=" ";
		}
		return  numbersToString;
	}
	
	// Searches in 2-dimensional array sequence_string for characters. Each sequence_string only contains one move that is returned as number.
	/**
	 * Searches in two-dimensional array sequence_string for characters and returns the corresponding number.
	 * Each sequence_string only contains one move that is returned as number.
	 * 
	 * @param sequence_string the sequence_string
	 * 
	 * @return the int
	 */
	public static int toNumbers(char[] sequence_string){
		int stringToNumbers = 0;
		for(int a=1; a<=sequence_string.length;a++){
			switch((sequence_string[a-1])){   
		     case  'F': stringToNumbers = 0; break;
		     case  'U': stringToNumbers = 1; break;
		     case  'R': stringToNumbers = 2; break;
		     case  'B': stringToNumbers = 3; break;
		     case  'D': stringToNumbers = 4; break;
		     case  'L': stringToNumbers = 5; break;
		     case  '2': stringToNumbers = stringToNumbers+6; break;
		     case  '\'': stringToNumbers = stringToNumbers+12; break;
			}
		}
		return stringToNumbers;
	}

	/**
	 * Initializes the cube.
	 * Gives all facelets on one side of the cube the same number
	 * 
	 * @return the int[]
	 */
	public static int[] initCube(){
		int[] init = new int[54];
		int index=0;
		int temp=0;

		for(int m=0; m<=5; m++){
			for(int n=0; n<=8; n++){
				init[index] = temp;
				index = index+1;
			}
			temp = temp+1;
		}
		return init;
	}

	/**
	 * Prints the cube status.
	 * 
	 * @param cube the cube
	 */
	public void printCube(int[] cube){
		for(int i = 0; i<=5; i++){
			switch(i){
				case 0: delegate.printTextLn("F"); break;
				case 1: delegate.printTextLn("U"); break;
				case 2: delegate.printTextLn("R"); break;
				case 3: delegate.printTextLn("B"); break;
				case 4: delegate.printTextLn("D"); break;
				case 5: delegate.printTextLn("L"); break;
			}

			for(int j = 0; j<=2; j++){
				delegate.printText(""+cube[i*9 + j]);
			}

			delegate.printTextLn("");
			for(int j = 3; j<=5; j++){
				delegate.printText(""+cube[i*9 + j]);
			}

			delegate.printTextLn("");
			for(int j = 6; j<=8; j++){
				delegate.printText(""+cube[i*9 + j]);
			}
			delegate.printTextLn("");
		}
	}

	/**
	 * Executes a move sequence on the cube.
	 * 
	 * @param cube the cube
	 * @param moveSequence the move sequence
	 * 
	 * @return the int[]
	 */
	public static int[] doSequence(int[] cube, int[] moveSequence){
		int[] doSequence = doMove(cube, moveSequence[0]); //initialization for later recursive calls
		if(moveSequence.length>1){
			for(int i=1; i<moveSequence.length; i++){
				doSequence = doMove(doSequence, moveSequence[i]);
			}
		}
		return doSequence;
	}

	/**
	 * Executes a move on the cube.
	 * 
	 * @param cubeState the cube state
	 * @param move the move
	 * 
	 * @return the int[]
	 */
	public static int[] doMove(int[] cubeState, int move){
			int[] doMove = cubeState.clone();	
			int repetition = 0;
			if(move>5) repetition = 1;
			if(move>11) repetition = 2;
			
			for(int i = 0; i<=repetition; i++){
				int[] cube = doMove.clone(); //clone copies by value, otherwise it would be by reference
				doMove = turnFace(doMove, move%6); //turn only one side

				//turn the surrounding facelets
				switch(move%6){
				case 0:
					doMove[15] = cube[53];
					doMove[16] = cube[50];
					doMove[17] = cube[47];
					doMove[18] = cube[15];
					doMove[21] = cube[16];
					doMove[24] = cube[17];
					doMove[36] = cube[24];
					doMove[37] = cube[21];
					doMove[38] = cube[18];
					doMove[47] = cube[36];
					doMove[50] = cube[37];
					doMove[53] = cube[38]; break;

				case 1:
					doMove[0] = cube[18];
					doMove[1] = cube[19];
					doMove[2] = cube[20];
					doMove[18] = cube[27];
					doMove[19] = cube[28];
					doMove[20] = cube[29];
					doMove[27] = cube[45];
					doMove[28] = cube[46];
					doMove[29] = cube[47];
					doMove[45] = cube[0];
					doMove[46] = cube[1];
					doMove[47] = cube[2]; break;

				case 2:
					doMove[2] = cube[38];
					doMove[5] = cube[41];
					doMove[8] = cube[44];
					doMove[11] = cube[2];
					doMove[14] = cube[5];
					doMove[17] = cube[8];
					doMove[27] = cube[17];
					doMove[30] = cube[14];
					doMove[33] = cube[11];
					doMove[38] = cube[33];
					doMove[41] = cube[30];
					doMove[44] = cube[27]; break;

				case 3:

					doMove[9] = cube[20];
					doMove[10] = cube[23];
					doMove[11] = cube[26];
					doMove[20] = cube[44];
					doMove[23] = cube[43];
					doMove[26] = cube[42];
					doMove[42] = cube[45];
					doMove[43] = cube[48];
					doMove[44] = cube[51];
					doMove[45] = cube[11];
					doMove[48] = cube[10];
					doMove[51] = cube[9]; break;

				case 4:

					doMove[6] = cube[51];
					doMove[7] = cube[52];
					doMove[8] = cube[53];
					doMove[24] = cube[6];
					doMove[25] = cube[7];
					doMove[26] = cube[8];
					doMove[33] = cube[24];
					doMove[34] = cube[25];
					doMove[35] = cube[26];
					doMove[51] = cube[33];
					doMove[52] = cube[34];
					doMove[53] = cube[35]; break;

				case 5:

					doMove[0] = cube[9];
					doMove[3] = cube[12];
					doMove[6] = cube[15];
					doMove[9] = cube[35];
					doMove[12] = cube[32];
					doMove[15] = cube[29];
					doMove[29] = cube[42];
					doMove[32] = cube[39];
					doMove[35] = cube[36];
					doMove[36] = cube[0];
					doMove[39] = cube[3];
					doMove[42] = cube[6]; break;
				}
			}
			return doMove;
		}	

	/**
	 * Turns the face stickers of the side to turn clockwise. Not the stickers on the surrounding sides.
	 * 
	 * @param cube the cube
	 * @param face the face
	 * 
	 * @return the int[]
	 */
	public static int[] turnFace(int[] cube, int face){
		int[] turnFace = cube.clone();
		int offset = 9*face; //e.g. 9*1 --> 9,...,17 are turned 

		//modulo maybe unnessessary
		turnFace[offset%54] = cube[(offset+6)%54];
		turnFace[(offset+1)%54] = cube[(offset+3)%54];
		turnFace[(offset+2)%54] = cube[(offset+0)%54];
		turnFace[(offset+3)%54] = cube[(offset+7)%54];
		turnFace[(offset+5)%54] = cube[(offset+1)%54];
		turnFace[(offset+6)%54] = cube[(offset+8)%54];
		turnFace[(offset+7)%54] = cube[(offset+5)%54];
		turnFace[(offset+8)%54] = cube[(offset+2)%54];
		return turnFace;
	}

	/**
	 * Rotates cube clockwise around X and then Y axis.
	 * 
	 * @param cube the cube
	 * 
	 * @return the new cube
	 */
	public static int[] rotateCubeXY(int[] cube){
		int[] rotateCubeXY = new int[54];

		// R->F
		int[] positionsF = {24,21,18,25,22,19,26,23,20};
		for (int i = 0; i <= 8; i++){
			rotateCubeXY[i] = cube[positionsF[i]];
		}

		// f->U
		int[] positionsU = {6,3,0,7,4,1,8,5,2};
		for (int i = 9; i <= 17; i++){
			rotateCubeXY[i] = cube[positionsU[i-9]];
		}

		// U->R
		int[] positionsR = {17,16,15,14,13,12,11,10,9};
		for (int i = 18; i <= 26; i++){
			rotateCubeXY[i] = cube[positionsR[i-18]];
		}

		// L->B
		int[] positionsB = {47,50,53,46,49,52,45,48,51};
		for (int i = 27; i <= 35; i++){
			rotateCubeXY[i] = cube[positionsB[i-27]];
		}

		// B->D
		int[] positionsD = {33,30,27,34,31,28,35,32,29};
		for (int i = 36; i <= 44; i++){
			rotateCubeXY[i] = cube[positionsD[i-36]];
		}

		System.arraycopy(cube, 36, rotateCubeXY, 45, 9); // D->L
		
		return rotateCubeXY;
	}

	// rotates cube clockwise around Y axis

	/**
	 * Rotates cube clockwise around Y axis.
	 * 
	 * @param cube the cube
	 * 
	 * @return the new cube
	 */
	public static int[] rotateCubeY(int[] cube){
		int[] rotateCubeY = new int[54];
		// U
		int[] positionsU = {15,12,9,16,13,10,17,14,11};
		for (int i = 9; i <= 17; i++){
			rotateCubeY[i] = cube[positionsU[i-9]];
		}

		// D
		int[] positionsD = {38,41,44,37,40,43,36,39,42};
		for (int i = 36; i <= 44; i++){
			rotateCubeY[i] = cube[positionsD[i-36]];
		}

		System.arraycopy(cube, 18, rotateCubeY, 0, 9); // R->F
		System.arraycopy(cube, 27, rotateCubeY, 18, 9); // B->R
		System.arraycopy(cube, 45, rotateCubeY, 27, 9); // L->B
		System.arraycopy(cube, 0, rotateCubeY, 45, 9); // F->L
		
		return rotateCubeY;
	}

	// rotates cube clockwise around X axis: (= rotate XY + Y+Y+Y), (maybe easier to teach X and Y and then XY) 

	/**
	 * Rotates cube clockwise around X axis. 
	 * 
	 * @param cube the cube
	 * 
	 * @return the new cube
	 */
	public static int[] rotateCubeX(int[] cube){

		int[] rotateCubeX = rotateCubeXY(cube).clone();
		for(int i=1; i<=3; i++){
			rotateCubeX = rotateCubeY(rotateCubeX);
		}
		return rotateCubeX;
	}	

	/**
	 * Restores Y turn.
	 * 
	 * @param solution the solution
	 * 
	 * @return the new solution
	 */
	public static int[] restoreTurnY(int[]solution){
		int[] solutionTemp = solution.clone();	
		for(int i=0; i<solutionTemp.length;i++){	
			switch(solutionTemp[i]%6){   
			case  3: solutionTemp[i] = solutionTemp[i]+2; break;
			case  5: solutionTemp[i] = solutionTemp[i]-5; break;
			case  0: solutionTemp[i] = solutionTemp[i]+2; break;
			case  2: solutionTemp[i] = solutionTemp[i]+1; break;
			}
		}
		return solutionTemp;
	}	

	/**
	 * Restores X turn.
	 * 
	 * @param solution the solution
	 * 
	 * @return the new solution
	 */
	public static int[] restoreTurnX(int[]solution){
		int[] solutionTemp = solution.clone();
		for(int i=0; i<solutionTemp.length;i++){	
			switch(solutionTemp[i]%6){   
			case  1: solutionTemp[i] = solutionTemp[i]-1; break;
			case  0: solutionTemp[i] = solutionTemp[i]+4; break;
			case  3: solutionTemp[i] = solutionTemp[i]-2; break;
			case  4: solutionTemp[i] = solutionTemp[i]-1; break;
			}
		}
		return solutionTemp;
	}	

	/**
	 * Restore XY turn.
	 * 
	 * @param solution the solution
	 * 
	 * @return the new solution
	 */
	public static int[] restoreTurnXY(int[]solution){
		int[] solutionTemp = solution.clone();
		for(int i=0; i<solutionTemp.length;i++){	
			switch(solutionTemp[i]%6){   
			case  3: solutionTemp[i] = solutionTemp[i]+2; break;
			case  5: solutionTemp[i] = solutionTemp[i]-5; break;
			case  0: solutionTemp[i] = solutionTemp[i]+2; break;
			case  2: solutionTemp[i] = solutionTemp[i]+1; break;
			}
		}	

		for(int i=0; i<solutionTemp.length;i++){	
			switch(solutionTemp[i]%6){   
			case  1: solutionTemp[i] = solutionTemp[i]-1; break;
			case  0: solutionTemp[i] = solutionTemp[i]+4; break;
			case  3: solutionTemp[i] = solutionTemp[i]-2; break;
			case  4: solutionTemp[i] = solutionTemp[i]-1; break;
			}
		}
		return solutionTemp;
	}	

	/**
	 * Tests whether the cube is in the two-generator subgroup.
	 * 
	 * @param cube the cube
	 * 
	 * @return is in two-generator
	 */
	public static int is2gen(int[] cube){
		int is2gen = 0;
		int edgesOriented = 0;
		int[] cornerTemp = new int[7];
		int[] cornersPos = new int[7];
		int code;

		// Test for correct edges.
		// In two generator group edges cannot be switched in orientation.
		// So all edges must be in the correct orientation to reach solved cube in two generator.
		// Each of the 7 edge pieces that are moveable in 2gen group are checked if one sticker is correct.
		// If at least one sticker is correct, the edge is in correct orientation.
		// If all 7 edges are correct, edgesOriented is counted up to 7.
		if(cube[1] == cube[4] || cube[16] == cube[13]) edgesOriented = edgesOriented +1;
		if(cube[3] == cube[4] || cube[50] == cube[13]) edgesOriented = edgesOriented +1;
		if(cube[5] == cube[4] || cube[21] == cube[13]) edgesOriented   = edgesOriented +1;
		if(cube[7] == cube[4] || cube[37] == cube[13]) edgesOriented   = edgesOriented +1;
		if(cube[28] == cube[4] || cube[10] == cube[13]) edgesOriented  = edgesOriented +1;
		if(cube[46] == cube[4] || cube[12] == cube[13]) edgesOriented  = edgesOriented +1;
		if(cube[19] == cube[4] || cube[14] == cube[13]) edgesOriented  = edgesOriented +1;
		if (edgesOriented < 7) return is2gen;

		// Check for correct corners
		//	5  6  U face
		//	3  4
		//	1  2  F face
		// Checks the position of the corners: 1st test (link 5-1, 6-2, 3-4)
		// First for each corner position it is checked which corner is in this position and a code defines the kind of corner.
		cornerTemp[1] = (int)(Math.pow(2,cube[6]) + Math.pow(2,cube[36]) + Math.pow(2,cube[53])); //FDL positioned corner of cube
		cornerTemp[2] = (int)(Math.pow(2,cube[8]) + Math.pow(2,cube[38]) + Math.pow(2,cube[24])); //FDR
		cornerTemp[3] = (int)(Math.pow(2,cube[0]) + Math.pow(2,cube[15]) + Math.pow(2,cube[47])); //FUL
		cornerTemp[4] = (int)(Math.pow(2,cube[2]) + Math.pow(2,cube[17]) + Math.pow(2,cube[18])); //FUR		
		cornerTemp[5] = (int)(Math.pow(2,cube[29]) + Math.pow(2,cube[9]) + Math.pow(2,cube[45])); //BUL
		cornerTemp[6] = (int)(Math.pow(2,cube[27]) + Math.pow(2,cube[11]) + Math.pow(2,cube[20])); //BUR

		// Here it is checked which kind of corner is really in the corner position by comparing the code to the code of known kinds of corners and a value depending on the link is assigned.
		for(int i = 1; i<=6; i++){
		if (cornerTemp[i] == Math.pow(2,cube[4]) + Math.pow(2,cube[40]) + Math.pow(2,cube[49])) cornersPos[i]=0;
		if (cornerTemp[i] == Math.pow(2,cube[4]) + Math.pow(2,cube[40]) + Math.pow(2,cube[22])) cornersPos[i]=1;
		if (cornerTemp[i] == Math.pow(2,cube[4]) + Math.pow(2,cube[13]) + Math.pow(2,cube[49])) cornersPos[i]=2;
		if (cornerTemp[i] == Math.pow(2,cube[4]) + Math.pow(2,cube[13]) + Math.pow(2,cube[22])) cornersPos[i]=2;
		if (cornerTemp[i] == Math.pow(2,cube[31]) + Math.pow(2,cube[13]) + Math.pow(2,cube[49])) cornersPos[i]=0;
		if (cornerTemp[i] == Math.pow(2,cube[31]) + Math.pow(2,cube[13]) + Math.pow(2,cube[22])) cornersPos[i]=1;
		}
		
		code = 100000*cornersPos[1]+10000*cornersPos[2]+1000*cornersPos[3]+100*cornersPos[4]+10*cornersPos[5]+cornersPos[6];	
		int[] codeAllowed = {12201,1221,11022,12120,10212,102210,110220,100122,102021,101202,210021,221001,211200,210102,212010,21102,2112,22011,21210,20121,120012,112002,122100,120201,121020,201120,220110,200211,201012,202101};

		// sets is2gen to 1 if 1st test successful. Else returns is2gen(=0). 
		for(int i = 0; i<codeAllowed.length; i++){
			if (code == codeAllowed[i]){
				is2gen = 1;
			}
		}
		if (is2gen == 0){
			return is2gen;
		}

		// 2nd test (link 5-2, 6-4, 3-1)
		for(int i=1; i<=6; i++){
		if (cornerTemp[i] == Math.pow(2,cube[4])+Math.pow(2,cube[40])+Math.pow(2,cube[49])) cornersPos[i]=0;
		if (cornerTemp[i] == Math.pow(2,cube[4])+Math.pow(2,cube[40])+Math.pow(2,cube[22])) cornersPos[i]=1;
		if (cornerTemp[i] == Math.pow(2,cube[4])+Math.pow(2,cube[13])+Math.pow(2,cube[49])) cornersPos[i]=0;
		if (cornerTemp[i] == Math.pow(2,cube[4])+Math.pow(2,cube[13])+Math.pow(2,cube[22])) cornersPos[i]=2;
		if (cornerTemp[i] == Math.pow(2,cube[31])+Math.pow(2,cube[13])+Math.pow(2,cube[49])) cornersPos[i]=1;
		if (cornerTemp[i] == Math.pow(2,cube[31])+Math.pow(2,cube[13])+Math.pow(2,cube[22])) cornersPos[i]=2;
		}

		code = 100000*cornersPos[1]+10000*cornersPos[2]+1000*cornersPos[3]+100*cornersPos[4]+10*cornersPos[5]+cornersPos[6];

		//returns 1 if 1st and 2nd test are 1. Else 0.
		for(int i = 0; i<codeAllowed.length; i++){
			if ((code == codeAllowed[i])&&((is2x2x3(cube))==16)){
				is2gen = 1;
				return is2gen;
			}
		}
		is2gen = 0;
		return is2gen;
	}

	/**
	 * Optimization of the move sequence. If possible the move sequence becomes reduced in length.
	 * 
	 * @param movesSequence the moves sequence
	 * 
	 * @return the optimized move sequence
	 */
	public static int[] optimization(int[] movesSequence){
		int geneSize = movesSequence.length;
		int[] optimization = movesSequence.clone();
		int shoots = 0;
		int move, nextMove;

		for (int i = 0; i<geneSize-1; i++){
			move = optimization[i];
			nextMove = optimization[i+1];
			if(move%6 == nextMove%6){  // second move is same direction as first move
				if(Math.abs(move-nextMove)==12){ //second move undoes first move
					System.arraycopy(optimization, i+2, optimization, i, geneSize-(i+2)); //both moves deleted
					for(shoots = 0; shoots < 2; shoots++){
						optimization[geneSize-2+shoots] = (int)(Math.random()*18);
					}
				}

				if(move<6){ // quarter turn
					if(nextMove == move){ // turn+turn=turn2
						optimization[i]=move+6; //first move compensates both moves
						System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
						optimization[geneSize-1] = (int)(Math.random()*18);
					}else if(nextMove == move+6){ //turn + turn2 = turn'
						optimization[i]=move+12;
						System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
						optimization[geneSize-1] = (int)(Math.random()*18);
					}
				}
				if(move<12){
					if(move>=6){
						if(nextMove == move-6){ //turn2 + turn = turn'
							optimization[i]=move+6;
							System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
							optimization[geneSize-1] = (int)(Math.random()*18);
						}else if(nextMove == move){ //turn2 + turn2 = no turn
							System.arraycopy(optimization, i+2, optimization, i, geneSize-(i+2)); //both moves deleted
							for(shoots = 0; shoots < 2; shoots++){
								optimization[geneSize-2+shoots] = (int)(Math.random()*18);
							}
						}else if(nextMove == move+6){ //turn2 + turn' = turn
							optimization[i] = move-6;
							System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
							optimization[geneSize-1] = (int)(Math.random()*18);
						}
					}
				}
				if(move>=12){
					if(nextMove == move-6){ //turn' + turn2 = turn
						optimization[i] = move-12;
						System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
						optimization[geneSize-1] = (int)(Math.random()*18);
					}else if(nextMove == move){ //face' + face' = face2
						optimization[i] = move-6;
						System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
						optimization[geneSize-1] = (int)(Math.random()*18);
					}	
				}
			}
		}
		return optimization;
	}

	/**
	 * Optimization of the solution sequence. If possible the solution sequence becomes reduced in length.
	 * 
	 * @param movesSequence the moves sequence
	 * 
	 * @return the optimized solution sequence
	 */
	public static int[] optimizeEndSequence(int[] movesSequence){
		int geneSize = movesSequence.length;
		int[] optimization = movesSequence.clone();
		int shoots = 0;
		int optimizeEndSequenceCount = 0;
		int move, nextMove;

		for (int i = 0; i<geneSize-1; i++){
			move = optimization[i];
			nextMove = optimization[i+1];
		if((move != -1) && (nextMove != -1)){	
			if(move%6 == nextMove%6){  //second move is same direction as first move
				if(Math.abs(move-nextMove)==12){ //second move undoes first move
					System.arraycopy(optimization, i+2, optimization, i, geneSize-(i+2)); //both moves deleted
					for(shoots = 0; shoots < 2; shoots++){
						optimization[geneSize-2+shoots] = -1;
					}
					optimizeEndSequenceCount+=2;
				}
				if(move<6){ //quarter turn
					if(nextMove == move){ // turn+turn=turn2
						optimization[i]=move+6; //first move compensates both moves
						System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
						optimization[geneSize-1] = -1;
						optimizeEndSequenceCount+=1;
					}else if(nextMove == move+6){ //turn + turn2 = turn'
						optimization[i]=move+12;
						System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
						optimization[geneSize-1] = -1;
						optimizeEndSequenceCount+=1;
					}
				}
				if(move<12){
					if(move>=6){
						if(nextMove == move-6){ //turn2 + turn = turn'
							optimization[i]=move+6;
							System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
							optimization[geneSize-1] = -1;
							optimizeEndSequenceCount+=1;
						}else if(nextMove == move){ //turn2 + turn2 = no turn
							System.arraycopy(optimization, i+2, optimization, i, geneSize-(i+2)); //both moves deleted
							for(shoots = 0; shoots < 2; shoots++){
								optimization[geneSize-2+shoots] = -1;
							}
							optimizeEndSequenceCount+=2;
						}else if(nextMove == move+6){ //turn2 + turn' = turn
							optimization[i] = move-6;
							System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
							optimization[geneSize-1] = -1;
							optimizeEndSequenceCount+=1;
						}
					}
				}
				if(move>=12){
					if(nextMove == move-6){ //turn' + turn2 = turn
						optimization[i] = move-12;
						System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
						optimization[geneSize-1] = -1;
						optimizeEndSequenceCount+=1;
					}else if(nextMove == move){ //face' + face' = face2
						optimization[i] = move-6;
						System.arraycopy(optimization, i+2, optimization, i+1, geneSize-(i+2)); //second move deleted
						optimization[geneSize-1] = -1;
						optimizeEndSequenceCount+=1;
					}	
				}
			}	
			}
		}
		int[] optimizeEndSequence = new int[movesSequence.length-optimizeEndSequenceCount];
		System.arraycopy(optimization, 0, optimizeEndSequence, 0, optimizeEndSequence.length);
		return optimizeEndSequence;
	}

	/**
	 * The fitness function evaluates the fitness of move sequence.
	 * 
	 * @param cube the cube
	 * @param movesSequence the moves sequence
	 * @param functionType the function type
	 * 
	 * @return the fitness
	 */
	public static int[] fitnessFunction(int[] cube, int[] movesSequence, int functionType){
		int sequenceSize = movesSequence.length;
		int functionValue = 0; // fitess function value
		int functionValueMax = 0; // target function value maximum
		// functionValueMax = compare(cube, cubeSolved) // for facelet approach
		int position = 0; // position of the move on the individual
		int[] fitnessFunction = new int[2];

		if (sequenceSize > 1){
			for(int i = 0; i<sequenceSize; i++){
				cube = doMove(cube, movesSequence[i]);
				switch(functionType){
				case 1: functionValue = integrity(cube); break;
				case 2: functionValue = is2x2x3(cube); break;
				case 3: functionValue = 10*is2gen(cube) + 10*is2x2x3(cube); break;
				}
				if (functionValue>functionValueMax){
					functionValueMax = functionValue;
					position=i;
				}
			}
		}
		fitnessFunction[0] = 10*functionValueMax - (position+1); //10*functionvaluemax - #moves
		fitnessFunction[1] = position;
		return fitnessFunction;
	}

	/*
	// compares two cubes, e.g. cube with solved cube
	public static int compare(int[] cube1, int[] cube2){
		int compare = 0;
		for(int i = 0; i<54; i++){
			if(cube1[i]==cube2[i]){
				compare = compare+1;
			}
		}
		return compare;
	}
	*/
	
	/**
	 * Calculates the integrity of the cube, defined by sum(CEP) + sum(ECeP).
	 * CEP = corner-edge pairs.
	 * ECeP = edges-center pairs.
	 * 
	 * @param cube the cube
	 * 
	 * @return the int
	 */
	public static int integrity(int[] cube){ //integrity
		int integrity = 0;

		//Corner-edges pairs
		//FLU corner
		if(cube[0] == cube[1] && cube[15]== cube[16]) integrity = integrity+1;
		if(cube[0] == cube[3] && cube[47]== cube[50]) integrity = integrity+1;
		if(cube[47] == cube[46] && cube[15] == cube[12]) integrity = integrity+1;
		//FRU corner
		if(cube[2] == cube[5] && cube[18]== cube[21]) integrity = integrity+1;
		if(cube[2] == cube[1] && cube[17]== cube[16]) integrity = integrity+1;
		if(cube[17] == cube[14] && cube[18] == cube[19]) integrity = integrity+1;
		//FRD corner
		if(cube[8] == cube[5] && cube[24]== cube[21]) integrity = integrity+1;
		if(cube[8] == cube[7] && cube[38]== cube[37]) integrity = integrity+1;
		if(cube[38] == cube[41] && cube[24] == cube[25]) integrity = integrity+1;
		//FLD corner
		if(cube[6] == cube[3] && cube[53]== cube[50]) integrity = integrity+1;
		if(cube[6] == cube[7] && cube[36]== cube[37]) integrity = integrity+1;
		if(cube[36] == cube[39] && cube[53] == cube[52]) integrity = integrity+1;
		//BLU corner
		if(cube[29] == cube[32] && cube[45]== cube[48]) integrity = integrity+1;
		if(cube[29] == cube[28] && cube[9]== cube[10]) integrity = integrity+1;
		if(cube[9] == cube[12] && cube[45] == cube[46]) integrity = integrity+1;
		//BRU corner
		if(cube[27] == cube[28] && cube[11]== cube[10]) integrity = integrity+1;
		if(cube[27] == cube[30] && cube[20]== cube[23]) integrity = integrity+1;
		if(cube[11] == cube[14] && cube[20] == cube[19]) integrity = integrity+1;
		//BRD corner
		if(cube[33] == cube[34] && cube[44]== cube[43]) integrity = integrity+1;
		if(cube[33] == cube[30] && cube[26]== cube[23]) integrity = integrity+1;
		if(cube[44] == cube[41] && cube[26] == cube[25]) integrity = integrity+1;
		//BLD corner
		if(cube[35] == cube[34] && cube[42]== cube[43]) integrity = integrity+1;
		if(cube[35] == cube[32] && cube[51]== cube[48]) integrity = integrity+1;
		if(cube[42] == cube[39] && cube[51] == cube[52]) integrity = integrity+1;

		//Edges-centers pairs
		//F
		if(cube[4] == cube[1])  integrity = integrity+1;
		if(cube[4] == cube[3])  integrity = integrity+1;
		if(cube[4] == cube[5])  integrity = integrity+1;
		if(cube[4] == cube[7])  integrity = integrity+1;
		//U
		if(cube[13] == cube[10])  integrity = integrity+1;
		if(cube[13] == cube[12])  integrity = integrity+1;
		if(cube[13] == cube[14])  integrity = integrity+1;
		if(cube[13] == cube[16])  integrity = integrity+1;
		//R
		if(cube[22] == cube[19])  integrity = integrity+1;
		if(cube[22] == cube[21])  integrity = integrity+1;
		if(cube[22] == cube[23])  integrity = integrity+1;
		if(cube[22] == cube[25])  integrity = integrity+1;
		//B
		if(cube[31] == cube[28])  integrity = integrity+1;
		if(cube[31] == cube[30])  integrity = integrity+1;
		if(cube[31] == cube[32])  integrity = integrity+1;
		if(cube[31] == cube[34])  integrity = integrity+1;
		//D
		if(cube[40] == cube[37])  integrity = integrity+1;
		if(cube[40] == cube[39])  integrity = integrity+1;
		if(cube[40] == cube[41])  integrity = integrity+1;
		if(cube[40] == cube[43])  integrity = integrity+1;
		//L
		if(cube[49] == cube[46])  integrity = integrity+1;
		if(cube[49] == cube[48])  integrity = integrity+1;
		if(cube[49] == cube[50])  integrity = integrity+1;
		if(cube[49] == cube[52])  integrity = integrity+1;
	
		return integrity;
	}

	/**
	 * Checks whether 2x2x3 subcube is solved.
	 * The same like function integrity does.
	 * Function looks whether 2x2x3 is in BD with CE and ECe pairs test.
	 * If 2x2x3 is 16, the 2x2x3 is complete.
	 * 
	 * @param cube the cube
	 * 
	 * @return 2x2x3 solved
	 */
	public static int is2x2x3(int[] cube){
		int is2x2x3 = 0;

		//Corner-edges pairs
		//BRD corner
		if(cube[33] == cube[34] && cube[44]== cube[43]) is2x2x3 = is2x2x3+1;
		if(cube[33] == cube[30] && cube[26]== cube[23]) is2x2x3 = is2x2x3+1;
		if(cube[44] == cube[41] && cube[26] == cube[25]) is2x2x3 = is2x2x3+1;
		//BLD corner
		if(cube[35] == cube[34] && cube[42]== cube[43]) is2x2x3 = is2x2x3+1;
		if(cube[35] == cube[32] && cube[51]== cube[48]) is2x2x3 = is2x2x3+1;
		if(cube[42] == cube[39] && cube[51] == cube[52]) is2x2x3 = is2x2x3+1;

		// Edges-Centers pairs
		//R
		if(cube[22] == cube[23])  is2x2x3 = is2x2x3+1;
		if(cube[22] == cube[25])  is2x2x3 = is2x2x3+1;
		//B
		if(cube[31] == cube[30])  is2x2x3 = is2x2x3+1;
		if(cube[31] == cube[32])  is2x2x3 = is2x2x3+1;
		if(cube[31] == cube[34])  is2x2x3 = is2x2x3+1;
		//D
		if(cube[40] == cube[39])  is2x2x3 = is2x2x3+1;
		if(cube[40] == cube[41])  is2x2x3 = is2x2x3+1;
		if(cube[40] == cube[43])  is2x2x3 = is2x2x3+1;
		//L
		if(cube[49] == cube[48])  is2x2x3 = is2x2x3+1;
		if(cube[49] == cube[52])  is2x2x3 = is2x2x3+1;

		return is2x2x3;
	}

	/**
	 * Returns the population sorted after the fitness values of each individual.
	 * 
	 * @param population the population
	 * @param FitnessValuesIn the fitness values in
	 * 
	 * @return the sorted population
	 */
	public static int[][] populationSort(int[][] population, int[] FitnessValuesIn){
		int[][] populationSort = population.clone();
		int maxIndex = 0;
		int FitnessValues[] = FitnessValuesIn.clone();

		for(int i=0; i<population.length; i++){
			for (int j=0; j<FitnessValues.length; j++) {
				if (FitnessValues[j] > FitnessValues[maxIndex]) {
				maxIndex = j;
				}
			}
			populationSort[i]=population[maxIndex];
			FitnessValues[maxIndex]=-10000;	
		}
		return populationSort;
	}

	/**
	 * Returns the sum of values of the first column of the input array.
	 * 
	 * @param fitness the fitness
	 * 
	 * @return the sum
	 */
	public static int sumOfFitnessValues(int[][] fitness){
		int[] columnArray = new int[fitness.length];
		int sum = 0;
		for(int i=0; i<fitness.length; i++){
			columnArray[i] = fitness[i][0];
		}

		for (int i : columnArray) {
		sum += i;
		}
		return sum;
	}
 
	/**
	 * Mutates random genes of an individual with probability mutationProbability. The first gene and the best individual are excluded. 
	 * 
	 * @param population the population
	 * @param mutationProbability the mutation probability
	 * 
	 * @return the int[][]
	 */
	public static int[][] mutate(int[][] population, double mutationProbability){
		int n1 = population.length;
		int n2 = population[0].length;
		int[][] mutate = population.clone();

		for(int i=1; i<n1; i++){ // best gene does not mutate
			for(int j=0; j<n2; j++){
				if(Math.random()<mutationProbability){
					mutate[i][j]=(int)(Math.random()*18);
				}
			}
		}
		return mutate;
	}

	/**
	 * Selection operator using stochastic universal sampling or linear ranking exchangeable.
	 * 
	 * @param population the population
	 * @param fitness the fitness
	 * @param selectionType the selection type
	 * 
	 * @return the mating pool
	 */
	public static int[][] selection(int[][] population, int[][] fitness, int selectionType){
		int n = population.length;
		double[] selectionBorder = new double[population.length+1]; // determines probability of getting selected
		double r1; // random variable
		int SUS = selectionType;
		int[][] selection = new int[population.length][population[0].length];
		int sumOfFitnessValues = 0;
		int sumOfRanks = 0;
		double[] normalizedFitnessValues = new double[n];
		
		// calculates sumOfRanks
		for(int i=1; i<=n; i++){
			sumOfRanks=sumOfRanks+i;
		}

		// calculates sumOfFitnessValues
		for(int i=0; i<n; i++){
			sumOfFitnessValues=sumOfFitnessValues+fitness[i][0];
		}

		// calculates normalizedFitnessValues
		for(int i=0; i<n; i++){
			normalizedFitnessValues[i]=((double)fitness[i][0])/((double)sumOfFitnessValues);
		}

		// calculates the probability of selection depending on target value (Stochastic Universal Sampling) or rank (Linear Ranking)
		selectionBorder[n] = 0; // to add 0 on the first run
		for (int i=n-1; i>=0; i--){
			if(SUS==1){ // Stochastic Universal Sampling used
				selectionBorder[i] = normalizedFitnessValues[i]+selectionBorder[i+1];
			}else{ // Linear Ranking used
				selectionBorder[i] = ((double)(n-i))/((double)sumOfRanks)+selectionBorder[i+1];
			}
		}
		selectionBorder[0]=1; // to prevent errors if best border is not 1 due to roundings
		selection[0]=population[0].clone(); // elite strategy: the best individual is conserved. starting at second individual (Number=1) 

		// Roulette wheel
		for(int i=1; i<n; i+=2){
			r1= Math.random();
			for(int j=n-1; j>=0; j--){
				if(r1<selectionBorder[j]){ // first pointer
					selection[i]=population[j].clone();
					for(int k=n-1; k>=0; k--){
						if((r1+0.5)%1<selectionBorder[k]){ // second pointer
							selection[i+1]=population[k].clone();
							break;
						}
					}	
					break;
				}
			}
		}	
		return selection;
	}

	// 
	/**
	 * Recombines couples from the population of individuals.
	 * 
	 * @param population the population
	 * @param crossoverProbability the crossover probability
	 * 
	 * @return the recombined population
	 */
	public static int[][] crossover(int[][] population, double crossoverProbability){
		int n = population.length;
		int geneSize=population[0].length;
		int[] bitPattern = new int[geneSize]; // bit pattern containing 0s and 1s

		// The first gene of population is the elite gene and wont be changed 
		int[][] crossover = new int[n][geneSize];
		crossover[0]=population[0].clone();

		// Uniform crossover, always two pairs shuffled by selection
		for(int i=0; i<n-1; i+=2){ // all except last gene are used for crossover
			
			// Fill bitPattern randomly with 0s and 1s
			for(int l=0; l<geneSize; l++){
				bitPattern[l]=(int)(Math.random()*2);
			}

			// Crossover
			if(Math.random()<crossoverProbability){
				for(int j=0; j<geneSize; j++){
					if(bitPattern[j]==0){
						crossover[i+1][j]=population[i][j];
						crossover[i+2][j]=population[i+1][j];
					}else{
						crossover[i+1][j]=population[i+1][j];
						crossover[i+2][j]=population[i][j];
					}
				}
			}else{
				crossover[i+1]=population[i].clone();
				crossover[i+2]=population[i+1].clone();	
			}		
		}
		return crossover;
	}
}