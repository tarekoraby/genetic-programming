package EEV_v4;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import java.util.StringTokenizer;


public class SystemtoDyadPredictor {
	static int[] years;
	static int[] numOfStates;
	//static double[][] realCapabilities;
	//static int[][] ccodes;
	static RealResNCode[][] resNCodes;
	static Profile[] profiles;
	static WriteFile predicted_writer;
	static String inputFileNameString = "C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\profilesV1.txt";
	static String realCapFileNameString = "C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\systemic_inputs_predictor.txt";
	static String outputFileNameString = "C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\output_predictor_dyadic_predictions.txt";

	public static void main(String[] args) {
		readRealCapData();
		readProfilesCount();
		try {
			predicted_writer = new WriteFile(outputFileNameString, false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		predictConflict();
		predicted_writer.flush();
	}

	private static void readProfilesCount() {

		String line;
		BufferedReader in;
		try {
			int numOfObs = countLines(inputFileNameString);
			profiles = new Profile[numOfObs];

			in = new BufferedReader(new FileReader(
					inputFileNameString));
			for (int i = 0; i < numOfObs; i++) {
				profiles[i] = new Profile();
				line = in.readLine();
				StringTokenizer tokens = new StringTokenizer(line);
				profiles[i].world_size = Integer.parseInt(tokens.nextToken().trim());
				profiles[i].EEV = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].oppCost = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].oppCostRelNoAttack = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].resourceActor = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].resourceTarget = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].resourcesOthers = new double[profiles[i].world_size - 1 ];
				for (int k = 0; k < profiles[i].world_size - 1; k++) {
					profiles[i].resourcesOthers[k] = Double.parseDouble(tokens.nextToken().trim());
				}
			}

			in.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private static void readRealCapData() {

		String line;
		BufferedReader in;
		try {
			int numOfObs = countLines(realCapFileNameString);
			System.out.println("Num of obs " + numOfObs);
			//System.out.println("Years: " );
			years = new int[numOfObs];
			numOfStates = new int[numOfObs];
			resNCodes= new RealResNCode[numOfObs][];

			in = new BufferedReader(new FileReader(realCapFileNameString));
			for (int i = 0; i < numOfObs; i++) {
				line = in.readLine();
				StringTokenizer tokens = new StringTokenizer(line);
				years[i] = Integer.parseInt(tokens.nextToken().trim());
				// System.out.println(years[i]);
				numOfStates[i] = Integer.parseInt(tokens.nextToken().trim());
				resNCodes[i] = new RealResNCode[numOfStates[i]];

				double sum = 0;
				for (int k = 0; k < numOfStates[i]; k++) {
					resNCodes[i][k] = new RealResNCode();
					resNCodes[i][k].resources = Double.parseDouble(tokens.nextToken().trim());
					sum += resNCodes[i][k].resources;
				}

				for (int k = 0; k < numOfStates[i]; k++) {
					resNCodes[i][k].ccode = Integer.parseInt(tokens.nextToken().trim());
				}

				for (int k = 0; k < numOfStates[i]; k++) {
					resNCodes[i][k].resources = resNCodes[i][k].resources * 100 / sum;
				}
			}

			in.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private static void predictConflict() {
		/*
		 * static int[] years; static int[] numOfStates; static double[][]
		 * realCapabilities; static int[][] ccodes; static Profile[] profiles;
		 */
		int year, numOfStates;
		double  total_attack_pred;
		double[] results1, results2;
		for (int counter = 0; counter < years.length; counter++) {
			year = years[counter];
			numOfStates = SystemtoDyadPredictor.numOfStates[counter];
			resNCodes[counter] = sortOnResources(resNCodes[counter]);
			
			if (numOfStates != resNCodes[counter].length){
				Thread.dumpStack();
				System.exit(0);
			}
			
			for (int firstState = 0; firstState < numOfStates - 1; firstState++) {
				for (int secondState = firstState + 1; secondState < numOfStates; secondState++) {
					results1 = match(resNCodes[counter], firstState, secondState, profiles);
					results2 = match(resNCodes[counter], secondState, firstState, profiles);
					
					
					/*World world = new World(capabilities, ccodes);
					world.myCap = capabilities[firstState];
					world.oppCap = capabilities[secondState];
					world.leftCapSum = 1 - world.myCap - world.oppCap;
					if (Math.abs(world.leftCapSum) < 1E-10)
						world.leftCapSum = 0;
					oppCost1 = match(world, profiles);
					world.myCap = capabilities[secondState];
					world.oppCap = capabilities[firstState];
					attackPerc2 = match(world, profiles);*/
					//total_attack_pred = (oppCost1 + oppCost2) / 2;
					printToFile(year, resNCodes[counter][firstState].ccode, resNCodes[counter][secondState].ccode, results1, results2);
				}
			}
		}

	}

	private static double[] match(RealResNCode[] realResNCodes, int actorIndex, int targetIndex, Profile[] profiles) {
		if (checkIsSorted(realResNCodes) == false || actorIndex==targetIndex){
			Thread.dumpStack();
			System.exit(0);
		}
		
		int worldSize = realResNCodes.length;

		double realResourcesActor = realResNCodes[actorIndex].resources;
		double realResourcesTarget = realResNCodes[targetIndex].resources;
		double[] resources = new double[realResNCodes.length - 1];
		int counter = 0;
		for (int i = 0; i < realResNCodes.length; i++) {
			if (i == actorIndex)
				continue;
			if (i==targetIndex)
				targetIndex= counter;
			resources[counter++] = realResNCodes[i].resources;
		}
		
		int mostSimilarProfileIndex = -1;
		double similarity, highestSimilarity = -1 * Double.MAX_VALUE;
		for (int i = 0; i < profiles.length; i++) {
			similarity = calcSimilarity(worldSize, realResourcesActor, realResourcesTarget, resources, targetIndex, profiles[i]);
			if (similarity > highestSimilarity) {
				highestSimilarity = similarity;
				mostSimilarProfileIndex = i;
			}
		}
		
		if (highestSimilarity == -1 * Double.MAX_VALUE){
			Thread.dumpStack();
			System.exit(0);
		}
		
		/*System.out.println("*********************");
		System.out.print(realResourcesActor + " " + realResourcesTarget + " ");
		for (int i = 0; i < resources.length; i++) {
			System.out.print(resources[i] + " ");
		}
		System.out.println();
		
		System.out.print(profiles[mostSimilarProfileIndex].resourceActor + " " + profiles[mostSimilarProfileIndex].resourceTarget + " ");
		for (int i = 0; i < profiles[mostSimilarProfileIndex].resourcesOthers.length; i++) {
			System.out.print(profiles[mostSimilarProfileIndex].resourcesOthers[i] + " ");
		}
		System.out.println();*/
		
		double[] result = new double[3];
		result[0] = profiles[mostSimilarProfileIndex].EEV;
		result[1] = profiles[mostSimilarProfileIndex].oppCost;
		result[2] = profiles[mostSimilarProfileIndex].oppCostRelNoAttack;
		
		return result;
	}

	private static double calcSimilarity(int worldSize, double resourceActor, double resourceTarget,
			double[] resources, int targetIndex, Profile profile) {
		if (worldSize != profile.world_size )
			return -1 * Double.MAX_VALUE;
		double similarity = 0;
		similarity = similarity - Math.abs(resourceActor - profile.resourceActor);
		similarity = similarity - Math.abs(resourceTarget - profile.resourceTarget);
		
		for (int i=0; i<resources.length; i++){
			if (i==targetIndex)
				continue;
			similarity = similarity - Math.abs(resources[i] - profile.resourcesOthers[i]);
		}
		
		return similarity;
	}

	/*private static double match(World world, Profile[] profiles) {
		double lowest_diff_sum = Double.MAX_VALUE, diff_sum;
		int bestMatchIndex = -99;
		for (int i = 0; i < profiles.length; i++) {
			if (world.numOfStates != profiles[i].world_size)
				continue;
			diff_sum = 0;
			diff_sum += Math.abs(world.myCap - profiles[i].myCap);
			diff_sum += Math.abs(world.oppCap - profiles[i].oppCap);
			diff_sum += Math.abs(world.capMax - profiles[i].capMax);
			diff_sum += Math.abs(world.capMed - profiles[i].capMed);
			diff_sum += Math.abs(world.capMin - profiles[i].capMin);
			diff_sum += Math.abs(world.capSD - profiles[i].capSD);
			diff_sum += Math.abs(world.leftCapSum - profiles[i].leftCapSum);
			if (diff_sum < lowest_diff_sum) {
				lowest_diff_sum = diff_sum;
				bestMatchIndex = i;
			}
		}
		System.out.println("*******************************************************");
		System.out.println(world.myCap + " " + world.oppCap + " " + world.capMax + " " + world.capMed + " " + 
		world.capMin + " "+ world.capSD + " " + world.leftCapSum );
		System.out.println(profiles[bestMatchIndex].myCap + " " + profiles[bestMatchIndex].oppCap + " " + profiles[bestMatchIndex].capMax + " " + profiles[bestMatchIndex].capMed + " " + 
				profiles[bestMatchIndex].capMin + " "+ profiles[bestMatchIndex].capSD + " " + profiles[bestMatchIndex].leftCapSum );
		
		return profiles[bestMatchIndex].attack_perc;
	}*/

	private static RealResNCode[] sortOnResources(RealResNCode[] realResNCodes) {
		RealResNCode[] newRealResNCodes = new RealResNCode[realResNCodes.length];
		boolean[] taken = new boolean[realResNCodes.length];
		for (int i=0; i<realResNCodes.length; i++){
			newRealResNCodes[i] = findSmallestNonTaken(realResNCodes, taken);
		}
		return newRealResNCodes;
	}

	private static RealResNCode findSmallestNonTaken(RealResNCode[] realResNCodes, boolean[] taken) {
		double smallestResNonTaken = Double.MAX_VALUE;
		int smallestIndex = -99;
		for (int k = 0; k < realResNCodes.length; k++) {
			if (!taken[k] && realResNCodes[k].resources < smallestResNonTaken) {
				smallestResNonTaken = realResNCodes[k].resources;
				smallestIndex = k;
			}
		}
		taken[smallestIndex] = true;
		return realResNCodes[smallestIndex];
	}

	private static boolean checkIsSorted(RealResNCode[] realResNCodes) {
		double latest = realResNCodes[0].resources;
		for (int i = 1; i < realResNCodes.length; i++) {
			if (realResNCodes[i].resources < latest)
				return false;
			latest = realResNCodes[i].resources;
		}
		return true;
	}

	private static void printToFile(int year, int i, int j, double[] results1, double[] results2) {
		System.out.println(year + " " + i  + " " + j + " " + results1[0]+ " " + results2[0]+ " " + results1[1]+ " " + results2[1]+ " " + results1[2]+ " " + results2[2]);
		predicted_writer.writeNewLineToFile(year + " " + i + " " + j + " " + results1[0]+ " " + results1[1]+ " " + results1[2] + "");
		predicted_writer.writeNewLineToFile(year + " " + j + " " + i + " " + results2[0]+ " " + results2[1]+ " " + results2[2] + "");

	}

	public static int countLines(String filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n') {
						++count;
					}
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}
}
