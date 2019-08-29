package v14;

import java.util.Arrays;

public class TestCase {

	boolean[] surviving, warringA, warringB;
	int currentSystemSize, initialSystemSize, LeftInteractionRounds, PC;
	public double cap_1, cap_2, cap_3, cap_4, cap_5, cap_6, cap_7, cap_8, myCap, oppCap, smallerSideCap, largerSideCap,
			smallerSideLeaderCap, largerSideLeaderCap, leftCapSum, myEnmity, oppEnmity, orig_cap_1, orig_cap_2,
			orig_cap_3, orig_cap_4, orig_cap_5, orig_cap_6, orig_cap_7, orig_cap_8, capMed, capSD, orig_capMed,
			orig_capSD;
	public double sideACap, sideBCap;
	char[] strategy;
	double[] capabilities, original_capabilities;
	double[] randNums, randGauss;
	double[][] capChangeRates;
	char[] testedInitStrategy, testedJoinStrategy, testedBalanceStrategy;
	int testedStrategyIndex, randNumCounter, randGaussCounter, maxInteractionRounds;
	double init_proness, balance_proness, join_proness;
	Strategy[][] strategies;

	public TestCase(double[] capabilities, double[][] capChangeRates, double[] randNums, double[] randGauss,
			int testedStrategyIndex, double init_proness, double balance_proness, double join_proness,
			Strategy[][] strategies) {
		this.strategies = strategies;
		this.capabilities = capabilities;
		this.original_capabilities = capabilities;
		this.testedStrategyIndex = testedStrategyIndex;
		this.capChangeRates = capChangeRates;
		this.randNums = randNums;
		this.randGauss = randGauss;
		this.init_proness = init_proness;
		this.balance_proness = balance_proness;
		this.join_proness = join_proness;
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

	void resetTestCase(char[] testedInitStrategy, char[] testedJoinStrategy, char[] testedBalanceStrategy) {
		this.testedInitStrategy = testedInitStrategy;
		this.testedJoinStrategy = testedJoinStrategy;
		this.testedBalanceStrategy = testedBalanceStrategy;
		capabilities = original_capabilities.clone();
		LeftInteractionRounds = maxInteractionRounds;
		currentSystemSize = initialSystemSize;
		randNumCounter = 0;
		randGaussCounter = 0;
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

		// opponentDistance = 0;
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
		int counter = 0;

		capabilities[testedStrategyIndex] = capabilities[testedStrategyIndex]
				* (double) capChangeRates[LeftInteractionRounds - 1][counter++];
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i] && i != testedStrategyIndex) {
				capabilities[i] = capabilities[i] * (double) capChangeRates[LeftInteractionRounds - 1][counter++];
			}
		}
		

	}

	private void resolveWar() {
		double random = 1 + Math.abs(randGauss[randGaussCounter++] * MasterVariables.GAUSS_PROB_FACTOR);

		if ((random < (sideACap / sideBCap) && sideACap > sideBCap)
				|| (random > (sideBCap / sideACap) && sideBCap > sideACap)) {
			// kill side B states
			// assign their capabilities to side A depending on their power
			for (int i = 0; i < initialSystemSize; i++) {
				if (warringA[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideACap * sideBCap);
				} else if (warringB[i]) {
					surviving[i] = false;
				}
			}
		} else {
			// kill side A states
			// assign their capabilities to side B depending on their power
			for (int i = 0; i < initialSystemSize; i++) {
				if (warringB[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideBCap * sideACap);
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
			int stateCounter = 0;
			for (int i = 0; i < initialSystemSize; i++) {
				if (!surviving[i] || warringA[i] || warringB[i])
					continue;
				checkRandNumCounter();
				if (i != testedStrategyIndex && randNums[randNumCounter++] < MasterVariables.RANDOMPLAYER_PROB) {
					if (randNums[randNumCounter++] < randNums[randNumCounter++] * join_proness) {
						if (randNums[randNumCounter++] < randNums[randNumCounter++] * balance_proness) {
							if (sideACap < sideBCap) {
								sideACap += capabilities[i];
								warringA[i] = true;
							} else {
								sideBCap += capabilities[i];
								warringB[i] = true;
							}
						} else {
							if (sideACap < sideBCap) {
								sideBCap += capabilities[i];
								warringB[i] = true;
							} else {
								sideACap += capabilities[i];
								warringA[i] = true;
							}
						}
						leftCapSum = 1 - sideACap - sideBCap;
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
							smallerSideCap = sideBCap;
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
					oppCap = 1;
					if (i == testedStrategyIndex && currentSystemSize == initialSystemSize)
						strategy = testedJoinStrategy;
					else
						strategy = strategies[currentSystemSize - 2][stateCounter++].getJoinStrategy(currentSystemSize);
					leftCapSum = 1 - sideACap - sideBCap - myCap;
					if (Math.abs(leftCapSum) < 1E-10)
						leftCapSum = 0;
					if (run() == 1) {
						PC = 0;
						if (i == testedStrategyIndex && currentSystemSize == initialSystemSize)
							strategy = testedBalanceStrategy;
						else
							strategy = strategies[currentSystemSize - 2][stateCounter].getBalanceStrategy(currentSystemSize);
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
						leftCapSum = 1 - sideACap - sideBCap;
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
							smallerSideCap = sideBCap;
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
		
		/*opponentDistance++;
		if (opponentDistance >= currentSystemSize)
			opponentDistance = 1;*/

		int stateCounter = 0;
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i]) {
				checkRandNumCounter();
				
				if (i != testedStrategyIndex && randNums[randNumCounter++] < MasterVariables.RANDOMPLAYER_PROB) {
					if (randNums[randNumCounter++] < randNums[randNumCounter++] * init_proness) {
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
						leftCapSum = 1 - sideACap - sideBCap;
						if (Math.abs(leftCapSum) < 1E-10)
							leftCapSum = 0;
						if (sideACap < sideBCap) {
							smallerSideCap = smallerSideLeaderCap = sideACap;
							largerSideCap = largerSideLeaderCap = sideBCap;
						} else {
							smallerSideCap = smallerSideLeaderCap = sideBCap;
							largerSideCap = largerSideLeaderCap = sideACap;
						}
						return true;
					}
				} else {
					myCap = capabilities[i];
					if (i == testedStrategyIndex && currentSystemSize == initialSystemSize)
						strategy = testedInitStrategy;
					else
						strategy = strategies[currentSystemSize - 2][stateCounter++].getInitStrategy(currentSystemSize);
					
					/*int k = i, counterToOpp = opponentDistance;

					while (counterToOpp > 0) {
						k++;
						if (k == initialSystemSize)
							k = 0;
						if (surviving[k])
							counterToOpp--;
					}*/
					for (int k = 0; k < initialSystemSize; k++) {
						if (i == k || !surviving[k])
							continue;					
						PC = 0;
						oppCap = capabilities[k];
						leftCapSum = 1 - myCap - oppCap;
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

		double total = 0;
		for (int i = 0; i <= lastSuvivIndex; i++)
			if (surviving[i])
				total += capabilities[i];

		double sum = 0;
		for (int i = 0; i < lastSuvivIndex; i++)
			if (surviving[i]) {
				capabilities[i] = capabilities[i] / total;
				sum += capabilities[i];
			}

		capabilities[lastSuvivIndex] = 1 - sum;

		for (int i = 0; i < lastSuvivIndex; i++) {
			if (surviving[i] && Math.abs(capabilities[i]) < 1E-10) {
				capabilities[i] = 0.0001;
				normalizeCapabilities();
			}
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

		capSD = Math.sqrt(capSS / currentSystemSize);

		// calculate median for the current surviving states
		double[] temp = new double[currentSystemSize];
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

	private double run() {

		int primitive = strategy[PC++];

		switch (primitive) {
		case MasterVariables.CAPMED:
			return (capMed);
		case MasterVariables.CAPSD:
			return (capSD);
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

		case MasterVariables.LARGERSIDECAP:
			return (largerSideCap);

		case MasterVariables.SMALLERSIDELEADERCAP:
			return (smallerSideLeaderCap);

		case MasterVariables.LARGERSIDELEADERCAP:
			return (largerSideLeaderCap);

		case MasterVariables.LEFTCAPSUM:
			return (leftCapSum);

		case MasterVariables.MYENMITY:
			return (myEnmity);
		case MasterVariables.OPPENMITY:
			return (oppEnmity);
		case MasterVariables.GT:
			double left = run(),
			right = run();
			if (left > right && Math.abs(left - right) > 1E-10)
				return (1);
			else
				return (0);
		case MasterVariables.LT:
			double left2 = run(),
			right2 = run();
			if (left2 < right2 && Math.abs(left2 - right2) > 1E-10)
				return (1);
			else
				return (0);
		case MasterVariables.EQ:
			if (Math.abs(run() - run()) < 1E-10)
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
			double num = run(), den = run();
			if (Math.abs(den) < 1E-10)
				return (num);
			else
				return (num / den);
		}
		case MasterVariables.IF_THEN_ELSE:
			if (run() == 1)
				return run();
			else {
				run();
				return run();
			}
		default:
			return (MasterVariables.randNum[primitive]);
		}

	}

	

}
