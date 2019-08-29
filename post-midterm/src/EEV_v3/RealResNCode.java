package EEV_v3;

public class RealResNCode {
	double resources, population;
	int ccode;

	RealResNCode deepCopy() {
		RealResNCode newRealResNCode = new RealResNCode();
		newRealResNCode.resources = resources;
		newRealResNCode.population = population;
		newRealResNCode.ccode = ccode;
		return newRealResNCode;
	}

}
