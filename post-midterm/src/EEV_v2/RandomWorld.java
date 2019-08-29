package EEV_v2;

import java.util.Random;

public class RandomWorld {

	int attackOrder, worldSize, testedStateIndex;
	int[] resDistr, terrDistr;
	Random rd = new Random();

	public RandomWorld(int attackOrder, int[] resDistr, int[] terrDistr) {
		this.attackOrder = attackOrder;
		this.resDistr = resDistr.clone();
		this.terrDistr = terrDistr.clone();
		worldSize = resDistr.length;
		testedStateIndex = 0;
	}

	public double reCalcValue() {
		// shuffle to ensure random initiators/joiners order
		//System.out.println("XXXXXXXXXXXXXXXXXXXXX");
		//System.out.println("testedStateIndex " + testedStateIndex + " attackOrder " + attackOrder + " res dist " + resDistr[0] + " " + resDistr[1]);
		for (int i = worldSize - 1; i > 0; i--) {
			int index = rd.nextInt(i + 1);

			if (index == i)
				continue;

			int temp;

			temp = resDistr[index];
			resDistr[index] = resDistr[i];
			resDistr[i] = temp;

			temp = terrDistr[index];
			terrDistr[index] = terrDistr[i];
			terrDistr[i] = temp;

			if (attackOrder == i) {
				attackOrder = index;
			} else if (attackOrder == index) {
				attackOrder = i;
			}

			if (testedStateIndex == i) {
				testedStateIndex = index;
			} else if (testedStateIndex == index) {
				testedStateIndex = i;
			}
		}

		int[] randInitStrategies = new int[worldSize];
		int[] randJoinStrategies = new int[worldSize];
		boolean[] sideA = new boolean[worldSize];
		boolean[] sideB = new boolean[worldSize];
		randInitStrategies[testedStateIndex] = -99;
		randJoinStrategies[testedStateIndex] = -99;

		// generate random strategies for other states
		for (int i = 0; i < randInitStrategies.length; i++) {
			if (i == testedStateIndex)
				continue;
			randInitStrategies[i] = rd.nextInt(worldSize);
			randJoinStrategies[i] = rd.nextInt(3);
		}

		boolean atWar = false;

		// calculate who's on both sides
		double sideAcap = 0;
		double sideBcap = 0;
		double potentialTerrSpoil = 0;
		if (attackOrder != testedStateIndex) {
			atWar = true;
			sideA[testedStateIndex] = true;
			sideB[attackOrder] = true;
		} 

		if (atWar)
			findJoiners(randJoinStrategies, sideA, sideB);
		else {
			atWar = findInitiators(randInitStrategies, sideA, sideB);
			if (atWar) {
				findJoiners(randJoinStrategies, sideA, sideB);
			}
		}

		if (atWar == false)
			return (0);

		for (int i = 0; i < resDistr.length; i++) {
			if (sideA[i])
				sideAcap += resDistr[i];
			else if (sideB[i]) {
				sideBcap += resDistr[i];
				potentialTerrSpoil += terrDistr[i];
			}
		}

		// determine real power as function of observed resources
		double realSideACap = sideAcap + (sideAcap * rd.nextGaussian() * 0.5);
		double realSideBCap = sideBcap + (sideBcap * rd.nextGaussian() * 0.5);

		if (realSideBCap > realSideACap)
			return -terrDistr[testedStateIndex];

		double share = resDistr[testedStateIndex] / sideAcap;

		return (((double) potentialTerrSpoil * share));
	}

	private boolean findInitiators(int[] randInitStrategies, boolean[] sideA, boolean[] sideB) {
		boolean atWar[] = new boolean[randInitStrategies.length];
		for (int i = 0; i < randInitStrategies.length; i++) {
			if (i == testedStateIndex)
				continue;
			if (atWar[i] || randInitStrategies[i] == i)
				continue;
			if (randInitStrategies[i] == testedStateIndex) {
				sideA[testedStateIndex] = true;
				sideB[i] = true;
				return true;
			}
			atWar[i] = true;
			atWar[randInitStrategies[i]] = true;
		}
		return false;
	}

	private void findJoiners(int[] randJoinStrategies, boolean[] sideA, boolean[] sideB) {
		for (int i = 0; i < randJoinStrategies.length; i++) {
			if (i == testedStateIndex)
				continue;
			if (sideA[i] || sideB[i])
				continue;
			if (randJoinStrategies[i] == 1) {
				sideA[i] = true;
			} else if (randJoinStrategies[i] == 2) {
				sideB[i] = true;
			} else if (randJoinStrategies[i] == 0) {

			} else {
				Thread.dumpStack();
				System.exit(0);
			}
		}

	}

}
