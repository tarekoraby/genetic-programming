package v6;


import java.util.Arrays;
import java.util.Random;

public class World_System_Shell {

	public int currentStatesNum, PC;
	public double capMed, capStd, capMin, capMax, myCap, oppCap, sideAcap, leftCapSum, myEnmity, oppEnmity;
	public double capRatio;
	public double  sideBcap;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner, smallerSideA;

	public World_System_Shell(boolean simulateJoiner, int length) {
		this.simulateJoiner = simulateJoiner;
		currentStatesNum = length;
		checkErrors();

		double remainder = 1;
		capabilities = new double[currentStatesNum];
		for (int i = 0; i < currentStatesNum - 1; i++) {
			capabilities[i] = remainder * rd.nextDouble();
			remainder -= capabilities[i];
		}

		capabilities[currentStatesNum - 1] = remainder;

		// shuffle capabilities to ensure their uniform distribution
		for (int i = length - 1; i > 0; i--) {
			int index = rd.nextInt(i + 1);

			double temp = capabilities[index];
			capabilities[index] = capabilities[i];
			capabilities[i] = temp;
		}

		myEnmity = 666;
		oppEnmity = 666;

		normalizeCapabilities_simplified();
		calculateSystemicVariables_simplified();

		int myIndex = rd.nextInt(currentStatesNum);

		myCap = capabilities[myIndex];
		if (!simulateJoiner) {
			sideAcap = myCap;
			int oppIndex = rd.nextInt(currentStatesNum);
			while (myIndex == oppIndex)
				oppIndex = rd.nextInt(currentStatesNum);
			oppCap = capabilities[oppIndex];
			leftCapSum = 0;
			if (currentStatesNum > 2)
				leftCapSum = 1 - myCap - oppCap;
			capRatio = myCap / oppCap;
		} else {
			int attackerIndex = rd.nextInt(currentStatesNum);
			while (myIndex == attackerIndex)
				attackerIndex = rd.nextInt(currentStatesNum);
			sideAcap = capabilities[attackerIndex];
			int targetIndex = rd.nextInt(currentStatesNum);
			while (myIndex == targetIndex || attackerIndex == targetIndex)
				targetIndex = rd.nextInt(currentStatesNum);
			sideBcap = capabilities[targetIndex];
			for (int i = 0; i < currentStatesNum; i++) {
				if (i == myIndex || i == attackerIndex || i == targetIndex)
					continue;
				if (rd.nextInt(2) == 0)
					if (rd.nextInt(2) == 0)
						sideAcap += capabilities[i];
					else
						sideBcap += capabilities[i];

			}
			leftCapSum = 1 - sideAcap - sideBcap;
			capRatio = sideAcap / sideBcap;
			
			oppCap = sideBcap;
			
			smallerSideA = false;
			if (sideAcap > sideBcap)
				smallerSideA = true;
		}

	}

	boolean willItAttack(char[] strategy) {
		this.strategy = strategy;

		PC = 0;
		if (run() == 1)
			return (true);
		else
			return (false);
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
			return (sideAcap);
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
			if (run() == run())
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
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (MasterVariables.randNum[primitive]);
		}
	}

	private void normalizeCapabilities_simplified() {
		double total = 0;
		for (int i = 0; i < currentStatesNum; i++)
			total += capabilities[i];

		double sum = 0;
		for (int i = 0; i < currentStatesNum - 1; i++) {
			capabilities[i] = capabilities[i] / total;
			sum += capabilities[i];
		}

		capabilities[currentStatesNum - 1] = 1 - sum;
	}

	private void calculateSystemicVariables_simplified() {

		double capAvg = (double) 1 / currentStatesNum;

		double capSS = 0;
		for (int i = 0; i < currentStatesNum; i++)
			capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capStd = Math.sqrt(capSS / currentStatesNum);

		// calculate median
		double[] temp =  capabilities.clone();
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

	private void checkErrors() {
		if (currentStatesNum < 2 || (currentStatesNum == 2 && simulateJoiner)) {
			System.out.println("World_system_shell class error!!!");
			System.exit(0);
		}

	}

}
