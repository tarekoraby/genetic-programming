import java.util.Arrays;

public class Simulation_v27 extends GP_PilotStudy_v27 {

	public Simulation_v27() {
		simulation_initalize();
		for (currentGen = 1; currentGen <= MASTER_GENERATIONS; currentGen++) {
			setRandommness();
			for (currentTestedSize = MINSYSTEM; currentTestedSize <= MAXSYSTEM; currentTestedSize++) {
				System.out.println("\nCurrent tested size is " + currentTestedSize);
				System.out.print(" Working on Deme ");
				for (currentDeme = 0; currentDeme < currentTestedSize; currentDeme++) {
					System.out.print(currentDeme + 1 + " ");
					int currentTestedIndex = currentTestedSize - MINSYSTEM;
					initializeDeme();
					createTempArrays();
					/*
					 * if (!Arrays.equals(temp_init_strategy[0][0][0],
					 * FT_init_strategy[currentTestedIndex][currentDeme][0]))
					 * System.out.println("error");
					 */
					calculateFitness();
					evolve();
					for (int demeGen = 0; demeGen < DEME_GENERATIONS; demeGen++) {
						calculateFitness();
						evolve();
					}
					place_FT_in_Temp(currentTestedIndex);
					calculateFitness();
					createMasterArrays();
					double prevBestFitness = fitness[0];
					sortDesc();
					//System.out.println(" fitbest " + fitness[0]);
					if (prevBestFitness + prevBestFitness * ENHANCMENT_MARGIN < fitness[0]) {
						//System.out.println("\nChange in deme " + (currentDeme + 1) + " " + prevBestFitness + " "	+ fitness[0] + " " + tempFitness[0][0]);
						for (int j = 0; j < uniqueWorldSizes; j++) {
							if (j > currentTestedIndex)
								break;
							FT_init_strategy[currentTestedIndex][currentDeme][j] = (char[]) init_strategy[0][j].clone();
						}
						if (currentTestedIndex > 0)
							for (int j = 0; j < uniqueWorldSizes - 1; j++) {
								if (j >= currentTestedIndex)
									break;
								FT_balance_strategy[currentTestedIndex - 1][currentDeme][j] = (char[]) balance_strategy[0][j]
										.clone();
								FT_bandwagon_strategy[currentTestedIndex - 1][currentDeme][j] = (char[]) bandwagon_strategy[0][j]
										.clone();
							}
					} /*
					 * else { init_strategy[0][currentTestedIndex] = (char[])
					 * FT_init_strategy[currentTestedIndex][currentDeme]
					 * .clone(); if (currentTestedIndex > 0)
					 * balance_strategy[0][currentTestedIndex - 1] = (char[])
					 * FT_balance_strategy[currentTestedIndex - 1][currentDeme]
					 * .clone(); }
					 */
					finalizeDeme();
				}
			}
			StatisticsPrinter_v27 StatisticsPrinter = new StatisticsPrinter_v27();
			if (MAXSYSTEM == 2)
				StatisticsPrinter.printStats(FT_init_strategy, null, null);
			else
				StatisticsPrinter.printStats(FT_init_strategy, FT_balance_strategy, FT_bandwagon_strategy);
			// resetTestCases();
		}

	}

	private void resetTestCases() {
		int prevLevelTests = 0;
		for (int currentLevel = 0; currentLevel < TESTCASES.length; currentLevel++) {
			int currentLevelTests = TESTCASES[currentLevel];
			if (currentLevel > 0) {
				prevLevelTests += TESTCASES[currentLevel - 1];
				currentLevelTests += prevLevelTests;
			}

			for (int test = prevLevelTests; test < currentLevelTests; test++) {
				for (int k = 0; k <= CAPCHANGE; k++) {
					testStrategyIndexes[test][k] = (int[]) tempTestedStrategyIndexes[test][k].clone();
				}
			}
		}
	}

