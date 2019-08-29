package v13;

public class legacy_JoinStrategy {

	private int worldSize;
	private int fitness;
	private char[] joinStrategy, balanceStrategy;
	boolean tested;

	public JoinStrategy(int worldSize) {
		this.worldSize = worldSize;
		checkErrors();
	}

	public JoinStrategy(int worldSize, char[] joinStrategy, char[] balanceStrategy) {
		this.worldSize = worldSize;
		this.joinStrategy = joinStrategy.clone();
		this.balanceStrategy = balanceStrategy.clone();
		checkErrors();
	}

	char[] getJoinStrategy(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! JoinStrategy class!!");
			System.exit(0);
		}
		return (joinStrategy.clone());
	}

	char[] getBalanceStrategy(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! JoinStrategy class!!");
			System.exit(0);
		}
		return (balanceStrategy.clone());
	}

	void setJoinStrategy(int worldSize, char[] joinStrategy) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! JoinStrategy class!!");
			System.exit(0);
		}
		this.joinStrategy = joinStrategy.clone();
	}

	void setBalanceStrategy(int worldSize, char[] balanceStrategy) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! JoinStrategy class!!");
			System.exit(0);
		}
		this.balanceStrategy = balanceStrategy.clone();
	}

	JoinStrategy deepCopy() {
		JoinStrategy newJoinStrategy = new JoinStrategy(worldSize, joinStrategy, balanceStrategy);
		newJoinStrategy.fitness = fitness;
		return newJoinStrategy;
	}

	int getFitness(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! JoinStrategy class!!");
			System.exit(0);
		}
		return fitness;
	}

	void resetFitness(int worldSize) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! JoinStrategy class!!");
			System.exit(0);
		}
		fitness = 0;
	}

	void increaseFitnessBy(int worldSize, int increase) {
		if (worldSize != this.worldSize) {
			System.out.println("Error!! JoinStrategy class!!");
			System.exit(0);
		}
		fitness += increase;
	}

	private void checkErrors() {
		if (worldSize < MasterVariables.MINSYSTEM || worldSize > MasterVariables.MAXSYSTEM) {
			System.out.println("Error!! JoinStrategy class!!");
			System.exit(0);
		}
	}
}
