package v1;


import java.util.Arrays;
import java.util.concurrent.Callable;



public class StatisticsPrinter implements Callable<Void>{

	static double[] total_inits, total_balances, total_bandwagons, total_buckpasses, init_myCap_min, init_myCap_med,
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

	static int currentSimulation, currentGen, PROFILES_PER_CAT, minLevel, maxLevel;
	static LevelStrategy[] bestLevelStrategies;

	static boolean[][] attackProfiles, balanceProfiles, bandwagonProfiles, buckpassingProfiles;
	static World_System_Shell[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static boolean printInProgress;

	StatisticsPrinter(LevelStrategy[] bestLevelStrategies, int currentSimulation, int currentGen) {
		if (printInProgress){
			System.out.println("Error StatisticsPrinter class !!! Print in progress");
			System.exit(0);
		}
		printInProgress=true;
		this.bestLevelStrategies = sortBySize(bestLevelStrategies);
		this.currentSimulation = currentSimulation;
		this.currentGen = currentGen;
		minLevel = bestLevelStrategies[0].level;
		maxLevel = bestLevelStrategies[bestLevelStrategies.length - 1].level;
		PROFILES_PER_CAT = MasterVariables.PROFILES_PER_CAT;
		this.profilingWorlds_Attacks = MasterVariables.profilingWorlds_Attacks;
		this.profilingWorlds_Joins = MasterVariables.profilingWorlds_Joins;
		initializeArrays(bestLevelStrategies.length);
		checkErrors();
	}

	public Void call() {		
		
		
		populateProfiles();
		calculateProfileVariables();
		printProfileVariablesToFile();
		
		printInProgress=false;
		return null;		
	}

	private static void populateProfiles() {
		attackProfiles = new boolean[bestLevelStrategies.length][PROFILES_PER_CAT];

		for (int i = 0; i < attackProfiles.length; i++) {
			int indexWorldSizeAttacks = bestLevelStrategies[i].level - minLevel;
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				attackProfiles[i][l] = profilingWorlds_Attacks[indexWorldSizeAttacks][l].willItAttack(
						bestLevelStrategies[i].init_strategy, 0);
			}
		}

		if (maxLevel > 2) {
			balanceProfiles = new boolean[bestLevelStrategies.length][PROFILES_PER_CAT];
			bandwagonProfiles = new boolean[bestLevelStrategies.length][PROFILES_PER_CAT];
			buckpassingProfiles = new boolean[bestLevelStrategies.length][PROFILES_PER_CAT];
			for (int i = 0; i < attackProfiles.length; i++) {
				if (bestLevelStrategies[i].level == 2)
					continue;
				int indexWorldSizeJoins = bestLevelStrategies[i].level - minLevel - 1;
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					balanceProfiles[i][l] = profilingWorlds_Joins[indexWorldSizeJoins][l].willItAttack(
							bestLevelStrategies[i].balance_strategy, 1);
					bandwagonProfiles[i][l] = profilingWorlds_Joins[indexWorldSizeJoins][l].willItAttack(
							bestLevelStrategies[i].bandwagon_strategy, 2);
					if (!balanceProfiles[i][l] && !bandwagonProfiles[i][l])
						buckpassingProfiles[i][l] = true;
				}
			}
		}
	}

	private static void calculateProfileVariables() {
		for (int index = 0; index < bestLevelStrategies.length; index++) {
			boolean even;

			int totalAttacks = 0;
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (attackProfiles[index][l] == true)
					totalAttacks++;
			}

			total_inits[index] = (double) totalAttacks;

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

				int indexWorldSizeAttacks = bestLevelStrategies[index].level - minLevel;

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (attackProfiles[index][l] == true) {
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

				init_myCap_min[index] = init_myCap[0];
				init_oppCap_min[index] = init_oppCap[0];
				init_capRatio_min[index] = init_capRatio[0];
				init_capMed_min[index] = init_capMed[0];
				init_capStd_min[index] = init_capStd[0];
				init_capMin_min[index] = init_capMin[0];
				init_capMax_min[index] = init_capMax[0];
				init_mySideCap_min[index] = init_mySideCap[0];
				init_leftCapSum_min[index] = init_leftCapSum[0];
				init_myEnmity_min[index] = init_myEnmity[0];
				init_oppEnmity_min[index] = init_oppEnmity[0];

				even = total_inits[index] % 2 == 0 ? true : false;
				if (!even) {
					init_myCap_med[index] = init_myCap[(int) total_inits[index] / 2];
					init_oppCap_med[index] = init_oppCap[(int) total_inits[index] / 2];
					init_capRatio_med[index] = init_capRatio[(int) total_inits[index] / 2];
					init_capMed_med[index] = init_capMed[(int) total_inits[index] / 2];
					init_capStd_med[index] = init_capStd[(int) total_inits[index] / 2];
					init_capMin_med[index] = init_capMin[(int) total_inits[index] / 2];
					init_capMax_med[index] = init_capMax[(int) total_inits[index] / 2];
					init_mySideCap_med[index] = init_mySideCap[(int) total_inits[index] / 2];
					init_leftCapSum_med[index] = init_leftCapSum[(int) total_inits[index] / 2];
					init_myEnmity_med[index] = init_myEnmity[(int) total_inits[index] / 2];
					init_oppEnmity_med[index] = init_oppEnmity[(int) total_inits[index] / 2];
				} else {
					init_myCap_med[index] = (init_myCap[(int) total_inits[index] / 2] + init_myCap[(int) (total_inits[index] / 2) - 1]) / 2;
					init_oppCap_med[index] = (init_oppCap[(int) total_inits[index] / 2] + init_oppCap[(int) (total_inits[index] / 2) - 1]) / 2;
					init_capRatio_med[index] = (init_capRatio[(int) total_inits[index] / 2] + init_capRatio[(int) (total_inits[index] / 2) - 1]) / 2;
					init_capMed_med[index] = (init_capMed[(int) total_inits[index] / 2] + init_capMed[(int) (total_inits[index] / 2) - 1]) / 2;
					init_capStd_med[index] = (init_capStd[(int) total_inits[index] / 2] + init_capStd[(int) (total_inits[index] / 2) - 1]) / 2;
					init_capMin_med[index] = (init_capMin[(int) total_inits[index] / 2] + init_capMin[(int) (total_inits[index] / 2) - 1]) / 2;
					init_capMax_med[index] = (init_capMax[(int) total_inits[index] / 2] + init_capMax[(int) (total_inits[index] / 2) - 1]) / 2;
					init_mySideCap_med[index] = (init_mySideCap[(int) total_inits[index] / 2] + init_mySideCap[(int) (total_inits[index] / 2) - 1]) / 2;
					init_leftCapSum_med[index] = (init_leftCapSum[(int) total_inits[index] / 2] + init_leftCapSum[(int) (total_inits[index] / 2) - 1]) / 2;
					init_myEnmity_med[index] = (init_myEnmity[(int) total_inits[index] / 2] + init_myEnmity[(int) (total_inits[index] / 2) - 1]) / 2;
					init_oppEnmity_med[index] = (init_oppEnmity[(int) total_inits[index] / 2] + init_oppEnmity[(int) (total_inits[index] / 2) - 1]) / 2;
				}
			}

			if (bestLevelStrategies[index].level > 2) {

				int totalBalances = 0;
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (balanceProfiles[index][l] == true)
						totalBalances++;
				}

				total_balances[index] = (double) totalBalances;

				if (total_balances[index] > 0) {

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

					int indexWorldSizeJoins = bestLevelStrategies[index].level - minLevel - 1;

					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (balanceProfiles[index][l] == true) {
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

					balance_myCap_min[index] = balance_myCap[0];
					balance_oppCap_min[index] = balance_oppCap[0];
					balance_capRatio_min[index] = balance_capRatio[0];
					balance_capMed_min[index] = balance_capMed[0];
					balance_capStd_min[index] = balance_capStd[0];
					balance_capMin_min[index] = balance_capMin[0];
					balance_capMax_min[index] = balance_capMax[0];
					balance_mySideCap_min[index] = balance_mySideCap[0];
					balance_leftCapSum_min[index] = balance_leftCapSum[0];
					balance_myEnmity_min[index] = balance_myEnmity[0];
					balance_oppEnmity_min[index] = balance_oppEnmity[0];

					even = total_balances[index] % 2 == 0 ? true : false;
					if (!even) {
						balance_myCap_med[index] = balance_myCap[(int) total_balances[index] / 2];
						balance_oppCap_med[index] = balance_oppCap[(int) total_balances[index] / 2];
						balance_capRatio_med[index] = balance_capRatio[(int) total_balances[index] / 2];
						balance_capMed_med[index] = balance_capMed[(int) total_balances[index] / 2];
						balance_capStd_med[index] = balance_capStd[(int) total_balances[index] / 2];
						balance_capMin_med[index] = balance_capMin[(int) total_balances[index] / 2];
						balance_capMax_med[index] = balance_capMax[(int) total_balances[index] / 2];
						balance_mySideCap_med[index] = balance_mySideCap[(int) total_balances[index] / 2];
						balance_leftCapSum_med[index] = balance_leftCapSum[(int) total_balances[index] / 2];
						balance_myEnmity_med[index] = balance_myEnmity[(int) total_balances[index] / 2];
						balance_oppEnmity_med[index] = balance_oppEnmity[(int) total_balances[index] / 2];
					} else {
						balance_myCap_med[index] = (balance_myCap[(int) total_balances[index] / 2] + balance_myCap[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_oppCap_med[index] = (balance_oppCap[(int) total_balances[index] / 2] + balance_oppCap[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_capRatio_med[index] = (balance_capRatio[(int) total_balances[index] / 2] + balance_capRatio[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_capMed_med[index] = (balance_capMed[(int) total_balances[index] / 2] + balance_capMed[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_capStd_med[index] = (balance_capStd[(int) total_balances[index] / 2] + balance_capStd[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_capMin_med[index] = (balance_capMin[(int) total_balances[index] / 2] + balance_capMin[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_capMax_med[index] = (balance_capMax[(int) total_balances[index] / 2] + balance_capMax[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_mySideCap_med[index] = (balance_mySideCap[(int) total_balances[index] / 2] + balance_mySideCap[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_leftCapSum_med[index] = (balance_leftCapSum[(int) total_balances[index] / 2] + balance_leftCapSum[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_myEnmity_med[index] = (balance_myEnmity[(int) total_balances[index] / 2] + balance_myEnmity[(int) (total_balances[index] / 2) - 1]) / 2;
						balance_oppEnmity_med[index] = (balance_oppEnmity[(int) total_balances[index] / 2] + balance_oppEnmity[(int) (total_balances[index] / 2) - 1]) / 2;
					}
				}

				int totalBandwagons = 0;
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (bandwagonProfiles[index][l] == true)
						totalBandwagons++;
				}

				total_bandwagons[index] = (double) totalBandwagons;

				if (total_bandwagons[index] > 0) {
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

					int indexWorldSizeJoins = bestLevelStrategies[index].level - minLevel - 1;

					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (bandwagonProfiles[index][l] == true) {
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

					bandwagon_myCap_min[index] = bandwagon_myCap[0];
					bandwagon_oppCap_min[index] = bandwagon_oppCap[0];
					bandwagon_capRatio_min[index] = bandwagon_capRatio[0];
					bandwagon_capMed_min[index] = bandwagon_capMed[0];
					bandwagon_capStd_min[index] = bandwagon_capStd[0];
					bandwagon_capMin_min[index] = bandwagon_capMin[0];
					bandwagon_capMax_min[index] = bandwagon_capMax[0];
					bandwagon_mySideCap_min[index] = bandwagon_mySideCap[0];
					bandwagon_leftCapSum_min[index] = bandwagon_leftCapSum[0];
					bandwagon_myEnmity_min[index] = bandwagon_myEnmity[0];
					bandwagon_oppEnmity_min[index] = bandwagon_oppEnmity[0];

					even = total_bandwagons[index] % 2 == 0 ? true : false;
					if (!even) {
						bandwagon_myCap_med[index] = bandwagon_myCap[(int) total_bandwagons[index] / 2];
						bandwagon_oppCap_med[index] = bandwagon_oppCap[(int) total_bandwagons[index] / 2];
						bandwagon_capRatio_med[index] = bandwagon_capRatio[(int) total_bandwagons[index] / 2];
						bandwagon_capMed_med[index] = bandwagon_capMed[(int) total_bandwagons[index] / 2];
						bandwagon_capStd_med[index] = bandwagon_capStd[(int) total_bandwagons[index] / 2];
						bandwagon_capMin_med[index] = bandwagon_capMin[(int) total_bandwagons[index] / 2];
						bandwagon_capMax_med[index] = bandwagon_capMax[(int) total_bandwagons[index] / 2];
						bandwagon_mySideCap_med[index] = bandwagon_mySideCap[(int) total_bandwagons[index] / 2];
						bandwagon_leftCapSum_med[index] = bandwagon_leftCapSum[(int) total_bandwagons[index] / 2];
						bandwagon_myEnmity_med[index] = bandwagon_myEnmity[(int) total_bandwagons[index] / 2];
						bandwagon_oppEnmity_med[index] = bandwagon_oppEnmity[(int) total_bandwagons[index] / 2];
					} else {
						bandwagon_myCap_med[index] = (bandwagon_myCap[(int) total_bandwagons[index] / 2] + bandwagon_myCap[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_oppCap_med[index] = (bandwagon_oppCap[(int) total_bandwagons[index] / 2] + bandwagon_oppCap[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_capRatio_med[index] = (bandwagon_capRatio[(int) total_bandwagons[index] / 2] + bandwagon_capRatio[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_capMed_med[index] = (bandwagon_capMed[(int) total_bandwagons[index] / 2] + bandwagon_capMed[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_capStd_med[index] = (bandwagon_capStd[(int) total_bandwagons[index] / 2] + bandwagon_capStd[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_capMin_med[index] = (bandwagon_capMin[(int) total_bandwagons[index] / 2] + bandwagon_capMin[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_capMax_med[index] = (bandwagon_capMax[(int) total_bandwagons[index] / 2] + bandwagon_capMax[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_mySideCap_med[index] = (bandwagon_mySideCap[(int) total_bandwagons[index] / 2] + bandwagon_mySideCap[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_leftCapSum_med[index] = (bandwagon_leftCapSum[(int) total_bandwagons[index] / 2] + bandwagon_leftCapSum[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_myEnmity_med[index] = (bandwagon_myEnmity[(int) total_bandwagons[index] / 2] + bandwagon_myEnmity[(int) (total_bandwagons[index] / 2) - 1]) / 2;
						bandwagon_oppEnmity_med[index] = (bandwagon_oppEnmity[(int) total_bandwagons[index] / 2] + bandwagon_oppEnmity[(int) (total_bandwagons[index] / 2) - 1]) / 2;
					}
				}

				int totalbuckpasses = 0;
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (buckpassingProfiles[index][l] == true)
						totalbuckpasses++;
				}

				total_buckpasses[index] = (double) totalbuckpasses;

				if (total_buckpasses[index] > 0) {
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

					int indexWorldSizeJoins = bestLevelStrategies[index].level - minLevel - 1;

					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (buckpassingProfiles[index][l] == true) {
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

					buckpass_myCap_min[index] = buckpass_myCap[0];
					buckpass_oppCap_min[index] = buckpass_oppCap[0];
					buckpass_capRatio_min[index] = buckpass_capRatio[0];
					buckpass_capMed_min[index] = buckpass_capMed[0];
					buckpass_capStd_min[index] = buckpass_capStd[0];
					buckpass_capMin_min[index] = buckpass_capMin[0];
					buckpass_capMax_min[index] = buckpass_capMax[0];
					buckpass_mySideCap_min[index] = buckpass_mySideCap[0];
					buckpass_leftCapSum_min[index] = buckpass_leftCapSum[0];
					buckpass_myEnmity_min[index] = buckpass_myEnmity[0];
					buckpass_oppEnmity_min[index] = buckpass_oppEnmity[0];

					even = total_buckpasses[index] % 2 == 0 ? true : false;
					if (!even) {
						buckpass_myCap_med[index] = buckpass_myCap[(int) total_buckpasses[index] / 2];
						buckpass_oppCap_med[index] = buckpass_oppCap[(int) total_buckpasses[index] / 2];
						buckpass_capRatio_med[index] = buckpass_capRatio[(int) total_buckpasses[index] / 2];
						buckpass_capMed_med[index] = buckpass_capMed[(int) total_buckpasses[index] / 2];
						buckpass_capStd_med[index] = buckpass_capStd[(int) total_buckpasses[index] / 2];
						buckpass_capMin_med[index] = buckpass_capMin[(int) total_buckpasses[index] / 2];
						buckpass_capMax_med[index] = buckpass_capMax[(int) total_buckpasses[index] / 2];
						buckpass_mySideCap_med[index] = buckpass_mySideCap[(int) total_buckpasses[index] / 2];
						buckpass_leftCapSum_med[index] = buckpass_leftCapSum[(int) total_buckpasses[index] / 2];
						buckpass_myEnmity_med[index] = buckpass_myEnmity[(int) total_buckpasses[index] / 2];
						buckpass_oppEnmity_med[index] = buckpass_oppEnmity[(int) total_buckpasses[index] / 2];
					} else {
						buckpass_myCap_med[index] = (buckpass_myCap[(int) total_buckpasses[index] / 2] + buckpass_myCap[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_oppCap_med[index] = (buckpass_oppCap[(int) total_buckpasses[index] / 2] + buckpass_oppCap[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_capRatio_med[index] = (buckpass_capRatio[(int) total_buckpasses[index] / 2] + buckpass_capRatio[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_capMed_med[index] = (buckpass_capMed[(int) total_buckpasses[index] / 2] + buckpass_capMed[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_capStd_med[index] = (buckpass_capStd[(int) total_buckpasses[index] / 2] + buckpass_capStd[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_capMin_med[index] = (buckpass_capMin[(int) total_buckpasses[index] / 2] + buckpass_capMin[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_capMax_med[index] = (buckpass_capMax[(int) total_buckpasses[index] / 2] + buckpass_capMax[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_mySideCap_med[index] = (buckpass_mySideCap[(int) total_buckpasses[index] / 2] + buckpass_mySideCap[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_leftCapSum_med[index] = (buckpass_leftCapSum[(int) total_buckpasses[index] / 2] + buckpass_leftCapSum[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_myEnmity_med[index] = (buckpass_myEnmity[(int) total_buckpasses[index] / 2] + buckpass_myEnmity[(int) (total_buckpasses[index] / 2) - 1]) / 2;
						buckpass_oppEnmity_med[index] = (buckpass_oppEnmity[(int) total_buckpasses[index] / 2] + buckpass_oppEnmity[(int) (total_buckpasses[index] / 2) - 1]) / 2;
					}
				}
			}
		}
	}

	private static void printProfileVariablesToFile() {
		String outputString;
		int prevSize = -99, stateNumber = 0;
		for (int i = 0; i < bestLevelStrategies.length; i++) {
			if (bestLevelStrategies[i].level != prevSize) {
				prevSize = bestLevelStrategies[i].level;
				stateNumber = 0;
			}
			stateNumber++;
			outputString = (currentSimulation + "," + stateNumber + "," + currentGen + ","
					+ bestLevelStrategies[i].level + "," + total_inits[i] + ",");
			if (bestLevelStrategies[i].level == 2)
				outputString += ("." + "," + "." + "," + ".");
			else
				outputString += (total_balances[i] + "," + total_bandwagons[i] + "," + total_buckpasses[i]);

			for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
				if (stateNumber - 1 == j || j >= bestLevelStrategies[i].level)
					outputString += ("," + ".");
				else
					outputString += ("," + calcSimilarity(attackProfiles[i], attackProfiles[j]));
			}

			for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
				if (stateNumber - 1 == j || j >= bestLevelStrategies[i].level || maxLevel == 2)
					outputString += ("," + ".");
				else
					outputString += ("," + calcSimilarity(balanceProfiles[i], balanceProfiles[j]));
			}

			for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
				if (stateNumber - 1 == j || j >= bestLevelStrategies[i].level || maxLevel == 2)
					outputString += ("," + ".");
				else
					outputString += ("," + calcSimilarity(bandwagonProfiles[i], bandwagonProfiles[j]));
			}

			for (int j = 0; j < MasterVariables.MAXSYSTEM; j++) {
				if (stateNumber - 1 == j || j >= bestLevelStrategies[i].level || maxLevel == 2)
					outputString += ("," + ".");
				else
					outputString += ("," + calcSimilarity(buckpassingProfiles[i], buckpassingProfiles[j]));
			}

			outputString += ("," + init_myCap_min[i] + "," + init_myCap_med[i] + "," + init_oppCap_min[i] + ","
					+ init_oppCap_med[i] + "," + init_capRatio_min[i] + "," + init_capRatio_med[i] + ","
					+ init_capMed_min[i] + "," + init_capMed_med[i] + "," + init_capStd_min[i] + ","
					+ init_capStd_med[i] + "," + init_capMin_min[i] + "," + init_capMin_med[i] + ","
					+ init_capMax_min[i] + "," + init_capMax_med[i] + "," + init_mySideCap_min[i] + ","
					+ init_mySideCap_med[i] + "," + init_leftCapSum_min[i] + "," + init_leftCapSum_med[i] + ","
					+ init_myEnmity_min[i] + "," + init_myEnmity_med[i] + "," + init_oppEnmity_min[i] + "," + init_oppEnmity_med[i]);

			if (maxLevel > 2) {
				outputString += ("," + balance_myCap_min[i] + "," + balance_myCap_med[i] + "," + balance_oppCap_min[i]
						+ "," + balance_oppCap_med[i] + "," + balance_capRatio_min[i] + "," + balance_capRatio_med[i]
						+ "," + balance_capMed_min[i] + "," + balance_capMed_med[i] + "," + balance_capStd_min[i] + ","
						+ balance_capStd_med[i] + "," + balance_capMin_min[i] + "," + balance_capMin_med[i] + ","
						+ balance_capMax_min[i] + "," + balance_capMax_med[i] + "," + balance_mySideCap_min[i] + ","
						+ balance_mySideCap_med[i] + "," + balance_leftCapSum_min[i] + "," + balance_leftCapSum_med[i]
						+ "," + balance_myEnmity_min[i] + "," + balance_myEnmity_med[i] + ","
						+ balance_oppEnmity_min[i] + "," + balance_oppEnmity_med[i] + "," + bandwagon_myCap_min[i]
						+ "," + bandwagon_myCap_med[i] + "," + bandwagon_oppCap_min[i] + "," + bandwagon_oppCap_med[i]
						+ "," + bandwagon_capRatio_min[i] + "," + bandwagon_capRatio_med[i] + ","
						+ bandwagon_capMed_min[i] + "," + bandwagon_capMed_med[i] + "," + bandwagon_capStd_min[i] + ","
						+ bandwagon_capStd_med[i] + "," + bandwagon_capMin_min[i] + "," + bandwagon_capMin_med[i] + ","
						+ bandwagon_capMax_min[i] + "," + bandwagon_capMax_med[i] + "," + bandwagon_mySideCap_min[i]
						+ "," + bandwagon_mySideCap_med[i] + "," + bandwagon_leftCapSum_min[i] + ","
						+ bandwagon_leftCapSum_med[i] + "," + bandwagon_myEnmity_min[i] + ","
						+ bandwagon_myEnmity_med[i] + "," + bandwagon_oppEnmity_min[i] + ","
						+ bandwagon_oppEnmity_med[i] + "," + buckpass_myCap_min[i] + "," + buckpass_myCap_med[i] + ","
						+ buckpass_oppCap_min[i] + "," + buckpass_oppCap_med[i] + "," + buckpass_capRatio_min[i] + ","
						+ buckpass_capRatio_med[i] + "," + buckpass_capMed_min[i] + "," + buckpass_capMed_med[i] + ","
						+ buckpass_capStd_min[i] + "," + buckpass_capStd_med[i] + "," + buckpass_capMin_min[i] + ","
						+ buckpass_capMin_med[i] + "," + buckpass_capMax_min[i] + "," + buckpass_capMax_med[i] + ","
						+ buckpass_mySideCap_min[i] + "," + buckpass_mySideCap_med[i] + ","
						+ buckpass_leftCapSum_min[i] + "," + buckpass_leftCapSum_med[i] + ","
						+ buckpass_myEnmity_min[i] + "," + buckpass_myEnmity_med[i] + "," + buckpass_oppEnmity_min[i]
						+ "," + buckpass_oppEnmity_med[i]);
			}
			MasterVariables.state_profiles_writer.writeToFile(outputString + "\n");
		}
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

	private LevelStrategy[] sortBySize(LevelStrategy[] bestLevelStrategies) {
		LevelStrategy[] sortedLevelStrategies = new LevelStrategy[bestLevelStrategies.length];
		int[] levels = new int[bestLevelStrategies.length];
		boolean[] takenStrategy = new boolean[bestLevelStrategies.length];
		for (int i = 0; i < bestLevelStrategies.length; i++) {
			levels[i] = bestLevelStrategies[i].level;
			takenStrategy[i] = false;
		}
		Arrays.sort(levels);
		for (int i = 0; i < bestLevelStrategies.length; i++) {
			for (int k = 0; k < bestLevelStrategies.length; k++) {
				if (bestLevelStrategies[k].level == levels[i] && takenStrategy[k] == false) {
					sortedLevelStrategies[i] = bestLevelStrategies[k];
					takenStrategy[k] = true;
					break;
				}
			}
		}
		return sortedLevelStrategies;
	}

	private void initializeArrays(int length) {
		total_inits = new double[length];
		total_balances = new double[length];
		total_bandwagons = new double[length];
		total_buckpasses = new double[length];
		init_myCap_min = new double[length];
		init_myCap_med = new double[length];
		init_oppCap_min = new double[length];
		init_oppCap_med = new double[length];
		init_capRatio_min = new double[length];
		init_capRatio_med = new double[length];
		init_capMed_min = new double[length];
		init_capMed_med = new double[length];
		init_capStd_min = new double[length];
		init_capStd_med = new double[length];
		init_capMin_min = new double[length];
		init_capMin_med = new double[length];
		init_capMax_min = new double[length];
		init_capMax_med = new double[length];
		init_mySideCap_min = new double[length];
		init_mySideCap_med = new double[length];
		init_leftCapSum_min = new double[length];
		init_leftCapSum_med = new double[length];
		init_myEnmity_min = new double[length];
		init_myEnmity_med = new double[length];
		init_oppEnmity_min = new double[length];
		init_oppEnmity_med = new double[length];
		balance_myCap_min = new double[length];
		balance_myCap_med = new double[length];
		balance_oppCap_min = new double[length];
		balance_oppCap_med = new double[length];
		balance_capRatio_min = new double[length];
		balance_capRatio_med = new double[length];
		balance_capMed_min = new double[length];
		balance_capMed_med = new double[length];
		balance_capStd_min = new double[length];
		balance_capStd_med = new double[length];
		balance_capMin_min = new double[length];
		balance_capMin_med = new double[length];
		balance_capMax_min = new double[length];
		balance_capMax_med = new double[length];
		balance_mySideCap_min = new double[length];
		balance_mySideCap_med = new double[length];
		balance_leftCapSum_min = new double[length];
		balance_leftCapSum_med = new double[length];
		balance_myEnmity_min = new double[length];
		balance_myEnmity_med = new double[length];
		balance_oppEnmity_min = new double[length];
		balance_oppEnmity_med = new double[length];
		bandwagon_myCap_min = new double[length];
		bandwagon_myCap_med = new double[length];
		bandwagon_oppCap_min = new double[length];
		bandwagon_oppCap_med = new double[length];
		bandwagon_capRatio_min = new double[length];
		bandwagon_capRatio_med = new double[length];
		bandwagon_capMed_min = new double[length];
		bandwagon_capMed_med = new double[length];
		bandwagon_capStd_min = new double[length];
		bandwagon_capStd_med = new double[length];
		bandwagon_capMin_min = new double[length];
		bandwagon_capMin_med = new double[length];
		bandwagon_capMax_min = new double[length];
		bandwagon_capMax_med = new double[length];
		bandwagon_mySideCap_min = new double[length];
		bandwagon_mySideCap_med = new double[length];
		bandwagon_leftCapSum_min = new double[length];
		bandwagon_leftCapSum_med = new double[length];
		bandwagon_myEnmity_min = new double[length];
		bandwagon_myEnmity_med = new double[length];
		bandwagon_oppEnmity_min = new double[length];
		bandwagon_oppEnmity_med = new double[length];
		buckpass_myCap_min = new double[length];
		buckpass_myCap_med = new double[length];
		buckpass_oppCap_min = new double[length];
		buckpass_oppCap_med = new double[length];
		buckpass_capRatio_min = new double[length];
		buckpass_capRatio_med = new double[length];
		buckpass_capMed_min = new double[length];
		buckpass_capMed_med = new double[length];
		buckpass_capStd_min = new double[length];
		buckpass_capStd_med = new double[length];
		buckpass_capMin_min = new double[length];
		buckpass_capMin_med = new double[length];
		buckpass_capMax_min = new double[length];
		buckpass_capMax_med = new double[length];
		buckpass_mySideCap_min = new double[length];
		buckpass_mySideCap_med = new double[length];
		buckpass_leftCapSum_min = new double[length];
		buckpass_leftCapSum_med = new double[length];
		buckpass_myEnmity_min = new double[length];
		buckpass_myEnmity_med = new double[length];
		buckpass_oppEnmity_min = new double[length];
		buckpass_oppEnmity_med = new double[length];
	}

	private void checkErrors() {
		if (minLevel != 2) {
			System.out.println("StatisticsPrinter class erorr!!!");
			System.exit(0);
		}

	}

}
