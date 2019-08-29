package v1;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.Callable;

public class StatisticsPrinter implements Callable<Void> {

	static double total_inits, total_balances, total_bandwagons, total_buckpasses, init_myCap_min, init_myCap_med,
			init_oppCap_min, init_oppCap_med, init_capRatio_min, init_capRatio_med, init_capMed_min, init_capMed_med,
			init_capStd_min, init_capStd_med, init_capMin_min, init_capMin_med, init_capMax_min, init_capMax_med,
			init_mySideCap_min, init_mySideCap_med, init_leftCapSum_min, init_leftCapSum_med, init_myEnmity_min,
			init_myEnmity_med, init_oppEnmity_min, init_oppEnmity_med, balance_myCap_min, balance_myCap_med,
			balance_oppCap_min, balance_oppCap_med, balance_capRatio_min, balance_capRatio_med, balance_capMed_min,
			balance_capMed_med, balance_capStd_min, balance_capStd_med, balance_capMin_min, balance_capMin_med,
			balance_capMax_min, balance_capMax_med, balance_mySideCap_min, balance_mySideCap_med,
			balance_leftCapSum_min, balance_leftCapSum_med, balance_myEnmity_min, balance_myEnmity_med,
			balance_oppEnmity_min, balance_oppEnmity_med, bandwagon_myCap_min, bandwagon_myCap_med,
			bandwagon_oppCap_min, bandwagon_oppCap_med, bandwagon_capRatio_min, bandwagon_capRatio_med,
			bandwagon_capMed_min, bandwagon_capMed_med, bandwagon_capStd_min, bandwagon_capStd_med,
			bandwagon_capMin_min, bandwagon_capMin_med, bandwagon_capMax_min, bandwagon_capMax_med,
			bandwagon_mySideCap_min, bandwagon_mySideCap_med, bandwagon_leftCapSum_min, bandwagon_leftCapSum_med,
			bandwagon_myEnmity_min, bandwagon_myEnmity_med, bandwagon_oppEnmity_min, bandwagon_oppEnmity_med,
			buckpass_myCap_min, buckpass_myCap_med, buckpass_oppCap_min, buckpass_oppCap_med, buckpass_capRatio_min,
			buckpass_capRatio_med, buckpass_capMed_min, buckpass_capMed_med, buckpass_capStd_min, buckpass_capStd_med,
			buckpass_capMin_min, buckpass_capMin_med, buckpass_capMax_min, buckpass_capMax_med, buckpass_mySideCap_min,
			buckpass_mySideCap_med, buckpass_leftCapSum_min, buckpass_leftCapSum_med, buckpass_myEnmity_min,
			buckpass_myEnmity_med, buckpass_oppEnmity_min, buckpass_oppEnmity_med;

	static int currentSimulation, currentGen, currentDeme, PROFILES_PER_CAT, currentLevel;
	static LevelStrategy bestLevelStrategy;

	static boolean[][] attackProfiles, balanceProfiles, bandwagonProfiles, buckpassingProfiles;
	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static boolean printInProgress;
	static String outputString;