	private void setRandommness() {

		int prevLevelTests = 0;
		for (int currentLevel = 0; currentLevel < TESTCASES.length; currentLevel++) {
			int currentLevelTests = TESTCASES[currentLevel];
			if (currentLevel > 0) {
				prevLevelTests += TESTCASES[currentLevel - 1];
				currentLevelTests += prevLevelTests;
			}

			for (int test = prevLevelTests; test < currentLevelTests; test++) {
				length[test] = MINSYSTEM + currentLevel;

				world_sizes[length[test] - MINSYSTEM]++;

				initialEnmity[test] = new int[length[test]][length[test]];
				for (int i = 0; i < length[test]; i++) {
					for (int k = 0; k < length[test] - 1; k++) {
						initialEnmity[test][i][k] = rd.nextInt(ENMITYLEVELS - 1);
					}
					initialEnmity[test][i][i] = -99;
				}

				double remainder = 1;
				capabilities[test] = new double[length[test]];
				for (int i = 0; i < length[test] - 1; i++) {
					capabilities[test][i] = remainder * rd.nextDouble();
					remainder -= capabilities[test][i];
				}

				capabilities[test][length[test] - 1] = remainder;

				for (int k = 0; k < CAPCHANGE; k++) {
					capChangeRates[test][k] = new double[length[test]];
					for (int i = 0; i < length[test]; i++) {
						capChangeRates[test][k][i] = rd.nextDouble();
					}
				}

				int[] taken = new int[length[test] - 1];
				for (int i = 0; i < length[test] - 1; i++)
					taken[i] = i;

				testedStrategyIndex[test][0] = rd.nextInt(length[test]);
				testStrategyIndexes[test][0] = new int[length[test]];
				for (int i = 0; i < length[test]; i++) {
					if (i == testedStrategyIndex[test][0])
						continue;
					int random = rd.nextInt(length[test] - 1);
					while (taken[random] < 0)
						random = rd.nextInt(length[test] - 1);
					testStrategyIndexes[test][0][i] = random;
					taken[random] = -99;

				}

				for (int k = 1; k <= CAPCHANGE; k++) {
					testStrategyIndexes[test][k] = new int[length[test]];
					changeIndexes[test][k - 1] = new int[length[test]];
					boolean[] temp = new boolean[length[test]];
					for (int i = 0; i < length[test]; i++) {
						temp[i] = false;
					}
					int random;

					for (int i = 0; i < length[test]; i++) {
						random = rd.nextInt(length[test]);
						while (temp[random] == true)
							random = rd.nextInt(length[test]);

						temp[random] = true;

						testStrategyIndexes[test][k][i] = testStrategyIndexes[test][k - 1][random];
						changeIndexes[test][k - 1][random] = i;

						if (random == testedStrategyIndex[test][k - 1])
							testedStrategyIndex[test][k] = i;

					}
				}

				for (int k = 0; k <= CAPCHANGE; k++) {
					for (int i = 0; i < length[test]; i++) {
						if (testedStrategyIndex[test][k] == i)
							continue;
						if (rd.nextDouble() < STRATEGY_CHANGE_PROB) {
							testStrategyIndexes[test][k][i] = rd.nextInt(length[test] - 1);
						}
						if (rd.nextDouble() < RANDOMPLAYER_PROB) {
							testStrategyIndexes[test][k][i] = length[test] - 1;
						}
					}
				}

				probabilities[test] = new double[30 * MAXSYSTEM];
				for (int i = 0; i < 30 * MAXSYSTEM; i++)
					probabilities[test][i] = rd.nextDouble();

				GaussProb[test] = new double[10 * MAXSYSTEM];
				for (int i = 0; i < 10 * MAXSYSTEM; i++)
					GaussProb[test][i] = 1 + Math.abs(rd.nextGaussian() * GAUSS_PROB_FACTOR);

				GaussProb2[test] = new double[10 * CAPCHANGE * MAXSYSTEM];
				for (int i = 0; i < 10 * CAPCHANGE * MAXSYSTEM; i++)
					GaussProb2[test][i] = Math.abs(rd.nextGaussian());
			}
		}

	}

