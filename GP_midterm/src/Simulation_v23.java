import java.util.Arrays;

public class Simulation_v23 extends GP_PilotStudy_v23 {
	
	
			

	public Simulation_v23() {
		simulation_initalize();
		for (currentGen = 1; currentGen <= MASTER_GENERATIONS; currentGen++) {
			for (currentTestedSize = MINSYSTEM; currentTestedSize <= MAXSYSTEM; currentTestedSize++) {
				System.out.println("\nCurrent tested size is " + currentTestedSize );
				System.out.print(" Working on Deme ");
				for (currentDeme = 0; currentDeme < currentTestedSize; currentDeme++) {
					System.out.print(currentDeme + 1 + " ");
					int currentTestedIndex = currentTestedSize - MINSYSTEM;
					initializeDeme();
					createTempArrays();
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
					if (prevBestFitness + prevBestFitness * ENHANCMENT_MARGIN < fitness[0] || currentGen == 1) {
						for (int j = 0; j < uniqueWorldSizes; j++){
							if (j>currentTestedIndex)
								break;
							FT_init_strategy[currentTestedIndex][currentDeme][j] = (char[]) init_strategy[0][j].clone();
						}
						if (currentTestedIndex > 0)
							for (int j = 0; j < uniqueWorldSizes - 1; j++){
								if (j>=currentTestedIndex)
									break;
								FT_join_strategy[currentTestedIndex - 1][currentDeme][j] = (char[]) join_strategy[0][j]
										.clone();
							}
					} /*else {
						init_strategy[0][currentTestedIndex] = (char[]) FT_init_strategy[currentTestedIndex][currentDeme]
								.clone();
						if (currentTestedIndex > 0)
							join_strategy[0][currentTestedIndex - 1] = (char[]) FT_join_strategy[currentTestedIndex - 1][currentDeme]
									.clone();
					}*/
					finalizeDeme();
				}
			}
			StatisticsPrinter_v23 StatisticsPrinter=new StatisticsPrinter_v23();
			if (MAXSYSTEM == 2)
				StatisticsPrinter.printStats(FT_init_strategy, null);
			else
				StatisticsPrinter.printStats(FT_init_strategy, FT_join_strategy);
		}

	}

	private void place_FT_in_Temp(int currentTestedIndex) {
		for (int j = 0; j < uniqueWorldSizes; j++)
			temp_init_strategy[0][currentTestedIndex][j] = (char[]) FT_init_strategy[currentTestedIndex][currentDeme][j]
					.clone();
		if (currentTestedIndex > 0) {
			for (int j = 0; j < uniqueWorldSizes - 1; j++) {
				temp_join_strategy[0][currentTestedIndex - 1][j] = (char[]) FT_join_strategy[currentTestedIndex - 1][currentDeme][j]
						.clone();
			}
		}
		/*for (int i = 1; i < currentTestedSize; i++) {
			int d = i;
			if (d == currentDeme)
				d++;
			temp_init_strategy[0][i][currentTestedIndex] = (char[]) FT_init_strategy[currentTestedIndex][i]
					.clone();
			if (currentTestedIndex > 0)
				temp_join_strategy[0][i][currentTestedIndex - 1] = (char[]) FT_join_strategy[currentTestedIndex - 1][i]
						.clone();
		}*/	
	}

	private void finalizeDeme() {
		for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
			demes_init_strategy[currentTestedSize - MINSYSTEM][currentDeme][indivs] = (char[]) init_strategy[indivs][currentTestedSize
					- MINSYSTEM].clone();
		}

