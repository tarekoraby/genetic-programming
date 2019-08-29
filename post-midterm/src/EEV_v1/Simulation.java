package EEV_v1;

public class Simulation {

	
	Evolver evolver = new Evolver();

	public void start(int currentSimulation) {
		for (int currentLevel = MasterVariables.MINSYSTEM; currentLevel <= MasterVariables.MAXSYSTEM; currentLevel++) {
			simulate(currentSimulation, currentLevel);
		}
	}

	private void simulate(int currentSimulation, int currentLevel) {
		boolean isInit ;
		if (currentLevel > 2) {
			isInit = false;
			System.out.println("\nLevel " + currentLevel + " join gen ");
			for (int currentGen = 1; currentGen <= MasterVariables.genPerLevel[currentLevel-2]; currentGen++) {
				System.out.print(currentGen + " ");
				evolver.evolve(currentLevel, isInit);				
				printStatistics(currentSimulation, currentLevel, currentGen, isInit);
			}
		}

		isInit= true;
		System.out.println("\nLevel " + currentLevel + " init gen ");
		for (int currentGen = 1; currentGen <= MasterVariables.genPerLevel[currentLevel-2]; currentGen++) {
			System.out.print( currentGen + " " );
			evolver.evolve(currentLevel, isInit);	
			printStatistics(currentSimulation, currentLevel, currentGen, isInit);
		}

		
	}

	private void printStatistics( int currentSimulation, int currentLevel, int currentGen,
			boolean isInit) {
		
		StatisticsPrinter sc = new StatisticsPrinter(currentSimulation, currentLevel, currentGen, isInit);
		sc.print();

	}

}