	private void place_FT_in_Temp(int currentTestedIndex) {
		for (int j = 0; j <= currentTestedIndex; j++)
			temp_init_strategy[0][0][j] = (char[]) FT_init_strategy[currentTestedIndex][currentDeme][j].clone();
		if (currentTestedIndex > 0) {
			for (int j = 0; j < uniqueWorldSizes - 1; j++) {
				temp_balance_strategy[0][0][j] = (char[]) FT_balance_strategy[currentTestedIndex - 1][currentDeme][j]
						.clone();
				temp_bandwagon_strategy[0][0][j] = (char[]) FT_bandwagon_strategy[currentTestedIndex - 1][currentDeme][j]
						.clone();
			}
		}
		/*
		 * for (int i = 1; i < currentTestedSize; i++) { int d = i; if (d ==
		 * currentDeme) d++; temp_init_strategy[0][i][currentTestedIndex] =
		 * (char[]) FT_init_strategy[currentTestedIndex][i] .clone(); if
		 * (currentTestedIndex > 0)
		 * temp_balance_strategy[0][i][currentTestedIndex - 1] = (char[])
		 * FT_balance_strategy[currentTestedIndex - 1][i] .clone(); }
		 */
	}

	private void finalizeDeme() {
		for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
			demes_init_strategy[currentTestedSize - MINSYSTEM][currentDeme][indivs] = (char[]) init_strategy[indivs][currentTestedSize
					- MINSYSTEM].clone();
		}

