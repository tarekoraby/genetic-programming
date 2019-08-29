import java.util.Arrays;
import java.util.Random;

public class WorldSystem_v13 {

	public int initialStatesNum, currentStatesNum, PC;
	public double capMed, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum;
	public double sideA_cap, sideB_cap;
	public double[] capabilities, probabilities;
	public boolean isStable;
	static Random rd = new Random();
	public char[] strategy_init_2, 			strategy_join_3;
	char[] strategy;
	boolean[] warringA, warringB, surviving;
	int capChangeCount, testedStrategyIndex, counter;

	public WorldSystem_v13(int length, int testedStrategyIndex, double[] capabilities, double[] probabilities) {
		
		initialStatesNum = currentStatesNum = length;
		surviving=new boolean[length];
		this.testedStrategyIndex = testedStrategyIndex;
		this.capabilities=(double[]) capabilities.clone();

		for (int i = 0; i < initialStatesNum; i++) {
			surviving[i] = true;
		}

		capChangeCount = implementClass_v13.CAPCHANGE;

		this.probabilities = (double[]) probabilities.clone();
		counter=0;
	}

	private void normalizeCapabilities() {
		int lastSuvivIndex = initialStatesNum - 1;
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

	private void calculateSystemicVariables() {
		// calculate median for the current surviving states
		double[] temp = new double[currentStatesNum];
		int counter = 0;
		for (int i = 0; i < initialStatesNum; i++)
			if (surviving[i])
				temp[counter++] = capabilities[i];

		Arrays.sort(temp);
		int middle = currentStatesNum / 2;
		if (currentStatesNum % 2 == 1) {
			capMed = temp[middle];
		} else {
			capMed = (temp[middle - 1] + temp[middle]) / 2;
		}

		capMax = temp[currentStatesNum - 1];
		capMin = temp[0];
	}

	public boolean simulate(char[] strategy_init_2,  char[] strategy_join_3) {
		this.strategy_init_2 = strategy_init_2;
		
		this.strategy_join_3 = strategy_join_3;
		isStable = false;
		while (!isStable) {
			calculateStatesNo();
			normalizeCapabilities();
			calculateSystemicVariables();
			isStable = true;
		
			/*
			// Implementing Fisher–Yates shuffle of strategies, capabilities,
			// and popID
			// arrays
			int index;
			double tempCap;
			boolean tempBoolean;
			for (int i = initialStatesNum - 1; i > 0; i--) {
				index = rd.nextInt(i + 1);

				if (i == testedStrategyIndex)
					testedStrategyIndex = index;
				else if (index == testedStrategyIndex)
					testedStrategyIndex = i;

				tempCap = capabilities[index];
				capabilities[index] = capabilities[i];
				capabilities[i] = tempCap;

				tempBoolean = surviving[index];
				surviving[index] = surviving[i];
				surviving[i] = tempBoolean;
			}
			*/


			for (int i = 0; i < initialStatesNum; i++) {
				warringA = new boolean[initialStatesNum];
				warringB = new boolean[initialStatesNum];
				sideA_cap = 0;
				sideB_cap = 0;
				if (surviving[i]) {
					if (i == testedStrategyIndex) {
						myCap = capabilities[i];
						mySideCap = myCap;
						strategy = strategy_init_2;
						
						for (int j = 0; j < initialStatesNum; j++) {
							if (i == j || !surviving[j])
								continue;
							PC = 0;
							oppCap = capabilities[j];
							leftCapSum = 1 - myCap - oppCap;
							if (Math.abs(leftCapSum) < 1E-10)
								leftCapSum = 0;
							if (run() == 1) {
								sideA_cap = capabilities[i];
								sideB_cap = capabilities[j];
								warringA[i] = true;
								warringB[j] = true;
								for (int k = 0; k < initialStatesNum; k++) {
									if (Math.abs(leftCapSum) < 1E-10){
										break;
									}
									if (warringA[k] || warringB[k]
											|| !surviving[k])
										continue;
									if (probabilities[counter++] > probabilities[counter++]) {										
										if (probabilities[counter++] > probabilities[counter++]) {
											warringA[k] = true;
											sideA_cap += capabilities[k];
											leftCapSum = 1 - sideA_cap
													- sideB_cap;
										} else {
											warringB[k] = true;
											sideB_cap += capabilities[k];
											leftCapSum = 1 - sideA_cap
													- sideB_cap;
										}
									}
								}
							}
						}
					} else {
						if (probabilities[counter++] > probabilities[counter++]) {
							int targetIndex = rd.nextInt(initialStatesNum);
							while (!surviving[targetIndex] || i == targetIndex)
								targetIndex = rd.nextInt(initialStatesNum);
							sideA_cap = capabilities[i];
							sideB_cap = capabilities[targetIndex];
							leftCapSum = 1 - sideA_cap - sideB_cap;
							warringA[i] = true;
							warringB[targetIndex] = true;

							// check if others want to join either side
							boolean newJoiner;
							findJoiners: do {
								newJoiner = false;
								if (Math.abs(leftCapSum) < 1E-10)
									break;
								for (int j = 0; j < initialStatesNum; j++) {
									if (!surviving[j] || warringA[j]
											|| warringB[j])
										continue;
									if (j != testedStrategyIndex) {
										if (probabilities[counter++] > probabilities[counter++]) {
											if (probabilities[counter++] > probabilities[counter++]) {
												sideA_cap += capabilities[j];
												warringA[j] = true;
												leftCapSum = 1 - sideA_cap
														- sideB_cap;
												newJoiner = true;
												continue findJoiners;
											} else {
												sideB_cap += capabilities[j];
												warringB[j] = true;
												leftCapSum = 1 - sideA_cap
														- sideB_cap;
												newJoiner = true;
												continue findJoiners;
											}

										}
									} else {
										myCap = capabilities[j];
										strategy = strategy_join_3;
										int rand = rd.nextInt(2);
										switch (rand) {
										case 0:
											mySideCap = sideA_cap;
											oppCap = sideB_cap;
											PC = 0;
											if (run() == 1) {											
												sideA_cap += myCap;
												leftCapSum = 1 - sideA_cap
														- sideB_cap;
												warringA[j] = true;
												newJoiner = true;
												continue findJoiners;
											}
											mySideCap = sideB_cap;
											oppCap = sideA_cap;
											PC = 0;
											if (run() == 1) {
												sideB_cap += myCap;
												leftCapSum = 1 - sideA_cap
														- sideB_cap;
												warringB[j] = true;
												newJoiner = true;
												continue findJoiners;
											}
											break;
										case 1:
											mySideCap = sideB_cap;
											oppCap = sideA_cap;
											PC = 0;
											if (run() == 1) {
												sideB_cap += myCap;
												leftCapSum = 1 - sideA_cap
														- sideB_cap;
												warringB[j] = true;
												newJoiner = true;
												continue findJoiners;
											}
											mySideCap = sideA_cap;
											oppCap = sideB_cap;
											PC = 0;
											if (run() == 1) {
												sideA_cap += myCap;
												leftCapSum = 1 - sideA_cap
														- sideB_cap;
												warringA[j] = true;
												newJoiner = true;
												continue findJoiners;
											}
										}
									}
								}
							} while (newJoiner);
						}
					}
					
					if (warringA[i] ) {
						excuteAttack();
						if (!surviving[testedStrategyIndex]){
							return false;
						}
						calculateStatesNo();
						if (currentStatesNum == 1) {
							return true;
						}
						normalizeCapabilities();
						calculateSystemicVariables();
						isStable = false;
						break;
					}
				}
			}
		}

		if (capChangeCount > 0) {
			capChangeCount--;
			for (int i = 0; i < initialStatesNum; i++)
				capabilities[i] = capabilities[i] * probabilities[counter++] ;

			return (simulate(strategy_init_2, 
					strategy_join_3));
		}

		if (!surviving[testedStrategyIndex])
			return false;
		else
			return true;
	}

	private void calculateStatesNo() {
		currentStatesNum = 0;
		for (int i = 0; i < initialStatesNum; i++)
			if (surviving[i])
				currentStatesNum++;
	}

	private void excuteAttack() {

		// This method should set the loosing side to null and adjust the
		// capability of the winner
		if (sideA_cap > sideB_cap) {
			// kill side B states
			// assign their capabilities to side A depending on their power
			for (int i = 0; i < initialStatesNum; i++) {
				if (warringA[i]) {
					capabilities[i] = capabilities[i]
							+ (capabilities[i] / sideA_cap * sideB_cap);
				} else if (warringB[i]) {
					surviving[i] = false;
				}
			}
		} else {
			// kill side A states
			// assign their capabilities to side B depending on their power
			for (int i = 0; i < initialStatesNum; i++) {
				if (warringB[i]) {
					capabilities[i] = capabilities[i]
							+ (capabilities[i] / sideB_cap * sideA_cap);
				} else if (warringA[i]) {
					surviving[i] = false;
				}
			}
		}

		implementClass_v13.TC6++;
		implementClass_v13.TC12++;
	}

	private double run() {
		char primitive = strategy[PC++];

		switch (primitive) {
		case implementClass_v13.CAPMED:
			return (capMed);
		case implementClass_v13.CAPMIN:
			return (capMin);
		case implementClass_v13.CAPMAX:
			return (capMax);
		case implementClass_v13.MYCAP:
			return (myCap);
		case implementClass_v13.OPPCAP:
			return (oppCap);
		case implementClass_v13.MYSIDECAP:
			return (mySideCap);
		case implementClass_v13.LEFTCAPSUM:
			return (leftCapSum);
		case implementClass_v13.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case implementClass_v13.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case implementClass_v13.EQ:
			if (Math.abs(run() - run()) < 1E-10)
				return (1);
			else
				return (0);
		case implementClass_v13.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case implementClass_v13.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case implementClass_v13.ADD:
			return (run() + run());
		case implementClass_v13.SUB:
			return (run() - run());
		case implementClass_v13.MUL:
			return (run() * run());
		case implementClass_v13.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (implementClass_v13.randNum[primitive]);
		}

	}

}
