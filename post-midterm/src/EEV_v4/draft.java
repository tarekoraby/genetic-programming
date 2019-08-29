package EEV_v4;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class draft {
	
	static Random rd=new Random();
	
	public static void main(String[] args) {
		
		testExponentialDistr();
		//testGaussianProbabiliities();
		//testWorldDistributions();
		//testThreading();
		//testForLoop();
		//cloneTest();
		//arrayCopy();
		//compareDoubles();
		//worldSizes(2);
		//charTOInt();
		//testTernary(4);
		//testIntToObject();
		//testArrayList();
		//testArrayPass();
		//testProb();
		//shuffleArray();
	}
	
	private static void testExponentialDistr() {
		
		
		int N = 10000000;
		rd = new Random();
		int counter1 = 0, counter2 = 0, counter3 = 0, counter4 = 0;
		double d, sum = 0;
		double capSS = 0;
		double[] numbers= new double[N];

		for (int i = 0; i < N; i++) {
			d =  Math.log(1-rd.nextDouble())/(-4);
			numbers[i]= d;
			sum +=d;
			if (d > 0 & d < 0.5)
				counter1++;
			if (d > 0.5 & d<1)
				counter2++;
			if (d > 1 )
				counter3++;
			if (d > 0 & d<0.25)
				counter4++;
		}
		
		double capAvg = sum / N;
		
		for (int i = 0; i < N; i++)
				capSS += (numbers[i] - capAvg) * (numbers[i] - capAvg);
		double capSD = Math.sqrt(capSS / N);
		
		

		System.out.println((double) counter1/ N + " " + (double) counter2 / N + " " + (double) counter3
				/ N + " " + (double) counter4 / N);
		System.out.println("Mean " +  capAvg + " SD " + capSD);
		
	}

	private static void shuffleArray() {
		int[] x={100, 101, 102};
		int order = 2, myIndex = 0;
		
		for (int f= 0; f<10; f++)
			for (int i = x.length - 1; i > 0; i--) {
				int index = rd.nextInt(i + 1);
				if (i == index)
					continue;
				
				int temp;

				temp = x[index];
				x[index] = x[i];
				x[i] = temp;

				if (i == order) {
					order = index;
				} else if (order == index)
					order = i;

				if (i == myIndex) {
					myIndex = index;
				} else if (myIndex == index)
					myIndex = i;
				
			}
		System.out.println ( " order " + order + " myindex " + myIndex + " " + x[0] + " " + x[1] + " " + x[2]);
		
	}

	private static void testProb() {
		int size=8;
		double total = 10000, war = 0, nowar = 0;
		int temp1, temp2, temp3;
		for (int j = 0; j < total; j++) {
			boolean atWar[]=new boolean[size];
			for (int i = 1; i < size; i++) {
				if (atWar[i])
					continue;
				temp1 = rd.nextInt(size*1);
				if (i == temp1)
					continue;
				if (temp1 == 0) {
					war++;
					break;
				}
				if (temp1 >= size)
					continue;
				atWar[i] = true;
				atWar[temp1] = true;
				continue;
				/*for (int k = 1; k < size; k++) {
					if (atWar[k])
						continue;
					temp2 = rd.nextInt(3);
					if (temp2 == 0) 
						continue;
					atWar[k] = true;
				}*/
				
				/*temp2 = rd.nextInt(4);
				temp3 = rd.nextInt(4);
				if (rd.nextInt(1) == 0) {
					if (temp1 == 0) {
						war++;
						continue;
					} else if (temp1 == 1) {
						if (temp2 == 0) {
							war++;
							continue;
						} else if (temp2 == 1) {
							if (temp3 == 0) {
								war++;
								continue;
							}
						}
					}
				}
				nowar++;*/
			}
		}

		System.out.println(war / total );

	}

	private static void testArrayPass() {
		boolean[] sideA = new boolean[2];
		Pass(sideA);
		System.out.println(sideA[0] + " " + sideA[1]);
		
	}

	private static void Pass(boolean[] sideA) {
		sideA[1]=true;
		
	}

	private static void testArrayList() {
		ArrayList<Outcome> xArrayList=new ArrayList<Outcome>();
		System.out.println(xArrayList.size());
		Outcome xOutcome=new Outcome(null);
		xArrayList.add(xOutcome);
		System.out.println(xArrayList.size());
	}

	private static void testIntToObject() {

		Integer yInteger=1;
		System.out.println(yInteger);
		Integer zInteger = yInteger;
		yInteger=2;
		System.out.println(yInteger + " "  + zInteger);
		
	}

	private static void testTernary(int worldSize) {
		System.out.println("Num combinations " + Math.pow(3, worldSize - 1));
		long[] output = new long[worldSize - 1];
		for (int numCounter = 0; numCounter < Math.pow(3, worldSize - 1); numCounter++) {
			output = decToTernary(numCounter, worldSize - 1);
			System.out.print(numCounter + ": ");
			for (int i = 0; i < output.length; i++)
				System.out.print(output[i] + " ");
			System.out.println();
		}
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

	private static void charTOInt() {
		char x;
		int y=rd.nextInt(2);
		x = (char) y;
		System.out.print((int)x + " " + y);
		
	}



	private static void worldSizes(int worldSize) {
		int[][] x = createRelation(worldSize);
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[i].length; j++) {
				System.out.print(x[i][j]);
			}
			System.out.println();
		}
	}

	private static int[][] createRelation(int worldSize) {
		int[][] GTRelations = new int[Integer.MAX_VALUE][worldSize - 2];
		int countWorlds = 0;
		return GTRelations;
	}

	private static void compareDoubles() {
		rd = new Random();
		for (int k=0; k<100; k++){
		double f= 0.05;
		boolean[] x = new boolean[200000000];
		double[] a = new double[200000000];
		//double[] b = new double[100000000];
		int counter=0, z=0;
		for (int i = 0; i < 200000000; i++) 
			a[i] = rd.nextDouble();
			//b[i] = rd.nextDouble();
		while(counter<200000000 - 5) {
			if (a[counter++] < a[counter++]) {
				x[z++] = true;
			}
		}
		
			for (int i = 0; i < 200000000; i++) {
				if (a[i] < f & x[i] == false){
					System.out.print("error");
					System.exit(0);
				}
			}
			System.out.println("here");
		}
	}

	private static void arrayCopy() {
		// TODO Auto-generated method stub
		char[] c=new char[3];
		c[0]='A';
		c[1]='B';
		c[2]='C';
		char[] d = Arrays.copyOfRange(c, 2, 3);
		System.out.print(c[2] + " " + d[0]);
	}

	private static void cloneTest() {
		char[] c=new char[3];
		c[0]='A';
		c[1]='B';
		c[2]='C';
		char[] d=c.clone();
		d[0]='X';
		System.out.print(c[0] + " " + d[0]);
		
	}

	private static void testForLoop() {
		for (int i=1; i<1; i++)
			System.out.print("here");
	}

	private static void testThreading() {
		int processors = Runtime.getRuntime().availableProcessors();
		final ExecutorService executor;
		executor = Executors.newFixedThreadPool(processors);// creating a
		// pool
		// of 5 threads
		for (int k = 0; k < 2; k++) {
			Future<Void>[] tasks = new Future[10];
			
			String[] strings = new String[10];
			for (int i = 0; i < 10; i++) {
				Callable<Void> draft2_test = new draft2("hello");
				tasks[i] = executor.submit(draft2_test);
			}
			for (int i = 0; i < 10; i++) {
				try {
					tasks[i].get();
					System.out.println("got string " + i);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("I am here!!!!!!!!!!!!");
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}

        System.out.println("Finished all threads");  
		
	}

	private static void testWorldDistributions() {
		rd = new Random();
		double counter1 = 0, counter2 = 0, counter3 = 0, counter4 = 0;
		double[] capabilities = new double[4];
		for (int i = 0; i < 100000; i++) {
			double remainder = 1;

			capabilities[0] = remainder * rd.nextDouble();			
			remainder -= capabilities[0];

			capabilities[1] = remainder * rd.nextDouble();
			remainder -= capabilities[1];
			
			capabilities[2] = remainder * rd.nextDouble();
			remainder -= capabilities[2];

			capabilities[3] = remainder;
			
			shuffleArray(capabilities);
			counter1 += capabilities[0];
			counter2 += capabilities[1];
			counter3 += capabilities[2];
			counter4 += capabilities[3];
		}
		
		
		System.out.println((double)counter1/100000 + " " + (double)counter2/100000 + " " + (double)counter3/100000 + " " + (double)counter4/100000);
		
	}
	
	static void shuffleArray(double[] ar)
	  {
	    // If running on Java 6 or older, use `new Random()` on RHS here
		rd = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rd.nextInt(i + 1);
	      // Simple swap
	      double a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }

	private static void testGaussianProbabiliities() {
		int N = 10000000;
		rd = new Random();
		int counter1 = 0, counter2 = 0, counter3 = 0, counter4 = 0;
		double d, sum = 0;
		double capSS = 0;
		double[] numbers= new double[N];

		for (int i = 0; i < N; i++) {
			d =  Math.abs(rd.nextGaussian()* 0.315);
			numbers[i]= d;
			sum +=d;
			if (d > 0 & d < 0.5)
				counter1++;
			if (d > 0.5 & d<1)
				counter2++;
			if (d > 1 )
				counter3++;
			if (d > 0 & d<0.25)
				counter4++;
		}
		
		double capAvg = sum / N;
		
		for (int i = 0; i < N; i++)
				capSS += (numbers[i] - capAvg) * (numbers[i] - capAvg);
		double capSD = Math.sqrt(capSS / N);
		
		

		System.out.println((double) counter1/ N + " " + (double) counter2 / N + " " + (double) counter3
				/ N + " " + (double) counter4 / N);
		System.out.println("Mean " +  capAvg + " SD " + capSD);

	}

}
