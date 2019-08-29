package EEV_v4;

import java.util.Random;

public class RandomWorld {

	int attackOrder, worldSize, testedStateIndex, orig_attackOrder, orig_worldSize, orig_testedStateIndex;
	int numOfRounds, currentRoundCounter, origTerritory;;
	int[] resDistr, terrDistr, orig_resDistr, orig_terrDistr;
	boolean isDead;
	Random rd = new Random();

	public RandomWorld(int attackOrder, int[] resDistr, int[] terrDistr, int numOfRounds) {
		this.attackOrder = orig_attackOrder = attackOrder;
		this.resDistr = resDistr.clone();
		this.terrDistr = terrDistr.clone();
		orig_resDistr = resDistr.clone();
		orig_terrDistr = terrDistr.clone();
		worldSize = resDistr.length;
		testedStateIndex = orig_testedStateIndex = 0;
		this.numOfRounds = numOfRounds;
		origTerritory = terrDistr[0];
	}

	public double reCalcValue() {
		//System.out.println("xxxxxxxxxxxxxxxxxxxxx");
		attackOrder = orig_attackOrder;
		testedStateIndex = orig_testedStateIndex;
		resDistr = orig_resDistr.clone();
		terrDistr = orig_terrDistr.clone();
		currentRoundCounter = numOfRounds;
		isDead = false ;
		return simulateRound();
	}
	
	double simulateRound(){
		if (isDead)
			return -origTerritory;
		
		shuffle();	

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
		if (currentRoundCounter == numOfRounds && attackOrder != testedStateIndex) {
			atWar = true;
			sideA[testedStateIndex] = true;
			sideB[attackOrder] = true;
		} 

		currentRoundCounter -- ;
		
		
		if (atWar)
			findJoiners(randJoinStrategies, sideA, sideB);
		else {
			atWar = findInitiators(randInitStrategies, sideA, sideB);
			if (atWar) {
				findJoiners(randJoinStrategies, sideA, sideB);
			}
		}

		if (atWar == false){	
			if (currentRoundCounter == 0)
				return (terrDistr[testedStateIndex] - origTerritory);
			changeResources();
			return simulateRound();
		}

		for (int i = 0; i < resDistr.length; i++) {
			if (sideA[i])
				sideAcap += resDistr[i];
			else if (sideB[i]) {
				sideBcap += resDistr[i];
				potentialTerrSpoil += terrDistr[i];
			}
		}

		// determine real power as function of observed resources
		double realSideACap = sideAcap + (sideAcap * rd.nextGaussian() * 0.4);
		double realSideBCap = sideBcap + (sideBcap * rd.nextGaussian() * 0.4);

		//System.out.println(realSideACap + " " + realSideBCap);
		if (realSideBCap > realSideACap){
			isDead = true;
			return -origTerritory;
		}

		for (int i = 0; i < resDistr.length; i++) {
			if (sideA[i]) {
				double share = resDistr[i] * potentialTerrSpoil / sideAcap;
				terrDistr[i] += share;
			} else {
				resDistr[i] = terrDistr[i] = 0;
			}
		}

		if (currentRoundCounter == 0)
			return (terrDistr[testedStateIndex] - origTerritory);
		changeResources();
		return simulateRound();
	}

	private void changeResources() {
		//System.out.println("change, tested index " + testedStateIndex);
		for (int i = 0; i < resDistr.length; i++) {
			if (terrDistr[i] == 0) {
				continue;
			}
			resDistr[i] =  (1 + rd.nextInt(terrDistr[i]))  ;
			//System.out.println(terrDistr[i] + " " + resDistr[i]);
		}		
	}

	private void shuffle() {
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
	}

	private boolean findInitiators(int[] randInitStrategies, boolean[] sideA, boolean[] sideB) {
		//System.out.println("findInitiators");
		boolean atWar[] = new boolean[randInitStrategies.length];
		for (int i = 0; i < randInitStrategies.length; i++) {
			if (i == testedStateIndex)
				continue;
			if (atWar[i] || randInitStrategies[i] == i || terrDistr[i] ==0)
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
			if (sideA[i] || sideB[i] || terrDistr[i] ==0)
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
