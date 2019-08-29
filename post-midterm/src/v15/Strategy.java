package v15;

public class Strategy {

	private int worldSize;
	private int fitness;
	private char[] initStrategy, joinStrategy, balanceStrategy;

	public Strategy(int worldSize, char[] initStrategy) {
		this.worldSize = worldSize;
		if (worldSize != 2) {
			System.out.println("Error!! Strategy class!! Method No 1");
			System.exit(0);
		}
		this.initStrategy = initStrategy.clone();
		checkErrors();
	}

	public Strategy(int worldSize, char[] initStrategy , char[] joinStrategy, char[] balanceStrategy) {
		this.worldSize = worldSize;
		if (worldSize < 3) {
			System.out.println("Error!! Strategy class!! Method No 2");
			System.exit(0);
		}
		this.initStrategy = initStrategy.clone();
		this.joinStrategy = joinStrategy.clone();
		this.balanceStrategy = balanceStrategy.clone();
		checkErrors();
	}

	char[] getInitStrategy(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 3");
			System.exit(0);
		}
		return (initStrategy.clone());
	}

	void setInitStrategy(int worldSize, char[] initStrategy) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 4");
			System.exit(0);
		}
		this.initStrategy = initStrategy.clone();
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
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 7");
			System.exit(0);
		}
		this.joinStrategy = joinStrategy.clone();
	}

	void setBalanceStrategy(int worldSize, char[] balanceStrategy) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! Strategy class!! Method No 8");
			System.exit(0);
		}
		this.balanceStrategy = balanceStrategy.clone();
	}

	Strategy deepCopy() {
		Strategy newInitStrategy;
		if (worldSize==2)
			newInitStrategy = new Strategy(worldSize, initStrategy);
		else
			newInitStrategy = new Strategy(worldSize, initStrategy, joinStrategy, balanceStrategy);

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
			System.out.println("Error!! Strategy class!! Method No 12");
			System.exit(0);
		}
	}
}
