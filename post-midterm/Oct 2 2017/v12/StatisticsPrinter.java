package v12;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class StatisticsPrinter implements Callable<Void> {

	static double total_inits, total_balances, total_bandwagons, total_joins, init_myCap_min, init_myCap_med,
			init_oppCap_min, init_oppCap_med, init_capRatio_min, init_capRatio_med, init_capMed_min, init_capMed_med,
			init_capSD_min, init_capSD_med, init_cap_1_min, init_cap_1_med, init_cap_2_min, init_cap_2_med,
			init_cap_3_min, init_cap_3_med, init_cap_4_min, init_cap_4_med, init_cap_5_min, init_cap_5_med,
			init_cap_6_min, init_cap_6_med, init_cap_7_min, init_cap_7_med, init_cap_8_min, init_cap_8_med,
			init_smallerSideCap_min, init_smallerSideCap_med, init_largerSideCap_min, init_largerSideCap_med,
			init_smallerSideLeaderCap_min, init_smallerSideLeaderCap_med, init_largerSideLeaderCap_min,
			init_largerSideLeaderCap_med, init_leftCapSum_min, init_leftCapSum_med, init_myEnmity_min,
			init_myEnmity_med, init_oppEnmity_min, init_oppEnmity_med, balance_myCap_min, balance_myCap_med,
			balance_oppCap_min, balance_oppCap_med, balance_capRatio_min, balance_capRatio_med, balance_capMed_min,
			balance_capMed_med, balance_capSD_min, balance_capSD_med, balance_cap_1_min, balance_cap_1_med,
			balance_cap_2_min, balance_cap_2_med, balance_cap_3_min, balance_cap_3_med, balance_cap_4_min,
			balance_cap_4_med, balance_cap_5_min, balance_cap_5_med, balance_cap_6_min, balance_cap_6_med,
			balance_cap_7_min, balance_cap_7_med, balance_cap_8_min, balance_cap_8_med, balance_smallerSideCap_min,
			balance_smallerSideCap_med, balance_largerSideCap_min, balance_largerSideCap_med,
			balance_smallerSideLeaderCap_min, balance_smallerSideLeaderCap_med, balance_largerSideLeaderCap_min,
			balance_largerSideLeaderCap_med, balance_leftCapSum_min, balance_leftCapSum_med, balance_myEnmity_min,
			balance_myEnmity_med, balance_oppEnmity_min, balance_oppEnmity_med, bandwagon_myCap_min,
			bandwagon_myCap_med, bandwagon_oppCap_min, bandwagon_oppCap_med, bandwagon_capRatio_min,
			bandwagon_capRatio_med, bandwagon_capMed_min, bandwagon_capMed_med, bandwagon_capSD_min,
			bandwagon_capSD_med, bandwagon_cap_1_min, bandwagon_cap_1_med, bandwagon_cap_2_min, bandwagon_cap_2_med,
			bandwagon_cap_3_min, bandwagon_cap_3_med, bandwagon_cap_4_min, bandwagon_cap_4_med, bandwagon_cap_5_min,
			bandwagon_cap_5_med, bandwagon_cap_6_min, bandwagon_cap_6_med, bandwagon_cap_7_min, bandwagon_cap_7_med,
			bandwagon_cap_8_min, bandwagon_cap_8_med, bandwagon_smallerSideCap_min, bandwagon_smallerSideCap_med,
			bandwagon_largerSideCap_min, bandwagon_largerSideCap_med, bandwagon_smallerSideLeaderCap_min,
			bandwagon_smallerSideLeaderCap_med, bandwagon_largerSideLeaderCap_min, bandwagon_largerSideLeaderCap_med,
			bandwagon_leftCapSum_min, bandwagon_leftCapSum_med, bandwagon_myEnmity_min, bandwagon_myEnmity_med,
			bandwagon_oppEnmity_min, bandwagon_oppEnmity_med, join_myCap_min, join_myCap_med, join_oppCap_min,
			join_oppCap_med, join_capRatio_min, join_capRatio_med, join_capMed_min, join_capMed_med, join_capSD_min,
			join_capSD_med, join_cap_1_min, join_cap_1_med, join_cap_2_min, join_cap_2_med, join_cap_3_min,
			join_cap_3_med, join_cap_4_min, join_cap_4_med, join_cap_5_min, join_cap_5_med, join_cap_6_min,
			join_cap_6_med, join_cap_7_min, join_cap_7_med, join_cap_8_min, join_cap_8_med, join_smallerSideCap_min,
			join_smallerSideCap_med, join_largerSideCap_min, join_largerSideCap_med, join_smallerSideLeaderCap_min,
			join_smallerSideLeaderCap_med, join_largerSideLeaderCap_min, join_largerSideLeaderCap_med,
			join_leftCapSum_min, join_leftCapSum_med, join_myEnmity_min, join_myEnmity_med, join_oppEnmity_min,
			join_oppEnmity_med;

	static int currentSimulation, currentGen, currentDeme, PROFILES_PER_CAT, currentLevel;
	static LevelStrategy bestLevelStrategy;

	static boolean[][] attackProfiles, balanceProfiles, bandwagonProfiles, joiningProfiles;
	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static boolean printInProgress;
	static String outputString;
	static int fitness;

	StatisticsPrinter(LevelStrategy bestLevelStrategy, int fitness, int currentSimulation, int currentGen,
			int currentLevel, int currentDeme) {
		if (printInProgress) {
			System.out.println("Error StatisticsPrinter class !!! Print in progress");
			System.exit(0);
		}
		printInProgress = true;
		StatisticsPrinter.bestLevelStrategy = bestLevelStrategy;
		StatisticsPrinter.currentSimulation = currentSimulation;
		StatisticsPrinter.currentGen = currentGen;
		StatisticsPrinter.currentDeme = currentDeme;
		StatisticsPrinter.currentLevel = currentLevel;
		StatisticsPrinter.fitness = fitness;
		PROFILES_PER_CAT = MasterVariables.PROFILES_PER_CAT;
		profilingWorlds_Attacks = MasterVariables.profilingWorlds_Attacks;
		profilingWorlds_Joins = MasterVariables.profilingWorlds_Joins;
		attackProfiles = MasterVariables.attackProfiles[currentLevel - 2];
		if (currentLevel > 2) {
			balanceProfiles = MasterVariables.balanceProfiles[currentLevel - 2];
			bandwagonProfiles = MasterVariables.bandwagonProfiles[currentLevel - 2];
			joiningProfiles = MasterVariables.joiningProfiles[currentLevel - 2];
		}
		outputString = MasterVariables.stateProfilesString;
		checkErrors();
	}

	public Void call() {
		populateProfiles();
		calculateProfileVariables();
		printProfileVariablesToFile();
		printProfileWorlds();

		printInProgress = false;
		return null;
	}

	private static void populateProfiles() {

		for (int i = 0; i < PROFILES_PER_CAT; i++) {
			attackProfiles[currentDeme][i] = profilingWorlds_Attacks[currentLevel - 2][i]
					.willItAttack(bestLevelStrategy.init_strategy);
			if (attackProfiles[currentDeme][i]){
				MasterVariables.totalProfileAttacks[currentSimulation - 1][currentLevel - 2][i]++;
				MasterVariables.totalProfileAttacks_PerState[currentSimulation - 1][currentLevel - 2][currentDeme][i]++;
			}
		}

		if (currentLevel > 2)
			for (int i = 0; i < PROFILES_PER_CAT; i++) {
				joiningProfiles[currentDeme][i] = profilingWorlds_Joins[currentLevel - 3][i]
						.willItAttack(bestLevelStrategy.join_strategy);
				if (joiningProfiles[currentDeme][i]) {
					MasterVariables.totalProfileJoins[currentSimulation - 1][currentLevel - 3][i]++;
					//System.out.println(currentSimulation + " " + currentLevel + " " + currentDeme + " " + i);
					MasterVariables.totalProfileJoins_PerState[currentSimulation - 1][currentLevel - 3][currentDeme][i]++;
					balanceProfiles[currentDeme][i] = profilingWorlds_Joins[currentLevel - 3][i]
							.willItAttack(bestLevelStrategy.balance_strategy);
					bandwagonProfiles[currentDeme][i] = !balanceProfiles[currentDeme][i];
					if (balanceProfiles[currentDeme][i]) {
						MasterVariables.totalProfileBalances[currentSimulation - 1][currentLevel - 3][i]++;
						MasterVariables.totalProfileBalances_PerState[currentSimulation - 1][currentLevel - 3][currentDeme][i]++;
					} else{
						MasterVariables.totalProfileBandwagons[currentSimulation - 1][currentLevel - 3][i]++;
						MasterVariables.totalProfileBandwagons_PerState[currentSimulation - 1][currentLevel - 3][currentDeme][i]++;
					}
				} else {
					balanceProfiles[currentDeme][i] = false;
					bandwagonProfiles[currentDeme][i] = false;
				}
			}
	}

	private static void calculateProfileVariables() {
		boolean even;

		int totalAttacks = 0;
		for (int i = 0; i < PROFILES_PER_CAT; i++) {
			if (attackProfiles[currentDeme][i] == true)
				totalAttacks++;
		}

		total_inits = (double) totalAttacks;

		if (totalAttacks > 0) {
			double[] init_myCap = new double[totalAttacks];
			double[] init_oppCap = new double[totalAttacks];
			double[] init_capRatio = new double[totalAttacks];
			double[] init_capMed = new double[totalAttacks];
			double[] init_capSD = new double[totalAttacks];
			double[] init_cap_1 = new double[totalAttacks];
			double[] init_cap_2 = new double[totalAttacks];
			double[] init_cap_3 = new double[totalAttacks];
			double[] init_cap_4 = new double[totalAttacks];
			double[] init_cap_5 = new double[totalAttacks];
			double[] init_cap_6 = new double[totalAttacks];
			double[] init_cap_7 = new double[totalAttacks];
			double[] init_cap_8 = new double[totalAttacks];
			double[] init_smallerSideCap = new double[totalAttacks];
			double[] init_largerSideCap = new double[totalAttacks];
			double[] init_smallerSideLeaderCap = new double[totalAttacks];
			double[] init_largerSideLeaderCap = new double[totalAttacks];
			double[] init_leftCapSum = new double[totalAttacks];
			double[] init_myEnmity = new double[totalAttacks];
			double[] init_oppEnmity = new double[totalAttacks];

			int indexWorldSizeAttacks = currentLevel - 2;

			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (attackProfiles[currentDeme][l] == true) {
					init_myCap[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].myCap;
					init_oppCap[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].oppCap;
					init_capRatio[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].capRatio;
					init_capMed[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].capMed;
					init_capSD[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].capSD;
					init_cap_1[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].cap_1;
					init_cap_2[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].cap_2;
					init_cap_3[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].cap_3;
					init_cap_4[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].cap_4;
					init_cap_5[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].cap_5;
					init_cap_6[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].cap_6;
					init_cap_7[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].cap_7;
					init_cap_8[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].cap_8;
					init_smallerSideCap[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].smallerSideCap;
					init_largerSideCap[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].largerSideCap;
					init_smallerSideLeaderCap[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].smallerSideLeaderCap;
					init_largerSideLeaderCap[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].largerSideLeaderCap;
					init_leftCapSum[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].leftCapSum;
					init_myEnmity[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].myEnmity;
					init_oppEnmity[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].oppEnmity;
					totalAttacks--;
				}
			}

			Arrays.sort(init_myCap);
			Arrays.sort(init_oppCap);
			Arrays.sort(init_capRatio);
			Arrays.sort(init_capMed);
			Arrays.sort(init_capSD);
			Arrays.sort(init_cap_1);
			Arrays.sort(init_cap_2);
			Arrays.sort(init_cap_3);
			Arrays.sort(init_cap_4);
			Arrays.sort(init_cap_5);
			Arrays.sort(init_cap_6);
			Arrays.sort(init_cap_7);
			Arrays.sort(init_cap_8);
			Arrays.sort(init_smallerSideCap);
			Arrays.sort(init_largerSideCap);
			Arrays.sort(init_smallerSideLeaderCap);
			Arrays.sort(init_largerSideLeaderCap);
			Arrays.sort(init_leftCapSum);
			Arrays.sort(init_myEnmity);
			Arrays.sort(init_oppEnmity);

			init_myCap_min = init_myCap[0];
			init_oppCap_min = init_oppCap[0];
			init_capRatio_min = init_capRatio[0];
			init_capMed_min = init_capMed[0];
			init_capSD_min = init_capSD[0];
			init_cap_1_min = init_cap_1[0];
			init_cap_2_min = init_cap_2[0];
			init_cap_3_min = init_cap_3[0];
			init_cap_4_min = init_cap_4[0];
			init_cap_5_min = init_cap_5[0];
			init_cap_6_min = init_cap_6[0];
			init_cap_7_min = init_cap_7[0];
			init_cap_8_min = init_cap_8[0];
			init_smallerSideCap_min = init_smallerSideCap[0];
			init_largerSideCap_min = init_largerSideCap[0];
			init_smallerSideLeaderCap_min = init_smallerSideLeaderCap[0];
			init_largerSideLeaderCap_min = init_largerSideLeaderCap[0];
			init_leftCapSum_min = init_leftCapSum[0];
			init_myEnmity_min = init_myEnmity[0];
			init_oppEnmity_min = init_oppEnmity[0];

			even = total_inits % 2 == 0 ? true : false;
			if (!even) {
				init_myCap_med = init_myCap[(int) total_inits / 2];
				init_oppCap_med = init_oppCap[(int) total_inits / 2];
				init_capRatio_med = init_capRatio[(int) total_inits / 2];
				init_capMed_med = init_capMed[(int) total_inits / 2];
				init_capSD_med = init_capSD[(int) total_inits / 2];
				init_cap_1_med = init_cap_1[(int) total_inits / 2];
				init_cap_2_med = init_cap_2[(int) total_inits / 2];
				init_cap_3_med = init_cap_3[(int) total_inits / 2];
				init_cap_4_med = init_cap_4[(int) total_inits / 2];
				init_cap_5_med = init_cap_5[(int) total_inits / 2];
				init_cap_6_med = init_cap_6[(int) total_inits / 2];
				init_cap_7_med = init_cap_7[(int) total_inits / 2];
				init_cap_8_med = init_cap_8[(int) total_inits / 2];
				init_smallerSideCap_min = init_smallerSideCap[(int) total_inits / 2];
				init_largerSideCap_min = init_largerSideCap[(int) total_inits / 2];
				init_smallerSideLeaderCap_min = init_smallerSideLeaderCap[(int) total_inits / 2];
				init_largerSideLeaderCap_min = init_largerSideLeaderCap[(int) total_inits / 2];
				init_leftCapSum_med = init_leftCapSum[(int) total_inits / 2];
				init_myEnmity_med = init_myEnmity[(int) total_inits / 2];
				init_oppEnmity_med = init_oppEnmity[(int) total_inits / 2];
			} else {
				init_myCap_med = (init_myCap[(int) total_inits / 2] + init_myCap[(int) (total_inits / 2) - 1]) / 2;
				init_oppCap_med = (init_oppCap[(int) total_inits / 2] + init_oppCap[(int) (total_inits / 2) - 1]) / 2;
				init_capRatio_med = (init_capRatio[(int) total_inits / 2] + init_capRatio[(int) (total_inits / 2) - 1]) / 2;
				init_capMed_med = (init_capMed[(int) total_inits / 2] + init_capMed[(int) (total_inits / 2) - 1]) / 2;
				init_capSD_med = (init_capSD[(int) total_inits / 2] + init_capSD[(int) (total_inits / 2) - 1]) / 2;
				init_cap_1_med = (init_cap_1[(int) total_inits / 2] + init_cap_1[(int) (total_inits / 2) - 1]) / 2;
				init_cap_2_med = (init_cap_2[(int) total_inits / 2] + init_cap_2[(int) (total_inits / 2) - 1]) / 2;
				init_cap_3_med = (init_cap_3[(int) total_inits / 2] + init_cap_3[(int) (total_inits / 2) - 1]) / 2;
				init_cap_4_med = (init_cap_4[(int) total_inits / 2] + init_cap_4[(int) (total_inits / 2) - 1]) / 2;
				init_cap_5_med = (init_cap_5[(int) total_inits / 2] + init_cap_5[(int) (total_inits / 2) - 1]) / 2;
				init_cap_6_med = (init_cap_6[(int) total_inits / 2] + init_cap_6[(int) (total_inits / 2) - 1]) / 2;
				init_cap_7_med = (init_cap_7[(int) total_inits / 2] + init_cap_7[(int) (total_inits / 2) - 1]) / 2;
				init_cap_8_med = (init_cap_8[(int) total_inits / 2] + init_cap_8[(int) (total_inits / 2) - 1]) / 2;
				init_smallerSideCap_min = (init_smallerSideCap[(int) total_inits / 2] + init_smallerSideCap[(int) (total_inits / 2) - 1]) / 2;
				init_largerSideCap_min = (init_largerSideCap[(int) total_inits / 2] + init_largerSideCap[(int) (total_inits / 2) - 1]) / 2;
				init_smallerSideLeaderCap_min = (init_smallerSideLeaderCap[(int) total_inits / 2] + init_smallerSideLeaderCap[(int) (total_inits / 2) - 1]) / 2;
				init_largerSideLeaderCap_min = (init_largerSideLeaderCap[(int) total_inits / 2] + init_largerSideLeaderCap[(int) (total_inits / 2) - 1]) / 2;
				init_leftCapSum_med = (init_leftCapSum[(int) total_inits / 2] + init_leftCapSum[(int) (total_inits / 2) - 1]) / 2;
				init_myEnmity_med = (init_myEnmity[(int) total_inits / 2] + init_myEnmity[(int) (total_inits / 2) - 1]) / 2;
				init_oppEnmity_med = (init_oppEnmity[(int) total_inits / 2] + init_oppEnmity[(int) (total_inits / 2) - 1]) / 2;
			}
		}

		if (currentLevel > 2) {

			int totalBalances = 0;
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (balanceProfiles[currentDeme][l] == true)
					totalBalances++;
			}

			total_balances = (double) totalBalances;

			if (total_balances > 0) {

				double[] balance_myCap = new double[totalBalances];
				double[] balance_oppCap = new double[totalBalances];
				double[] balance_capRatio = new double[totalBalances];
				double[] balance_capMed = new double[totalBalances];
				double[] balance_capSD = new double[totalBalances];
				double[] balance_cap_1 = new double[totalBalances];
				double[] balance_cap_2 = new double[totalBalances];
				double[] balance_cap_3 = new double[totalBalances];
				double[] balance_cap_4 = new double[totalBalances];
				double[] balance_cap_5 = new double[totalBalances];
				double[] balance_cap_6 = new double[totalBalances];
				double[] balance_cap_7 = new double[totalBalances];
				double[] balance_cap_8 = new double[totalBalances];

				double[] balance_smallerSideCap = new double[totalBalances];
				double[] balance_largerSideCap = new double[totalBalances];
				double[] balance_smallerSideLeaderCap = new double[totalBalances];
				double[] balance_largerSideLeaderCap = new double[totalBalances];

				double[] balance_leftCapSum = new double[totalBalances];
				double[] balance_myEnmity = new double[totalBalances];
				double[] balance_oppEnmity = new double[totalBalances];

				int indexWorldSizeJoins = currentLevel - 3;

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (balanceProfiles[currentDeme][l] == true) {
						balance_myCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myCap;
						balance_oppCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].oppCap;
						balance_capRatio[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capRatio;
						balance_capMed[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMed;
						balance_capSD[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capSD;
						balance_capSD[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].capSD;
						balance_cap_1[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_1;
						balance_cap_2[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_2;
						balance_cap_3[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_3;
						balance_cap_4[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_4;
						balance_cap_5[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_5;
						balance_cap_6[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_6;
						balance_cap_7[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_7;
						balance_cap_8[totalBalances - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_8;
						balance_smallerSideCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideCap;
						balance_largerSideCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideCap;
						balance_smallerSideLeaderCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideLeaderCap;
						balance_largerSideLeaderCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideLeaderCap;
						balance_leftCapSum[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].leftCapSum;
						balance_myEnmity[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myEnmity;
						balance_oppEnmity[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].oppEnmity;
						totalBalances--;
					}
				}

				Arrays.sort(balance_myCap);
				Arrays.sort(balance_oppCap);
				Arrays.sort(balance_capRatio);
				Arrays.sort(balance_capMed);
				Arrays.sort(balance_capSD);
				Arrays.sort(balance_cap_1);
				Arrays.sort(balance_cap_2);
				Arrays.sort(balance_cap_3);
				Arrays.sort(balance_cap_4);
				Arrays.sort(balance_cap_5);
				Arrays.sort(balance_cap_6);
				Arrays.sort(balance_cap_7);
				Arrays.sort(balance_cap_8);
				Arrays.sort(balance_smallerSideCap);
				Arrays.sort(balance_largerSideCap);
				Arrays.sort(balance_smallerSideLeaderCap);
				Arrays.sort(balance_largerSideLeaderCap);
				Arrays.sort(balance_leftCapSum);
				Arrays.sort(balance_myEnmity);
				Arrays.sort(balance_oppEnmity);

				balance_myCap_min = balance_myCap[0];
				balance_oppCap_min = balance_oppCap[0];
				balance_capRatio_min = balance_capRatio[0];
				balance_capMed_min = balance_capMed[0];
				balance_capSD_min = balance_capSD[0];
				balance_cap_2_min = balance_cap_2[0];
				balance_cap_3_min = balance_cap_3[0];
				balance_cap_4_min = balance_cap_4[0];
				balance_cap_5_min = balance_cap_5[0];
				balance_cap_6_min = balance_cap_6[0];
				balance_cap_7_min = balance_cap_7[0];
				balance_cap_8_min = balance_cap_8[0];
				balance_smallerSideCap_min = balance_smallerSideCap[0];
				balance_largerSideCap_min = balance_largerSideCap[0];
				balance_smallerSideLeaderCap_min = balance_smallerSideLeaderCap[0];
				balance_largerSideLeaderCap_min = balance_largerSideLeaderCap[0];
				balance_leftCapSum_min = balance_leftCapSum[0];
				balance_myEnmity_min = balance_myEnmity[0];
				balance_oppEnmity_min = balance_oppEnmity[0];

				even = total_balances % 2 == 0 ? true : false;
				if (!even) {
					balance_myCap_med = balance_myCap[(int) total_balances / 2];
					balance_oppCap_med = balance_oppCap[(int) total_balances / 2];
					balance_capRatio_med = balance_capRatio[(int) total_balances / 2];
					balance_capMed_med = balance_capMed[(int) total_balances / 2];
					balance_capSD_med = balance_capSD[(int) total_balances / 2];
					balance_cap_1_med = balance_cap_1[(int) total_balances / 2];
					balance_cap_2_med = balance_cap_2[(int) total_balances / 2];
					balance_cap_3_med = balance_cap_3[(int) total_balances / 2];
					balance_cap_4_med = balance_cap_4[(int) total_balances / 2];
					balance_cap_5_med = balance_cap_5[(int) total_balances / 2];
					balance_cap_6_med = balance_cap_6[(int) total_balances / 2];
					balance_cap_7_med = balance_cap_7[(int) total_balances / 2];
					balance_cap_8_med = balance_cap_8[(int) total_balances / 2];
					balance_smallerSideCap_min = balance_smallerSideCap[(int) total_balances / 2];
					balance_largerSideCap_min = balance_largerSideCap[(int) total_balances / 2];
					balance_smallerSideLeaderCap_min = balance_smallerSideLeaderCap[(int) total_balances / 2];
					balance_largerSideLeaderCap_min = balance_largerSideLeaderCap[(int) total_balances / 2];
					balance_leftCapSum_med = balance_leftCapSum[(int) total_balances / 2];
					balance_myEnmity_med = balance_myEnmity[(int) total_balances / 2];
					balance_oppEnmity_med = balance_oppEnmity[(int) total_balances / 2];
				} else {
					balance_myCap_med = (balance_myCap[(int) total_balances / 2] + balance_myCap[(int) (total_balances / 2) - 1]) / 2;
					balance_oppCap_med = (balance_oppCap[(int) total_balances / 2] + balance_oppCap[(int) (total_balances / 2) - 1]) / 2;
					balance_capRatio_med = (balance_capRatio[(int) total_balances / 2] + balance_capRatio[(int) (total_balances / 2) - 1]) / 2;
					balance_capMed_med = (balance_capMed[(int) total_balances / 2] + balance_capMed[(int) (total_balances / 2) - 1]) / 2;
					balance_capSD_med = (balance_capSD[(int) total_balances / 2] + balance_capSD[(int) (total_balances / 2) - 1]) / 2;
					balance_cap_1_med = (balance_cap_1[(int) total_balances / 2] + balance_cap_1[(int) (total_balances / 2) - 1]) / 2;
					balance_cap_2_med = (balance_cap_2[(int) total_balances / 2] + balance_cap_2[(int) (total_balances / 2) - 1]) / 2;
					balance_cap_3_med = (balance_cap_3[(int) total_balances / 2] + balance_cap_3[(int) (total_balances / 2) - 1]) / 2;
					balance_cap_4_med = (balance_cap_4[(int) total_balances / 2] + balance_cap_4[(int) (total_balances / 2) - 1]) / 2;
					balance_cap_5_med = (balance_cap_5[(int) total_balances / 2] + balance_cap_5[(int) (total_balances / 2) - 1]) / 2;
					balance_cap_6_med = (balance_cap_6[(int) total_balances / 2] + balance_cap_6[(int) (total_balances / 2) - 1]) / 2;
					balance_cap_7_med = (balance_cap_7[(int) total_balances / 2] + balance_cap_7[(int) (total_balances / 2) - 1]) / 2;
					balance_cap_8_med = (balance_cap_8[(int) total_balances / 2] + balance_cap_8[(int) (total_balances / 2) - 1]) / 2;
					balance_smallerSideCap_min = (balance_smallerSideCap[(int) total_balances / 2] + balance_smallerSideCap[(int) (total_balances / 2) - 1]) / 2;
					balance_largerSideCap_min = (balance_largerSideCap[(int) total_balances / 2] + balance_largerSideCap[(int) (total_balances / 2) - 1]) / 2;
					balance_smallerSideLeaderCap_min = (balance_smallerSideLeaderCap[(int) total_balances / 2] + balance_smallerSideLeaderCap[(int) (total_balances / 2) - 1]) / 2;
					balance_largerSideLeaderCap_min = (balance_largerSideLeaderCap[(int) total_balances / 2] + balance_largerSideLeaderCap[(int) (total_balances / 2) - 1]) / 2;
					balance_leftCapSum_med = (balance_leftCapSum[(int) total_balances / 2] + balance_leftCapSum[(int) (total_balances / 2) - 1]) / 2;
					balance_myEnmity_med = (balance_myEnmity[(int) total_balances / 2] + balance_myEnmity[(int) (total_balances / 2) - 1]) / 2;
					balance_oppEnmity_med = (balance_oppEnmity[(int) total_balances / 2] + balance_oppEnmity[(int) (total_balances / 2) - 1]) / 2;
				}
			}

			int totalBandwagons = 0;
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (bandwagonProfiles[currentDeme][l] == true)
					totalBandwagons++;
			}

			total_bandwagons = (double) totalBandwagons;

			if (total_bandwagons > 0) {
				double[] bandwagon_myCap = new double[totalBandwagons];
				double[] bandwagon_oppCap = new double[totalBandwagons];
				double[] bandwagon_capRatio = new double[totalBandwagons];
				double[] bandwagon_capMed = new double[totalBandwagons];
				double[] bandwagon_capSD = new double[totalBandwagons];
				double[] bandwagon_cap_1 = new double[totalBandwagons];
				double[] bandwagon_cap_2 = new double[totalBandwagons];
				double[] bandwagon_cap_3 = new double[totalBandwagons];
				double[] bandwagon_cap_4 = new double[totalBandwagons];
				double[] bandwagon_cap_5 = new double[totalBandwagons];
				double[] bandwagon_cap_6 = new double[totalBandwagons];
				double[] bandwagon_cap_7 = new double[totalBandwagons];
				double[] bandwagon_cap_8 = new double[totalBandwagons];
				double[] bandwagon_smallerSideCap = new double[totalBandwagons];
				double[] bandwagon_largerSideCap = new double[totalBandwagons];
				double[] bandwagon_smallerSideLeaderCap = new double[totalBandwagons];
				double[] bandwagon_largerSideLeaderCap = new double[totalBandwagons];
				double[] bandwagon_leftCapSum = new double[totalBandwagons];
				double[] bandwagon_myEnmity = new double[totalBandwagons];
				double[] bandwagon_oppEnmity = new double[totalBandwagons];

				int indexWorldSizeJoins = currentLevel - 3;

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (bandwagonProfiles[currentDeme][l] == true) {
						bandwagon_myCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myCap;
						bandwagon_oppCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].oppCap;
						bandwagon_capRatio[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capRatio;
						bandwagon_capMed[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMed;
						bandwagon_capSD[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capSD;
						bandwagon_capSD[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].capSD;
						bandwagon_cap_1[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_1;
						bandwagon_cap_2[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_2;
						bandwagon_cap_3[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_3;
						bandwagon_cap_4[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_4;
						bandwagon_cap_5[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_5;
						bandwagon_cap_6[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_6;
						bandwagon_cap_7[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_7;
						bandwagon_cap_8[totalBandwagons - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_8;
						bandwagon_smallerSideCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideCap;
						bandwagon_largerSideCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideCap;
						bandwagon_smallerSideLeaderCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideLeaderCap;
						bandwagon_largerSideLeaderCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideLeaderCap;
						bandwagon_leftCapSum[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].leftCapSum;
						bandwagon_myEnmity[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myEnmity;
						bandwagon_oppEnmity[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].oppEnmity;
						totalBandwagons--;
					}
				}

				Arrays.sort(bandwagon_myCap);
				Arrays.sort(bandwagon_oppCap);
				Arrays.sort(bandwagon_capRatio);
				Arrays.sort(bandwagon_capMed);
				Arrays.sort(bandwagon_capSD);
				Arrays.sort(bandwagon_cap_1);
				Arrays.sort(bandwagon_cap_2);
				Arrays.sort(bandwagon_cap_3);
				Arrays.sort(bandwagon_cap_4);
				Arrays.sort(bandwagon_cap_5);
				Arrays.sort(bandwagon_cap_6);
				Arrays.sort(bandwagon_cap_7);
				Arrays.sort(bandwagon_cap_8);
				Arrays.sort(bandwagon_smallerSideCap);
				Arrays.sort(bandwagon_largerSideCap);
				Arrays.sort(bandwagon_smallerSideLeaderCap);
				Arrays.sort(bandwagon_largerSideLeaderCap);
				Arrays.sort(bandwagon_leftCapSum);
				Arrays.sort(bandwagon_myEnmity);
				Arrays.sort(bandwagon_oppEnmity);

				bandwagon_myCap_min = bandwagon_myCap[0];
				bandwagon_oppCap_min = bandwagon_oppCap[0];
				bandwagon_capRatio_min = bandwagon_capRatio[0];
				bandwagon_capMed_min = bandwagon_capMed[0];
				bandwagon_capSD_min = bandwagon_capSD[0];
				bandwagon_cap_2_min = bandwagon_cap_2[0];
				bandwagon_cap_3_min = bandwagon_cap_3[0];
				bandwagon_cap_4_min = bandwagon_cap_4[0];
				bandwagon_cap_5_min = bandwagon_cap_5[0];
				bandwagon_cap_6_min = bandwagon_cap_6[0];
				bandwagon_cap_7_min = bandwagon_cap_7[0];
				bandwagon_cap_8_min = bandwagon_cap_8[0];
				bandwagon_smallerSideCap_min = bandwagon_smallerSideCap[0];
				bandwagon_largerSideCap_min = bandwagon_largerSideCap[0];
				bandwagon_smallerSideLeaderCap_min = bandwagon_smallerSideLeaderCap[0];
				bandwagon_largerSideLeaderCap_min = bandwagon_largerSideLeaderCap[0];
				bandwagon_leftCapSum_min = bandwagon_leftCapSum[0];
				bandwagon_myEnmity_min = bandwagon_myEnmity[0];
				bandwagon_oppEnmity_min = bandwagon_oppEnmity[0];

				even = total_bandwagons % 2 == 0 ? true : false;
				if (!even) {
					bandwagon_myCap_med = bandwagon_myCap[(int) total_bandwagons / 2];
					bandwagon_oppCap_med = bandwagon_oppCap[(int) total_bandwagons / 2];
					bandwagon_capRatio_med = bandwagon_capRatio[(int) total_bandwagons / 2];
					bandwagon_capMed_med = bandwagon_capMed[(int) total_bandwagons / 2];
					bandwagon_capSD_med = bandwagon_capSD[(int) total_bandwagons / 2];
					bandwagon_cap_1_med = bandwagon_cap_1[(int) total_bandwagons / 2];
					bandwagon_cap_2_med = bandwagon_cap_2[(int) total_bandwagons / 2];
					bandwagon_cap_3_med = bandwagon_cap_3[(int) total_bandwagons / 2];
					bandwagon_cap_4_med = bandwagon_cap_4[(int) total_bandwagons / 2];
					bandwagon_cap_5_med = bandwagon_cap_5[(int) total_bandwagons / 2];
					bandwagon_cap_6_med = bandwagon_cap_6[(int) total_bandwagons / 2];
					bandwagon_cap_7_med = bandwagon_cap_7[(int) total_bandwagons / 2];
					bandwagon_cap_8_med = bandwagon_cap_8[(int) total_bandwagons / 2];
					bandwagon_smallerSideCap_min = bandwagon_smallerSideCap[(int) total_bandwagons / 2];
					bandwagon_largerSideCap_min = bandwagon_largerSideCap[(int) total_bandwagons / 2];
					bandwagon_smallerSideLeaderCap_min = bandwagon_smallerSideLeaderCap[(int) total_bandwagons / 2];
					bandwagon_largerSideLeaderCap_min = bandwagon_largerSideLeaderCap[(int) total_bandwagons / 2];
					bandwagon_leftCapSum_med = bandwagon_leftCapSum[(int) total_bandwagons / 2];
					bandwagon_myEnmity_med = bandwagon_myEnmity[(int) total_bandwagons / 2];
					bandwagon_oppEnmity_med = bandwagon_oppEnmity[(int) total_bandwagons / 2];
				} else {
					bandwagon_myCap_med = (bandwagon_myCap[(int) total_bandwagons / 2] + bandwagon_myCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_oppCap_med = (bandwagon_oppCap[(int) total_bandwagons / 2] + bandwagon_oppCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_capRatio_med = (bandwagon_capRatio[(int) total_bandwagons / 2] + bandwagon_capRatio[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_capMed_med = (bandwagon_capMed[(int) total_bandwagons / 2] + bandwagon_capMed[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_capSD_med = (bandwagon_capSD[(int) total_bandwagons / 2] + bandwagon_capSD[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_cap_1_med = (bandwagon_cap_1[(int) total_bandwagons / 2] + bandwagon_cap_1[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_cap_2_med = (bandwagon_cap_2[(int) total_bandwagons / 2] + bandwagon_cap_2[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_cap_3_med = (bandwagon_cap_3[(int) total_bandwagons / 2] + bandwagon_cap_3[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_cap_4_med = (bandwagon_cap_4[(int) total_bandwagons / 2] + bandwagon_cap_4[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_cap_5_med = (bandwagon_cap_5[(int) total_bandwagons / 2] + bandwagon_cap_5[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_cap_6_med = (bandwagon_cap_6[(int) total_bandwagons / 2] + bandwagon_cap_6[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_cap_7_med = (bandwagon_cap_7[(int) total_bandwagons / 2] + bandwagon_cap_7[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_cap_8_med = (bandwagon_cap_8[(int) total_bandwagons / 2] + bandwagon_cap_8[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_smallerSideCap_min = (bandwagon_smallerSideCap[(int) total_bandwagons / 2] + bandwagon_smallerSideCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_largerSideCap_min = (bandwagon_largerSideCap[(int) total_bandwagons / 2] + bandwagon_largerSideCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_smallerSideLeaderCap_min = (bandwagon_smallerSideLeaderCap[(int) total_bandwagons / 2] + bandwagon_smallerSideLeaderCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_largerSideLeaderCap_min = (bandwagon_largerSideLeaderCap[(int) total_bandwagons / 2] + bandwagon_largerSideLeaderCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_leftCapSum_med = (bandwagon_leftCapSum[(int) total_bandwagons / 2] + bandwagon_leftCapSum[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_myEnmity_med = (bandwagon_myEnmity[(int) total_bandwagons / 2] + bandwagon_myEnmity[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_oppEnmity_med = (bandwagon_oppEnmity[(int) total_bandwagons / 2] + bandwagon_oppEnmity[(int) (total_bandwagons / 2) - 1]) / 2;
				}
			}

			int totalJoins = 0;
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (joiningProfiles[currentDeme][l] == true)
					totalJoins++;
			}

			total_joins = (double) totalJoins;

			if (total_joins > 0) {
				double[] join_myCap = new double[totalJoins];
				double[] join_oppCap = new double[totalJoins];
				double[] join_capRatio = new double[totalJoins];
				double[] join_capMed = new double[totalJoins];
				double[] join_capSD = new double[totalJoins];
				double[] join_cap_1 = new double[totalJoins];
				double[] join_cap_2 = new double[totalJoins];
				double[] join_cap_3 = new double[totalJoins];
				double[] join_cap_4 = new double[totalJoins];
				double[] join_cap_5 = new double[totalJoins];
				double[] join_cap_6 = new double[totalJoins];
				double[] join_cap_7 = new double[totalJoins];
				double[] join_cap_8 = new double[totalJoins];
				double[] join_smallerSideCap = new double[totalJoins];
				double[] join_largerSideCap = new double[totalJoins];
				double[] join_smallerSideLeaderCap = new double[totalJoins];
				double[] join_largerSideLeaderCap = new double[totalJoins];
				double[] join_leftCapSum = new double[totalJoins];
				double[] join_myEnmity = new double[totalJoins];
				double[] join_oppEnmity = new double[totalJoins];

				int indexWorldSizeJoins = currentLevel - 3;

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (joiningProfiles[currentDeme][l] == true) {
						join_myCap[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myCap;
						join_oppCap[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].oppCap;
						join_capRatio[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capRatio;
						join_capMed[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMed;
						join_capSD[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capSD;
						join_capSD[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].capSD;
						join_cap_1[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_1;
						join_cap_2[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_2;
						join_cap_3[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_3;
						join_cap_4[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_4;
						join_cap_5[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_5;
						join_cap_6[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_6;
						join_cap_7[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_7;
						join_cap_8[totalJoins - 1] = profilingWorlds_Attacks[indexWorldSizeJoins][l].cap_8;
						join_smallerSideCap[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideCap;
						join_largerSideCap[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideCap;
						join_smallerSideLeaderCap[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideLeaderCap;
						join_largerSideLeaderCap[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideLeaderCap;
						join_leftCapSum[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].leftCapSum;
						join_myEnmity[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myEnmity;
						join_oppEnmity[totalJoins - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].oppEnmity;
						totalJoins--;
					}
				}

				Arrays.sort(join_myCap);
				Arrays.sort(join_oppCap);
				Arrays.sort(join_capRatio);
				Arrays.sort(join_capMed);
				Arrays.sort(join_capSD);
				Arrays.sort(join_cap_1);
				Arrays.sort(join_cap_2);
				Arrays.sort(join_cap_3);
				Arrays.sort(join_cap_4);
				Arrays.sort(join_cap_5);
				Arrays.sort(join_cap_6);
				Arrays.sort(join_cap_7);
				Arrays.sort(join_cap_8);
				Arrays.sort(join_smallerSideCap);
				Arrays.sort(join_largerSideCap);
				Arrays.sort(join_smallerSideLeaderCap);
				Arrays.sort(join_largerSideLeaderCap);
				Arrays.sort(join_leftCapSum);
				Arrays.sort(join_myEnmity);
				Arrays.sort(join_oppEnmity);

				join_myCap_min = join_myCap[0];
				join_oppCap_min = join_oppCap[0];
				join_capRatio_min = join_capRatio[0];
				join_capMed_min = join_capMed[0];
				join_capSD_min = join_capSD[0];
				join_cap_2_min = join_cap_2[0];
				join_cap_3_min = join_cap_3[0];
				join_cap_4_min = join_cap_4[0];
				join_cap_5_min = join_cap_5[0];
				join_cap_6_min = join_cap_6[0];
				join_cap_7_min = join_cap_7[0];
				join_cap_8_min = join_cap_8[0];
				join_smallerSideCap_min = join_smallerSideCap[0];
				join_largerSideCap_min = join_largerSideCap[0];
				join_smallerSideLeaderCap_min = join_smallerSideLeaderCap[0];
				join_largerSideLeaderCap_min = join_largerSideLeaderCap[0];
				join_leftCapSum_min = join_leftCapSum[0];
				join_myEnmity_min = join_myEnmity[0];
				join_oppEnmity_min = join_oppEnmity[0];

				even = total_joins % 2 == 0 ? true : false;
				if (!even) {
					join_myCap_med = join_myCap[(int) total_joins / 2];
					join_oppCap_med = join_oppCap[(int) total_joins / 2];
					join_capRatio_med = join_capRatio[(int) total_joins / 2];
					join_capMed_med = join_capMed[(int) total_joins / 2];
					join_capSD_med = join_capSD[(int) total_joins / 2];
					join_cap_1_med = join_cap_1[(int) total_joins / 2];
					join_cap_2_med = join_cap_2[(int) total_joins / 2];
					join_cap_3_med = join_cap_3[(int) total_joins / 2];
					join_cap_4_med = join_cap_4[(int) total_joins / 2];
					join_cap_5_med = join_cap_5[(int) total_joins / 2];
					join_cap_6_med = join_cap_6[(int) total_joins / 2];
					join_cap_7_med = join_cap_7[(int) total_joins / 2];
					join_cap_8_med = join_cap_8[(int) total_joins / 2];
					join_smallerSideCap_min = join_smallerSideCap[(int) total_joins / 2];
					join_largerSideCap_min = join_largerSideCap[(int) total_joins / 2];
					join_smallerSideLeaderCap_min = join_smallerSideLeaderCap[(int) total_joins / 2];
					join_largerSideLeaderCap_min = join_largerSideLeaderCap[(int) total_joins / 2];
					join_leftCapSum_med = join_leftCapSum[(int) total_joins / 2];
					join_myEnmity_med = join_myEnmity[(int) total_joins / 2];
					join_oppEnmity_med = join_oppEnmity[(int) total_joins / 2];
				} else {
					join_myCap_med = (join_myCap[(int) total_joins / 2] + join_myCap[(int) (total_joins / 2) - 1]) / 2;
					join_oppCap_med = (join_oppCap[(int) total_joins / 2] + join_oppCap[(int) (total_joins / 2) - 1]) / 2;
					join_capRatio_med = (join_capRatio[(int) total_joins / 2] + join_capRatio[(int) (total_joins / 2) - 1]) / 2;
					join_capMed_med = (join_capMed[(int) total_joins / 2] + join_capMed[(int) (total_joins / 2) - 1]) / 2;
					join_capSD_med = (join_capSD[(int) total_joins / 2] + join_capSD[(int) (total_joins / 2) - 1]) / 2;
					join_cap_1_med = (join_cap_1[(int) total_joins / 2] + join_cap_1[(int) (total_joins / 2) - 1]) / 2;
					join_cap_2_med = (join_cap_2[(int) total_joins / 2] + join_cap_2[(int) (total_joins / 2) - 1]) / 2;
					join_cap_3_med = (join_cap_3[(int) total_joins / 2] + join_cap_3[(int) (total_joins / 2) - 1]) / 2;
					join_cap_4_med = (join_cap_4[(int) total_joins / 2] + join_cap_4[(int) (total_joins / 2) - 1]) / 2;
					join_cap_5_med = (join_cap_5[(int) total_joins / 2] + join_cap_5[(int) (total_joins / 2) - 1]) / 2;
					join_cap_6_med = (join_cap_6[(int) total_joins / 2] + join_cap_6[(int) (total_joins / 2) - 1]) / 2;
					join_cap_7_med = (join_cap_7[(int) total_joins / 2] + join_cap_7[(int) (total_joins / 2) - 1]) / 2;
					join_cap_8_med = (join_cap_8[(int) total_joins / 2] + join_cap_8[(int) (total_joins / 2) - 1]) / 2;
					join_smallerSideCap_min = (join_smallerSideCap[(int) total_joins / 2] + join_smallerSideCap[(int) (total_joins / 2) - 1]) / 2;
					join_largerSideCap_min = (join_largerSideCap[(int) total_joins / 2] + join_largerSideCap[(int) (total_joins / 2) - 1]) / 2;
					join_smallerSideLeaderCap_min = (join_smallerSideLeaderCap[(int) total_joins / 2] + join_smallerSideLeaderCap[(int) (total_joins / 2) - 1]) / 2;
					join_largerSideLeaderCap_min = (join_largerSideLeaderCap[(int) total_joins / 2] + join_largerSideLeaderCap[(int) (total_joins / 2) - 1]) / 2;
					join_leftCapSum_med = (join_leftCapSum[(int) total_joins / 2] + join_leftCapSum[(int) (total_joins / 2) - 1]) / 2;
					join_myEnmity_med = (join_myEnmity[(int) total_joins / 2] + join_myEnmity[(int) (total_joins / 2) - 1]) / 2;
					join_oppEnmity_med = (join_oppEnmity[(int) total_joins / 2] + join_oppEnmity[(int) (total_joins / 2) - 1]) / 2;
				}
			}
		}
	}

	private static void printProfileVariablesToFile() {

		int stateNumber = currentDeme + 1;

		outputString += (currentSimulation + "," + MasterVariables.GENERATIONS_PER_DEME + ","
				+ MasterVariables.RANDOMPLAYER_PROB + "," + MasterVariables.INTERACTIONROUNDS + ","
				+ MasterVariables.RANDOMNUMBERS + "," + stateNumber + "," + currentGen + ","
				+ currentLevel + "," + total_inits + ",");
		if (currentLevel == 2)
			outputString += ("." + "," + "." + "," + ".");
		else
			outputString += (total_balances + "," + total_bandwagons + "," + total_joins);

		outputString += ("," + fitness);

		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			if (j > currentLevel || j == stateNumber)
				outputString += ("," + ".");
			else
				outputString += ("," + calcSimilarity(attackProfiles[currentDeme], attackProfiles[j - 1]));
		}

		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			if (j > currentLevel || j == stateNumber || currentLevel == 2)
				outputString += ("," + ".");
			else
				outputString += ("," + calcSimilarity(balanceProfiles[currentDeme], balanceProfiles[j - 1]));
		}

		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			if (j > currentLevel || j == stateNumber || currentLevel == 2)
				outputString += ("," + ".");
			else
				outputString += ("," + calcSimilarity(bandwagonProfiles[currentDeme], bandwagonProfiles[j - 1]));
		}

		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			if (j > currentLevel || j == stateNumber || currentLevel == 2)
				outputString += ("," + ".");
			else
				outputString += ("," + calcSimilarity(joiningProfiles[currentDeme], joiningProfiles[j - 1]));
		}

		outputString += ("," + calcGenSimilarity(attackProfiles));

		if (currentLevel == 2)
			outputString += ("," + "." + "," + "." + "," + ".");
		else {
			outputString += ("," + calcGenSimilarity(balanceProfiles) + "," + calcGenSimilarity(bandwagonProfiles)
					+ "," + calcGenSimilarity(joiningProfiles));
		}

		outputString += ("," + init_myCap_min + "," + init_myCap_med + "," + init_oppCap_min + "," + init_oppCap_med
				+ "," + init_capRatio_min + "," + init_capRatio_med + "," + init_capMed_min + "," + init_capMed_med
				+ "," + init_capSD_min + "," + init_capSD_med + "," + init_cap_1_min + "," + init_cap_1_med + ","
				+ init_cap_2_min + "," + init_cap_2_med + "," + init_cap_3_min + "," + init_cap_3_med + ","
				+ init_cap_4_min + "," + init_cap_4_med + "," + init_cap_5_min + "," + init_cap_5_med + ","
				+ init_cap_6_min + "," + init_cap_6_med + "," + init_cap_7_min + "," + init_cap_7_med + ","
				+ init_cap_8_min + "," + init_cap_8_med + "," + init_smallerSideCap_min + "," + init_smallerSideCap_med
				+ "," + init_largerSideCap_min + "," + init_largerSideCap_med + "," + init_smallerSideLeaderCap_min
				+ "," + init_smallerSideLeaderCap_med + "," + init_largerSideLeaderCap_min + ","
				+ init_largerSideLeaderCap_med + "," + init_leftCapSum_min + "," + init_leftCapSum_med + ","
				+ init_myEnmity_min + "," + init_myEnmity_med + "," + init_oppEnmity_min + "," + init_oppEnmity_med);

		if (currentLevel > 2) {
			outputString += ("," + balance_myCap_min + "," + balance_myCap_med + "," + balance_oppCap_min + ","
					+ balance_oppCap_med + "," + balance_capRatio_min + "," + balance_capRatio_med + ","
					+ balance_capMed_min + "," + balance_capMed_med + "," + balance_capSD_min + "," + balance_capSD_med
					+ "," + balance_cap_1_min + "," + balance_cap_1_med + "," + balance_cap_2_min + ","
					+ balance_cap_2_med + "," + balance_cap_3_min + "," + balance_cap_3_med + "," + balance_cap_4_min
					+ "," + balance_cap_4_med + "," + balance_cap_5_min + "," + balance_cap_5_med + ","
					+ balance_cap_6_min + "," + balance_cap_6_med + "," + balance_cap_7_min + "," + balance_cap_7_med
					+ "," + balance_cap_8_min + "," + balance_cap_8_med + "," + balance_smallerSideCap_min + ","
					+ balance_smallerSideCap_med + "," + balance_largerSideCap_min + "," + balance_largerSideCap_med
					+ "," + balance_smallerSideLeaderCap_min + "," + balance_smallerSideLeaderCap_med + ","
					+ balance_largerSideLeaderCap_min + "," + balance_largerSideLeaderCap_min + ","
					+ balance_leftCapSum_min + "," + balance_leftCapSum_med + "," + balance_myEnmity_min + ","
					+ balance_myEnmity_med + "," + balance_oppEnmity_min + "," + balance_oppEnmity_med + ","
					+ bandwagon_myCap_min + "," + bandwagon_myCap_med + "," + bandwagon_oppCap_min + ","
					+ bandwagon_oppCap_med + "," + bandwagon_capRatio_min + "," + bandwagon_capRatio_med + ","
					+ bandwagon_capMed_min + "," + bandwagon_capMed_med + "," + bandwagon_capSD_min + ","
					+ bandwagon_capSD_med + "," + bandwagon_cap_1_min + "," + bandwagon_cap_1_med + ","
					+ bandwagon_cap_2_min + "," + bandwagon_cap_2_med + "," + bandwagon_cap_3_min + ","
					+ bandwagon_cap_3_med + "," + bandwagon_cap_4_min + "," + bandwagon_cap_4_med + ","
					+ bandwagon_cap_5_min + "," + bandwagon_cap_5_med + "," + bandwagon_cap_6_min + ","
					+ bandwagon_cap_6_med + "," + bandwagon_cap_7_min + "," + bandwagon_cap_7_med + ","
					+ bandwagon_cap_8_min + "," + bandwagon_cap_8_med + "," + bandwagon_smallerSideCap_min + ","
					+ bandwagon_smallerSideCap_med + "," + bandwagon_largerSideCap_min + ","
					+ bandwagon_largerSideCap_med + "," + bandwagon_smallerSideLeaderCap_min + ","
					+ bandwagon_smallerSideLeaderCap_med + "," + bandwagon_largerSideLeaderCap_min + ","
					+ bandwagon_largerSideLeaderCap_med + "," + bandwagon_leftCapSum_min + ","
					+ bandwagon_leftCapSum_med + "," + bandwagon_myEnmity_min + "," + bandwagon_myEnmity_med + ","
					+ bandwagon_oppEnmity_min + "," + bandwagon_oppEnmity_med + "," + join_myCap_min + ","
					+ join_myCap_med + "," + join_oppCap_min + "," + join_oppCap_med + "," + join_capRatio_min + ","
					+ join_capRatio_med + "," + join_capMed_min + "," + join_capMed_med + "," + join_capSD_min + ","
					+ join_capSD_med + "," + join_cap_1_min + "," + join_cap_1_med + "," + join_cap_2_min + ","
					+ join_cap_2_med + "," + join_cap_3_min + "," + join_cap_3_med + "," + join_cap_4_min + ","
					+ join_cap_4_med + "," + join_cap_5_min + "," + join_cap_5_med + "," + join_cap_6_min + ","
					+ join_cap_6_med + "," + join_cap_7_min + "," + join_cap_7_med + "," + join_cap_8_min + ","
					+ join_cap_8_med + "," + join_smallerSideCap_min + "," + join_smallerSideCap_med + ","
					+ join_largerSideCap_min + "," + join_largerSideCap_med + "," + join_smallerSideLeaderCap_min + ","
					+ join_smallerSideLeaderCap_med + "," + join_leftCapSum_min + "," + join_largerSideLeaderCap_min
					+ "," + join_largerSideLeaderCap_med + "," + join_leftCapSum_med + "," + join_myEnmity_min + ","
					+ join_myEnmity_med + "," + join_oppEnmity_min + "," + join_oppEnmity_med);
		}

		// if (currentGen % 8 != 0 || !(currentLevel ==
		// MasterVariables.MAXSYSTEM && currentDeme == currentLevel - 1)) {
		MasterVariables.state_profiles_writer.writeToFile(outputString + "\n");
		outputString = "";
		// }
	}

	private static double calcGenSimilarity(boolean[][] profiles) {
		double genSimilarity = 0;
		int size = profiles.length, numOfTrues, numOfFalses, maxAgreements, minPossibleMaxAgreement = (int) Math
				.ceil((double) size / 2);
		for (int k = 0; k < PROFILES_PER_CAT; k++) {
			numOfTrues = 0;
			numOfFalses = 0;
			for (int i = 0; i < size; i++)
				if (profiles[i][k])
					numOfTrues++;
				else
					numOfFalses++;
			maxAgreements = Math.max(numOfTrues, numOfFalses);
			genSimilarity = genSimilarity + (double) (maxAgreements - minPossibleMaxAgreement)
					/ (double) (size - minPossibleMaxAgreement);
		}
		genSimilarity = genSimilarity / (double) PROFILES_PER_CAT;
		return genSimilarity;
	}

	private static double calcSimilarity(boolean[] array1, boolean[] array2) {

		double similarity = 0;

		if (array1.length != array2.length) {
			System.out.println("Statistics printer class ... calcSimilarity method erorr!!");
			System.exit(0);
		}

		for (int i = 0; i < array1.length; i++)
			if (array1[i] == array2[i])
				similarity++;

		similarity = (similarity / PROFILES_PER_CAT);

		return (similarity);
	}

	private static void printProfileWorlds() {
		if (currentGen % 200 != 0 || !(currentLevel == MasterVariables.MAXSYSTEM && currentDeme == currentLevel - 1))
			return;
		try {
			MasterVariables.profiles_count_writer = new WriteFile("profiles_count.txt", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String outputString = "simulation" + "," + "generation" + "," + "genPerSimulation" + "," + "profile_no" + ","
				+ "simulate_Joiner" + "," + "world_size" + "," + "capMed" + "," + "capSD" + "," + "cap_1" + ","
				+ "cap_2" + "," + "cap_3" + "," + "cap_4" + "," + "cap_5" + "," + "cap_6" + "," + "cap_7" + ","
				+ "cap_8" + "," + "myCap" + "," + "oppCap" + "," + "capRatio" + "," + "smallerSideCap" + ","
				+ "largerSideCap" + "," + "smallerSideLeaderCap" + "," + "largerSideLeaderCap" + "," + "leftCapSum"
				+ "," + "myEnmity" + "," + "oppEnmity" + "," + "totalProfileAttacks" + "," + "totalProfileBalances"
				+ "," + "totalProfileBandwagons" + "," + "totalProfileJoins";
		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			outputString += ("," + "totalProfileAttack_state_" + j);
		}
		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			outputString += ("," + "totalProfileBalances_state_" + j);
		}
		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			outputString += ("," + "totalProfileBandwagons_state_" + j);
		}
		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			outputString += ("," + "totalProfileJoins_state_" + j);
		}

		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			outputString += ("," + "attack_state_" + j);
		}
		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			outputString += ("," + "balance_state_" + j);
		}
		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			outputString += ("," + "bandwagon_state_" + j);
		}
		for (int j = 1; j <= MasterVariables.MAXSYSTEM; j++) {
			outputString += ("," + "join_state_" + j);
		}
		outputString += "\n";

		for (int simulationIndex = 0; simulationIndex < currentSimulation; simulationIndex++) {

			outputString += "";
			for (int i = 0; i < MasterVariables.uniqueWorldSizes; i++) {
				for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
					outputString += (Integer.toString(simulationIndex + 1) + "," + currentGen + ","
							+ MasterVariables.MASTER_GENERATIONS + "," + (k + 1) + "," + "0" + ","
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
							+ MasterVariables.totalProfileAttacks[simulationIndex][i][k] + "," + "." + "," + "." + "," + ".");
					
					
					for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
						if (j <= i + 1) {
							outputString += ("," + MasterVariables.totalProfileAttacks_PerState[simulationIndex][i][j][k]);
						} else
							outputString += ("," + ".");	
					}
					for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
						outputString += ("," + ".");
					}
					for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
						outputString += ("," + ".");
					}
					for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
						outputString += ("," + ".");
					}
					
					
					for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
						if (j <= i + 1) {
							if (MasterVariables.attackProfiles[i][j][k])
								outputString += ("," + "1");
							else
								outputString += ("," + "0");
						} else
							outputString += ("," + ".");
					}
					for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
						outputString += ("," + ".");
					}
					for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
						outputString += ("," + ".");
					}
					for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
						outputString += ("," + ".");
					}
					outputString += "\n";
					
				}
				
				if (i > 0)
					for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
						outputString += (Integer.toString(simulationIndex + 1) + "," + currentGen + ","
								+ MasterVariables.MASTER_GENERATIONS + "," + (k + 1) + "," + "1" + ","
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
								+ MasterVariables.totalProfileBandwagons[simulationIndex][i - 1][k] + "," + MasterVariables.totalProfileJoins[simulationIndex][i - 1][k]);
						
						for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
							outputString += ("," + ".");
						}
						for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
							if (j <= i + 1) {
								outputString += ("," + MasterVariables.totalProfileBalances_PerState[simulationIndex][i - 1][j][k]);
							} else
								outputString += ("," + ".");
						}
						for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
							if (j <= i + 1) {
								outputString += ("," + MasterVariables.totalProfileBandwagons_PerState[simulationIndex][i - 1][j][k]);
							} else
								outputString += ("," + ".");
						}
						for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
							if (j <= i + 1) {
								outputString += ("," + MasterVariables.totalProfileJoins_PerState[simulationIndex][i - 1][j][k]);
							} else
								outputString += ("," + ".");
						}
						
						for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
							outputString += ("," + ".");
						}
						for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
							if (j <= i + 1) {
								if (MasterVariables.balanceProfiles[i][j][k])
									outputString += ("," + "1");
								else
									outputString += ("," + "0");
							} else
								outputString += ("," + ".");
						}
						for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
							if (j <= i + 1) {
								if (MasterVariables.bandwagonProfiles[i][j][k])
									outputString += ("," + "1");
								else
									outputString += ("," + "0");
							} else
								outputString += ("," + ".");
						}
						for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
							if (j <= i + 1) {
								if (MasterVariables.joiningProfiles[i][j][k])
									outputString += ("," + "1");
								else
									outputString += ("," + "0");
							} else
								outputString += ("," + ".");
						}
						outputString += "\n";
					}
			}
			
		}
		
		MasterVariables.profiles_count_writer.writeToFile(outputString);
	}

	private void checkErrors() {
		if (currentLevel < 2
				|| attackProfiles == null
				|| (currentLevel > 2 && (balanceProfiles == null || bandwagonProfiles == null || joiningProfiles == null))) {
			System.out.println("StatisticsPrinter class erorr!!!");
			System.exit(0);
		}

	}

}
