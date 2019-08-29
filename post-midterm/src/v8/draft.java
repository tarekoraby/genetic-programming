package v8;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class draft {
	
	static Random rd;
	
	public static void main(String[] args) {
		
		//testGaussianProbabiliities();
		//testWorldDistributions();
		//testThreading();
		//testForLoop();
		//cloneTest();
		arrayCopy();
		
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
		rd=new Random();
		int counter1=0, counter2=0, counter3=0;
		double d;
	
		for (int i=0; i<100000; i++){
			d=1 + Math.abs(rd.nextGaussian() * 0.3);
			if (d<1.2)
				counter1++;
			else if (d<1.4)
				counter2++;
			else if (d<1.6)
				counter3++;
		}
		
		System.out.println((double)counter1/100000 + " " + (double)counter2/100000 + " " + (double)counter3/100000 );
		
	}

}
