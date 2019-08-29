package v15;

public class Simulation {

	public void start() {
		for (int currentLevel = MasterVariables.MINSYSTEM; currentLevel <= MasterVariables.MAXSYSTEM; currentLevel++) {
			simulate();
		}
	}

	private void simulate() {
		Strategy[][] currentPopulation2 = new Strategy[2][];
		currentPopulation2[0] = createInitialPopulation(2);
		currentPopulation2[1] = createInitialPopulation(2);
		Strategy[][] currentPopulation3_1 = new Strategy[3][];
		currentPopulation3_1[0] = createInitialPopulation(3);
		currentPopulation3_1[1] = createInitialPopulation(3);
		currentPopulation3_1[2] = createInitialPopulation(3);
		Strategy[][] currentPopulation3_2 = new Strategy[3][];
		currentPopulation3_2[0] = createInitialPopulation(3);
		currentPopulation3_2[1] = createInitialPopulation(3);
		currentPopulation3_2[2] = createInitialPopulation(3);
		for (int currentGen = 1; currentGen <= 2000; currentGen++) {
			System.out.println("Gen " + currentGen);
			Evolver.evolve(currentPopulation2, currentPopulation3_1, currentPopulation3_2);
			printStatistics(currentPopulation2, currentPopulation3_1, currentPopulation3_2, 3, currentGen);
		}
		System.exit(0);
	}

	private void printStatistics(Strategy[][] pop2, Strategy[][] pop3_1, Strategy[][] pop3_2, int currentLevel, int currentGen) {
		StatisticsPrinter sc = new StatisticsPrinter(pop2, pop3_1, pop3_2, MasterVariables.currentSimulation, currentGen,
				3);
		sc.print();
	}

	Strategy[] createInitialPopulation(int currentLevel) {
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
