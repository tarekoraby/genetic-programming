package v1;

import java.awt.Frame;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.swing.text.StyledEditorKit.ForegroundAction;

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

	public static void start() {
		for (MasterVariables.currentGen = 1; MasterVariables.currentGen <= MasterVariables.MASTER_GENERATIONS; MasterVariables.currentGen++) {
			for (currentLevel = MasterVariables.MINSYSTEM; currentLevel <= MasterVariables.MAXSYSTEM; currentLevel++) {
				for (currentDeme = 0; currentDeme < currentLevel; currentDeme++) {
					displayStatus();
					setupStrategies();
					calculateFitness();
					saveBestStrategy();
					evolve();
					printStatistics();
				}
			}
		}
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
		// System.out.println(currentBestFit);
		for (int i = 0; i < testedStrategies.length; i++) {
			if (testedStrategies[i].fitness > currentBestFit + (currentBestFit * MasterVariables.ENHANCMENT_MARGIN)) {
				currentBestFit = testedStrategies[i].fitness;
				currentBestIndex = i;
			}
		}
		// System.out.println(currentBestFit);
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

		// call and execute test cases
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

		// reset fitness cases
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
			System.out.println("Sim " + MasterVariables.currentSimulation + " Gen " + MasterVariables.currentGen);
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
