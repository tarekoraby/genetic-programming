import java.util.Arrays;


public class StatisticsPrinter_v24 extends GP_PilotStudy_v24{
	
	static double total_inits, total_balances, total_bandwagons, init_myCap_min, init_myCap_med, init_oppCap_min,
			init_oppCap_med, init_capRatio_min, init_capRatio_med, init_capMed_min, init_capMed_med, init_capStd_min,
			init_capStd_med, init_capMin_min, init_capMin_med, init_capMax_min, init_capMax_med, init_mySideCap_min,
			init_mySideCap_med, init_leftCapSum_min, init_leftCapSum_med, init_myEnmity_min, init_myEnmity_med,
			init_oppEnmity_min, init_oppEnmity_med, balance_myCap_min, balance_myCap_med, balance_oppCap_min,
			balance_oppCap_med, balance_capRatio_min, balance_capRatio_med, balance_capMed_min, balance_capMed_med,
			balance_capStd_min, balance_capStd_med, balance_capMin_min, balance_capMin_med, balance_capMax_min,
			balance_capMax_med, balance_mySideCap_min, balance_mySideCap_med, balance_leftCapSum_min,
			balance_leftCapSum_med, balance_myEnmity_min, balance_myEnmity_med, balance_oppEnmity_min,
			balance_oppEnmity_med, bandwagon_myCap_min, bandwagon_myCap_med, bandwagon_oppCap_min,
			bandwagon_oppCap_med, bandwagon_capRatio_min, bandwagon_capRatio_med, bandwagon_capMed_min,
			bandwagon_capMed_med, bandwagon_capStd_min, bandwagon_capStd_med, bandwagon_capMin_min,
			bandwagon_capMin_med, bandwagon_capMax_min, bandwagon_capMax_med, bandwagon_mySideCap_min,
			bandwagon_mySideCap_med, bandwagon_leftCapSum_min, bandwagon_leftCapSum_med, bandwagon_myEnmity_min,
			bandwagon_myEnmity_med, bandwagon_oppEnmity_min, bandwagon_oppEnmity_med;
	
	

	static void printStats(char[][][][] init_strategy, char[][][][] join_strategy) {
		
		//how similar are the stratgies to each oteher in every level
		//how many attacks per world

		if (!sortedFit) {
			System.out.println("Not Sorted!!!");
			System.exit(0);
		}

		System.out.println("\n\nSimulation=" + currentSimulation + "  Generation=" + currentGen);

		char[][][] temp_init = new char[uniqueWorldSizes][][];
		char[][][] temp_join = null;

		for (int i = 0; i < uniqueWorldSizes; i++) {
			temp_init[i] = new char[i + MINSYSTEM][];
			for (int k = 0; k < temp_init[i].length; k++)
				temp_init[i][k] = (char[]) init_strategy[i][k][i].clone();
		}
		
		if (join_strategy != null) {
			temp_join = new char[uniqueWorldSizes - 1][][];
			for (int i = 0; i < uniqueWorldSizes - 1; i++) {
				temp_join[i] = new char[i + MINSYSTEM + 1][];
				for (int k = 0; k < temp_join[i].length; k++)
					temp_join[i][k] = (char[]) join_strategy[i][k][i].clone();
			}
		}
		

		populateProfiles(temp_init, temp_join);	
		
		printProfilesToFile();
		
		
		
		
		for (int i = 0; i < attackProfiles.length; i++) {
			System.out.println("In world " + (i+MINSYSTEM) + ", percentage of attacks of state ");
			for (int k = 0; k < attackProfiles[i].length; k++) {
				System.out.print((k+1) + " is ") ;
				int totalAttacks=0;
				for (int l = 0; l < PROFILES_PER_CAT/2; l++) {
					if (attackProfiles[i][k][l] == true)
						totalAttacks++;
				}
				System.out.print((double)totalAttacks/(PROFILES_PER_CAT/2) * 100  + " %");
				
				totalAttacks=0;
				for (int l = PROFILES_PER_CAT/2; l < PROFILES_PER_CAT; l++) {
					if (attackProfiles[i][k][l] == true)
						totalAttacks++;
				}
				System.out.println("   " + (double)totalAttacks/(PROFILES_PER_CAT/2) * 100  + " %");
			}
		}
		
		System.out.println();

		if (MAXSYSTEM > 2) {
			for (int i = 0; i < balanceProfiles.length; i++) {
				System.out.println("In world " + (i + MINSYSTEM + 1) + ", percentage of balances of state ");
				for (int k = 0; k < balanceProfiles[i].length; k++) {
					System.out.print((k + 1) + " is ");
					int totalbalances = 0;
					for (int l = 0; l < PROFILES_PER_CAT/2; l++) {
						if (balanceProfiles[i][k][l] == true)
							totalbalances++;
					}
					System.out.print((double) totalbalances / (PROFILES_PER_CAT/2) * 100 + " %");
					
					totalbalances = 0;
					for (int l = PROFILES_PER_CAT/2; l < PROFILES_PER_CAT; l++) {
						if (balanceProfiles[i][k][l] == true)
							totalbalances++;
					}
					System.out.println("   " + (double) totalbalances / (PROFILES_PER_CAT/2) * 100 + " %");
				}
			}

			System.out.println();

			for (int i = 0; i < bandwagonProfiles.length; i++) {
				System.out.println("In world " + (i + MINSYSTEM + 1) + ", percentage of bandwagons of state ");
				for (int k = 0; k < bandwagonProfiles[i].length; k++) {
					System.out.print((k + 1) + " is ");
					int totalbandwagons = 0;
					for (int l = 0; l < PROFILES_PER_CAT/2; l++) {
						if (bandwagonProfiles[i][k][l] == true)
							totalbandwagons++;
					}
					System.out.print((double) totalbandwagons / (PROFILES_PER_CAT/2) * 100 + " %");
					
					totalbandwagons = 0;
					for (int l = PROFILES_PER_CAT/2; l < PROFILES_PER_CAT; l++) {
						if (bandwagonProfiles[i][k][l] == true)
							totalbandwagons++;
					}
					System.out.println("   " + (double) totalbandwagons / (PROFILES_PER_CAT/2) * 100 + " %");
				}
			}

		}

	}

