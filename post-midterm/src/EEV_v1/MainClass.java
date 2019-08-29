package EEV_v1;

import java.io.IOException;
import java.util.ArrayList;



public class MainClass {

	static int resourcesStepSize = 5;
	static int territoryStepSize = 20;

	public static void main(String[] args) {
		runSimulation();

	}

	private static void runSimulation() {

		int maxWorldSize = 2;
		
		ArrayList<Result> results = new ArrayList<Result>();

		for (int worldSize = 2; worldSize <= maxWorldSize; worldSize++) {
			System.out.println("\n\n***********************************************"  );
			System.out.println("World Size " + worldSize + "\n\n");
			ArrayList<Act> acts = createActs(worldSize);
			Country[] otherCountries = new Country[worldSize - 1];
			for (int i = 0; i < otherCountries.length; i++)
				otherCountries[i] = new Country();
			Country testedCountry = new Country();
			ArrayList<State> states = createStates(worldSize, otherCountries, testedCountry);
			Outcome[][] outcomes = calcOutcomes(acts, states, otherCountries, testedCountry);

			DistributionGenerator DG = new DistributionGenerator();
			int[][] resourcesDistribution = DG.calcDistribution(worldSize, resourcesStepSize, 100, false);
		//	int[][] territoriesDistribution = DG.calcDistribution(worldSize, territoryStepSize, 100, false);
			for (int resDistCounter = 0; resDistCounter < resourcesDistribution.length; resDistCounter++) {
				//for (int terrDistCounter = 0; terrDistCounter < territoriesDistribution.length; terrDistCounter++) {
					testedCountry.resources = resourcesDistribution[resDistCounter][0];
					testedCountry.territory.setTerritorySize(resourcesDistribution[resDistCounter][0]);
					for (int otherCountryCounter = 0; otherCountryCounter < worldSize - 1; otherCountryCounter++) {
						otherCountries[otherCountryCounter].resources = resourcesDistribution[resDistCounter][otherCountryCounter + 1];
						otherCountries[otherCountryCounter].territory
								.setTerritorySize(resourcesDistribution[resDistCounter][otherCountryCounter + 1]);
					}

					for (int actCounter = 0; actCounter < acts.size(); actCounter++) {
						updateActOpps(acts.get(actCounter), states, otherCountries);
						double EEV = calcEEV(outcomes[actCounter]);
						//printStats( resourcesDistribution[resDistCounter], resourcesDistribution[resDistCounter], acts.get(actCounter), EEV);
						results.add(new Result(worldSize, acts.get(actCounter).attackOrder, EEV, resourcesDistribution[resDistCounter], resourcesDistribution[resDistCounter]));
					}
				//}
			}
			
			printResultsToFile(results, maxWorldSize);
		}

	}


