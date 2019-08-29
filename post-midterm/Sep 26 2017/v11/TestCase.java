package v11;

import java.util.Arrays;

public class TestCase {

	boolean[] surviving, warringA, warringB;
	int currentSystemSize, initialSystemSize, LeftInteractionRounds, PC;
	public int capMin, capMax, myCap, oppCap, smallerSideCap, largerSideCap, smallerSideLeaderCap,
			largerSideLeaderCap, leftCapSum, myEnmity, oppEnmity, orig_capMin, orig_capMax, capMed, capSD, orig_capMed, orig_capSD;
	public int sideACap, sideBCap;
	char[] strategy;
	int[] capabilities, original_capabilities;
	double[] randNums, randGauss;
	int[][] capChangeRates;
	Strategy[] strategies;
	int testedStrategyIndex, randNumCounter, randGaussCounter, maxInteractionRounds, opponentDistance;

	public TestCase(int[] capabilities, int[][] capChangeRates, double[] randNums, double[] randGauss,
			int testedStrategyIndex) {
		this.capabilities = capabilities;
		this.original_capabilities = capabilities;
		this.testedStrategyIndex = testedStrategyIndex;
		this.capChangeRates = capChangeRates;
		this.randNums = randNums;
		this.randGauss = randGauss;
		LeftInteractionRounds = MasterVariables.INTERACTIONROUNDS;
		maxInteractionRounds = MasterVariables.INTERACTIONROUNDS;
		currentSystemSize = capabilities.length;
		initialSystemSize = capabilities.length;
		randNumCounter = 0;
		randGaussCounter = 0;
		surviving = new boolean[currentSystemSize];
		for (int i = 0; i < currentSystemSize; i++)
			surviving[i] = true;
		calculateSystemVariables();
		orig_capMed = capMed;
		orig_capSD = capSD;
		orig_capMin = capMin;
		orig_capMax = capMax;
		
		myEnmity = 1;
		oppEnmity = 1;
	}

	void resetTestCase(Strategy[] strategies) {
		this.strategies = strategies;
		capabilities = original_capabilities.clone();
		LeftInteractionRounds = MasterVariables.INTERACTIONROUNDS;
		currentSystemSize = initialSystemSize;
		randNumCounter = 0;
		randGaussCounter = 0;
		checkErrors();
		surviving = new boolean[currentSystemSize];
		for (int i = 0; i < currentSystemSize; i++)
			surviving[i] = true;
		capMed = orig_capMed;
		capSD = orig_capSD;
		capMin = orig_capMin;
		capMax = orig_capMax;

		warringA = null;
		warringB = null;

		opponentDistance = 0;
	}
	
	int simulate() {
		boolean warInitiated = false;
		LeftInteractionRounds--;

		do {
			warInitiated = findInitators();
			if (warInitiated) {
				if (currentSystemSize > 2)
					findJoiners();
				resolveWar();
				calculateCurrentSystemSize();
				if (surviving[testedStrategyIndex] == false)
					return (maxInteractionRounds - LeftInteractionRounds - 1);
				else if (currentSystemSize == 1)
					return maxInteractionRounds;
				normalizeCapabilities();
				calculateSystemVariables();
			}
		} while (warInitiated);
		
		if (LeftInteractionRounds > 0) {
			changeVariables();
			normalizeCapabilities();
			calculateSystemVariables();
			return (simulate());
		} else
			return maxInteractionRounds;
	}

