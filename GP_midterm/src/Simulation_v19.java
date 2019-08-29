import java.util.Arrays;

public class Simulation_v19 extends GP_PilotStudy_v19 {

	public Simulation_v19() {
		simulation_initalize();
		for (currentGen = 1; currentGen <= MASTER_GENERATIONS; currentGen++) {
			System.out.println("Simulation=" + currentSimulation + "  Generation=" + currentGen);
			System.out.print ("Current evolving world size is " + currentTestedSize +  " ||| Working on Deme ");
			for (currentDeme = 0; currentDeme < DEMES; currentDeme++) {
				System.out.print(currentDeme + 1 + " ");
				init_strategy = demes_init_strategy[currentDeme];
				join_strategy = demes_join_strategy[currentDeme];
				createTempArrays();
				calculateFitness();
				for (int demeGen = 0; demeGen < DEME_GENERATIONS; demeGen++) {
					evolve();
					for (int i = 0; i < uniqueWorldSizes - 1; i++) {
						temp_init_strategy[0][0][i] = (char[]) FT_init_strategy[currentDeme][i].clone();
						temp_join_strategy[0][0][i] = (char[]) FT_join_strategy[currentDeme][i].clone();
					}
					temp_init_strategy[0][0][uniqueWorldSizes - 1] = (char[]) FT_init_strategy[currentDeme][uniqueWorldSizes - 1]
							.clone();
					calculateFitness();
				}
				createMasterArrays();
				double prevBestFitness = fitness[0];
				sortDesc();
				if (prevBestFitness + prevBestFitness * ENHANCMENT_MARGIN < fitness[0]) {
					for (int i = 0; i < uniqueWorldSizes - 1; i++) {
						FT_init_strategy[currentDeme][i] = (char[]) init_strategy[0][i].clone();
						FT_join_strategy[currentDeme][i] = (char[]) join_strategy[0][i].clone();
					}
					FT_init_strategy[currentDeme][uniqueWorldSizes - 1] = (char[]) init_strategy[0][uniqueWorldSizes - 1].clone();
				} else {
					for (int i = 0; i < uniqueWorldSizes - 1; i++) {
						if (FT_init_strategy[currentDeme][i] != null) {
							init_strategy[0][i] = (char[]) FT_init_strategy[currentDeme][i].clone();
						}
						if (FT_join_strategy[currentDeme][i] != null) {
							join_strategy[0][i] = (char[]) FT_join_strategy[currentDeme][i].clone();
						}
					}
					if (FT_init_strategy[currentDeme][uniqueWorldSizes - 1] != null) {
						init_strategy[0][uniqueWorldSizes - 1] = (char[]) FT_init_strategy[currentDeme][uniqueWorldSizes - 1]
								.clone();
					}
				}
				demes_init_strategy[currentDeme] = init_strategy;
				demes_join_strategy[currentDeme] = join_strategy;
			}
			System.out.println();
			printStats(FT_init_strategy, FT_join_strategy);
			if (prevSimilarityCount == stabilityNum) {
				System.out.println("System reached stability at level " + currentTestedSize);
				for (int currentDeme = 0; currentDeme < DEMES; currentDeme++) {
					for (int i = 0; i < TOTAL_POPSIZE; i++) {
						demes_init_strategy[currentDeme][i][currentTestedSize - MINSYSTEM] = (char[]) FT_init_strategy[currentDeme][currentTestedSize
								- MINSYSTEM].clone();
						if (currentTestedSize > 2)
							demes_join_strategy[currentDeme][i][currentTestedSize - MINSYSTEM - 1] = (char[]) FT_join_strategy[currentDeme][currentTestedSize
									- MINSYSTEM - 1].clone();
					}
				}

				currentTestedSize++;
				prevSimilarityCount = 0;
				if (currentTestedSize <= MAXSYSTEM) {
					System.out.println("Moving on to test worlds of size  " + currentTestedSize);
				} else {
					System.out.println("System reached overall stability !!!!!!!!!!!!!!!!!!");
					break;
				}

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

		demes_init_strategy = new char[DEMES][TOTAL_POPSIZE][uniqueWorldSizes][];
		demes_join_strategy = new char[DEMES][TOTAL_POPSIZE][uniqueWorldSizes - 1][];

		for (int currentDeme = 0; currentDeme < DEMES; currentDeme++) {
			init_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes][];
			join_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes - 1][];
			
			temp_init_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes][];
			temp_join_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes - 1][];
			
