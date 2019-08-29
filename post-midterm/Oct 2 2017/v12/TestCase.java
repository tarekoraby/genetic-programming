package v12;

import java.util.Arrays;

public class TestCase {

	boolean[] surviving, warringA, warringB;
	int currentSystemSize, initialSystemSize, LeftInteractionRounds, PC;
	public int cap_1, cap_2, cap_3, cap_4, cap_5, cap_6, cap_7, cap_8, myCap, oppCap, smallerSideCap, largerSideCap,
			smallerSideLeaderCap, largerSideLeaderCap, leftCapSum, myEnmity, oppEnmity, orig_cap_1, orig_cap_2,
			orig_cap_3, orig_cap_4, orig_cap_5, orig_cap_6, orig_cap_7, orig_cap_8, capMed, capSD, orig_capMed,
			orig_capSD;
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
		orig_cap_1 = cap_1;
		orig_cap_2 = cap_2;
		orig_cap_3 = cap_3;
		orig_cap_4 = cap_4;
		orig_cap_5 = cap_5;
		orig_cap_6 = cap_6;
		orig_cap_7 = cap_7;
		orig_cap_8 = cap_8;

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
		cap_1 = orig_cap_1;
		cap_2 = orig_cap_2;
		cap_3 = orig_cap_3;
		cap_4 = orig_cap_4;
		cap_5 = orig_cap_5;
		cap_6 = orig_cap_6;
		cap_7 = orig_cap_7;
		cap_8 = orig_cap_8;

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
					leftCapSum = 1000 - sideACap - sideBCap - myCap;
					if (Math.abs(leftCapSum) < 1E-10)
						leftCapSum = 0;
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

		switch (currentSystemSize) {
		case 2:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = 0;
			cap_4 = 0;
			cap_5 = 0;
			cap_6 = 0;
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 3:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = 0;
			cap_5 = 0;
			cap_6 = 0;
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 4:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = 0;
			cap_6 = 0;
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 5:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = temp[currentSystemSize - 5];
			cap_6 = 0;
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 6:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = temp[currentSystemSize - 5];
			cap_6 = temp[currentSystemSize - 6];
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 7:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = temp[currentSystemSize - 5];
			cap_6 = temp[currentSystemSize - 6];
			cap_7 = temp[currentSystemSize - 7];
			cap_8 = 0;
			break;
		case 8:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = temp[currentSystemSize - 5];
			cap_6 = temp[currentSystemSize - 6];
			cap_7 = temp[currentSystemSize - 7];
			cap_8 = temp[currentSystemSize - 8];
			break;
		default:
			System.out.println("ERRORR!!!!!! Test case class. assigning capailities error !!");
			System.exit(0);
		}
		
		

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
			// XXX
			return (capSD);
			// return (1);
		case MasterVariables.CAP_1:
			return (cap_1);
		case MasterVariables.CAP_2:
			return (cap_2);
		case MasterVariables.CAP_3:
			return (cap_3);
		case MasterVariables.CAP_4:
			return (cap_4);
		case MasterVariables.CAP_5:
			return (cap_5);
		case MasterVariables.CAP_6:
			return (cap_6);
		case MasterVariables.CAP_7:
			return (cap_7);
		case MasterVariables.CAP_8:
			return (cap_8);
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
