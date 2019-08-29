package EEV_v1;

import java.util.ArrayList;

public class Outcome {

	boolean atWar, isDead;
	ArrayList<Territory> potentialTerritorySpoils = new ArrayList<Territory>();
	ArrayList<Country> alliedCountries;
	ArrayList<Country> oppCountries;
	ArrayList<Country> neutralCountries;
	Country testedCountry;
	State associatedState;
	Act associatedAct;
	
	public Outcome(State associatedState, Act associatedAct) {
		this.testedCountry = associatedState.getTestedCountry();
		this.alliedCountries = associatedState.getAlliedCountries();
		this.oppCountries = associatedState.getOppCountries();
		this.neutralCountries = associatedState.getNeutralCountries();
		this.associatedState = associatedState;
		this.associatedAct = associatedAct;
		completeConfiguration();
	}

	private void completeConfiguration() {
		if (associatedAct.attackOrder == 0 && associatedState.warInitiatedAgainstA() == false) {
			atWar = false;
			isDead = false;
			return;
		}

		atWar = true;

		if (associatedState.sideAstronger() == false) {
			isDead = true;
			return;
		}

		for (int countryCounter = 0; countryCounter < oppCountries.size(); countryCounter++) {
			potentialTerritorySpoils.add(oppCountries.get(countryCounter).territory);
		}

	}

	double getOutcomeExpectedValue() {
		double outcomeValue = getOutcomeValue();
		//if (outcomeValue == 0)
		//	return 0;
		double stateProb = associatedState.getStateProb();
		System.out.println(outcomeValue + " " + stateProb + " " + atWar + " " + outcomeValue * stateProb);
		return (outcomeValue * stateProb);
	}

	private double getOutcomeValue() {
		if (isDead)
			return 0;

		double testedCountryTerritorySize = testedCountry.territory.getTerritorySize();
		if (atWar == false)
			return testedCountryTerritorySize;
		/*double testedCountryTerritorySize = testedCountry.territory.getTerritorySize();
		if (isDead)
			return (-1 * testedCountryTerritorySize);
		if (atWar == false)
			return 0;*/

		double totalTerrSpoils = 0;
		for (int i = 0; i < potentialTerritorySpoils.size(); i++)
			totalTerrSpoils += potentialTerritorySpoils.get(i).getTerritorySize();

		double myShare;
		if (alliedCountries.size() == 0) {
			myShare = 1;
		} else {
			myShare = calcMyShare();
		}

		return ((myShare * totalTerrSpoils) + testedCountryTerritorySize);
		//return (myShare * totalTerrSpoils);

	}

	private double calcMyShare() {
		double myResources = testedCountry.resources;
		double totalResources = myResources;

		for (int i = 0; i < alliedCountries.size(); i++)
			totalResources += alliedCountries.get(i).resources;
		return (myResources / totalResources);
	}
}
