package v12;

import java.util.Arrays;
import java.util.Random;

public class World_System_Shell {

	public int currentSystemSize, PC;
	public int capMed, capSD, cap_1, cap_2, cap_3, cap_4, cap_5, cap_6, cap_7, cap_8, myCap, oppCap, smallerSideCap,
			largerSideCap, smallerSideLeaderCap, largerSideLeaderCap, leftCapSum, myEnmity, oppEnmity;
	public double capRatio;
	private int sideAcap, sideBcap;
	public int[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;

	public World_System_Shell(boolean simulateJoiner, int length) {
		this.simulateJoiner = simulateJoiner;
		currentSystemSize = length;
		checkErrors();

		int remainder = 1000;
		capabilities = new int[currentSystemSize];
		for (int i = 0; i < currentSystemSize - 1; i++) {
			capabilities[i] = 1 + rd.nextInt(remainder - currentSystemSize + i + 1);
			remainder -= capabilities[i];
		}

		capabilities[currentSystemSize - 1] = remainder;

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

		int myIndex = rd.nextInt(currentSystemSize);

		myCap = capabilities[myIndex];
		if (!simulateJoiner) {
			sideAcap = myCap;
			int oppIndex = rd.nextInt(currentSystemSize);
			while (myIndex == oppIndex)
				oppIndex = rd.nextInt(currentSystemSize);
			oppCap = capabilities[oppIndex];
			leftCapSum = 0;
			if (currentSystemSize > 2)
				leftCapSum = 1000 - myCap - oppCap;
			if (Math.abs(leftCapSum) < 1E-10)
				leftCapSum = 0;
			capRatio = (double) myCap / oppCap;
			smallerSideCap = largerSideCap = smallerSideLeaderCap = largerSideLeaderCap = 1;
		} else {
			boolean[] warringA = new boolean[currentSystemSize];
			boolean[] warringB = new boolean[currentSystemSize];
			int attackerIndex = rd.nextInt(currentSystemSize);
			while (myIndex == attackerIndex)
				attackerIndex = rd.nextInt(currentSystemSize);
			warringA[attackerIndex] = true;
			sideAcap = capabilities[attackerIndex];
			int targetIndex = rd.nextInt(currentSystemSize);
			while (myIndex == targetIndex || attackerIndex == targetIndex)
				targetIndex = rd.nextInt(currentSystemSize);
			warringB[targetIndex] = true;
			sideBcap = capabilities[targetIndex];
			for (int i = 0; i < currentSystemSize; i++) {
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
			leftCapSum = 1000 - sideAcap - sideBcap - myCap;
			if (Math.abs(leftCapSum) < 1E-10)
				leftCapSum = 0;
			
			if (sideAcap < sideBcap) {
				smallerSideCap = sideAcap;
				largerSideCap = sideBcap;
				largerSideLeaderCap = smallerSideLeaderCap = -1;
				for (int j = 0; j < currentSystemSize; j++) {
					if (warringA[j] && capabilities[j] > smallerSideLeaderCap)
						smallerSideLeaderCap = capabilities[j];
					if (warringB[j] && capabilities[j] > largerSideLeaderCap)
						largerSideLeaderCap = capabilities[j];
				}
			} else {
				smallerSideCap = sideBcap;
				largerSideCap = sideAcap;
				largerSideLeaderCap = smallerSideLeaderCap = -1;
				for (int j = 0; j < currentSystemSize; j++) {
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
			// XXX
			return (capSD);
			// return (1);
		case MasterVariables.CAP_1:
			return (cap_1);
		case MasterVariables.CAP_2:
			return (cap_2);
		case MasterVariables.CAP_3:
			return (cap_3);
		case MasterVariables.CAP_4:
			return (cap_4);
		case MasterVariables.CAP_5:
			return (cap_5);
		case MasterVariables.CAP_6:
			return (cap_6);
		case MasterVariables.CAP_7:
			return (cap_7);
		case MasterVariables.CAP_8:
			return (cap_8);
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
		for (int i = 0; i < currentSystemSize; i++)
			total += capabilities[i];

		int sum = 0;
		for (int i = 0; i < currentSystemSize; i++) {
			capabilities[i] = capabilities[i] * 1000 / total;
			if (capabilities[i] == 0)
				capabilities[i] = 1;
			sum += capabilities[i];
		}

		
		if (sum !=1000)
			System.exit(0);
		
		for (int i = 0; i < currentSystemSize ; i++)
			if (capabilities[i]<=0)
				System.exit(0);
	}

	private void calculateSystemicVariables_simplified() {

		double capAvg = (double) 1 / currentSystemSize;

		double capSS = 0;
		for (int i = 0; i < currentSystemSize; i++)
			capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capSD = (int) Math.sqrt(capSS / currentSystemSize);

		// calculate median
		int[] temp = capabilities.clone();
		Arrays.sort(temp);
		int middle = currentSystemSize / 2;
		if (currentSystemSize % 2 == 1) {
			capMed = temp[middle];
		} else {
			capMed = (temp[middle - 1] + temp[middle]) / 2;
		}

		switch (currentSystemSize) {
		case 2:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = 0;
			cap_4 = 0;
			cap_5 = 0;
			cap_6 = 0;
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 3:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = 0;
			cap_5 = 0;
			cap_6 = 0;
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 4:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = 0;
			cap_6 = 0;
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 5:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = temp[currentSystemSize - 5];
			cap_6 = 0;
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 6:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = temp[currentSystemSize - 5];
			cap_6 = temp[currentSystemSize - 6];
			cap_7 = 0;
			cap_8 = 0;
			break;
		case 7:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = temp[currentSystemSize - 5];
			cap_6 = temp[currentSystemSize - 6];
			cap_7 = temp[currentSystemSize - 7];
			cap_8 = 0;
			break;
		case 8:
			cap_1 = temp[currentSystemSize - 1];
			cap_2 = temp[currentSystemSize - 2];
			cap_3 = temp[currentSystemSize - 3];
			cap_4 = temp[currentSystemSize - 4];
			cap_5 = temp[currentSystemSize - 5];
			cap_6 = temp[currentSystemSize - 6];
			cap_7 = temp[currentSystemSize - 7];
			cap_8 = temp[currentSystemSize - 8];
			break;
		default:
			System.out.println("ERRORR!!!!!! Test case class. assigning capailities error !!");
			System.exit(0);
		}
	}

	private void checkErrors() {
		if (currentSystemSize < 2 || (currentSystemSize == 2 && simulateJoiner)) {
			System.out.println("World_system_shell class error!!!");
			System.exit(0);
		}

	}

}
