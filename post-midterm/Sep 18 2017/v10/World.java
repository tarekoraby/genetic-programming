package v10;

import java.util.Arrays;

public class World {
	
	double capMed, capSD, capMin, capMax, myCap, oppCap, leftCapSum;
	double[] capabilities;
	int[] ccodes;
	int numOfStates;

	public World(double[] capabilities, int[] ccodes) {
		this.capabilities= capabilities;
		this.ccodes=ccodes;
		numOfStates=capabilities.length;
		calculateSystemicVariables();
	}
	
	

	private void calculateSystemicVariables() {

		double capAvg = (double) 1 / numOfStates;

		double capSS = 0;
		for (int i = 0; i < numOfStates; i++)
			capSS += (capabilities[i] - capAvg) * (capabilities[i] - capAvg);
		capSD = Math.sqrt(capSS / numOfStates);

		// calculate median
		double[] temp = capabilities.clone();
		Arrays.sort(temp);
		int middle = numOfStates / 2;
		if (numOfStates % 2 == 1) {
			capMed = temp[middle];
		} else {
			capMed = (temp[middle - 1] + temp[middle]) / 2;
		}

		capMax = temp[numOfStates - 1];
		capMin = temp[0];
	}
}
