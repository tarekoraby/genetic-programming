package v19;

import java.util.Arrays;
import java.util.Random;

public class World_System_Shell {

	public int currentSystemSize, PC, myOrder;
	public double capMed, capSD, cap_1, cap_2, cap_3, cap_4, cap_5, cap_6, cap_7, cap_8, myCap, oppCap, smallerSideCap,
			largerSideCap, smallerSideLeaderCap, largerSideLeaderCap, leftCapSum, myEnmity, oppEnmity;
	public double capRatio;
	private double sideAcap, sideBcap;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;

	public World_System_Shell(boolean simulateJoiner, int length) {
		this.simulateJoiner = simulateJoiner;
		currentSystemSize = length;
		checkErrors();

		double remainder = 1;
		capabilities = new double[currentSystemSize];
		for (int i = 0; i < currentSystemSize - 1; i++) {
			capabilities[i] = remainder * rd.nextDouble();
			remainder -= capabilities[i];
		}

		capabilities[currentSystemSize - 1] = remainder;

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
				leftCapSum = 1 - myCap - oppCap;
			if (Math.abs(leftCapSum) < 1E-10)
				leftCapSum = 0;
			capRatio = (double) myCap / oppCap;
			oppCap = 1;
			leftCapSum = 1;
			smallerSideCap = largerSideCap = smallerSideLeaderCap = largerSideLeaderCap = 1;
			calculateMyOrder(myIndex);
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
			leftCapSum = 1 - sideAcap - sideBcap - myCap;
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

	int willItJoin(char[][] joinStrategy) {
		boolean[] tested = new boolean[currentSystemSize];
		for (int i = 0; i < joinStrategy.length; i++) {
			PC = 0;
			strategy = Arrays.copyOfRange(joinStrategy[i], 1, joinStrategy[i].length);
			double output = run();
			if (output == 1){
				return (int)joinStrategy[i][0];
			}
			tested[(int)joinStrategy[i][0]] = true;
		}
		for (int i = 0; i < currentSystemSize; i++)
			if (tested[i] == false)
				return i;

		System.out.println("ERROR ... worldsystemshell class ... runJoin method");
		System.exit(0);
		return 0;
	}
	
	/*boolean willItJoin(char[] joinStrategy) {
		
		PC = 0;

		strategy = Arrays.copyOfRange(joinStrategy, 1, joinStrategy.length);
		double output = run();
		if ((output == 1 && joinStrategy[0] == (char) 0) || (output == 0 && joinStrategy[0] == (char) 1)) {
			return false;
		} else {
			return true;
		}

	
	}*/
	
	int willItAttack(char[][] initStrategy) {
		int order;
		boolean[] tested = new boolean[currentSystemSize];
		for (int i = 0; i < initStrategy.length; i++) {
			strategy = Arrays.copyOfRange(initStrategy[i], 1, initStrategy[i].length);
			order = (int) initStrategy[i][0];
			PC = 0;
			oppCap = 1;
			leftCapSum = 1;
			smallerSideCap = largerSideCap = smallerSideLeaderCap = largerSideLeaderCap = 1;

			if (run() == 1){
				return order;
			}
			tested[order] = true;
		}

		for (int i = 0; i < currentSystemSize; i++)
			if (tested[i] == false){
				return i;
			}

		System.out.println("ERROR ... worldsystemshell class ... willItAttack method");
		System.exit(0);
		return 0;
	}

	private double run() {

		int primitive = strategy[PC++];

		switch (primitive) {
		case MasterVariables.CAPMED:
			return (capMed);
		case MasterVariables.CAPSD:
			return (capSD);
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
			double left = run(),
			right = run();
			if (left > right && Math.abs(left - right) > 1E-10)
				return (1);
			else
				return (0);
		case MasterVariables.LT:
			double left2 = run(),
			right2 = run();
			if (left2 < right2 && Math.abs(left2 - right2) > 1E-10)
				return (1);
			else
				return (0);
		case MasterVariables.EQ:
			if (Math.abs(run() - run()) < 1E-10)
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
			if (Math.abs(den) < 1E-10)
				return (num);
			else
				return (num / den);
		}
		case MasterVariables.IF_THEN_ELSE:
			if (run() == 1)
				return run();
			else {
				run();
				return run();
			}
		default:
			return (MasterVariables.randNum[primitive]);
		}

	}

	private void normalizeCapabilities_simplified() {
		double total = 0;
		for (int i = 0; i < currentSystemSize; i++)
			total += capabilities[i];

		double sum = 0;
		for (int i = 0; i < currentSystemSize - 1; i++) {
			capabilities[i] = capabilities[i] / total;
			sum += capabilities[i];
		}

		capabilities[currentSystemSize - 1] = 1 - sum;
	}

	private void calculateSystemicVariables_simplified() {

		double capAvg = (double) 1 / currentSystemSize;

		double capSS = 0;
		for (int i = 0; i < currentSystemSize; i++)
			capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capSD =  Math.sqrt(capSS / currentSystemSize);

		// calculate median
		double[] temp = capabilities.clone();
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

	private void calculateMyOrder(int myIndex) {
		myOrder = -1;
		double mycap = capabilities[myIndex];
		if (mycap == cap_1)
			myOrder = 1;
		else if (mycap == cap_2)
			myOrder = 2;
		else if (mycap == cap_3)
			myOrder = 3;
		else if (mycap == cap_4)
			myOrder = 4;
		else if (mycap == cap_5)
			myOrder = 5;
		else if (mycap == cap_6)
			myOrder = 6;
		else if (mycap == cap_7)
			myOrder = 7;
		else if (mycap == cap_8)
			myOrder = 8;
		
	}

	private void checkErrors() {
		if (currentSystemSize < 2 || (currentSystemSize == 2 && simulateJoiner)) {
			System.out.println("World_system_shell class error!!!");
			System.exit(0);
		}

	}

}
