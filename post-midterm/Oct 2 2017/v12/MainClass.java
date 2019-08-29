package v12;


import java.io.IOException;
import java.util.concurrent.Executors;

public class MainClass {


	public static void main(String[] args) {
		@SuppressWarnings("unused")
		MasterVariables mvclass = new MasterVariables();
		createThreadPool();
		printFileHeaders();
		populateProfileWorlds();
		printProfileWorlds();
		printSetupVariables();
		startSimulations();
		terminate();
	}

	private static void createThreadPool() {
		MasterVariables.Threads = Runtime.getRuntime().availableProcessors();
		MasterVariables.executor = Executors.newFixedThreadPool(MasterVariables.Threads);
		MasterVariables.TOTAL_POPSIZE_PER_DEME = MasterVariables.Threads * MasterVariables.POPSIZE_PER_PROCESSOR;
	}

	static void printFileHeaders() {
		try {
			MasterVariables.profiles_writer = new WriteFile("profiles.txt", false);
			MasterVariables.profiles_writer.writeToFile("profile_no" + "," + "simulate_Joiner" + "," + "world_size" + ","
					+ "capMed" + "," + "capSD" + "," + "cap_1" + "," + "cap_2" 
					+ "," + "cap_3"+ "," + "cap_4"+ "," + "cap_5"+ "," + "cap_6"
					+ "," + "cap_7"+ "," + "cap_8"+ "," + "myCap" + ","
					+ "oppCap" + "," + "leftCapSum" + "," + "smallerSideCap" + "," + "largerSideCap" + "," + "smallerSideLeaderCap" 
					+ "," + "largerSideLeaderCap" + "," + "capRatio" +"," + "myEnmity" + "," + "oppEnmity" + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

		String outputString;
		try {
			MasterVariables.state_profiles_writer = new WriteFile("state_profiles.txt", false);
			outputString = ("simulation" + "," +  "generations_per_deme" + "," +  "random_player_prob" + "," +  "interaction_rounds" + "," + 
			"random_numbers"  + "," +  "stateNum" + "," + "generation" + "," + "world_size" + ","
					+ "total_inits" + "," + "total_balances" + "," + "total_bandwagons" + "," + "total_joins"  + "," + "fitness");

			for (int k = 1; k <= MasterVariables.MAXSYSTEM; k++) {
				outputString += ("," + "similar_init_state_" + k);
			}

			for (int k = 1; k <= MasterVariables.MAXSYSTEM; k++) {
				outputString += ("," + "similar_balance_state_" + k);
			}

			for (int k = 1; k <= MasterVariables.MAXSYSTEM; k++) {
				outputString += ("," + "similar_bandwagon_state_" + k);
			}

			for (int k = 1; k <= MasterVariables.MAXSYSTEM; k++) {
				outputString += ("," + "similar_join_state_" + k);
			}

			outputString += ("," + "gen_similarity_init_avg" + "," + "gen_similarity_balance_avg" + ","
					+ "gen_similarity_bandwagon_avg" + "," + "gen_similarity_joining_avg");

			outputString += ("," + "init_myCap_min" + "," + "init_myCap_med" + "," + "init_oppCap_min" + ","
					+ "init_oppCap_med" + "," + "init_capRatio_min" + "," + "init_capRatio_med" + ","
					+ "init_capMed_min" + "," + "init_capMed_med" + "," + "init_capSD_min" + "," + "init_capSD_med"
					+ "," + "init_capMin_min" + "," + "init_capMin_med" + "," + "init_capMax_min" + ","
					+ "init_capMax_med" + "," 			
					+ "init_smallerSideCap_min" + "," + "init_smallerSideCap_med" + ","
					+ "init_largerSideCap_min" + "," + "init_largerSideCap_med" + ","
					+ "init_smallerSideLeaderCap_min" + "," + "init_smallerSideLeaderCap_med" + ","
					+ "init_largerSideLeaderCap_min" + "," + "init_largerSideLeaderCap_med" + ","
					+ "init_leftCapSum_min" + "," + "init_leftCapSum_med" + "," + "init_myEnmity_min" + ","
					+ "init_myEnmity_med" + "," + "init_oppEnmity_min" + "," + "init_oppEnmity_med" + ","
					+ "balance_myCap_min" + "," + "balance_myCap_med" + "," + "balance_oppCap_min" + ","
					+ "balance_oppCap_med" + "," + "balance_capRatio_min" + "," + "balance_capRatio_med" + ","
					+ "balance_capMed_min" + "," + "balance_capMed_med" + "," + "balance_capSD_min" + ","
					+ "balance_capSD_med" + "," + "balance_capMin_min" + "," + "balance_capMin_med" + ","
					+ "balance_capMax_min" + "," + "balance_capMax_med" + ","
					+ "balance_smallerSideCap_min" + "," + "balance_smallerSideCap_med" + ","
					+ "balance_largerSideCap_min" + "," + "balance_largerSideCap_med" + ","
					+ "balance_smallerSideLeaderCap_min" + "," + "balance_smallerSideLeaderCap_med" + ","
					+ "balance_largerSideLeaderCap_min" + "," + "balance_largerSideLeaderCap_med" + ","
					+ "balance_leftCapSum_min" + "," + "balance_leftCapSum_med" + ","
					+ "balance_myEnmity_min" + "," + "balance_myEnmity_med" + "," + "balance_oppEnmity_min" + ","
					+ "balance_oppEnmity_med" + "," + "bandwagon_myCap_min" + "," + "bandwagon_myCap_med" + ","
					+ "bandwagon_oppCap_min" + "," + "bandwagon_oppCap_med" + "," + "bandwagon_capRatio_min" + ","
					+ "bandwagon_capRatio_med" + "," + "bandwagon_capMed_min" + "," + "bandwagon_capMed_med" + ","
					+ "bandwagon_capSD_min" + "," + "bandwagon_capSD_med" + "," + "bandwagon_capMin_min" + ","
					+ "bandwagon_capMin_med" + "," + "bandwagon_capMax_min" + "," + "bandwagon_capMax_med" + ","
					+ "bandwagon_smallerSideCap_min" + "," + "bandwagon_smallerSideCap_med" + ","
					+ "bandwagon_largerSideCap_min" + "," + "bandwagon_largerSideCap_med" + ","
					+ "bandwagon_smallerSideLeaderCap_min" + "," + "bandwagon_smallerSideLeaderCap_med" + ","
					+ "bandwagon_largerSideLeaderCap_min" + "," + "bandwagon_largerSideLeaderCap_med" + ","
					+ "bandwagon_leftCapSum_min"
					+ "," + "bandwagon_leftCapSum_med" + "," + "bandwagon_myEnmity_min" + ","
					+ "bandwagon_myEnmity_med" + "," + "bandwagon_oppEnmity_min" + "," + "bandwagon_oppEnmity_med"
					+ "," + "join_myCap_min" + "," + "join_myCap_med" + "," + "join_oppCap_min" + ","
					+ "join_oppCap_med" + "," + "join_capRatio_min" + "," + "join_capRatio_med" + ","
					+ "join_capMed_min" + "," + "join_capMed_med" + "," + "join_capSD_min" + ","
					+ "join_capSD_med" + "," + "join_capMin_min" + "," + "join_capMin_med" + ","
					+ "join_capMax_min" + "," + "join_capMax_med" + "," 
					+ "join_smallerSideCap_min" + "," + "join_smallerSideCap_med" + ","
					+ "join_largerSideCap_min" + "," + "join_largerSideCap_med" + ","
					+ "join_smallerSideLeaderCap_min" + "," + "join_smallerSideLeaderCap_med" + ","
					+ "join_largerSideLeaderCap_min" + "," + "join_largerSideLeaderCap_med" + ","
					+ "join_leftCapSum_min" + "," + "join_leftCapSum_med"
					+ "," + "join_myEnmity_min" + "," + "join_myEnmity_med" + "," + "join_oppEnmity_min"
					+ "," + "join_oppEnmity_med");

			MasterVariables.state_profiles_writer.writeToFile(outputString + "\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	private static void populateProfileWorlds() {
		MasterVariables.profilingWorlds_Attacks = new World_System_Shell[MasterVariables.uniqueWorldSizes][MasterVariables.PROFILES_PER_CAT];
		MasterVariables.profilingWorlds_Joins = new World_System_Shell[MasterVariables.uniqueWorldSizes - 1][MasterVariables.PROFILES_PER_CAT];

		for (int i = 0; i < MasterVariables.uniqueWorldSizes; i++)
			for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++)
				MasterVariables.profilingWorlds_Attacks[i][k] = new World_System_Shell(false, (i + MasterVariables.MINSYSTEM));

		for (int i = 0; i < MasterVariables.uniqueWorldSizes - 1; i++)
			for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++)
				MasterVariables.profilingWorlds_Joins[i][k] = new World_System_Shell(true, (i + MasterVariables.MINSYSTEM + 1));
	}

	private static void printProfileWorlds() {

		for (int i = 0; i < MasterVariables.uniqueWorldSizes; i++) {
			for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
				MasterVariables.profiles_writer.writeToFile( (k + 1) + "," + "0" + "," + (i + MasterVariables.MINSYSTEM) + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].capMed + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].capSD + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].cap_1 + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].cap_2 + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].cap_3 + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].cap_4 + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].cap_5 + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].cap_6 + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].cap_7 + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].cap_8 + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].myCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].oppCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].leftCapSum + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].smallerSideCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].largerSideCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].smallerSideLeaderCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].largerSideLeaderCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].capRatio + ","					
						+ MasterVariables.profilingWorlds_Attacks[i][k].myEnmity + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].oppEnmity + "\n");
			}
			if (i > 0)
				for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
					MasterVariables.profiles_writer.writeToFile( (k + 1) + "," + "1" + "," + (i + MasterVariables.MINSYSTEM) + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].capMed + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].capSD + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].cap_1 + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].cap_2 + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].cap_3 + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].cap_4 + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].cap_5 + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].cap_6 + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].cap_7 + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].cap_8 + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].myCap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].oppCap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].leftCapSum + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].smallerSideCap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].largerSideCap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].smallerSideLeaderCap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].largerSideLeaderCap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].capRatio + ","					
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].myEnmity + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].oppEnmity + "\n");
				}
		}
	}

	private static void printSetupVariables() {
		System.out.println("START OF PROGRAM");
		System.out.println("\nThis program runs " + MasterVariables.simulations + " simulations");
		System.out.println("Processing is divided over " + MasterVariables.Threads + " processors");
		System.out.println("Total population size per deme is " + MasterVariables.TOTAL_POPSIZE_PER_DEME);
	}

	private static void startSimulations() {
		MasterVariables.currentSimulation = 1;
		do {
			Simulation simulation = new Simulation();
			simulation.start();
			MasterVariables.currentSimulation++;
		} while (MasterVariables.currentSimulation <= MasterVariables.simulations);
	}

	private static void terminate() {
		MasterVariables.executor.shutdown();
	}
}
