package EEV_v1;

public class DistributionGenerator {
	private  int counter;
	


	public int[][] calcDistribution(int size, int step, int totalPool, boolean onlyGreater) {

		if (size * step > totalPool) {
			Thread.dumpStack();
			System.exit(0);
		}
		counter = 0;

		int firstPlayerAmount = step;
		int[][] distributions = caculateDistribution(firstPlayerAmount, step, totalPool, size);

		// remove any illegal combinations
		if (onlyGreater) {
			int numOfIllegals = 0;
			for (int j = 0; j < distributions.length; j++) {
				for (int k = 0; k < distributions[j].length - 1; k++) {
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

		return distributions;

	}

	private int[][] caculateDistribution(int firstPlayerAmount, int step, int total, int totalInitialPlayers) {
		int[][] distribution = new int[100000][totalInitialPlayers];
		counter = 0;

		int[] array = new int[totalInitialPlayers];
		Split(firstPlayerAmount, step, total, totalInitialPlayers, 0, array, distribution);

		int i = 0, unique = 0;
		while (distribution[i][totalInitialPlayers - 1] > 0) {
			unique++;
			i++;
		}
		int[][] result = new int[unique][];
		System.arraycopy(distribution, 0, result, 0, unique);

		return result;

	}

	private void Split(int firstPlayerAmount, int step, int total, int totalPlayers, int playerIndex,
			int[] receivedArray, int[][] distribution) {

		if (totalPlayers - 1 == 0) {
			receivedArray[playerIndex] = total;

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
			Split(step, step, remainder, totalPlayers - 1, playerIndex + 1, receivedArray, distribution);
		}
	}
}
