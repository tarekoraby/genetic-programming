package v15;

import java.io.IOException;
import java.util.Arrays;

public class StatisticsPrinter {

	static double total_inits, total_balances, total_bandwagons, total_joins, total_buckpasses;

	static int currentSimulation, currentGen, PROFILES_PER_CAT, maxLevel;

	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;

	static Strategy[][] population2;
	static Strategy[][] population3_1, population3_2;

	StatisticsPrinter(Strategy[][] pop2, Strategy[][] pop3_1, Strategy[][] pop3_2, int currentSimulation, int currentGen, int maxLevel) {

		StatisticsPrinter.population2 = pop2;
		StatisticsPrinter.population3_1 = pop3_1;
		StatisticsPrinter.population3_2 = pop3_2;
		StatisticsPrinter.currentSimulation = currentSimulation;
		StatisticsPrinter.currentGen = currentGen;
		StatisticsPrinter.maxLevel = maxLevel;

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
			if (profilingWorlds_Attacks[0][profCounter].myCap == profilingWorlds_Attacks[0][profCounter].cap_2)
				for (int strategyCounter = 0; strategyCounter < population2[0].length; strategyCounter++) {
					init = profilingWorlds_Attacks[0][profCounter].willItAttack(population2[0][strategyCounter]
							.getInitStrategy(2));
					if (init) {
						total_inits++;
						MasterVariables.totalProfileAttacks[currentSimulation - 1][0][profCounter]++;
					}
				}
		}

		for (int profCounter = 0; profCounter < PROFILES_PER_CAT; profCounter++) {
			if (profilingWorlds_Attacks[0][profCounter].myCap == profilingWorlds_Attacks[0][profCounter].cap_1)
				for (int strategyCounter = 0; strategyCounter < population2[1].length; strategyCounter++) {
					init = profilingWorlds_Attacks[0][profCounter].willItAttack(population2[1][strategyCounter]
							.getInitStrategy(2));
					if (init) {
						total_inits++;
						MasterVariables.totalProfileAttacks[currentSimulation - 1][0][profCounter]++;
					}
				}
		}

		System.out.println("Level 2");
		System.out.println("Inits " + total_inits + " joins " + total_joins + " bal " + total_balances + " ban "
				+ total_bandwagons);

		total_inits = total_joins = total_balances = total_bandwagons = total_buckpasses = 0;

