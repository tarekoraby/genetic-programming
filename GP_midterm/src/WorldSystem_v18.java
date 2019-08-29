import java.util.Arrays;
import java.util.Random;

public class WorldSystem_v18 {

	public int initialStatesNum, currentStatesNum, PC;
	public double capMed, capAvg, capStd, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum;
	public double sideA_cap, sideB_cap;
	public double[] capabilities, probabilities, GaussProb;
	public boolean isStable;
	static Random rd = new Random();
	char[] strategy;
	boolean[] warringA, warringB, surviving;
	int capChangeCount, capChanges, testedStrategyIndex, counter, counter2;
	int[][] changeIndexes;
	double[][] capChangeRates;

	public WorldSystem_v18(int length, int testedStrategyIndex, double[] capabilities, double[] probabilities,
			double[] GaussProb, int[][] changeIndexes, double[][] capChangeRates) {

		initialStatesNum = currentStatesNum = length;
		surviving = new boolean[length];
		this.testedStrategyIndex = testedStrategyIndex;
		this.capabilities = (double[]) capabilities.clone();
		this.changeIndexes = changeIndexes;
		this.capChangeRates = capChangeRates;

		for (int i = 0; i < initialStatesNum; i++) {
			surviving[i] = true;
		}

		capChangeCount = 0;
		capChanges = GP_PilotStudy_v18.CAPCHANGE;

		this.probabilities = (double[]) probabilities.clone();
		this.GaussProb = (double[]) GaussProb.clone();
		counter = 0;
		counter2 = 0;
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
		capAvg = (double) 1 / currentStatesNum;

		double capSS = 0;
		for (int i = 0; i < initialStatesNum; i++)
			if (surviving[i])
				capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capStd = Math.sqrt(capSS / currentStatesNum);

		// calculate median for the current surviving states
		double[] temp = new double[currentStatesNum];
		int counter = 0;
		for (int i = 0; i < initialStatesNum; i++)
			if (surviving[i]) {
				temp[counter++] = capabilities[i];

			}

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

	public boolean simulate(char[][][][] strategy_init, char[][][][] strategy_join) {

		isStable = false;
		while (!isStable) {
			calculateStatesNo();
			normalizeCapabilities();
			calculateSystemicVariables();
			isStable = true;

			for (int i = 0; i < initialStatesNum; i++) {
				warringA = new boolean[initialStatesNum];
				warringB = new boolean[initialStatesNum];
				sideA_cap = 0;
				sideB_cap = 0;
				if (surviving[i]) {
					if (strategy_init[i][capChangeCount][0] != null) {
						myCap = capabilities[i];
						mySideCap = myCap;

						strategy = strategy_init[i][capChangeCount][currentStatesNum - GP_PilotStudy_v18.MINSYSTEM];

						for (int k = 0; k < initialStatesNum; k++) {
							if (i == k || !surviving[k])
								continue;
							PC = 0;
							oppCap = capabilities[k];
							leftCapSum = 1 - myCap - oppCap;
							if (Math.abs(leftCapSum) < 1E-10)
								leftCapSum = 0;
							if (run() == 1) {
								sideA_cap = capabilities[i];
								sideB_cap = capabilities[k];
								warringA[i] = true;
								warringB[k] = true;
								// check if others want to join either side
								boolean newJoiner;
								findJoiners: do {
									newJoiner = false;
									if (Math.abs(leftCapSum) < 1E-10)
										break;
									for (int j = 0; j < initialStatesNum; j++) {
										if (!surviving[j] || warringA[j] || warringB[j])
											continue;
										if (strategy_init[j][capChangeCount][0] == null) {
											if (probabilities[counter++] > probabilities[counter++]) {
												if (probabilities[counter++] > probabilities[counter++] ) {
													sideA_cap += capabilities[j];
													warringA[j] = true;
													leftCapSum = 1 - sideA_cap - sideB_cap;
													newJoiner = true;
													continue findJoiners;
												} else {
													sideB_cap += capabilities[j];
													warringB[j] = true;
													leftCapSum = 1 - sideA_cap - sideB_cap;
													newJoiner = true;
													continue findJoiners;
												}

											}
										} else {
											myCap = capabilities[j];
											strategy = strategy_join[j][capChangeCount][currentStatesNum
													- GP_PilotStudy_v18.MINSYSTEM - 1];
											int rand;
											if (probabilities[counter++] > probabilities[counter++]) {
												rand = 0;
											} else {
												rand = 1;
											}
											switch (rand) {
											case 0:
												mySideCap = sideA_cap;
												oppCap = sideB_cap;
												PC = 0;
												if (run() == 1) {
													sideA_cap += myCap;
													leftCapSum = 1 - sideA_cap - sideB_cap;
													warringA[j] = true;
													newJoiner = true;
													continue findJoiners;
												}
												mySideCap = sideB_cap;
												oppCap = sideA_cap;
												PC = 0;
												if (run() == 1) {
													sideB_cap += myCap;
													leftCapSum = 1 - sideA_cap - sideB_cap;
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
													leftCapSum = 1 - sideA_cap - sideB_cap;
													warringB[j] = true;
													newJoiner = true;
													continue findJoiners;
												}
												mySideCap = sideA_cap;
												oppCap = sideB_cap;
												PC = 0;
												if (run() == 1) {
													sideA_cap += myCap;
													leftCapSum = 1 - sideA_cap - sideB_cap;
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
					} else {
						if (probabilities[counter++] > probabilities[counter++]) {
							int targetIndex = 0;
							while (targetIndex == i || !surviving[targetIndex])
								targetIndex++;

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
									if (!surviving[j] || warringA[j] || warringB[j])
										continue;
									if (strategy_init[j][capChangeCount][0] == null) {
										if (probabilities[counter++] > probabilities[counter++]) {
											if (probabilities[counter++] > probabilities[counter++]) {
												sideA_cap += capabilities[j];
												warringA[j] = true;
												leftCapSum = 1 - sideA_cap - sideB_cap;
												newJoiner = true;
												continue findJoiners;
											} else {
												sideB_cap += capabilities[j];
												warringB[j] = true;
												leftCapSum = 1 - sideA_cap - sideB_cap;
												newJoiner = true;
												continue findJoiners;
											}

										}
									} else {
										myCap = capabilities[j];
										strategy = strategy_join[j][capChangeCount][currentStatesNum
												- GP_PilotStudy_v18.MINSYSTEM - 1];
										int rand;
										if (probabilities[counter++] > probabilities[counter++]) {
											rand = 0;
										} else {
											rand = 1;
										}
										switch (rand) {
										case 0:
											mySideCap = sideA_cap;
											oppCap = sideB_cap;
											PC = 0;
											if (run() == 1) {
												sideA_cap += myCap;
												leftCapSum = 1 - sideA_cap - sideB_cap;
												warringA[j] = true;
												newJoiner = true;
												continue findJoiners;
											}
											mySideCap = sideB_cap;
											oppCap = sideA_cap;
											PC = 0;
											if (run() == 1) {
												sideB_cap += myCap;
												leftCapSum = 1 - sideA_cap - sideB_cap;
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
												leftCapSum = 1 - sideA_cap - sideB_cap;
												warringB[j] = true;
												newJoiner = true;
												continue findJoiners;
											}
											mySideCap = sideA_cap;
											oppCap = sideB_cap;
											PC = 0;
											if (run() == 1) {
												sideA_cap += myCap;
												leftCapSum = 1 - sideA_cap - sideB_cap;
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

					if (warringA[i]) {
						excuteAttack();
						if (!surviving[testedStrategyIndex]) {
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

		if (capChangeCount < capChanges) {
			for (int i = 0; i < initialStatesNum; i++)
				capabilities[i] = capabilities[i] * capChangeRates[capChangeCount][i];
			double[] temp_capabilities = new double[initialStatesNum];
			boolean[] temp_surviving = new boolean[initialStatesNum];

			int newTestedStrategyIndex = -99;

			for (int i = 0; i < initialStatesNum; i++) {
				temp_capabilities[changeIndexes[capChangeCount][i]] = capabilities[i];
				temp_surviving[changeIndexes[capChangeCount][i]] = surviving[i];

				if (i == testedStrategyIndex) {
					newTestedStrategyIndex = changeIndexes[capChangeCount][i];
				}
			}
			capabilities = temp_capabilities;
			surviving = temp_surviving;
			strategy_init[newTestedStrategyIndex][capChangeCount + 1] = strategy_init[testedStrategyIndex][capChangeCount];
			strategy_join[newTestedStrategyIndex][capChangeCount + 1] = strategy_join[testedStrategyIndex][capChangeCount];
			testedStrategyIndex = newTestedStrategyIndex;
			capChangeCount++;
			return (simulate(strategy_init, strategy_join));
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
		double random = GaussProb[counter2++];
		if ((random < (sideA_cap / sideB_cap) && sideA_cap > sideB_cap)
				|| (random > (sideB_cap / sideA_cap) && sideB_cap > sideA_cap)) {
			// kill side B states
			// assign their capabilities to side A depending on their power
			for (int i = 0; i < initialStatesNum; i++) {
				if (warringA[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideA_cap * sideB_cap);
				} else if (warringB[i]) {
					surviving[i] = false;
				}
			}
		} else {
			// kill side A states
			// assign their capabilities to side B depending on their power
			for (int i = 0; i < initialStatesNum; i++) {
				if (warringB[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideB_cap * sideA_cap);
				} else if (warringA[i]) {
					surviving[i] = false;
				}
			}
		}

	}

	private double run() {
		char primitive = strategy[PC++];

		switch (primitive) {
		case GP_PilotStudy_v18.CAPMED:
			return (capMed);
		case GP_PilotStudy_v18.CAPAVG:
			return (capAvg);
		case GP_PilotStudy_v18.CAPSTD:
			return (capStd);
		case GP_PilotStudy_v18.CAPMIN:
			return (capMin);
		case GP_PilotStudy_v18.CAPMAX:
			return (capMax);
		case GP_PilotStudy_v18.MYCAP:
			return (myCap);
		case GP_PilotStudy_v18.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v18.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v18.LEFTCAPSUM:
			return (leftCapSum);
		case GP_PilotStudy_v18.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v18.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v18.EQ:
			if (Math.abs(run() - run()) < 1E-10)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v18.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v18.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v18.ADD:
			return (run() + run());
		case GP_PilotStudy_v18.SUB:
			return (run() - run());
		case GP_PilotStudy_v18.MUL:
			return (run() * run());
		case GP_PilotStudy_v18.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v18.randNum[primitive]);
		}

	}

}
