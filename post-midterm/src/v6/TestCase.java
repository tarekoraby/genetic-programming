package v6;

import java.util.Arrays;


public class TestCase {
	
	boolean[] surviving, warringA, warringB;
	int currentSystemSize, initialSystemSize, LeftInteractionRounds, PC;
	public double capMed, capStd, capMin, capMax, myCap, oppCap, sideACap, leftCapSum, myEnmity, oppEnmity;
	public double sideB_cap;
	char[] strategy;
	double[] capabilities, original_capabilities, randNums, randGauss;
	double[][] capChangeRates;
	double orig_capMed, orig_capStd, orig_capMin, orig_capMax;
	Strategy[] strategies;
	int testedStrategyIndex, randNumCounter, randGaussCounter;

	public TestCase(double[] capabilities, double[][] capChangeRates, double[] randNums, double[] randGauss, int testedStrategyIndex) {
		this.capabilities = capabilities;
		this.original_capabilities = capabilities.clone();
		this.testedStrategyIndex = testedStrategyIndex;
		this.capChangeRates = capChangeRates;
		this.randNums = randNums;
		this.randGauss = randGauss;
		LeftInteractionRounds = MasterVariables.INTERACTIONROUNDS;
		currentSystemSize = initialSystemSize = capabilities.length;
		randNumCounter = 0;
		randGaussCounter = 0;
		surviving = new boolean[currentSystemSize];
		for (int i = 0; i < currentSystemSize; i++)
			surviving[i] = true;
		calculateSystemVariables();
		orig_capMed = capMed;
		orig_capStd = capStd;
		orig_capMin = capMin;
		orig_capMax = capMax;
		
		myEnmity = 666;
		oppEnmity = 666;
	}

	void resetTestCase(Strategy[] strategies) {
		this.strategies = strategies;
		capabilities = original_capabilities.clone();
		LeftInteractionRounds = MasterVariables.INTERACTIONROUNDS;
		currentSystemSize = capabilities.length;
		randNumCounter = 0;
		randGaussCounter = 0;
		checkErrors();
		surviving = new boolean[currentSystemSize];
		for (int i = 0; i < currentSystemSize; i++)
			surviving[i] = true;
		capMed = orig_capMed;
		capStd = orig_capStd;
		capMin = orig_capMin;
		capMax = orig_capMax;
	}
	
	boolean simulate() {
		boolean warInitiated = false;
		do {
			warInitiated = findInitators();
			if (warInitiated) {
				if (currentSystemSize > 2)
					findJoiners();
				resolveWar();
				calculateCurrentSystemSize();
				if (surviving[testedStrategyIndex] == false)
					return false;
				else if (currentSystemSize == 1)
					return true;
				normalizeCapabilities();
				calculateSystemVariables();
			}
		} while (warInitiated);

		LeftInteractionRounds--;
		if (LeftInteractionRounds > 0) {
			changeVariables();
			normalizeCapabilities();
			calculateSystemVariables();
			return (simulate());
		} else
			return (true);
	}