		if (currentTestedSize > 2)
			for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
				demes_balance_strategy[currentTestedSize - MINSYSTEM - 1][currentDeme][indivs] = (char[]) balance_strategy[indivs][currentTestedSize
						- MINSYSTEM - 1].clone();
				demes_bandwagon_strategy[currentTestedSize - MINSYSTEM - 1][currentDeme][indivs] = (char[]) bandwagon_strategy[indivs][currentTestedSize
						- MINSYSTEM - 1].clone();
			}
	}

	private void initializeDeme() {
		init_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 1][];
		balance_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 2][];
		bandwagon_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 2][];

		for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
			init_strategy[indivs][currentTestedSize - MINSYSTEM] = (char[]) demes_init_strategy[currentTestedSize
					- MINSYSTEM][currentDeme][indivs].clone();
		}

		if (currentTestedSize > 2)
			for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
				balance_strategy[indivs][currentTestedSize - MINSYSTEM - 1] = (char[]) demes_balance_strategy[currentTestedSize
						- MINSYSTEM - 1][currentDeme][indivs].clone();
				bandwagon_strategy[indivs][currentTestedSize - MINSYSTEM - 1] = (char[]) demes_bandwagon_strategy[currentTestedSize
						- MINSYSTEM - 1][currentDeme][indivs].clone();
			}

		for (int i = currentTestedSize - MINSYSTEM - 1; i >= 0; i--) {
			for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
				init_strategy[indivs][i] = (char[]) FT_init_strategy[i][rd.nextInt(FT_init_strategy[i].length - 1)][i]
						.clone();
			}
			if (i > 0)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
					balance_strategy[indivs][i - 1] = (char[]) FT_balance_strategy[i - 1][rd
							.nextInt(FT_balance_strategy[i - 1].length - 1)][i - 1].clone();
					bandwagon_strategy[indivs][i - 1] = (char[]) FT_bandwagon_strategy[i - 1][rd
							.nextInt(FT_bandwagon_strategy[i - 1].length - 1)][i - 1].clone();
				}
		}
	}

	private static void simulation_initalize() {
		System.out.println("\n\n******************************************************************");
		System.out.println("Start of simulation " + currentSimulation);
		System.out.println("******************************************************************\n\n");

		currentTestedSize = MINSYSTEM;
		fitness = new double[TOTAL_POPSIZE];

		prevSimilarityCount = 0;

		sortedFit = false;
		shuffled = false;

		demes_init_strategy = new char[uniqueWorldSizes][][][];
		demes_balance_strategy = new char[uniqueWorldSizes - 1][][][];
		demes_bandwagon_strategy = new char[uniqueWorldSizes - 1][][][];

		implementClass_v27 implement = new implementClass_v27();

		for (int i = 0; i < uniqueWorldSizes; i++) {
			demes_init_strategy[i] = new char[i + MINSYSTEM][TOTAL_POPSIZE][];
			for (int currentDeme = 0; currentDeme < demes_init_strategy[i].length; currentDeme++)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++)
					demes_init_strategy[i][currentDeme][indivs] = implement.create_random_indiv(DEPTH,
							SIMULATECONSTRUCTIVISM);
		}
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			demes_balance_strategy[i] = new char[i + MINSYSTEM + 1][TOTAL_POPSIZE][];
			demes_bandwagon_strategy[i] = new char[i + MINSYSTEM + 1][TOTAL_POPSIZE][];
			for (int currentDeme = 0; currentDeme < demes_balance_strategy[i].length; currentDeme++)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
					demes_balance_strategy[i][currentDeme][indivs] = implement.create_random_indiv(DEPTH,
							SIMULATECONSTRUCTIVISM);
					demes_bandwagon_strategy[i][currentDeme][indivs] = implement.create_random_indiv(DEPTH,
							SIMULATECONSTRUCTIVISM);
				}
		}

		init_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes][];
		balance_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes - 1][];
		bandwagon_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes - 1][];

		temp_init_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes][];
		temp_balance_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes - 1][];
		temp_bandwagon_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes - 1][];

		tempFitness = new double[processors][POPSIZE_PER_PROCESSOR];

		FT_init_strategy = new char[uniqueWorldSizes][][][];
		FT_balance_strategy = new char[uniqueWorldSizes - 1][][][];
		FT_bandwagon_strategy = new char[uniqueWorldSizes - 1][][][];

		for (int i = 0; i < uniqueWorldSizes; i++) {
			// extra one for randomness
			FT_init_strategy[i] = new char[i + MINSYSTEM + 1][uniqueWorldSizes][];
			for (int k = 0; k < FT_init_strategy[i].length - 1; k++)
				for (int j = 0; j < uniqueWorldSizes; j++) {
					FT_init_strategy[i][k][j] = (char[]) demes_init_strategy[i][rd.nextInt(i + MINSYSTEM)][rd
							.nextInt(TOTAL_POPSIZE)].clone();
				}
		}
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			// extra one for randomness
			FT_balance_strategy[i] = new char[i + MINSYSTEM + 2][uniqueWorldSizes - 1][];
			FT_bandwagon_strategy[i] = new char[i + MINSYSTEM + 2][uniqueWorldSizes - 1][];
			for (int k = 0; k < FT_balance_strategy[i].length - 1; k++)
				for (int j = 0; j < uniqueWorldSizes - 1; j++) {
					FT_balance_strategy[i][k][j] = (char[]) demes_balance_strategy[i][rd.nextInt(i + MINSYSTEM + 1)][rd
							.nextInt(TOTAL_POPSIZE)].clone();
					FT_bandwagon_strategy[i][k][j] = (char[]) demes_bandwagon_strategy[i][rd.nextInt(i + MINSYSTEM + 1)][rd
							.nextInt(TOTAL_POPSIZE)].clone();
				}
		}

	}

	private static void evolve() {

		initiateThreads(3);
		waitForThreadsFinish();
	}

	private static void shuffle() {
		// Implementing Fisher Yates shuffle of fitness and population
		// arrays
		int index;
		double tempFitness;
		char[][] tempIndiv_1, tempIndiv_2, tempIndiv_3;
		for (int i = TOTAL_POPSIZE - 1; i > 0; i--) {
			index = rd.nextInt(i + 1);
			tempFitness = fitness[index];
			fitness[index] = fitness[i];
			fitness[i] = tempFitness;

			tempIndiv_1 = init_strategy[index];
			init_strategy[index] = init_strategy[i];
			init_strategy[i] = tempIndiv_1;

			tempIndiv_2 = balance_strategy[index];
			balance_strategy[index] = balance_strategy[i];
			balance_strategy[i] = tempIndiv_2;
			
			tempIndiv_3 = bandwagon_strategy[index];
			bandwagon_strategy[index] = bandwagon_strategy[i];
			bandwagon_strategy[i] = tempIndiv_3;
		}
		shuffled = true;
		sortedFit = false;

	}

	private static void sortDesc() {
		// sort fitness and strategy arrays in fitness descending order

		char[][][] tempinit_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 1][];
		char[][][] tempbalance_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 2][];
		char[][][] tempbandwagon_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 2][];

		for (int i = 0; i < TOTAL_POPSIZE; i++) {
			tempinit_strategy[i][0] = (char[]) init_strategy[i][0].clone();
		}

		for (int k = 1; k < currentTestedSize - 1; k++) {
			for (int i = 0; i < TOTAL_POPSIZE; i++) {
				tempinit_strategy[i][k] = (char[]) init_strategy[i][k].clone();
				tempbalance_strategy[i][k - 1] = (char[]) balance_strategy[i][k - 1].clone();
				tempbandwagon_strategy[i][k - 1] = (char[]) bandwagon_strategy[i][k - 1].clone();
			}
		}

		double[] tempFitness = (double[]) fitness.clone();
		Arrays.sort(fitness);

		double temp;
		for (int i = 0; i < TOTAL_POPSIZE / 2; i++) {
			temp = fitness[i];
			fitness[i] = fitness[TOTAL_POPSIZE - 1 - i];
			fitness[TOTAL_POPSIZE - 1 - i] = temp;
		}

		init_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 1][];
		balance_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 2][];
		bandwagon_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 2][];

		for (int i = 0; i < TOTAL_POPSIZE; i++) {
			int j = 0;
			while (init_strategy[i][currentTestedSize - MINSYSTEM] == null && j < TOTAL_POPSIZE) {
				if (fitness[i] == tempFitness[j]) {

					init_strategy[i][0] = (char[]) tempinit_strategy[j][0].clone();

					for (int k = 1; k < currentTestedSize - 1; k++) {
						init_strategy[i][k] = (char[]) tempinit_strategy[j][k].clone();
						balance_strategy[i][k - 1] = (char[]) tempbalance_strategy[j][k - 1].clone();
						bandwagon_strategy[i][k - 1] = (char[]) tempbandwagon_strategy[j][k - 1].clone();
					}

					tempinit_strategy[j] = null;
					tempbalance_strategy[j] = null;
					tempbandwagon_strategy[j] = null;
					tempFitness[j] = -1e-5;
				}
				j++;
			}
		}

		sortedFit = true;
		shuffled = false;

	}

	private static void calculateFitness() {
		initiateThreads(2);
		waitForThreadsFinish();
		sortedFit = false;
	}

	private static void createMasterArrays() {
		int counter = 0;
		for (int i = 0; i < processors; i++) {
			for (int j = 0; j < POPSIZE_PER_PROCESSOR; j++) {
				for (int k = 0; k < currentTestedSize - 1; k++) {
					init_strategy[counter + j][k] = (char[]) temp_init_strategy[i][j][k].clone();
				}

				for (int k = 0; k < currentTestedSize - 2; k++) {
					balance_strategy[counter + j][k] = (char[]) temp_balance_strategy[i][j][k].clone();
					bandwagon_strategy[counter + j][k] = (char[]) temp_bandwagon_strategy[i][j][k].clone();
				}

				fitness[counter + j] = tempFitness[i][j];
			}
			counter += POPSIZE_PER_PROCESSOR;
		}
	}

	private static void createTempArrays() {
		int counter = 0;
		for (int i = 0; i < processors; i++) {
			for (int j = 0; j < POPSIZE_PER_PROCESSOR; j++) {
				for (int k = 0; k < currentTestedSize - 1; k++) {
					temp_init_strategy[i][j][k] = (char[]) init_strategy[counter + j][k].clone();
				}

				for (int k = 0; k < currentTestedSize - 2; k++) {
					temp_balance_strategy[i][j][k] = (char[]) balance_strategy[counter + j][k].clone();
					temp_bandwagon_strategy[i][j][k] = (char[]) bandwagon_strategy[counter + j][k].clone();
				}

				tempFitness[i][j] = fitness[counter + j];
			}
			counter += POPSIZE_PER_PROCESSOR;
		}

	}

	private static void initiateThreads(int operationID) {
		thread = new Thread[processors];
		implement = new implementClass_v27[processors];

		for (int threadID = 0; threadID < processors; threadID++) {
			implement[threadID] = new implementClass_v27(threadID, randNum, POPSIZE_PER_PROCESSOR,
					tempFitness[threadID], temp_init_strategy[threadID], temp_balance_strategy[threadID],
					temp_bandwagon_strategy[threadID], TOTAL_TESTCASES, SIMULATECONSTRUCTIVISM);
			implement[threadID].setOperationID(operationID);
			thread[threadID] = new Thread(implement[threadID]);
			thread[threadID].start();
		}

	}

	private static void waitForThreadsFinish() {
		for (int i = 0; i < processors; i++) {
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}