package v19;

public class Simulation {

	public void start() {
		for (int currentLevel = MasterVariables.MINSYSTEM; currentLevel <= MasterVariables.MAXSYSTEM; currentLevel++) {
			simulate();
		}
	}

	private void simulate() {
		for (int currentGen = 1; currentGen <= 1000; currentGen++) {
			Strategy[][] currentAttackPopulation2 = new Strategy[2][];
			currentAttackPopulation2[0] = createInitialPopulation(2, true);
			currentAttackPopulation2[1] = createInitialPopulation(2, true);
			Strategy[][] currentAttackPopulation3_1 = new Strategy[3][];
			currentAttackPopulation3_1[0] = createInitialPopulation(3, true);
			currentAttackPopulation3_1[1] = createInitialPopulation(3, true);
			currentAttackPopulation3_1[2] = createInitialPopulation(3, true);
			Strategy[][] currentAttackPopulation3_2 = new Strategy[3][];
			currentAttackPopulation3_2[0] = createInitialPopulation(3, true);
			currentAttackPopulation3_2[1] = createInitialPopulation(3, true);
			currentAttackPopulation3_2[2] = createInitialPopulation(3, true);
			Strategy[][] currentJoinPopulation3_1 = new Strategy[3][];
			currentJoinPopulation3_1[0] = createInitialPopulation(3, false);
			currentJoinPopulation3_1[1] = createInitialPopulation(3, false);
			currentJoinPopulation3_1[2] = createInitialPopulation(3, false);
			Strategy[][] currentJoinPopulation3_2 = new Strategy[3][];
			currentJoinPopulation3_2[0] = createInitialPopulation(3, false);
			currentJoinPopulation3_2[1] = createInitialPopulation(3, false);
			currentJoinPopulation3_2[2] = createInitialPopulation(3, false);

			System.out.println("\nGen " + currentGen);
			Evolver.evolve(currentAttackPopulation2, currentAttackPopulation3_1, currentAttackPopulation3_2,
					currentJoinPopulation3_1, currentJoinPopulation3_2);
			printStatistics(currentAttackPopulation2, currentAttackPopulation3_1, currentAttackPopulation3_2,
					currentJoinPopulation3_1, currentJoinPopulation3_2, 3, currentGen);
		}
		System.exit(0);
	}

	private void printStatistics(Strategy[][] popAttack2, Strategy[][] popAttack3_1, Strategy[][] popAttack3_2,
			Strategy[][] popJoin3_1, Strategy[][] popJoin3_2, int currentLevel, int currentGen) {
		StatisticsPrinter sc = new StatisticsPrinter(popAttack2, popAttack3_1, popAttack3_2, popJoin3_1, popJoin3_2,
				MasterVariables.currentSimulation, currentGen, 3);
		sc.print();
	}

	Strategy[] createInitialPopulation(int currentLevel, boolean isInit) {
		Strategy[] newStrategies = new Strategy[MasterVariables.popSizes[currentLevel - 2]];
		StrategyCreator sCreator = new StrategyCreator();
		if (isInit) {
			for (int counter = 0; counter < newStrategies.length; counter++) {
				newStrategies[counter] = new Strategy(currentLevel, isInit);
				char[][] initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, 2);
				newStrategies[counter].setInitStrategy(2, initStrategy);
				for (int currLevCounter = currentLevel; currLevCounter >= 3; currLevCounter--) {
					initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
							MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);
					newStrategies[counter].setInitStrategy(currLevCounter, initStrategy);
					char[][] joinStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
							MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);

					newStrategies[counter].setJoinStrategy(currLevCounter, joinStrategy);
				}
				if (newStrategies[counter].checkCompleteness() == false) {
					System.out.println("Error! createInitialPopulation method");
					System.exit(0);
				}
			}
		} else {
			for (int counter = 0; counter < newStrategies.length; counter++) {
				newStrategies[counter] = new Strategy(currentLevel, isInit);
				char[][] initStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, 2);
				newStrategies[counter].setInitStrategy(2, initStrategy);
				for (int currLevCounter = currentLevel; currLevCounter >= 3; currLevCounter--) {
					char[][] joinStrategy = sCreator.create_random_init_indiv(MasterVariables.DEPTH,
							MasterVariables.SIMULATECONSTRUCTIVISM, currLevCounter);

					newStrategies[counter].setJoinStrategy(currLevCounter, joinStrategy);
				}
				if (newStrategies[counter].checkCompleteness() == false) {
					System.out.println("Error! createInitialPopulation method");
					System.exit(0);
				}
			}
		}

		return newStrategies;	
	}
}
