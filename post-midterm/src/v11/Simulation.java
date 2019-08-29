package v11;

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
	static Random rd = new Random();
	static int[] lastGenChange;
	static int pbf, pbi;
	static Strategy pbs;

	public Simulation() {
		executor = MasterVariables.executor;
		threads = MasterVariables.Threads;
		initializeSimulation();
	}

	public void start() {
		
		// xxx
		// xxx
		// xxx
		// xxx
		// xxx
		// xxx
		// xxx
		// xxx
		// xxx
/*
		MasterVariables.GENERATIONS_PER_DEME = 1 + rd.nextInt(20);
		MasterVariables.RANDOMPLAYER_PROB = (double) rd.nextInt(10) / 10;
		MasterVariables.INTERACTIONROUNDS = 3 + rd.nextInt(28);
		*/
		/*
		 * MasterVariables.RANDOMNUMBERS = 7 + rd.nextInt(94);
		 * MasterVariables.randNum = new int[MasterVariables.RANDOMNUMBERS];
		 * MasterVariables.randNum[0] = 0; MasterVariables.randNum[1] = 1;
		 * MasterVariables.randNum[2] = -1; MasterVariables.randNum[3] = 500;
		 * for (int i = 4; i < MasterVariables.RANDOMNUMBERS; i++)
		 * MasterVariables.randNum[i] = MasterVariables.MINRANDOM +
		 * rd.nextInt(MasterVariables.MAXRANDOM + 1);
		 */

		// xxx
		// xxx
		// xxx
		// xxx
		// xxx
		// xxx
		
		for (MasterVariables.currentGen = 1; MasterVariables.currentGen <= MasterVariables.MASTER_GENERATIONS; MasterVariables.currentGen++) {
			for (currentLevel = MasterVariables.MINSYSTEM; currentLevel <= MasterVariables.MAXSYSTEM; currentLevel++) {
				
				for (currentDeme = 0; currentDeme < currentLevel; currentDeme++) {
					initializeFitCases();
					displayStatus();
					/*if (currentLevel == 2 && MasterVariables.currentGen > 200) {
						printStatistics();
						continue;
					}*/
					
					pbf = 0;
					pbi=-1;
					pbs=null;
					for (int i = 0; i < MasterVariables.GENERATIONS_PER_DEME; i++) {
						
						Strategy[] testedStrategies = setupStrategies(demes[currentLevel - 2][currentDeme].strategies);
						//XXX
						//testedStrategies = addBestStrategy(currentDeme, 0, testedStrategies);
						// demes[currentLevel - 2][currentDeme].strategies[0] =
						// testedStrategies[0].getLevelStrategy(
						// currentLevel).deepCopy();
						calculateFitness(testedStrategies);
						int bestFitIndex = findBestStrategy(testedStrategies, 0);
						calculateFitness(bestStrategies[currentLevel - 2][currentDeme]);
						if (testedStrategies[bestFitIndex].fitness > bestStrategies[currentLevel - 2][currentDeme].fitness
								+ (bestStrategies[currentLevel - 2][currentDeme].fitness * MasterVariables.ENHANCMENT_MARGIN)) {
							bestStrategies[currentLevel - 2][currentDeme] = testedStrategies[bestFitIndex].deepCopy();
						}
						bestStrategies[currentLevel - 2][currentDeme].bestStrategyFitness = bestStrategies[currentLevel - 2][currentDeme].fitness;

						if (MasterVariables.shareBestAcrossDemes) {
							// xxx
							for (int x = 0; x < currentLevel; x++) {
								calculateFitness(bestStrategies[currentLevel - 2][x]);
								bestStrategies[currentLevel - 2][x].bestStrategyFitness = bestStrategies[currentLevel - 2][x].fitness;
							}

							int bestDeme = 0;
							for (int x = 1; x < currentLevel; x++) {
								if (bestStrategies[currentLevel - 2][x].bestStrategyFitness > bestStrategies[currentLevel - 2][bestDeme].bestStrategyFitness)
									bestDeme = x;
							}
							for (int x = 0; x < currentLevel; x++)
								bestStrategies[currentLevel - 2][x] = bestStrategies[currentLevel - 2][bestDeme]
										.deepCopy();
						}
						

						for (int k = 0; k < MasterVariables.TOTAL_POPSIZE_PER_DEME; k++)
							demes[currentLevel - 2][currentDeme].strategies[k].fitness = testedStrategies[k].fitness;
						
						LevelStrategy[] evolvedLevelStrategies = evolve(demes[currentLevel - 2][currentDeme].strategies);
						testedStrategies = setupStrategies(evolvedLevelStrategies);
						calculateFitness(testedStrategies);
						for (int k = 0; k < MasterVariables.TOTAL_POPSIZE_PER_DEME; k++)
							evolvedLevelStrategies[k].fitness = testedStrategies[k].fitness;
						demes[currentLevel - 2][currentDeme].strategies = updateDeme(demes[currentLevel - 2][currentDeme].strategies, evolvedLevelStrategies);
						
					}
					printStatistics();
					// reset fitness cases
					MasterVariables.resetFitnessWorlds();
					
				}
				
			}
		}

		

		
		
		
	}

	private LevelStrategy[] updateDeme(LevelStrategy[] originalLevelStrategies, LevelStrategy[] evolvedLevelStrategies) {
		int pIndex, nIndex;
		Evolver evoClass = new Evolver();
		for (int i = 0; i < MasterVariables.TOTAL_POPSIZE_PER_DEME; i++) {
			pIndex = evoClass.tournament(evolvedLevelStrategies, MasterVariables.TSIZE);
			nIndex = evoClass.negativeTournament(originalLevelStrategies, MasterVariables.TSIZE);
			//XXX
			//if (originalLevelStrategies[nIndex].fitness < evolvedLevelStrategies[pIndex].fitness)
				originalLevelStrategies[nIndex] = evolvedLevelStrategies[pIndex].deepCopy();
		}
		return originalLevelStrategies;
	}

	private int findBestStrategy(Strategy[] testedStrategies, int defaultBestIndex) {
		int currentBestIndex = defaultBestIndex;
		int currentBestFit = testedStrategies[defaultBestIndex].fitness;
		//System.out.println("Fitness 0 is " + testedStrategies[defaultBestIndex].fitness);
		for (int i = 0; i < testedStrategies.length; i++) {
			if (testedStrategies[i].fitness > currentBestFit) {
				currentBestFit = testedStrategies[i].fitness;
				currentBestIndex = i;
			}
		}
		//System.out.println(" " + currentBestFit + " " + currentBestIndex);
		

		if (false && pbf > currentBestFit) {
			System.out.println("ERORRR !!! Simulation class .. previous best fitness is larger than current best fitness");
			System.exit(0);
		}
		pbf = testedStrategies[currentBestIndex].fitness;
		pbi = currentBestIndex;
		pbs = testedStrategies[currentBestIndex].deepCopy();

		return currentBestIndex;
	}

	private Strategy[] addBestStrategy(int bestDeme, int position, Strategy[] testedStrategies) {
		// save the best strategy of specified deme
		testedStrategies[position] = bestStrategies[currentLevel - 2][bestDeme].deepCopy();
		return testedStrategies;
	}

	private void initializeFitCases() {
		// initialize fitness test case variables
		Strategy[] otherDemesBestStrategies = new Strategy[currentLevel - 1];
		int counter = 0;
		for (int i = 0; i < currentLevel - 1; i++) {
			if (i == currentDeme)
				counter++;
			otherDemesBestStrategies[i] = bestStrategies[currentLevel - 2][counter].deepCopy();
			counter++;
		}
		MasterVariables.setupFitnessWorlds(otherDemesBestStrategies, currentLevel);
	}

	private static void printStatistics() {
		System.out.println("BF " + bestStrategies[currentLevel - 2][currentDeme].bestStrategyFitness);
		LevelStrategy currentBestLevelStrategy;
		char[] init_strategy = bestStrategies[currentLevel - 2][currentDeme].getInitStrategy(currentLevel).clone();
		if (currentLevel > 2) {
			char[] balance_strategy = bestStrategies[currentLevel - 2][currentDeme].getBalanceStrategy(currentLevel)
					.clone();
			char[] join_strategy = bestStrategies[currentLevel - 2][currentDeme].getJoinStrategy(currentLevel).clone();
			currentBestLevelStrategy = new LevelStrategy(currentLevel, init_strategy, balance_strategy, join_strategy);
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
				bestStrategies[currentLevel - 2][currentDeme].bestStrategyFitness, MasterVariables.currentSimulation,
				MasterVariables.currentGen, currentLevel, currentDeme);
		
		
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

	private static LevelStrategy[] evolve(LevelStrategy[] originalStrategies) {
		LevelStrategy[] evolvedStrategies;
		int segments = threads * 5;
		if (MasterVariables.TOTAL_POPSIZE_PER_DEME < segments)
			segments = MasterVariables.TOTAL_POPSIZE_PER_DEME;
		
		Future<LevelStrategy[]>[] taskEvolver = new Future[segments];
		int segmentSize = MasterVariables.TOTAL_POPSIZE_PER_DEME / segments, remainder = MasterVariables.TOTAL_POPSIZE_PER_DEME;
		for (int i = 0; i < segments - 1; i++) {
			Callable<LevelStrategy[]> evolver = new Evolver(currentLevel, originalStrategies, segmentSize);
			taskEvolver[i] = executor.submit(evolver);
			remainder = remainder - segmentSize;
		}
		Callable<LevelStrategy[]> evolver = new Evolver(currentLevel, originalStrategies, remainder);
		taskEvolver[segments - 1] = executor.submit(evolver);

		LevelStrategy[] tempLevelStrategies;
		evolvedStrategies = new LevelStrategy[MasterVariables.TOTAL_POPSIZE_PER_DEME];
		int counter = 0;
		for (int i = 0; i < taskEvolver.length; i++)
			try {
				tempLevelStrategies = taskEvolver[i].get();
				for (int k = 0; k < tempLevelStrategies.length; k++) {
					evolvedStrategies[counter + k] = tempLevelStrategies[k];
				}
				// System.arraycopy(tempLevelStrategies, 0, evolvedStrategies,
				// counter, tempLevelStrategies.length);
				counter += tempLevelStrategies.length;
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
		Strategy[] testedStrategies= new Strategy[1];
		testedStrategies[0] = testedStrategy;
		calculateFitness(testedStrategies);
	}
	
	private static void calculateFitness(Strategy[] testedStrategies) {

		for (int i = 0; i < testedStrategies.length; i++) {
			testedStrategies[i].fitness = 0;
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

	private static Strategy[] setupStrategies(LevelStrategy[] levelStrategies) {
		Strategy[] testedStrategies = new Strategy[MasterVariables.TOTAL_POPSIZE_PER_DEME];

		for (int i = 0; i < MasterVariables.TOTAL_POPSIZE_PER_DEME; i++) {
			testedStrategies[i] = new Strategy(currentLevel);
			testedStrategies[i].setInitStrategy(currentLevel,
					levelStrategies[i].init_strategy.clone());
			if (currentLevel > 2) {
				testedStrategies[i].setBalanceStrategy(currentLevel,
						levelStrategies[i].balance_strategy.clone());
				testedStrategies[i].setJoinStrategy(currentLevel,
						levelStrategies[i].join_strategy.clone());

				Strategy lowerBestStrategy = bestStrategies[currentLevel - 2 - 1][rd.nextInt(currentLevel - 1)]
						.deepCopy();
				testedStrategies[i].setInitStrategy(2, lowerBestStrategy.getInitStrategy(2).clone());
				for (int k = 3; k < currentLevel; k++) {
					testedStrategies[i].setInitStrategy(k, lowerBestStrategy.getInitStrategy(k).clone());
					testedStrategies[i].setBalanceStrategy(k, lowerBestStrategy.getBalanceStrategy(k).clone());
					testedStrategies[i].setJoinStrategy(k, lowerBestStrategy.getJoinStrategy(k).clone());
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

		return testedStrategies;
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
					char[] randInitStrategy = demes[k - MasterVariables.MINSYSTEM][rand1].strategies[rand2].init_strategy
							.clone();
					bestStrategies[i][j].setInitStrategy(k, randInitStrategy);
					if (k > 2) {
						rand1 = rd.nextInt(k);
						rand2 = rd.nextInt(demes[k - MasterVariables.MINSYSTEM][rand1].strategies.length);
						char[] randBalanceStrategy = demes[k - MasterVariables.MINSYSTEM][rand1].strategies[rand2].balance_strategy
								.clone();
						bestStrategies[i][j].setBalanceStrategy(k, randBalanceStrategy);
						rand1 = rd.nextInt(k);
						rand2 = rd.nextInt(demes[k - MasterVariables.MINSYSTEM][rand1].strategies.length);
						char[] randJoinStrategy = demes[k - MasterVariables.MINSYSTEM][rand1].strategies[rand2].join_strategy
								.clone();
						bestStrategies[i][j].setJoinStrategy(k, randJoinStrategy);
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
							.willItAttack(bestStrategies[i][k].getInitStrategy(i + 2));
				}
			}
		}

		MasterVariables.balanceProfiles = new boolean[MasterVariables.uniqueWorldSizes][][];
		MasterVariables.bandwagonProfiles = new boolean[MasterVariables.uniqueWorldSizes][][];
		MasterVariables.joiningProfiles = new boolean[MasterVariables.uniqueWorldSizes][][];
		// the following loop start from 1 as the joining profiles of bipolarity
		// shouldn't be initialized
		for (int i = 1; i < MasterVariables.balanceProfiles.length; i++) {
			MasterVariables.balanceProfiles[i] = new boolean[i + MasterVariables.MINSYSTEM][MasterVariables.PROFILES_PER_CAT];
			MasterVariables.bandwagonProfiles[i] = new boolean[i + MasterVariables.MINSYSTEM][MasterVariables.PROFILES_PER_CAT];
			MasterVariables.joiningProfiles[i] = new boolean[i + MasterVariables.MINSYSTEM][MasterVariables.PROFILES_PER_CAT];
			for (int k = 0; k < MasterVariables.balanceProfiles[i].length; k++) {
				for (int l = 0; l < MasterVariables.PROFILES_PER_CAT; l++) {
					MasterVariables.joiningProfiles[i][k][l] = MasterVariables.profilingWorlds_Joins[i - 1][l]
							.willItAttack(bestStrategies[i][k].getJoinStrategy(i + 2));
					if (MasterVariables.joiningProfiles[i][k][l]) {
						MasterVariables.balanceProfiles[i][k][l] = MasterVariables.profilingWorlds_Joins[i - 1][l]
								.willItAttack(bestStrategies[i][k].getBalanceStrategy(i + 2));
						MasterVariables.bandwagonProfiles[i][k][l] = !MasterVariables.balanceProfiles[i][k][l];
					} else {
						MasterVariables.balanceProfiles[i][k][l] = false;
						MasterVariables.bandwagonProfiles[i][k][l] = false;
					}
				}
			}
		}

		// initialize output strings
		MasterVariables.stateProfilesString = "";

		// initialize variable tracking last best strategy change
		lastGenChange = new int[MasterVariables.uniqueWorldSizes];
	}
}
