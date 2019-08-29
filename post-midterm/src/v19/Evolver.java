package v19;

import java.util.Arrays;
import java.util.Random;

public class Evolver {

	static Strategy[][] popAttack_2;
	static Strategy[][] popAttack_3_1, popAttack_3_2;
	static Strategy[][] popJoin_3_1, popJoin_3_2;
	static int evoSysSize, tsize, POPSIZE;
	static StrategyCreator creator = new StrategyCreator();
	static Random rd = new Random();
	static TestCase currTestCase;
	static boolean XYZ;

	public static void evolve(Strategy[][] popAttack2, Strategy[][] popAttack3_1, Strategy[][] popAttack3_2,
			Strategy[][] popJoin3_1, Strategy[][] popJoin3_2) {
		popAttack_2 = popAttack2;
		popAttack_3_1 = popAttack3_1;
		popAttack_3_2 = popAttack3_2;
		popJoin_3_1 = popJoin3_1;
		popJoin_3_2 = popJoin3_2;
		tsize = MasterVariables.TSIZE;

		Strategy[] evoPopStrategies;
		int bestIndex;

		if (popAttack_2[0].length != 1 || popAttack_3_1[0].length != 1)
			System.exit(0);

		evoSysSize = 2;
		POPSIZE = popAttack_2[0].length;
		System.out.print("Testing 2");
		boolean ss;

		for (int i = 0; i < 1; i++) {
			ss = createTestCase(true, 1, 1);
			while (!ss)
				ss = createTestCase(true, 1, 1);
			evoPopStrategies = createInitialPopulation(evoSysSize, true, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], true);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, true);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, true);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popAttack_2[1].length; counter++) {
				popAttack_2[1][counter].resetFitness();
				calculateFitness(popAttack_2[1][counter], true);
				if (evoPopStrategies[bestIndex].getFitness() >= popAttack_2[1][counter].getFitness())
					popAttack_2[1][counter] = evoPopStrategies[bestIndex].deepCopy();
			}
			/*
			 * char[][] xxx=new char[1][4]; xxx[0][0]=0;
			 * xxx[0][1]=MasterVariables.GT; xxx[0][2]=MasterVariables.MYCAP;
			 * xxx[0][3]=MasterVariables.MYCAP; Strategy mmm =new Strategy(2,
			 * true, xxx); calculateFitness(mmm, true);
			 * System.out.println("xxxxxxxxxxxxxxxxxxx " + mmm.getFitness(2));
			 */

			ss = createTestCase(true, 1, 2);
			while (!ss)
				ss = createTestCase(true, 1, 2);
			evoPopStrategies = createInitialPopulation(evoSysSize, true, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], true);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, true);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, true);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}

			for (int counter = 0; counter < popAttack_2[0].length; counter++) {
				popAttack_2[0][counter].resetFitness();
				calculateFitness(popAttack_2[0][counter], true);
				if (evoPopStrategies[bestIndex].getFitness() >= popAttack_2[0][counter].getFitness())
					popAttack_2[0][counter] = evoPopStrategies[bestIndex].deepCopy();
			}
		}

		System.out.print(" att 3");
		for (int i = 0; i < 1; i++) {
			evoSysSize = 3;
			POPSIZE = popAttack_3_1[0].length;

			ss = createTestCase(true, 1, 3);
			while (!ss)
				ss = createTestCase(true, 1, 3);
			evoPopStrategies = createInitialPopulation(evoSysSize, true, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], true);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, true);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, true);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popAttack_3_1[0].length; counter++) {
				popAttack_3_1[0][counter].resetFitness();
				calculateFitness(popAttack_3_1[0][counter], true);
				if (evoPopStrategies[bestIndex].getFitness() >= popAttack_3_1[0][counter].getFitness())
					popAttack_3_1[0][counter] = evoPopStrategies[bestIndex].deepCopy();
			}

			ss = createTestCase(true, 1, 2);
			while (!ss)
				ss = createTestCase(true, 1, 2);
			evoPopStrategies = createInitialPopulation(evoSysSize, true, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], true);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, true);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, true);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popAttack_3_1[1].length; counter++) {
				popAttack_3_1[1][counter].resetFitness();
				calculateFitness(popAttack_3_1[1][counter], true);
				if (evoPopStrategies[bestIndex].getFitness() >= popAttack_3_1[1][counter].getFitness())
					popAttack_3_1[1][counter] = evoPopStrategies[bestIndex].deepCopy();
			}

			ss = createTestCase(true, 1, 1);
			while (!ss)
				ss = createTestCase(true, 1, 1);
			evoPopStrategies = createInitialPopulation(evoSysSize, true, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], true);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, true);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, true);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popAttack_3_1[2].length; counter++) {
				popAttack_3_1[2][counter].resetFitness();
				calculateFitness(popAttack_3_1[2][counter], true);
				if (evoPopStrategies[bestIndex].getFitness() >= popAttack_3_1[2][counter].getFitness())
					popAttack_3_1[2][counter] = evoPopStrategies[bestIndex].deepCopy();
			}
		}

		for (int i = 0; i < 1; i++) {
			POPSIZE = popAttack_3_2[0].length;
			ss = createTestCase(true, 2, 3);
			while (!ss)
				ss = createTestCase(true, 2, 3);
			evoPopStrategies = createInitialPopulation(evoSysSize, true, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], true);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, true);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, true);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popAttack_3_2[0].length; counter++) {
				popAttack_3_2[0][counter].resetFitness();
				calculateFitness(popAttack_3_2[0][counter], true);
				if (evoPopStrategies[bestIndex].getFitness() >= popAttack_3_2[0][counter].getFitness())
					popAttack_3_2[0][counter] = evoPopStrategies[bestIndex].deepCopy();
			}

			ss = createTestCase(true, 2, 2);
			while (!ss)
				ss = createTestCase(true, 2, 2);
			evoPopStrategies = createInitialPopulation(evoSysSize, true, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], true);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, true);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, true);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popAttack_3_2[1].length; counter++) {
				popAttack_3_2[1][counter].resetFitness();
				calculateFitness(popAttack_3_2[1][counter], true);
				if (evoPopStrategies[bestIndex].getFitness() >= popAttack_3_2[1][counter].getFitness())
					popAttack_3_2[1][counter] = evoPopStrategies[bestIndex].deepCopy();
			}

			ss = createTestCase(true, 2, 1);
			while (!ss)
				ss = createTestCase(true, 2, 1);
			evoPopStrategies = createInitialPopulation(evoSysSize, true, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], true);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, true);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, true);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			/*
			 * char[] xxx=new char[3]; xxx[0]=MasterVariables.GT;
			 * xxx[1]=MasterVariables.MYCAP; xxx[2]=MasterVariables.OPPCAP;
			 */
			for (int counter = 0; counter < popAttack_3_2[2].length; counter++) {
				popAttack_3_2[2][counter].resetFitness();
				// popAttack_3_2[2][counter].setInitStrategy(3, xxx.clone());
				calculateFitness(popAttack_3_2[2][counter], true);
				if (evoPopStrategies[bestIndex].getFitness() >= popAttack_3_2[2][counter].getFitness())
					popAttack_3_2[2][counter] = evoPopStrategies[bestIndex].deepCopy();
			}

		}

		// /xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
		System.out.println(" join 3");
		for (int i = 0; i < 1; i++) {
			evoSysSize = 3;
			POPSIZE = popJoin_3_1[0].length;
			XYZ = true;
			ss = createTestCase(false, 1, 3);
			while (!ss)
				ss = createTestCase(false, 1, 3);
			evoPopStrategies = createInitialPopulation(evoSysSize, false, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], false);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, false);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, false);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popJoin_3_1[0].length; counter++) {
				popJoin_3_1[0][counter].resetFitness();
				calculateFitness(popJoin_3_1[0][counter], false);
				if (evoPopStrategies[bestIndex].getFitness() >= popJoin_3_1[0][counter].getFitness())
					popJoin_3_1[0][counter] = evoPopStrategies[bestIndex].deepCopy();
			}
			XYZ=false;

			ss = createTestCase(false, 1, 2);
			while (!ss)
				ss = createTestCase(false, 1, 2);
			evoPopStrategies = createInitialPopulation(evoSysSize, false, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], false);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, false);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, false);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popJoin_3_1[1].length; counter++) {
				popJoin_3_1[1][counter].resetFitness();
				calculateFitness(popJoin_3_1[1][counter], false);
				if (evoPopStrategies[bestIndex].getFitness() >= popJoin_3_1[1][counter].getFitness())
					popJoin_3_1[1][counter] = evoPopStrategies[bestIndex].deepCopy();
			}

			ss = createTestCase(false, 1, 1);
			while (!ss)
				ss = createTestCase(false, 1, 1);
			evoPopStrategies = createInitialPopulation(evoSysSize, false, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], false);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, false);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, false);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}

			/*
			 * Strategy xxxStrategy=createRandomIndiv(3, false);
			 * System.out.println("****************");
			 * currTestCase.resetTestCase(xxxStrategy);
			 * currTestCase.simulateJoiners(); MasterVariables.xxxTotalCounter
			 * ++; int result =
			 * currTestCase.runJoin(xxxStrategy.getJoinStrategy(3)); if
			 * (result>0){ MasterVariables.xxxJoinCounter++; if (result==1)
			 * MasterVariables.xxxbalanceCounter++; }
			 * 
			 * System.out.println((double)MasterVariables.xxxJoinCounter/
			 * MasterVariables.xxxTotalCounter + " " +
			 * (double)MasterVariables.xxxbalanceCounter
			 * /MasterVariables.xxxTotalCounter);
			 */

			for (int counter = 0; counter < popJoin_3_1[2].length; counter++) {
				popJoin_3_1[2][counter].resetFitness();
				calculateFitness(popJoin_3_1[2][counter], false);

				if (evoPopStrategies[bestIndex].getFitness() != 1)
					System.exit(0);
				if (evoPopStrategies[bestIndex].getFitness() >= popJoin_3_1[2][counter].getFitness())
					popJoin_3_1[2][counter] = evoPopStrategies[bestIndex].deepCopy();

			}
		}

		for (int i = 0; i < 1; i++) {

			POPSIZE = popJoin_3_2[0].length;
			ss = createTestCase(false, 2, 3);
			while (!ss)
				ss = createTestCase(false, 2, 3);
			evoPopStrategies = createInitialPopulation(evoSysSize, false, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], false);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, false);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, false);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popJoin_3_2[0].length; counter++) {
				popJoin_3_2[0][counter].resetFitness();
				calculateFitness(popJoin_3_2[0][counter], false);
				if (evoPopStrategies[bestIndex].getFitness() >= popJoin_3_2[0][counter].getFitness())
					popJoin_3_2[0][counter] = evoPopStrategies[bestIndex].deepCopy();
			}

			ss = createTestCase(false, 2, 2);
			while (!ss)
				ss = createTestCase(false, 2, 2);
			evoPopStrategies = createInitialPopulation(evoSysSize, false, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], false);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, false);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, false);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			for (int counter = 0; counter < popJoin_3_2[1].length; counter++) {
				popJoin_3_2[1][counter].resetFitness();
				calculateFitness(popJoin_3_2[1][counter], false);
				if (evoPopStrategies[bestIndex].getFitness() >= popJoin_3_2[1][counter].getFitness())
					popJoin_3_2[1][counter] = evoPopStrategies[bestIndex].deepCopy();
			}

			ss = createTestCase(false, 2, 1);
			while (!ss)
				ss = createTestCase(false, 2, 1);
			evoPopStrategies = createInitialPopulation(evoSysSize, false, 10);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				evoPopStrategies[counter].resetFitness();
				calculateFitness(evoPopStrategies[counter], false);
			}
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				Strategy evolvedIndiv = evolveIndividual(evoPopStrategies, false);
				int replacementIndex = rd.nextInt(POPSIZE);
				calculateFitness(evolvedIndiv, false);
				if (evolvedIndiv.getFitness() > evoPopStrategies[replacementIndex].getFitness()) {
					evoPopStrategies[replacementIndex] = evolvedIndiv.deepCopy();
				}
			}

			bestIndex = rd.nextInt(evoPopStrategies.length);
			for (int counter = 0; counter < evoPopStrategies.length; counter++) {
				if (evoPopStrategies[counter].getFitness() > evoPopStrategies[bestIndex].getFitness())
					bestIndex = counter;
			}
			/*
			 * char[] xxx=new char[3]; xxx[0]=MasterVariables.GT;
			 * xxx[1]=MasterVariables.MYCAP; xxx[2]=MasterVariables.OPPCAP;
			 */
			for (int counter = 0; counter < popJoin_3_2[2].length; counter++) {
				popJoin_3_2[2][counter].resetFitness();
				// popJoin_3_2[2][counter].setInitStrategy(3, xxx.clone());
				calculateFitness(popJoin_3_2[2][counter], false);
				if (evoPopStrategies[bestIndex].getFitness() >= popJoin_3_2[2][counter].getFitness())
					popJoin_3_2[2][counter] = evoPopStrategies[bestIndex].deepCopy();
			}
		}

	}
	
	private static boolean createTestCase(boolean isInit, int worldTypeNo, int order) {

		double[] capabilities = new double[evoSysSize], randNums = new double[1000], randGauss = new double[1000];
		double[][] capChangeRates = new double[MasterVariables.INTERACTIONROUNDS - 1][evoSysSize];
		int testedStrategyIndex = rd.nextInt(evoSysSize);
		Strategy[] strategies = new Strategy[evoSysSize];
		

		double remainder = 1;
		for (int i = 0; i < evoSysSize - 1; i++) {
			capabilities[i] = remainder * rd.nextDouble();
			remainder -= capabilities[i];
		}
		capabilities[evoSysSize - 1] = remainder;

		double max = -1;
		for (int i = 0; i < evoSysSize; i++) {
			if (capabilities[i] > max)
				max = capabilities[i];
		}

		if (evoSysSize == 3 && ((worldTypeNo == 1 && max > 0.5) || (worldTypeNo == 2 && max < 0.5)))
			return false;

		// shuffle capabilities to ensure their uniform distribution
		for (int i = evoSysSize - 1; i > 0; i--) {
			int index = rd.nextInt(i + 1);

			double temp = capabilities[index];
			capabilities[index] = capabilities[i];
			capabilities[i] = temp;
		}
		

		for (int i = 0; i < MasterVariables.INTERACTIONROUNDS - 1; i++) {
			capChangeRates[i] = new double[evoSysSize];
			for (int j = 0; j < evoSysSize; j++)
				capChangeRates[i][j] = 1 + rd.nextInt(MasterVariables.capChangeRatesFactor);
		}

		double[] temp = capabilities.clone();
		Arrays.sort(temp);
		double[] temp2 = capabilities.clone();
		int j = 0;
		for (int i = capabilities.length - 1; i >= 0; i--)
			temp2[j++] = temp[i];
		temp = temp2;
		testedStrategyIndex = -1;
		if (order == 3)
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[2]);
		else if (order == 2)
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[1]);
		else if (order == 1)
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[0]);
		
		
		//xxx
		/*if (testedStrategyIndex!=0)
			return false;*/
		
		//if (evoSysSize == 3 && worldTypeNo == 2 && order == 1)
		//	System.out.println(capabilities[testedStrategyIndex]);
		

		if (evoSysSize==2){
			if (capabilities[0] < 0.5) {
				strategies[0] = popAttack_2[0][rd.nextInt(popAttack_2[0].length)];
				strategies[1] = popAttack_2[1][rd.nextInt(popAttack_2[0].length)];
			} else{
				strategies[0] = popAttack_2[1][rd.nextInt(popAttack_2[0].length)];
				strategies[1] = popAttack_2[0][rd.nextInt(popAttack_2[0].length)];
			}
		} else if (evoSysSize == 3) {
			if (isInit) {
				if (worldTypeNo == 1) {
					for (int i=0; i<evoSysSize; i++){
						if (capabilities[i] == temp[2])
							strategies[i] = popAttack_3_1[0][rd.nextInt(popAttack_3_1[0].length)];
						else if (capabilities[i] == temp[1])
							strategies[i] = popAttack_3_1[1][rd.nextInt(popAttack_3_1[1].length)];
						else if (capabilities[i] == temp[0])
							strategies[i] = popAttack_3_1[2][rd.nextInt(popAttack_3_1[2].length)];
						else {
							System.exit(0);
						}
					}
				} else if (worldTypeNo == 2) {
					for (int i=0; i<evoSysSize; i++){
						if (capabilities[i] == temp[2])
							strategies[i] = popAttack_3_2[0][rd.nextInt(popAttack_3_2[0].length)];
						else if (capabilities[i] == temp[1])
							strategies[i] = popAttack_3_2[1][rd.nextInt(popAttack_3_2[1].length)];
						else if (capabilities[i] == temp[0])
							strategies[i] = popAttack_3_2[2][rd.nextInt(popAttack_3_2[2].length)];
						else {
							System.exit(0);
						}
					}
				}
			} else {
				if (worldTypeNo == 1) {
					for (int i=0; i<evoSysSize; i++){
						if (capabilities[i] == temp[2])
							strategies[i] = popJoin_3_1[0][rd.nextInt(popJoin_3_1[0].length)];
						else if (capabilities[i] == temp[1])
							strategies[i] = popJoin_3_1[1][rd.nextInt(popJoin_3_1[1].length)];
						else if (capabilities[i] == temp[0])
							strategies[i] = popJoin_3_1[2][rd.nextInt(popJoin_3_1[2].length)];
						else {
							System.exit(0);
						}
					}
				} else if (worldTypeNo == 2) {
					for (int i=0; i<evoSysSize; i++){
						if (capabilities[i] == temp[2])
							strategies[i] = popJoin_3_2[0][rd.nextInt(popJoin_3_2[0].length)];
						else if (capabilities[i] == temp[1])
							strategies[i] = popJoin_3_2[1][rd.nextInt(popJoin_3_2[1].length)];
						else if (capabilities[i] == temp[0])
							strategies[i] = popJoin_3_2[2][rd.nextInt(popJoin_3_2[2].length)];
						else {
							System.exit(0);
						}
					}
				}
			}
		}
		
		/*char[][] xxx=new char[1][4];
		xxx[0][0]=0;
		xxx[0][1]=MasterVariables.GT;
		xxx[0][2]=MasterVariables.MYCAP;
		xxx[0][3]=MasterVariables.MYCAP;*/
		
		if (evoSysSize == 3) {
			Strategy xxx = createRandomIndiv(3, true);
			Strategy yyy = createRandomIndiv(3, true);
			if ( order == 2)
				for (int i = 0; i < evoSysSize; i++) {
					if (capabilities[i] == temp[0])
						strategies[i] = xxx;
					else if (capabilities[i] == temp[2])
						strategies[i] = yyy;

				}

			if ( order == 1)
				for (int i = 0; i < evoSysSize; i++) {
					if (capabilities[i] == temp[1])
						strategies[i] = xxx;
					else if (capabilities[i] == temp[2])
						strategies[i] = yyy;

				}

			if (order == 3)
				for (int i = 0; i < evoSysSize; i++) {
					if (capabilities[i] == temp[0])
						strategies[i] = xxx;
					else if (capabilities[i] == temp[1])
						strategies[i] = yyy;

				}
		}

		for (int i=0; i<evoSysSize; i++)
		strategies[i]=null;

		for (int i = 0; i < randNums.length; i++)
			randNums[i] = rd.nextDouble();

		for (int i = 0; i < randGauss.length; i++)
			randGauss[i] = rd.nextGaussian();

		currTestCase = new TestCase(capabilities, capChangeRates, randNums, randGauss, testedStrategyIndex,
				MasterVariables.initPronness, MasterVariables.balancePronness, MasterVariables.joinPronness,
				strategies, isInit);
		return true;
	}

	private static void calculateFitness(Strategy evolvedIndiv, boolean simulateInit) {
		evolvedIndiv.resetFitness();
		
		if (simulateInit)
			currTestCase.resetTestCase(evolvedIndiv);
		else
			currTestCase.resetTestCase(evolvedIndiv);

		int fitness = currTestCase.simulate();
		evolvedIndiv.increaseFitnessBy(fitness);

		if (false && XYZ) {
			int fitness1, fitness2, fitness3;
			Strategy newstrStrategy = evolvedIndiv.deepCopy();
			char[][] xxx = new char[2][4];
			xxx[0][1] = MasterVariables.GT;
			xxx[0][2] = MasterVariables.CAP_1;
			xxx[0][3] = MasterVariables.CAP_2;
			xxx[0][0] = (char) 0;
			newstrStrategy.setJoinStrategy(3, xxx);
			currTestCase.resetTestCase(newstrStrategy);
			fitness1 = currTestCase.simulate();
			xxx[0][0] = (char) 1;
			newstrStrategy.setJoinStrategy(3, xxx);
			currTestCase.resetTestCase(newstrStrategy);
			fitness2 = currTestCase.simulate();
			xxx[0][0] = (char) 2;
			newstrStrategy.setJoinStrategy(3, xxx);
			currTestCase.resetTestCase(newstrStrategy);
			fitness3 = currTestCase.simulate();
			System.out.println(fitness1 + " " + fitness2 + " " + fitness3);
			if (fitness1 != fitness2 || fitness1 != fitness3 | fitness2 != fitness3){
				System.out.println(currTestCase.cap_1 + " " + currTestCase.cap_2 + " " + currTestCase.cap_3);
				currTestCase.resetTestCase(newstrStrategy);
				System.out.println(currTestCase.cap_1 + " " + currTestCase.cap_2 + " " + currTestCase.cap_3);
				System.out.println(currTestCase.capabilities[0] + " " + currTestCase.capabilities[1] + " " + currTestCase.capabilities[2]);
				System.out.println(currTestCase.runInit(currTestCase.strategies[0].getInitStrategy(2)) + " " + currTestCase.runInit(currTestCase.strategies[1].getInitStrategy(2)) + " " + currTestCase.runInit(currTestCase.strategies[2].getInitStrategy(2)));
				System.exit(0);
			}
		}
	}

	private static Strategy evolveIndividual(Strategy[] population, boolean isInit) {
		Strategy evolvedIndiv;
		int indivs, parent1 = -99, parent2 = -99, parent = -99;
		double random;
		
		///xxxxx
		return (createRandomIndiv(evoSysSize, isInit));
		
		//xxxxxxx
		/*if (isInit) {
			char[][] newInitStrategy;
			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				newInitStrategy = population[parent].getInitStrategy(evoSysSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(population, MasterVariables.TSIZE);
				parent2 = tournament(population, MasterVariables.TSIZE);
				int index1 = rd.nextInt(evoSysSize - 1);
				int index2 = rd.nextInt(evoSysSize - 1);
				char[][] strategy1 = population[parent1].getInitStrategy(evoSysSize);
				char[][] strategy2 = population[parent2].getInitStrategy(evoSysSize);
				char[] mutStrategy1 = Arrays.copyOfRange(strategy1[index1], 1, strategy1[index1].length);
				char[] mutStrategy2 = Arrays.copyOfRange(strategy1[index2], 1, strategy1[index2].length);

				char[] result = crossover(mutStrategy1, mutStrategy2);
				char order = strategy1[index1][0];
				newInitStrategy = strategy1;
				newInitStrategy[index1] = new char[1 + result.length];
				newInitStrategy[index1][0] = order;
				System.arraycopy(result, 0, newInitStrategy[index1], 1, result.length);

				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				int index = rd.nextInt(evoSysSize - 1);				
				char[][] strategy = population[parent].getInitStrategy(evoSysSize);				
				char[] mutStrategy = Arrays.copyOfRange(strategy[index], 1, strategy[index].length);
				char[] result = subtreeMutation(mutStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				char order = strategy[index][0];
				newInitStrategy = strategy;
				newInitStrategy[index] = new char[1 + result.length];
				newInitStrategy[index][0] = order;
				System.arraycopy(result, 0, newInitStrategy[index], 1, result.length);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				int index = rd.nextInt(evoSysSize - 1);
				char[][] strategy = population[parent].getInitStrategy(evoSysSize);
				char[] mutStrategy = Arrays.copyOfRange(strategy[index], 1, strategy[index].length);
				char[] result = pointMutation(mutStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				char order = strategy[index][0];
				newInitStrategy = strategy;
				newInitStrategy[index] = new char[1 + result.length];
				newInitStrategy[index][0] = order;
				System.arraycopy(result, 0, newInitStrategy[index], 1, result.length);
				newInitStrategy = strategy;
				parent = -99;
			}  else {
				newInitStrategy = creator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, evoSysSize);
			}

			evolvedIndiv = new Strategy(evoSysSize, isInit, newInitStrategy);
			return evolvedIndiv;

		} else {
			random = rd.nextDouble();
			if (random < MasterVariables.MOD_CROSSOVER_PROB) {
				evolvedIndiv = new Strategy(evoSysSize, isInit,
						population[tournament(population, MasterVariables.TSIZE)].getJoinStrategy(evoSysSize),
						population[tournament(population, MasterVariables.TSIZE)].getBalanceStrategy(evoSysSize));
				return evolvedIndiv;
			}

			char[] newJoinStrategy, newBalanceStrategy;

			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				newJoinStrategy = population[parent].getJoinStrategy(evoSysSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(population, MasterVariables.TSIZE);
				parent2 = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy1 = Arrays.copyOfRange(population[parent1].getJoinStrategy(evoSysSize), 1, population[parent1].getJoinStrategy(evoSysSize).length); 
				char[] parentStrategy2 = Arrays.copyOfRange(population[parent2].getJoinStrategy(evoSysSize), 1, population[parent2].getJoinStrategy(evoSysSize).length);
				char[] result = crossover(parentStrategy1,
						parentStrategy2);
				newJoinStrategy = new char[result.length + 1];
				newJoinStrategy[0] = population[parent1].getJoinStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newJoinStrategy, 1, result.length);
				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy = Arrays.copyOfRange(population[parent].getJoinStrategy(evoSysSize), 1,
						population[parent].getJoinStrategy(evoSysSize).length);
				char[] result = subtreeMutation(parentStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				newJoinStrategy = new char[result.length + 1];
				newJoinStrategy[0] = population[parent].getJoinStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newJoinStrategy, 1, result.length);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy = Arrays.copyOfRange(population[parent].getJoinStrategy(evoSysSize), 1,
						population[parent].getJoinStrategy(evoSysSize).length);
				char[] result = pointMutation(parentStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				newJoinStrategy = new char[result.length + 1];
				newJoinStrategy[0] = population[parent].getJoinStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newJoinStrategy, 1, result.length);
				parent = -99;
			} else {
				newJoinStrategy = creator.create_random_join_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
			}

			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				newBalanceStrategy = population[parent].getBalanceStrategy(evoSysSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(population, MasterVariables.TSIZE);
				parent2 = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy1 = Arrays.copyOfRange(population[parent1].getBalanceStrategy(evoSysSize), 1, population[parent1].getBalanceStrategy(evoSysSize).length); 
				char[] parentStrategy2 = Arrays.copyOfRange(population[parent2].getBalanceStrategy(evoSysSize), 1, population[parent2].getBalanceStrategy(evoSysSize).length);
				char[] result = crossover(parentStrategy1,
						parentStrategy2);
				newBalanceStrategy = new char[result.length + 1];
				newBalanceStrategy[0] = population[parent1].getBalanceStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newBalanceStrategy, 1, result.length);
				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy = Arrays.copyOfRange(population[parent].getBalanceStrategy(evoSysSize), 1,
						population[parent].getBalanceStrategy(evoSysSize).length);
				char[] result = subtreeMutation(parentStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				newBalanceStrategy = new char[result.length + 1];
				newBalanceStrategy[0] = population[parent].getBalanceStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newBalanceStrategy, 1, result.length);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy = Arrays.copyOfRange(population[parent].getBalanceStrategy(evoSysSize), 1,
						population[parent].getBalanceStrategy(evoSysSize).length);
				char[] result = pointMutation(parentStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				newBalanceStrategy = new char[result.length + 1];
				newBalanceStrategy[0] = population[parent].getBalanceStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newBalanceStrategy, 1, result.length);
				parent = -99;
			}  else {
				newBalanceStrategy = creator.create_random_join_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
			}

			evolvedIndiv = new Strategy(evoSysSize, isInit, newJoinStrategy, newBalanceStrategy);
			return evolvedIndiv;
		}
		*/
	}

	static int tournament(Strategy[] originalPopulation, int tsize) {
		int best = rd.nextInt(originalPopulation.length), i, competitor;
		double fbest = -1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(originalPopulation.length);
			//calculateFitness(originalPopulation[competitor]);
			if (originalPopulation[competitor].getFitness() > fbest) {
				fbest = originalPopulation[competitor].getFitness();
				best = competitor;
			}
			//originalPopulation[competitor].resetFitness();
		}
		return (best);
	}

	static char[] crossover(char[] parent1, char[] parent2) {
		int xo1start, xo1end, xo2start, xo2end;
		char[] offspring;
		int len1 = traverse(parent1, 0);
		int len2 = traverse(parent2, 0);
		int lenoff;

		xo1start = 1 + rd.nextInt(len1 - 1); // 1+ because the root of the tree
												// shouldn't be replaced
		xo1end = traverse(parent1, xo1start);

		xo2start = rd.nextInt(len2);
		while ((parent2[xo2start] < MasterVariables.FSET_2_START && parent1[xo1start] >= MasterVariables.FSET_2_START)
				|| (parent2[xo2start] >= MasterVariables.FSET_2_START && parent1[xo1start] < MasterVariables.FSET_2_START))
			xo2start = rd.nextInt(len2);
		xo2end = traverse(parent2, xo2start);

		lenoff = xo1start + (xo2end - xo2start) + (len1 - xo1end);

		offspring = new char[lenoff];

		System.arraycopy(parent1, 0, offspring, 0, xo1start);
		System.arraycopy(parent2, xo2start, offspring, xo1start, (xo2end - xo2start));
		System.arraycopy(parent1, xo1end, offspring, xo1start + (xo2end - xo2start), (len1 - xo1end));

		return (offspring);
	}

	static char[] pointMutation(char[] parent, boolean simulateConstructivism) {
		int len = traverse(parent, 0), i;
		int mutsite = 1 + rd.nextInt(len - 1); // This is to avoid replacing the
												// root of the tree
		char[] parentcopy = new char[len];

		System.arraycopy(parent, 0, parentcopy, 0, len);
		if (parentcopy[mutsite] < MasterVariables.FSET_1_START) {
			char prim = (char) rd.nextInt(2);
			if (prim == 0)
				if (simulateConstructivism == false)
					prim = (char) (MasterVariables.TSET_1_START + rd.nextInt(MasterVariables.TSET_1_END
							- MasterVariables.TSET_1_START + 1));
				else
					prim = (char) (MasterVariables.TSET_1_START + rd.nextInt(MasterVariables.TSET_2_END
							- MasterVariables.TSET_1_START + 1));
			else
				prim = (char) rd.nextDouble();
			parentcopy[mutsite] = prim;
		} else
			switch (parentcopy[mutsite]) {
			case MasterVariables.ADD:
			case MasterVariables.SUB:
			case MasterVariables.MUL:
			case MasterVariables.DIV:
				parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_1_END - MasterVariables.FSET_1_START + 1) + MasterVariables.FSET_1_START);
				break;
			case MasterVariables.GT:
				parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END - MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
				break;
			case MasterVariables.LT:
				parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END - MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
				break;
			case MasterVariables.EQ:
				parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END - MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
				break;
			case MasterVariables.AND:
				parentcopy[mutsite] = MasterVariables.OR;
				break;
			case MasterVariables.OR:
				parentcopy[mutsite] = MasterVariables.AND;
				break;
			case MasterVariables.IF_THEN_ELSE:
				// do nothing in case of if-then-else
				break;
			default:
				System.out.println("ERROR!! Evolver class ... point mutation method");
				System.exit(0);
			}

		return (parentcopy);
	}

	static char[] subtreeMutation(char[] parent, boolean simulateConstructivism) {
		int mutStart, mutEnd, parentLen = traverse(parent, 0), subtreeLen, lenOff;
		char[] newSubtree, offspring;

		// Calculate the mutation starting point.
		mutStart = 1 + rd.nextInt(parentLen - 1);
		mutEnd = traverse(parent, mutStart);

		// Grow new subtree. If the replaced tree returned boolean, make sure
		// the new subtree returns boolean as well. The new subtree cannot be
		// more
		// than one level deeper than the replaced one
		if (parent[mutStart] >= MasterVariables.FSET_2_START)
			newSubtree = creator.grow(1 + rd.nextInt((int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), true,
					simulateConstructivism);
		else
			newSubtree = creator.grow(rd.nextInt(1 + (int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), false,
					simulateConstructivism);

		subtreeLen = traverse(newSubtree, 0);

		lenOff = mutStart + subtreeLen + (parentLen - mutEnd);

		offspring = new char[lenOff];

		System.arraycopy(parent, 0, offspring, 0, mutStart);
		System.arraycopy(newSubtree, 0, offspring, mutStart, subtreeLen);
		System.arraycopy(parent, mutEnd, offspring, (mutStart + subtreeLen), (parentLen - mutEnd));

		return (offspring);
	}

	static int traverse(char[] buffer, int buffercount) {
		if (buffer[buffercount] < MasterVariables.FSET_1_START)
			return (++buffercount);

		switch (buffer[buffercount]) {
		case MasterVariables.ADD:
		case MasterVariables.SUB:
		case MasterVariables.MUL:
		case MasterVariables.DIV:
		case MasterVariables.GT:
		case MasterVariables.LT:
		case MasterVariables.EQ:
		case MasterVariables.AND:
		case MasterVariables.OR:
			return (traverse(buffer, traverse(buffer, ++buffercount)));
		case MasterVariables.IF_THEN_ELSE:
			return (traverse(buffer, traverse(buffer, traverse(buffer, ++buffercount))));
		}
		System.out.println("traverse method error!!!");
		return (0); // should never get here
	}


	
	static Strategy[] createInitialPopulation(int currentLevel, boolean isInit, int popSize) {
		Strategy[] newStrategies = new Strategy[popSize];
		StrategyCreator sCreator = new StrategyCreator();
		if (isInit) {
			for (int counter = 0; counter < newStrategies.length; counter++) {
				newStrategies[counter] = new Strategy(currentLevel, isInit);
				char[][] initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, 2);
				newStrategies[counter].setInitStrategy(2, initStrategy);
				for (int currLevCounter = currentLevel; currLevCounter >= 3; currLevCounter--) {
					initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
							MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);
					newStrategies[counter].setInitStrategy(currLevCounter, initStrategy);
					char[][] joinStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
							MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);
					newStrategies[counter].setJoinStrategy(currLevCounter, joinStrategy);
				}
				if (newStrategies[counter].checkCompleteness() == false) {
					System.out.println("Error! createInitialPopulation method");
					System.exit(0);
				}
			}
		} else {
			for (int counter = 0; counter < newStrategies.length; counter++) {
				newStrategies[counter] = new Strategy(currentLevel, isInit);
				char[][] initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, 2);
				newStrategies[counter].setInitStrategy(2, initStrategy);
				for (int currLevCounter = currentLevel; currLevCounter >= 3; currLevCounter--) {
					char[][] joinStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
							MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);
					newStrategies[counter].setJoinStrategy(currLevCounter, joinStrategy);
				}
				if (newStrategies[counter].checkCompleteness() == false) {
					System.out.println("Error! createInitialPopulation method");
					System.exit(0);
				}
			}
		}

		return newStrategies;	
	}
	
	static Strategy createRandomIndiv(int currentLevel, boolean isInit) {
		Strategy newStrategy = new Strategy(currentLevel, isInit);
		StrategyCreator sCreator = new StrategyCreator();
		
		if (isInit) {
			char[][] initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
					MasterVariables.SIMULATECONSTRUCTIVISM, 2);
			newStrategy.setInitStrategy(2, initStrategy);
			for (int currLevCounter = currentLevel; currLevCounter >= 3; currLevCounter--) {
				initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);
				newStrategy.setInitStrategy(currLevCounter, initStrategy);
				char[][] joinStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);
				newStrategy.setJoinStrategy(currLevCounter, joinStrategy);
			}
			if (newStrategy.checkCompleteness() == false) {
				System.out.println("Error! createRandomIndiv method");
				System.exit(0);
			}
		} else {
			char[][] initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
					MasterVariables.SIMULATECONSTRUCTIVISM, 2);
			newStrategy.setInitStrategy(2, initStrategy);
			for (int currLevCounter = currentLevel; currLevCounter >= 3; currLevCounter--) {
				char[][] joinStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);
				newStrategy.setJoinStrategy(currLevCounter, joinStrategy);
			}
			if (newStrategy.checkCompleteness() == false) {
				System.out.println("Error! createInitialPopulation method");
				System.exit(0);
			}

		}

		return newStrategy;	
	}
	
	private void checkErrors() {
		if (evoSysSize < 2 || evoSysSize > MasterVariables.MAXSYSTEM || POPSIZE < 1) {
			System.out.println("Evolver class error!!");
			System.exit(0);
		}

	}

}
