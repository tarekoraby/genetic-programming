package EEV_v3;

import java.io.IOException;
import java.util.ArrayList;



public class MainClass {

	//static int resourcesStepSize = 2;
	//static int territoryStepSize = 2;
	static int sampleSize= 100000;
	static boolean terrDistEqualResDest = true;


	public static void main(String[] args) {
		runSimulation();

	}

	private static void runSimulation() {

		int initialWorldSize = 4;
		int maxWorldSize = 8;
		
		ArrayList<Result> results = new ArrayList<Result>();
		
		for (int worldSize = initialWorldSize; worldSize <= maxWorldSize; worldSize++) {
			System.out.println("\n\n***********************************************");
			System.out.println("World Size " + worldSize + "\n\n");
			DistributionGenerator DG = new DistributionGenerator();
			double[][] resourcesDistribution = DG.calcDistribution(worldSize, 5, 100, true, false);
			double[][] territoriesDistribution = new double[1][];
			if (!terrDistEqualResDest) {
				territoriesDistribution = DG.calcDistribution(worldSize, 1, worldSize*40, false, false);
			}
			for (int attackOrder = 0; attackOrder < worldSize; attackOrder++) {
				for (int resDistCounter = 0; resDistCounter < resourcesDistribution.length; resDistCounter++) {
					if (terrDistEqualResDest)
						territoriesDistribution[0] = resourcesDistribution[resDistCounter].clone();
					
					for (int terrDistCounter = 0; terrDistCounter < territoriesDistribution.length; terrDistCounter++) {
						/*if (territoriesDistribution[terrDistCounter][0] != resourcesDistribution[resDistCounter][0])
							System.exit(0);*/
						double EEV = calcEEV(attackOrder, resourcesDistribution[resDistCounter],
								territoriesDistribution[terrDistCounter], sampleSize);
						results.add(new Result(worldSize, attackOrder, EEV, resourcesDistribution[resDistCounter],
								territoriesDistribution[terrDistCounter]));
					}
				}
			}
		}
		
		printResultsToFile(results, maxWorldSize);

	}


	private static void printResultsToFile(ArrayList<Result> results, int maxWorldSize) {
		WriteFile fileWriter= null;
		try {
			if (terrDistEqualResDest)
				fileWriter = new WriteFile("resultsV1.txt", false);
			else
				fileWriter = new WriteFile("resultsV2.txt", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fileWriter.writeToFile("WorldSize" + "," + "attackOrder" + "," + "EEV");
		for (int j = 1; j <= maxWorldSize; j++) {
			fileWriter.writeToFile("," + "res_state_" + j);
		}
		
		for (int j = 1; j <= maxWorldSize; j++) {
			fileWriter.writeToFile("," + "terr_state_" + j);
		}
		fileWriter.writeToFile("\n");
		
		Result currResult;
		int currWorldSize;
		for (int resultCounter=0; resultCounter<results.size(); resultCounter++){
			currResult = results.get(resultCounter);
			currWorldSize = currResult.worldSize;
			fileWriter.writeToFile(currWorldSize + "," + currResult.attackOrder + "," + currResult.EEV );
			for (int j = 1; j <= maxWorldSize; j++) {
				if (j <= currWorldSize)
					fileWriter.writeToFile("," + currResult.resDistr[j - 1]);
				else 
					fileWriter.writeToFile("," + ".");
			}
			
			for (int j = 1; j <= maxWorldSize; j++) {
				if (j <= currWorldSize)
					fileWriter.writeToFile("," + currResult.terrDistr[j - 1]);
				else 
					fileWriter.writeToFile("," + ".");
			}
			fileWriter.writeToFile("\n");
		}
		
		fileWriter.flush();
		
	}




	private static double calcEEV(int attackOrder, double[] resDistr, double[] terrDistr, int sampleSize) {
		double EEV = 0;

		RandomWorld_T2 RW = new RandomWorld_T2(attackOrder, resDistr, terrDistr, 1);
		/*if (resDistr[0] != terrDistr[0])
			System.exit(0);*/
		
		for (int test = 0; test < sampleSize; test++) {
			//double x = RW.reCalcValue();
			//		System.out.println(x);
			EEV +=RW.reCalcValue();
		}
		return EEV/(double)sampleSize;
	}

	

	static long[] decToTernary(long input, int arraySize){
		long ret = 0, factor = 1;
	    while (input > 0) {
	        ret += input % 3 * factor;
	        input /= 3;
	        factor *= 10;
	    }
	    return numToArray(ret, arraySize);
	}
	
	static long[] numToArray(long number, int arraySize){
		long[] iarray = new long[arraySize];
		for (int index = 0; index < arraySize; index++) {
		    iarray[index] = number % 10;
		    number /= 10;
		}
		return iarray;
	}

}
