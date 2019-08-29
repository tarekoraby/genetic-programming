package v13;


import java.util.Random;
import java.util.concurrent.ExecutorService;


public final class MasterVariables {

	static final int MASTER_GENERATIONS = 10000, POPSIZE_PER_PROCESSOR = 25, MINRANDOM = -1, MAXRANDOM = 1,
			PROFILES_PER_CAT = 1000, MINSYSTEM = 2, MAXSYSTEM = 3, MAX_LEN = 10000, DEPTH = 6, TSIZE = 2,
			simulations = 1, GENERATIONS_PER_DEME = 1;

	static final int RANDOMNUMBERS = 2, CAPMED = RANDOMNUMBERS, CAPSD = CAPMED + 1, CAP_1 = CAPSD + 1,
			CAP_2 = CAP_1 + 1, CAP_3 = CAP_2 + 1, CAP_4 = CAP_3 + 1, CAP_5 = CAP_4 + 1, CAP_6 = CAP_5 + 1,
			CAP_7 = CAP_6 + 1, CAP_8 = CAP_7 + 1, MYCAP = CAP_8 + 1, OPPCAP = MYCAP + 1, SMALLERSIDECAP = OPPCAP + 1,
			LARGERSIDECAP = SMALLERSIDECAP + 1, SMALLERSIDELEADERCAP = LARGERSIDECAP + 1,
			LARGERSIDELEADERCAP = SMALLERSIDELEADERCAP + 1, LEFTCAPSUM = LARGERSIDELEADERCAP + 1,
			MYENMITY = LEFTCAPSUM + 1, OPPENMITY = MYENMITY + 1, ADD = OPPENMITY + 1, SUB = ADD + 1, MUL = ADD + 2,
			DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7, OR = ADD + 8,
			IF_THEN_ELSE = OR + 1, TSET_1_START = CAPMED, TSET_1_END = LEFTCAPSUM, TSET_2_START = MYENMITY,
			TSET_2_END = OPPENMITY, FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT, FSET_2_END = EQ,
			FSET_3_START = AND, FSET_3_END = OR, FSET_4_START = IF_THEN_ELSE, FSET_4_END = IF_THEN_ELSE;
	static final double RANDOMPLAYER_PROB = 0.1, ENHANCMENT_MARGIN = 0, PMUT_PER_NODE = 0,
			REPLICATION_PROB = 0, CROSSOVER_PROB = 0.1, LOGICAL_COMB_PROB = 0.1, SUBTREE_MUT_PROB = 0.2,
			PMUT_PROB = 0.1, ABIOGENSIS_PROB = 0.5, GAUSS_PROB_FACTOR = 0;
	static final int INTERACTIONROUNDS = 100;
	static final boolean SIMULATECONSTRUCTIVISM = true;
	// /xxxx final
	static double[] randNum = new double[RANDOMNUMBERS];
	static final Random rd = new Random();
	static final int[] TESTCASES = { 1000, 10000 };

	static final boolean changeTestCases = false;
	static final boolean changeRandomVariables = false;
	static final boolean changePronnessFactors = false;
	static final boolean changeGausianProbs = false;
	

	static final int capChangeRatesFactor = 100;
	static final double initialInitPronness = 1;
	static final double initialJoinPronness = 1;
	static final double initialBalancePronness = 1;
	
	
	
	static double[] init_proness, join_proness, balance_proness;
	static double[][] masterInitProness, masterJoinProness, masterBalanceProness;

	static Strategy[][] bestInitStrategies, bestJoinStrategies;

	static double[][] TestCasesRandNums, TestCasesRandGauss;
	static double[][] TestCasesCapabilities;
	static int[][][] TestCasesCapChangeRates;
	static int[][] bestStrategiesOrder;
	static int[] testedStratgyIndexes;

	static int testedSize, testedDeme, testCases;
	static boolean setupCompleted;
	
	static double[][][] masterTestCasesRandNums, masterTestCasesRandGauss;
	static double[][][] masterTestCasesCapabilities;
	static int[][][][] masterTestCasesCapChangeRates;
	static int[][][] masterbestStrategiesOrder;
	static int[][] mastertestedStratgyIndexes;

	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static double[][][] totalProfileAttacks, totalProfileBalances, totalProfileBandwagons, totalProfileJoins;
	static double[][][][] totalProfileAttacks_PerState, totalProfileBalances_PerState, totalProfileBandwagons_PerState, totalProfileJoins_PerState;

