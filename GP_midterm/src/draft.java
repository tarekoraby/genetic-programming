import java.util.Random;
import java.util.Spliterator;


public class draft {
	static int numberOfWays, numberOfGreaterWays, step, counter, totalInitialPlayers, remainder;
	static int[] array;

	public static void main(String[] args) {
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
		int total = 120;
		numberOfWays = 0;
		numberOfGreaterWays = 0;
		step = 3;
		counter = 0;
		totalInitialPlayers = 3;
		array = new int[totalInitialPlayers];
		
		if (total < totalInitialPlayers){
			System.out.print("ERROR!");
			System.exit(0);
		}
		
		
		Split(step, total, totalInitialPlayers, 0, array);

		System.out.println("Total number of ways is : " + numberOfWays);
		System.out.println("Total number of Greater ways is : " + numberOfGreaterWays);
	}

	private static void Split(int firstPlayerAmount, int total, int totalPlayers, int playerIndex, int[] receivedArray) {
		// TODO Auto-generated method stub

		if (totalPlayers == 1) {
			receivedArray[playerIndex] = total;
			numberOfWays++;
			boolean valid = true;
			for (int i = 0; i < array.length; i++)
				System.out.print(array[i] + " ");
			System.out.println();
			for (int i = 1; i < array.length; i++){
				if(array[i]>=array[i-1]){
					valid=false;
					break;
				}
			}
			if (valid){
				numberOfGreaterWays++;
			}
			return;
		}
		
		for (int i = firstPlayerAmount; i <= total - totalPlayers + 1; i += step) {
				receivedArray[playerIndex]=firstPlayerAmount;
				remainder = total - firstPlayerAmount;
				Split(firstPlayerAmount, remainder, totalPlayers - 1, playerIndex + 1, receivedArray);
		}
	}
}
