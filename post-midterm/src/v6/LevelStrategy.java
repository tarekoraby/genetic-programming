package v6;


public class LevelStrategy {
	int level;
	int fitness;
	char[] init_strategy;
	char[] balance_strategy;
	char[] join_strategy;
	
	public LevelStrategy(int level, char[] init_strategy, char[] balance_strategy,
			char[] join_strategy) {
		this.level = level;
		this.init_strategy = init_strategy;
		this.balance_strategy = balance_strategy;
		this.join_strategy = join_strategy;
		checkErrors();
	}

	public LevelStrategy(int level, char[] init_strategy) {
		this.level = level;
		this.init_strategy = init_strategy;
		checkErrors();
	}
	
	private void checkErrors() {
		if (level < 2 || level > MasterVariables.MAXSYSTEM || init_strategy == null
				|| (balance_strategy == null && level > 2) || (join_strategy == null && level > 2)) {
			System.out.println("LevelStrategy class erorr!!!!");
			System.exit(0);
		}

	}

	public LevelStrategy deepCopy() {
		char[] init_strategy = this.init_strategy.clone();
		LevelStrategy newLevelStrategy;
		if (level == 2)
			newLevelStrategy = new LevelStrategy(level, init_strategy);
		else {
			char[] balance_strategy = this.balance_strategy.clone();
			char[] join_strategy = this.join_strategy.clone();
			newLevelStrategy = new LevelStrategy(level, init_strategy, balance_strategy, join_strategy);
		}
		newLevelStrategy.fitness = fitness;
		return newLevelStrategy;
	}
}
