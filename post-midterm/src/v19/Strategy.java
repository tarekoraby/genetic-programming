package v19;



public class Strategy {

	private int maxSize;
	private int fitness;
	private char[][][]  joinStrategy;
	private char[][][] initStrategy;
	private boolean isInit;

	public Strategy(int maxSize, boolean isInit) {
		this.maxSize = maxSize;
		this.isInit = isInit;
		if (isInit){
			initStrategy = new char[maxSize-1][][];
			joinStrategy = new char[maxSize-2][][];
		} else{
			initStrategy = new char[maxSize-2][][];
			joinStrategy = new char[maxSize-2][][];
		}
	}
	
	char[][] getInitStrategy(int worldSize) {
		if (worldSize > this.maxSize) {
			System.out.println("Error!! Strategy class!! Method No 3");
			System.exit(0);
		}
		char[][] initClone = new char[initStrategy[worldSize - 2].length][];
		for (int i = 0; i < initStrategy[worldSize - 2].length; i++)
			initClone[i] = initStrategy[worldSize - 2][i].clone();
		return (initClone);
	}

	void setInitStrategy(int worldSize, char[][] initStrategy) {
		if (worldSize > this.maxSize || initStrategy.length != worldSize - 1 || initStrategy==null) {
			System.out.println("Error!! Strategy class!! Method No 4");
			System.exit(0);
		}
		this.initStrategy[worldSize - 2] = new char[initStrategy.length][];
		for (int i = 0; i < initStrategy.length; i++)
			this.initStrategy[worldSize - 2][i] = initStrategy[i].clone();
	}

	char[][] getJoinStrategy(int worldSize) {
		if (worldSize > this.maxSize) {
			System.out.println("Error!! Strategy class!! Method No 5");
			System.exit(0);
		}

		char[][] joinClone = new char[joinStrategy[worldSize - 3].length][];
		for (int i = 0; i < joinStrategy[worldSize - 3].length; i++)
			joinClone[i] = joinStrategy[worldSize - 3][i].clone();
		return (joinClone);
	}

	void setJoinStrategy(int worldSize, char[][] joinStrategy) {
		if (worldSize > this.maxSize || joinStrategy==null ) {
			System.out.println("Error!! Strategy class!! Method No 7");
			System.exit(0);
		}

		this.joinStrategy[worldSize - 3] = new char[joinStrategy.length][];
		for (int i = 0; i < joinStrategy.length; i++)
			this.joinStrategy[worldSize - 3][i] = joinStrategy[i].clone();
	}

	

	Strategy deepCopy() {
		Strategy newInitStrategy = new Strategy(maxSize, isInit);
		for (int i = 0; i < initStrategy.length; i++) {
			newInitStrategy.setInitStrategy(i + 2, initStrategy[i]);
		}
		for (int i = 0; i < joinStrategy.length; i++) {
			newInitStrategy.setJoinStrategy(i + 3, joinStrategy[i]);
		}
		

		newInitStrategy.fitness = fitness;
		return newInitStrategy;
	}
	
	boolean checkCompleteness() {
		if (initStrategy.length == 0)
			return false;
		if (isInit == false) {
			if (joinStrategy.length == 0)
				return false;			
		}
		for (int i = 0; i < initStrategy.length; i++) {
			if (initStrategy[i] == null)
				return false;
		}
		for (int i = 0; i < joinStrategy.length; i++) {
			if (joinStrategy[i] == null)
				return false;
		}
		return true;
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
		if (maxSize < MasterVariables.MINSYSTEM || maxSize > MasterVariables.MAXSYSTEM) {
			System.out.println("Error!! Strategy class!! Method No ");
			System.exit(0);
		}
	}
}
