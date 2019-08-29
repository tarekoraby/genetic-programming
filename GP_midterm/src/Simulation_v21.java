import java.util.Arrays;

public class Simulation_v21 extends GP_PilotStudy_v21 {

	public Simulation_v21() {
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
					for (int demeGen = 0; demeGen < DEME_GENERATIONS; demeGen++) {
						evolve();	
						temp_init_strategy[0][0][currentTestedIndex]=(char[]) FT_init_strategy[currentTestedIndex][currentDeme].clone();
						if (currentTestedIndex > 0)
							temp_join_strategy[0][0][currentTestedIndex-1]=(char[]) FT_join_strategy[currentTestedIndex-1][currentDeme].clone();				
						calculateFitness();
					}
					createMasterArrays();
					double prevBestFitness = fitness[0];
					sortDesc();
					if (prevBestFitness + prevBestFitness * ENHANCMENT_MARGIN < fitness[0]) {
						FT_init_strategy[currentTestedIndex][currentDeme] = (char[]) init_strategy[0][currentTestedIndex]
								.clone();
						if (currentTestedIndex > 0)
							FT_join_strategy[currentTestedIndex - 1][currentDeme] = (char[]) join_strategy[0][currentTestedIndex - 1]
									.clone();
					} else {
						init_strategy[0][currentTestedIndex] = (char[]) FT_init_strategy[currentTestedIndex][currentDeme]
								.clone();
						if (currentTestedIndex > 0)
							join_strategy[0][currentTestedIndex - 1] = (char[]) FT_join_strategy[currentTestedIndex - 1][currentDeme]
									.clone();
					}
					finalizeDeme();
				}
			}
			if (MAXSYSTEM == 2)
				printStats(FT_init_strategy, null);
			else
				printStats(FT_init_strategy, FT_join_strategy);
		}

	}

	private void finalizeDeme() {
		for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
			demes_init_strategy[currentTestedSize - MINSYSTEM][currentDeme][indivs] = (char[]) init_strategy[indivs][currentTestedSize - MINSYSTEM].clone();
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
			init_strategy[indivs][currentTestedSize - MINSYSTEM] = (char[]) demes_init_strategy[currentTestedSize - MINSYSTEM][currentDeme][indivs].clone();
		}

		if (currentTestedSize > 2)
			for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
				join_strategy[indivs][currentTestedSize - MINSYSTEM - 1] = (char[]) demes_join_strategy[currentTestedSize
						- MINSYSTEM - 1][currentDeme][indivs].clone();
			}

		for (int i = currentTestedSize - MINSYSTEM - 1; i >= 0; i--) {
			for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
				init_strategy[indivs][i] = (char[]) FT_init_strategy[i][rd.nextInt(FT_init_strategy[i].length-1)].clone();
			}
			if (i > 0)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++) {
					join_strategy[indivs][i-1] = (char[]) FT_join_strategy[i-1][rd.nextInt(FT_join_strategy[i-1].length-1)].clone();
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
		
		implementClass_v21 implement = new implementClass_v21();

		for (int i = 0; i < uniqueWorldSizes; i++) {
			demes_init_strategy[i] = new char[i + MINSYSTEM][TOTAL_POPSIZE][];
			for (int currentDeme = 0; currentDeme < demes_init_strategy[i].length; currentDeme++)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++)
					demes_init_strategy[i][currentDeme][indivs] = implement.create_random_indiv(DEPTH);
		}
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			demes_join_strategy[i] = new char[i + MINSYSTEM + 1][TOTAL_POPSIZE][];
			for (int currentDeme = 0; currentDeme < demes_join_strategy[i].length; currentDeme++)
				for (int indivs = 0; indivs < TOTAL_POPSIZE; indivs++)
					demes_join_strategy[i][currentDeme][indivs] = implement.create_random_indiv(DEPTH);
		}
		

	
		
		init_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes][];
		join_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes - 1][];
		
		temp_init_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes][];
		temp_join_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes - 1][];
		
		tempFitness = new double[processors][POPSIZE_PER_PROCESSOR];

		FT_init_strategy = new char[uniqueWorldSizes][][];
		FT_join_strategy = new char[uniqueWorldSizes - 1][][];

		for (int i = 0; i < uniqueWorldSizes; i++) {
			// extra one for randomness
			FT_init_strategy[i] = new char[i + MINSYSTEM + 1][];
			for (int k = 0; k < FT_init_strategy[i].length - 1; k++)
				FT_init_strategy[i][k] = (char[]) demes_init_strategy[i][rd.nextInt(i + MINSYSTEM)][rd
						.nextInt(TOTAL_POPSIZE)].clone();
		}
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			// extra one for randomness
			FT_join_strategy[i] = new char[i + MINSYSTEM + 2][];
			for (int k = 0; k < FT_join_strategy[i].length - 1; k++)
				FT_join_strategy[i][k] = (char[]) demes_join_strategy[i][rd.nextInt(i + MINSYSTEM + 1)][rd
						.nextInt(TOTAL_POPSIZE)].clone();
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

	private static void printStats(char[][][] init_strategy, char[][][] join_strategy) {
		
		//how similar are the stratgies to each oteher in every level
		//how many attacks per world

		if (!sortedFit) {
			System.out.println("Not Sorted!!!");
			System.exit(0);
		}

		System.out.println("\n\nSimulation=" + currentSimulation + "  Generation=" + currentGen);

		char[][][] temp_init = new char[uniqueWorldSizes][][];
		char[][][] temp_join = null;

		for (int i = 0; i < uniqueWorldSizes; i++) {
			temp_init[i] = new char[i + MINSYSTEM][];
			for (int k = 0; k < temp_init[i].length; k++)
				temp_init[i][k] = init_strategy[i][k];
		}
		init_strategy = temp_init;
		
		if (join_strategy != null) {
			temp_join = new char[uniqueWorldSizes - 1][][];
			for (int i = 0; i < uniqueWorldSizes - 1; i++) {
				temp_join[i] = new char[i + MINSYSTEM + 1][];
				for (int k = 0; k < temp_join[i].length; k++)
					temp_join[i][k] = join_strategy[i][k];
			}
			join_strategy = temp_join;
		}

		populateProfiles(init_strategy, join_strategy);	
		
		printProfilesToFile();
		
		
		for (int i = 0; i < attackProfiles.length; i++) {
			System.out.println("In world " + (i+MINSYSTEM) + ", percentage of attacks of state ");
			for (int k = 0; k < attackProfiles[i].length; k++) {
				System.out.print((k+1) + " is ") ;
				int totalAttacks=0;
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (attackProfiles[i][k][l] == true)
						totalAttacks++;
				}
				System.out.println((double)totalAttacks/PROFILES_PER_CAT * 100  + " %");
			}
		}
		
		System.out.println();

		if (MAXSYSTEM > 2) {
			for (int i = 0; i < balanceProfiles.length; i++) {
				System.out.println("In world " + (i + MINSYSTEM + 1) + ", percentage of balances of state ");
				for (int k = 0; k < balanceProfiles[i].length; k++) {
					System.out.print((k + 1) + " is ");
					int totalbalances = 0;
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (balanceProfiles[i][k][l] == true)
							totalbalances++;
					}
					System.out.println((double) totalbalances / PROFILES_PER_CAT * 100 + " %");
				}
			}

			System.out.println();

			for (int i = 0; i < bandwagonProfiles.length; i++) {
				System.out.println("In world " + (i + MINSYSTEM + 1) + ", percentage of bandwagons of state ");
				for (int k = 0; k < bandwagonProfiles[i].length; k++) {
					System.out.print((k + 1) + " is ");
					int totalbandwagons = 0;
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (bandwagonProfiles[i][k][l] == true)
							totalbandwagons++;
					}
					System.out.println((double) totalbandwagons / PROFILES_PER_CAT * 100 + " %");
				}
			}

		}

	}

	private static void printProfilesToFile() {
		for (int i = 0; i < attackProfiles.length; i++) {
			for (int k = 0; k < attackProfiles[i].length; k++) {
				outputString = (currentSimulation + "," + (k + 1) + "," + currentGen + "," + (i + MINSYSTEM));
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					outputString += ("," + (attackProfiles[i][k][l] == true ? 1 : 0));
				}
				if (i > 0) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						outputString += ("," + (balanceProfiles[i - 1][k][l] == true ? 1 : 0));
					}
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						outputString += ("," + (bandwagonProfiles[i - 1][k][l] == true ? 1 : 0));
					}
				}
				state_profiles_writer.writeToFile(outputString + "\n");
			}	
		}
	}

	private static void populateProfiles(char[][][] init_strategy, char[][][] join_strategy) {
		attackProfiles = new boolean[init_strategy.length][][];

		for (int i = 0; i < attackProfiles.length; i++)
			attackProfiles[i] = new boolean[init_strategy[i].length][PROFILES_PER_CAT];

		for (int i = 0; i < attackProfiles.length; i++) {
			for (int k = 0; k < attackProfiles[i].length; k++) {
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					attackProfiles[i][k][l] = profilingWorlds_Attacks[i][l].willItAttack(init_strategy[i][k], 0);
				}
			}
		}

		if (join_strategy != null) {
			balanceProfiles = new boolean[join_strategy.length][][];
			bandwagonProfiles = new boolean[join_strategy.length][][];

			for (int i = 0; i < balanceProfiles.length; i++) {
				balanceProfiles[i] = new boolean[join_strategy[i].length][PROFILES_PER_CAT];
				bandwagonProfiles[i] = new boolean[join_strategy[i].length][PROFILES_PER_CAT];
			}

			for (int i = 0; i < balanceProfiles.length; i++) {
				for (int k = 0; k < balanceProfiles[i].length; k++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						balanceProfiles[i][k][l] = profilingWorlds_Joins[i][l].willItAttack(join_strategy[i][k], 1);
						bandwagonProfiles[i][k][l] = profilingWorlds_Joins[i][l].willItAttack(join_strategy[i][k], 2);
					}
				}
			}

		}
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
			System.arraycopy(temp_init_strategy[i], 0, init_strategy, counter, POPSIZE_PER_PROCESSOR);
			System.arraycopy(temp_join_strategy[i], 0, join_strategy, counter, POPSIZE_PER_PROCESSOR);
			System.arraycopy(tempFitness[i], 0, fitness, counter, POPSIZE_PER_PROCESSOR);
			counter += POPSIZE_PER_PROCESSOR;
		}
	}

	private static void createTempArrays() {
		int counter = 0;
		for (int i = 0; i < processors; i++) {
			System.arraycopy(init_strategy, counter, temp_init_strategy[i], 0, POPSIZE_PER_PROCESSOR);
			System.arraycopy(join_strategy, counter, temp_join_strategy[i], 0, POPSIZE_PER_PROCESSOR);
			System.arraycopy(fitness, counter, tempFitness[i], 0, POPSIZE_PER_PROCESSOR);
			counter += POPSIZE_PER_PROCESSOR;
		}

	}

	private static void initiateThreads(int operationID) {
		thread = new Thread[processors];
		implement = new implementClass_v21[processors];

		for (int threadID = 0; threadID < processors; threadID++) {
			implement[threadID] = new implementClass_v21(threadID, randNum, POPSIZE_PER_PROCESSOR,
					tempFitness[threadID], temp_init_strategy[threadID], temp_join_strategy[threadID], TOTAL_TESTCASES);
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

	static int traverse(char[] buffer, int buffercount) {
		if (buffer[buffercount] < FSET_1_START)
			return (++buffercount);

		switch (buffer[buffercount]) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case GT:
		case LT:
		case EQ:
		case AND:
		case OR:
			return (traverse(buffer, traverse(buffer, ++buffercount)));
		}
		return (0); // should never get here
	}

	private static void calcSimilarity(char[][] init_strategy, char[][] join_strategy, int indivs) {
		similarityWarInit_Avg = new double[uniqueWorldSizes];
		similarityWarJoin_Avg = new double[uniqueWorldSizes - 1];

		

		if (indivs == 1) {
			for (int k = 0; k < uniqueWorldSizes - 1; k++) {
				similarityWarInit_Avg[k] = 1;
				similarityWarJoin_Avg[k] = 1;
			}
			similarityWarInit_Avg[uniqueWorldSizes - 1] = 1;
		}

		int[] m = new int[uniqueWorldSizes];
		int[] n = new int[uniqueWorldSizes - 1];
		for (int i = 0; i < indivs - 1; i++)
			for (int j = i + 1; j < indivs; j++) {
				for (int k = 0; k < uniqueWorldSizes; k++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						m[k]++;
						if (attackProfiles[i][k][l] == attackProfiles[j][k][l]) {
							similarityWarInit_Avg[k]++;
						}
					}
				}
			}

		if (currentTestedSize > 2)
			for (int i = 0; i < indivs - 1; i++)
				for (int j = i + 1; j < indivs; j++) {
					for (int k = 0; k < uniqueWorldSizes - 1; k++) {
						for (int l = 0; l < PROFILES_PER_CAT; l++) {
							n[k]++;
							if (balanceProfiles[i][k][l] == balanceProfiles[j][k][l] && bandwagonProfiles[i][k][l] == bandwagonProfiles[j][k][l]) {
								similarityWarJoin_Avg[k]++;
							}
						}
					}
				}

		for (int i = 0; i < uniqueWorldSizes; i++) {
			similarityWarInit_Avg[i] = similarityWarInit_Avg[i] / m[i];
		}

		if (currentTestedSize > 2)
			for (int i = 0; i < uniqueWorldSizes - 1; i++) {
				similarityWarJoin_Avg[i] = similarityWarJoin_Avg[i] / n[i];
			}

	}

	static int print_indiv(char[] buffer, int buffercounter) {
		int a1 = 0, a2;
		if (buffer[buffercounter] < FSET_1_START) {
			switch (buffer[buffercounter]) {
			case CAPMED:
				System.out.print("CAP_MED");
				break;
			case CAPAVG:
				System.out.print("CAP_AVG");
				break;
			case CAPSTD:
				System.out.print("CAP_STD");
				break;
			case CAPMIN:
				System.out.print("CAP_MIN");
				break;
			case CAPMAX:
				System.out.print("CAP_MAX");
				break;
			case MYCAP:
				System.out.print("MY_CAP");
				break;
			case OPPCAP:
				System.out.print("OPP_CAP");
				break;
			case MYSIDECAP:
				System.out.print("MY_SIDE_CAP");
				break;
			case LEFTCAPSUM:
				System.out.print("LEFT_CAP_SUM");
				break;
			default:
				System.out.print(randNum[buffer[buffercounter]]);
				break;
			}
			return (++buffercounter);
		}

		switch (buffer[buffercounter]) {
		case ADD:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" + ");
			break;
		case SUB:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" - ");
			break;
		case MUL:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" * ");
			break;
		case DIV:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" / ");
			break;
		case GT:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" > ");
			break;
		case LT:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" < ");
			break;
		case EQ:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" = ");
			break;
		case AND:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" AND ");
			break;
		case OR:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" OR ");
			break;
		}
		a2 = print_indiv(buffer, a1);
		System.out.print(")");
		return (a2);
	}

}