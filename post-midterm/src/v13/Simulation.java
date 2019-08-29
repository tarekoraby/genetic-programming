package v13;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Simulation {

	static ExecutorService executor;
	static int threads, currentLevel, currentDeme;
	static Strategy[][][] InitDemes, JoinDemes;
	static Strategy[][] bestInitStrategies, bestJoinStrategies;
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
					MasterVariables.testedDeme = currentDeme;
					for (int currentDemeGeneration = 0; currentDemeGeneration < MasterVariables.GENERATIONS_PER_DEME; currentDemeGeneration++) {
						if (currentLevel > 2) {
							MasterVariables.testInit = false;

							calculateFitness(JoinDemes[currentLevel - 3][currentDeme]);
							int bestFitIndex = findBestStrategy(JoinDemes[currentLevel - 3][currentDeme],
									rd.nextInt(JoinDemes[currentLevel - 3][currentDeme].length));
							calculateFitness(bestJoinStrategies[currentLevel - 3][currentDeme]);
							if (JoinDemes[currentLevel - 3][currentDeme][bestFitIndex].getFitness(currentLevel) > bestJoinStrategies[currentLevel - 3][currentDeme]
									.getFitness(currentLevel)
									+ (bestJoinStrategies[currentLevel - 3][currentDeme].getFitness(currentLevel) * MasterVariables.ENHANCMENT_MARGIN)) {
								bestJoinStrategies[currentLevel - 3][currentDeme] = JoinDemes[currentLevel - 3][currentDeme][bestFitIndex]
										.deepCopy();
							}
							Strategy[] evolvedJoinDeme = evolve(JoinDemes[currentLevel - 3][currentDeme]);
							calculateFitness(evolvedJoinDeme);

							JoinDemes[currentLevel - 3][currentDeme] = updateDeme(
									 evolvedJoinDeme);
						}

						MasterVariables.testInit = true;

						calculateFitness(InitDemes[currentLevel - 2][currentDeme]);
						int bestFitIndex = findBestStrategy(InitDemes[currentLevel - 2][currentDeme],
								rd.nextInt(InitDemes[currentLevel - 2][currentDeme].length));
						calculateFitness(bestInitStrategies[currentLevel - 2][currentDeme]);
						if (InitDemes[currentLevel - 2][currentDeme][bestFitIndex].getFitness(currentLevel) > bestInitStrategies[currentLevel - 2][currentDeme]
								.getFitness(currentLevel)
								+ (bestInitStrategies[currentLevel - 2][currentDeme].getFitness(currentLevel) * MasterVariables.ENHANCMENT_MARGIN)) {
							bestInitStrategies[currentLevel - 2][currentDeme] = InitDemes[currentLevel - 2][currentDeme][bestFitIndex]
									.deepCopy();
						}
						Strategy[] evolvedInitDeme = evolve(InitDemes[currentLevel - 2][currentDeme]);

						calculateFitness(evolvedInitDeme);

						InitDemes[currentLevel - 2][currentDeme] = updateDeme(evolvedInitDeme);
					}

					printStatistics();

				}
				// reset fitness cases
				MasterVariables.resetFitnessWorlds();

			}
		}

	}

	private Strategy[] updateDeme(Strategy[] evolvedStrategies) {
		int pIndex, nIndex;
		Evolver evoClass = new Evolver(currentLevel);
		Strategy[] newStrategy= new Strategy[MasterVariables.TOTAL_POPSIZE_PER_DEME];
		for (int i = 0; i < MasterVariables.TOTAL_POPSIZE_PER_DEME; i++) {
			pIndex = evoClass.tournament(evolvedStrategies, MasterVariables.TSIZE);
			//nIndex = evoClass.negativeTournament(originalStrategies, MasterVariables.TSIZE);

			newStrategy[i] = evolvedStrategies[pIndex].deepCopy();
		}
		return newStrategy;
	}

	private int findBestStrategy(Strategy[] testedStrategies, int defaultBestIndex) {
		int currentBestIndex = defaultBestIndex;
		int currentBestFit = testedStrategies[defaultBestIndex].getFitness(currentLevel);

		for (int i = 0; i < testedStrategies.length; i++) {
			if (testedStrategies[i].getFitness(currentLevel) > currentBestFit) {
				currentBestFit = testedStrategies[i].getFitness(currentLevel);
				currentBestIndex = i;
			}
		}

		return currentBestIndex;
	}

	private void initializeFitCases() {
		Strategy[][] Copy_bestInitStrategies = new Strategy[bestInitStrategies.length][];
		Strategy[][] Copy_bestJoinStrategies = new Strategy[bestJoinStrategies.length][];

		for (int levelCounter = 0; levelCounter < Copy_bestInitStrategies.length; levelCounter++) {
			Copy_bestInitStrategies[levelCounter] = new Strategy[bestInitStrategies[levelCounter].length];
			for (int demeCounter = 0; demeCounter < Copy_bestInitStrategies[levelCounter].length; demeCounter++)
				Copy_bestInitStrategies[levelCounter][demeCounter] = bestInitStrategies[levelCounter][demeCounter]
						.deepCopy();
		}

		for (int levelCounter = 0; levelCounter < Copy_bestJoinStrategies.length; levelCounter++) {
			Copy_bestJoinStrategies[levelCounter] = new Strategy[bestJoinStrategies[levelCounter].length];
			for (int demeCounter = 0; demeCounter < Copy_bestJoinStrategies[levelCounter].length; demeCounter++)
				Copy_bestJoinStrategies[levelCounter][demeCounter] = bestJoinStrategies[levelCounter][demeCounter]
						.deepCopy();
		}
		MasterVariables.setupFitnessWorlds(Copy_bestInitStrategies, Copy_bestJoinStrategies, currentLevel);
	}

	private static void printStatistics() {
		if (currentLevel > 2)
			System.out.println("BIF " + bestInitStrategies[currentLevel - 2][currentDeme].getFitness(currentLevel)
					+ " BJF " + bestJoinStrategies[currentLevel - 3][currentDeme].getFitness(currentLevel));
		else {
			System.out.println("BIF " + bestInitStrategies[currentLevel - 2][currentDeme].getFitness(currentLevel));
		}

		while (StatisticsPrinter.printInProgress)
			try {
				TimeUnit.NANOSECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		Strategy copyInitStrategy = bestInitStrategies[currentLevel - 2][currentDeme].deepCopy(), copyJoinStrategy = null;
		int bif = bestInitStrategies[currentLevel - 2][currentDeme].getFitness(currentLevel);
		int bjf = 0;
		if (currentLevel > 2) {
			copyJoinStrategy = bestJoinStrategies[currentLevel - 3][currentDeme].deepCopy();
			bjf = bestJoinStrategies[currentLevel - 3][currentDeme].getFitness(currentLevel);
		}
		Callable<Void> statisticsPrinter = new StatisticsPrinter(copyInitStrategy, copyJoinStrategy, bif, bjf,
				MasterVariables.currentSimulation, MasterVariables.currentGen, currentLevel, currentDeme);

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

	private static Strategy[] evolve(Strategy[] originalStrategies) {
		Strategy[] evolvedStrategies;
		int segments = threads * 5;
		if (MasterVariables.TOTAL_POPSIZE_PER_DEME < segments)
			segments = MasterVariables.TOTAL_POPSIZE_PER_DEME;

		Future<Strategy[]>[] taskEvolver = new Future[segments];
		int segmentSize = MasterVariables.TOTAL_POPSIZE_PER_DEME / segments, remainder = MasterVariables.TOTAL_POPSIZE_PER_DEME;
		for (int i = 0; i < segments - 1; i++) {
			Callable<Strategy[]> evolver = new Evolver(currentLevel, originalStrategies, segmentSize);
			taskEvolver[i] = executor.submit(evolver);
			remainder = remainder - segmentSize;
		}
		Callable<Strategy[]> evolver = new Evolver(currentLevel, originalStrategies, remainder);
		taskEvolver[segments - 1] = executor.submit(evolver);

		Strategy[] tempStrategies;
		evolvedStrategies = new Strategy[MasterVariables.TOTAL_POPSIZE_PER_DEME];
		int counter = 0;
		for (int i = 0; i < taskEvolver.length; i++)
			try {
				tempStrategies = taskEvolver[i].get();
				for (int k = 0; k < tempStrategies.length; k++) {
					evolvedStrategies[counter + k] = tempStrategies[k];
				}
				// System.arraycopy(tempLevelStrategies, 0, evolvedStrategies,
				// counter, tempLevelStrategies.length);
				counter += tempStrategies.length;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return evolvedStrategies;
	}

	private static void calculateFitness(Strategy testedStrategy) {
		Strategy[] testedStrategies = new Strategy[1];
		testedStrategies[0] = testedStrategy;
		calculateFitness(testedStrategies);
	}

	private static void calculateFitness(Strategy[] testedStrategies) {

		for (int i = 0; i < testedStrategies.length; i++) {
			testedStrategies[i].resetFitness(currentLevel);
			testedStrategies[i].tested = false;
		}

		// call and execute test cases
		int segments = threads * 5;
		if (testedStrategies.length < segments)
			segments = testedStrategies.length;

		Future<Void>[] taskFitnessCalculator = new Future[segments];
		Strategy[][] segmentedTestedStrategies = new Strategy[segments][];
		int segmentSize = testedStrategies.length / segments, from = 0, to = segmentSize;
		for (int i = 0; i < segments; i++) {
			if (i == segments - 1)
				to = testedStrategies.length;
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

		for (int i = 0; i < testedStrategies.length; i++)
			if (testedStrategies[i].tested == false) {
				System.out.println("Error!!! Simulation class - Calculate Fitness method");
				System.exit(0);
			} else {
				testedStrategies[i].tested = false;
			}

	}

	private static void displayStatus() {
		if (currentLevel == MasterVariables.MINSYSTEM && currentDeme == 0) {
			if (MasterVariables.currentGen == 1) {
				System.out.println("\n\n*****************************************");
				System.out.println("SIMULATION " + MasterVariables.currentSimulation + "\n");
			}
			System.out.println("\nSim " + MasterVariables.currentSimulation + " Gen " + MasterVariables.currentGen);
			System.out.println("Working on level-deme:");
		}

		System.out.print(" " + currentLevel + "-" + (currentDeme + 1) + " ");

	}

	private static void initializeSimulation() {

		InitDemes = new Strategy[MasterVariables.uniqueWorldSizes][][];
		for (int i = 0; i < InitDemes.length; i++)
			InitDemes[i] = new Strategy[i + 2][MasterVariables.TOTAL_POPSIZE_PER_DEME];

		JoinDemes = new Strategy[MasterVariables.uniqueWorldSizes - 1][][];
		for (int i = 0; i < JoinDemes.length; i++)
			JoinDemes[i] = new Strategy[i + 3][MasterVariables.TOTAL_POPSIZE_PER_DEME];

		StrategyCreator SC = new StrategyCreator();
		char[] tempStrategy;

		for (int levelCounter = 0; levelCounter < InitDemes.length; levelCounter++)
			for (int demeCounter = 0; demeCounter < InitDemes[levelCounter].length; demeCounter++)
				for (int indivCounter = 0; indivCounter < InitDemes[levelCounter][demeCounter].length; indivCounter++) {
					tempStrategy = SC
							.create_random_indiv(MasterVariables.DEPTH, MasterVariables.SIMULATECONSTRUCTIVISM);
					InitDemes[levelCounter][demeCounter][indivCounter] = new Strategy(levelCounter + 2, true,
							tempStrategy);
				}

		for (int levelCounter = 0; levelCounter < JoinDemes.length; levelCounter++)
			for (int demeCounter = 0; demeCounter < JoinDemes[levelCounter].length; demeCounter++)
				for (int indivCounter = 0; indivCounter < JoinDemes[levelCounter][demeCounter].length; indivCounter++) {
					JoinDemes[levelCounter][demeCounter][indivCounter] = new Strategy(levelCounter + 3, false);
					tempStrategy = SC
							.create_random_indiv(MasterVariables.DEPTH, MasterVariables.SIMULATECONSTRUCTIVISM);
					JoinDemes[levelCounter][demeCounter][indivCounter]
							.setJoinStrategy((levelCounter + 3), tempStrategy);
					tempStrategy = SC
							.create_random_indiv(MasterVariables.DEPTH, MasterVariables.SIMULATECONSTRUCTIVISM);
					JoinDemes[levelCounter][demeCounter][indivCounter].setBalanceStrategy((levelCounter + 3),
							tempStrategy);
				}

		// populate best init and join strategies with random strategies
		bestInitStrategies = new Strategy[MasterVariables.uniqueWorldSizes][];
		bestJoinStrategies = new Strategy[MasterVariables.uniqueWorldSizes - 1][];

		for (int levelCounter = 0; levelCounter < bestInitStrategies.length; levelCounter++) {
			bestInitStrategies[levelCounter] = new Strategy[levelCounter + 2];
			for (int demeCounter = 0; demeCounter < bestInitStrategies[levelCounter].length; demeCounter++)
				bestInitStrategies[levelCounter][demeCounter] = InitDemes[levelCounter][demeCounter][rd
						.nextInt(InitDemes[levelCounter][demeCounter].length)].deepCopy();
		}

		for (int levelCounter = 0; levelCounter < bestJoinStrategies.length; levelCounter++) {
			bestJoinStrategies[levelCounter] = new Strategy[levelCounter + 3];
			for (int demeCounter = 0; demeCounter < bestJoinStrategies[levelCounter].length; demeCounter++)
				bestJoinStrategies[levelCounter][demeCounter] = JoinDemes[levelCounter][demeCounter][rd
						.nextInt(JoinDemes[levelCounter][demeCounter].length)].deepCopy();
		}

		// initialize and populate profiles
		MasterVariables.attackProfiles = new boolean[MasterVariables.uniqueWorldSizes][][];
		for (int levelCounter = 0; levelCounter < MasterVariables.attackProfiles.length; levelCounter++) {
			MasterVariables.attackProfiles[levelCounter] = new boolean[levelCounter + 2][MasterVariables.PROFILES_PER_CAT];
			for (int demeCounter = 0; demeCounter < MasterVariables.attackProfiles[levelCounter].length; demeCounter++) {
				for (int l = 0; l < MasterVariables.PROFILES_PER_CAT; l++) {
					MasterVariables.attackProfiles[levelCounter][demeCounter][l] = MasterVariables.profilingWorlds_Attacks[levelCounter][l]
							.willItAttack(bestInitStrategies[levelCounter][demeCounter]
									.getInitStrategy(levelCounter + 2));
				}
			}
		}

		MasterVariables.balanceProfiles = new boolean[MasterVariables.uniqueWorldSizes - 1][][];
		MasterVariables.bandwagonProfiles = new boolean[MasterVariables.uniqueWorldSizes - 1][][];
		MasterVariables.joiningProfiles = new boolean[MasterVariables.uniqueWorldSizes - 1][][];
		for (int levelCounter = 0; levelCounter < MasterVariables.balanceProfiles.length; levelCounter++) {
			MasterVariables.balanceProfiles[levelCounter] = new boolean[levelCounter + 3][MasterVariables.PROFILES_PER_CAT];
			MasterVariables.bandwagonProfiles[levelCounter] = new boolean[levelCounter + 3][MasterVariables.PROFILES_PER_CAT];
			MasterVariables.joiningProfiles[levelCounter] = new boolean[levelCounter + 3][MasterVariables.PROFILES_PER_CAT];
			for (int demeCounter = 0; demeCounter < MasterVariables.balanceProfiles[levelCounter].length; demeCounter++) {
				for (int l = 0; l < MasterVariables.PROFILES_PER_CAT; l++) {
					MasterVariables.joiningProfiles[levelCounter][demeCounter][l] = MasterVariables.profilingWorlds_Joins[levelCounter][l]
							.willItAttack(bestJoinStrategies[levelCounter][demeCounter]
									.getJoinStrategy(levelCounter + 3));
					if (MasterVariables.joiningProfiles[levelCounter][demeCounter][l]) {
						MasterVariables.balanceProfiles[levelCounter][demeCounter][l] = MasterVariables.profilingWorlds_Joins[levelCounter][l]
								.willItAttack(bestJoinStrategies[levelCounter][demeCounter]
										.getBalanceStrategy(levelCounter + 3));
						MasterVariables.bandwagonProfiles[levelCounter][demeCounter][l] = !MasterVariables.balanceProfiles[levelCounter][demeCounter][l];
					} else {
						MasterVariables.balanceProfiles[levelCounter][demeCounter][l] = false;
						MasterVariables.bandwagonProfiles[levelCounter][demeCounter][l] = false;
					}
				}
			}
		}
	}
}
