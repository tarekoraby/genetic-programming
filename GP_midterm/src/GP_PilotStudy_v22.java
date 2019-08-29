import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class GP_PilotStudy_v22 {
	public static final boolean PROFILEPOWER = false;
	static int MASTER_GENERATIONS = 100, DEME_GENERATIONS = 1, POPSIZE_PER_PROCESSOR = 200, MINRANDOM = 0, MAXRANDOM = 1,
			PROFILES_PER_CAT = 2000, ENMITYLEVELS = 2, MINSYSTEM = 2, MAXSYSTEM = 3, MAX_LEN = 10000, DEPTH = 5,
			TSIZE = 2, simulations = 10;
	static final int RANDOMNUMBERS = 50, CAPMED = RANDOMNUMBERS, CAPAVG = CAPMED + 1, CAPSTD = CAPMED + 2,
			CAPMIN = CAPMED + 3, CAPMAX = CAPMED + 4, MYCAP = CAPMED + 5, OPPCAP = CAPMED + 6, MYSIDECAP = CAPMED + 7,
			LEFTCAPSUM = CAPMED + 8, MYENMITY = LEFTCAPSUM + 1, OPPENMITY = MYENMITY + 1, ADD = OPPENMITY + 1,
			SUB = ADD + 1, MUL = ADD + 2, DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7,
			OR = ADD + 8, TSET_1_START = CAPMED, TSET_1_END = LEFTCAPSUM, TSET_2_START = MYENMITY,
			TSET_2_END = OPPENMITY, FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT, FSET_2_END = EQ,
			FSET_3_START = AND, FSET_3_END = OR;
	public static final double RANDOMPLAYER_PROB = 0.05, STRATEGY_CHANGE_PROB = 0, ENHANCMENT_MARGIN = 0,
			PMUT_PER_NODE = 0.1, MOD_CROSSOVER_PROB = 0.2, REPLICATION_PROB = 0.2, CROSSOVER_PROB = 0.1,
			SUBTREE_MUT_PROB = 0.1, PMUT_PROB = 0.1, ABIOGENSIS_PROB = 0.3, GAUSS_PROB_FACTOR = 0;
	static final int CAPCHANGE = 1;
	static final boolean SIMULATECONSTRUCTIVISM = false, PRINT_EXTENDED_PROFILES=false;
	static int currentGen, uniqueWorldSizes, currentSimulation, currentTestedSize, currentDeme;
	static implementClass_v22[] implement;
	static Thread[] thread;
	static int processors, TOTAL_POPSIZE, TOTAL_TESTCASES;
	static double[] fitness, randNum;
	static char[][][] init_strategy, join_strategy;
	static char[][][][] demes_init_strategy, demes_join_strategy;
	static char[][][] FT_init_strategy, FT_join_strategy;
	static World_System_Shell_v22[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static Random rd = new Random();
	static boolean[][][] attackProfiles, balanceProfiles, bandwagonProfiles;
	static boolean sortedFit, shuffled;
	static char[][][][] temp_init_strategy, temp_join_strategy;
	static double[][] tempFitness;
	static int prevAttacks, prevJoins, prevSimilarityCount;
	static double avg_len;
	static double[] similarityWarInit_Avg, similarityWarJoin_Avg;
	static final boolean BLOATFIGHT = false, PRINTINDIV = false, PRINTPROFILE = false;
	static int previousBestHash_1, previousBestHash_2;
	static int[] length, world_sizes, TESTCASES = {1000, 2000};
	static int[][] testedStrategyIndex;
	static int[][][] testStrategyIndexes, initialEnmity;
	static double[][] capabilities, probabilities, GaussProb, GaussProb2;
	static double[][][] capChangeRates;
	static int[][][] changeIndexes;
	static WriteFile writer, stateStats_writer, state_profiles_writer, state_profiles_writer2;
	static String outputString;

	public static void main(String[] args) {
		master_initalize();
		for (currentSimulation = 1; currentSimulation <= simulations; currentSimulation++) {
			Simulation_v22 simulation_v22 = new Simulation_v22();
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
		initialEnmity= new int[TOTAL_TESTCASES][][];

		
		world_sizes = new int[uniqueWorldSizes];

		

		profilingWorlds_Attacks = new World_System_Shell_v22[uniqueWorldSizes][PROFILES_PER_CAT];
		profilingWorlds_Joins = new World_System_Shell_v22[uniqueWorldSizes - 1][PROFILES_PER_CAT];

		WriteFile profiles_writer = null;
		try {
			profiles_writer = new WriteFile("profiles.txt", false);
			profiles_writer.writeToFile("profile_no" + "," + "simulate_Joiner" + "," + "world_size" + "," + "capMed"
					+ "," + "capAvg" + "," + "capStd" + "," + "capMin" + "," + "capMax" + "," + "myCap" + ","
					+ "oppCap" + "," + "mySideCap" + "," + "leftCapSum" + "," + "myEnmity" + "," + "oppEnmity" + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < uniqueWorldSizes; i++) {
			for (int k = 0; k < PROFILES_PER_CAT; k++) {
				profilingWorlds_Attacks[i][k] = new World_System_Shell_v22(false, (i + MINSYSTEM));
				profiles_writer.writeToFile( k + "," + "0" + "," + (i + MINSYSTEM) + ","
						+ profilingWorlds_Attacks[i][k].capMed + "," + profilingWorlds_Attacks[i][k].capAvg + ","
						+ profilingWorlds_Attacks[i][k].capStd + "," + profilingWorlds_Attacks[i][k].capMin + ","
						+ profilingWorlds_Attacks[i][k].capMax + "," + profilingWorlds_Attacks[i][k].myCap + ","
						+ profilingWorlds_Attacks[i][k].oppCap + "," + profilingWorlds_Attacks[i][k].mySideCap + ","
						+ profilingWorlds_Attacks[i][k].leftCapSum + "," + profilingWorlds_Attacks[i][k].myEnmity + ","
						+ profilingWorlds_Attacks[i][k].oppEnmity + "\n");
			}
			if (i > 0)
				for (int k = 0; k < PROFILES_PER_CAT; k++) {
					profilingWorlds_Joins[i - 1][k] = new World_System_Shell_v22(true, (i + MINSYSTEM));
					profiles_writer.writeToFile( k + "," + "1" + "," + (i  + MINSYSTEM ) + ","
							+ profilingWorlds_Joins[i - 1][k].capMed + "," + profilingWorlds_Joins[i - 1][k].capAvg + ","
							+ profilingWorlds_Joins[i - 1][k].capStd + "," + profilingWorlds_Joins[i - 1][k].capMin + ","
							+ profilingWorlds_Joins[i - 1][k].capMax + "," + profilingWorlds_Joins[i - 1][k].myCap + ","
							+ profilingWorlds_Joins[i - 1][k].oppCap + "," + profilingWorlds_Joins[i - 1][k].mySideCap + ","
							+ profilingWorlds_Joins[i - 1][k].leftCapSum + "," + profilingWorlds_Joins[i - 1][k].myEnmity + ","
							+ profilingWorlds_Joins[i - 1][k].oppEnmity + "\n");
				}
		}

		
		
		try {
			state_profiles_writer= new WriteFile("state_profiles.txt", false);
			outputString = ("simulation" + "," + "stateNum" + "," + "generation" + "," + "world_size") ; 
		
			for (int k = 0; k < PROFILES_PER_CAT; k++) {
				outputString += ("," + "init_profile_" + (k + 1) ); 
				 
			}
			for (int k = 0; k < PROFILES_PER_CAT; k++) {
				outputString += ("," + "balance_profile_" + (k + 1) );
				
			}
			for (int k = 0; k < PROFILES_PER_CAT; k++) {
				outputString += ("," + "bandwagon_profile_" + (k + 1) );
				
			}
			state_profiles_writer.writeToFile(outputString + "\n"); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			state_profiles_writer2 = new WriteFile("state_profiles_v2.txt", false);
			outputString = ("simulation" + "," + "stateNum" + "," + "generation" + "," + "world_size" + ","
					+ "attack_type" + "," + "attack" + "," + "profile_no");

			state_profiles_writer2.writeToFile(outputString + "\n");
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

				capabilities[test] = new double[length[test]];
				for (int i = 0; i < length[test]; i++) {
					capabilities[test][i] = rd.nextDouble();
				}

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
