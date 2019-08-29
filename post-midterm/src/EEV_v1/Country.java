package EEV_v1;

public class Country {
	Territory territory;
	double resources;

	Country() {
		territory = new Territory();
		territory.setTerritorySize(0);
		resources=0;
	}
}
