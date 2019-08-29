import java.util.Arrays;
import java.util.Random;

public class WorldSystem_v6 {
	


	public int initialStatesNum, currentStatesNum, PC;
	public double capMed, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum;
	public double sideA_cap, sideB_cap;
	public double[] capabilities;
	public boolean isStable;
	static Random rd = new Random();
	public char[][] strategy_1, strategy_2;
	char[] strategy;
	int[] popID;
	boolean[] warringA, warringB ;
	int capChangeCount;
	
	



	public WorldSystem_v6(char[][] strategy_1, char[][] strategy_2, int length) {
		this.strategy_1 = strategy_1;
		this.strategy_2 = strategy_2;
		initialStatesNum = currentStatesNum = length;
		isStable = false;
		
		
		// because the passed population will be later shuffled, popID will keep
		// track of the initial order of the population
		popID = new int[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++) {
			popID[i] = i;

		}
		
		GP_PilotStudy_v6.TC1++; 
		GP_PilotStudy_v6.TC11++; 
		GP_PilotStudy_v6.TC2 +=initialStatesNum;


		// generate randomly capabilities so that they add up to 1
		capabilities = new double[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++)
			capabilities[i] = rd.nextDouble();

		normalizeCapabilities();
		calculateSystemicVariables();

		GP_PilotStudy_v6.TC5 += capMed;
		
		
		capChangeCount=GP_PilotStudy_v6.CAPCHANGE;

	}

