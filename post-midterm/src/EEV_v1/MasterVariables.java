package EEV_v1;


public class MasterVariables {

	static final int PROFILES_PER_CAT = 1000, MINSYSTEM = 2, MAXSYSTEM = 2;

	static final int capChangeRatesFactor = 100;
	static final double initPronness = 1;
	static final double joinPronness = 1;
	static final double balancePronness = 1;

	static BestStratgies bestStratgies = new BestStratgies(MAXSYSTEM);

	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static double[][][] totalProfileAttacks, totalProfileBalances, totalProfileBandwagons, totalProfileJoins;
	static double[][][][] totalProfileAttacksOrders;

	static WriteFile profiles_writer, profiles_count_writer;
	static int uniqueWorldSizes, currentSimulation, currentGen;

	public static Strategy[][] masterPopulations;

}