		if (maxLevel > 2) {
			for (int profCounter = 0; profCounter < PROFILES_PER_CAT; profCounter++) {
				if (profilingWorlds_Joins[0][profCounter].cap_1 < 0.5) {
					if (profilingWorlds_Joins[0][profCounter].myCap == profilingWorlds_Joins[0][profCounter].cap_3) {						
						for (int strategyCounter = 0; strategyCounter < population3_1[0].length; strategyCounter++) {
							join = profilingWorlds_Joins[0][profCounter].willItAttack(population3_1[0][strategyCounter]
									.getJoinStrategy(3));
							if (join) {
								total_joins++;
								MasterVariables.totalProfileJoins[currentSimulation - 1][0][profCounter]++;

								bal = profilingWorlds_Joins[0][profCounter]
										.willItAttack(population3_1[0][strategyCounter].getBalanceStrategy(3));
								if (bal) {
									total_balances++;
									MasterVariables.totalProfileBalances[currentSimulation - 1][0][profCounter]++;
								} else {
									total_bandwagons++;
									MasterVariables.totalProfileBandwagons[currentSimulation - 1][0][profCounter]++;
								}
							} else {
								total_buckpasses++;
							}
						}
					} else if (profilingWorlds_Joins[0][profCounter].myCap == profilingWorlds_Joins[0][profCounter].cap_2) {
						for (int strategyCounter = 0; strategyCounter < population3_1[1].length; strategyCounter++) {
							join = profilingWorlds_Joins[0][profCounter].willItAttack(population3_1[1][strategyCounter]
									.getJoinStrategy(3));
							if (join) {
								total_joins++;
								MasterVariables.totalProfileJoins[currentSimulation - 1][0][profCounter]++;

								bal = profilingWorlds_Joins[0][profCounter]
										.willItAttack(population3_1[1][strategyCounter].getBalanceStrategy(3));
								if (bal) {
									total_balances++;
									MasterVariables.totalProfileBalances[currentSimulation - 1][0][profCounter]++;
								} else {
									total_bandwagons++;
									MasterVariables.totalProfileBandwagons[currentSimulation - 1][0][profCounter]++;
								}
							} else {
								total_buckpasses++;
							}
						}
					} else if (profilingWorlds_Joins[0][profCounter].myCap == profilingWorlds_Joins[0][profCounter].cap_1) {
						for (int strategyCounter = 0; strategyCounter < population3_1[2].length; strategyCounter++) {
							join = profilingWorlds_Joins[0][profCounter].willItAttack(population3_1[2][strategyCounter]
									.getJoinStrategy(3));
							if (join) {
								total_joins++;
								MasterVariables.totalProfileJoins[currentSimulation - 1][0][profCounter]++;

								bal = profilingWorlds_Joins[0][profCounter]
										.willItAttack(population3_1[2][strategyCounter].getBalanceStrategy(3));
								if (bal) {
									total_balances++;
									MasterVariables.totalProfileBalances[currentSimulation - 1][0][profCounter]++;
								} else {
									total_bandwagons++;
									MasterVariables.totalProfileBandwagons[currentSimulation - 1][0][profCounter]++;
								}
							} else {
								total_buckpasses++;
							}
						}
					} else
						System.exit(0);
				} else if (profilingWorlds_Joins[0][profCounter].cap_1 > 0.5) {
						if (profilingWorlds_Joins[0][profCounter].myCap == profilingWorlds_Joins[0][profCounter].cap_3) {
							for (int strategyCounter = 0; strategyCounter < population3_2[0].length; strategyCounter++) {
								join = profilingWorlds_Joins[0][profCounter].willItAttack(population3_2[0][strategyCounter]
										.getJoinStrategy(3));
								if (join) {
									total_joins++;
									MasterVariables.totalProfileJoins[currentSimulation - 1][0][profCounter]++;

									bal = profilingWorlds_Joins[0][profCounter]
											.willItAttack(population3_2[0][strategyCounter].getBalanceStrategy(3));
									if (bal) {
										total_balances++;
										MasterVariables.totalProfileBalances[currentSimulation - 1][0][profCounter]++;
									} else {
										total_bandwagons++;
										MasterVariables.totalProfileBandwagons[currentSimulation - 1][0][profCounter]++;
									}
								} else {
									total_buckpasses++;
								}
							}
						} else if (profilingWorlds_Joins[0][profCounter].myCap == profilingWorlds_Joins[0][profCounter].cap_2) {
							for (int strategyCounter = 0; strategyCounter < population3_2[1].length; strategyCounter++) {
								join = profilingWorlds_Joins[0][profCounter].willItAttack(population3_2[1][strategyCounter]
										.getJoinStrategy(3));
								if (join) {
									total_joins++;
									MasterVariables.totalProfileJoins[currentSimulation - 1][0][profCounter]++;

									bal = profilingWorlds_Joins[0][profCounter]
											.willItAttack(population3_2[1][strategyCounter].getBalanceStrategy(3));
									if (bal) {
										total_balances++;
										MasterVariables.totalProfileBalances[currentSimulation - 1][0][profCounter]++;
									} else {
										total_bandwagons++;
										MasterVariables.totalProfileBandwagons[currentSimulation - 1][0][profCounter]++;
									}
								} else {
									total_buckpasses++;
								}
							}
						} else if (profilingWorlds_Joins[0][profCounter].myCap == profilingWorlds_Joins[0][profCounter].cap_1) {
							for (int strategyCounter = 0; strategyCounter < population3_2[2].length; strategyCounter++) {
								join = profilingWorlds_Joins[0][profCounter].willItAttack(population3_2[2][strategyCounter]
										.getJoinStrategy(3));
								if (join) {
									total_joins++;
									MasterVariables.totalProfileJoins[currentSimulation - 1][0][profCounter]++;

									bal = profilingWorlds_Joins[0][profCounter]
											.willItAttack(population3_2[2][strategyCounter].getBalanceStrategy(3));
									if (bal) {
										total_balances++;
										MasterVariables.totalProfileBalances[currentSimulation - 1][0][profCounter]++;
									} else {
										total_bandwagons++;
										MasterVariables.totalProfileBandwagons[currentSimulation - 1][0][profCounter]++;
									}
								} else {
									total_buckpasses++;
								}
							}
						} else
							System.exit(0);
					
				} else {
					System.exit(0);
				}
			}
			


			
			
			
			
			
			
			
			
			

			for (int profCounter = 0; profCounter < PROFILES_PER_CAT; profCounter++) {
				if (profilingWorlds_Attacks[1][profCounter].cap_1 < 0.5) {
					if (profilingWorlds_Attacks[1][profCounter].myCap == profilingWorlds_Attacks[1][profCounter].cap_3) {
						for (int strategyCounter = 0; strategyCounter < population3_1[0].length; strategyCounter++) {
							init = profilingWorlds_Attacks[1][profCounter].willItAttack(population3_1[0][strategyCounter]
									.getInitStrategy(3));
							if (init) {
								total_inits++;
								MasterVariables.totalProfileAttacks[currentSimulation - 1][1][profCounter]++;
							}
						}
						
					} else if (profilingWorlds_Attacks[1][profCounter].myCap == profilingWorlds_Attacks[1][profCounter].cap_2) {
						for (int strategyCounter = 0; strategyCounter < population3_1[1].length; strategyCounter++) {
							init = profilingWorlds_Attacks[1][profCounter].willItAttack(population3_1[1][strategyCounter]
									.getInitStrategy(3));
							if (init) {
								total_inits++;
								MasterVariables.totalProfileAttacks[currentSimulation - 1][1][profCounter]++;
							}
						}
						
					} else if (profilingWorlds_Attacks[1][profCounter].myCap == profilingWorlds_Attacks[1][profCounter].cap_1) {
						for (int strategyCounter = 0; strategyCounter < population3_1[2].length; strategyCounter++) {
							init = profilingWorlds_Attacks[1][profCounter].willItAttack(population3_1[2][strategyCounter]
									.getInitStrategy(3));
							if (init) {
								total_inits++;
								MasterVariables.totalProfileAttacks[currentSimulation - 1][1][profCounter]++;
							}
						}

					} else
						System.exit(0);
				} else if (profilingWorlds_Attacks[1][profCounter].cap_1 > 0.5) {
						if (profilingWorlds_Attacks[1][profCounter].myCap == profilingWorlds_Attacks[1][profCounter].cap_3) {
							for (int strategyCounter = 0; strategyCounter < population3_2[0].length; strategyCounter++) {
								init = profilingWorlds_Attacks[1][profCounter].willItAttack(population3_2[0][strategyCounter]
										.getInitStrategy(3));
								if (init) {
									total_inits++;
									MasterVariables.totalProfileAttacks[currentSimulation - 1][1][profCounter]++;
								}
							}
							
						} else if (profilingWorlds_Attacks[1][profCounter].myCap == profilingWorlds_Attacks[1][profCounter].cap_2) {
							for (int strategyCounter = 0; strategyCounter < population3_2[1].length; strategyCounter++) {
								init = profilingWorlds_Attacks[1][profCounter].willItAttack(population3_2[1][strategyCounter]
										.getInitStrategy(3));
								if (init) {
									total_inits++;
									MasterVariables.totalProfileAttacks[currentSimulation - 1][1][profCounter]++;
								}
							}
							
						} else if (profilingWorlds_Attacks[1][profCounter].myCap == profilingWorlds_Attacks[1][profCounter].cap_1) {
							for (int strategyCounter = 0; strategyCounter < population3_2[2].length; strategyCounter++) {
								init = profilingWorlds_Attacks[1][profCounter].willItAttack(population3_2[2][strategyCounter]
										.getInitStrategy(3));
								if (init) {
									total_inits++;
									MasterVariables.totalProfileAttacks[currentSimulation - 1][1][profCounter]++;
								}
							}
							
						} else
							System.exit(0);
					
				} else {
					System.exit(0);
				}
			}
		
		}

		System.out.println("Level 3");
		System.out.println("Inits " + total_inits + " joins " + total_joins + " bal " + total_balances + " ban "
				+ total_bandwagons);
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
				evaluationValue = maxLevel - 1;
			else
				evaluationValue = MasterVariables.uniqueWorldSizes;

			for (int i = 0; i < evaluationValue; i++) {
				for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
					MasterVariables.profiles_count_writer.writeToFile(Integer.toString(simulationIndex + 1) + ","
							+ currentGen + "," + MasterVariables.genPerLevel[i] + "," + MasterVariables.popSizes[i]
							+ "," + (k + 1) + "," + "0" + "," + (i + MasterVariables.MINSYSTEM) + ","
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
						MasterVariables.profiles_count_writer.writeToFile(Integer.toString(simulationIndex + 1) + ","
								+ currentGen + "," + MasterVariables.genPerLevel[i] + "," + MasterVariables.popSizes[i]
								+ "," + (k + 1) + "," + "1" + "," + (i + MasterVariables.MINSYSTEM) + ","
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
		if (maxLevel < 2) {
			System.out.println("StatisticsPrinter class erorr!!!");
			System.exit(0);
		}

	}

}
