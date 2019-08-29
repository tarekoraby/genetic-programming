package v5;


import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@SuppressWarnings("unchecked")
public class Simulation {

	static ExecutorService executor;
	static int threads, currentLevel, currentDeme, numOfDemes;
	static Deme[][] demes;
	static Strategy[][] bestStrategies;
	static Strategy[] testedStrategies;
	static Random rd = new Random();

	public Simulation() {
		executor = MasterVariables.executor;
		threads = MasterVariables.Threads;
		initializeSimulation();
	}

	public void start() {
		for (MasterVariables.currentGen = 1; MasterVariables.currentGen <= MasterVariables.MASTER_GENERATIONS; MasterVariables.currentGen++) {
			for (currentLevel = MasterVariables.MINSYSTEM; currentLevel <= MasterVariables.MAXSYSTEM; currentLevel++) {
				initializeFitCases();
				for (currentDeme = 0; currentDeme < currentLevel; currentDeme++) {
					displayStatus();
					setupStrategies();
					//save the best strategy of current deme
					testedStrategies[0] = bestStrategies[currentLevel - 2][currentDeme].deepCopy();
					demes[currentLevel - 2][currentDeme].strategies[0] = bestStrategies[currentLevel - 2][currentDeme].getLevelStrategy(currentLevel).deepCopy();
					calculateFitness();
					saveBestStrategy();
					evolve();
					printStatistics();
				}
				// reset fitness cases
				MasterVariables.resetFitnessWorlds();
			}
		}
	}

	private void initializeFitCases() {
		// initialize fitness test case variables
				Strategy[] otherDemesBestStrategies = new Strategy[currentLevel - 1];
				int counter = 0;
				for (int i = 0; i < currentLevel - 1; i++) {
					if (i == currentDeme)
						counter++;
					otherDemesBestStrategies[i] = bestStrategies[currentLevel - 2][counter];
					counter++;
				}
				MasterVariables.setupFitnessWorlds(otherDemesBestStrategies, currentLevel);
		
	}

