package v20;

import java.io.IOException;
import java.util.Arrays;


public class StatisticsPrinter {

	static double total_inits, total_balances, total_bandwagons, total_joins, total_buckpasses;

	static int currentSimulation, currentGen, PROFILES_PER_CAT, currLevel;

	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	
	static boolean[][][] attackProfiles, balanceProfiles, bandwagonProfiles, joiningProfiles;

	static Strategy[][] populationAttack_2;
	static Strategy[][] populationAttack_3_1, populationAttack_3_2;
	static Strategy[][] populationJoin_3_1, populationJoin_3_2;
	
	static boolean isInit;

	StatisticsPrinter(int currentSimulation, int currLevel, int currentGen, boolean isInit) {


		StatisticsPrinter.currentSimulation = currentSimulation;
		StatisticsPrinter.currentGen = currentGen;
		StatisticsPrinter.currLevel = currLevel;
		StatisticsPrinter.isInit = isInit;

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
		
		boolean init = false, join, bal;
		int initOrder, joinOutput, myorder=-99;
		
		if (isInit) {
			int index = currLevel - 2;
			
			Strategy[][] initStrategies = new Strategy[currLevel][];
			for (int order = 1; order <= initStrategies.length; order++)
				initStrategies[order - 1] = MasterVariables.bestStratgies.getInitStratgies(currLevel, order);
			attackProfiles = new boolean[currLevel][initStrategies[0].length][PROFILES_PER_CAT];
			for (int profCounter = 0; profCounter < PROFILES_PER_CAT; profCounter++) {
				if (profilingWorlds_Attacks[index][profCounter].myCap == profilingWorlds_Attacks[index][profCounter].cap_1) {
					myorder = 1;
				} else if (profilingWorlds_Attacks[index][profCounter].myCap == profilingWorlds_Attacks[index][profCounter].cap_2) {
					myorder = 2;
				} else if (profilingWorlds_Attacks[index][profCounter].myCap == profilingWorlds_Attacks[index][profCounter].cap_3) {
					myorder = 3;
				} else if (profilingWorlds_Attacks[index][profCounter].myCap == profilingWorlds_Attacks[index][profCounter].cap_4) {
					myorder = 4;
				} else if (profilingWorlds_Attacks[index][profCounter].myCap == profilingWorlds_Attacks[index][profCounter].cap_5) {
					myorder = 5;
				} else if (profilingWorlds_Attacks[index][profCounter].myCap == profilingWorlds_Attacks[index][profCounter].cap_6) {
					myorder = 6;
				} else if (profilingWorlds_Attacks[index][profCounter].myCap == profilingWorlds_Attacks[index][profCounter].cap_7) {
					myorder = 7;
				} else if (profilingWorlds_Attacks[index][profCounter].myCap == profilingWorlds_Attacks[index][profCounter].cap_8) {
					myorder = 8;
				} else {
					System.out.println("Error statistics printer class");
					Thread.dumpStack();
					System.exit(0);
				}
				
				for (int strategyCounter = 0; strategyCounter < initStrategies[myorder - 1].length; strategyCounter++) {
					initOrder = profilingWorlds_Attacks[index][profCounter]
							.willItAttack(initStrategies[myorder - 1][strategyCounter].getInitStrategy(currLevel));
					if (initOrder > 0)
						init = true;
					else
						init = false;
					if (init) {
						attackProfiles[myorder - 1][strategyCounter][profCounter] = true;
						total_inits++;
						MasterVariables.totalProfileAttacks[currentSimulation - 1][index][profCounter]++;
						if (initOrder >= profilingWorlds_Attacks[index][profCounter].myOrder)
							initOrder++;
						MasterVariables.totalProfileAttacksOrders[currentSimulation - 1][index][profCounter][initOrder - 1]++;
					}
				}

			}
		} else {

			int index = currLevel - 3;
			
			Strategy[][] joinsStrategies = new Strategy[currLevel][];
			for (int order = 1; order <= joinsStrategies.length; order++)
				joinsStrategies[order - 1] = MasterVariables.bestStratgies.getJoinStratgies(currLevel, order);
			joiningProfiles = new boolean[currLevel][joinsStrategies[0].length][PROFILES_PER_CAT];
			balanceProfiles = new boolean[currLevel][joinsStrategies[0].length][PROFILES_PER_CAT];
			bandwagonProfiles = new boolean[currLevel][joinsStrategies[0].length][PROFILES_PER_CAT];
			for (int profCounter = 0; profCounter < PROFILES_PER_CAT; profCounter++) {
				if (profilingWorlds_Joins[index][profCounter].myCap == profilingWorlds_Joins[index][profCounter].cap_1) {
					myorder = 1;
				} else if (profilingWorlds_Joins[index][profCounter].myCap == profilingWorlds_Joins[index][profCounter].cap_2) {
					myorder = 2;
				} else if (profilingWorlds_Joins[index][profCounter].myCap == profilingWorlds_Joins[index][profCounter].cap_3) {
					myorder = 3;
				} else if (profilingWorlds_Joins[index][profCounter].myCap == profilingWorlds_Joins[index][profCounter].cap_4) {
					myorder = 4;
				} else if (profilingWorlds_Joins[index][profCounter].myCap == profilingWorlds_Joins[index][profCounter].cap_5) {
					myorder = 5;
				} else if (profilingWorlds_Joins[index][profCounter].myCap == profilingWorlds_Joins[index][profCounter].cap_6) {
					myorder = 6;
				} else if (profilingWorlds_Joins[index][profCounter].myCap == profilingWorlds_Joins[index][profCounter].cap_7) {
					myorder = 7;
				} else if (profilingWorlds_Joins[index][profCounter].myCap == profilingWorlds_Joins[index][profCounter].cap_8) {
					myorder = 8;
				} else {
					System.out.println("Error statistics printer class");
					Thread.dumpStack();
					System.exit(0);
				}

				for (int strategyCounter = 0; strategyCounter < joinsStrategies[myorder - 1].length; strategyCounter++) {
					joinOutput = profilingWorlds_Joins[index][profCounter]
							.willItJoin(joinsStrategies[myorder - 1][strategyCounter].getJoinStrategy(currLevel));
					if (joinOutput > 0)
						join = true;
					else
						join = false;
					if (join) {
						joiningProfiles[myorder - 1][strategyCounter][profCounter] = true;
						total_joins++;
						MasterVariables.totalProfileJoins[currentSimulation - 1][index][profCounter]++;
						if (joinOutput == 1)
							bal = true;
						else
							bal = false;
						if (bal) {
							balanceProfiles[myorder - 1][strategyCounter][profCounter] = true;
							total_balances++;
							MasterVariables.totalProfileBalances[currentSimulation - 1][index][profCounter]++;
						} else {
							bandwagonProfiles[myorder - 1][strategyCounter][profCounter] = true;
							total_bandwagons++;
							MasterVariables.totalProfileBandwagons[currentSimulation - 1][index][profCounter]++;
						}
					} else {
						total_buckpasses++;
					}
				}
			}
			
		}
		

		int x = 0, y=0, z=0;
		if(isInit){
		for (int i=0; i<currLevel; i++){
			x=0;
			for (int strategyCounter = 0; strategyCounter < MasterVariables.popSizes[currLevel-2]; strategyCounter++) 
			for (int profCounter = 0; profCounter < PROFILES_PER_CAT; profCounter++) 
			if(attackProfiles[i][strategyCounter][profCounter])
				x++;
			System.out.println("Level " + currLevel + " order " + (i +1) + " total inits " + x );
		}
		}
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
				+ "," + "leftCapSum" + "," + "myEnmity" + "," + "oppEnmity" + "," + "myOrder");
		
		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			MasterVariables.profiles_count_writer.writeToFile("," + "sim_tot_oppOrder_" + j);
		}
		
		MasterVariables.profiles_count_writer.writeToFile("," + "sim_tot_ProfAttacks" + ","
				+ "sim_tot_ProfBalances" + "," + "sim_tot_ProfBandwagons" + "," + "sim_tot_ProfJoins");

		MasterVariables.profiles_count_writer.writeToFile("\n");

		for (int simulationIndex = 0; simulationIndex < currentSimulation; simulationIndex++) {
			int evaluationValue;
			if (simulationIndex == currentSimulation - 1)
				evaluationValue = currLevel - 1;
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
							+ MasterVariables.profilingWorlds_Attacks[i][k].myOrder);
					for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
						if (j == MasterVariables.profilingWorlds_Attacks[i][k].myOrder || j > MasterVariables.profilingWorlds_Attacks[i][k].currentSystemSize)
							MasterVariables.profiles_count_writer.writeToFile("," + ".");
						else
						MasterVariables.profiles_count_writer.writeToFile("," + MasterVariables.totalProfileAttacksOrders[simulationIndex][i][k][j-1]);
					}
					MasterVariables.profiles_count_writer.writeToFile("," +  MasterVariables.totalProfileAttacks[simulationIndex][i][k] + "," + "." + "," + "." + ","
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
								+ MasterVariables.profilingWorlds_Joins[i - 1][k].oppEnmity + "," + ".");
						for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++)
							MasterVariables.profiles_count_writer.writeToFile("," + ".");
						MasterVariables.profiles_count_writer.writeToFile("," + "." + ","
								+ MasterVariables.totalProfileBalances[simulationIndex][i - 1][k] + ","
								+ MasterVariables.totalProfileBandwagons[simulationIndex][i - 1][k] + ","
								+ MasterVariables.totalProfileJoins[simulationIndex][i - 1][k]);

						MasterVariables.profiles_count_writer.writeToFile("\n");
					}
			}
			MasterVariables.profiles_count_writer.flush();
		}
	}

	private void checkErrors() {
		if (currLevel < 2) {
			System.out.println("StatisticsPrinter class erorr!!!");
			System.exit(0);
		}

	}

}