			tempFitness = new double[processors][POPSIZE_PER_PROCESSOR];

			initiateThreads(1);
			waitForThreadsFinish();
			createMasterArrays();
			demes_init_strategy[currentDeme] = init_strategy;
			demes_join_strategy[currentDeme] = join_strategy;
		}
		
		init_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes][];
		join_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes - 1][];
		
		temp_init_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes][];
		temp_join_strategy = new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes - 1][];
		
		tempFitness = new double[processors][POPSIZE_PER_PROCESSOR];

		FT_init_strategy = new char[DEMES + 1][uniqueWorldSizes][];
		FT_join_strategy = new char[DEMES + 1][uniqueWorldSizes - 1][];

		for (int currentDeme = 0; currentDeme < DEMES; currentDeme++) {
			for (int i = 0; i < uniqueWorldSizes - 1; i++) {
				FT_init_strategy[currentDeme][i] = (char[]) demes_init_strategy[currentDeme][rd.nextInt(TOTAL_POPSIZE)][i].clone();
				FT_join_strategy[currentDeme][i] = (char[]) demes_join_strategy[currentDeme][rd.nextInt(TOTAL_POPSIZE)][i].clone();
			}
			FT_init_strategy[currentDeme][uniqueWorldSizes - 1] = (char[]) demes_init_strategy[currentDeme][rd.nextInt(TOTAL_POPSIZE)][uniqueWorldSizes - 1]
					.clone();
			/*if(randNum[0]>randNum[1])
			FT_init_strategy[currentDeme][uniqueWorldSizes - 1] = new char[] {GT, 0, 1};
			else
				FT_init_strategy[currentDeme][uniqueWorldSizes - 1] = new char[] {GT, 1, 0};*/
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

		if (!sortedFit) {
			System.out.println("Not Sorted!!!");
			System.exit(0);
		}

		int length = init_strategy.length;
		int validStrategies = 0;
		for (int i = 0; i < length; i++) {
			if (init_strategy[i][0] != null) {
				validStrategies++;
			}
		}

		char[][][] temp_init_strategy = new char[validStrategies][uniqueWorldSizes][];
		char[][][] temp_join_strategy = new char[validStrategies][uniqueWorldSizes - 1][];

		validStrategies = 0;
		for (int i = 0; i < length; i++) {
			if (init_strategy[i][0] != null) {
				for (int k = 0; k < uniqueWorldSizes - 1; k++) {
					temp_init_strategy[validStrategies][k] = init_strategy[i][k];
					temp_join_strategy[validStrategies][k] = join_strategy[i][k];
				}
				temp_init_strategy[validStrategies++][uniqueWorldSizes - 1] = init_strategy[i][uniqueWorldSizes - 1];
			}
		}

		init_strategy = temp_init_strategy;
		join_strategy = temp_join_strategy;
		length = init_strategy.length;

		int node_count_1 = 0;
		int node_count_2 = 0;
		for (int i = 0; i < length; i++) {
			for (int k = 0; k < currentTestedSize - MINSYSTEM + 1; k++) {
				node_count_1 += traverse(init_strategy[i][k], 0);
			}
			for (int k = 0; k < currentTestedSize - MINSYSTEM; k++) {
				node_count_2 += traverse(join_strategy[i][k], 0);
			}
		}
		double avg_len = (double) (node_count_1 + node_count_2) / length;

		calcSimilarity(init_strategy, join_strategy, length);


		System.out.println("Among the top " + length + " strategies" + " Avg Size=" + avg_len
				+ "\nwar init similarity of ");
		int counter = MINSYSTEM;
		for (int i = 0; i < uniqueWorldSizes; i++) {
			System.out.println(counter + " world is " + similarityWarInit_Avg[i]);
			counter++;
		}
		System.out.println("And war joining similarity of ");
		counter = MINSYSTEM + 1;
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			System.out.println(counter + " world is " + similarityWarJoin_Avg[i]);
			counter++;
		}

		int[] attacks = new int[uniqueWorldSizes];
		int[][] indiv_attacks = new int[length][uniqueWorldSizes];
		int[] joins = null;
		int[][] indiv_joins = new int[length][uniqueWorldSizes - 1];
		int[] joins_balance = new int[uniqueWorldSizes - 1];
		int[][] indiv_balances = new int[length][uniqueWorldSizes - 1];
		int[] joins_bandwagon = new int[uniqueWorldSizes - 1];
		int[][] indiv_bandwagons = new int[length][uniqueWorldSizes - 1];
		int totalAttacks = 0, totalJoins = 0;

		for (int i = 0; i < length; i++) {
			for (int k = 0; k < uniqueWorldSizes; k++) {
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (attackProfiles[i][k][l] == true) {
						totalAttacks++;
						attacks[k]++;
						indiv_attacks[i][k]++;
					}
				}
			}
		}

		if (MAXSYSTEM > 2){
			joins = new int[uniqueWorldSizes - 1];
			for (int i = 0; i < length; i++) {
				for (int k = 0; k < uniqueWorldSizes - 1; k++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (joinProfiles[i][k][l] == true) {
							totalJoins++;
							joins[k]++;
							indiv_joins[i][k]++;
							if (profilingWorlds_Joins[k][l].mySideCap < profilingWorlds_Joins[k][l].oppCap) {
								joins_balance[k]++;
								indiv_balances[i][k]++;
							} else {
								joins_bandwagon[k]++;
								indiv_bandwagons[i][k]++;
							}
						}
					}
				}
			}
		}

		double[][] attacksCapabilities = new double[uniqueWorldSizes][];
		double[][] attacksCapabilitiesRatios = new double[uniqueWorldSizes][];
		for (int i = 0; i < uniqueWorldSizes; i++) {
			attacksCapabilities[i] = new double[attacks[i]];
			attacksCapabilitiesRatios[i] = new double[attacks[i]];
			for (int m = 0; m < attacks[i]; m++) {
				for (int j = 0; j < length; j++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (attackProfiles[j][i][l] == true) {
							attacksCapabilities[i][m] = profilingWorlds_Attacks[i][l].myCap;
							attacksCapabilitiesRatios[i][m++] = profilingWorlds_Attacks[i][l].myCap
									/ profilingWorlds_Attacks[i][l].oppCap;
						}
					}
				}
			}
		}

		double[][] joinCapabilities = new double[uniqueWorldSizes - 1][];
		double[][] joinCapabilitiesRatios = new double[uniqueWorldSizes - 1][];
		if (MAXSYSTEM > 2) {
			for (int i = 0; i < uniqueWorldSizes - 1; i++) {
				joinCapabilities[i] = new double[joins[i]];
				joinCapabilitiesRatios[i] = new double[joins[i]];
				for (int m = 0; m < joins[i]; m++) {
					for (int j = 0; j < length; j++) {
						for (int l = 0; l < PROFILES_PER_CAT; l++) {
							if (joinProfiles[j][i][l] == true) {
								joinCapabilities[i][m] = profilingWorlds_Joins[i][l].myCap;
								joinCapabilitiesRatios[i][m++] = profilingWorlds_Joins[i][l].myCap
										/ profilingWorlds_Joins[i][l].oppCap;
							}
						}
					}
				}
			}
		}

		for (int i = 0; i < uniqueWorldSizes; i++) {
			Arrays.sort(attacksCapabilities[i]);
			Arrays.sort(attacksCapabilitiesRatios[i]);
		}
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			Arrays.sort(joinCapabilities[i]);
			Arrays.sort(joinCapabilitiesRatios[i]);
		}

		System.out.println("\navg percentage of profile attacks is: " + (double) totalAttacks * 100
				/ (length * uniqueWorldSizes * PROFILES_PER_CAT) + "%");
		System.out.println("avg percentage of profile joins is: " + (double) totalJoins * 100
				/ (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + "%");
		System.out.println("\npercentage of profile attacks in a ");
		for (int i = 0; i < uniqueWorldSizes; i++) {
			System.out.println((i + MINSYSTEM) + " world is " + (double) attacks[i] * 100 / (length * PROFILES_PER_CAT)
					+ "%");
		}

		System.out.println("\nMedian cap of attackers in a");
		for (int i = 0; i < uniqueWorldSizes; i++) {
			if (attacks[i] > 0)
				System.out.println((i + MINSYSTEM) + " world is " + attacksCapabilities[i][attacks[i] / 2]);
		}

		System.out.println("\nMin and 1st quart cap_ratio of attacks in a ");
		for (int i = 0; i < uniqueWorldSizes; i++) {
			if (attacks[i] > 0)
				System.out.println((i + MINSYSTEM) + " world is " + attacksCapabilitiesRatios[i][0] + " & "
						+ attacksCapabilitiesRatios[i][attacks[i] / 4]);
		}

		System.out.println("\nMin and 1st quart cap of attackers in a");
		for (int i = 0; i < uniqueWorldSizes; i++) {
			if (attacks[i] > 0)
				System.out.println((i + MINSYSTEM) + " world are " + attacksCapabilities[i][0] + " & "
						+ attacksCapabilities[i][attacks[i] / 4]);
		}

		System.out.println("\npercentage of profile joins in a ");
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			System.out.println((i + MINSYSTEM + 1) + " world is " + (double) joins[i] * 100
					/ (length * PROFILES_PER_CAT) + "%");
		}

		System.out.println("\npercentage of balancing joins in a ");
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			System.out.println((i + MINSYSTEM + 1) + " world is " + (double) joins_balance[i] * 100
					/ (length * PROFILES_PER_CAT) + "%");
		}
		System.out.println("percentage of bandwagoning joins in a ");
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			System.out.println((i + MINSYSTEM + 1) + " world is " + (double) joins_bandwagon[i] * 100
					/ (length * PROFILES_PER_CAT) + "%");
		}

		System.out.println("\nMedian cap of joiners in a");
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			if (joins[i] > 0)
				System.out.println((i + MINSYSTEM + 1) + " world is " + joinCapabilities[i][joins[i] / 2]);
		}

		System.out.println("\nMin and 1st quart cap of joiners in a");
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			if (joins[i] > 0)
				System.out.println((i + MINSYSTEM + 1) + " world are " + joinCapabilities[i][0] + " & "
						+ joinCapabilities[i][joins[i] / 4]);
		}

		if (PRINTINDIV) {
			System.out.println("\nBest Individual: ");
			System.out.print("Attack strategy: ");
			for (int k = 0; k < uniqueWorldSizes; k++) {
				System.out.println();
				print_indiv(init_strategy[k][0], 0);
			}
			System.out.print("\nJoining strategy: ");
			for (int k = 0; k < join_strategy.length; k++) {
				print_indiv(join_strategy[k][0], 0);
				System.out.println();
			}
			System.out.println();
		}

		/*
		 * int bestAttacks = -1; int bestJoins = -1;
		 * 
		 * if (PRINTPROFILE) { bestAttacks = 0; bestJoins = 0;
		 * System.out.println("It's attacking behavioral profile is:"); for (int
		 * i = 0; i < PROFILES/2; i++) if (profile[0][i] == true) {
		 * System.out.print("1"); bestAttacks++; } else System.out.print("0");
		 * System.out.println();
		 * System.out.println("It's joining behavioral profile is:"); for (int i
		 * = PROFILES / 2; i < PROFILES; i++) if (profile[0][i] == true) {
		 * System.out.print("1"); bestJoins++; } else System.out.print("0");
		 * System.out.println(); }
		 * 
		 * if (bestAttacks == -1 && bestJoins == -1) { bestAttacks = 0; for (int
		 * i = 0; i < PROFILES / 2; i++) if (profile[0][i] == true) {
		 * bestAttacks++; } bestJoins = 0; for (int i = PROFILES / 2; i <
		 * PROFILES; i++) if (profile[0][i] == true) { bestJoins++; } }
		 * 
		 * 
		 * 
		 * System.out.println("\nNumber of profile attacks by best is " +
		 * bestAttacks);
		 * System.out.println("Number of profile attacks by prev best is " +
		 * prevBestAttacks); prevBestAttacks = bestAttacks;
		 * System.out.println("Number of profile joins by best is " +
		 * bestJoins);
		 * System.out.println("Number of profile joins by prev best is " +
		 * prevBestJoins); prevBestJoins = bestJoins;
		 * System.out.print("Profile differences from previous best is "); int
		 * differences = 0; for (int i = 0; i < PROFILES; i++) if (profile[0][i]
		 * != profilePrevBest[i]) differences++; profilePrevBest =
		 * Arrays.copyOf(profile[0], PROFILES); System.out.print(differences);
		 */

		System.out.print("\n");
		System.out.flush();

		if (currentTestedSize == 2) {
			if (attacks[currentTestedSize - MINSYSTEM] == prevAttacks) {
				prevSimilarityCount++;
			} else {
				prevAttacks = attacks[currentTestedSize - MINSYSTEM];
				prevSimilarityCount = 0;
			}
		} else {
			if (attacks[currentTestedSize - MINSYSTEM] == prevAttacks
					&& joins[currentTestedSize - MINSYSTEM - 1] == prevJoins)
				prevSimilarityCount++;
			else {
				prevAttacks = attacks[currentTestedSize - MINSYSTEM];
				prevJoins = joins[currentTestedSize - MINSYSTEM - 1];
				prevSimilarityCount = 0;
			}
		}

		System.out.println("\nNumber of consecutively similar generations is " + prevSimilarityCount);

		if (attacks[0] > 0) {
			writer.writeToFile(currentSimulation + "," + currentGen + ","
					+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
					+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
					+ (MINSYSTEM) + "," + similarityWarInit_Avg[0] + ","
					+ (double) attacks[0] * 100 / (length * PROFILES_PER_CAT) + ","
					+ attacksCapabilities[0][attacks[0] / 2] + "," + attacksCapabilities[0][0] + ","
					+ attacksCapabilities[0][attacks[0] / 4] + "," + attacksCapabilitiesRatios[0][0] + ","
					+ attacksCapabilitiesRatios[0][attacks[0] / 4] + "," + "." + "," + "." + "," + "." + "," + "."
					+ "," + "." + "," + "." + "," + "." + "\n");
		} else {
			writer.writeToFile(currentSimulation + "," + currentGen + ","
					+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
					+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
					+ (MINSYSTEM) + "," + similarityWarInit_Avg[0] + ","
					+ (double) attacks[0] * 100 / (length * PROFILES_PER_CAT) + "," + "." + "," + "." + "," + "." + ","
					+ "." + "," + "." + "," + "." + "," + "." + "," + "." + "," + "." + "," + "." + "," + "." + ","
					+ "." + "\n");
		}

		for (int i = 1; i < uniqueWorldSizes; i++) {
			if (attacks[i] > 0 && joins[i - 1] > 0) {
				writer.writeToFile(currentSimulation + "," + currentGen + ","
						+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
						+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
						+ (i + MINSYSTEM) + "," + similarityWarInit_Avg[i] + ","
						+ (double) attacks[i] * 100 / (length * PROFILES_PER_CAT) + ","
						+ attacksCapabilities[i][attacks[i] / 2] + "," + attacksCapabilities[i][0] + ","
						+ attacksCapabilities[i][attacks[i] / 4] + "," + attacksCapabilitiesRatios[i][0] + ","
						+ attacksCapabilitiesRatios[i][attacks[i] / 4] + "," + similarityWarJoin_Avg[i - 1] + ","
						+ (double) joins[i - 1] * 100 / (length * PROFILES_PER_CAT) + ","
						+ (double) joins_balance[i - 1] * 100 / (length * PROFILES_PER_CAT) + ","
						+ (double) joins_bandwagon[i - 1] * 100 / (length * PROFILES_PER_CAT) + ","
						+ joinCapabilities[i - 1][joins[i - 1] / 2] + "," + joinCapabilities[i - 1][0] + ","
						+ joinCapabilities[i - 1][joins[i - 1] / 4] + "\n");
			} else if (attacks[i] > 0) {
				writer.writeToFile(currentSimulation + "," + currentGen + ","
						+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
						+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
						+ (MINSYSTEM) + "," + similarityWarInit_Avg[i] + ","
						+ (double) attacks[i] * 100 / (length * PROFILES_PER_CAT) + ","
						+ attacksCapabilities[i][attacks[i] / 2] + "," + attacksCapabilities[i][0] + ","
						+ attacksCapabilities[i][attacks[i] / 4] + "," + attacksCapabilitiesRatios[i][0] + ","
						+ attacksCapabilitiesRatios[i][attacks[i] / 4] + "," + similarityWarJoin_Avg[i - 1] + ","
						+ (double) joins[i - 1] * 100 / (length * PROFILES_PER_CAT) + ","
						+ (double) joins_balance[i - 1] * 100 / (length * PROFILES_PER_CAT) + ","
						+ (double) joins_bandwagon[i - 1] * 100 / (length * PROFILES_PER_CAT) + "," + "." + "," + "."
						+ "," + "." + "\n");
			} else if (joins[i - 1] > 0) {
				writer.writeToFile(currentSimulation + "," + currentGen + ","
						+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
						+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
						+ (i + MINSYSTEM) + "," + similarityWarInit_Avg[i] + ","
						+ (double) attacks[i] * 100 / (length * PROFILES_PER_CAT) + "," + "." + "," + "." + "," + "."
						+ "," + "." + "," + "." + "," + similarityWarJoin_Avg[i - 1] + "," + (double) joins[i - 1]
						* 100 / (length * PROFILES_PER_CAT) + "," + (double) joins_balance[i - 1] * 100
						/ (length * PROFILES_PER_CAT) + "," + (double) joins_bandwagon[i - 1] * 100
						/ (length * PROFILES_PER_CAT) + "," + joinCapabilities[i - 1][joins[i - 1] / 2] + ","
						+ joinCapabilities[i - 1][0] + "," + joinCapabilities[i - 1][joins[i - 1] / 4] + "\n");
			} else {
				writer.writeToFile(currentSimulation + "," + currentGen + ","
						+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
						+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
						+ (i + MINSYSTEM) + "," + similarityWarInit_Avg[i] + ","
						+ (double) attacks[i] * 100 / (length * PROFILES_PER_CAT) + "," + "." + "," + "." + "," + "."
						+ "," + "." + "," + "." + "," + similarityWarJoin_Avg[i - 1] + "," + (double) joins[i - 1]
						* 100 / (length * PROFILES_PER_CAT) + "," + (double) joins_balance[i - 1] * 100
						/ (length * PROFILES_PER_CAT) + "," + (double) joins_bandwagon[i - 1] * 100
						/ (length * PROFILES_PER_CAT) + "," + "." + "," + "." + "," + "." + "\n");
			}
		}

		/*
		 * stateStats.writeToFile("simulation" + "," + "stateNum" + "," + "generation" + "," + "world_size" + ","
					+ "indiv_attcks" + "," + "indiv_balances" + "," + "indiv_bandwagones" + "\n");
		 */

		
		if (currentTestedSize == 2) {
			for (int i = 0; i < length; i++) {
				stateStats_writer.writeToFile(currentSimulation + "," + (i + 1) + "," + currentGen + "," + currentTestedSize
						+ "," + indiv_attacks[i][currentTestedSize - MINSYSTEM] + ","
						+ "." + ","
						+ "." + "\n");
			}
		} else {
			for (int i = 0; i < length; i++) {
				stateStats_writer.writeToFile(currentSimulation + "," + (i + 1) + "," + currentGen + "," + currentTestedSize
						+ "," + indiv_attacks[i][currentTestedSize - MINSYSTEM] + ","
						+ indiv_balances[i][currentTestedSize - MINSYSTEM - 1] + ","
						+ indiv_bandwagons[i][currentTestedSize - MINSYSTEM - 1] + "\n");
			}
		}

		for (int i = 0; i < length; i++) {
			outputString = (currentSimulation + "," + (i + 1) + "," + currentGen + "," + currentTestedSize);
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				outputString += ("," + (profilingWorlds_Attacks[currentTestedSize - MINSYSTEM][l]
						.willItAttack(init_strategy[i][currentTestedSize - MINSYSTEM]) == true ? 1 : 0));

			}

			if (currentTestedSize > 2)
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					outputString += ("," + (profilingWorlds_Joins[currentTestedSize - MINSYSTEM - 1][l]
							.willItAttack(join_strategy[i][currentTestedSize - MINSYSTEM - 1]) == true ? 1 : 0));

				}
			state_profiles_writer.writeToFile(outputString + "\n");
		}

		/*if (joins == null) {
			for (int i = 0; i < length; i++) {
				stateStats.writeToFile(currentSimulation + "," + (i + 1) + "," + currentGen + ","
						+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
						+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
						+ totalAttacks + "," + totalJoins + "," + attacks[0] + "," + "." + "," + (MINSYSTEM) + ","
						+ indiv_attacks[i][0] + "," + "." + "," + "." + "," + "." + "\n");
			}
		} else
			for (int i = 0; i < length; i++) {
				stateStats.writeToFile(currentSimulation + "," + (i + 1) + "," + currentGen + ","
						+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
						+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
						+ totalAttacks + "," + totalJoins + "," + attacks[0] + "," + joins[0] + "," + (MINSYSTEM) + ","
						+ indiv_attacks[i][0] + "," + "." + "," + "." + "," + "." + "\n");
			}
		if (MAXSYSTEM > 2)
			for (int i = 0; i < length; i++) {
				for (int k = 1; k < uniqueWorldSizes; k++) {
					stateStats.writeToFile(currentSimulation + "," + (i + 1) + "," + currentGen + ","
							+ (double) totalAttacks * 100 / (length * uniqueWorldSizes * PROFILES_PER_CAT) + ","
							+ (double) totalJoins * 100 / (length * (uniqueWorldSizes - 1) * PROFILES_PER_CAT) + ","
							+ totalAttacks + "," + totalJoins + "," + attacks[k] + "," + joins[k - 1] + ","
							+ (k + MINSYSTEM) + "," + indiv_attacks[i][k] + "," + indiv_joins[i][k - 1] + ","
							+ indiv_balances[i][k - 1] + "," + indiv_bandwagons[i][k - 1] + "\n");
				}
			}
			*/

		System.out.println("*******************************************************************");
		System.out.println();

	}

	private static void sortDesc() {
		// sort fitness and strategy arrays in fitness descending order

		char[][][] tempinit_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes][];
		char[][][] tempjoin_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes - 1][];
		
		for (int i = 0; i < TOTAL_POPSIZE; i++) {
			tempinit_strategy[i][0] = (char[]) init_strategy[i][0].clone();
		}
	
		for (int k = 1; k < uniqueWorldSizes; k++) {
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
		implement = new implementClass_v19[processors];

		for (int threadID = 0; threadID < processors; threadID++) {
			implement[threadID] = new implementClass_v19(threadID, randNum, POPSIZE_PER_PROCESSOR,
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

	private static void calcSimilarity(char[][][] init_strategy, char[][][] join_strategy, int indivs) {
		similarityWarInit_Avg = new double[uniqueWorldSizes];
		similarityWarJoin_Avg = new double[uniqueWorldSizes - 1];

		attackProfiles = new boolean[indivs][uniqueWorldSizes][PROFILES_PER_CAT];
		joinProfiles = new boolean[indivs][uniqueWorldSizes][PROFILES_PER_CAT];

		for (int i = 0; i < indivs; i++) {
			for (int k = 0; k < currentTestedSize - MINSYSTEM + 1; k++) {
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					attackProfiles[i][k][l] = profilingWorlds_Attacks[k][l].willItAttack(init_strategy[i][k]);
				}
			}
		}

		if (currentTestedSize > 2)
			for (int i = 0; i < indivs; i++) {
				for (int k = 0; k < currentTestedSize - MINSYSTEM; k++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						joinProfiles[i][k][l] = profilingWorlds_Joins[k][l].willItAttack(join_strategy[i][k]);
					}
				}
			}

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
							if (joinProfiles[i][k][l] == joinProfiles[j][k][l]) {
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