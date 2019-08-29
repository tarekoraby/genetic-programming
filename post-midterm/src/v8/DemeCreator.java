package v8;


import java.util.concurrent.Callable;

public class DemeCreator implements Callable<Deme> {
	int level, newPopulationSize;

	public DemeCreator(int level, int newPopulationSize) {
		this.level = level;
		this.newPopulationSize = newPopulationSize;
	}

	public Deme call() {
		StrategyCreator creator = new StrategyCreator();
		return creator.createDeme(level, newPopulationSize, MasterVariables.DEPTH,
				MasterVariables.SIMULATECONSTRUCTIVISM);
	}

}
