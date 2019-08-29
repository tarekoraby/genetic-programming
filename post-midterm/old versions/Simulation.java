package v1;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class Simulation {

	static ExecutorService executor;
	static int threads, currentLevel, currentDeme, numOfDemes;
	static Deme[][] demes;
	static Strategy[][] bestStrategies;
	static Strategy[] testedStrategies;
	static Random rd= new Random();

	public Simulation() {
		executor = MasterVariables.executor;
		threads = MasterVariables.Threads;
		initializeSimulation();
	}

	public static void start() {
		for (MasterVariables.currentGen = 1; MasterVariables.currentGen <= MasterVariables.MASTER_GENERATIONS; MasterVariables.currentGen++) {
			for (currentLevel = MasterVariables.MINSYSTEM; currentLevel <= MasterVariables.MAXSYSTEM; currentLevel++) {
				for (currentDeme = 0; currentDeme < currentLevel; currentDeme++) {
					displayStatus();
					setupStrategies();
					calculateFitness();
					saveBestStrategy();
					evolve();
				}
			}
			//printing should be by deme to compare to previous 
			printStatistics();
		}
	}

	private static void printStatistics() {
		LevelStrategy[] bestLevelStrategies = new LevelStrategy[numOfDemes];
		char[] init_strategy = null, balance_strategy=null, bandwagon_strategy=null;
		int counter = 0;
		for (int i = 0; i < demes.length; i++) {
			for (int j = 0; j < demes[i].length; j++) {
				init_strategy = bestStrategies[i][j].getStrategy(0, i + 2).clone();
				if (i>0){
					balance_strategy = bestStrategies[i][j].getStrategy(1, i + 2).clone();
					bandwagon_strategy = bestStrategies[i][j].getStrategy(2, i + 2).clone();
					bestLevelStrategies[counter++] = new LevelStrategy(i + 2, init_strategy, balance_strategy, bandwagon_strategy);
				} else {
					bestLevelStrategies[counter++] = new LevelStrategy(i + 2, init_strategy);
				}
			}
		}
		Callable<Void> statisticsPrinter = new StatisticsPrinter(bestLevelStrategies,
				MasterVariables.currentSimulation, MasterVariables.currentGen);
		Future<Void> taskPrinter = executor.submit(statisticsPrinter);
		try {
			taskPrinter.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private static void evolve() {
		int segments = threads * 5;
		if (MasterVariables.TOTAL_POPSIZE_PER_DEME < segments)
			segments = MasterVariables.TOTAL_POPSIZE_PER_DEME;
		Future<LevelStrategy[]>[] taskEvolver = new Future[segments];
		int segmentSize = MasterVariables.TOTAL_POPSIZE_PER_DEME / segments, remainder = MasterVariables.TOTAL_POPSIZE_PER_DEME;
		for (int i = 0; i < segments - 1; i++) {
			Callable<LevelStrategy[]> evolver = new Evolver(currentLevel,
					demes[currentLevel - 2][currentDeme].strategies, segmentSize);
			taskEvolver[i] = executor.submit(evolver);
			remainder = remainder - segmentSize;
		}
		Callable<LevelStrategy[]> evolver = new Evolver(currentLevel, demes[currentLevel - 2][currentDeme].strategies,
				remainder);
		taskEvolver[segments - 1] = executor.submit(evolver);

		LevelStrategy[] tempLevelStrategies;
		int counter = 0;
		for (int i = 0; i < taskEvolver.length; i++)
			try {
				tempLevelStrategies = taskEvolver[i].get();
				System.arraycopy(tempLevelStrategies, 0, demes[currentLevel - 2][currentDeme].strategies, counter,
						tempLevelStrategies.length);
				counter = tempLevelStrategies.length;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private static void saveBestStrategy() {
		int currentBestIndex = 0;
		int currentBestFit = testedStrategies[0].fitness;
		//System.out.println(currentBestFit);
		for (int i = 0; i < testedStrategies.length; i++) {
			if (testedStrategies[i].fitness > currentBestFit + (currentBestFit * MasterVariables.ENHANCMENT_MARGIN)) {
				currentBestFit = testedStrategies[i].fitness;
				currentBestIndex = i;
			}
		}
		//System.out.println(currentBestFit);
		bestStrategies[currentLevel - 2][currentDeme] = testedStrategies[currentBestIndex];
	}

	private static void calculateFitness() {
		// initialize fitness test case variables
		Strategy[] otherDemesBestStrategies = new Strategy[currentLevel - 1];
		int counter = 0;
		for (int i = 0; i < currentLevel - 1; i++) {
			if (i == currentDeme)
				counter++;
			otherDemesBestStrategies[i] = bestStrategies[currentLevel - 2][counter];
			counter++;
		}
		MasterVariables.setupFitnessWorlds(otherDemesBestStrategies, currentLevel,
				MasterVariables.TESTCASES[currentLevel - 2]);

		//call and execute test cases
		int segments = threads * 5;
		if (MasterVariables.TOTAL_POPSIZE_PER_DEME < segments)
			segments = MasterVariables.TOTAL_POPSIZE_PER_DEME;
		Future<Void>[] taskFitnessCalculator = new Future[segments];
		Strategy[][] segmentedTestedStrategies = new Strategy[segments][];
		int segmentSize = MasterVariables.TOTAL_POPSIZE_PER_DEME / segments, from = 0, to = segmentSize - 1;
		for (int i = 0; i < segments; i++) {
			segmentedTestedStrategies[i] = Arrays.copyOfRange(testedStrategies, from, to);
			from = to + 1;
			to = to + segmentSize;
			if (i + 2 == segments)
				to = MasterVariables.TOTAL_POPSIZE_PER_DEME - 1;
		}
		for (int i = 0; i < segments; i++) {
			Callable<Void> fitnessCalculator = new FitnessCalculator(segmentedTestedStrategies[i]);
			taskFitnessCalculator[i] = executor.submit(fitnessCalculator);
		}
		for (int i = 0; i < taskFitnessCalculator.length; i++){
			try {
				taskFitnessCalculator[i].get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		//reset fitness cases
		MasterVariables.resetFitnessWorlds();
	}

	private static void setupStrategies() {
		testedStrategies = new Strategy[MasterVariables.TOTAL_POPSIZE_PER_DEME];
		testedStrategies[0] = bestStrategies[currentLevel - 2][currentDeme];
		testedStrategies[0].fitness = 0;
		for (int i = 1; i < MasterVariables.TOTAL_POPSIZE_PER_DEME; i++) {
			testedStrategies[i] = new Strategy(currentLevel);
			testedStrategies[i].setStrategy(0, currentLevel,
					demes[currentLevel - 2][currentDeme].strategies[i].init_strategy);
			if (currentLevel > 2) {
				testedStrategies[i].setStrategy(1, currentLevel,
						demes[currentLevel - 2][currentDeme].strategies[i].balance_strategy);
				testedStrategies[i].setStrategy(2, currentLevel,
						demes[currentLevel - 2][currentDeme].strategies[i].bandwagon_strategy);

				Strategy lowerBestStrategy = bestStrategies[currentLevel - 2 - 1][rd.nextInt(currentLevel - 1)];
				testedStrategies[i].setStrategy(0, 2, lowerBestStrategy.getStrategy(0, 2));
				for (int k = 3; k < currentLevel; k++) {
					testedStrategies[i].setStrategy(0, k, lowerBestStrategy.getStrategy(0, k));
					testedStrategies[i].setStrategy(1, k, lowerBestStrategy.getStrategy(1, k));
					testedStrategies[i].setStrategy(2, k, lowerBestStrategy.getStrategy(2, k));
				}
			}

			if (MasterVariables.currentGen < 5) {
				boolean complete = testedStrategies[i].checkCompleteness();
				if (!complete) {
					System.out.println("setUpStratgies method error!!!");
					System.exit(0);
				}
			}
		}
	}

	private static void displayStatus() {
		if (currentLevel == MasterVariables.MINSYSTEM && currentDeme == 0) {
			if (MasterVariables.currentGen == 1) {
				System.out.println("\n\n*****************************************");
				System.out.println("SIMULATION " + MasterVariables.currentSimulation + "\n");
			}
			System.out.println("Sim " + MasterVariables.currentSimulation + " Gen " + MasterVariables.currentGen);
			System.out.print("Working on level-deme:");
		}

		System.out.print(" " + currentLevel + "-" + (currentDeme+1));

		if (currentLevel == MasterVariables.MAXSYSTEM && currentDeme == MasterVariables.MAXSYSTEM -1) {
			System.out.println();
		}
	}

	private static void initializeSimulation() {
		numOfDemes = 0;
		for (int i = MasterVariables.MINSYSTEM; i <= MasterVariables.MAXSYSTEM; i++) {
			for (int j = 0; j < i; j++)
				numOfDemes++;
		}
		demes = new Deme[MasterVariables.uniqueWorldSizes][];
		for (int i = 0; i < demes.length; i++)
			demes[i] = new Deme[i + MasterVariables.MINSYSTEM];
	
		// create future holders of the demes
		Future<Deme>[] taskDemeCreation = new Future[numOfDemes];
		int counter = 0;
		for (int i = 0; i < demes.length; i++) {
			for (int j = 0; j < demes[i].length; j++) {
				Callable<Deme> demeCreator = new DemeCreator(i + MasterVariables.MINSYSTEM,
						MasterVariables.TOTAL_POPSIZE_PER_DEME);
				taskDemeCreation[counter++] = executor.submit(demeCreator);
			}
		}
	
		counter = 0;
		for (int i = 0; i < demes.length; i++) {
			for (int j = 0; j < demes[i].length; j++) {
				try {
					demes[i][j] = taskDemeCreation[counter++].get();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
		// populate best strategies with random strategies
		bestStrategies = new Strategy[MasterVariables.uniqueWorldSizes][];
		int maxWorldSize, rand1, rand2; 
		for (int i = 0; i < bestStrategies.length; i++) {
			maxWorldSize = i + MasterVariables.MINSYSTEM;
			bestStrategies[i] = new Strategy[maxWorldSize];
			for (int j = 0; j < maxWorldSize; j++) {
				bestStrategies[i][j] = new Strategy(maxWorldSize);
				for (int k = 2; k <= maxWorldSize; k++) {
					rand1 = rd.nextInt(k);
					rand2 = rd.nextInt(demes[k - MasterVariables.MINSYSTEM][rand1].strategies.length);
					char[] randInitStrategy = demes[k - MasterVariables.MINSYSTEM][rand1].strategies[rand2].init_strategy;
					bestStrategies[i][j].setStrategy(0, k, randInitStrategy);
					if (k > 2) {
						rand1 = rd.nextInt(k);
						rand2 = rd.nextInt(demes[k - MasterVariables.MINSYSTEM][rand1].strategies.length);
						char[] randBalanceStrategy = demes[k - MasterVariables.MINSYSTEM][rand1].strategies[rand2].balance_strategy;
						bestStrategies[i][j].setStrategy(1, k, randBalanceStrategy);
						rand1 = rd.nextInt(k);
						rand2 = rd.nextInt(demes[k - MasterVariables.MINSYSTEM][rand1].strategies.length);
						char[] randBandwagonStrategy = demes[k - MasterVariables.MINSYSTEM][rand1].strategies[rand2].bandwagon_strategy;
						bestStrategies[i][j].setStrategy(2, k, randBandwagonStrategy);
					}
				}
				boolean complete = bestStrategies[i][j].checkCompleteness();
				if (!complete) {
					System.out.println("Incomplete strategy!!");
					System.exit(0);
				}
			}
		}
	}
}
