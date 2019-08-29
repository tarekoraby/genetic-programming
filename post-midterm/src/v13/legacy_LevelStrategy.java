package v13;


public class legacy_LevelStrategy {
	public int fitness;
	private int level;	
	private char[] init_strategy;
	private char[] balance_strategy;
	private char[] join_strategy;
	private boolean initLevelStartegy;
	
	public LevelStrategy(int level, boolean initLevelStartegy) {
		this.level = level;
		this.initLevelStartegy = initLevelStartegy;
		calculateLength();
	}
	
	public void setInitLevelStrategy(char[] init_strategy){
		this.init_strategy = init_strategy.clone();
		checkErrors();
	}

	public void setJoinLevelStrategies( char[] balance_strategy,
			char[] join_strategy){
		this.balance_strategy = balance_strategy.clone();
		this.join_strategy = join_strategy.clone();
		checkErrors();
	}
	
	public char[] getInitLevelStrategy(int level){
		if (this.level!=level){
			System.out.println("LevelStrategy class error !!!");
		}
		return init_strategy.clone();
	}
	
	public char[] getBalanceLevelStrategy(int level){
		if (this.level!=level){
			System.out.println("LevelStrategy class error !!!");
		}
		return balance_strategy.clone();
	}
	
	public char[] getJoinLevelStrategy(int level){
		if (this.level!=level){
			System.out.println("LevelStrategy class error !!!");
		}
		return join_strategy.clone();
	}

	public int calculateLength() {
		if (initLevelStartegy)
			return init_strategy.length;
		else 
			return (balance_strategy.length + join_strategy.length);
	}

	private void checkErrors() {
		if (level < 2 || level > MasterVariables.MAXSYSTEM || (initLevelStartegy && init_strategy == null)
				|| (!initLevelStartegy && level == 2) || (!initLevelStartegy && balance_strategy == null)
				|| (!initLevelStartegy && join_strategy == null)) {
			System.out.println("LevelStrategy class erorr!!!!");
			System.exit(0);
		}
	}

	public LevelStrategy deepCopy() {
		LevelStrategy newLevelStrategy =  new LevelStrategy(level, initLevelStartegy);
		
		if (initLevelStartegy){
			newLevelStrategy.setInitLevelStrategy(init_strategy.clone());
		} else {
			newLevelStrategy.setJoinLevelStrategies(balance_strategy.clone(), join_strategy.clone());
		}
		
		newLevelStrategy.fitness = fitness;
		return newLevelStrategy;
	}
}
