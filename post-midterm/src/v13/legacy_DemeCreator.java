package v13;


import java.util.concurrent.Callable;

public class legacy_DemeCreator implements Callable<Deme> {
	int level, newPopulationSize;
	boolean initDeme, joinDeme;

	public DemeCreator(int level, int newPopulationSize, boolean initDeme, boolean joinDeme) {
		this.level = level;
		this.newPopulationSize = newPopulationSize;
		this.initDeme=initDeme;
		this.joinDeme=joinDeme;
		checkErrors();
	}

	private void checkErrors() {
		if (level < MasterVariables.MINSYSTEM || level > MasterVariables.MAXSYSTEM || (!initDeme && !joinDeme)) {
			System.out.println("DemeCreator class error!!");
			System.exit(0);
		}
	}

	public Deme call() {
		StrategyCreator creator = new StrategyCreator();
		return creator.createDeme(level, initDeme, newPopulationSize, MasterVariables.DEPTH,
				MasterVariables.SIMULATECONSTRUCTIVISM);
	}

}
