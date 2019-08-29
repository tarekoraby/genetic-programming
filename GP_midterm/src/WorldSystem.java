import java.util.Arrays;
import java.util.Random;

public class WorldSystem {
	


	public int initialStatesNum, currentStatesNum, PC;
	public double capAvg, capMed, capSS, capStdDev, myCap, oppCap, oppCapSum;
	public double[] capabilities;
	public boolean isStable;
	static Random rd = new Random();
	public char[][] stateMembers;
	char[] strategy;
	int[] popID;
	int capChangeCount;
	
	



	public WorldSystem(char[][] stateMembers) {
		this.stateMembers = stateMembers;
		initialStatesNum = stateMembers.length;
		currentStatesNum = initialStatesNum;
		isStable = false;
		
		
		GP_PilotStudy.TC1++; 
		GP_PilotStudy.TC11++; 
		GP_PilotStudy.TC2 +=initialStatesNum;


		// generate randomly capabilities so that they add up to 1
		capabilities = new double[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++)
			capabilities[i] = rd.nextDouble();

		normalizeCapabilities();
		calculateSystemicVariables();

		GP_PilotStudy.TC3 += capAvg;
		GP_PilotStudy.TC4 += capStdDev;
		GP_PilotStudy.TC5 += capMed;
		
		
		capChangeCount=GP_PilotStudy.CAPCHANGE;

		// because the passed population will be later shuffled, popID will keep
		// track of the initial order of the population
		popID=new int[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++) {
			popID[i] = i;

		}
	}

	private void normalizeCapabilities() {
		int lastSuvivIndex = initialStatesNum - 1;
		while (stateMembers[lastSuvivIndex] == null)
			lastSuvivIndex -= 1;

		double total = 0;
		for (int i = 0; i <= lastSuvivIndex; i++)
			if (stateMembers[i] != null)
				total += capabilities[i];

		double sum = 0;
		for (int i = 0; i < lastSuvivIndex; i++)
			if (stateMembers[i] != null) {
				capabilities[i] = capabilities[i] / total;
				sum += capabilities[i];
			}

		capabilities[lastSuvivIndex] = 1 - sum;
		
		
	}

	private void calculateSystemicVariables() {
		//since capabilities should already be normalized average can be calculated as follows
		capAvg= 1.0 / (double)currentStatesNum;
		
		capSS = 0;
		for (int i = 0; i < initialStatesNum; i++)
			if (stateMembers[i] != null)
				capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capStdDev = Math.sqrt(capSS / currentStatesNum);
		
		//calculate median
		double[] temp=new double[currentStatesNum];
		int counter=0;
		for (int i = 0; i < initialStatesNum; i++)
			if (stateMembers[i] != null)
				temp[counter++]=capabilities[i];
		Arrays.sort(temp);
		int middle = currentStatesNum / 2;
		if (currentStatesNum % 2 == 1) {
			capMed = temp[middle];
		} else {
			capMed = (temp[middle - 1] + temp[middle]) / 2;
		}
	}

	public boolean[] simulate() {
		while (!isStable) {
			// Implementing Fisher–Yates shuffle of population, capabilities,
			// and popID
			// arrays
			int index, tempID;
			double tempCap;
			char[] tempIndiv;
			for (int i = initialStatesNum - 1; i > 0; i--) {
				index = rd.nextInt(i + 1);

				tempID = popID[index];
				popID[index] = popID[i];
				popID[i] = tempID;

				tempCap = capabilities[index];
				capabilities[index] = capabilities[i];
				capabilities[i] = tempCap;

				tempIndiv = stateMembers[index];
				stateMembers[index] = stateMembers[i];
				stateMembers[i] = tempIndiv;
			}

			isStable = true;
			for (int i = 0; i < initialStatesNum; i++) {
				if (stateMembers[i] != null) {
					myCap=capabilities[i];
					strategy = stateMembers[i];
					for (int j = 0; j < initialStatesNum; j++) {
						if (i == j || stateMembers[j] == null)
							continue;
						PC = 0;
						oppCap=capabilities[j];
						oppCapSum=capabilities[j]; 
						if (run()==1) {
							attack(i, j);
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
				capabilities[i] *= rd.nextDouble() ;
			normalizeCapabilities();
			calculateSystemicVariables();
			isStable=false;
			simulate();
		}
		// shuffle the state array to randomly determine the order of action
		// choose each state one by one and ask if it wants to attack the next
		// state on the list
		// if an attack happens subtract capabilities from both sides, eliminate
		// the looser and reshuffle
		// if all states are given the opportunity none of them attack then the
		// world is stable and return the list of survivors

		boolean[] survivors = new boolean[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++)
			if (stateMembers[i] != null)
				survivors[popID[i]] = true;
		return survivors;
	}

	private void attack(int initiatorID, int targetID) {
		// This method should set the loosing side to null, adjust the
		// capability of the winner, and adjust currentStatesNum.  
		if (capabilities[initiatorID] > capabilities[targetID]) {
			capabilities[initiatorID] -= capabilities[targetID];
			stateMembers[targetID] = null;
			currentStatesNum -= 1;

		} else if (capabilities[targetID] > capabilities[initiatorID]) {
			capabilities[targetID] -= capabilities[initiatorID];
			stateMembers[initiatorID] = null;
			currentStatesNum -= 1;

		} else {
			if (rd.nextBoolean()) {
				capabilities[initiatorID] = 0.00000001 + rd.nextDouble() / 1000000;
				stateMembers[targetID] = null;
				currentStatesNum -= 1;
			} else {
				capabilities[targetID] = 0.00000001 + rd.nextDouble() / 1000000;
				stateMembers[initiatorID] = null;
				currentStatesNum -= 1;
			}
		}
		GP_PilotStudy.TC6++;
		GP_PilotStudy.TC12++;
	}

	private double run() {
		char primitive = strategy[PC++];
		
		switch (primitive) {
		case GP_PilotStudy.CAPAVG:
			return (capAvg);
		case GP_PilotStudy.CAPMED:
			return (capMed);
		case GP_PilotStudy.CAPSS:
			return (capSS);	
		case GP_PilotStudy.CAPSTDEV:
			return (capStdDev);
		case GP_PilotStudy.CURRENTSTATESNUM:
			return (currentStatesNum);
		case GP_PilotStudy.MYCAP:
			return (myCap);
		case GP_PilotStudy.OPPCAP:
			return (oppCap);
		case GP_PilotStudy.OPPCAPSUM:
			return (oppCapSum);
		case GP_PilotStudy.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy.ADD:
			return (run() + run());
		case GP_PilotStudy.SUB:
			return (run() - run());
		case GP_PilotStudy.MUL:
			return (run() * run());
		case GP_PilotStudy.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy.randNum[primitive]);
		}

	}

}
