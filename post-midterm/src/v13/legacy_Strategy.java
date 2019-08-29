package v13;

public class legacy_Strategy {

	int maxSize;
	int fitness, bestStrategyFitness, bestJoinFit, bestInitFit;
	private char[][] init_stratgies;
	private char[][] join_stratgies;
	private char[][] balance_stratgies;
	private boolean isInitStrategy;
	boolean tested;

	public Strategy(int maxSize, boolean isInitStrategy) {
		this.maxSize = maxSize;
		this.isInitStrategy = isInitStrategy;
		fitness = 0;
		int levels = maxSize - MasterVariables.MINSYSTEM + 1;
		init_stratgies = new char[levels][];
		balance_stratgies = new char[levels - 1][];
		join_stratgies = new char[levels - 1][];
		bestStrategyFitness = -99;
		tested = false;
	}

	// This method retrieves the strategy of the designated type and world-size
	// Strategy types can be 0, 1, or 2 for war initiation, balancing, and
	// joining respectively
	private char[] getStrategy(int type, int worldSize) {
		if ((type != 0 && worldSize == 2) || (worldSize < MasterVariables.MINSYSTEM)
				|| worldSize > MasterVariables.MAXSYSTEM || type < 0 || type > 2) {
			System.out.println("\n\nError !!! Strategy class error ");
			System.out.println("getStrategy method 1");
			System.out.println(type + " " + worldSize);
			System.exit(0);
		}

		if (type == 0)
			return (init_stratgies[worldSize - MasterVariables.MINSYSTEM].clone());
		if (type == 1)
			return (balance_stratgies[worldSize - MasterVariables.MINSYSTEM - 1].clone());
		if (type == 2)
			return (join_stratgies[worldSize - MasterVariables.MINSYSTEM - 1].clone());

		System.out.println("\n\nError !!! Strategy class error ");
		System.out.println("getStrategy method 2");
		System.exit(0);
		return null;
	}

	char[] getInitStrategy(int worldSize) {
		return (getStrategy(0, worldSize));
	}

	char[] getBalanceStrategy(int worldSize) {
		return (getStrategy(1, worldSize));
	}

	char[] getJoinStrategy(int worldSize) {
		return (getStrategy(2, worldSize));
	}

	void setInitStrategy(int worldSize, char[] strategy) {
		setStrategy(0, worldSize, strategy);
	}

	void setBalanceStrategy(int worldSize, char[] strategy) {
		setStrategy(1, worldSize, strategy);
	}

	void setJoinStrategy(int worldSize, char[] strategy) {
		setStrategy(2, worldSize, strategy);
	}

	private void setStrategy(int type, int worldSize, char[] strategy) {
		if ((type != 0 && worldSize == 2) || (worldSize < MasterVariables.MINSYSTEM)
				|| worldSize > MasterVariables.MAXSYSTEM || type < 0 || type > 2) {
			System.out.println("\n\nError !!! Strategy class error ");
			System.out.println("setStrategy method ");
			System.exit(0);
		}

		if (type == 0)
			init_stratgies[worldSize - MasterVariables.MINSYSTEM] = strategy.clone();
		if (type == 1)
			balance_stratgies[worldSize - MasterVariables.MINSYSTEM - 1] = strategy.clone();
		if (type == 2)
			join_stratgies[worldSize - MasterVariables.MINSYSTEM - 1] = strategy.clone();
	}

	Strategy deepCopy() {
		Strategy newStrategy = new Strategy(maxSize, isInitStrategy);
		newStrategy.setInitStrategy(2, getInitStrategy(2).clone());
		for (int i = 3; i <= maxSize; i++) {
			newStrategy.setInitStrategy(i, getInitStrategy(i).clone());
			newStrategy.setBalanceStrategy(i, getBalanceStrategy(i).clone());
			newStrategy.setJoinStrategy(i, getJoinStrategy(i).clone());
		}
		newStrategy.fitness = fitness;
		newStrategy.bestStrategyFitness = bestStrategyFitness;
		return newStrategy;
	}

	public boolean checkCompleteness() {
		boolean complete = true;
		if (isInitStrategy) {
			for (int i = 0; i <= maxSize - 2; i++) {
				if (init_stratgies[i] == null) {
					complete = false;
					return complete;
				}
			}
			if (maxSize > 2)
				for (int i = 0; i <= maxSize - 3; i++) {
					if (balance_stratgies[i] == null || join_stratgies[i] == null) {
						complete = false;
						return complete;
					}
				}
		} else {
			for (int i = 0; i <= maxSize - 3; i++) {
				if (init_stratgies[i] == null) {
					complete = false;
					return complete;
				}
			}

			for (int i = 0; i <= maxSize - 3; i++) {
				if (balance_stratgies[i] == null || join_stratgies[i] == null) {
					complete = false;
					return complete;
				}
			}
		}

		return complete;
	}

	public void addSubStrategy(Strategy subStrategy) {
		if ((subStrategy.maxSize == maxSize && !isInitStrategy) || (subStrategy.maxSize > maxSize)) {
			System.out.println("Error strategy class !!! addSubStrategy method");
		}

		setInitStrategy(2, subStrategy.getInitStrategy(2));
		if (subStrategy.maxSize > 2) {
			for (int i = 3; i < subStrategy.maxSize; i++) {
				setInitStrategy(i, subStrategy.getInitStrategy(i));
				setJoinStrategy(i, subStrategy.getJoinStrategy(i));
				setBalanceStrategy(i, subStrategy.getBalanceStrategy(i));
			}
			
			xxxxxxxxxx
			
		}

	}

	public void increaseFitness(int i) {
		fitness += i;
	}
}