	private void changeVariables() {
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i])
				capabilities[i] = capabilities[i] * capChangeRates[LeftInteractionRounds-1][i];
		}
	}

	private void resolveWar() {
		double random = 1 + Math.abs(randGauss[randGaussCounter++] * MasterVariables.GAUSS_PROB_FACTOR);
		if ((random < (sideACap / sideB_cap) && sideACap > sideB_cap)
				|| (random > (sideB_cap / sideACap) && sideB_cap > sideACap)) {
			// kill side B states
			// assign their capabilities to side A depending on their power
			for (int i = 0; i < initialSystemSize; i++) {
				if (warringA[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideACap * sideB_cap);
				} else if (warringB[i]) {
					surviving[i] = false;
				}
			}
		} else {
			// kill side A states
			// assign their capabilities to side B depending on their power
			for (int i = 0; i < initialSystemSize; i++) {
				if (warringB[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideB_cap * sideACap);
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
				if (i != testedStrategyIndex && randNums[randNumCounter++] < MasterVariables.RANDOMPLAYER_PROB) {
					if (randNums[randNumCounter++] < randNums[randNumCounter++]) {
						if (randNums[randNumCounter++] > randNums[randNumCounter++]) {
							sideACap += capabilities[i];
							warringA[i] = true;
						} else {
							sideB_cap += capabilities[i];
							warringB[i] = true;
						}
						leftCapSum = 1 - sideACap - sideB_cap;
						newJoiner = true;
						continue findJoiners;
					}
				} else {
					PC = 0;
					myCap = capabilities[i];
					oppCap = sideB_cap ; 
					strategy = strategies[i].getJoinStrategy(currentSystemSize);
					if (run() == 1) {
						PC = 0;
						strategy = strategies[i].getBalanceStrategy(currentSystemSize);
						if (run() == 1) {
							sideACap += myCap;
							warringA[i] = true;
						} else {
							sideB_cap += myCap;
							warringB[i] = true;
						}
						leftCapSum = 1 - sideACap - sideB_cap;
						newJoiner = true;
						continue findJoiners;
					}		
				}
			}
		} while (newJoiner);
	}

	private boolean findInitators() {
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i]) {
				if (i != testedStrategyIndex && randNums[randNumCounter++] < MasterVariables.RANDOMPLAYER_PROB) {
					if (randNums[randNumCounter++] < randNums[randNumCounter++]) {
						warringA = new boolean[initialSystemSize];
						warringB = new boolean[initialSystemSize];
						warringA[i] = true;
						sideACap = capabilities[i];
						int target = i + 1;
						if (target == initialSystemSize)
							target = 0;
						while (!surviving[target] || target == i){
							target++;
							if (target==initialSystemSize)
								target = 0;
						}
						warringB[target] = true;
						sideB_cap = capabilities[target];
						return true;
					}
				} else {
					myCap = capabilities[i];
					sideACap = myCap;
					strategy = strategies[i].getInitStrategy(currentSystemSize);
					for (int k = 0; k < initialSystemSize; k++) {
						if (i == k || !surviving[k])
							continue;
						PC = 0;
						oppCap = capabilities[k];
						leftCapSum = 1 - myCap - oppCap;
						if (Math.abs(leftCapSum) < 1E-10)
							leftCapSum = 0;
						if (run() == 1) {
							warringA = new boolean[initialSystemSize];
							warringB = new boolean[initialSystemSize];
							warringA[i] = true;
							warringB[k] = true;
							sideACap = myCap;
							sideB_cap = oppCap;
							return (true);
						}
					}
				}
			}
		}
		return false;
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
	}

	private void calculateSystemVariables() {
	
		double capAvg = (double) 1 / currentSystemSize, capSS = 0;
		for (int i = 0; i < initialSystemSize; i++) {
			if (surviving[i]) {
				capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
			}
		}
	
		capStd = Math.sqrt(capSS / currentSystemSize);
	
		// calculate median for the current surviving states
		double[] temp = new double[currentSystemSize];
		int remainingStates = currentSystemSize, i=0;
		do{
			if (surviving[i])
				temp[--remainingStates] = capabilities[i];
			i++;
		} while (remainingStates>0);
	
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

	private double run() {

		char primitive = strategy[PC++];

		switch (primitive) {
		case MasterVariables.CAPMED:
			return (capMed);
		case MasterVariables.CAPSTD:
			return (capStd);
		case MasterVariables.CAPMIN:
			return (capMin);
		case MasterVariables.CAPMAX:
			return (capMax);
		case MasterVariables.MYCAP:
			return (myCap);
		case MasterVariables.OPPCAP:
			return (oppCap);
		case MasterVariables.SIDEACAP:
			return (sideACap);
		case MasterVariables.LEFTCAPSUM:
			return (leftCapSum);
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
			if (Math.abs(den) <= 0.00001)
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
