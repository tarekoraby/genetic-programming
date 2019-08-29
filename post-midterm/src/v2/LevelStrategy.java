package v2;

public class LevelStrategy {
	int level;
	double fitness;
	char[] init_strategy;
	char[] balance_strategy;
	char[] bandwagon_strategy;
	
	public LevelStrategy(int level, char[] init_strategy, char[] balance_strategy,
			char[] bandwagon_strategy) {
		this.level = level;
		this.init_strategy = init_strategy;
		this.balance_strategy = balance_strategy;
		this.bandwagon_strategy = bandwagon_strategy;
		checkErrors();
	}

	public LevelStrategy(int level, char[] init_strategy) {
		this.level = level;
		this.init_strategy = init_strategy;
		checkErrors();
	}
	
	private void checkErrors() {
		if (level < 2 || level > MasterVariables.MAXSYSTEM || init_strategy == null
				|| (balance_strategy == null && level > 2) || (bandwagon_strategy == null && level > 2)) {
			System.out.println("LevelStrategy class erorr!!!!");
			System.exit(0);
		}

	}
}
