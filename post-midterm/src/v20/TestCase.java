package v20;

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
	int testedStrategyIndex, testedOrder, myOrder, randNumCounter, randGaussCounter, maxInteractionRounds;
	double init_proness, balance_proness, join_proness;
	StrategyCollection strategies;
	boolean simulateInit;
	Strategy testedStrategy;

	public TestCase(double[] capabilities, double[][] capChangeRates, double[] randNums, double[] randGauss,
			int testedOrder, double init_proness, double balance_proness, double join_proness, StrategyCollection strategies, boolean isInit) {
		this.strategies= strategies;
		this.simulateInit = isInit;
		this.capabilities = capabilities;
		this.original_capabilities = capabilities;
		this.testedOrder = testedOrder;
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
		
		calcTestedStrategyIndex();
	}

	private void calcTestedStrategyIndex() {
		double[] temp = capabilities.clone();
		Arrays.sort(temp);
		double[] temp2 = capabilities.clone();
		int j = 0;
		for (int i = capabilities.length - 1; i >= 0; i--)
			temp2[j++] = temp[i];
		temp = temp2;
		
		testedStrategyIndex = -1;
		switch (testedOrder) {
		case 1:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[0]);
			break;
		case 2:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[1]);
			break;
		case 3:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[2]);
			break;
		case 4:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[3]);
			break;
		case 5:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[4]);
			break;
		case 6:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[5]);
			break;
		case 7:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[6]);
			break;
		case 8:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[7]);
			break;
		}
		
	}

	private void resetTestCase(Strategy testedStrategy) {
		this.testedStrategy = testedStrategy;
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
	
	int test(Strategy testedStrategy){
		resetTestCase(testedStrategy);
		return simulate();	
	}

	private int simulate() {
		boolean warInitiated = false;
		LeftInteractionRounds--;

		do {
			if (simulateInit == false && initialSystemSize == currentSystemSize) {
				warInitiated = true;
				simulateJoiners();
				findJoiners();
				resolveWar();
				calculateCurrentSystemSize();
				if (surviving[testedStrategyIndex] == false)
					return (maxInteractionRounds - LeftInteractionRounds - 1);
				else if (currentSystemSize == 1)
					return maxInteractionRounds;
				normalizeCapabilities();
				calculateSystemVariables();
			} else {
				
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
		//xxx
		double random = 1 + Math.abs(randGauss[randGaussCounter++] * MasterVariables.GAUSS_PROB_FACTOR);
		
		double random1 =  Math.abs(randGauss[randGaussCounter++] );
		double random2 = Math.abs(randGauss[randGaussCounter++] );

		if ((sideACap + sideACap * random1 > sideBCap + sideBCap * random2)){
		//if ((random < (sideACap / sideBCap) && sideACap > sideBCap)
			//	|| (random > (sideBCap / sideACap) && sideBCap > sideACap)) {
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
			for (int i = 0; i < initialSystemSize; i++) {
				if (!surviving[i] || warringA[i] || warringB[i])
					continue;

				PC = 0;
				myCap = capabilities[i];
				oppCap = 1;
				char[][] joinStrategy = null;
				myOrder = calcMyOrder(myCap);
				if (currentSystemSize == initialSystemSize && myOrder == testedOrder && simulateInit==false)
					joinStrategy = testedStrategy.getJoinStrategy(currentSystemSize);
				else
					joinStrategy = strategies.getJoinStrategy(currentSystemSize, myOrder).getJoinStrategy(
							currentSystemSize);

				leftCapSum = 1 - sideACap - sideBCap - myCap;
				if (Math.abs(leftCapSum) < 1E-10)
					leftCapSum = 0;
				int output = runJoin(joinStrategy);

				if (output >0 ){

					if (output == 1) {
						if (currentSystemSize == initialSystemSize && myOrder == testedOrder && simulateInit==false)
							testedStrategy.balances++;
						if (sideACap < sideBCap) {
							sideACap += myCap;
							warringA[i] = true;
						} else {
							sideBCap += myCap;
							warringB[i] = true;
						}
					} else if (output==2){
						if (currentSystemSize == initialSystemSize && myOrder == testedOrder && simulateInit==false)
							testedStrategy.bandwagons++;
						if (sideACap > sideBCap) {
							sideACap += myCap;
							warringA[i] = true;
						} else {
							sideBCap += myCap;
							warringB[i] = true;
						}
					} else {
						System.exit(0);
					}
				/*if (runJoin(joinStrategy) == 1) {
					PC = 0;
					char[][] balanceStrategy = strategies[i].getBalanceStrategy(currentSystemSize);

					if (runJoin(balanceStrategy) == 1) {
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
					}*/
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

				} else {
					if (currentSystemSize == initialSystemSize && myOrder == testedOrder && simulateInit==false)
						testedStrategy.buckpasses++;
				}

			}
		} while (newJoiner);
	}

	private boolean findInitators() {

		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i]) {

				myCap = capabilities[i];
				char[][] initStrategy = null;

				myOrder = calcMyOrder(myCap);
				
				if (currentSystemSize == initialSystemSize && myOrder == testedOrder && simulateInit==true)
					initStrategy = testedStrategy.getInitStrategy(currentSystemSize);
				else{
					initStrategy = MasterVariables.bestStratgies.getRandomInitStratgy(currentSystemSize, myOrder).getInitStrategy(currentSystemSize);
					//initStrategy = strategies.getInitStrategy(currentSystemSize, myOrder).getInitStrategy(
					//		currentSystemSize);
				}

				myCap = capabilities[i];
				int targetOrder = runInit(initStrategy);
				if (!(currentSystemSize == initialSystemSize && myOrder == testedOrder && simulateInit == true))
					if (randNums[randNumCounter++] < MasterVariables.RANDOMPLAYER_PROB)
						if (randNums[randNumCounter++] < randNums[randNumCounter++])
							targetOrder = 0;
						else {
						targetOrder=1;
					}
				if (targetOrder > 0) {
					if (currentSystemSize == initialSystemSize && myOrder == testedOrder && simulateInit==true)
						testedStrategy.attacks++;
					int k = findTargetIndex(targetOrder, i);
					warringA = new boolean[initialSystemSize];
					warringB = new boolean[initialSystemSize];
					warringA[i] = true;
					warringB[k] = true;
					sideACap = myCap;
					sideBCap = capabilities[k];
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
		return false;
	}


	private int calcMyOrder(double myCap) {
		int myOrder = -1;

		if (myCap == cap_1)
			myOrder = 1;
		else if (myCap == cap_2)
			myOrder = 2;
		else if (myCap == cap_3)
			myOrder = 3;
		else if (myCap == cap_4)
			myOrder = 4;
		else if (myCap == cap_5)
			myOrder = 5;
		else if (myCap == cap_6)
			myOrder = 6;
		else if (myCap == cap_7)
			myOrder = 7;
		else if (myCap == cap_8)
			myOrder = 8;
		else {
			System.out.println("Error! TestCase classCase ... Method 1");
			System.exit(0);
		}

		return myOrder;
	}

	private int findTargetIndex(int targetOrder, int myIndex) {
		int targetIndex = -1, myOrder = -1;
		double mycap = capabilities[myIndex];
		if (mycap == cap_1)
			myOrder = 1;
		else if (mycap == cap_2)
			myOrder = 2;
		else if (mycap == cap_3)
			myOrder = 3;
		else if (mycap == cap_4)
			myOrder = 4;
		else if (mycap == cap_5)
			myOrder = 5;
		else if (mycap == cap_6)
			myOrder = 6;
		else if (mycap == cap_7)
			myOrder = 7;
		else if (mycap == cap_8)
			myOrder = 8;
		
		
		if (targetOrder >= myOrder)
			targetOrder++;


		if (targetOrder == 1) {
			for (int i = 0; i < initialSystemSize; i++) {
				if (surviving[i] && capabilities[i] == cap_1)
					return i;
			}
		} else if (targetOrder == 2) {
			for (int i = 0; i < initialSystemSize; i++) {
				if (surviving[i] && capabilities[i] == cap_2)
					return i;
			}
		} else if (targetOrder == 3) {
			for (int i = 0; i < initialSystemSize; i++) {
				if (surviving[i] && capabilities[i] == cap_3)
					return i;
			}
		} else if (targetOrder == 4) {
			for (int i = 0; i < initialSystemSize; i++) {
				if (surviving[i] && capabilities[i] == cap_4)
					return i;
			}
		} else if (targetOrder == 5) {
			for (int i = 0; i < initialSystemSize; i++) {
				if (surviving[i] && capabilities[i] == cap_5)
					return i;
			}
		} else if (targetOrder == 6) {
			for (int i = 0; i < initialSystemSize; i++) {
				if (surviving[i] && capabilities[i] == cap_6)
					return i;
			}
		} else if (targetOrder == 7) {
			for (int i = 0; i < initialSystemSize; i++) {
				if (surviving[i] && capabilities[i] == cap_7)
					return i;
			}
		} else if (targetOrder == 8) {
			for (int i = 0; i < initialSystemSize; i++) {
				if (surviving[i] && capabilities[i] == cap_8)
					return i;
			}
		}

		System.out.println("ERROR ... testcase class ... findTargetIndex method");
		System.exit(0);

		return targetIndex;
	}

	void simulateJoiners() {
		warringA = new boolean[initialSystemSize];
		warringB = new boolean[initialSystemSize];

		int i = 0;
		if (i == testedStrategyIndex)
			i++;

		int attackerIndex = i++;
		warringA[attackerIndex] = true;

		if (i == testedStrategyIndex)
			i++;
		int targetIndex = i;
		warringB[targetIndex] = true;
		sideACap = capabilities[attackerIndex];
		sideBCap = capabilities[targetIndex];
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

	int runInit(char[][] initStrategy) {
		int order;
		boolean[] tested = new boolean[currentSystemSize];
		for (int i = 0; i < initStrategy.length; i++) {		
			order = (int) initStrategy[i][0];
			PC = 0;
			oppCap = 1;
			leftCapSum = 1;
			smallerSideCap = largerSideCap = smallerSideLeaderCap = largerSideLeaderCap = 1;

			strategy = Arrays.copyOfRange(initStrategy[i], 1, initStrategy[i].length);
			if (run() == 1)
				return order;
			tested[order] = true;
		}

		for (int i = 0; i < currentSystemSize; i++)
			if (tested[i] == false)
				return i;

		System.out.println("ERROR ... testcase class ... runInit method");
		System.exit(0);
		return 0;
	}
	
	int runJoin(char[][] joinStrategy) {
		
		boolean[] tested = new boolean[currentSystemSize];
		for (int i = 0; i < joinStrategy.length; i++) {
			PC = 0;
			strategy = Arrays.copyOfRange(joinStrategy[i], 1, joinStrategy[i].length);
			double output = run();
			if (output == 1)
				return (int) joinStrategy[i][0];
			tested[(int) joinStrategy[i][0]] = true;
		}

		for (int i = 0; i < currentSystemSize; i++)
			if (tested[i] == false)
				return i;

		System.out.println("ERROR ... testcase class ... runJoin method");
		System.exit(0);
		return 0;


		
		/*PC = 0;
		strategy = Arrays.copyOfRange(joinStrategy, 1, joinStrategy.length);
		double output = run();

		if ((output == 1 && joinStrategy[0] == (char) 0) || (output == 0 && joinStrategy[0] == (char) 1)) {
			return 0;
		} else {
			return 1;
		}
*/
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