	private void normalizeCapabilities() {
		int lastSuvivIndex = initialStatesNum - 1;
		while (strategy_1[lastSuvivIndex] == null)
			lastSuvivIndex -= 1;

		double total = 0;
		for (int i = 0; i <= lastSuvivIndex; i++)
			if (strategy_1[i] != null)
				total += capabilities[i];

		double sum = 0;
		for (int i = 0; i < lastSuvivIndex; i++)
			if (strategy_1[i] != null) {
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
			if (strategy_1[i] != null)
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

	public boolean[] simulate() {
		while (!isStable) {
			// Implementing Fisher–Yates shuffle of strategies, capabilities,
			// and popID
			// arrays
			int index, tempID;
			double tempCap;
			char[] tempIndiv_1, tempIndiv_2;
			for (int i = initialStatesNum - 1; i > 0; i--) {
				index = rd.nextInt(i + 1);

				tempID = popID[index];
				popID[index] = popID[i];
				popID[i] = tempID;

				tempCap = capabilities[index];
				capabilities[index] = capabilities[i];
				capabilities[i] = tempCap;

				tempIndiv_1 = strategy_1[index];
				strategy_1[index] = strategy_1[i];
				strategy_1[i] = tempIndiv_1;

				tempIndiv_2 = strategy_2[index];
				strategy_2[index] = strategy_2[i];
				strategy_2[i] = tempIndiv_2;
			}

			
			
			isStable = true;
			warringA = new boolean[initialStatesNum];
			warringB = new boolean[initialStatesNum];
			sideA_cap = 0;
			sideB_cap = 0;
			for (int i = 0; i < initialStatesNum; i++) {
				if (strategy_1[i] != null) {
					myCap = capabilities[i];
					mySideCap = myCap;
					strategy = strategy_1[i];
					for (int j = 0; j < initialStatesNum; j++) {
						if (i == j || strategy_1[j] == null)
							continue;
						PC = 0;
						oppCap = capabilities[j];
						leftCapSum = 1 - myCap - oppCap;
						if (Math.abs(leftCapSum) < 1E-16)
							leftCapSum = 0;
						if (run() == 1) {
							sideA_cap = capabilities[i];
							sideB_cap = capabilities[j];
							warringA[i] = true;
							warringB[j] = true;
							// check if others want to join either side
							boolean newJoiner;
							findJoiners: do {
								newJoiner = false;
								if (Math.abs(leftCapSum) < 1E-16)
									break;
								
								for (int n = 0; n < initialStatesNum; n++)
									if (strategy_2[n] != null && !warringA[n] && !warringB[n]) {
										myCap = capabilities[n];
										strategy = strategy_2[n];
										int rand = rd.nextInt(2);
										switch (rand) {
										case 0:
											mySideCap = sideA_cap;
											oppCap = sideB_cap;
											PC = 0;
											if (run() == 1) {
												sideA_cap += myCap;
												leftCapSum = 1 - sideA_cap - sideB_cap;
												warringA[n] = true;
												newJoiner = true;
												continue findJoiners;
											}
											mySideCap = sideB_cap;
											oppCap = sideA_cap;
											PC = 0;
											if (run() == 1) {
												sideB_cap += myCap;
												leftCapSum = 1 - sideA_cap - sideB_cap;
												warringB[n] = true;
												newJoiner = true;
												continue findJoiners;
											}
										case 1:
											mySideCap = sideB_cap;
											oppCap = sideA_cap;
											PC = 0;
											if (run() == 1) {
												sideB_cap += myCap;
												leftCapSum = 1 - sideA_cap - sideB_cap;
												warringB[n] = true;
												newJoiner = true;
												continue findJoiners;
											}
											mySideCap = sideA_cap;
											oppCap = sideB_cap;
											PC = 0;
											if (run() == 1) {
												sideA_cap += myCap;
												leftCapSum = 1 - sideA_cap - sideB_cap;
												warringA[n] = true;
												newJoiner = true;
												continue findJoiners;
											}
										}
									}
							} while (newJoiner);

							excuteAttack();
							calculateStatesNo();
							if (currentStatesNum == 1) {
								boolean[] survivors = new boolean[initialStatesNum];
								for (int n = 0; n < initialStatesNum; n++)
									if (strategy_1[n] != null){
										survivors[popID[n]] = true;
										return survivors;
									}
							}
							normalizeCapabilities();
							calculateSystemicVariables();
							isStable = false;
							break;
						}
					}
					if (!isStable)
						break;
				}
			}
		}
		

		if (capChangeCount > 0) {
			capChangeCount--;
			for (int i = 0; i < initialStatesNum; i++) 
				capabilities[i] = capabilities[i] * rd.nextDouble() ;
			calculateStatesNo();
			normalizeCapabilities();
			calculateSystemicVariables();
			isStable=false;
			return(simulate());
		}

		boolean[] survivors = new boolean[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++)
			if (strategy_1[i] != null)
				survivors[popID[i]] = true;
		return survivors;
	}

	private void calculateStatesNo() {
		currentStatesNum = 0;
		for (int i = 0; i < initialStatesNum; i++)
			if (strategy_1[i] != null)
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
					capabilities[i] = capabilities[i] + (capabilities[i] / sideA_cap * sideB_cap);
				} else if (warringB[i]) {
					strategy_1[i] = null;
					strategy_2[i] = null;
				}
			}
		} else {
			// kill side A states
			// assign their capabilities to side B depending on their power
			for (int i = 0; i < initialStatesNum; i++) {
				if (warringB[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideB_cap * sideA_cap);
				} else if (warringA[i]) {
					strategy_1[i] = null;
					strategy_2[i] = null;
				}
			}
		}

		GP_PilotStudy_v6.TC6++;
		GP_PilotStudy_v6.TC12++;
	}

	private double run() {
		char primitive = strategy[PC++];
		
		switch (primitive) {
		case GP_PilotStudy_v6.CAPMED:
			return (capMed);
		case GP_PilotStudy_v6.CAPMIN:
			return (capMin);	
		case GP_PilotStudy_v6.CAPMAX:
			return (capMax);
		case GP_PilotStudy_v6.MYCAP:
			return (myCap);
		case GP_PilotStudy_v6.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v6.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v6.LEFTCAPSUM:
			return (leftCapSum);
		case GP_PilotStudy_v6.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v6.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v6.EQ:
			if (run() == run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v6.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v6.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v6.ADD:
			return (run() + run());
		case GP_PilotStudy_v6.SUB:
			return (run() - run());
		case GP_PilotStudy_v6.MUL:
			return (run() * run());
		case GP_PilotStudy_v6.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v6.randNum[primitive]);
		}

	}

}