		if (currentTestedSize > 2)
			for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
				demes_join_strategy[currentTestedSize - MINSYSTEM - 1][currentDeme][indivs] = (char[]) join_strategy[indivs][currentTestedSize
						- MINSYSTEM - 1].clone();
			}
	}

	private void initializeDeme() {
		init_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 1][];
		join_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 2][];

		for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
			init_strategy[indivs][currentTestedSize - MINSYSTEM] = (char[]) demes_init_strategy[currentTestedSize
					- MINSYSTEM][currentDeme][indivs].clone();
		}

		if (currentTestedSize > 2)
			for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
				join_strategy[indivs][currentTestedSize - MINSYSTEM - 1] = (char[]) demes_join_strategy[currentTestedSize
						- MINSYSTEM - 1][currentDeme][indivs].clone();
			}

		for (int i = currentTestedSize - MINSYSTEM - 1; i >= 0; i--) {
			for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
				init_strategy[indivs][i] = (char[]) FT_init_strategy[i][rd.nextInt(FT_init_strategy[i].length - 1)][i]
						.clone();
			}
			if (i > 0)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
					join_strategy[indivs][i - 1] = (char[]) FT_join_strategy[i - 1][rd
							.nextInt(FT_join_strategy[i - 1].length - 1)][i-1].clone();
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
		demes_join_strategy = new char[uniqueWorldSizes-1][][][];

		implementClass_v23 implement = new implementClass_v23();

		for (int i = 0; i < uniqueWorldSizes; i++) {
			demes_init_strategy[i] = new char[i + MINSYSTEM][TOTAL_POPSIZE][];
			for (int currentDeme = 0; currentDeme < demes_init_strategy[i].length; currentDeme++)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++)
					demes_init_strategy[i][currentDeme][indivs] = implement.create_random_indiv(DEPTH,
							SIMULATECONSTRUCTIVISM);
		}
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			demes_join_strategy[i] = new char[i + MINSYSTEM + 1][TOTAL_POPSIZE][];
			for (int currentDeme = 0; currentDeme < demes_join_strategy[i].length; currentDeme++)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++)
					demes_join_strategy[i][currentDeme][indivs] = implement.create_random_indiv(DEPTH,
							SIMULATECONSTRUCTIVISM);
		}

		init_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes][];
		join_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes - 1][];

		temp_init_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes][];
		temp_join_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes - 1][];

		tempFitness = new double[processors][POPSIZE_PER_PROCESSOR];

		FT_init_strategy = new char[uniqueWorldSizes][][][];
		FT_join_strategy = new char[uniqueWorldSizes - 1][][][];

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
			FT_join_strategy[i] = new char[i + MINSYSTEM + 2][uniqueWorldSizes - 1][];
			for (int k = 0; k < FT_join_strategy[i].length - 1; k++)
				for (int j = 0; j < uniqueWorldSizes - 1; j++) {
				FT_join_strategy[i][k][j] = (char[]) demes_join_strategy[i][rd.nextInt(i + MINSYSTEM + 1)][rd
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
		char[][] tempIndiv_1, tempIndiv_2;
		for (int i = TOTAL_POPSIZE - 1; i > 0; i--) {
			index = rd.nextInt(i + 1);
			tempFitness = fitness[index];
			fitness[index] = fitness[i];
			fitness[i] = tempFitness;

			tempIndiv_1 = init_strategy[index];
			init_strategy[index] = init_strategy[i];
			init_strategy[i] = tempIndiv_1;

			tempIndiv_2 = join_strategy[index];
			join_strategy[index] = join_strategy[i];
			join_strategy[i] = tempIndiv_2;
		}
		shuffled = true;
		sortedFit = false;

	}

	private static void sortDesc() {
		// sort fitness and strategy arrays in fitness descending order

		char[][][] tempinit_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 1][];
		char[][][] tempjoin_strategy = new char[TOTAL_POPSIZE][currentTestedSize - 2][];
		
		for (int i = 0; i < TOTAL_POPSIZE; i++) {
			tempinit_strategy[i][0] = (char[]) init_strategy[i][0].clone();
		}
	
		for (int k = 1; k < currentTestedSize - 1; k++) {
				for (int i = 0; i < TOTAL_POPSIZE; i++) {
					tempinit_strategy[i][k] = (char[]) init_strategy[i][k].clone();
					tempjoin_strategy[i][k - 1] = (char[]) join_strategy[i][k - 1].clone();
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

		for (int i = 0; i < TOTAL_POPSIZE; i++) {
			init_strategy[i] = null;
			join_strategy[i] = null;
			int j = 0;
			while (init_strategy[i] == null && j < TOTAL_POPSIZE) {
				if (fitness[i] == tempFitness[j]) {
					init_strategy[i] = (char[][]) tempinit_strategy[j].clone();
					join_strategy[i] = (char[][]) tempjoin_strategy[j].clone();
					tempinit_strategy[j] = null;
					tempjoin_strategy[j] = null;
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
					join_strategy[counter + j][k] = (char[]) temp_join_strategy[i][j][k].clone();
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
					temp_join_strategy[i][j][k] = (char[]) join_strategy[counter + j][k].clone();
				}

				tempFitness[i][j] = fitness[counter + j];
			}
			counter += POPSIZE_PER_PROCESSOR;
		}

	}

	private static void initiateThreads(int operationID) {
		thread = new Thread[processors];
		implement = new implementClass_v23[processors];

		for (int threadID = 0; threadID < processors; threadID++) {
			implement[threadID] = new implementClass_v23(threadID, randNum, POPSIZE_PER_PROCESSOR,
					tempFitness[threadID], temp_init_strategy[threadID], temp_join_strategy[threadID], TOTAL_TESTCASES, SIMULATECONSTRUCTIVISM);
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