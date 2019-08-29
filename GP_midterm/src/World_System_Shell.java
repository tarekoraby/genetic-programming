import java.util.Arrays;
import java.util.Random;

public class World_System_Shell {
	
	public int  currentStatesNum, PC;
	public double capAvg, capMed, capSS, capStdDev, myCap, oppCap, oppCapSum;
	public double[] capabilities;
	static Random rd = new Random();
	char[] strategy;

	public World_System_Shell() { // this might lead to uncreatable worlds, but that shouldn't affect profiling
		currentStatesNum = GP_PilotStudy_v2.MINSYSTEM + rd.nextInt(GP_PilotStudy_v2.MAXSYSTEM - GP_PilotStudy_v2.MINSYSTEM + 1);
		
		capabilities = new double[currentStatesNum];
		for (int i = 0; i < currentStatesNum; i++)
			capabilities[i] = rd.nextDouble();

		normalizeCapabilities_simplified();
		calculateSystemicVariables_simplified();

		int myIndex = rd.nextInt(currentStatesNum);
		myCap = capabilities[myIndex];
		int oppIndex = rd.nextInt(currentStatesNum);
		while (myIndex == oppIndex)
			oppIndex = rd.nextInt(currentStatesNum);
		oppCap = capabilities[oppIndex];
		oppCapSum = oppCap;

		int oppNo = rd.nextInt(currentStatesNum + 1) - 2;

		if (oppNo <= 0)
			for (int i = 0; i < currentStatesNum; i++) {
				if (i == myIndex || i == oppIndex)
					continue;
				oppCapSum += capabilities[i];
				oppNo--;
				if (oppNo == 0)
					break;
			}
		
	}
	
	boolean willItAttack(char[]strategy){
		PC=0;
		this.strategy=strategy;
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
		case GP_PilotStudy_v2.CURRENTSTATESNUM:
			return (currentStatesNum);
		case GP_PilotStudy_v2.MYCAP:
			return (myCap);
		case GP_PilotStudy_v2.OPPCAP:
			return (oppCap);
		case GP_PilotStudy_v2.OPPCAPSUM:
			return (oppCapSum);
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
		double[] temp=capabilities.clone();
		Arrays.sort(temp);
		int middle = currentStatesNum / 2;
		if (currentStatesNum % 2 == 1) {
			capMed = temp[middle];
		} else {
			capMed = (temp[middle - 1] + temp[middle]) / 2;
		}
	}

}
