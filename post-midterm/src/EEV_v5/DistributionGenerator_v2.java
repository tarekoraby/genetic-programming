package EEV_v5;

import java.util.Random;

public class DistributionGenerator_v2 {
	private  int counter;
	


	public double[][] calcDistribution(int size, int step, int totalPool, boolean onlyGreater, boolean uniqueCombinations) {

		if (size * step > totalPool) {
			Thread.dumpStack();
			System.exit(0);
		}
		counter = 0;

		int firstPlayerAmount = step;
		int[][] distributions = caculateDistribution(onlyGreater, firstPlayerAmount, step, totalPool, size);
		
		

		// remove any illegal combinations
		if (onlyGreater || uniqueCombinations) {
			int numOfIllegals = 0;
			for (int j = 0; j < distributions.length; j++) {
				int x = 0;
				if (uniqueCombinations)
					x = 2;
				for (int k = x; k < distributions[j].length - 1; k++) {
					if (distributions[j][k] > distributions[j][k + 1]) {
						distributions[j] = null;
						numOfIllegals++;
						break;
					}
				}
			}
			

			if (numOfIllegals > 0) {
				int[][] newDistributions = new int[distributions.length - numOfIllegals][];
				int counter = 0;
				for (int j = 0; j < distributions.length; j++) {
					if (distributions[j] == null)
						continue;
					newDistributions[counter++] = distributions[j].clone();
				}
				distributions = newDistributions;
			}
		}

		/*
		 * System.out.println("Total number of ways is : " +
		 * (distributions.length - numOfIllegals)); for (int j = 0; j <
		 * distributions.length; j++) { if (distributions[j] == null) continue;
		 * for (int k = 0; k < distributions[j].length; k++) {
		 * System.out.print(distributions[j][k] + "\t"); } System.out.println();
		 * } System.out.println(
		 * "******************************************************");
		 */
		
		if (distributions.length< 5){
			Thread.dumpStack();
			System.exit(0);
		}
		
		double[][] doubleDistributions = new double[distributions.length][];
		for (int i = 0; i < doubleDistributions.length; i++) {
			doubleDistributions[i] = new double[distributions[i].length];
			for (int k = 0; k < doubleDistributions[i].length; k++) {
				doubleDistributions[i][k] = (double) distributions[i][k] * 100 / totalPool;
			}
		}
		
		/*
		Random rd = new Random();
		doubleDistributions = new double[1000][];
		for (int i = 0; i < doubleDistributions.length; i++) {
			doubleDistributions[i] = new double[size];
			double sum = 0;
			for (int k = 0; k < doubleDistributions[i].length; k++) {
				doubleDistributions[i][k] = rd.nextDouble();
				sum += doubleDistributions[i][k];
				
			}
			for (int k = 0; k < doubleDistributions[i].length; k++) {
				doubleDistributions[i][k] = (double) doubleDistributions[i][k] * 100 / sum;
			}
		}*/

		return doubleDistributions;

	}

	private int[][] caculateDistribution(boolean onlyGreater, int firstPlayerAmount, int step, int total, int totalInitialPlayers) {
		int[][] distribution = new int[1000000][totalInitialPlayers];
		counter = 0;

		int[] array = new int[totalInitialPlayers];
		Split(onlyGreater, firstPlayerAmount, step, total, totalInitialPlayers, 0, array, distribution);

		int i = 0, unique = 0;
		while (distribution[i][totalInitialPlayers - 1] > 0) {
			unique++;
			i++;
		}
		int[][] result = new int[unique][];
		System.arraycopy(distribution, 0, result, 0, unique);

		return result;

	}

	private void Split(boolean onlyGreater, int firstPlayerAmount, int step, int total, int totalPlayers, int playerIndex,
			int[] receivedArray, int[][] distribution) {

		if (totalPlayers - 1 == 0) {
			receivedArray[playerIndex] = total;
			
			if (onlyGreater)
				for (int i = 2; i < receivedArray.length - 1; i++) {
					if (receivedArray[i] > receivedArray[i + 1]) {
						return;
					}
				}

			for (int i = 0; i < receivedArray.length; i++) {
				distribution[counter][i] = receivedArray[i];
			}
			counter++;
			return;
		}

		int remainder = 0;
		for (int i = firstPlayerAmount; i <= total - totalPlayers + 1; i += step) {
			receivedArray[playerIndex] = i;
			remainder = total - i;
			Split(onlyGreater, step, step, remainder, totalPlayers - 1, playerIndex + 1, receivedArray, distribution);
		}
	}
}
