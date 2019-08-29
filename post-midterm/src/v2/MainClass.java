package v2;

import java.io.IOException;
import java.util.concurrent.Executors;

public class MainClass {

	static MasterVariables mvClass;

	public static void main(String[] args) {
		mvClass = new MasterVariables();
		createThreadPool();
		printFileHeaders();
		populateProfileWorlds();
		printProfileWorlds();
		printSetupVariables();
		startSimulations();
		terminate();
	}

	private static void createThreadPool() {
		mvClass.Threads = Runtime.getRuntime().availableProcessors();
		mvClass.executor = Executors.newFixedThreadPool(mvClass.Threads);
		mvClass.TOTAL_POPSIZE_PER_DEME = mvClass.Threads * mvClass.POPSIZE_PER_PROCESSOR;
	}

	static void printFileHeaders() {
		try {
			mvClass.profiles_writer = new WriteFile("profiles.txt", false);
			mvClass.profiles_writer.writeToFile("profile_no" + "," + "simulate_Joiner" + "," + "world_size" + ","
					+ "capMed" + "," + "capStd" + "," + "capMin" + "," + "capMax" + "," + "myCap" + ","
					+ "oppCap-sideAcap" + "," + "capRatio-sideAtoBratio" + "," + "mySideCap-sideBcap" + ","
					+ "leftCapSum" + "," + "myEnmity" + "," + "oppEnmity" + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String outputString;
		try {
			mvClass.state_profiles_writer = new WriteFile("state_profiles.txt", false);
			outputString = ("simulation" + "," + "stateNum" + "," + "generation" + "," + "world_size" + ","
					+ "total_inits" + "," + "total_balances" + "," + "total_bandwagons" + "," + "total_buckpasses");

			for (int k = 1; k <= mvClass.MAXSYSTEM; k++) {
				outputString += ("," + "similar_init_state_" + k);
			}

			for (int k = 1; k <= mvClass.MAXSYSTEM; k++) {
				outputString += ("," + "similar_balance_state_" + k);
			}

			for (int k = 1; k <= mvClass.MAXSYSTEM; k++) {
				outputString += ("," + "similar_bandwagon_state_" + k);
			}

			for (int k = 1; k <= mvClass.MAXSYSTEM; k++) {
				outputString += ("," + "similar_buckpass_state_" + k);
			}

			outputString += ("," + "gen_similarity_init_avg" + "," + "gen_similarity_balance_avg" + ","
					+ "gen_similarity_bandwagon_avg" + "," + "gen_similarity_buckpassing_avg");

			outputString += ("," + "init_myCap_min" + "," + "init_myCap_med" + "," + "init_oppCap_min" + ","
					+ "init_oppCap_med" + "," + "init_capRatio_min" + "," + "init_capRatio_med" + ","
					+ "init_capMed_min" + "," + "init_capMed_med" + "," + "init_capStd_min" + "," + "init_capStd_med"
					+ "," + "init_capMin_min" + "," + "init_capMin_med" + "," + "init_capMax_min" + ","
					+ "init_capMax_med" + "," + "init_mySideCap_min" + "," + "init_mySideCap_med" + ","
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
					+ "bandwagon_mySideCap_min" + "," + "bandwagon_mySideCap_med" + "," + "bandwagon_leftCapSum_min"
					+ "," + "bandwagon_leftCapSum_med" + "," + "bandwagon_myEnmity_min" + ","
					+ "bandwagon_myEnmity_med" + "," + "bandwagon_oppEnmity_min" + "," + "bandwagon_oppEnmity_med"
					+ "," + "buckpass_myCap_min" + "," + "buckpass_myCap_med" + "," + "buckpass_oppCap_min" + ","
					+ "buckpass_oppCap_med" + "," + "buckpass_capRatio_min" + "," + "buckpass_capRatio_med" + ","
					+ "buckpass_capMed_min" + "," + "buckpass_capMed_med" + "," + "buckpass_capStd_min" + ","
					+ "buckpass_capStd_med" + "," + "buckpass_capMin_min" + "," + "buckpass_capMin_med" + ","
					+ "buckpass_capMax_min" + "," + "buckpass_capMax_med" + "," + "buckpass_mySideCap_min" + ","
					+ "buckpass_mySideCap_med" + "," + "buckpass_leftCapSum_min" + "," + "buckpass_leftCapSum_med"
					+ "," + "buckpass_myEnmity_min" + "," + "buckpass_myEnmity_med" + "," + "buckpass_oppEnmity_min"
					+ "," + "buckpass_oppEnmity_med");

			mvClass.state_profiles_writer.writeToFile(outputString + "\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private static void populateProfileWorlds() {
		mvClass.profilingWorlds_Attacks = new World_System_Shell[mvClass.uniqueWorldSizes][mvClass.PROFILES_PER_CAT];
		mvClass.profilingWorlds_Joins = new World_System_Shell[mvClass.uniqueWorldSizes - 1][mvClass.PROFILES_PER_CAT];

		for (int i = 0; i < mvClass.uniqueWorldSizes; i++)
			for (int k = 0; k < mvClass.PROFILES_PER_CAT; k++)
				mvClass.profilingWorlds_Attacks[i][k] = new World_System_Shell(false, (i + mvClass.MINSYSTEM));

		for (int i = 0; i < mvClass.uniqueWorldSizes - 1; i++)
			for (int k = 0; k < mvClass.PROFILES_PER_CAT; k++)
				mvClass.profilingWorlds_Joins[i][k] = new World_System_Shell(true, (i + mvClass.MINSYSTEM + 1));
	}

	private static void printProfileWorlds() {
		for (int i = 0; i < mvClass.uniqueWorldSizes; i++) {
			for (int k = 0; k < mvClass.PROFILES_PER_CAT; k++) {
				mvClass.profiles_writer.writeToFile((int) (k + 1) + "," + "0" + "," + (i + mvClass.MINSYSTEM) + ","
						+ mvClass.profilingWorlds_Attacks[i][k].capMed + ","
						+ mvClass.profilingWorlds_Attacks[i][k].capStd + ","
						+ mvClass.profilingWorlds_Attacks[i][k].capMin + ","
						+ mvClass.profilingWorlds_Attacks[i][k].capMax + ","
						+ mvClass.profilingWorlds_Attacks[i][k].myCap + ","
						+ mvClass.profilingWorlds_Attacks[i][k].oppCap + ","
						+ mvClass.profilingWorlds_Attacks[i][k].capRatio + ","
						+ mvClass.profilingWorlds_Attacks[i][k].mySideCap + ","
						+ mvClass.profilingWorlds_Attacks[i][k].leftCapSum + ","
						+ mvClass.profilingWorlds_Attacks[i][k].myEnmity + ","
						+ mvClass.profilingWorlds_Attacks[i][k].oppEnmity + "\n");
			}
			if (i > 0)
				for (int k = 0; k < mvClass.PROFILES_PER_CAT; k++) {
					mvClass.profiles_writer.writeToFile((int) (k + 1) + "," + "1" + "," + (i + mvClass.MINSYSTEM) + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].capMed + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].capStd + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].capMin + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].capMax + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].myCap + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].sideAcap + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].capRatio + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].sideBcap + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].leftCapSum + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].myEnmity + ","
							+ mvClass.profilingWorlds_Joins[i - 1][k].oppEnmity + "\n");
				}
		}
	}

	private static void printSetupVariables() {
		System.out.println("START OF PROGRAM");
		System.out.println("\nThis program runs " + mvClass.simulations + " simulations");
		System.out.println("Processing is divided over " + mvClass.Threads + " processors");
		System.out.println("Total population size per deme is " + mvClass.TOTAL_POPSIZE_PER_DEME);
	}

	private static void startSimulations() {
		MasterVariables.currentSimulation = 1;
		do {
			Simulation simulation = new Simulation();
			Simulation.start();
			MasterVariables.currentSimulation++;
		} while (MasterVariables.currentSimulation <= MasterVariables.simulations);
	}

	private static void terminate() {
		mvClass.executor.shutdown();
	}
}
