package v11;


import java.util.Random;
import java.util.concurrent.ExecutorService;




public final class MasterVariables {

	///xxxx final
	static int MASTER_GENERATIONS = 1000, POPSIZE_PER_PROCESSOR = 25, MINRANDOM = 0, MAXRANDOM = 1000,
			PROFILES_PER_CAT = 1000, MINSYSTEM = 2, MAXSYSTEM = 3, MAX_LEN = 10000, DEPTH = 5, TSIZE = 2, simulations = 100;
			/* xxx GENERATIONS_PER_DEME = 10 */
	///xxxx final
	static final int RANDOMNUMBERS = 20, CAPMED = RANDOMNUMBERS, CAPSD = CAPMED + 1, CAPMIN = CAPSD + 1,
			CAPMAX = CAPMIN + 1, MYCAP = CAPMAX + 1, OPPCAP = MYCAP + 1, SMALLERSIDECAP = OPPCAP + 1,
			LARGERSIDECAP = SMALLERSIDECAP + 1, SMALLERSIDELEADERCAP = LARGERSIDECAP + 1,
			LARGERSIDELEADERCAP = SMALLERSIDELEADERCAP + 1, LEFTCAPSUM = LARGERSIDELEADERCAP + 1,
			MYENMITY = LEFTCAPSUM + 1, OPPENMITY = MYENMITY + 1, ADD = OPPENMITY + 1, SUB = ADD + 1, MUL = ADD + 2,
			DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7, OR = ADD + 8,
			TSET_1_START = CAPMED, TSET_1_END = LEFTCAPSUM, TSET_2_START = MYENMITY, TSET_2_END = OPPENMITY,
			FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT, FSET_2_END = EQ, FSET_3_START = AND,
			FSET_3_END = OR;
	///xxxx final
	static double RANDOMPLAYER_PROB = 0.1, ENHANCMENT_MARGIN = 0, PMUT_PER_NODE = 0, MOD_CROSSOVER_PROB = 0.1,
			REPLICATION_PROB = 0.1, CROSSOVER_PROB = 0.1, SUBTREE_MUT_PROB = 0.4, PMUT_PROB = 0.1,
			ABIOGENSIS_PROB = 0.2, GAUSS_PROB_FACTOR = 0;
	///xxxx final
	static  int INTERACTIONROUNDS = 100;
	static final boolean SIMULATECONSTRUCTIVISM = true;
	///xxxx final
	static int[] randNum = new int[RANDOMNUMBERS];
	static final Random rd = new Random();
	static final int[] TESTCASES = {500, 500};
	
	static final boolean changeTestCases = false;
	static final boolean changeRandomVariables = true;
	static final boolean shareBestAcrossDemes = false;
	static final boolean HYP_TEST = false;

	static Strategy[] bestStrategies;

	static double[][] TestCasesRandNums, TestCasesRandGauss;
	static int[][] TestCasesCapabilities;
	static int[][][] TestCasesCapChangeRates;
	static int[][] bestStrategiesOrder;
	static int[] testedStratgyIndexes;

	static int testedSize, testCases;
	static boolean setupCompleted;
	
	static double[][][] masterTestCasesRandNums, masterTestCasesRandGauss;
	static int[][][] masterTestCasesCapabilities;
	static int[][][][] masterTestCasesCapChangeRates;
	static int[][][] masterbestStrategiesOrder;
	static int[][] mastertestedStratgyIndexes;

	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static double[][] totalProfileAttacks, totalProfileBalances, totalProfileBandwagons, totalProfileJoins;

	static ExecutorService executor;
	static WriteFile profiles_writer, state_profiles_writer, profiles_count_writer;
	static int Threads, uniqueWorldSizes, currentSimulation, currentGen, TOTAL_POPSIZE_PER_DEME;

	static boolean[][][] attackProfiles, balanceProfiles, bandwagonProfiles, joiningProfiles;
	static String stateProfilesString;
	
	// xxx 
	static int GENERATIONS_PER_DEME = 1;


	MasterVariables() {
		randNum[0] = 0;
		randNum[1] = 1;
		randNum[2] = -1;
		randNum[3] = 500;
		for (int i = 4; i < RANDOMNUMBERS; i++)
			randNum[i] = MINRANDOM + rd.nextInt(MAXRANDOM + 1);
		uniqueWorldSizes = 1 + MAXSYSTEM - MINSYSTEM;
		
		totalProfileAttacks = new double[uniqueWorldSizes][PROFILES_PER_CAT];
		totalProfileBalances = new double[uniqueWorldSizes][PROFILES_PER_CAT];
		totalProfileBandwagons = new double[uniqueWorldSizes][PROFILES_PER_CAT];
		totalProfileJoins = new double[uniqueWorldSizes][PROFILES_PER_CAT];
		
		initializeFitnessWorlds();
		CheckErrors();
	}

