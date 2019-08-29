import java.util.Arrays;
import java.util.Random;

public class World_System_Shell_v4 {
	
	public int  currentStatesNum, PC;
	public double capMed, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;

	public World_System_Shell_v4(boolean simulateJoiner) { 
		this.simulateJoiner = simulateJoiner;
		currentStatesNum = GP_PilotStudy_v4.MINSYSTEM
				+ rd.nextInt(GP_PilotStudy_v4.MAXSYSTEM - GP_PilotStudy_v4.MINSYSTEM + 1);
		if (simulateJoiner) {
			while (currentStatesNum <= 2)
				currentStatesNum = GP_PilotStudy_v4.MINSYSTEM
						+ rd.nextInt(GP_PilotStudy_v4.MAXSYSTEM - GP_PilotStudy_v4.MINSYSTEM + 1);
		}
		
		capabilities = new double[currentStatesNum];
		for (int i = 0; i < currentStatesNum; i++)
			capabilities[i] = rd.nextDouble();

		normalizeCapabilities_simplified();
		calculateSystemicVariables_simplified();

		int myIndex = rd.nextInt(currentStatesNum);
		myCap = capabilities[myIndex];
		if (!simulateJoiner) {
			mySideCap = myCap;
			int oppIndex = rd.nextInt(currentStatesNum);
			while (myIndex == oppIndex)
				oppIndex = rd.nextInt(currentStatesNum);
			oppCap = capabilities[oppIndex];
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
				if (rd.nextInt(3) == 0)
					if (rd.nextInt(2) == 0)
						mySideCap += capabilities[i];
					else
						oppCap += capabilities[i];

			}
		}

	}
	
	boolean willItAttack(char[]strategy_1, char[]strategy_2){
		if (!simulateJoiner)
			this.strategy=strategy_1;
		else 
			this.strategy=strategy_2;
		PC=0;
		if (run()==1)
			return(true);
		else
			return(false);
	}
	
	private double run() {
		char primitive = strategy[PC++];
		
		switch (primitive) {
		case GP_PilotStudy_v4.CAPMED:
			return (capMed);
		case GP_PilotStudy_v4.CAPMIN:
			return (capMin);	
		case GP_PilotStudy_v4.CAPMAX:
			return (capMax);
		case GP_PilotStudy_v4.MYCAP:
			return (myCap);
		case GP_PilotStudy_v4.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v4.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v4.LEFTCAPSUM:
			return (leftCapSum);
		case GP_PilotStudy_v4.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v4.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v4.EQ:
			if (run() == run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v4.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v4.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v4.ADD:
			return (run() + run());
		case GP_PilotStudy_v4.SUB:
			return (run() - run());
		case GP_PilotStudy_v4.MUL:
			return (run() * run());
		case GP_PilotStudy_v4.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v4.randNum[primitive]);
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

		
		//calculate median
		double[] temp=capabilities.clone();
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
