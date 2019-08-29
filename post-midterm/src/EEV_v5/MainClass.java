package EEV_v5;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;



public class MainClass {

	static int resourcesStepSize = 5;
	//static int territoryStepSize = 2;
	static int sampleSize= 10000;
	static boolean terrDistEqualResDest = true;


	public static void main(String[] args) {
		runSimulation();

	}

	private static void runSimulation() {

		int initialWorldSize = 4;
		int maxWorldSize =8;
		
		ArrayList<Result> results = new ArrayList<Result>();
		
		for (int worldSize = initialWorldSize; worldSize <= maxWorldSize; worldSize++) {
			switch (worldSize) {
			case 2:
				resourcesStepSize = 1;
				break;
			case 3:
				resourcesStepSize = 2;
				break;
			case 4:
				resourcesStepSize = 3;
				break;
			case 5:
				resourcesStepSize = 4;
				break;
			case 6:
				resourcesStepSize = 4;
				break;
			case 7:
				resourcesStepSize = 4;
				break;
			case 8:
				resourcesStepSize = 4;
				break;
			case 9:
				resourcesStepSize = 4;
				break;
			case 10:
				resourcesStepSize = 4;
				break;
				
			case 11:
				resourcesStepSize = 4;
				break;
				
			case 12:
				resourcesStepSize = 4;
				break;
				
			case 13:
				resourcesStepSize = 4;
				break;
				
			case 14:
				resourcesStepSize = 4;
				break;
				/*
			case 15:
				resourcesStepSize = 4;
				break;
			case 16:
				resourcesStepSize = 4;
				break;
			case 17:
				resourcesStepSize = 4;
				break;
			case 18:
				resourcesStepSize = 4;
				break;
			case 19:
				resourcesStepSize = 4;
				break;
			case 20:
				resourcesStepSize = 4;
				break;
				*/

			default:
				resourcesStepSize = 3;
				break;
			}
			System.out.println("\n\n***********************************************");
			System.out.print("World Size " + worldSize );
			DistributionGenerator DG = new DistributionGenerator();
			DistributionGenerator_v2 DG2 = new DistributionGenerator_v2();
			double[][] resourcesDistribution = DG2.calcDistribution(worldSize, resourcesStepSize, 100, true, true);
			double[][] territoriesDistribution = new double[1][];
			if (!terrDistEqualResDest) {
				territoriesDistribution = DG.calcDistribution(worldSize, 5, 100, false, false);
			}
			System.out.println(". Num of distributions " + resourcesDistribution.length + "\n\n");
			for (int resDistCounter = 0; resDistCounter < resourcesDistribution.length; resDistCounter++) {
				if (terrDistEqualResDest)
					territoriesDistribution[0] = resourcesDistribution[resDistCounter].clone();

				for (int terrDistCounter = 0; terrDistCounter < territoriesDistribution.length; terrDistCounter++) {
					/*
					 * if (territoriesDistribution[terrDistCounter][0] !=
					 * resourcesDistribution[resDistCounter][0]) System.exit(0);
					 */
					double EEV = calcEEV(1, resourcesDistribution[resDistCounter],
							territoriesDistribution[terrDistCounter], sampleSize);
					results.add(new Result(worldSize, 1, EEV, resourcesDistribution[resDistCounter],
							territoriesDistribution[terrDistCounter]));
				}
			}
			
			/*
			// calc for att order 0
			resourcesDistribution = DG.calcDistribution(worldSize, resourcesStepSize, 100, true, true);
			territoriesDistribution = new double[1][];
			if (!terrDistEqualResDest) {
				territoriesDistribution = DG.calcDistribution(worldSize, 5, 100, false, false);
			}
			for (int resDistCounter = 0; resDistCounter < resourcesDistribution.length; resDistCounter++) {
				if (terrDistEqualResDest)
					territoriesDistribution[0] = resourcesDistribution[resDistCounter].clone();

				for (int terrDistCounter = 0; terrDistCounter < territoriesDistribution.length; terrDistCounter++) {
					
					double EEV = calcEEV(0, resourcesDistribution[resDistCounter],
							territoriesDistribution[terrDistCounter], sampleSize);
					results.add(new Result(worldSize, 0, EEV, resourcesDistribution[resDistCounter],
							territoriesDistribution[terrDistCounter]));
				}
			}*/

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
		
		fileWriter.writeToFile("WorldSize" + "," + "attackOrder" + "," + "EEV" + "," + "eevApplyTo");
		for (int j = 1; j <= maxWorldSize; j++) {
			fileWriter.writeToFile("," + "res_state_" + j);
		}
		
		for (int j = 1; j <= maxWorldSize; j++) {
			fileWriter.writeToFile("," + "terr_state_" + j);
		}
		for (int j = 1; j < maxWorldSize; j++) {
			fileWriter.writeToFile("," + "ordered_res_state_" + j);
		}
		
		for (int j = 1; j < maxWorldSize; j++) {
			fileWriter.writeToFile("," + "ordered_terr_state_" + j);
		}
		fileWriter.writeToFile("\n");
		
		Result currResult;
		int currWorldSize;
		for (int resultCounter=0; resultCounter<results.size(); resultCounter++){
			currResult = results.get(resultCounter);
			currWorldSize = currResult.worldSize;
			fileWriter.writeToFile(currWorldSize + "," + currResult.attackOrder + "," + currResult.EEV );
			
			short eevApplyTo = 1;
			if (currResult.attackOrder == 1)
				for (int i = 2; i < currResult.resDistr.length; i++) {
					if (currResult.resDistr[1] == currResult.resDistr[i]
							&& currResult.terrDistr[1] == currResult.terrDistr[i]) {
						eevApplyTo++;
					}
				}
			fileWriter.writeToFile("," + eevApplyTo);
			
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
			
			double[] orderedResDistr = Arrays.copyOfRange(currResult.resDistr, 1, currResult.resDistr.length);
			double[] orderedTerrDistr = Arrays.copyOfRange(currResult.terrDistr, 1, currResult.terrDistr.length);
			Arrays.sort(orderedResDistr);
			Arrays.sort(orderedTerrDistr);
			for (int j = 1; j < maxWorldSize; j++) {
				if (j < currWorldSize)
					fileWriter.writeToFile("," + orderedResDistr[j - 1]);
				else 
					fileWriter.writeToFile("," + ".");
			}
			
			for (int j = 1; j < maxWorldSize; j++) {
				if (j < currWorldSize)
					fileWriter.writeToFile("," + orderedTerrDistr[j - 1]);
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
		//RandomWorld_T4 RW = new RandomWorld_T4(resDistr, terrDistr);
		/*
		 * if (resDistr[0] != terrDistr[0]) System.exit(0);
		 */

		for (int test = 0; test < sampleSize; test++) {
			// double x = RW.reCalcValue();
			// System.out.println(x);
			EEV += RW.reCalcValue();
		}
		return EEV / (double) sampleSize;
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
