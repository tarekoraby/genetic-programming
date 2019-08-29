package v11;

import java.util.Arrays;
import java.util.Random;

public class World_System_Shell {

	public int currentStatesNum, PC;
	public int capMed, capSD, capMin, capMax, myCap, oppCap, smallerSideCap, largerSideCap, smallerSideLeaderCap,
			largerSideLeaderCap, leftCapSum, myEnmity, oppEnmity;
	public double capRatio;
	private int sideAcap, sideBcap;
	public int[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;

	public World_System_Shell(boolean simulateJoiner, int length) {
		this.simulateJoiner = simulateJoiner;
		currentStatesNum = length;
		checkErrors();

		int remainder = 1000;
		capabilities = new int[currentStatesNum];
		for (int i = 0; i < currentStatesNum - 1; i++) {
			capabilities[i] = 1 + rd.nextInt(remainder - currentStatesNum + i + 1);
			remainder -= capabilities[i];
		}

		capabilities[currentStatesNum - 1] = remainder;

		// shuffle capabilities to ensure their uniform distribution
		for (int i = length - 1; i > 0; i--) {
			int index = rd.nextInt(i + 1);

			int temp = capabilities[index];
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
				leftCapSum = 1000 - myCap - oppCap;
			if (Math.abs(leftCapSum) < 1E-10)
				leftCapSum = 0;
			capRatio = (double) myCap / oppCap;
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
			leftCapSum = 1000 - sideAcap - sideBcap;
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
			capRatio = (double) smallerSideCap / largerSideCap;
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

	private int run() {

		char primitive = strategy[PC++];

		switch (primitive) {
		case MasterVariables.CAPMED:
			//XXX
			return (capMed);
			//return (1);		
		case MasterVariables.CAPSD:
			//XXX
			return (capSD);
			//return (1);
		case MasterVariables.CAPMIN:
			return (capMin);
			//return (1);
		case MasterVariables.CAPMAX:
			//XXX
			return (capMax);
			//return (1);
		case MasterVariables.MYCAP:
			return (myCap);
		case MasterVariables.OPPCAP:
			return (oppCap);
		case MasterVariables.SMALLERSIDECAP:
			return (smallerSideCap);
			//return (1);
		case MasterVariables.LARGERSIDECAP:
			return (largerSideCap);
			//return (1);
		case MasterVariables.SMALLERSIDELEADERCAP:
			//XXX
			return (smallerSideLeaderCap);
			//return (1);
		case MasterVariables.LARGERSIDELEADERCAP:
			//XXX
			return (largerSideLeaderCap);
			//return (1);
		case MasterVariables.LEFTCAPSUM:
			//XXX
			return (leftCapSum);
			//return (1);
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
			int num = run(), den = run();
			if (den == 0)
				return (num);
			else
				return (num / den);
		}
		default:
			return (MasterVariables.randNum[primitive]);
		}

	}

	private void normalizeCapabilities_simplified() {
		int total = 0;
		for (int i = 0; i < currentStatesNum; i++)
			total += capabilities[i];

		int sum = 0;
		for (int i = 0; i < currentStatesNum; i++) {
			capabilities[i] = capabilities[i] * 1000 / total;
			if (capabilities[i] == 0)
				capabilities[i] = 1;
			sum += capabilities[i];
		}

		
		if (sum !=1000)
			System.exit(0);
		
		for (int i = 0; i < currentStatesNum ; i++)
			if (capabilities[i]<=0)
				System.exit(0);
	}

	private void calculateSystemicVariables_simplified() {

		double capAvg = (double) 1 / currentStatesNum;

		double capSS = 0;
		for (int i = 0; i < currentStatesNum; i++)
			capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capSD = (int) Math.sqrt(capSS / currentStatesNum);

		// calculate median
		int[] temp = capabilities.clone();
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