	StatisticsPrinter(LevelStrategy bestLevelStrategy, int currentSimulation, int currentGen, int currentLevel,
			int currentDeme) {
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
		PROFILES_PER_CAT = MasterVariables.PROFILES_PER_CAT;
		profilingWorlds_Attacks = MasterVariables.profilingWorlds_Attacks;
		profilingWorlds_Joins = MasterVariables.profilingWorlds_Joins;
		attackProfiles = MasterVariables.attackProfiles[currentLevel - 2];
		if (currentLevel > 2) {
			balanceProfiles = MasterVariables.balanceProfiles[currentLevel - 2];
			bandwagonProfiles = MasterVariables.bandwagonProfiles[currentLevel - 2];
			buckpassingProfiles = MasterVariables.buckpassingProfiles[currentLevel - 2];
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
			attackProfiles[currentDeme][i] = profilingWorlds_Attacks[currentLevel - 2][i].willItAttack(
					bestLevelStrategy.init_strategy, 0);
			if (attackProfiles[currentDeme][i])
				MasterVariables.totalProfileAttacks[currentLevel - 2][i]++;
		}

		if (currentLevel > 2)
			for (int i = 0; i < PROFILES_PER_CAT; i++) {
				balanceProfiles[currentDeme][i] = profilingWorlds_Joins[currentLevel - 3][i].willItAttack(
						bestLevelStrategy.balance_strategy, 1);
				if (balanceProfiles[currentDeme][i])
					MasterVariables.totalProfileBalances[currentLevel - 3][i]++;

				bandwagonProfiles[currentDeme][i] = profilingWorlds_Joins[currentLevel - 3][i].willItAttack(
						bestLevelStrategy.bandwagon_strategy, 2);
				if (bandwagonProfiles[currentDeme][i])
					MasterVariables.totalProfileBandwagons[currentLevel - 3][i]++;

				buckpassingProfiles[currentDeme][i] = false;
				if (!balanceProfiles[currentDeme][i] && !bandwagonProfiles[currentDeme][i]) {
					buckpassingProfiles[currentDeme][i] = true;
					MasterVariables.totalProfileBuckpasses[currentLevel - 3][i]++;
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
			double[] init_capStd = new double[totalAttacks];
			double[] init_capMin = new double[totalAttacks];
			double[] init_capMax = new double[totalAttacks];
			double[] init_mySideCap = new double[totalAttacks];
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
					init_capStd[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].capStd;
					init_capMin[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].capMin;
					init_capMax[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].capMax;
					init_mySideCap[totalAttacks - 1] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].mySideCap;
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
			Arrays.sort(init_capStd);
			Arrays.sort(init_capMin);
			Arrays.sort(init_capMax);
			Arrays.sort(init_mySideCap);
			Arrays.sort(init_leftCapSum);
			Arrays.sort(init_myEnmity);
			Arrays.sort(init_oppEnmity);

			init_myCap_min = init_myCap[0];
			init_oppCap_min = init_oppCap[0];
			init_capRatio_min = init_capRatio[0];
			init_capMed_min = init_capMed[0];
			init_capStd_min = init_capStd[0];
			init_capMin_min = init_capMin[0];
			init_capMax_min = init_capMax[0];
			init_mySideCap_min = init_mySideCap[0];
			init_leftCapSum_min = init_leftCapSum[0];
			init_myEnmity_min = init_myEnmity[0];
			init_oppEnmity_min = init_oppEnmity[0];

			even = total_inits % 2 == 0 ? true : false;
			if (!even) {
				init_myCap_med = init_myCap[(int) total_inits / 2];
				init_oppCap_med = init_oppCap[(int) total_inits / 2];
				init_capRatio_med = init_capRatio[(int) total_inits / 2];
				init_capMed_med = init_capMed[(int) total_inits / 2];
				init_capStd_med = init_capStd[(int) total_inits / 2];
				init_capMin_med = init_capMin[(int) total_inits / 2];
				init_capMax_med = init_capMax[(int) total_inits / 2];
				init_mySideCap_med = init_mySideCap[(int) total_inits / 2];
				init_leftCapSum_med = init_leftCapSum[(int) total_inits / 2];
				init_myEnmity_med = init_myEnmity[(int) total_inits / 2];
				init_oppEnmity_med = init_oppEnmity[(int) total_inits / 2];
			} else {
				init_myCap_med = (init_myCap[(int) total_inits / 2] + init_myCap[(int) (total_inits / 2) - 1]) / 2;
				init_oppCap_med = (init_oppCap[(int) total_inits / 2] + init_oppCap[(int) (total_inits / 2) - 1]) / 2;
				init_capRatio_med = (init_capRatio[(int) total_inits / 2] + init_capRatio[(int) (total_inits / 2) - 1]) / 2;
				init_capMed_med = (init_capMed[(int) total_inits / 2] + init_capMed[(int) (total_inits / 2) - 1]) / 2;
				init_capStd_med = (init_capStd[(int) total_inits / 2] + init_capStd[(int) (total_inits / 2) - 1]) / 2;
				init_capMin_med = (init_capMin[(int) total_inits / 2] + init_capMin[(int) (total_inits / 2) - 1]) / 2;
				init_capMax_med = (init_capMax[(int) total_inits / 2] + init_capMax[(int) (total_inits / 2) - 1]) / 2;
				init_mySideCap_med = (init_mySideCap[(int) total_inits / 2] + init_mySideCap[(int) (total_inits / 2) - 1]) / 2;
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
				double[] balance_capStd = new double[totalBalances];
				double[] balance_capMin = new double[totalBalances];
				double[] balance_capMax = new double[totalBalances];
				double[] balance_mySideCap = new double[totalBalances];
				double[] balance_leftCapSum = new double[totalBalances];
				double[] balance_myEnmity = new double[totalBalances];
				double[] balance_oppEnmity = new double[totalBalances];

				int indexWorldSizeJoins = currentLevel - 3;

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (balanceProfiles[currentDeme][l] == true) {
						balance_myCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myCap;
						balance_oppCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideCap;
						balance_capRatio[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capRatio;
						balance_capMed[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMed;
						balance_capStd[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capStd;
						balance_capMin[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMin;
						balance_capMax[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMax;
						balance_mySideCap[totalBalances - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideCap;
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
				Arrays.sort(balance_capStd);
				Arrays.sort(balance_capMin);
				Arrays.sort(balance_capMax);
				Arrays.sort(balance_mySideCap);
				Arrays.sort(balance_leftCapSum);
				Arrays.sort(balance_myEnmity);
				Arrays.sort(balance_oppEnmity);

				balance_myCap_min = balance_myCap[0];
				balance_oppCap_min = balance_oppCap[0];
				balance_capRatio_min = balance_capRatio[0];
				balance_capMed_min = balance_capMed[0];
				balance_capStd_min = balance_capStd[0];
				balance_capMin_min = balance_capMin[0];
				balance_capMax_min = balance_capMax[0];
				balance_mySideCap_min = balance_mySideCap[0];
				balance_leftCapSum_min = balance_leftCapSum[0];
				balance_myEnmity_min = balance_myEnmity[0];
				balance_oppEnmity_min = balance_oppEnmity[0];

				even = total_balances % 2 == 0 ? true : false;
				if (!even) {
					balance_myCap_med = balance_myCap[(int) total_balances / 2];
					balance_oppCap_med = balance_oppCap[(int) total_balances / 2];
					balance_capRatio_med = balance_capRatio[(int) total_balances / 2];
					balance_capMed_med = balance_capMed[(int) total_balances / 2];
					balance_capStd_med = balance_capStd[(int) total_balances / 2];
					balance_capMin_med = balance_capMin[(int) total_balances / 2];
					balance_capMax_med = balance_capMax[(int) total_balances / 2];
					balance_mySideCap_med = balance_mySideCap[(int) total_balances / 2];
					balance_leftCapSum_med = balance_leftCapSum[(int) total_balances / 2];
					balance_myEnmity_med = balance_myEnmity[(int) total_balances / 2];
					balance_oppEnmity_med = balance_oppEnmity[(int) total_balances / 2];
				} else {
					balance_myCap_med = (balance_myCap[(int) total_balances / 2] + balance_myCap[(int) (total_balances / 2) - 1]) / 2;
					balance_oppCap_med = (balance_oppCap[(int) total_balances / 2] + balance_oppCap[(int) (total_balances / 2) - 1]) / 2;
					balance_capRatio_med = (balance_capRatio[(int) total_balances / 2] + balance_capRatio[(int) (total_balances / 2) - 1]) / 2;
					balance_capMed_med = (balance_capMed[(int) total_balances / 2] + balance_capMed[(int) (total_balances / 2) - 1]) / 2;
					balance_capStd_med = (balance_capStd[(int) total_balances / 2] + balance_capStd[(int) (total_balances / 2) - 1]) / 2;
					balance_capMin_med = (balance_capMin[(int) total_balances / 2] + balance_capMin[(int) (total_balances / 2) - 1]) / 2;
					balance_capMax_med = (balance_capMax[(int) total_balances / 2] + balance_capMax[(int) (total_balances / 2) - 1]) / 2;
					balance_mySideCap_med = (balance_mySideCap[(int) total_balances / 2] + balance_mySideCap[(int) (total_balances / 2) - 1]) / 2;
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
				double[] bandwagon_capStd = new double[totalBandwagons];
				double[] bandwagon_capMin = new double[totalBandwagons];
				double[] bandwagon_capMax = new double[totalBandwagons];
				double[] bandwagon_mySideCap = new double[totalBandwagons];
				double[] bandwagon_leftCapSum = new double[totalBandwagons];
				double[] bandwagon_myEnmity = new double[totalBandwagons];
				double[] bandwagon_oppEnmity = new double[totalBandwagons];

				int indexWorldSizeJoins = currentLevel - 3;

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (bandwagonProfiles[currentDeme][l] == true) {
						bandwagon_myCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myCap;
						bandwagon_oppCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideCap;
						bandwagon_capRatio[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capRatio;
						bandwagon_capMed[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMed;
						bandwagon_capStd[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capStd;
						bandwagon_capMin[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMin;
						bandwagon_capMax[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMax;
						bandwagon_mySideCap[totalBandwagons - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideCap;
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
				Arrays.sort(bandwagon_capStd);
				Arrays.sort(bandwagon_capMin);
				Arrays.sort(bandwagon_capMax);
				Arrays.sort(bandwagon_mySideCap);
				Arrays.sort(bandwagon_leftCapSum);
				Arrays.sort(bandwagon_myEnmity);
				Arrays.sort(bandwagon_oppEnmity);

				bandwagon_myCap_min = bandwagon_myCap[0];
				bandwagon_oppCap_min = bandwagon_oppCap[0];
				bandwagon_capRatio_min = bandwagon_capRatio[0];
				bandwagon_capMed_min = bandwagon_capMed[0];
				bandwagon_capStd_min = bandwagon_capStd[0];
				bandwagon_capMin_min = bandwagon_capMin[0];
				bandwagon_capMax_min = bandwagon_capMax[0];
				bandwagon_mySideCap_min = bandwagon_mySideCap[0];
				bandwagon_leftCapSum_min = bandwagon_leftCapSum[0];
				bandwagon_myEnmity_min = bandwagon_myEnmity[0];
				bandwagon_oppEnmity_min = bandwagon_oppEnmity[0];

				even = total_bandwagons % 2 == 0 ? true : false;
				if (!even) {
					bandwagon_myCap_med = bandwagon_myCap[(int) total_bandwagons / 2];
					bandwagon_oppCap_med = bandwagon_oppCap[(int) total_bandwagons / 2];
					bandwagon_capRatio_med = bandwagon_capRatio[(int) total_bandwagons / 2];
					bandwagon_capMed_med = bandwagon_capMed[(int) total_bandwagons / 2];
					bandwagon_capStd_med = bandwagon_capStd[(int) total_bandwagons / 2];
					bandwagon_capMin_med = bandwagon_capMin[(int) total_bandwagons / 2];
					bandwagon_capMax_med = bandwagon_capMax[(int) total_bandwagons / 2];
					bandwagon_mySideCap_med = bandwagon_mySideCap[(int) total_bandwagons / 2];
					bandwagon_leftCapSum_med = bandwagon_leftCapSum[(int) total_bandwagons / 2];
					bandwagon_myEnmity_med = bandwagon_myEnmity[(int) total_bandwagons / 2];
					bandwagon_oppEnmity_med = bandwagon_oppEnmity[(int) total_bandwagons / 2];
				} else {
					bandwagon_myCap_med = (bandwagon_myCap[(int) total_bandwagons / 2] + bandwagon_myCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_oppCap_med = (bandwagon_oppCap[(int) total_bandwagons / 2] + bandwagon_oppCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_capRatio_med = (bandwagon_capRatio[(int) total_bandwagons / 2] + bandwagon_capRatio[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_capMed_med = (bandwagon_capMed[(int) total_bandwagons / 2] + bandwagon_capMed[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_capStd_med = (bandwagon_capStd[(int) total_bandwagons / 2] + bandwagon_capStd[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_capMin_med = (bandwagon_capMin[(int) total_bandwagons / 2] + bandwagon_capMin[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_capMax_med = (bandwagon_capMax[(int) total_bandwagons / 2] + bandwagon_capMax[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_mySideCap_med = (bandwagon_mySideCap[(int) total_bandwagons / 2] + bandwagon_mySideCap[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_leftCapSum_med = (bandwagon_leftCapSum[(int) total_bandwagons / 2] + bandwagon_leftCapSum[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_myEnmity_med = (bandwagon_myEnmity[(int) total_bandwagons / 2] + bandwagon_myEnmity[(int) (total_bandwagons / 2) - 1]) / 2;
					bandwagon_oppEnmity_med = (bandwagon_oppEnmity[(int) total_bandwagons / 2] + bandwagon_oppEnmity[(int) (total_bandwagons / 2) - 1]) / 2;
				}
			}

			int totalbuckpasses = 0;
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (buckpassingProfiles[currentDeme][l] == true)
					totalbuckpasses++;
			}

			total_buckpasses = (double) totalbuckpasses;

			if (total_buckpasses > 0) {
				double[] buckpass_myCap = new double[totalbuckpasses];
				double[] buckpass_oppCap = new double[totalbuckpasses];
				double[] buckpass_capRatio = new double[totalbuckpasses];
				double[] buckpass_capMed = new double[totalbuckpasses];
				double[] buckpass_capStd = new double[totalbuckpasses];
				double[] buckpass_capMin = new double[totalbuckpasses];
				double[] buckpass_capMax = new double[totalbuckpasses];
				double[] buckpass_mySideCap = new double[totalbuckpasses];
				double[] buckpass_leftCapSum = new double[totalbuckpasses];
				double[] buckpass_myEnmity = new double[totalbuckpasses];
				double[] buckpass_oppEnmity = new double[totalbuckpasses];

				int indexWorldSizeJoins = currentLevel - 3;

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (buckpassingProfiles[currentDeme][l] == true) {
						buckpass_myCap[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myCap;
						buckpass_oppCap[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].smallerSideCap;
						buckpass_capRatio[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capRatio;
						buckpass_capMed[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMed;
						buckpass_capStd[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capStd;
						buckpass_capMin[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMin;
						buckpass_capMax[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].capMax;
						buckpass_mySideCap[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].largerSideCap;
						buckpass_leftCapSum[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].leftCapSum;
						buckpass_myEnmity[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].myEnmity;
						buckpass_oppEnmity[totalbuckpasses - 1] = profilingWorlds_Joins[indexWorldSizeJoins][l].oppEnmity;
						totalbuckpasses--;
					}
				}

				Arrays.sort(buckpass_myCap);
				Arrays.sort(buckpass_oppCap);
				Arrays.sort(buckpass_capRatio);
				Arrays.sort(buckpass_capMed);
				Arrays.sort(buckpass_capStd);
				Arrays.sort(buckpass_capMin);
				Arrays.sort(buckpass_capMax);
				Arrays.sort(buckpass_mySideCap);
				Arrays.sort(buckpass_leftCapSum);
				Arrays.sort(buckpass_myEnmity);
				Arrays.sort(buckpass_oppEnmity);

				buckpass_myCap_min = buckpass_myCap[0];
				buckpass_oppCap_min = buckpass_oppCap[0];
				buckpass_capRatio_min = buckpass_capRatio[0];
				buckpass_capMed_min = buckpass_capMed[0];
				buckpass_capStd_min = buckpass_capStd[0];
				buckpass_capMin_min = buckpass_capMin[0];
				buckpass_capMax_min = buckpass_capMax[0];
				buckpass_mySideCap_min = buckpass_mySideCap[0];
				buckpass_leftCapSum_min = buckpass_leftCapSum[0];
				buckpass_myEnmity_min = buckpass_myEnmity[0];
				buckpass_oppEnmity_min = buckpass_oppEnmity[0];

				even = total_buckpasses % 2 == 0 ? true : false;
				if (!even) {
					buckpass_myCap_med = buckpass_myCap[(int) total_buckpasses / 2];
					buckpass_oppCap_med = buckpass_oppCap[(int) total_buckpasses / 2];
					buckpass_capRatio_med = buckpass_capRatio[(int) total_buckpasses / 2];
					buckpass_capMed_med = buckpass_capMed[(int) total_buckpasses / 2];
					buckpass_capStd_med = buckpass_capStd[(int) total_buckpasses / 2];
					buckpass_capMin_med = buckpass_capMin[(int) total_buckpasses / 2];
					buckpass_capMax_med = buckpass_capMax[(int) total_buckpasses / 2];
					buckpass_mySideCap_med = buckpass_mySideCap[(int) total_buckpasses / 2];
					buckpass_leftCapSum_med = buckpass_leftCapSum[(int) total_buckpasses / 2];
					buckpass_myEnmity_med = buckpass_myEnmity[(int) total_buckpasses / 2];
					buckpass_oppEnmity_med = buckpass_oppEnmity[(int) total_buckpasses / 2];
				} else {
					buckpass_myCap_med = (buckpass_myCap[(int) total_buckpasses / 2] + buckpass_myCap[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_oppCap_med = (buckpass_oppCap[(int) total_buckpasses / 2] + buckpass_oppCap[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_capRatio_med = (buckpass_capRatio[(int) total_buckpasses / 2] + buckpass_capRatio[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_capMed_med = (buckpass_capMed[(int) total_buckpasses / 2] + buckpass_capMed[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_capStd_med = (buckpass_capStd[(int) total_buckpasses / 2] + buckpass_capStd[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_capMin_med = (buckpass_capMin[(int) total_buckpasses / 2] + buckpass_capMin[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_capMax_med = (buckpass_capMax[(int) total_buckpasses / 2] + buckpass_capMax[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_mySideCap_med = (buckpass_mySideCap[(int) total_buckpasses / 2] + buckpass_mySideCap[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_leftCapSum_med = (buckpass_leftCapSum[(int) total_buckpasses / 2] + buckpass_leftCapSum[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_myEnmity_med = (buckpass_myEnmity[(int) total_buckpasses / 2] + buckpass_myEnmity[(int) (total_buckpasses / 2) - 1]) / 2;
					buckpass_oppEnmity_med = (buckpass_oppEnmity[(int) total_buckpasses / 2] + buckpass_oppEnmity[(int) (total_buckpasses / 2) - 1]) / 2;
				}
			}
		}
	}

	private static void printProfileVariablesToFile() {
		
		int stateNumber = currentDeme + 1;

		outputString += (currentSimulation + "," + stateNumber + "," + currentGen + "," + currentLevel + ","
				+ total_inits + ",");
		if (currentLevel == 2)
			outputString += ("." + "," + "." + "," + ".");
		else
			outputString += (total_balances + "," + total_bandwagons + "," + total_buckpasses);

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
				outputString += ("," + calcSimilarity(buckpassingProfiles[currentDeme], buckpassingProfiles[j - 1]));
		}

		outputString += ("," + calcGenSimilarity(attackProfiles));

		if (currentLevel == 2)
			outputString += ("," + "." + "," + "." + "," + ".");
		else {
			outputString += ("," + calcGenSimilarity(balanceProfiles) + "," + calcGenSimilarity(bandwagonProfiles)
					+ "," + calcGenSimilarity(buckpassingProfiles));
		}

		outputString += ("," + init_myCap_min + "," + init_myCap_med + "," + init_oppCap_min + "," + init_oppCap_med
				+ "," + init_capRatio_min + "," + init_capRatio_med + "," + init_capMed_min + "," + init_capMed_med
				+ "," + init_capStd_min + "," + init_capStd_med + "," + init_capMin_min + "," + init_capMin_med + ","
				+ init_capMax_min + "," + init_capMax_med + "," + init_mySideCap_min + "," + init_mySideCap_med + ","
				+ init_leftCapSum_min + "," + init_leftCapSum_med + "," + init_myEnmity_min + "," + init_myEnmity_med
				+ "," + init_oppEnmity_min + "," + init_oppEnmity_med);

		if (currentLevel > 2) {
			outputString += ("," + balance_myCap_min + "," + balance_myCap_med + "," + balance_oppCap_min + ","
					+ balance_oppCap_med + "," + balance_capRatio_min + "," + balance_capRatio_med + ","
					+ balance_capMed_min + "," + balance_capMed_med + "," + balance_capStd_min + ","
					+ balance_capStd_med + "," + balance_capMin_min + "," + balance_capMin_med + ","
					+ balance_capMax_min + "," + balance_capMax_med + "," + balance_mySideCap_min + ","
					+ balance_mySideCap_med + "," + balance_leftCapSum_min + "," + balance_leftCapSum_med + ","
					+ balance_myEnmity_min + "," + balance_myEnmity_med + "," + balance_oppEnmity_min + ","
					+ balance_oppEnmity_med + "," + bandwagon_myCap_min + "," + bandwagon_myCap_med + ","
					+ bandwagon_oppCap_min + "," + bandwagon_oppCap_med + "," + bandwagon_capRatio_min + ","
					+ bandwagon_capRatio_med + "," + bandwagon_capMed_min + "," + bandwagon_capMed_med + ","
					+ bandwagon_capStd_min + "," + bandwagon_capStd_med + "," + bandwagon_capMin_min + ","
					+ bandwagon_capMin_med + "," + bandwagon_capMax_min + "," + bandwagon_capMax_med + ","
					+ bandwagon_mySideCap_min + "," + bandwagon_mySideCap_med + "," + bandwagon_leftCapSum_min + ","
					+ bandwagon_leftCapSum_med + "," + bandwagon_myEnmity_min + "," + bandwagon_myEnmity_med + ","
					+ bandwagon_oppEnmity_min + "," + bandwagon_oppEnmity_med + "," + buckpass_myCap_min + ","
					+ buckpass_myCap_med + "," + buckpass_oppCap_min + "," + buckpass_oppCap_med + ","
					+ buckpass_capRatio_min + "," + buckpass_capRatio_med + "," + buckpass_capMed_min + ","
					+ buckpass_capMed_med + "," + buckpass_capStd_min + "," + buckpass_capStd_med + ","
					+ buckpass_capMin_min + "," + buckpass_capMin_med + "," + buckpass_capMax_min + ","
					+ buckpass_capMax_med + "," + buckpass_mySideCap_min + "," + buckpass_mySideCap_med + ","
					+ buckpass_leftCapSum_min + "," + buckpass_leftCapSum_med + "," + buckpass_myEnmity_min + ","
					+ buckpass_myEnmity_med + "," + buckpass_oppEnmity_min + "," + buckpass_oppEnmity_med);
		}

		if (currentGen % 8 != 0 || !(currentLevel == MasterVariables.MAXSYSTEM && currentDeme == currentLevel - 1)) {
			MasterVariables.state_profiles_writer.writeToFile(outputString + "\n");
			outputString = "";
		}
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

		similarity = (double) similarity / (double) PROFILES_PER_CAT;

		return (similarity);
	}

	private static void printProfileWorlds() {
		if (currentGen % 20 != 0 || !(currentLevel == MasterVariables.MAXSYSTEM && currentDeme == currentLevel - 1))
			return;

		try {
			MasterVariables.profiles_count_writer = new WriteFile("profiles_count.txt", false);
			MasterVariables.profiles_count_writer.writeToFile("generation" + "," + "profile_no" + ","
					+ "simulate_Joiner" + "," + "world_size" + "," + "capMed" + "," + "capStd" + "," + "capMin" + ","
					+ "capMax" + "," + "myCap" + "," + "oppCap-sideAcap" + "," + "capRatio-sideAtoBratio" + ","
					+ "mySideCap-sideBcap" + "," + "leftCapSum" + "," + "myEnmity" + "," + "oppEnmity" + ","
					+ "totalProfileAttacks" + "," + "totalProfileBalances" + "," + "totalProfileBandwagons" + ","
					+ "totalProfileBuckpasses" + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String outputString = "";
		for (int i = 0; i < MasterVariables.uniqueWorldSizes; i++) {
			for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
				outputString += (currentGen + "," + (k + 1) + "," + "0" + "," + (i + MasterVariables.MINSYSTEM) + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].capMed + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].capStd + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].capMin + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].capMax + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].myCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].oppCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].capRatio + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].mySideCap + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].leftCapSum + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].myEnmity + ","
						+ MasterVariables.profilingWorlds_Attacks[i][k].oppEnmity + ","
						+ MasterVariables.totalProfileAttacks[i][k] + "," + "." + "," + "." + "," + "." + "\n");
			}
			if (i > 0)
				for (int k = 0; k < MasterVariables.PROFILES_PER_CAT; k++) {
					outputString += (currentGen + "," + (k + 1) + "," + "1" + "," + (i + MasterVariables.MINSYSTEM)
							+ "," + MasterVariables.profilingWorlds_Joins[i - 1][k].capMed + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].capStd + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].capMin + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].capMax + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].myCap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].sideAcap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].capRatio + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].sideBcap + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].leftCapSum + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].myEnmity + ","
							+ MasterVariables.profilingWorlds_Joins[i - 1][k].oppEnmity + "," + "." + ","
							+ MasterVariables.totalProfileBalances[i - 1][k] + ","
							+ MasterVariables.totalProfileBandwagons[i - 1][k] + ","
							+ MasterVariables.totalProfileBuckpasses[i - 1][k] + "\n");
				}
		}
		MasterVariables.profiles_count_writer.writeToFile(outputString);
	}

	private void checkErrors() {
		if (currentLevel < 2
				|| attackProfiles == null
				|| (currentLevel > 2 && (balanceProfiles == null || bandwagonProfiles == null || buckpassingProfiles == null))) {
			System.out.println("StatisticsPrinter class erorr!!!");
			System.exit(0);
		}

	}

}
