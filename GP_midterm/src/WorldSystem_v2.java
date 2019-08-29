import java.util.Arrays;
import java.util.Random;

public class WorldSystem_v2 {
	


	public int initialStatesNum, currentStatesNum, PC;
	public double capAvg, capMed, capSS, capStdDev, myCap, oppCap, oppCapSum;
	public double[] capabilities;
	public boolean isStable;
	static Random rd = new Random();
	public char[][] stateMembers;
	char[] strategy;
	boolean[][] attackList; 
	double[][] allocation;
	int capChangeCount;
	
	



	public WorldSystem_v2(char[][] stateMembers) {
		this.stateMembers = stateMembers;
		initialStatesNum = stateMembers.length;
		currentStatesNum = initialStatesNum;
		capChangeCount=GP_PilotStudy.CAPCHANGE;
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
		while (true) {
			isStable = true;
			attackList = new boolean[initialStatesNum][initialStatesNum];
			allocation = new double[initialStatesNum][initialStatesNum];
			for (int i = 0; i < initialStatesNum; i++) {
				if (stateMembers[i] != null) {
					myCap = capabilities[i];
					oppCapSum = 0;
					strategy = stateMembers[i];

					for (int j = 0; j < initialStatesNum; j++) {
						if (i == j || stateMembers[j] == null || attackList[i][j])
							continue;
						PC = 0;
						oppCap = capabilities[j];
						if (run() == 1) {
							isStable = false; // only needs to happen once
							attackList[i][j] = true;
							attackList[j][i] = true;
							oppCapSum += oppCap;
							GP_PilotStudy.TC6++;
							GP_PilotStudy.TC12++;
						}
					}

				}
			}

			if (isStable)
				break;

			for (int i = 0; i < initialStatesNum; i++) {
				oppCapSum = 0;
				for (int j = 0; j < initialStatesNum; j++) {
					if (attackList[i][j])
						oppCapSum += capabilities[j];
				}

				for (int j = 0; j < initialStatesNum; j++) {
					if (attackList[i][j])
						allocation[i][j] = capabilities[j] / oppCapSum * capabilities[i];
				}
			}

			for (int i = 0; i < initialStatesNum; i++) {
				if (stateMembers[i] == null)
					continue;
				for (int j = 0; j < initialStatesNum; j++) {
					capabilities[i] -= allocation[j][i];
				}
			}

			for (int i = 0; i < initialStatesNum; i++) {
				if (capabilities[i] <= 0)
					stateMembers[i] = null;
			}

			countStateNumber();
			normalizeCapabilities();
			calculateSystemicVariables();
		}
		
		if (capChangeCount > 0) {
			capChangeCount--;
			for (int i = 0; i < initialStatesNum; i++)
				capabilities[i] *= rd.nextDouble();
			normalizeCapabilities();
			calculateSystemicVariables();
			isStable = false;
			simulate();
		}
		
		
		
		boolean[] survivors = new boolean[initialStatesNum];
		for (int i = 0; i < initialStatesNum; i++)
			if (stateMembers[i] != null)
				survivors[i] = true;
		return survivors;
	}

	
	private void countStateNumber() {
		currentStatesNum = 0;
		for (int i = 0; i < initialStatesNum; i++)
			if (stateMembers[i] != null)
				currentStatesNum++;
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
