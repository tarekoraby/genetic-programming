package v10;

import java.util.Arrays;
import java.util.Random;

public class World_System_Shell {

	public int currentStatesNum, PC;
	public double capMed, capSD, capMin, capMax, myCap, oppCap, smallerSideCap, largerSideCap, smallerSideLeaderCap,
			largerSideLeaderCap, leftCapSum, myEnmity, oppEnmity;
	public double capRatio;
	private double sideAcap, sideBcap;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;

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

		myEnmity = 1;
		oppEnmity = 1;

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
			if (Math.abs(leftCapSum) < 1E-10)
				leftCapSum = 0;
			capRatio = myCap / oppCap;
			smallerSideCap = largerSideCap = smallerSideLeaderCap = largerSideLeaderCap = 1;
		} else {
			boolean[] warringA = new boolean[currentStatesNum];
			boolean[] warringB = new boolean[currentStatesNum];
			int attackerIndex = rd.nextInt(currentStatesNum);
			while (myIndex == attackerIndex)
				attackerIndex = rd.nextInt(currentStatesNum);
			warringA[attackerIndex] = true;
			sideAcap = capabilities[attackerIndex];
			int targetIndex = rd.nextInt(currentStatesNum);
			while (myIndex == targetIndex || attackerIndex == targetIndex)
				targetIndex = rd.nextInt(currentStatesNum);
			warringB[targetIndex] = true;
			sideBcap = capabilities[targetIndex];
			for (int i = 0; i < currentStatesNum; i++) {
				if (i == myIndex || i == attackerIndex || i == targetIndex)
					continue;
				if (rd.nextInt(2) == 0)
					if (rd.nextInt(2) == 0) {
						sideAcap += capabilities[i];
						warringA[i] = true;
					} else {
						sideBcap += capabilities[i];
						warringB[i] = true;
					}
			}
			leftCapSum = 1 - sideAcap - sideBcap;
			if (Math.abs(leftCapSum) < 1E-10)
				leftCapSum = 0;
			if (sideAcap < sideBcap) {
				smallerSideCap = sideAcap;
				largerSideCap = sideBcap;
				largerSideLeaderCap = smallerSideLeaderCap = -1;
				for (int j = 0; j < currentStatesNum; j++) {
					if (warringA[j] && capabilities[j] > smallerSideLeaderCap)
						smallerSideLeaderCap = capabilities[j];
					if (warringB[j] && capabilities[j] > largerSideLeaderCap)
						largerSideLeaderCap = capabilities[j];
				}
			} else {
				smallerSideCap = sideBcap;
				largerSideCap = sideAcap;
				largerSideLeaderCap = smallerSideLeaderCap = -1;
				for (int j = 0; j < currentStatesNum; j++) {
					if (warringB[j] && capabilities[j] > smallerSideLeaderCap)
						smallerSideLeaderCap = capabilities[j];
					if (warringA[j] && capabilities[j] > largerSideLeaderCap)
						largerSideLeaderCap = capabilities[j];
				}
			}
			capRatio = smallerSideCap / largerSideCap;
			oppCap = 1;
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
		case MasterVariables.CAPSD:
			return (capSD);
		case MasterVariables.CAPMIN:
			return (capMin);
		case MasterVariables.CAPMAX:
			return (capMax);
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
		capSD = Math.sqrt(capSS / currentStatesNum);

		// calculate median
		double[] temp = capabilities.clone();
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
