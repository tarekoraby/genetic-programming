import java.util.Arrays;
import java.util.Random;

public class WorldSystem_v10 {
	


	public int initialStatesNum, currentStatesNum, PC;
	public double capMed, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum;
	public double sideA_cap, sideB_cap;
	public double[] capabilities;
	public double[][] capList;
	public boolean isStable;
	static Random rd = new Random();
	public char[][] strategy_init_2, strategy_init_3, strategy_init_4, strategy_join_3;
	char[] strategy;
	int[] popID;
	boolean[] warringA, warringB ;
	int capChangeCount;
	
	



	public WorldSystem_v10(char[][] strategy_init_2, char[][] strategy_init_3, char[][] strategy_init_4, char[][] strategy_join_3, int length, double[][] capList) {
		this.strategy_init_2 = strategy_init_2;
		this.strategy_init_3 = strategy_init_3;
		this.strategy_init_4 = strategy_init_4;
		this.strategy_join_3 = strategy_join_3;
		initialStatesNum = currentStatesNum = length;
		isStable = false;
		
		
		// because the passed population will be later shuffled, popID will keep
		// track of the initial order of the population
		popID = new int[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++) {
			popID[i] = i;

		}
		
		GP_PilotStudy_v10.TC1++; 
		GP_PilotStudy_v10.TC11++; 
		GP_PilotStudy_v10.TC2 +=initialStatesNum;

		capChangeCount = GP_PilotStudy_v10.CAPCHANGE;
		this.capList = new double[capChangeCount + 1][];
		for (int i = 0; i <= capChangeCount; i++)
			this.capList[i] = (double[]) capList[i].clone();
		this.capabilities=(double[]) capList[capChangeCount].clone();
		

		normalizeCapabilities();
		calculateSystemicVariables();

		GP_PilotStudy_v10.TC5 += capMed;
		
		
		

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

	public boolean[] simulate() {
		while (!isStable) {
			

			
			
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
							calculateStatesNo();
							if (currentStatesNum == 1) {
								boolean[] survivors = new boolean[initialStatesNum];
								for (int n = 0; n < initialStatesNum; n++)
									if (strategy_init_2[n] != null){
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
			capabilities=(double[]) capList[capChangeCount].clone();
			calculateStatesNo();
			normalizeCapabilities();
			calculateSystemicVariables();
			isStable=false;
			return(simulate());
		}

		boolean[] survivors = new boolean[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++)
			if (strategy_init_2[i] != null)
				survivors[popID[i]] = true;
		return survivors;
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
		double random = 1 + Math.abs(rd.nextGaussian() * 0);
		if ((random < (sideA_cap / sideB_cap) && sideA_cap > sideB_cap) || (random > (sideB_cap / sideA_cap) && sideB_cap > sideA_cap)) {
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

		GP_PilotStudy_v10.TC6++;
		GP_PilotStudy_v10.TC12++;
	}

	private double run() {
		char primitive = strategy[PC++];
		
		switch (primitive) {
		case GP_PilotStudy_v10.CAPMED:
			return (capMed);
		case GP_PilotStudy_v10.CAPMIN:
			return (capMin);	
		case GP_PilotStudy_v10.CAPMAX:
			return (capMax);
		case GP_PilotStudy_v10.MYCAP:
			return (myCap);
		case GP_PilotStudy_v10.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v10.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v10.LEFTCAPSUM:
			return (leftCapSum);
		case GP_PilotStudy_v10.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v10.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v10.EQ:
			if (run() == run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v10.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v10.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v10.ADD:
			return (run() + run());
		case GP_PilotStudy_v10.SUB:
			return (run() - run());
		case GP_PilotStudy_v10.MUL:
			return (run() * run());
		case GP_PilotStudy_v10.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v10.randNum[primitive]);
		}

	}

}
