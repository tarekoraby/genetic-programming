package EEV_v1;

public class StrategyCollection {
	private Strategy[][] initStrategies, joinStrategies;
	private int maxWorldSize;

	StrategyCollection(int maxWorldSize) {
		this.maxWorldSize = maxWorldSize;
		int currentLevel;

		initStrategies = new Strategy[maxWorldSize - 1][];
		for (int i = 0; i < initStrategies.length; i++) {
			currentLevel = i + 2;
			initStrategies[i] = new Strategy[currentLevel];
		}

		if (maxWorldSize > 2) {
			joinStrategies = new Strategy[maxWorldSize - 1][];
			// starts from 1 because bipolarity has no join stratgies
			for (int i = 1; i < joinStrategies.length; i++) {
				currentLevel = i + 2;
				joinStrategies[i] = new Strategy[currentLevel];
			}
		}
	}

	void populateWithRandomStrategies() {
		int currentLevel;
		for (int i = 0; i < initStrategies.length; i++) {
			currentLevel = i + 2;
			for (int currentOrder = 1; currentOrder <= currentLevel; currentOrder++)
				initStrategies[i][currentOrder - 1] = MasterVariables.bestStratgies.getRandomInitStratgy(currentLevel,
						currentOrder);
		}

		if (maxWorldSize > 2) {
			// starts from 1 because bipolarity has no join stratgies
			for (int i = 1; i < joinStrategies.length; i++) {
				currentLevel = i + 2;
				for (int currentOrder = 1; currentOrder <= currentLevel; currentOrder++)
					joinStrategies[i][currentOrder - 1] = MasterVariables.bestStratgies.getRandomJoinStratgy(
							currentLevel, currentOrder);
			}
		}

	}

	Strategy getInitStrategy(int worldSize, int order) {
		Strategy initCopy = initStrategies[worldSize - 2][order - 1].deepCopy();
		return initCopy;
	}

	Strategy getJoinStrategy(int worldSize, int order) {
		Strategy joinCopy = joinStrategies[worldSize - 2][order - 1].deepCopy();
		return joinCopy;
	}

	void setToNull(int worldSize, int order, boolean isInit) {
		if (isInit)
			initStrategies[worldSize - 2][order - 1] = null;
		else
			joinStrategies[worldSize - 2][order - 1] = null;
	}

	void setToNull(int worldSize, boolean isInit) {
		if (isInit)
			for (int order = 0; order < worldSize; order++)
				initStrategies[worldSize - 2][order] = null;
		else
			for (int order = 0; order < worldSize; order++)
				joinStrategies[worldSize - 2][order] = null;
	}
}