	private void initializeFitnessWorlds() {
		masterTestCasesCapabilities = new int[uniqueWorldSizes][][];
		masterTestCasesRandNums = new double[uniqueWorldSizes][][];
		masterTestCasesRandGauss = new double[uniqueWorldSizes][][];
		masterTestCasesCapChangeRates = new int[uniqueWorldSizes][][][];
		masterbestStrategiesOrder = new int[uniqueWorldSizes][][];
		mastertestedStratgyIndexes = new int[uniqueWorldSizes][];
		for (int k = 0; k < uniqueWorldSizes; k++) {
			masterTestCasesCapabilities[k] = new int[TESTCASES[k]][];
			masterTestCasesRandNums[k] = new double[TESTCASES[k]][]; 
			masterTestCasesRandGauss[k] = new double[TESTCASES[k]][];
			masterTestCasesCapChangeRates[k] = new int[TESTCASES[k]][INTERACTIONROUNDS - 1][];
			masterbestStrategiesOrder[k] = new int[TESTCASES[k]][];
			mastertestedStratgyIndexes[k] = new int[TESTCASES[k]];
			
			int testedSize = k + 2;
			for (int test = 0; test < TESTCASES[k]; test++) {
	
				int remainder = 1000;
				masterTestCasesCapabilities[k][test] = new int[testedSize];
				for (int i = 0; i < testedSize - 1; i++) {
					masterTestCasesCapabilities[k][test][i] = 1 + rd.nextInt(remainder - testedSize + i + 1);
					remainder -= masterTestCasesCapabilities[k][test][i];
				}
				masterTestCasesCapabilities[k][test][testedSize - 1] = remainder;
				
				if (HYP_TEST) {
					int sum = 0;
					for (int i = 0; i < testedSize; i++) {
						sum += masterTestCasesCapabilities[k][test][i];
						if (masterTestCasesCapabilities[k][test][i] < 0
								|| masterTestCasesCapabilities[k][test][i] >= 1000) {
							System.out.println("HYP_TEST ... master capabilities creation error");
							System.exit(0);
						}
					}
				}
	
				// shuffle capabilities to ensure their uniform distribution
				for (int i = testedSize - 1; i > 0; i--) {
					int index = rd.nextInt(i + 1);
	
					int temp = masterTestCasesCapabilities[k][test][index];
					masterTestCasesCapabilities[k][test][index] = masterTestCasesCapabilities[k][test][i];
					masterTestCasesCapabilities[k][test][i] = temp;
				}
	
				
				masterTestCasesRandNums[k][test] = new double[997];
				masterTestCasesRandGauss[k][test] = new double[997];
				for (int i = 0; i < masterTestCasesRandGauss[k][test].length; i++) {
					masterTestCasesRandNums[k][test][i] = rd.nextDouble();
					masterTestCasesRandGauss[k][test][i] = rd.nextGaussian();
				}
	
	
				for (int i = 0; i < INTERACTIONROUNDS - 1; i++) {
					masterTestCasesCapChangeRates[k][test][i] = new int[testedSize];
					for (int j = 0; j < testedSize; j++)
						masterTestCasesCapChangeRates[k][test][i][j] = 1 + rd.nextInt(101);
				}
	
				mastertestedStratgyIndexes[k][test] = rd.nextInt(testedSize);
	
				int[] order = new int[testedSize - 1];
				for (int i = 0; i < testedSize - 1; i++) {
					order[i] = i;
				}
				int random;
				masterbestStrategiesOrder[k][test] = new int[testedSize - 1];
				for (int i = 0; i < testedSize - 1; i++) {
					random = rd.nextInt(testedSize - 1);
					while (order[random] == -99)
						random = rd.nextInt(testedSize - 1);
					masterbestStrategiesOrder[k][test][i] = random;
					order[random] = -99;
				}
	
			}
		}
	}

