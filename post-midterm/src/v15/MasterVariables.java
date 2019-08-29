package v15;

import java.util.Random;

public final class MasterVariables {

	static final int  MINRANDOM = -1, MAXRANDOM = 1, PROFILES_PER_CAT = 1000, MINSYSTEM = 2,
			MAXSYSTEM = 3, MAX_LEN = 10000, DEPTH = 5, TSIZE = 2, simulations = 1;

	static final char RANDOMNUMBERS = 2, CAPMED = RANDOMNUMBERS, CAPSD = CAPMED + 1, CAP_1 = CAPSD + 1,
			CAP_2 = CAP_1 + 1, CAP_3 = CAP_2 + 1, CAP_4 = CAP_3 + 1, CAP_5 = CAP_4 + 1, CAP_6 = CAP_5 + 1,
			CAP_7 = CAP_6 + 1, CAP_8 = CAP_7 + 1, MYCAP = CAP_8 + 1, OPPCAP = MYCAP + 1, SMALLERSIDECAP = OPPCAP + 1,
			LARGERSIDECAP = SMALLERSIDECAP + 1, SMALLERSIDELEADERCAP = LARGERSIDECAP + 1,
			LARGERSIDELEADERCAP = SMALLERSIDELEADERCAP + 1, LEFTCAPSUM = LARGERSIDELEADERCAP + 1,
			MYENMITY = LEFTCAPSUM + 1, OPPENMITY = MYENMITY + 1, ADD = OPPENMITY + 1, SUB = ADD + 1, MUL = ADD + 2,
			DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7, OR = ADD + 8,
			IF_THEN_ELSE = OR + 1, TSET_1_START = CAPMED, TSET_1_END = LEFTCAPSUM, TSET_2_START = MYENMITY,
			TSET_2_END = OPPENMITY, FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT, FSET_2_END = EQ,
			FSET_3_START = AND, FSET_3_END = OR, FSET_4_START = IF_THEN_ELSE, FSET_4_END = IF_THEN_ELSE;
	static final double RANDOMPLAYER_PROB = 0.1, ENHANCMENT_MARGIN = 0, MOD_CROSSOVER_PROB = 0.1, REPLICATION_PROB = 0,
			CROSSOVER_PROB = 0.15, LOGICAL_COMB_PROB = 0.2, SUBTREE_MUT_PROB = 0.2, PMUT_PROB = 0.15,
			ABIOGENSIS_PROB = 0.2, GAUSS_PROB_FACTOR = 0;
	static final int INTERACTIONROUNDS = 100;
	static final boolean SIMULATECONSTRUCTIVISM = true;
	
	//xxx final
	static double[] randNum = new double[RANDOMNUMBERS];
	static final Random rd = new Random();
	static final int[] popSizes = { 100, 100 };
	static final int[] genPerLevel = { 10000, 10000 };

	static final int capChangeRatesFactor = 100;
	static final double initPronness = 1;
	static final double joinPronness = 1;
	static final double balancePronness = 1;

	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static double[][][] totalProfileAttacks, totalProfileBalances, totalProfileBandwagons, totalProfileJoins;
	static double[][][] lastProfileAttacks, lastProfileBalances, lastProfileBandwagons, lastProfileJoins;

	static WriteFile profiles_writer, profiles_count_writer;
	static int uniqueWorldSizes, currentSimulation, currentGen;

	public static Strategy[][] masterPopulations;

	public static void initializeMasterVariables() {
		randNum[0] = 0.5;
		randNum[1] = 0.49999;
		/*
		 * randNum[2] = -1; randNum[3] = 0; for (int i = 4; i < RANDOMNUMBERS;
		 * i++) randNum[i] = (MAXRANDOM - MINRANDOM) * rd.nextDouble() +
		 * MINRANDOM;
		 */
		uniqueWorldSizes = 1 + MAXSYSTEM - MINSYSTEM;

		totalProfileAttacks = new double[simulations][uniqueWorldSizes][PROFILES_PER_CAT];
		totalProfileBalances = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];
		totalProfileBandwagons = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];
		totalProfileJoins = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];
		
		lastProfileAttacks = new double[simulations][uniqueWorldSizes][PROFILES_PER_CAT];
		lastProfileBalances = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];
		lastProfileBandwagons = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];
		lastProfileJoins = new double[simulations][uniqueWorldSizes - 1][PROFILES_PER_CAT];

		masterPopulations = new Strategy[uniqueWorldSizes][];
		CheckErrors();
	}

	@SuppressWarnings("unused")
	private static void CheckErrors() {
		if ( PROFILES_PER_CAT < 1 || MINSYSTEM != 2 || MAXSYSTEM < 2 || MAX_LEN < 1
				|| DEPTH < 2 || TSIZE < 2 || simulations < 1 || INTERACTIONROUNDS < 1) {
			System.out.println("Too low value error");
			System.exit(0);
		}

		if ((MINSYSTEM > MAXSYSTEM) || (TSET_1_START >= TSET_1_END) || (TSET_2_START >= TSET_2_END)
				|| (FSET_1_START >= FSET_1_END) || (FSET_2_START >= FSET_2_END) || (FSET_3_START >= FSET_3_END)) {
			System.out.println("Illogical value error");
			System.exit(0);
		}

		if (Math.abs(MOD_CROSSOVER_PROB + LOGICAL_COMB_PROB + REPLICATION_PROB + CROSSOVER_PROB + SUBTREE_MUT_PROB
				+ PMUT_PROB + ABIOGENSIS_PROB - 1) > 1E-10) {
			System.out.println("Sum error");
			System.exit(0);
		}

		if (uniqueWorldSizes != genPerLevel.length) {
			System.out.println("Error!!! Test cases array and min and max system don't match!!");
			System.exit(0);
		}

		if (popSizes.length != uniqueWorldSizes) {
			System.out.println("Error!!! popSizes array and min and max system don't match!!");
			System.exit(0);
		}

	}
}
