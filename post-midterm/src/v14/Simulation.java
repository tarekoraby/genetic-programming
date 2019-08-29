package v14;

public class Simulation {

	public void start() {
		for (int currentLevel = MasterVariables.MINSYSTEM; currentLevel <= MasterVariables.MAXSYSTEM; currentLevel++) {
			simulate(currentLevel);
		}
	}

	private void simulate(int currentLevel) {
		Strategy[] currentPopulation = createInitialPopulation(currentLevel);
		for (int currentGen = 1; currentGen <= MasterVariables.genPerLevel[currentLevel - 2]; currentGen++) {
			System.out.println("Lev " + currentLevel + " gen " + currentGen);
			currentPopulation = Evolver.evolve(currentPopulation, currentLevel);
			printStatistics(currentPopulation, currentLevel, currentGen);
		}
		MasterVariables.masterPopulations[currentLevel - 2] = currentPopulation;
	}

	private void printStatistics(Strategy[] currentPopulation, int currentLevel, int currentGen) {
		StatisticsPrinter sc = new StatisticsPrinter(currentPopulation, MasterVariables.currentSimulation, currentGen,
				currentLevel);
		sc.print();
	}

	private Strategy[] createInitialPopulation(int currentLevel) {
		Strategy[] newStrategies = new Strategy[MasterVariables.popSizes[currentLevel - 2]];
		StrategyCreator sCreator = new StrategyCreator();
		char[][] initStrategies = new char[MasterVariables.popSizes[currentLevel - 2]][];
		for (int counter = 0; counter < initStrategies.length; counter++)
			initStrategies[counter] = sCreator.create_random_indiv(MasterVariables.DEPTH,
					MasterVariables.SIMULATECONSTRUCTIVISM);
		if (currentLevel == 2) {
			for (int counter = 0; counter < initStrategies.length; counter++)
				newStrategies[counter] = new Strategy(currentLevel, initStrategies[counter]);
		} else {
			for (int counter = 0; counter < initStrategies.length; counter++) {
				char[] joinStrategy = sCreator.create_random_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
				char[] balanceStrategy = sCreator.create_random_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
				newStrategies[counter] = new Strategy(currentLevel, initStrategies[counter], joinStrategy,
						balanceStrategy);
			}
		}
		return newStrategies;
	}

}