	private void changeVariables() {
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i])
				capabilities[i] = capabilities[i] * capChangeRates[LeftInteractionRounds - 1][i];
		}
	}

	private void resolveWar() {
		double random = 1 + Math.abs(randGauss[randGaussCounter++] * MasterVariables.GAUSS_PROB_FACTOR);
		
		if ((random < ((double)sideACap / (double)sideBCap) && sideACap > sideBCap)
				|| (random > ((double)sideBCap / (double)sideACap) && sideBCap > sideACap)) {
			// kill side B states
			// assign their capabilities to side A depending on their power
			for (int i = 0; i < initialSystemSize; i++) {
				if (warringA[i]) {
					capabilities[i] = (int) (capabilities[i] + ((double)capabilities[i] * (double)sideBCap / (double)sideACap ));
				} else if (warringB[i]) {
					surviving[i] = false;
				}
			}
		} else {
			// kill side A states
			// assign their capabilities to side B depending on their power
			for (int i = 0; i < initialSystemSize; i++) {
				if (warringB[i]) {
					capabilities[i] = (int) (capabilities[i] + ((double)capabilities[i] * (double)sideACap / (double)sideBCap ));
				} else if (warringA[i]) {
					surviving[i] = false;
				}
			}
		}
	}

	private void findJoiners() {
		boolean newJoiner;
		findJoiners: do {
			newJoiner = false;
			if (Math.abs(leftCapSum) < 1E-10)
				break;
			for (int i = 0; i < initialSystemSize; i++) {
				if (!surviving[i] || warringA[i] || warringB[i])
					continue;
				checkRandNumCounter();
				if (i != testedStrategyIndex && randNums[randNumCounter++] < MasterVariables.RANDOMPLAYER_PROB) {
					if (randNums[randNumCounter++] < randNums[randNumCounter++]) {						
						if (randNums[randNumCounter++] > randNums[randNumCounter++]) {
							sideACap += capabilities[i];
							warringA[i] = true;
						} else {
							sideBCap += capabilities[i];
							warringB[i] = true;
						}
						leftCapSum = 1000 - sideACap - sideBCap;
						if (Math.abs(leftCapSum) < 1E-10)
							leftCapSum = 0;
						if (sideACap < sideBCap) {
							smallerSideCap = sideACap;
							largerSideCap = sideBCap;
							largerSideLeaderCap = smallerSideLeaderCap = -1;
							for (int j = 0; j < initialSystemSize; j++) {
								if (warringA[j] && capabilities[j] > smallerSideLeaderCap)
									smallerSideLeaderCap = capabilities[j];
								if (warringB[j] && capabilities[j] > largerSideLeaderCap)
									largerSideLeaderCap = capabilities[j];
							}
						} else {
							smallerSideCap =  sideBCap;
							largerSideCap = sideACap;
							largerSideLeaderCap = smallerSideLeaderCap = -1;
							for (int j = 0; j < initialSystemSize; j++) {
								if (warringB[j] && capabilities[j] > smallerSideLeaderCap)
									smallerSideLeaderCap = capabilities[j];
								if (warringA[j] && capabilities[j] > largerSideLeaderCap)
									largerSideLeaderCap = capabilities[j];
							}
						}
						
						newJoiner = true;
						continue findJoiners;
					}
				} else {
					PC = 0;
					myCap = capabilities[i];
					oppCap = 1 ; 
					strategy = strategies[i].getJoinStrategy(currentSystemSize);
					if (run() == 1) {
						PC = 0;
						strategy = strategies[i].getBalanceStrategy(currentSystemSize);
						if (run() == 1) {
							if (sideACap < sideBCap) {
								sideACap += myCap;
								warringA[i] = true;
							} else {
								sideBCap += myCap;
								warringB[i] = true;
							}	
						} else {
							if (sideACap > sideBCap) {
								sideACap += myCap;
								warringA[i] = true;
							} else {
								sideBCap += myCap;
								warringB[i] = true;
							}	
						}
						leftCapSum = 1000 - sideACap - sideBCap;
						if (Math.abs(leftCapSum) < 1E-10)
							leftCapSum = 0;
						if (sideACap < sideBCap) {
							smallerSideCap = sideACap;
							largerSideCap = sideBCap;
							largerSideLeaderCap = smallerSideLeaderCap = -1;
							for (int j = 0; j < initialSystemSize; j++) {
								if (warringA[j] && capabilities[j] > smallerSideLeaderCap)
									smallerSideLeaderCap = capabilities[j];
								if (warringB[j] && capabilities[j] > largerSideLeaderCap)
									largerSideLeaderCap = capabilities[j];
							}
						} else {
							smallerSideCap =  sideBCap;
							largerSideCap = sideACap;
							largerSideLeaderCap = smallerSideLeaderCap = -1;
							for (int j = 0; j < initialSystemSize; j++) {
								if (warringB[j] && capabilities[j] > smallerSideLeaderCap)
									smallerSideLeaderCap = capabilities[j];
								if (warringA[j] && capabilities[j] > largerSideLeaderCap)
									largerSideLeaderCap = capabilities[j];
							}
						}
						
						newJoiner = true;
						continue findJoiners;
					}		
				}
			}
		} while (newJoiner);
	}

	private boolean findInitators() {
		opponentDistance ++;
		if (opponentDistance >= currentSystemSize)
			opponentDistance = 1;
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i]) {
				checkRandNumCounter();
				if (i != testedStrategyIndex && randNums[randNumCounter++] < MasterVariables.RANDOMPLAYER_PROB) {
					if (randNums[randNumCounter++]  < randNums[randNumCounter++]) {
						warringA = new boolean[initialSystemSize];
						warringB = new boolean[initialSystemSize];
						warringA[i] = true;
						sideACap = capabilities[i];
						int target = i + 1;
						if (target == initialSystemSize)
							target = 0;
						while (!surviving[target] || target == i) {
							target++;
							if (target == initialSystemSize)
								target = 0;
						}
						warringB[target] = true;
						sideBCap = capabilities[target];
						leftCapSum = 1000 - sideACap - sideBCap;
						if (Math.abs(leftCapSum) < 1E-10)
							leftCapSum = 0;
						if (sideACap < sideBCap) {
							smallerSideCap = smallerSideLeaderCap =sideACap;
							largerSideCap = largerSideLeaderCap = sideBCap;
						} else {
							smallerSideCap = smallerSideLeaderCap = sideBCap;
							largerSideCap = largerSideLeaderCap = sideACap;
						}
						return true;
					}
				} else {
					myCap = capabilities[i];
					strategy = strategies[i].getInitStrategy(currentSystemSize);
					int k = i, counterToOpp = opponentDistance;
					
					while (counterToOpp > 0) {
						k++;
						if (k == initialSystemSize)
							k = 0;
						if (surviving[k])
							counterToOpp--;
					}
					
					PC = 0;
					oppCap = capabilities[k];
					leftCapSum = 1000 - myCap - oppCap;
					if (Math.abs(leftCapSum) < 1E-10)
						leftCapSum = 0;
					smallerSideCap = largerSideCap = smallerSideLeaderCap = largerSideLeaderCap = 1;
					if (run() == 1) {
						warringA = new boolean[initialSystemSize];
						warringB = new boolean[initialSystemSize];
						warringA[i] = true;
						warringB[k] = true;
						sideACap = myCap;
						sideBCap = oppCap;
						if (sideACap < sideBCap) {
							smallerSideCap = smallerSideLeaderCap = sideACap;
							largerSideCap = largerSideLeaderCap = sideBCap;
						} else {
							smallerSideCap = smallerSideLeaderCap = sideBCap;
							largerSideCap = largerSideLeaderCap = sideACap;
						}
						return true;
					}
				}
			}
		}
		return false;
	}

	private void checkRandNumCounter() {
		if (randNums.length - 5 <= randNumCounter)
			randNumCounter = 0;
	}

	private void normalizeCapabilities() {
		int lastSuvivIndex = initialSystemSize - 1;
		while (!surviving[lastSuvivIndex])
			lastSuvivIndex -= 1;

		int total = 0;
		for (int i = 0; i <= lastSuvivIndex; i++)
			if (surviving[i])
				total += capabilities[i];
		

		int sum = 0;
		for (int i = 0; i <= lastSuvivIndex; i++)
			if (surviving[i]) {
				capabilities[i] = capabilities[i] * 1000 / total;
				if (capabilities[i] == 0){
					capabilities[i] = 1;
				}
				sum += capabilities[i];
			}

		if (sum <1000){
			//System.out.println("here "  + sum);
			capabilities[lastSuvivIndex]++;
			normalizeCapabilities();
		} else if (sum >1000){
			normalizeCapabilities();
		}
	}

	private void calculateSystemVariables() {
		// the SD and Median are deliberately rounded to integer to avoid the
		// imprecision of double calculations
		double capAvg = (double) 1 / currentSystemSize, capSS = 0;
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i]) {
				capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
			}
		}

		capSD = (int) Math.sqrt(capSS / currentSystemSize);

		// calculate median for the current surviving states
		int[] temp = new int[currentSystemSize];
		int remainingStates = currentSystemSize, i = 0;
		do {
			if (surviving[i])
				temp[--remainingStates] = capabilities[i];
			i++;
		} while (remainingStates > 0);

		Arrays.sort(temp);
		int middle = currentSystemSize / 2;
		if (currentSystemSize % 2 == 1) {
			capMed = temp[middle];
		} else {
			capMed = (temp[middle - 1] + temp[middle]) / 2;
		}

		capMax = temp[currentSystemSize - 1];
		capMin = temp[0];

	}

	private void calculateCurrentSystemSize() {
		currentSystemSize = 0;
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i])
				currentSystemSize++;
		}
	}

	private int run() {

		char primitive = strategy[PC++];

		switch (primitive) {
		case MasterVariables.CAPMED:
			//XXX
			return (capMed);
			//return (1);		
		case MasterVariables.CAPSD:
			//XXX
			return (capSD);
			//return (1);
		case MasterVariables.CAPMIN:
			return (capMin);
			//return (1);
		case MasterVariables.CAPMAX:
			//XXX
			return (capMax);
			//return (1);
		case MasterVariables.MYCAP:
			return (myCap);
		case MasterVariables.OPPCAP:
			return (oppCap);
		case MasterVariables.SMALLERSIDECAP:
			return (smallerSideCap);
			//return (1);
		case MasterVariables.LARGERSIDECAP:
			return (largerSideCap);
			//return (1);
		case MasterVariables.SMALLERSIDELEADERCAP:
			//XXX
			return (smallerSideLeaderCap);
			//return (1);
		case MasterVariables.LARGERSIDELEADERCAP:
			//XXX
			return (largerSideLeaderCap);
			//return (1);
		case MasterVariables.LEFTCAPSUM:
			//XXX
			return (leftCapSum);
			//return (1);
		case MasterVariables.MYENMITY:
			return (myEnmity);
		case MasterVariables.OPPENMITY:
			return (oppEnmity);
		case MasterVariables.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case MasterVariables.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case MasterVariables.EQ:
			if (run() == run())
				return (1);
			else
				return (0);
		case MasterVariables.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case MasterVariables.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case MasterVariables.ADD:
			return (run() + run());
		case MasterVariables.SUB:
			return (run() - run());
		case MasterVariables.MUL:
			return (run() * run());
		case MasterVariables.DIV: {
			int num = run(), den = run();
			if (den == 0)
				return (num);
			else
				return (num / den);
		}
		default:
			return (MasterVariables.randNum[primitive]);
		}

	}
	
	private void checkErrors() {
		if (capabilities.length != strategies.length) {
			System.out.println("TestCase class error !!!");
			System.exit(0);
		}
	}

}