	private static void printProfilesToFile() {

				
		for (int i = 0; i < attackProfiles.length; i++) {
			for (int k = 0; k < attackProfiles[i].length; k++) {
				calculateProfileVariables(i, k);
				outputString = (currentSimulation + "," + (k + 1) + "," + currentGen + "," + (i + MINSYSTEM) + ","
						+ total_inits + "," + total_balances + "," + total_bandwagons);
				
				for (int j = 0; j < MAXSYSTEM; j++) {
					if (k == j || j >= attackProfiles[i].length)
						outputString += ("," + ".");
					else 						
						outputString += ("," + calcSimilarity(i, k , j, 0));
				}
				
				for (int j = 0; j < MAXSYSTEM; j++) {
					if (k == j || j >= attackProfiles[i].length || i==0)
						outputString += ("," + ".");
					else 						
						outputString += ("," + calcSimilarity(i, k , j, 1));
				}
				
				for (int j = 0; j < MAXSYSTEM; j++) {
					if (k == j || j >= attackProfiles[i].length || i==0)
						outputString += ("," + ".");
					else 						
						outputString += ("," + calcSimilarity(i, k , j, 2));
				}

				
				outputString += ( "," + init_myCap_min + ","
						+ init_myCap_med + "," + init_oppCap_min + "," + init_oppCap_med + "," + init_capRatio_min
						+ "," + init_capRatio_med + "," + init_capMed_min + "," + init_capMed_med + ","
						+ init_capStd_min + "," + init_capStd_med + "," + init_capMin_min + "," + init_capMin_med + ","
						+ init_capMax_min + "," + init_capMax_med + "," + init_mySideCap_min + "," + init_mySideCap_med
						+ "," + init_leftCapSum_min + "," + init_leftCapSum_med + "," + init_myEnmity_min + ","
						+ init_myEnmity_med + "," + init_oppEnmity_min + "," + init_oppEnmity_med );

				if (i > 0) {
					outputString += ("," + balance_myCap_min + "," + balance_myCap_med + ","
							+ balance_oppCap_min + "," + balance_oppCap_med + "," + balance_capRatio_min + ","
							+ balance_capRatio_med + "," + balance_capMed_min + "," + balance_capMed_med + ","
							+ balance_capStd_min + "," + balance_capStd_med + "," + balance_capMin_min + ","
							+ balance_capMin_med + "," + balance_capMax_min + "," + balance_capMax_med + ","
							+ balance_mySideCap_min + "," + balance_mySideCap_med + "," + balance_leftCapSum_min + ","
							+ balance_leftCapSum_med + "," + balance_myEnmity_min + "," + balance_myEnmity_med + ","
							+ balance_oppEnmity_min + "," + balance_oppEnmity_med + "," + bandwagon_myCap_min + ","
							+ bandwagon_myCap_med + "," + bandwagon_oppCap_min + "," + bandwagon_oppCap_med + ","
							+ bandwagon_capRatio_min + "," + bandwagon_capRatio_med + "," + bandwagon_capMed_min + ","
							+ bandwagon_capMed_med + "," + bandwagon_capStd_min + "," + bandwagon_capStd_med + ","
							+ bandwagon_capMin_min + "," + bandwagon_capMin_med + "," + bandwagon_capMax_min + ","
							+ bandwagon_capMax_med + "," + bandwagon_mySideCap_min + "," + bandwagon_mySideCap_med
							+ "," + bandwagon_leftCapSum_min + "," + bandwagon_leftCapSum_med + ","
							+ bandwagon_myEnmity_min + "," + bandwagon_myEnmity_med + "," + bandwagon_oppEnmity_min
							+ "," + bandwagon_oppEnmity_med);
				}
				state_profiles_writer.writeToFile(outputString + "\n");
			}	
		}
	}

