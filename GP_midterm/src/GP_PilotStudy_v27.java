import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class GP_PilotStudy_v27 {
	public static final boolean PROFILEPOWER = false;
	static int MASTER_GENERATIONS = 10000, DEME_GENERATIONS = 1, POPSIZE_PER_PROCESSOR = 25, MINRANDOM = 0, MAXRANDOM = 1,
			PROFILES_PER_CAT = 1000, ENMITYLEVELS = 2, MINSYSTEM = 2, MAXSYSTEM = 2, MAX_LEN = 10000, DEPTH = 5,
			TSIZE = 2, simulations = 10;
	static final int RANDOMNUMBERS = 50, CAPMED = RANDOMNUMBERS, CAPAVG = CAPMED + 1, CAPSTD = CAPMED + 2,
			CAPMIN = CAPMED + 3, CAPMAX = CAPMED + 4, MYCAP = CAPMED + 5, OPPCAP = CAPMED + 6, MYSIDECAP = CAPMED + 7,
			LEFTCAPSUM = CAPMED + 8, MYENMITY = LEFTCAPSUM + 1, OPPENMITY = MYENMITY + 1, ADD = OPPENMITY + 1,
			SUB = ADD + 1, MUL = ADD + 2, DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7,
			OR = ADD + 8, TSET_1_START = CAPMED, TSET_1_END = LEFTCAPSUM, TSET_2_START = MYENMITY,
			TSET_2_END = OPPENMITY, FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT, FSET_2_END = EQ,
			FSET_3_START = AND, FSET_3_END = OR;
	public static final double RANDOMPLAYER_PROB = 0.20, STRATEGY_CHANGE_PROB = 0, ENHANCMENT_MARGIN = 0,
			PMUT_PER_NODE = 0.1, MOD_CROSSOVER_PROB = 0.1, REPLICATION_PROB = 0.1, CROSSOVER_PROB = 0.1,
			SUBTREE_MUT_PROB = 0.1, PMUT_PROB = 0.1, ABIOGENSIS_PROB = 0.5, GAUSS_PROB_FACTOR = 0.2;
	static final int CAPCHANGE = 1;
	static final boolean SIMULATECONSTRUCTIVISM = true;
	static int currentGen, uniqueWorldSizes, currentSimulation, currentTestedSize, currentDeme;
	static implementClass_v27[] implement;
	static Thread[] thread;
	static int processors, TOTAL_POPSIZE, TOTAL_TESTCASES;
	static double[] fitness, randNum;
	static char[][][] init_strategy, balance_strategy, bandwagon_strategy;
	static char[][][][] demes_init_strategy, demes_balance_strategy, demes_bandwagon_strategy;
	static char[][][][] FT_init_strategy, FT_balance_strategy, FT_bandwagon_strategy;
	static World_System_Shell_v27[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static Random rd = new Random();
	static boolean[][][] attackProfiles, balanceProfiles, bandwagonProfiles;
	static boolean sortedFit, shuffled;
	static char[][][][] temp_init_strategy, temp_balance_strategy, temp_bandwagon_strategy;
	static double[][] tempFitness;
	static int prevAttacks, prevbalances, prevbandwagons, prevSimilarityCount;
	static double avg_len;
	static double[] similarityWarInit_Avg, similarityWarbalance_Avg;
	static final boolean BLOATFIGHT = false, PRINTINDIV = false, PRINTPROFILE = false;
	static int previousBestHash_1, previousBestHash_2;
	static int[] length, world_sizes, TESTCASES = {500};
	static int[][] testedStrategyIndex;
	static int[][][] testStrategyIndexes, tempTestedStrategyIndexes, initialEnmity;
	static double[][] capabilities, probabilities, GaussProb, GaussProb2;
	static double[][][] capChangeRates;
	static int[][][] changeIndexes;
	static WriteFile writer, stateStats_writer, state_profiles_writer;
	static String outputString;

	public static void main(String[] args) {
		master_initalize();
		for (currentSimulation = 1; currentSimulation <= simulations; currentSimulation++) {
			Simulation_v27 simulation_v27 = new Simulation_v27();
		}
		
		System.out.println("\n\nEND OF PROGRAM !!!!!!!!");
	}

	private static void master_initalize() {
		System.out.println("START OF PROGRAM");
		if (MINSYSTEM != 2) {
			System.out.println("Error!!! MINSYSTEM must be 2");
			System.exit(0);
		}

		if (Math.abs(1 - (MOD_CROSSOVER_PROB + REPLICATION_PROB + CROSSOVER_PROB + SUBTREE_MUT_PROB + PMUT_PROB + ABIOGENSIS_PROB)) > 1E-10) {
			System.out.println("Error!!! Probabilities of evolutionary operators don't add up to 1");
			System.exit(0);
		}
		
		

		processors = Runtime.getRuntime().availableProcessors();
		TOTAL_POPSIZE = POPSIZE_PER_PROCESSOR * processors;
		System.out.println("\nThis program runs " + simulations + " simulations");
		System.out.println("Processing is divided over " + processors + " processors");
		System.out.println("Total population size is " + TOTAL_POPSIZE);

		randNum = new double[RANDOMNUMBERS];
		for (int i = 0; i < RANDOMNUMBERS; i++)
			randNum[i] = (MAXRANDOM - MINRANDOM) * rd.nextDouble() + MINRANDOM;

		uniqueWorldSizes = 1 + MAXSYSTEM - MINSYSTEM;

		if (uniqueWorldSizes != TESTCASES.length) {
			System.out.println("Error!!! Test cases array and mina dn max system don't match!!");
			System.exit(0);
		}
		
		TOTAL_TESTCASES = 0;

		for (int i = 0; i < TESTCASES.length; i++)
			TOTAL_TESTCASES += TESTCASES[i];

		length = new int[TOTAL_TESTCASES];
		testedStrategyIndex = new int[TOTAL_TESTCASES][CAPCHANGE + 1];
		capabilities = new double[TOTAL_TESTCASES][];
		probabilities = new double[TOTAL_TESTCASES][];
		GaussProb = new double[TOTAL_TESTCASES][];
		GaussProb2 = new double[TOTAL_TESTCASES][];
		changeIndexes = new int[TOTAL_TESTCASES][CAPCHANGE][];
		testStrategyIndexes = new int[TOTAL_TESTCASES][CAPCHANGE + 1][];
		capChangeRates = new double[TOTAL_TESTCASES][CAPCHANGE][];
		initialEnmity = new int[TOTAL_TESTCASES][][];

		world_sizes = new int[uniqueWorldSizes];

		if (PROFILES_PER_CAT % 2 != 0) {
			System.out.println("ERROR!!!! PROFILE CATEGORIES NEED TO BE EVEN");
			System.exit(0);
		}

		profilingWorlds_Attacks = new World_System_Shell_v27[uniqueWorldSizes][PROFILES_PER_CAT];
		profilingWorlds_Joins = new World_System_Shell_v27[uniqueWorldSizes - 1][PROFILES_PER_CAT];
		
		for (int i = 0; i < uniqueWorldSizes; i++) 
			for (int k = 0; k < PROFILES_PER_CAT; k++) 
				profilingWorlds_Attacks[i][k] = new World_System_Shell_v27(false, (i + MINSYSTEM));
		
		for (int i = 0; i < uniqueWorldSizes - 1; i++)
			for (int k = 0; k < PROFILES_PER_CAT; k++)
				profilingWorlds_Joins[i][k] = new World_System_Shell_v27(true, (i + MINSYSTEM + 1));

		/*for (int i = 0; i < uniqueWorldSizes; i++) {
			for (int k = PROFILES_PER_CAT / 2; k < PROFILES_PER_CAT; k++) {
				if (profilingWorlds_Attacks[i][k].oppEnmity > 2)
					profilingWorlds_Attacks[i][k].oppEnmity = rd.nextInt(ENMITYLEVELS - 1);
				if (profilingWorlds_Attacks[i][k].oppEnmity > 1)
					profilingWorlds_Attacks[i][k].oppEnmity = rd.nextInt(ENMITYLEVELS - 1);
				if (profilingWorlds_Attacks[i][k].oppEnmity > 0)
					profilingWorlds_Attacks[i][k].oppEnmity = rd.nextInt(ENMITYLEVELS - 1);
				profilingWorlds_Attacks[i][k].capabilities = (double[]) profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].capabilities.clone();
				profilingWorlds_Attacks[i][k].capAvg = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].capAvg;
				profilingWorlds_Attacks[i][k].capMax = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].capMax;
				profilingWorlds_Attacks[i][k].capMed = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].capMed;
				profilingWorlds_Attacks[i][k].capMin = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].capMin;
				profilingWorlds_Attacks[i][k].capRatio = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].capRatio;
				profilingWorlds_Attacks[i][k].capStd = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].capStd;
				profilingWorlds_Attacks[i][k].currentStatesNum = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].currentStatesNum;
				profilingWorlds_Attacks[i][k].largerSideCap = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].largerSideCap;
				profilingWorlds_Attacks[i][k].leftCapSum = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].leftCapSum;
				profilingWorlds_Attacks[i][k].myCap = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].myCap;
				profilingWorlds_Attacks[i][k].mySideCap = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].mySideCap;
				profilingWorlds_Attacks[i][k].oppCap = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].oppCap;
				profilingWorlds_Attacks[i][k].smallerSideCap = profilingWorlds_Attacks[i][ k - (PROFILES_PER_CAT / 2)].smallerSideCap;
			}
		}
		
		for (int i = 0; i < uniqueWorldSizes-1; i++) {
			for (int k = PROFILES_PER_CAT / 2; k < PROFILES_PER_CAT; k++) {
				profilingWorlds_Joins[i][k].capabilities = (double[]) profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].capabilities.clone();
				profilingWorlds_Joins[i][k].capAvg = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].capAvg;
				profilingWorlds_Joins[i][k].capMax = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].capMax;
				profilingWorlds_Joins[i][k].capMed = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].capMed;
				profilingWorlds_Joins[i][k].capMin = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].capMin;
				profilingWorlds_Joins[i][k].capRatio = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].capRatio;
				profilingWorlds_Joins[i][k].capStd = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].capStd;
				profilingWorlds_Joins[i][k].currentStatesNum = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].currentStatesNum;
				profilingWorlds_Joins[i][k].largerSideCap = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].largerSideCap;
				profilingWorlds_Joins[i][k].leftCapSum = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].leftCapSum;
				profilingWorlds_Joins[i][k].myCap = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].myCap;
				profilingWorlds_Joins[i][k].mySideCap = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].mySideCap;
				profilingWorlds_Joins[i][k].oppCap = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].oppCap;
				profilingWorlds_Joins[i][k].smallerSideCap = profilingWorlds_Joins[i][ k - (PROFILES_PER_CAT / 2)].smallerSideCap;
			}
		}*/

		WriteFile profiles_writer = null;
		try {
			profiles_writer = new WriteFile("profiles.txt", false);
			profiles_writer.writeToFile("profile_no" + "," + "simulate_Joiner" + "," + "world_size" + "," + "capMed"
					+ "," + "capAvg" + "," + "capStd" + "," + "capMin" + "," + "capMax" + "," + "myCap" + ","
					+ "oppCap-smallerSideCap" + "," + "capRatio" + "," + "mySideCap-largerSideCap" + "," + "leftCapSum" + "," + "myEnmity" + ","
					+ "oppEnmity" + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < uniqueWorldSizes; i++) {
			for (int k = 0; k < PROFILES_PER_CAT; k++) {
				profiles_writer.writeToFile(k + "," + "0" + "," + (i + MINSYSTEM) + ","
						+ profilingWorlds_Attacks[i][k].capMed + "," + profilingWorlds_Attacks[i][k].capAvg + ","
						+ profilingWorlds_Attacks[i][k].capStd + "," + profilingWorlds_Attacks[i][k].capMin + ","
						+ profilingWorlds_Attacks[i][k].capMax + "," + profilingWorlds_Attacks[i][k].myCap + ","
						+ profilingWorlds_Attacks[i][k].oppCap + "," + profilingWorlds_Attacks[i][k].capRatio + ","
						+ profilingWorlds_Attacks[i][k].mySideCap + "," + profilingWorlds_Attacks[i][k].leftCapSum
						+ "," + profilingWorlds_Attacks[i][k].myEnmity + "," + profilingWorlds_Attacks[i][k].oppEnmity
						+ "\n");
			}
			if (i > 0)
				for (int k = 0; k < PROFILES_PER_CAT; k++) {
					profiles_writer.writeToFile(k + "," + "1" + "," + (i + MINSYSTEM) + ","
							+ profilingWorlds_Joins[i - 1][k].capMed + "," + profilingWorlds_Joins[i - 1][k].capAvg
							+ "," + profilingWorlds_Joins[i - 1][k].capStd + ","
							+ profilingWorlds_Joins[i - 1][k].capMin + "," + profilingWorlds_Joins[i - 1][k].capMax
							+ "," + profilingWorlds_Joins[i - 1][k].myCap + ","
							+ profilingWorlds_Joins[i - 1][k].smallerSideCap + "," + profilingWorlds_Joins[i - 1][k].capRatio
							+ "," + profilingWorlds_Joins[i - 1][k].largerSideCap + ","
							+ profilingWorlds_Joins[i - 1][k].leftCapSum + ","
							+ profilingWorlds_Joins[i - 1][k].myEnmity + ","
							+ profilingWorlds_Joins[i - 1][k].oppEnmity + "\n");
				}
		}

		try {
			state_profiles_writer = new WriteFile("state_profiles.txt", false);
			outputString = ("simulation" + "," + "stateNum" + "," + "generation" + "," + "world_size" + "," + "total_inits"
					+ "," + "total_balances" + "," + "total_bandwagons");
			
			for (int k = 1; k <= MAXSYSTEM; k++) {
				outputString += ("," + "similar_init_state_" + k);
			}
			
			for (int k = 1; k <= MAXSYSTEM; k++) {
				outputString += ("," + "similar_balance_state_" + k);
			}
			
			for (int k = 1; k <= MAXSYSTEM; k++) {
				outputString += ("," + "similar_bandwagon_state_" + k);
			}
			
			outputString +=( "," + "init_myCap_min" + "," + "init_myCap_med"
					+ "," + "init_oppCap_min" + "," + "init_oppCap_med" + "," + "init_capRatio_min" + ","
					+ "init_capRatio_med" + "," + "init_capMed_min" + "," + "init_capMed_med" + "," + "init_capStd_min"
					+ "," + "init_capStd_med" + "," + "init_capMin_min" + "," + "init_capMin_med" + "," + "init_capMax_min"
					+ "," + "init_capMax_med" + "," + "init_mySideCap_min" + "," + "init_mySideCap_med" + ","
					+ "init_leftCapSum_min" + "," + "init_leftCapSum_med" + "," + "init_myEnmity_min" + ","
					+ "init_myEnmity_med" + "," + "init_oppEnmity_min" + "," + "init_oppEnmity_med" + ","
					+ "balance_myCap_min" + "," + "balance_myCap_med" + "," + "balance_oppCap_min" + ","
					+ "balance_oppCap_med" + "," + "balance_capRatio_min" + "," + "balance_capRatio_med" + ","
					+ "balance_capMed_min" + "," + "balance_capMed_med" + "," + "balance_capStd_min" + ","
					+ "balance_capStd_med" + "," + "balance_capMin_min" + "," + "balance_capMin_med" + ","
					+ "balance_capMax_min" + "," + "balance_capMax_med" + "," + "balance_mySideCap_min" + ","
					+ "balance_mySideCap_med" + "," + "balance_leftCapSum_min" + "," + "balance_leftCapSum_med" + ","
					+ "balance_myEnmity_min" + "," + "balance_myEnmity_med" + "," + "balance_oppEnmity_min" + ","
					+ "balance_oppEnmity_med" + "," + "bandwagon_myCap_min" + "," + "bandwagon_myCap_med" + ","
					+ "bandwagon_oppCap_min" + "," + "bandwagon_oppCap_med" + "," + "bandwagon_capRatio_min" + ","
					+ "bandwagon_capRatio_med" + "," + "bandwagon_capMed_min" + "," + "bandwagon_capMed_med" + ","
					+ "bandwagon_capStd_min" + "," + "bandwagon_capStd_med" + "," + "bandwagon_capMin_min" + ","
					+ "bandwagon_capMin_med" + "," + "bandwagon_capMax_min" + "," + "bandwagon_capMax_med" + ","
					+ "bandwagon_mySideCap_min" + "," + "bandwagon_mySideCap_med" + "," + "bandwagon_leftCapSum_min" + ","
					+ "bandwagon_leftCapSum_med" + "," + "bandwagon_myEnmity_min" + "," + "bandwagon_myEnmity_med" + ","
					+ "bandwagon_oppEnmity_min" + "," + "bandwagon_oppEnmity_med");

			
			state_profiles_writer.writeToFile(outputString + "\n"); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		

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
					for (int k = 0; k < length[test] - 1; k++){
						initialEnmity[test][i][k] = rd.nextInt(ENMITYLEVELS-1);
					}
					initialEnmity[test][i][i] = -99;
				}

				double remainder = 1;
				capabilities[test] = new double[length[test]];
				for (int i = 0; i < length[test] - 1; i++) {
					capabilities[test][i] = remainder * rd.nextDouble();
					remainder -= capabilities[test][i];
				}
				
				capabilities[test][length[test]-1]=remainder;

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
					if (i==testedStrategyIndex[test][0])
						continue;
					int random = rd.nextInt(length[test] - 1);
					while (taken[random] < 0)
						random = rd.nextInt(length[test] - 1);
					testStrategyIndexes[test][0][i] = random;
					taken[random] = -99;

				}

				for (int k = 1; k <= CAPCHANGE; k++) {
					testStrategyIndexes[test][k] = new int[length[test]];
					changeIndexes[test][k-1] = new int[length[test]];
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
				for (int i = 0; i < 10 *  CAPCHANGE * MAXSYSTEM; i++)
					GaussProb2[test][i] = Math.abs(rd.nextGaussian());
			}
		}

		System.out.println("Among " + TOTAL_TESTCASES + " test cases there are");
		for (int i = 0; i < uniqueWorldSizes; i++) {
			System.out.println(world_sizes[i] + " test cases of world size " + (i + MINSYSTEM));
		}

	}
	
}