	private static void printStatistics() {
		LevelStrategy currentBestLevelStrategy;
		char[] init_strategy = bestStrategies[currentLevel - 2][currentDeme].getInitStrategy(currentLevel).clone();
		if (currentLevel > 2) {
			char[] balance_strategy = bestStrategies[currentLevel - 2][currentDeme].getBalanceStrategy(currentLevel)
					.clone();
			char[] bandwagon_strategy = bestStrategies[currentLevel - 2][currentDeme]
					.getBandwagonStrategy(currentLevel).clone();
			currentBestLevelStrategy = new LevelStrategy(currentLevel, init_strategy, balance_strategy,
					bandwagon_strategy);
		} else {
			currentBestLevelStrategy = new LevelStrategy(currentLevel, init_strategy);
		}

		while (StatisticsPrinter.printInProgress)
			try {
				TimeUnit.NANOSECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		Callable<Void> statisticsPrinter = new StatisticsPrinter(currentBestLevelStrategy,
				MasterVariables.currentSimulation, MasterVariables.currentGen, currentLevel, currentDeme);
		executor.submit(statisticsPrinter);
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
		LevelStrategy[] evolvedStrategies = new LevelStrategy[MasterVariables.TOTAL_POPSIZE_PER_DEME];
		int counter = 0;
		for (int i = 0; i < taskEvolver.length; i++)
			try {
				tempLevelStrategies = taskEvolver[i].get();
				for (int k = 0; k < tempLevelStrategies.length; k++) {
					evolvedStrategies[counter + k] = tempLevelStrategies[k].deepCopy();
				}
				//System.arraycopy(tempLevelStrategies, 0, evolvedStrategies, counter, tempLevelStrategies.length);
				counter += tempLevelStrategies.length;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		tempLevelStrategies = demes[currentLevel - 2][currentDeme].strategies;
		demes[currentLevel - 2][currentDeme].strategies = evolvedStrategies;
		setupStrategies();
		calculateFitness();
		int nIndex;
		Evolver evoClass= new Evolver();
		for (int i = 0; i < MasterVariables.TOTAL_POPSIZE_PER_DEME; i++){
			nIndex= evoClass.negativeTournament(tempLevelStrategies, MasterVariables.TSIZE);
			if (evolvedStrategies[i].fitness > tempLevelStrategies[nIndex].fitness){
				//System.out.println(evolvedStrategies[i].fitness + " " + tempLevelStrategies[nIndex].fitness  + " " + nIndex);
				tempLevelStrategies[nIndex]= evolvedStrategies[i].deepCopy();
			}
		}

		demes[currentLevel - 2][currentDeme].strategies = tempLevelStrategies;

		double total = 0, n = 0;
		double[] fitArray = new double[demes[currentLevel - 2][currentDeme].strategies.length];
		for (int i = 0; i < demes[currentLevel - 2][currentDeme].strategies.length; i++) {
			total += demes[currentLevel - 2][currentDeme].strategies[i].fitness;
			n++;
			fitArray[i] = demes[currentLevel - 2][currentDeme].strategies[i].fitness;
		}
		Arrays.sort(fitArray);
		System.out.println("\nAvg fit " + total / n + " Q1 fit " + fitArray[fitArray.length / 4] + " Q3 fit "
				+ fitArray[fitArray.length / 4 * 3] + " Best fit " + fitArray[fitArray.length - 1] + " worst fit "
				+ fitArray[0]);
	}

	private static void saveBestStrategy() {
		int currentBestIndex = 0;
		int currentBestFit = testedStrategies[0].fitness;
		for (int i = 0; i < testedStrategies.length; i++) {
			if (testedStrategies[i].fitness > currentBestFit + (currentBestFit * MasterVariables.ENHANCMENT_MARGIN)) {
				currentBestFit = testedStrategies[i].fitness;
				currentBestIndex = i;
			}
		}
		System.out.println(" " + currentBestFit + " " + currentBestIndex);

		bestStrategies[currentLevel - 2][currentDeme] = testedStrategies[currentBestIndex].deepCopy();
	}

	private static void calculateFitness() {
		

		// call and execute test cases
		int segments = threads * 5;
		if (MasterVariables.TOTAL_POPSIZE_PER_DEME < segments)
			segments = MasterVariables.TOTAL_POPSIZE_PER_DEME;
		Future<Void>[] taskFitnessCalculator = new Future[segments];
		Strategy[][] segmentedTestedStrategies = new Strategy[segments][];
		int segmentSize = MasterVariables.TOTAL_POPSIZE_PER_DEME / segments, from = 0, to = segmentSize  ;
		for (int i = 0; i < segments; i++) {
			if (i == segments - 1)
				to = MasterVariables.TOTAL_POPSIZE_PER_DEME;
			segmentedTestedStrategies[i] = Arrays.copyOfRange(testedStrategies, from, to);
			from = from + segmentSize;
			to = from + segmentSize;
			
		}
		
		for (int i = 0; i < segments; i++) {
			Callable<Void> fitnessCalculator = new FitnessCalculator(segmentedTestedStrategies[i]);
			taskFitnessCalculator[i] = executor.submit(fitnessCalculator);
		}
		for (int i = 0; i < taskFitnessCalculator.length; i++) {
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
		
		for (int i = 0; i < MasterVariables.TOTAL_POPSIZE_PER_DEME; i++) 
			if (testedStrategies[i].tested ==false){
				System.out.println("Error!!! Simulation class - Calculate Fitness method");
				System.exit(0);
			}else {
				testedStrategies[i].tested = false;
			}
		
		for (int i = 0; i < MasterVariables.TOTAL_POPSIZE_PER_DEME; i++) 
			demes[currentLevel - 2][currentDeme].strategies[i].fitness = testedStrategies[i].fitness;

		
	}

	private static void setupStrategies() {
		testedStrategies = new Strategy[MasterVariables.TOTAL_POPSIZE_PER_DEME];
		
		
		
		// include the best strategies of the other demes
		/*int index = -1;
		for (int i = 1; i < currentLevel; i++) {
			if (currentDeme < i) {
				index = i;
			} else {
				index = i - 1;
			}
			testedStrategies[i] = bestStrategies[currentLevel - 2][index].deepCopy();
		}*/
		
		for (int i = 0; i < MasterVariables.TOTAL_POPSIZE_PER_DEME; i++) {
			testedStrategies[i] = new Strategy(currentLevel);
			testedStrategies[i].setStrategy(0, currentLevel,
					demes[currentLevel - 2][currentDeme].strategies[i].init_strategy);
			if (currentLevel > 2) {
				testedStrategies[i].setStrategy(1, currentLevel,
						demes[currentLevel - 2][currentDeme].strategies[i].balance_strategy);
				testedStrategies[i].setStrategy(2, currentLevel,
						demes[currentLevel - 2][currentDeme].strategies[i].bandwagon_strategy);

				Strategy lowerBestStrategy = bestStrategies[currentLevel - 2 - 1][rd.nextInt(currentLevel - 1)];
				testedStrategies[i].setStrategy(0, 2, lowerBestStrategy.getInitStrategy(2));
				for (int k = 3; k < currentLevel; k++) {
					testedStrategies[i].setStrategy(0, k, lowerBestStrategy.getInitStrategy(k));
					testedStrategies[i].setStrategy(1, k, lowerBestStrategy.getBalanceStrategy(k));
					testedStrategies[i].setStrategy(2, k, lowerBestStrategy.getBandwagonStrategy(k));
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
			System.out.println("\nSim " + MasterVariables.currentSimulation + " Gen " + MasterVariables.currentGen);
			System.out.print("Working on level-deme:");
		}

		System.out.print(" " + currentLevel + "-" + (currentDeme + 1));

		if (currentLevel == MasterVariables.MAXSYSTEM && currentDeme == MasterVariables.MAXSYSTEM - 1) {
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

		// initialize and populate profiles
		MasterVariables.attackProfiles = new boolean[MasterVariables.uniqueWorldSizes][][];
		for (int i = 0; i < MasterVariables.attackProfiles.length; i++) {
			MasterVariables.attackProfiles[i] = new boolean[i + MasterVariables.MINSYSTEM][MasterVariables.PROFILES_PER_CAT];
			for (int k = 0; k < MasterVariables.attackProfiles[i].length; k++) {
				for (int l = 0; l < MasterVariables.PROFILES_PER_CAT; l++) {
					MasterVariables.attackProfiles[i][k][l] = MasterVariables.profilingWorlds_Attacks[i][l]
							.willItAttack(bestStrategies[i][k].getInitStrategy(i + 2), 0);
				}
			}
		}

		MasterVariables.balanceProfiles = new boolean[MasterVariables.uniqueWorldSizes][][];
		MasterVariables.bandwagonProfiles = new boolean[MasterVariables.uniqueWorldSizes][][];
		MasterVariables.buckpassingProfiles = new boolean[MasterVariables.uniqueWorldSizes][][];
		// the following loop start from 1 as the joining profiles of bipolarity
		// shouldn't be initialized
		for (int i = 1; i < MasterVariables.balanceProfiles.length; i++) {
			MasterVariables.balanceProfiles[i] = new boolean[i + MasterVariables.MINSYSTEM][MasterVariables.PROFILES_PER_CAT];
			MasterVariables.bandwagonProfiles[i] = new boolean[i + MasterVariables.MINSYSTEM][MasterVariables.PROFILES_PER_CAT];
			MasterVariables.buckpassingProfiles[i] = new boolean[i + MasterVariables.MINSYSTEM][MasterVariables.PROFILES_PER_CAT];
			for (int k = 0; k < MasterVariables.balanceProfiles[i].length; k++) {
				for (int l = 0; l < MasterVariables.PROFILES_PER_CAT; l++) {
					MasterVariables.balanceProfiles[i][k][l] = MasterVariables.profilingWorlds_Joins[i - 1][l]
							.willItAttack(bestStrategies[i][k].getInitStrategy(i + 2), 1);
					MasterVariables.bandwagonProfiles[i][k][l] = MasterVariables.profilingWorlds_Joins[i - 1][l]
							.willItAttack(bestStrategies[i][k].getInitStrategy(i + 2), 2);
					if (!MasterVariables.balanceProfiles[i][k][l] && !MasterVariables.bandwagonProfiles[i][k][l])
						MasterVariables.buckpassingProfiles[i][k][l] = true;
				}
			}
		}

		MasterVariables.totalProfileAttacks = new double[MasterVariables.profilingWorlds_Attacks.length][MasterVariables.PROFILES_PER_CAT];
		MasterVariables.totalProfileBalances = new double[MasterVariables.profilingWorlds_Joins.length][MasterVariables.PROFILES_PER_CAT];
		MasterVariables.totalProfileBandwagons = new double[MasterVariables.profilingWorlds_Joins.length][MasterVariables.PROFILES_PER_CAT];
		MasterVariables.totalProfileBuckpasses = new double[MasterVariables.profilingWorlds_Joins.length][MasterVariables.PROFILES_PER_CAT];
		
		//initialize output strings
		MasterVariables.stateProfilesString="";
	}
}
