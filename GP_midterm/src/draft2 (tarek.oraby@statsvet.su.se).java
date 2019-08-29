import java.util.Random;
import java.util.Spliterator;

public class draft2 {
	static int numberOfWays, numberOfGreaterWays, counter;

	public static void main(String[] args) {
		System.out.println("START!!!");
		/*
		 * Random rd=new Random(); int total=10000000; double sum=0, avg=0,
		 * capSS = 0;; double[] num=new double[total]; for (int i=0; i<total;
		 * i++){ num[i]=Math.abs(rd.nextGaussian() ); sum+=num[i]; }
		 * avg=sum/total; System.out.println(avg); for (int i=0; i<total; i++){
		 * capSS += (num[i] - avg) * (num[i] - avg) ; }
		 * System.out.println(Math.sqrt(capSS/total));
		 */
		int TOTAL_CAP_POOL = 120;
		numberOfWays = 0;
		numberOfGreaterWays = 0;
		

		int MINSYSTEM = 2, MAXSYSTEM = 5;

		int uniqueWorldSizes = MAXSYSTEM - MINSYSTEM + 1;

		int[][][] cap_distributions = new int[uniqueWorldSizes][][];

		

		for (int i = 0; i > uniqueWorldSizes; i++) {
			int step = i + MINSYSTEM;
			int totalInitialPlayers = i + MINSYSTEM;
			if (TOTAL_CAP_POOL < totalInitialPlayers) {
				System.out.print("ERROR!");
				System.exit(0);
			}
			cap_distributions[i] = calculateDistributions(step, step, TOTAL_CAP_POOL, totalInitialPlayers, 0, 0);

		}

		System.out.println("Total number of ways is : " + numberOfWays);
		System.out.println("Total number of Greater ways is : " + numberOfGreaterWays);
	}

	private static int[][] calculateDistributions(int firstPlayerAmount, int step, int total, int totalPlayers,
			int distributionIndex, int playerIndex) {
		int[][] capDistribution=new int[100000000][totalPlayers];
		counter=0;
		
		for (int i=0; i<capDistribution.length; i++){
			Split(step, step, total, totalPlayers, 0, capDistribution);
			if(capDistribution[i]==null)
				break;
		}
		
		return capDistribution;
	}

	private static void Split(int firstPlayerAmount, int step, int total, int totalPlayers, int playerIndex, int[][] capDistribution) {
		
		if (totalPlayers == 1) {
			capDistribution[counter++][playerIndex]=total;
			return;
		}

		int remainder = 0;

		for (int i = firstPlayerAmount; i <= total - totalPlayers + 1; i += step) {
			capDistribution[counter][playerIndex] = i;
			remainder = total - i;
			Split(step, step, remainder, totalPlayers - 1, playerIndex++, capDistribution);
		}
	}
}
