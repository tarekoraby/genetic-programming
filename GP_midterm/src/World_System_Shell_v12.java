import java.util.Arrays;
import java.util.Random;

public class World_System_Shell_v12 {

	public int currentStatesNum, PC;
	public double capMed, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;

	public World_System_Shell_v12(boolean simulateJoiner) {
		this.simulateJoiner = simulateJoiner;
		currentStatesNum = GP_PilotStudy_v12.MINSYSTEM
				+ rd.nextInt(GP_PilotStudy_v12.MAXSYSTEM
						- GP_PilotStudy_v12.MINSYSTEM + 1);
		if (simulateJoiner) {
			while (currentStatesNum <= 2)
				currentStatesNum = GP_PilotStudy_v12.MINSYSTEM
						+ rd.nextInt(GP_PilotStudy_v12.MAXSYSTEM
								- GP_PilotStudy_v12.MINSYSTEM + 1);
		}

		capabilities = new double[currentStatesNum];
		for (int i = 0; i < currentStatesNum; i++)
			capabilities[i] = rd.nextDouble();

		normalizeCapabilities_simplified();
		calculateSystemicVariables_simplified();

		int myIndex = rd.nextInt(currentStatesNum);
		if (GP_PilotStudy_v12.PROFILEPOWER)
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

	}

	boolean willItAttack(char[] strategy_init_2, char[] strategy_join_3) {
		if (!simulateJoiner) {
			this.strategy = strategy_init_2;
		} else
			this.strategy = strategy_join_3;
		PC = 0;
		if (run() == 1)
			return (true);
		else
			return (false);
	}

	private double run() {
		char primitive = strategy[PC++];

		switch (primitive) {
		case GP_PilotStudy_v12.CAPMED:
			return (capMed);
		case GP_PilotStudy_v12.CAPMIN:
			return (capMin);
		case GP_PilotStudy_v12.CAPMAX:
			return (capMax);
		case GP_PilotStudy_v12.MYCAP:
			return (myCap);
		case GP_PilotStudy_v12.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v12.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v12.LEFTCAPSUM:
			return (leftCapSum);
		case GP_PilotStudy_v12.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v12.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v12.EQ:
			if (run() == run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v12.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v12.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v12.ADD:
			return (run() + run());
		case GP_PilotStudy_v12.SUB:
			return (run() - run());
		case GP_PilotStudy_v12.MUL:
			return (run() * run());
		case GP_PilotStudy_v12.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v12.randNum[primitive]);
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
