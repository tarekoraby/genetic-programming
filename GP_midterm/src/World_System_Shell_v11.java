import java.util.Arrays;
import java.util.Random;

public class World_System_Shell_v11 {
	
	public int  currentStatesNum, PC;
	public double capMed, capMin, capMax, myCap, oppCap, mySideCap, leftCapSum;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;
	boolean simulateJoiner;

	public World_System_Shell_v11(boolean simulateJoiner) { 
		this.simulateJoiner = simulateJoiner;
		currentStatesNum = GP_PilotStudy_v11.MINSYSTEM
				+ rd.nextInt(GP_PilotStudy_v11.MAXSYSTEM - GP_PilotStudy_v11.MINSYSTEM + 1);
		if (simulateJoiner) {
			while (currentStatesNum <= 2)
				currentStatesNum = GP_PilotStudy_v11.MINSYSTEM
						+ rd.nextInt(GP_PilotStudy_v11.MAXSYSTEM - GP_PilotStudy_v11.MINSYSTEM + 1);
		}
		

		capabilities = new double[currentStatesNum];
		for (int i = 0; i < currentStatesNum; i++)
			capabilities[i] = rd.nextDouble();

		normalizeCapabilities_simplified();
		calculateSystemicVariables_simplified();

		int myIndex = rd.nextInt(currentStatesNum);
		if (GP_PilotStudy_v11.PROFILEPOWER)
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
			if (currentStatesNum>2)
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
	
	boolean willItAttack(char[] strategy_init_2, char[] strategy_init_3, char[] strategy_init_4, char[] strategy_join_3){
		if (!simulateJoiner) {
			if (currentStatesNum == 2)
				this.strategy = strategy_init_2;
			else if (currentStatesNum == 3)
				this.strategy = strategy_init_3;
			else if (currentStatesNum == 4)
				this.strategy = strategy_init_4;
		} else
			this.strategy = strategy_join_3;
		PC=0;
		if (run()==1)
			return(true);
		else
			return(false);
	}
	
	private double run() {
		char primitive = strategy[PC++];
		
		switch (primitive) {
		case GP_PilotStudy_v11.CAPMED:
			return (capMed);
		case GP_PilotStudy_v11.CAPMIN:
			return (capMin);	
		case GP_PilotStudy_v11.CAPMAX:
			return (capMax);
		case GP_PilotStudy_v11.MYCAP:
			return (myCap);
		case GP_PilotStudy_v11.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v11.MYSIDECAP:
			return (mySideCap);
		case GP_PilotStudy_v11.LEFTCAPSUM:
			return (leftCapSum);
		case GP_PilotStudy_v11.GT:
			if (run() > run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v11.LT:
			if (run() < run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v11.EQ:
			if (run() == run())
				return (1);
			else
				return (0);
		case GP_PilotStudy_v11.AND:
			if (run() == 1 && run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v11.OR:
			if (run() == 1 || run() == 1)
				return (1);
			else
				return (0);
		case GP_PilotStudy_v11.ADD:
			return (run() + run());
		case GP_PilotStudy_v11.SUB:
			return (run() - run());
		case GP_PilotStudy_v11.MUL:
			return (run() * run());
		case GP_PilotStudy_v11.DIV: {
			double num = run(), den = run();
			if (Math.abs(den) <= 0.001)
				return (num);
			else
				return (num / den);
		}
		default:
			return (GP_PilotStudy_v11.randNum[primitive]);
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
		double[] temp=(double[]) capabilities.clone();
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
