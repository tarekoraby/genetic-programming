import java.util.Arrays;
import java.util.Random;

public class WorldSystem_v8 {
	


	public int initialStatesNum, currentStatesNum, PC;
	public double capMed, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum;
	public double sideA_cap, sideB_cap;
	public double[] capabilities;
	public boolean isStable;
	static Random rd = new Random();
	public char[][] strategy_init_2, strategy_init_3, strategy_init_4, strategy_join_3;
	char[] strategy;
	boolean[] warringA, warringB ;
	int capChangeCount, testedStrategyIndex;
	
	



	public WorldSystem_v8(char[][] strategy_init_2, char[][] strategy_init_3, char[][] strategy_init_4, char[][] strategy_join_3, int length) {
		this.strategy_init_2 = strategy_init_2;
		this.strategy_init_3 = strategy_init_3;
		this.strategy_init_4 = strategy_init_4;
		this.strategy_join_3 = strategy_join_3;
		initialStatesNum = currentStatesNum = length;
		testedStrategyIndex = 0;
		isStable = false;
		
	
		
		GP_PilotStudy_v8.TC1++; 
		GP_PilotStudy_v8.TC11++; 
		GP_PilotStudy_v8.TC2 +=initialStatesNum;


		// generate randomly capabilities so that they add up to 1
		capabilities = new double[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++)
			capabilities[i] = rd.nextDouble();

		normalizeCapabilities();
		calculateSystemicVariables();

		GP_PilotStudy_v8.TC5 += capMed;
		
		
		capChangeCount = GP_PilotStudy_v8.CAPCHANGE;

	}

	private void normalizeCapabilities() {
		int lastSuvivIndex = initialStatesNum - 1;
		while (strategy_init_2[lastSuvivIndex] == null)
			lastSuvivIndex -= 1;

		double total = 0;
		for (int i = 0; i <= lastSuvivIndex; i++)
			if (strategy_init_2[i] != null)
				total += capabilities[i];

		double sum = 0;
		for (int i = 0; i < lastSuvivIndex; i++)
			if (strategy_init_2[i] != null) {
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
			if (strategy_init_2[i] != null)
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

	public boolean simulate() {
		while (!isStable) {
			// Implementing Fisher–Yates shuffle of strategies, capabilities,
			// and popID
			// arrays
			int index, tempID;
			double tempCap;
			char[] tempIndiv_1, tempIndiv_2, tempIndiv_3, tempIndiv_4;
			for (int i = initialStatesNum - 1; i > 0; i--) {
				index = rd.nextInt(i + 1);

				if (i == testedStrategyIndex)
					testedStrategyIndex = index;
				else if (index == testedStrategyIndex)
					testedStrategyIndex = i;

				tempCap = capabilities[index];
				capabilities[index] = capabilities[i];
				capabilities[i] = tempCap;

				tempIndiv_1 = strategy_init_2[index];
				strategy_init_2[index] = strategy_init_2[i];
				strategy_init_2[i] = tempIndiv_1;
				
				tempIndiv_2 = strategy_init_3[index];
				strategy_init_3[index] = strategy_init_3[i];
				strategy_init_3[i] = tempIndiv_2;
				
				tempIndiv_3 = strategy_init_4[index];
				strategy_init_4[index] = strategy_init_4[i];
				strategy_init_4[i] = tempIndiv_3;

				tempIndiv_4 = strategy_join_3[index];
				strategy_join_3[index] = strategy_join_3[i];
				strategy_join_3[i] = tempIndiv_4;
			}

			
			
			isStable = true;
			warringA = new boolean[initialStatesNum];
			warringB = new boolean[initialStatesNum];
			sideA_cap = 0;
			sideB_cap = 0;
			for (int i = 0; i < initialStatesNum; i++) {
				if (strategy_init_2[i] != null) {
					myCap = capabilities[i];
					mySideCap = myCap;
					strategy=null;
					if (currentStatesNum == 2)
						strategy = strategy_init_2[i];
					else if (currentStatesNum == 3)
						strategy = strategy_init_3[i];
					else if (currentStatesNum == 4)
						strategy = strategy_init_4[i];
					for (int j = 0; j < initialStatesNum; j++) {
						if (i == j || strategy_init_2[j] == null)
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
									if (strategy_join_3[n] != null && !warringA[n] && !warringB[n]) {
										myCap = capabilities[n];
										strategy = strategy_join_3[n];
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
							if (strategy_init_2[testedStrategyIndex]==null)
								return false;
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

		if (strategy_init_2[testedStrategyIndex] == null)
			return false;
		else
			return true;
	}

	private void calculateStatesNo() {
		currentStatesNum = 0;
		for (int i = 0; i < initialStatesNum; i++)
			if (strategy_init_2[i] != null)
				currentStatesNum++;
	}

	private void excuteAttack() {

		// This method should set the loosing side to null and adjust the
		// capability of the winner
		if (sideA_cap  > sideB_cap ) {
			// kill side B states
			// assign their capabilities to side A depending on their power
			for (int i = 0; i < initialStatesNum; i++) {
				if (warringA[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideA_cap * sideB_cap);
				} else if (warringB[i]) {
					strategy_init_2[i] = null;
					strategy_init_3[i] = null;
					strategy_init_4[i] = null;
					strategy_join_3[i] = null;
				}
			}
		} else {
			// kill side A states
			// assign their capabilities to side B depending on their power
			for (int i = 0; i < initialStatesNum; i++) {
				if (warringB[i]) {
					capabilities[i] = capabilities[i] + (capabilities[i] / sideB_cap * sideA_cap);
				} else if (warringA[i]) {
					strategy_init_2[i] = null;
					strategy_init_3[i] = null;
					strategy_init_4[i] = null;
					strategy_join_3[i] = null;
				}
			}
		}

		GP_PilotStudy_v8.TC6++;
		GP_PilotStudy_v8.TC12++;
	}

	private double run() {
		char primitive = strategy[PC++];
		
		switch (primitive) {
		case GP_PilotStudy_v8.CAPMED:
			return (capMed);
		case GP_PilotStudy_v8.CAPMIN:
			return (capMin);	
		case GP_PilotStudy_v8.CAPMAX:
			return (capMax);
		case GP_PilotStudy_v8.MYCAP:
			return (myCap);
		case GP_PilotStudy_v8.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v8.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v8.LEFTCAPSUM:
			return (leftCapSum);
		case GP_PilotStudy_v8.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v8.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v8.EQ:
			if (run() == run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v8.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v8.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v8.ADD:
			return (run() + run());
		case GP_PilotStudy_v8.SUB:
			return (run() - run());
		case GP_PilotStudy_v8.MUL:
			return (run() * run());
		case GP_PilotStudy_v8.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v8.randNum[primitive]);
		}

	}

}
