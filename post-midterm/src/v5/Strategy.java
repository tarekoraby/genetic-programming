package v5;


public class Strategy {

	int maxSize;
	int fitness;
	char[][] init_stratgies;
	char[][] balance_stratgies;
	char[][] bandwagon_stratgies;
	boolean tested;

	public Strategy(int maxSize) {
		this.maxSize = maxSize;
		fitness = 0;
		int levels = maxSize - MasterVariables.MINSYSTEM + 1;
		init_stratgies = new char[levels][];
		balance_stratgies = new char[levels - 1][];
		bandwagon_stratgies = new char[levels - 1][];
		tested = false;
	}

	//This method retrieves the strategy of the designated type and world-size
	//Strategy types can be 0, 1, or 2 for war initiation, balancing, and bandwagoning respectively
	private char[] getStrategy(int type, int worldSize) {
		if ((type != 0 && worldSize == 2) || (worldSize < MasterVariables.MINSYSTEM)
				|| worldSize > MasterVariables.MAXSYSTEM || type < 0 || type > 2) {
			System.out.println("\n\nError !!! Strategy class error ");
			System.out.println("getStrategy method 1");
			System.out.println(type + " " + worldSize);
			System.exit(0);
		}

		if (type == 0)
			return (init_stratgies[worldSize - MasterVariables.MINSYSTEM]);
		if (type == 1)
			return (balance_stratgies[worldSize - MasterVariables.MINSYSTEM - 1]);
		if (type == 2)
			return (bandwagon_stratgies[worldSize - MasterVariables.MINSYSTEM - 1]);

		System.out.println("\n\nError !!! Strategy class error ");
		System.out.println("getStrategy method 2");
		System.exit(0);
		return null;
	}

	char[] getInitStrategy (int worldSize){
		return (getStrategy(0, worldSize));
	}
	
	char[] getBalanceStrategy (int worldSize){
		return (getStrategy(1, worldSize));
	}
	
	char[] getBandwagonStrategy (int worldSize){
		return (getStrategy(2, worldSize));
	}
	
	void setInitStrategy (int worldSize, char[] strategy){
		setStrategy(0, worldSize, strategy);
	}
	
	void setBalanceStrategy (int worldSize, char[] strategy){
		setStrategy(1, worldSize, strategy);
	}
	
	void setBandwagonStrategy (int worldSize, char[] strategy){
		setStrategy(2, worldSize, strategy);
	}
	
	void setStrategy(int type, int worldSize, char[] strategy) {
		if ((type != 0 && worldSize == 2) || (worldSize < MasterVariables.MINSYSTEM)
				|| worldSize > MasterVariables.MAXSYSTEM || type < 0 || type > 2) {
			System.out.println("\n\nError !!! Strategy class error ");
			System.out.println("setStrategy method ");
			System.exit(0);
		}

		if (type == 0)
			init_stratgies[worldSize - MasterVariables.MINSYSTEM] = strategy;
		if (type == 1)
			balance_stratgies[worldSize - MasterVariables.MINSYSTEM - 1] = strategy;
		if (type == 2)
			bandwagon_stratgies[worldSize - MasterVariables.MINSYSTEM - 1] = strategy;
	}

	LevelStrategy getLevelStrategy(int worldSize) {
		if ((worldSize < MasterVariables.MINSYSTEM) || worldSize > MasterVariables.MAXSYSTEM) {
			System.out.println("\n\nError !!! Strategy class error ");
			System.out.println("getLevelStrategy method ");
			System.exit(0);
		}

		LevelStrategy levelStrategy;
		if (worldSize == 2)
			levelStrategy = new LevelStrategy(worldSize, getStrategy(0, worldSize));
		else
			levelStrategy = new LevelStrategy(worldSize, getStrategy(0, worldSize), getStrategy(1, worldSize),
					getStrategy(2, worldSize));

		return levelStrategy;
	}

	Strategy deepCopy() {
		Strategy newStrategy = new Strategy(maxSize);
		newStrategy.setInitStrategy(2, getInitStrategy(2).clone());
		for (int i = 3; i <= maxSize; i++) {
			newStrategy.setInitStrategy(i, getInitStrategy(i).clone());
			newStrategy.setBalanceStrategy(i, getBalanceStrategy(i).clone());
			newStrategy.setBandwagonStrategy(i, getBandwagonStrategy(i).clone());
		}
		newStrategy.fitness = fitness;
		return newStrategy;
	}

	public boolean checkCompleteness() {
		boolean complete = true;
		for (int i = 0; i <= maxSize - 2; i++) {
			if (init_stratgies[i] == null) {
				complete = false;
				return complete;
			}
		}
		
		if (maxSize>2)
			for (int i = 0; i <= maxSize - 3; i++) {
				if (balance_stratgies[i] == null || bandwagon_stratgies[i]==null) {
					complete = false;
					return complete;
				}
			}
		return complete;
	}

	public void increaseFitness(int i) {
		fitness+=i;
		
	}
}