	private static void calculateProfileVariables(int i, int k) {

		total_inits = -99;
		total_balances = -99;
		total_bandwagons = -99;
		init_myCap_min = -99;
		init_myCap_med = -99;
		init_oppCap_min = -99;
		init_oppCap_med = -99;
		init_capRatio_min = -99;
		init_capRatio_med = -99;
		init_capMed_min = -99;
		init_capMed_med = -99;
		init_capStd_min = -99;
		init_capStd_med = -99;
		init_capMin_min = -99;
		init_capMin_med = -99;
		init_capMax_min = -99;
		init_capMax_med = -99;
		init_mySideCap_min = -99;
		init_mySideCap_med = -99;
		init_leftCapSum_min = -99;
		init_leftCapSum_med = -99;
		init_myEnmity_min = -99;
		init_myEnmity_med = -99;
		init_oppEnmity_min = -99;
		init_oppEnmity_med = -99;
		balance_myCap_min = -99;
		balance_myCap_med = -99;
		balance_oppCap_min = -99;
		balance_oppCap_med = -99;
		balance_capRatio_min = -99;
		balance_capRatio_med = -99;
		balance_capMed_min = -99;
		balance_capMed_med = -99;
		balance_capStd_min = -99;
		balance_capStd_med = -99;
		balance_capMin_min = -99;
		balance_capMin_med = -99;
		balance_capMax_min = -99;
		balance_capMax_med = -99;
		balance_mySideCap_min = -99;
		balance_mySideCap_med = -99;
		balance_leftCapSum_min = -99;
		balance_leftCapSum_med = -99;
		balance_myEnmity_min = -99;
		balance_myEnmity_med = -99;
		balance_oppEnmity_min = -99;
		balance_oppEnmity_med = -99;
		bandwagon_myCap_min = -99;
		bandwagon_myCap_med = -99;
		bandwagon_oppCap_min = -99;
		bandwagon_oppCap_med = -99;
		bandwagon_capRatio_min = -99;
		bandwagon_capRatio_med = -99;
		bandwagon_capMed_min = -99;
		bandwagon_capMed_med = -99;
		bandwagon_capStd_min = -99;
		bandwagon_capStd_med = -99;
		bandwagon_capMin_min = -99;
		bandwagon_capMin_med = -99;
		bandwagon_capMax_min = -99;
		bandwagon_capMax_med = -99;
		bandwagon_mySideCap_min = -99;
		bandwagon_mySideCap_med = -99;
		bandwagon_leftCapSum_min = -99;
		bandwagon_leftCapSum_med = -99;
		bandwagon_myEnmity_min = -99;
		bandwagon_myEnmity_med = -99;
		bandwagon_oppEnmity_min = -99;
		bandwagon_oppEnmity_med = -99;
		
		boolean even;
		
		int totalAttacks = 0;
		for (int l = 0; l < PROFILES_PER_CAT; l++) {
			if (attackProfiles[i][k][l] == true)
				totalAttacks++;
		}

		total_inits = (double) totalAttacks;

		
		
		if (total_inits > 0) {
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

			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (attackProfiles[i][k][l] == true) {
					init_myCap[totalAttacks - 1] = profilingWorlds_Attacks[i][l].myCap;
					init_oppCap[totalAttacks - 1] = profilingWorlds_Attacks[i][l].oppCap;
					init_capRatio[totalAttacks - 1] = profilingWorlds_Attacks[i][l].capRatio;
					init_capMed[totalAttacks - 1] = profilingWorlds_Attacks[i][l].capMed;
					init_capStd[totalAttacks - 1] = profilingWorlds_Attacks[i][l].capStd;
					init_capMin[totalAttacks - 1] = profilingWorlds_Attacks[i][l].capMin;
					init_capMax[totalAttacks - 1] = profilingWorlds_Attacks[i][l].capMax;
					init_mySideCap[totalAttacks - 1] = profilingWorlds_Attacks[i][l].mySideCap;
					init_leftCapSum[totalAttacks - 1] = profilingWorlds_Attacks[i][l].leftCapSum;
					init_myEnmity[totalAttacks - 1] = profilingWorlds_Attacks[i][l].myEnmity;
					init_oppEnmity[totalAttacks - 1] = profilingWorlds_Attacks[i][l].oppEnmity;
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

		if (i > 0) {

			int totalBalances = 0;
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (balanceProfiles[i - 1][k][l] == true)
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

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (balanceProfiles[i - 1][k][l] == true) {
						balance_myCap[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].myCap;
						balance_oppCap[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].largerSideCap;
						balance_capRatio[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].capRatio;
						balance_capMed[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].capMed;
						balance_capStd[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].capStd;
						balance_capMin[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].capMin;
						balance_capMax[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].capMax;
						balance_mySideCap[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].smallerSideCap;
						balance_leftCapSum[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].leftCapSum;
						balance_myEnmity[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].myEnmity;
						balance_oppEnmity[totalBalances - 1] = profilingWorlds_Joins[i - 1][l].oppEnmity;
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
				if (bandwagonProfiles[i - 1][k][l] == true)
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

				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (bandwagonProfiles[i - 1][k][l] == true) {
						bandwagon_myCap[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].myCap;
						bandwagon_oppCap[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].smallerSideCap;
						bandwagon_capRatio[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].capRatio;
						bandwagon_capMed[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].capMed;
						bandwagon_capStd[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].capStd;
						bandwagon_capMin[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].capMin;
						bandwagon_capMax[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].capMax;
						bandwagon_mySideCap[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].largerSideCap;
						bandwagon_leftCapSum[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].leftCapSum;
						bandwagon_myEnmity[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].myEnmity;
						bandwagon_oppEnmity[totalBandwagons - 1] = profilingWorlds_Joins[i - 1][l].oppEnmity;
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
		}
				
	}

	private static void populateProfiles(char[][][] init_strategy, char[][][] join_strategy) {
		attackProfiles = new boolean[init_strategy.length][][];

		for (int i = 0; i < attackProfiles.length; i++)
			attackProfiles[i] = new boolean[init_strategy[i].length][PROFILES_PER_CAT];

		for (int i = 0; i < attackProfiles.length; i++) {
			for (int k = 0; k < attackProfiles[i].length; k++) {
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					attackProfiles[i][k][l] = profilingWorlds_Attacks[i][l].willItAttack(init_strategy[i][k], 0);
				}
			}
		}

		if (join_strategy != null) {
			balanceProfiles = new boolean[join_strategy.length][][];
			bandwagonProfiles = new boolean[join_strategy.length][][];

			for (int i = 0; i < balanceProfiles.length; i++) {
				balanceProfiles[i] = new boolean[join_strategy[i].length][PROFILES_PER_CAT];
				bandwagonProfiles[i] = new boolean[join_strategy[i].length][PROFILES_PER_CAT];
			}

			for (int i = 0; i < balanceProfiles.length; i++) {
				for (int k = 0; k < balanceProfiles[i].length; k++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						balanceProfiles[i][k][l] = profilingWorlds_Joins[i][l].willItAttack(join_strategy[i][k], 1);
						bandwagonProfiles[i][k][l] = profilingWorlds_Joins[i][l].willItAttack(join_strategy[i][k], 2);
					}
				}
			}

		}
	}


	private static double calcSimilarity(int world_size, int index1, int index2, int type) {

		double similarity = 0;
		if (type == 0) {
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (attackProfiles[world_size][index1][l] == attackProfiles[world_size][index2][l])
					similarity++;
			}
		} else if (type == 1) {
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (balanceProfiles[world_size-1][index1][l] == balanceProfiles[world_size-1][index2][l])
					similarity++;
			}
		} else if (type ==2 ){
			for (int l = 0; l < PROFILES_PER_CAT; l++) {
				if (bandwagonProfiles[world_size-1][index1][l] == bandwagonProfiles[world_size-1][index2][l])
					similarity++;
			}
		}
		
		similarity = similarity / PROFILES_PER_CAT;

		return(similarity);

	}

}
