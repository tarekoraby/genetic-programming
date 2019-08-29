import java.util.Arrays;
import java.util.Random;

public class World_System_Shell_v2 {
	
	public int  currentStatesNum, PC;
	public double capAvg, capMed, capSS, capStdDev, capLeading_1, capLeading_2, myCap, oppCap, mySideCap;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;

	public World_System_Shell_v2(boolean simulateJoiner) { // this might lead to uncreatable worlds, but that shouldn't affect profiling
		this.simulateJoiner = simulateJoiner;
		currentStatesNum = GP_PilotStudy_v2.MINSYSTEM
				+ rd.nextInt(GP_PilotStudy_v2.MAXSYSTEM - GP_PilotStudy_v2.MINSYSTEM + 1);
		if (simulateJoiner) {
			while (currentStatesNum <= 2)
				currentStatesNum = GP_PilotStudy_v2.MINSYSTEM
						+ rd.nextInt(GP_PilotStudy_v2.MAXSYSTEM - GP_PilotStudy_v2.MINSYSTEM + 1);
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
		case GP_PilotStudy_v2.CAPAVG:
			return (capAvg);
		case GP_PilotStudy_v2.CAPMED:
			return (capMed);
		case GP_PilotStudy_v2.CAPSS:
			return (capSS);	
		case GP_PilotStudy_v2.CAPSTDEV:
			return (capStdDev);
		case GP_PilotStudy_v2.CAPLEADING_1:
			return (capLeading_1);
		case GP_PilotStudy_v2.CAPLEADING_2:
			return (capLeading_2);
		case GP_PilotStudy_v2.MYCAP:
			return (myCap);
		case GP_PilotStudy_v2.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v2.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v2.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v2.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v2.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v2.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v2.ADD:
			return (run() + run());
		case GP_PilotStudy_v2.SUB:
			return (run() - run());
		case GP_PilotStudy_v2.MUL:
			return (run() * run());
		case GP_PilotStudy_v2.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v2.randNum[primitive]);
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
		//since capabilities should already be normalized average can be calculated as follows
		capAvg= 1.0 / (double)currentStatesNum;

		capSS = 0;
		for (int i = 0; i < currentStatesNum; i++)
				capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capStdDev = Math.sqrt(capSS / currentStatesNum);
		
		//calculate median
		double[] temp=(double[]) capabilities.clone();
		Arrays.sort(temp);
		int middle = currentStatesNum / 2;
		if (currentStatesNum % 2 == 1) {
			capMed = temp[middle];
		} else {
			capMed = (temp[middle - 1] + temp[middle]) / 2;
		}
		
		capLeading_1 = temp[currentStatesNum - 1];
		capLeading_2 = temp[currentStatesNum - 2];
	}

}
