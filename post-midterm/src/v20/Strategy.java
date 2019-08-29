package v20;

public class Strategy {

	private int worldSize;
	private int fitness;
	private char[][] joinStrategy;
	private char[][] initStrategy;
	private boolean isInit;
	int attacks,  balances, bandwagons, buckpasses;

	public Strategy(int worldSize, boolean isInit) {
		this.worldSize = worldSize;
		this.isInit = isInit;
	}

	char[][] getInitStrategy(int worldSize) {
		if (worldSize != this.worldSize || !isInit) {
			System.out.println("Error!! Strategy class!! Method No 3");
			Thread.dumpStack();
			System.exit(0);
		}
		char[][] initClone = new char[initStrategy.length][];
		for (int i = 0; i < initStrategy.length; i++)
			initClone[i] = initStrategy[i].clone();
		return (initClone);
	}

	void setInitStrategy(int worldSize, char[][] initStrategy) {
		if (worldSize != this.worldSize || initStrategy.length != worldSize - 1 || initStrategy == null || !isInit) {
			System.out.println("Error!! Strategy class!! Method No 4");
			Thread.dumpStack();
			System.exit(0);
		}
		this.initStrategy = new char[initStrategy.length][];
		for (int i = 0; i < initStrategy.length; i++)
			this.initStrategy[i] = initStrategy[i].clone();
	}

	char[][] getJoinStrategy(int worldSize) {
		if (worldSize != this.worldSize || worldSize < 3 || isInit) {
			System.out.println("Error!! Strategy class!! Method No 5");
			Thread.dumpStack();
			System.exit(0);
		}

		char[][] joinClone = new char[joinStrategy.length][];
		for (int i = 0; i < joinStrategy.length; i++)
			joinClone[i] = joinStrategy[i].clone();
		return (joinClone);
	}

	void setJoinStrategy(int worldSize, char[][] joinStrategy) {
		if (worldSize != this.worldSize || joinStrategy == null || isInit) {
			System.out.println("Error!! Strategy class!! Method No 7");
			Thread.dumpStack();
			System.exit(0);
		}

		this.joinStrategy = new char[joinStrategy.length][];
		for (int i = 0; i < joinStrategy.length; i++)
			this.joinStrategy[i] = joinStrategy[i].clone();
	}

	Strategy deepCopy() {
		Strategy newInitStrategy = new Strategy(worldSize, isInit);
		if (isInit)
			newInitStrategy.setInitStrategy(worldSize, initStrategy);
		else
			newInitStrategy.setJoinStrategy(worldSize, joinStrategy);

		// newInitStrategy.fitness = fitness;
		return newInitStrategy;
	}

	int getFitness() {
		return fitness;
	}

	void resetFitness() {
		fitness = 0;
	}

	void increaseFitnessBy(int increase) {

		fitness += increase;
	}

	private void checkErrors() {
		if (worldSize < MasterVariables.MINSYSTEM || worldSize > MasterVariables.MAXSYSTEM) {
			System.out.println("Error!! Strategy class!! Method No ");
			Thread.dumpStack();
			System.exit(0);
		}
	}
}
