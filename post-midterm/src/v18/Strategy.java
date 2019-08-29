package v18;



public class Strategy {

	private int worldSize;
	private int fitness;
	private char[]  joinStrategy, balanceStrategy;
	private char[][] initStrategy;
	boolean tested;
	private boolean isInit;


	public Strategy(int worldSize, boolean isInit, char[][] initStrategy) {
		this.worldSize = worldSize;
		if (isInit == false || initStrategy.length != worldSize - 1) {
			System.out.println("Error!! Strategy class!! Method No 1");
			System.exit(0);
		}
		this.isInit = isInit;
		this.initStrategy = new char[initStrategy.length][];
		for (int i = 0; i < initStrategy.length; i++)
			this.initStrategy[i] = initStrategy[i].clone();
		checkErrors();
	}

	public Strategy(int worldSize, boolean isInit, char[] joinStrategy, char[] balanceStrategy) {
		this.worldSize = worldSize;
		if (isInit == true) {
			System.out.println("Error!! Strategy class!! Method No 2");
			System.exit(0);
		}
		this.isInit = isInit;
		this.joinStrategy = joinStrategy.clone();
		this.balanceStrategy = balanceStrategy.clone();
		checkErrors();
	}

	char[][] getInitStrategy(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 3");
			System.exit(0);
		}
		char[][] initClone = new char[initStrategy.length][];
		for (int i = 0; i < initStrategy.length; i++)
			initClone[i] = initStrategy[i].clone();
		return (initClone);
	}

	void setInitStrategy(int worldSize, char[][] initStrategy) {
		if (worldSize != this.worldSize || initStrategy.length != worldSize - 1 || initStrategy==null || isInit == false) {
			System.out.println("Error!! Strategy class!! Method No 4");
			System.exit(0);
		}
		this.initStrategy = new char[initStrategy.length][];
		for (int i = 0; i < initStrategy.length; i++)
			this.initStrategy[i] = initStrategy[i].clone();
	}

	char[] getJoinStrategy(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 5");
			System.exit(0);
		}
		return (joinStrategy.clone());
	}

	char[] getBalanceStrategy(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 6");
			System.exit(0);
		}
		return (balanceStrategy.clone());
	}

	void setJoinStrategy(int worldSize, char[] joinStrategy) {
		if (worldSize != this.worldSize || joinStrategy==null || isInit == true) {
			System.out.println("Error!! Strategy class!! Method No 7");
			System.exit(0);
		}
		this.joinStrategy = joinStrategy.clone();
	}

	void setBalanceStrategy(int worldSize, char[] balanceStrategy) {
		if (worldSize != this.worldSize || balanceStrategy==null || isInit == true) {
			System.out.println("Error!! Strategy class!! Method No 8");
			System.exit(0);
		}
		this.balanceStrategy = balanceStrategy.clone();
	}

	Strategy deepCopy() {
		Strategy newInitStrategy;
		if (isInit)
			newInitStrategy = new Strategy(worldSize, isInit, initStrategy);
		else
			newInitStrategy = new Strategy(worldSize, isInit, joinStrategy, balanceStrategy);

		newInitStrategy.fitness = fitness;
		return newInitStrategy;
	}

	int getFitness(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 9 " + worldSize);
			System.exit(0);
		}
		return fitness;
	}

	void resetFitness(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 10");
			System.exit(0);
		}
		fitness = 0;
	}

	void increaseFitnessBy(int worldSize, int increase) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 11");
			System.exit(0);
		}
		fitness += increase;
	}

	private void checkErrors() {
		if (worldSize < MasterVariables.MINSYSTEM || worldSize > MasterVariables.MAXSYSTEM) {
			System.out.println("Error!! Strategy class!! Method No ");
			System.exit(0);
		}
	}
}
