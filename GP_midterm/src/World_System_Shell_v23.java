import java.util.Arrays;
import java.util.Random;

public class World_System_Shell_v23 {

	public int currentStatesNum, PC;
	public double capMed, capAvg, capStd, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum, myEnmity, oppEnmity;
	public double capRatio;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;
	public double largerSideCap, smallerSideCap;

	public World_System_Shell_v23(boolean simulateJoiner, int length) {
		this.simulateJoiner = simulateJoiner;
		currentStatesNum = length;

		capabilities = new double[currentStatesNum];
		for (int i = 0; i < currentStatesNum; i++)
			capabilities[i] = rd.nextDouble();

		myEnmity = rd.nextInt(GP_PilotStudy_v23.ENMITYLEVELS - 1);
		oppEnmity = rd.nextInt(GP_PilotStudy_v23.ENMITYLEVELS - 1);

		normalizeCapabilities_simplified();
		calculateSystemicVariables_simplified();

		int myIndex = rd.nextInt(currentStatesNum);
		if (GP_PilotStudy_v23.PROFILEPOWER)
			for (int i = 0; i < currentStatesNum; i++)
				if (capabilities[i] == capMax) {
					myIndex = i;
					capabilities[i] = capabilities[i] * 10000000;
					normalizeCapabilities_simplified();
					calculateSystemicVariables_simplified();
				}

		myCap = capabilities[myIndex];
		if (!simulateJoiner) {
			mySideCap = myCap;
			int oppIndex = rd.nextInt(currentStatesNum);
			while (myIndex == oppIndex)
				oppIndex = rd.nextInt(currentStatesNum);
			oppCap = capabilities[oppIndex];
			leftCapSum = 0;
			if (currentStatesNum > 2)
				leftCapSum = 1 - myCap - oppCap;
		} else {
			int attackerIndex = rd.nextInt(currentStatesNum);
			while (myIndex == attackerIndex)
				attackerIndex = rd.nextInt(currentStatesNum);
			mySideCap = capabilities[attackerIndex];
			int targetIndex = rd.nextInt(currentStatesNum);
			while (myIndex == targetIndex || attackerIndex == targetIndex)
				targetIndex = rd.nextInt(currentStatesNum);
			oppCap = capabilities[targetIndex];
			for (int i = 0; i < currentStatesNum; i++) {
				if (i == myIndex || i == attackerIndex || i == targetIndex)
					continue;
				if (rd.nextInt(2) == 0)
					if (rd.nextInt(2) == 0)
						mySideCap += capabilities[i];
					else
						oppCap += capabilities[i];

			}
			leftCapSum = 1 - mySideCap - oppCap;
		}

		if (mySideCap > oppCap) {
			largerSideCap = mySideCap;
			smallerSideCap = oppCap;
		} else {
			largerSideCap = oppCap;
			smallerSideCap = mySideCap;
		}
		
		capRatio=myCap/oppCap;

	}

	boolean willItAttack(char[] strategy, int joinKind) {
		this.strategy = strategy;

		if (joinKind == 1) {
			mySideCap = smallerSideCap;
			oppCap = largerSideCap;
		} else if (joinKind == 2) {
			mySideCap = largerSideCap;
			oppCap = smallerSideCap;
		}

		PC = 0;
		if (run() == 1)
			return (true);
		else
			return (false);
	}

	private double run() {
		char primitive = strategy[PC++];

		switch (primitive) {
		case GP_PilotStudy_v23.CAPMED:
			return (capMed);
		case GP_PilotStudy_v23.CAPAVG:
			return (capAvg);
		case GP_PilotStudy_v23.CAPSTD:
			return (capStd);
		case GP_PilotStudy_v23.CAPMIN:
			return (capMin);
		case GP_PilotStudy_v23.CAPMAX:
			return (capMax);
		case GP_PilotStudy_v23.MYCAP:
			return (myCap);
		case GP_PilotStudy_v23.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v23.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v23.LEFTCAPSUM:
			return (leftCapSum);
		case GP_PilotStudy_v23.MYENMITY:
			return (myEnmity);
		case GP_PilotStudy_v23.OPPENMITY:
			return (oppEnmity);
		case GP_PilotStudy_v23.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v23.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v23.EQ:
			if (run() == run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v23.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v23.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v23.ADD:
			return (run() + run());
		case GP_PilotStudy_v23.SUB:
			return (run() - run());
		case GP_PilotStudy_v23.MUL:
			return (run() * run());
		case GP_PilotStudy_v23.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v23.randNum[primitive]);
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

		capAvg = (double) 1 / currentStatesNum;

		double capSS = 0;
		for (int i = 0; i < currentStatesNum; i++)
			capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capStd = Math.sqrt(capSS / currentStatesNum);

		// calculate median
		double[] temp = (double[]) capabilities.clone();
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

}
