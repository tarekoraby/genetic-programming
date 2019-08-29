package EEV_v1;

import java.util.ArrayList;
import java.util.Arrays;

public class State {
	private boolean sideAstronger, warInitiatedAgainstA;
	private long[] strategies;
	private double probOfStrategies;
	private ArrayList<Country> alliedCountries = new ArrayList<Country>();
	private ArrayList<Country> oppCountries = new ArrayList<Country>();
	private ArrayList<Country> neutralCountries = new ArrayList<Country>();
	private ArrayList<Country> attackedAlliedCountries;
	ArrayList<Country> actOppCountries = new ArrayList<Country>();
	private Country[] otherCountries;
	private Country testedCountry;

	public State(boolean sideAstronger, long[] strategies, double probOfStrategies, Country[] otherCountries,
			Country testedCountry) {
		this.sideAstronger = sideAstronger;
		this.strategies = strategies;
		this.otherCountries = otherCountries;
		this.testedCountry = testedCountry;
		this.probOfStrategies = probOfStrategies;
		assignOtherCountries();
	}
	
	void setProbOfStratgies(double probOfStrategies){
		this.probOfStrategies = probOfStrategies;
	}

	private void assignOtherCountries() {
		warInitiatedAgainstA = false;
		for (int countryCounter = 0; countryCounter < otherCountries.length; countryCounter++) {
			if (strategies[countryCounter] == 2) {
				warInitiatedAgainstA = true;
				oppCountries.add(otherCountries[countryCounter]);
			} else if (strategies[countryCounter] == 1) {
				alliedCountries.add(otherCountries[countryCounter]);
			} else if (strategies[countryCounter] == 0) {
				neutralCountries.add(otherCountries[countryCounter]);
			} else {
				Thread.dumpStack();
				System.exit(0);
			}
		}
	}

	double getStateProb() {
		double probOfPower = getProbOfPower();
		//System.out.println("state: " + probOfPower + " " + probOfStrategies + " " + sideAstronger + " " + alliedCountries.size());
		System.out.println("probOfStrat " + probOfStrategies  + " probOfPower " + probOfPower  );
		return (probOfPower * probOfStrategies);
	}

	private double getProbOfPower() {
		/*if (oppCountries.size()==0 && actOppCountries.size()==0)
			return 0.5;*/
		oppCountries=new ArrayList<>(Arrays.asList(otherCountries));
		
		//remove duplicates in actOppCountries & oppCountries
		for (int i = 0; i < actOppCountries.size(); i++) {
			for (int j = 0; j < oppCountries.size(); j++) {
				if (oppCountries.get(j) == actOppCountries.get(i)){
					actOppCountries.remove(i);
					break;
				}
			}
		}
		
		//note allied countries that are attacked
		attackedAlliedCountries  = new ArrayList<Country>();
		for (int i = 0; i < actOppCountries.size(); i++) {
			for (int j = 0; j < alliedCountries.size(); j++) {
				if (alliedCountries.get(j) == actOppCountries.get(i)){
					attackedAlliedCountries.add(alliedCountries.get(j));
				}
			}
		}
		
		double probOfPower, sideAtotalResources = testedCountry.resources, sideBtotalResources = 0;
		for (int i = 0; i < alliedCountries.size(); i++) {
			sideAtotalResources += alliedCountries.get(i).resources;
		}

		for (int i = 0; i < oppCountries.size(); i++) {
			sideBtotalResources += oppCountries.get(i).resources;
		}
		
		for (int i = 0; i < actOppCountries.size(); i++) {
			sideBtotalResources += actOppCountries.get(i).resources;
		}
		
		for (int i = 0; i < attackedAlliedCountries.size(); i++) {
			sideAtotalResources -=attackedAlliedCountries.get(i).resources;
		}

		sideAtotalResources = testedCountry.resources;
		sideBtotalResources = oppCountries.get(0).resources;
		probOfPower = sideAtotalResources / (sideAtotalResources + sideBtotalResources);
		
		//xxx
		double smallerToLargerRatio = Math.min(sideAtotalResources, sideBtotalResources)
				/ Math.max(sideAtotalResources, sideBtotalResources); 
		
		
		
		double probSmallerSideWins = 0.5 * smallerToLargerRatio * smallerToLargerRatio;
		if (sideAtotalResources > sideBtotalResources)
			probOfPower = 1 - probSmallerSideWins;
		else {
			probOfPower = probSmallerSideWins;
		}
			

		if (sideAstronger == false)
			probOfPower = 1 - probOfPower;


		return probOfPower;

	}

	public Country getTestedCountry() {
		return testedCountry;
	}

	public ArrayList<Country> getAlliedCountries() {
		return alliedCountries;
	}

	public ArrayList<Country> getOppCountries() {
		return oppCountries;
	}

	public ArrayList<Country> getNeutralCountries() {
		return neutralCountries;
	}
	
	public boolean warInitiatedAgainstA(){
		return warInitiatedAgainstA;
	}
	
	public boolean sideAstronger(){
		return sideAstronger;
	}
}