	public static void setupFitnessWorlds(Strategy[] bStrategies, int testedSize) {
		if (setupCompleted)
			return;
		
		bestStrategies = bStrategies;
		MasterVariables.testedSize = testedSize;

		if (changeTestCases == false) {
			TestCasesCapabilities = masterTestCasesCapabilities[testedSize - 2].clone();
			TestCasesCapChangeRates = masterTestCasesCapChangeRates[testedSize - 2];
			bestStrategiesOrder = masterbestStrategiesOrder[testedSize - 2];
			testedStratgyIndexes = mastertestedStratgyIndexes[testedSize - 2];

			if (changeRandomVariables) {
				int k = testedSize - 2;
				TestCasesRandNums = new double[TESTCASES[k]][];
				for (int test = 0; test < TESTCASES[k]; test++) {
					TestCasesRandNums[test] = new double[997];
					for (int i = 0; i < TestCasesRandNums[test].length; i++) {
						TestCasesRandNums[test][i] = rd.nextDouble();
					}
				}
			} else {
				TestCasesRandNums = masterTestCasesRandNums[testedSize - 2];
			}

			if (GAUSS_PROB_FACTOR > 0) {
				int k = testedSize - 2;
				TestCasesRandGauss = new double[TESTCASES[k]][];
				for (int test = 0; test < TESTCASES[k]; test++) {
					TestCasesRandGauss[test] = new double[997];
					for (int i = 0; i < TestCasesRandGauss[test].length; i++) {
						TestCasesRandGauss[test][i] = rd.nextDouble();
					}
				}
			} else {
				TestCasesRandGauss = masterTestCasesRandGauss[testedSize - 2];
			}
		} else {

			int k = testedSize - 2;
			TestCasesCapabilities = new int[TESTCASES[k]][];
			TestCasesRandNums = new double[TESTCASES[k]][]; 
			TestCasesRandGauss = new double[TESTCASES[k]][];
			TestCasesCapChangeRates = new int[TESTCASES[k]][INTERACTIONROUNDS - 1][];
			bestStrategiesOrder = new int[TESTCASES[k]][];
			testedStratgyIndexes = new int[TESTCASES[k]];

			for (int test = 0; test < TESTCASES[k]; test++) {
	
				int remainder = 1000;
				TestCasesCapabilities[test] = new int[testedSize];
				for (int i = 0; i < testedSize - 1; i++) {
					TestCasesCapabilities[test][i] = 1 + rd.nextInt(remainder - testedSize + i + 1);
					remainder -= TestCasesCapabilities[test][i];
				}
				TestCasesCapabilities[test][testedSize - 1] = remainder;
				
				if (HYP_TEST) {
					int sum = 0;
					for (int i = 0; i < testedSize; i++) {
						sum += TestCasesCapabilities[test][i];
						if (TestCasesCapabilities[test][i] < 0
								|| TestCasesCapabilities[test][i] >= 1000) {
							System.out.println("HYP_TEST ... MV class... setupFitnessWorlds method .... capabilities creation error");
							System.exit(0);
						}
					}
				}
	
				// shuffle capabilities to ensure their uniform distribution
				for (int i = testedSize - 1; i > 0; i--) {
					int index = rd.nextInt(i + 1);
	
					int temp = TestCasesCapabilities[test][index];
					TestCasesCapabilities[test][index] = TestCasesCapabilities[test][i];
					TestCasesCapabilities[test][i] = temp;
				}
	
				
				TestCasesRandNums[test] = new double[997];
				TestCasesRandGauss[test] = new double[997];
				for (int i = 0; i < TestCasesRandGauss[test].length; i++) {
					TestCasesRandNums[test][i] = rd.nextDouble();
					TestCasesRandGauss[test][i] = rd.nextGaussian();
				}
	
	
				for (int i = 0; i < INTERACTIONROUNDS - 1; i++) {
					TestCasesCapChangeRates[test][i] = new int[testedSize];
					for (int j = 0; j < testedSize; j++)
						TestCasesCapChangeRates[test][i][j] = 1 + rd.nextInt(101);
				}
	
				testedStratgyIndexes[test] = rd.nextInt(testedSize);
	
				int[] order = new int[testedSize - 1];
				for (int i = 0; i < testedSize - 1; i++) {
					order[i] = i;
				}
				int random;
				bestStrategiesOrder[test] = new int[testedSize - 1];
				for (int i = 0; i < testedSize - 1; i++) {
					random = rd.nextInt(testedSize - 1);
					while (order[random] == -99)
						random = rd.nextInt(testedSize - 1);
					bestStrategiesOrder[test][i] = random;
					order[random] = -99;
				}
	
			}
		
		}
		
		setupCompleted = true;
	}

	public static void resetFitnessWorlds() {
		bestStrategies = null;
		TestCasesCapabilities = null;
		TestCasesRandNums = null;
		TestCasesRandGauss = null;
		TestCasesCapChangeRates = null;
		bestStrategiesOrder = null;
		testedStratgyIndexes = null;
		testedSize = 0;
		testCases = 0;
		setupCompleted = false;
	}

	@SuppressWarnings("unused")
	private void CheckErrors() {
		if (MASTER_GENERATIONS < 1 || POPSIZE_PER_PROCESSOR < 1 || PROFILES_PER_CAT < 1 || MINSYSTEM != 2
				|| MAXSYSTEM < 2 || MAX_LEN < 1 || DEPTH < 2 || TSIZE < 2 || simulations < 1 || INTERACTIONROUNDS < 1) {
			System.out.println("Too low value error");
			System.exit(0);
		}

		if ((MINSYSTEM > MAXSYSTEM) || (TSET_1_START >= TSET_1_END) || (TSET_2_START >= TSET_2_END)
				|| (FSET_1_START >= FSET_1_END) || (FSET_2_START >= FSET_2_END) || (FSET_3_START >= FSET_3_END)) {
			System.out.println("Illogical value error");
			System.exit(0);
		}

		if (Math.abs(MOD_CROSSOVER_PROB + REPLICATION_PROB + CROSSOVER_PROB + SUBTREE_MUT_PROB + PMUT_PROB
				+ ABIOGENSIS_PROB - 1) > 1E-10) {
			System.out.println("Sum error");
			System.exit(0);
		}

		if (uniqueWorldSizes != TESTCASES.length) {
			System.out.println("Error!!! Test cases array and mina dn max system don't match!!");
			System.exit(0);
		}

	}
}
