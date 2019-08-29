package v14;

import java.io.IOException;
import java.util.Arrays;

public class StatisticsPrinter {

	static double total_inits, total_balances, total_bandwagons, total_joins, total_buckpasses;

	static int currentSimulation, currentGen, PROFILES_PER_CAT, currentLevel;

	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;

	private static Strategy[] population;

	StatisticsPrinter(Strategy[] pop, int currentSimulation, int currentGen, int currentLevel) {

		StatisticsPrinter.population = pop;
		StatisticsPrinter.currentSimulation = currentSimulation;
		StatisticsPrinter.currentGen = currentGen;
		StatisticsPrinter.currentLevel = currentLevel;

		PROFILES_PER_CAT = MasterVariables.PROFILES_PER_CAT;
		profilingWorlds_Attacks = MasterVariables.profilingWorlds_Attacks;
		profilingWorlds_Joins = MasterVariables.profilingWorlds_Joins;
		checkErrors();
	}

	public void print() {
		calcProfilesFreq();
		printProfileWorlds();
	}

	private static void calcProfilesFreq() {
		total_inits = total_joins = total_balances = total_bandwagons = total_buckpasses = 0;

		boolean init, join, bal;
		for (int profCounter = 0; profCounter < PROFILES_PER_CAT; profCounter++) {
			for (int strategyCounter = 0; strategyCounter < population.length; strategyCounter++) {
				init = profilingWorlds_Attacks[currentLevel - 2][profCounter].willItAttack(population[strategyCounter]
						.getInitStrategy(currentLevel));
				if (init) {
					total_inits++;
					MasterVariables.totalProfileAttacks[currentSimulation - 1][currentLevel - 2][profCounter]++;
				}
			}
			
		}
		
		

		if (currentLevel > 2)
			for (int profCounter = 0; profCounter < PROFILES_PER_CAT; profCounter++) {
				for (int strategyCounter = 0; strategyCounter < population.length; strategyCounter++) {
					join = profilingWorlds_Joins[currentLevel - 3][profCounter]
							.willItAttack(population[strategyCounter].getJoinStrategy(currentLevel));
					if (join) {
						total_joins++;
						MasterVariables.totalProfileJoins[currentSimulation - 1][currentLevel - 3][profCounter]++;
						
						bal = profilingWorlds_Joins[currentLevel - 3][profCounter]
								.willItAttack(population[strategyCounter].getBalanceStrategy(currentLevel));
						if (bal) {
							total_balances++;
							MasterVariables.totalProfileBalances[currentSimulation - 1][currentLevel - 3][profCounter]++;
						} else {
							total_bandwagons++;
							MasterVariables.totalProfileBandwagons[currentSimulation - 1][currentLevel - 3][profCounter]++;
						}
					} else {
						total_buckpasses++;
					}
				}
			}
		
		System.out.println("Inits " + total_inits + " joins " +total_joins + " bal " + total_balances + " ban " + total_bandwagons );
	}

	private static void printProfileWorlds() {

		try {
			MasterVariables.profiles_count_writer = new WriteFile("profiles_count_v3.txt", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		MasterVariables.profiles_count_writer.writeToFile("simulation" + "," + "generation" + "," + "genPerLevel" + ","
				+ "popSizeLevel" + "," + "profile_no" + "," + "simulate_Joiner" + "," + "world_size" + "," + "capMed"
				+ "," + "capSD" + "," + "cap_1" + "," + "cap_2" + "," + "cap_3" + "," + "cap_4" + "," + "cap_5" + ","
				+ "cap_6" + "," + "cap_7" + "," + "cap_8" + "," + "myCap" + "," + "oppCap" + "," + "capRatio" + ","
				+ "smallerSideCap" + "," + "largerSideCap" + "," + "smallerSideLeaderCap" + "," + "largerSideLeaderCap"
				+ "," + "leftCapSum" + "," + "myEnmity" + "," + "oppEnmity" + "," + "Sim_tot_ProfAttacks" + ","
				+ "Sim_tot_ProfBalances" + "," + "Sim_tot_ProfBandwagons" + "," + "Sim_tot_ProfJoins");

		MasterVariables.profiles_count_writer.writeToFile("\n");

		for (int simulationIndex = 0; simulationIndex < currentSimulation; simulationIndex++) {
			int evaluationValue;
			if (simulationIndex == currentSimulation - 1)
				evaluationValue = currentLevel - 1;
			else
				evaluationValue = MasterVariables.uniqueWorldSizes;

			for (int i = 0; i < evaluationValue; i++) {
				for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
					MasterVariables.profiles_count_writer.writeToFile(Integer.toString(simulationIndex + 1)
							+ ","
							+ ((simulationIndex + 1 == currentSimulation && currentLevel - 2 == i) ? currentGen
									: MasterVariables.genPerLevel[i]) + "," + MasterVariables.genPerLevel[i] + ","
							+ MasterVariables.popSizes[i] + "," + (k + 1) + "," + "0" + ","
							+ (i + MasterVariables.MINSYSTEM) + ","
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
							+ MasterVariables.profilingWorlds_Attacks[i][k].capRatio + ","
							+ MasterVariables.profilingWorlds_Attacks[i][k].smallerSideCap + ","
							+ MasterVariables.profilingWorlds_Attacks[i][k].largerSideCap + ","
							+ MasterVariables.profilingWorlds_Attacks[i][k].smallerSideLeaderCap + ","
							+ MasterVariables.profilingWorlds_Attacks[i][k].largerSideLeaderCap + ","
							+ MasterVariables.profilingWorlds_Attacks[i][k].leftCapSum + ","
							+ MasterVariables.profilingWorlds_Attacks[i][k].myEnmity + ","
							+ MasterVariables.profilingWorlds_Attacks[i][k].oppEnmity + ","
							+ MasterVariables.totalProfileAttacks[simulationIndex][i][k] + "," + "." + "," + "." + ","
							+ ".");

					MasterVariables.profiles_count_writer.writeToFile("\n");

				}

				if (i > 0)
					for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
						MasterVariables.profiles_count_writer.writeToFile(Integer.toString(simulationIndex + 1)
								+ ","
								+ ((simulationIndex + 1 == currentSimulation && currentLevel - 2 == i) ? currentGen
										: MasterVariables.genPerLevel[i]) + "," + MasterVariables.genPerLevel[i] + ","
								+ MasterVariables.popSizes[i] + "," + (k + 1) + "," + "1" + ","
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
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].capRatio + ","
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].smallerSideCap + ","
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].largerSideCap + ","
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].smallerSideLeaderCap + ","
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].largerSideLeaderCap + ","
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].leftCapSum + ","
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].myEnmity + ","
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].oppEnmity + "," + "." + ","
								+ MasterVariables.totalProfileBalances[simulationIndex][i - 1][k] + ","
								+ MasterVariables.totalProfileBandwagons[simulationIndex][i - 1][k] + ","
								+ MasterVariables.totalProfileJoins[simulationIndex][i - 1][k]);

						
						MasterVariables.profiles_count_writer.writeToFile("\n");
					}
			}

		}
	}

	private void checkErrors() {
		if (currentLevel < 2) {
			System.out.println("StatisticsPrinter class erorr!!!");
			System.exit(0);
		}

	}

}