	static ExecutorService executor;
	static WriteFile profiles_writer, state_profiles_writer, profiles_count_writer;
	static int Threads, uniqueWorldSizes, currentSimulation, currentGen, TOTAL_POPSIZE_PER_DEME;

	static boolean[][][] attackProfiles, balanceProfiles, bandwagonProfiles, joiningProfiles;
	
	//xxxx
	static boolean testInit=false;
	
	


	MasterVariables() {
		randNum[0] = 0.5;
		randNum[1] = 0.49999;
		/*randNum[2] = -1;
		randNum[3] = 0;
		for (int i = 4; i < RANDOMNUMBERS; i++)
			randNum[i] = (MAXRANDOM - MINRANDOM) * rd.nextDouble() + MINRANDOM;*/
		uniqueWorldSizes = 1 + MAXSYSTEM - MINSYSTEM;

		totalProfileAttacks = new double[simulations][uniqueWorldSizes][PROFILES_PER_CAT];
		totalProfileBalances = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];
		totalProfileBandwagons = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];
		totalProfileJoins = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];

		totalProfileAttacks_PerState = new double[simulations][uniqueWorldSizes][][];
		totalProfileBalances_PerState = new double[simulations][uniqueWorldSizes - 1][][];
		totalProfileBandwagons_PerState = new double[simulations][uniqueWorldSizes - 1][][];
		totalProfileJoins_PerState = new double[simulations][uniqueWorldSizes - 1][][];

		for (int k = 0; k < simulations; k++) {

			for (int i = 0; i < uniqueWorldSizes; i++) {
				totalProfileAttacks_PerState[k][i] = new double[i + 2][PROFILES_PER_CAT];
				if (i > 0) {
					totalProfileBalances_PerState[k][i- 1] = new double[i + 2][PROFILES_PER_CAT];
					totalProfileBandwagons_PerState[k][i -1] = new double[i + 2][PROFILES_PER_CAT];
					totalProfileJoins_PerState[k][i - 1] = new double[i + 2][PROFILES_PER_CAT];
				}
			}
		}
		
		initializeFitnessWorlds();
		CheckErrors();
	}

	private void initializeFitnessWorlds() {
		masterTestCasesCapabilities = new double[uniqueWorldSizes][][];
		masterTestCasesRandNums = new double[uniqueWorldSizes][][];
		masterTestCasesRandGauss = new double[uniqueWorldSizes][][];
		masterTestCasesCapChangeRates = new int[uniqueWorldSizes][][][];
		masterbestStrategiesOrder = new int[uniqueWorldSizes][][];
		mastertestedStratgyIndexes = new int[uniqueWorldSizes][];
		masterInitProness= new double[uniqueWorldSizes][];
		masterBalanceProness= new double[uniqueWorldSizes][];
		masterJoinProness= new double[uniqueWorldSizes][];
		for (int k = 0; k < uniqueWorldSizes; k++) {
			masterTestCasesCapabilities[k] = new double[TESTCASES[k]][];
			masterTestCasesRandNums[k] = new double[TESTCASES[k]][]; 
			masterTestCasesRandGauss[k] = new double[TESTCASES[k]][];
			masterTestCasesCapChangeRates[k] = new int[TESTCASES[k]][INTERACTIONROUNDS - 1][];
			masterbestStrategiesOrder[k] = new int[TESTCASES[k]][];
			mastertestedStratgyIndexes[k] = new int[TESTCASES[k]];
			masterInitProness[k]= new double[TESTCASES[k]];
			masterBalanceProness[k]= new double[TESTCASES[k]];
			masterJoinProness[k]= new double[TESTCASES[k]];
			
			int testedSize = k + 2;
			for (int test = 0; test < TESTCASES[k]; test++) {
	
				double remainder = 1;
				masterTestCasesCapabilities[k][test] = new double[testedSize];
				for (int i = 0; i < testedSize - 1; i++) {
					masterTestCasesCapabilities[k][test][i] = remainder * rd.nextDouble();
					remainder -= masterTestCasesCapabilities[k][test][i];
				}
				masterTestCasesCapabilities[k][test][testedSize - 1] = remainder;
				
				
	
				// shuffle capabilities to ensure their uniform distribution
				for (int i = testedSize - 1; i > 0; i--) {
					int index = rd.nextInt(i + 1);
	
					double temp = masterTestCasesCapabilities[k][test][index];
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
						masterTestCasesCapChangeRates[k][test][i][j] = 1 + rd.nextInt(capChangeRatesFactor);
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
	
				masterInitProness[k][test] = initialInitPronness;
				masterBalanceProness[k][test] = initialBalancePronness;
				masterJoinProness[k][test] = initialJoinPronness;
			}
		}
	}

	public static void setupFitnessWorlds(Strategy[][] bestInitStrategies, Strategy[][] bestJoinStrategies, int testedSize) {
		if (setupCompleted)
			return;
		
		MasterVariables.testedSize = testedSize;
		MasterVariables.bestInitStrategies = bestInitStrategies;
		MasterVariables.bestJoinStrategies = bestJoinStrategies;
		
		int k = testedSize - 2;
		
		if (changeRandomVariables) {
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

		if (!changeTestCases ) {
			TestCasesCapabilities = masterTestCasesCapabilities[testedSize - 2];
			TestCasesCapChangeRates = masterTestCasesCapChangeRates[testedSize - 2];
			bestStrategiesOrder = masterbestStrategiesOrder[testedSize - 2];
			testedStratgyIndexes = mastertestedStratgyIndexes[testedSize - 2];
		} else {
			TestCasesCapabilities = new double[TESTCASES[k]][];
			TestCasesCapChangeRates = new int[TESTCASES[k]][INTERACTIONROUNDS - 1][];
			bestStrategiesOrder = new int[TESTCASES[k]][];
			testedStratgyIndexes = new int[TESTCASES[k]];

			for (int test = 0; test < TESTCASES[k]; test++) {

				double remainder = 1;
				TestCasesCapabilities[test] = new double[testedSize];
				for (int i = 0; i < testedSize - 1; i++) {
					TestCasesCapabilities[test][i] = remainder * rd.nextDouble();
					remainder -= TestCasesCapabilities[test][i];
				}
				TestCasesCapabilities[test][testedSize - 1] = remainder;
				
	
				// shuffle capabilities to ensure their uniform distribution
				for (int i = testedSize - 1; i > 0; i--) {
					int index = rd.nextInt(i + 1);
	
					double temp = TestCasesCapabilities[test][index];
					TestCasesCapabilities[test][index] = TestCasesCapabilities[test][i];
					TestCasesCapabilities[test][i] = temp;
				}
	
				for (int i = 0; i < INTERACTIONROUNDS - 1; i++) {
					TestCasesCapChangeRates[test][i] = new int[testedSize];
					for (int j = 0; j < testedSize; j++)
						TestCasesCapChangeRates[test][i][j] = 1 + rd.nextInt(capChangeRatesFactor);
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

		if (changePronnessFactors) {
			init_proness = new double[TESTCASES[k]];
			join_proness = new double[TESTCASES[k]];
			balance_proness = new double[TESTCASES[k]];
			for (int test = 0; test < TESTCASES[k]; test++) {
				init_proness[test] = rd.nextDouble() * 2;
				join_proness[test] = rd.nextDouble() * 2;
				balance_proness[test] = rd.nextDouble() * 2;
			}
		} else {
			init_proness = masterInitProness[k];
			join_proness = masterJoinProness[k];
			balance_proness = masterBalanceProness[k];
		}
		

		
		if (GAUSS_PROB_FACTOR > 0 && changeGausianProbs) {
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
		
		setupCompleted = true;
	}

	public static void resetFitnessWorlds() {		
		bestInitStrategies = null;
		bestJoinStrategies = null;
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

		if (Math.abs(LOGICAL_COMB_PROB + REPLICATION_PROB + CROSSOVER_PROB + SUBTREE_MUT_PROB + PMUT_PROB
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
