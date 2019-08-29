package v13;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

import javax.swing.text.StyledEditorKit.ForegroundAction;

public class Predictor_Tripolar {
	static int[] years;
	static double[][] realCapabilities;
	static int[][] ccodes;
	static Profile[] profiles;
	static WriteFile predicted_writer;

	public static void main(String[] args) {
		readRealCapData();
		readProfilesCount();
		try {
			predicted_writer = new WriteFile("Tripolar_dyadic_predictions.txt", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		normalizeRealCap();
		predictConflict();
	}

	private static void normalizeRealCap() {
		for (int i = 0; i < realCapabilities.length; i++) {
			double total = 0;
			for (int k = 0; k < 3; k++) {
				total += realCapabilities[i][k];
			}

			double sum = 0;
			for (int k = 0; k < 3 - 1; k++) {
				realCapabilities[i][k] = realCapabilities[i][k] / total;
				sum += realCapabilities[i][k];
			}
			
			realCapabilities[i][2]= 1 - sum;
		}
	}

	private static void readProfilesCount() {

		String line;
		BufferedReader in;
		try {
			int numOfObs = countLines("C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\att_profiles_count.txt");
			profiles = new Profile[numOfObs];

			in = new BufferedReader(new FileReader(
					"C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\att_profiles_count.txt"));
			for (int i = 0; i < numOfObs; i++) {
				profiles[i] = new Profile();
				line = in.readLine();
				StringTokenizer tokens = new StringTokenizer(line);
				profiles[i].world_size = Integer.parseInt(tokens.nextToken().trim());
				profiles[i].capMed = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].capSD = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].capMin = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].capMax = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].myCap = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].oppCap = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].leftCapSum = Double.parseDouble(tokens.nextToken().trim());
				profiles[i].attack_perc = Double.parseDouble(tokens.nextToken().trim());
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
				int numOfObs = countLines("C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\triSystems.txt");
				years = new int[numOfObs];
				realCapabilities = new double[numOfObs][3];
				ccodes = new int[numOfObs][3];

				in = new BufferedReader(new FileReader(
						"C:\\Users\\taor9299\\Box Sync\\Java workspace\\post-midterm\\triSystems.txt"));
				for (int i = 0; i < numOfObs; i++) {
					line = in.readLine();
					StringTokenizer tokens = new StringTokenizer(line);
					years[i] = Integer.parseInt(tokens.nextToken().trim());
					for (int k = 0; k < 3; k++) {
						ccodes[i][k] =  Integer.parseInt(tokens.nextToken().trim());
					}
					for (int k = 0; k < 3; k++) {
						realCapabilities[i][k] =  Double.parseDouble(tokens.nextToken().trim());
					}
					
					
					// System.out.print(years[i] + " " + numOfStates[i]+ " "); for
					 // (int k = 0; k < numOfStates[i]; k++) {
					 // System.out.print(realCapabilities[i][k] + " "); } for (int k
					 // = 0; k < numOfStates[i]; k++) { System.out.print(ccodes[i][k]
					 // + " "); } System.out.println();
					 //
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
		double[] capabilities;
		int[] ccodes;
		double attackPerc1, attackPerc2, total_attack_pred;
		for (int counter = 0; counter < years.length; counter++) {
			year = years[counter];
			capabilities = realCapabilities[counter];
			ccodes = Predictor_Tripolar.ccodes[counter];
			World world = new World(capabilities, ccodes);
			for (int firstState = 0; firstState < 3 - 1; firstState++) {
				for (int secondState = firstState + 1; secondState < 3; secondState++) {
					world.myCap = capabilities[firstState];
					world.oppCap = capabilities[secondState];
					world.leftCapSum = 1 - world.myCap - world.oppCap;
					if (Math.abs(world.leftCapSum) < 1E-10)
						world.leftCapSum = 0;
					attackPerc1 = match(world, profiles);
					world.myCap = capabilities[secondState];
					world.oppCap = capabilities[firstState];
					attackPerc2 = match(world, profiles);
					total_attack_pred = (attackPerc1 + attackPerc2) / 2;
					printToFile(year, ccodes[firstState], ccodes[secondState], total_attack_pred);
				}
			}
		}

	}

	private static void printToFile(int year, int i, int j, double total_attack_pred) {
		//System.out.println(year + " " + i  + " " + j + " " + total_attack_pred);
		predicted_writer.writeNewLineToFile(year + " " + i + " " + j + " " + total_attack_pred + "");

	}

	private static double match(World world, Profile[] profiles) {
		double lowest_diff_sum = Double.MAX_VALUE, diff_sum;
		int bestMatchIndex = -99;
		for (int i = 0; i < profiles.length; i++) {
			if (world.numOfStates != profiles[i].world_size)
				continue;
			diff_sum = 0;
			diff_sum += Math.abs(world.myCap - profiles[i].myCap);
			diff_sum += Math.abs(world.oppCap - profiles[i].oppCap);
			//diff_sum += Math.abs(world.capMax - profiles[i].capMax);
			//diff_sum += Math.abs(world.capMed - profiles[i].capMed);
			//diff_sum += Math.abs(world.capMin - profiles[i].capMin);
			//diff_sum += Math.abs(world.capSD - profiles[i].capSD);
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
