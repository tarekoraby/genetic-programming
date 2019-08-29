package v16;

import java.io.IOException;

public class MainClass {

	public static void main(String[] args) {	
		prepareForSimulations();
		startSimulations();
	}

	private static void prepareForSimulations() {
		MasterVariables.initializeMasterVariables();
		printFileHeaders();
		populateProfileWorlds();
		printProfileWorlds();
		printSetupVariables();	
	}



	static void printFileHeaders() {
		try {
			MasterVariables.profiles_writer = new WriteFile("profiles.txt", false);
			MasterVariables.profiles_writer.writeToFile("profile_no" + "," + "simulate_Joiner" + "," + "world_size"
					+ "," + "capMed" + "," + "capSD" + "," + "cap_1" + "," + "cap_2" + "," + "cap_3" + "," + "cap_4"
					+ "," + "cap_5" + "," + "cap_6" + "," + "cap_7" + "," + "cap_8" + "," + "myCap" + "," + "oppCap"
					+ "," + "leftCapSum" + "," + "smallerSideCap" + "," + "largerSideCap" + ","
					+ "smallerSideLeaderCap" + "," + "largerSideLeaderCap" + "," + "capRatio" + "," + "myEnmity" + ","
					+ "oppEnmity" + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void populateProfileWorlds() {
		MasterVariables.profilingWorlds_Attacks = new World_System_Shell[MasterVariables.uniqueWorldSizes][MasterVariables.PROFILES_PER_CAT];
		MasterVariables.profilingWorlds_Joins = new World_System_Shell[MasterVariables.uniqueWorldSizes - 1][MasterVariables.PROFILES_PER_CAT];

		for (int i = 0; i < MasterVariables.uniqueWorldSizes; i++)
			for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++)
				MasterVariables.profilingWorlds_Attacks[i][k] = new World_System_Shell(false,
						(i + MasterVariables.MINSYSTEM));

		for (int i = 0; i < MasterVariables.uniqueWorldSizes - 1; i++)
			for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++)
				MasterVariables.profilingWorlds_Joins[i][k] = new World_System_Shell(true, (i
						+ MasterVariables.MINSYSTEM + 1));
	}

	private static void printProfileWorlds() {

		for (int i = 0; i < MasterVariables.uniqueWorldSizes; i++) {
			for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
				MasterVariables.profiles_writer.writeToFile((int) (k + 1) + "," + "0" + ","
						+ (i + MasterVariables.MINSYSTEM) + "," + MasterVariables.profilingWorlds_Attacks[i][k].capMed
						+ "," + MasterVariables.profilingWorlds_Attacks[i][k].capSD + ","
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
					MasterVariables.profiles_writer.writeToFile((int) (k + 1) + "," + "1" + ","
							+ (i + MasterVariables.MINSYSTEM) + ","
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
	}

	private static void startSimulations() {
		MasterVariables.currentSimulation = 1;
		do {
			Simulation simulation = new Simulation();
			simulation.start();
			MasterVariables.currentSimulation++;
		} while (MasterVariables.currentSimulation <= MasterVariables.simulations);
	}

}