	private static void printResultsToFile(ArrayList<Result> results, int maxWorldSize) {
		WriteFile fileWriter= null;
		try {
			fileWriter = new WriteFile("resultsV1.txt", false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fileWriter.writeToFile("WorldSize" + "," + "attackOrder" + "," + "EEV");
		for (int j = 1; j <= maxWorldSize; j++) {
			fileWriter.writeToFile("," + "res_state_" + j);
		}
		
		for (int j = 1; j <= maxWorldSize; j++) {
			fileWriter.writeToFile("," + "terr_state_" + j);
		}
		fileWriter.writeToFile("\n");
		
		Result currResult;
		int currWorldSize;
		for (int resultCounter=0; resultCounter<results.size(); resultCounter++){
			currResult = results.get(resultCounter);
			currWorldSize = currResult.worldSize;
			fileWriter.writeToFile(currWorldSize + "," + currResult.attackOrder + "," + currResult.EEV );
			for (int j = 1; j <= maxWorldSize; j++) {
				if (j <= currWorldSize)
					fileWriter.writeToFile("," + currResult.resDistr[j - 1]);
				else 
					fileWriter.writeToFile("," + ".");
			}
			
			for (int j = 1; j <= maxWorldSize; j++) {
				if (j <= currWorldSize)
					fileWriter.writeToFile("," + currResult.terrDistr[j - 1]);
				else 
					fileWriter.writeToFile("," + ".");
			}
			fileWriter.writeToFile("\n");
		}
		
		fileWriter.flush();
		
	}

	private static void updateActOpps(Act act, ArrayList<State> states, Country[] otherCountries) {
		if (act.attackOrder > 0) {
			for (int stateCounter = 0; stateCounter < states.size(); stateCounter++) {
				states.get(stateCounter).actOppCountries = new ArrayList<Country>();
				states.get(stateCounter).actOppCountries.add(otherCountries[act.attackOrder - 1]);
			}
		} else {
			for (int stateCounter = 0; stateCounter < states.size(); stateCounter++)
				states.get(stateCounter).actOppCountries = new ArrayList<Country>();
		}
	}

	private static void printStats( int[] resourcesDistribution, int[] territoriesDistribution, Act act, double EEV) {
		System.out.println("Act " + act.attackOrder + " EEV " + EEV);
		System.out.print("Cap distr: ");
		for (int resDistCounter = 0; resDistCounter < resourcesDistribution.length; resDistCounter++){
			System.out.print(resourcesDistribution[resDistCounter] + " ");
		}
		System.out.print("\nTerr distr: ");
		for (int terrDistCounter = 0; terrDistCounter < territoriesDistribution.length; terrDistCounter++) {
			System.out.print(territoriesDistribution[terrDistCounter] + " ");
		}
		System.out.println("\n");
	}

	

	private static double calcEEV(Outcome[] outcomes) {
		double EEV = 0;
		for (int outcomeCounter= 0; outcomeCounter<outcomes.length; outcomeCounter++){
			EEV +=outcomes[outcomeCounter].getOutcomeExpectedValue();
		}
		System.out.println("xxxxxxxxxxxxxx");
		return EEV;
	}

	private static Outcome[][] calcOutcomes(ArrayList<Act> acts, ArrayList<State> states, Country[] otherCountries,
			Country testedCountry) {
		Outcome[][] outcomes = new Outcome[acts.size()][states.size()];
		for (int actCounter = 0; actCounter < outcomes.length; actCounter++) {
			for (int stateCounter = 0; stateCounter < outcomes[actCounter].length; stateCounter++) {
				outcomes[actCounter][stateCounter] = new Outcome(states.get(stateCounter), acts.get(actCounter));
			}
		}
		return outcomes;
	}


	private static ArrayList<State> createStates(int worldSize, Country[] otherCountries, Country testedCountry) {
		ArrayList<State> states = new ArrayList<State>();
		State tempState;
		boolean sideAstronger = false;
		long[] outputStratgies = new long[worldSize - 1];
		double possibleCombinations =  Math.pow(3, worldSize - 1), probOfStrategy;
		for (int i = 0; i < 2; i++) {
			for (int numCounter = 0; numCounter < possibleCombinations; numCounter++) {
				outputStratgies = decToTernary(numCounter, worldSize - 1);
				probOfStrategy = (double) 1 / possibleCombinations;
				if (worldSize ==2){
					if (outputStratgies[0] == 0 || outputStratgies[0] == 1)
						probOfStrategy = 0.25;
					else if (outputStratgies[0] == 2 )
						probOfStrategy = 0.5;
					else {
						Thread.dumpStack();
						System.exit(0);
					}
				}
				//probOfStrategy = probOfStrategy / 2; // since they cover the case when a state is on the stronger side and when it's not
				tempState = new State(sideAstronger, outputStratgies, probOfStrategy, otherCountries, testedCountry);
				states.add(tempState);
			}
			sideAstronger = true;
		}
		states.trimToSize();
		return states;
	}

	private static ArrayList<Act> createActs(int worldSize) {
		ArrayList<Act> acts = new ArrayList<Act>();
		Act tempAct;
		for (int attackOrder = 0; attackOrder < worldSize; attackOrder++) {
			tempAct = new Act(attackOrder);
			acts.add(tempAct);
		}
		acts.trimToSize();
		return acts;
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

}
