package EEV_v4;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;


public class DirectedDyadtoDyadPredictor {
	static int[] years;
	static RealResNCode[][] resNCodes;
	static Profile[] profiles;
	static WriteFile predicted_writer;
	static String inputFileNameString = "C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\profilesV1.txt";
	static String realCapFileNameString = "C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\dyadic_inputs_predictor.txt";
	static String outputFileNameString = "C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\output_predictor_dyadic_predictions.txt";
	
	static boolean printToScreen =false;

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
			Profile[] tempProfiles = new Profile[numOfObs];
			
			int numOfTwos=0;

			in = new BufferedReader(new FileReader(
					inputFileNameString));
			for (int i = 0; i < numOfObs; i++) {
				tempProfiles[i] = new Profile();
				line = in.readLine();
				StringTokenizer tokens = new StringTokenizer(line);
				/*tempProfiles[i].world_size = Integer.parseInt(tokens.nextToken().trim());
				tempProfiles[i].attackOrder = Integer.parseInt(tokens.nextToken().trim());
				tempProfiles[i].EEV = Double.parseDouble(tokens.nextToken().trim());
				tempProfiles[i].oppCost = Double.parseDouble(tokens.nextToken().trim());
				tempProfiles[i].oppCostRelNoAttack = Double.parseDouble(tokens.nextToken().trim());
				tempProfiles[i].resources = new double[tempProfiles[i].world_size];
				for (int k = 0; k < tempProfiles[i].world_size; k++) {
					tempProfiles[i].resources[k] = Double.parseDouble(tokens.nextToken().trim());
				}*/
				tempProfiles[i].world_size = Integer.parseInt(tokens.nextToken().trim());
				tempProfiles[i].EEV = Double.parseDouble(tokens.nextToken().trim());
				tempProfiles[i].oppCost = Double.parseDouble(tokens.nextToken().trim());
				tempProfiles[i].oppCostRelNoAttack = Double.parseDouble(tokens.nextToken().trim());
				tempProfiles[i].resourceActor = Double.parseDouble(tokens.nextToken().trim());
				tempProfiles[i].resourceTarget = Double.parseDouble(tokens.nextToken().trim());
				tempProfiles[i].resourcesOthers = new double[tempProfiles[i].world_size - 1 ];
				for (int k = 0; k < tempProfiles[i].world_size - 1; k++) {
					tempProfiles[i].resourcesOthers[k] = Double.parseDouble(tokens.nextToken().trim());
				}
				if (tempProfiles[i].world_size==2)
					numOfTwos++;
			}

			profiles = new Profile[numOfTwos];
			int counter = 0;
			for (int i = 0; i < numOfObs; i++) {
				if (tempProfiles[i].world_size == 2) {
					profiles[counter++] = tempProfiles[i];
					numOfTwos--;
				}
				if (numOfTwos == 0)
					break;
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
			resNCodes= new RealResNCode[numOfObs][];

			in = new BufferedReader(new FileReader(realCapFileNameString));
			for (int i = 0; i < numOfObs; i++) {
				line = in.readLine();
				StringTokenizer tokens = new StringTokenizer(line);
				years[i] = Integer.parseInt(tokens.nextToken().trim());

				resNCodes[i]= new RealResNCode[2];			
				for (int k = 0; k < 2; k++) {
					resNCodes[i][k] = new RealResNCode();
					resNCodes[i][k].ccode = Integer.parseInt(tokens.nextToken().trim());					
				}
				double sum = 0;
				for (int k = 0; k < 2; k++) {
					resNCodes[i][k].resources =  Double.parseDouble(tokens.nextToken().trim());
					sum += resNCodes[i][k].resources;
				}
				
				
				for (int k = 0; k < 2; k++) {
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
		int year;
		double[] results1;
		for (int counter = 0; counter < years.length; counter++) {
			year = years[counter];

			results1 = match(resNCodes[counter], 0, 1, profiles);

			printToFile(year, resNCodes[counter][0].ccode, resNCodes[counter][1].ccode, results1);
		}

	}

	private static double[] match(RealResNCode[] realResNCodes, int actorIndex, int targetIndex, Profile[] profiles) {
		if (actorIndex==targetIndex){
			Thread.dumpStack();
			System.exit(0);
		}
		
		int worldSize = realResNCodes.length;

		/*double[] resources = new double[realResNCodes.length];
		resources[0] = realResNCodes[actorIndex].resources;
		int counter = 1;
		for (int i = 0; i < realResNCodes.length; i++) {
			if (i == actorIndex)
				continue;
			if (i==targetIndex)
				targetIndex= counter;
			resources[counter++] = realResNCodes[i].resources;
		}*/
		
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
			//similarity = calcSimilarity(worldSize, resources, targetIndex, profiles[i]);
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
	
	/*private static double calcSimilarity(int worldSize, double[] resources, int targetIndex, Profile profile) {
		if (worldSize != profile.world_size || targetIndex != profile.attackOrder)
			return -1 * Double.MAX_VALUE;
		double similarity = 0;
		for (int i=0; i<resources.length; i++){
			similarity = similarity - Math.abs(resources[i] - profile.resources[i]);
		}
		
		return similarity;
	}*/

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

	private static void printToFile(int year, int i, int j, double[] results1) {
		if (printToScreen)
			System.out.println(year + " " + i  + " " + j + " " + results1[0]+ " " + results1[1]+ " " + results1[2]);
		predicted_writer.writeNewLineToFile(year + " " + i + " " + j + " " + results1[0]+ " " + results1[1]+ " " + results1[2] + "");

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
