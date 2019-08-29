package v20;
import java.util.Random;
import java.util.Spliterator;


public class draft3 {
	static int counter;
	
	public static void main(String[] args) {
		System.out.println("START!!!");
		/*Random rd=new Random();
		int total=10000000;
		double sum=0, avg=0, capSS = 0;;
		double[] num=new double[total];
		for (int i=0; i<total; i++){
			num[i]=Math.abs(rd.nextGaussian() );
			sum+=num[i];
		}
		avg=sum/total;
			System.out.println(avg);
			for (int i=0; i<total; i++){
				capSS += (num[i] - avg) * (num[i] - avg) ;
			}
			System.out.println(Math.sqrt(capSS/total));
		*/	
		int TOTAL_CAP_POOL = 100;
		
		int MINSYSTEM = 2, MAXSYSTEM = 8;
		int step = 4;
		int uniqueWorldSizes = MAXSYSTEM - MINSYSTEM + 1;

		int[][][] cap_distributions = new int[uniqueWorldSizes][][];

		for (int i = 0; i < uniqueWorldSizes; i++) {
			//int step = i + MINSYSTEM;
			int firstPlayerAmount = step;
			int totalInitialPlayers = i + MINSYSTEM;
			if (TOTAL_CAP_POOL < totalInitialPlayers) {
				System.out.print("ERROR!");
				System.exit(0);
			}
			cap_distributions[i] = caculateCapDistribution(firstPlayerAmount, step, TOTAL_CAP_POOL, totalInitialPlayers);
		}
		
		//remove any illigal comibinations
		int[] numOfIllegals= new int[uniqueWorldSizes];
		for (int i = 0; i < cap_distributions.length; i++) {
			for (int j = 0; j < cap_distributions[i].length; j++) {
				for (int k = 0; k < cap_distributions[i][j].length - 1; k++) {
					if(cap_distributions[i][j][k] > cap_distributions[i][j][k+1]){
						cap_distributions[i][j] =null;
						numOfIllegals[i]++;
						break;
					}
				}
			}
		}
		
		for (int i = 0; i < cap_distributions.length; i++) {
			System.out.println("Total number of ways is : " + (cap_distributions[i].length - numOfIllegals[i]));
			for (int j = 0; j < cap_distributions[i].length; j++) {
				if (cap_distributions[i][j]==null)
					continue;
				for (int k = 0; k < cap_distributions[i][j].length; k++) {
					System.out.print(cap_distributions[i][j][k] + "\t");
				}
				System.out.println();
			}
			System.out.println("******************************************************");
		}


		
	}

	private static int[][] caculateCapDistribution(int firstPlayerAmount, int step, int total, int totalInitialPlayers) {
		int[][] capDistribution = new int[10000000][totalInitialPlayers];
		counter = 0;

		int[] array = new int[totalInitialPlayers];
		Split(firstPlayerAmount, step, total, totalInitialPlayers, 0, array, capDistribution);

		int i = 0, unique = 0;
		while (capDistribution[i][totalInitialPlayers - 1] > 0) {
			unique++;
			i++;
		}
		int[][] result = new int[unique][];
		System.arraycopy(capDistribution, 0, result, 0, unique);

		return result;

	}

	private static void Split(int firstPlayerAmount, int step, int total, int totalPlayers, int playerIndex,
			int[] receivedArray, int[][] capDistribution) {

		if (totalPlayers - 1 == 0) {
			receivedArray[playerIndex] = total;

			for (int i = 0; i < receivedArray.length; i++) {
				capDistribution[counter][i] = receivedArray[i];
			}
			counter++;
			return;
		}

		int remainder = 0;
		for (int i = firstPlayerAmount; i <= total - totalPlayers + 1; i += step) {
			receivedArray[playerIndex] = i;
			remainder = total - i;
			Split(step, step, remainder, totalPlayers - 1, playerIndex + 1, receivedArray, capDistribution);
		}
	}
}
