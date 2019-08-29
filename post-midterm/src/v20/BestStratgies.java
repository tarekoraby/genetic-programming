package v20;

import java.util.Random;

public class BestStratgies {
	private Strategy[][][] bestInitStrategies, bestJoinStrategies;
	private StrategyCreator sCreator = new StrategyCreator();
	private int maxWorldSize;
	private Random rd = new Random();

	BestStratgies(int maxWorldSize) {
		this.maxWorldSize = maxWorldSize;
		boolean isInit;
		int currentLevel;

		isInit = true;
		bestInitStrategies = new Strategy[maxWorldSize - 1][][];
		for (int i = 0; i < bestInitStrategies.length; i++) {
			currentLevel = i + 2;
			bestInitStrategies[i] = new Strategy[currentLevel][];
			for (int j = 0; j < bestInitStrategies[i].length; j++)
				bestInitStrategies[i][j] = sCreator.populationCreator(currentLevel, isInit,
						MasterVariables.popSizes[currentLevel - 2]);
		}

		if (maxWorldSize > 2) {
			isInit = false;
			bestJoinStrategies = new Strategy[maxWorldSize - 1][][];
			// starts from 1 because bipolarity has no join stratgies
			for (int i = 1; i < bestJoinStrategies.length; i++) {
				currentLevel = i + 2;
				bestJoinStrategies[i] = new Strategy[currentLevel][];
				for (int j = 0; j < bestJoinStrategies[i].length; j++)
					bestJoinStrategies[i][j] = sCreator.populationCreator(currentLevel, isInit,
							MasterVariables.popSizes[currentLevel - 2]);
			}
		}

	}

	Strategy[] getInitStratgies(int worldSize, int order) {
		Strategy[] initStrategiesCopy = new Strategy[bestInitStrategies[worldSize - 2][order - 1].length];
		for (int i = 0; i < initStrategiesCopy.length; i++)
			initStrategiesCopy[i] = bestInitStrategies[worldSize - 2][order - 1][i].deepCopy();
		return initStrategiesCopy;
	}

	Strategy[] getJoinStratgies(int worldSize, int order) {
		Strategy[] joinStrategiesCopy = new Strategy[bestJoinStrategies[worldSize - 2][order - 1].length];
		for (int i = 0; i < joinStrategiesCopy.length; i++)
			joinStrategiesCopy[i] = bestJoinStrategies[worldSize - 2][order - 1][i].deepCopy();
		return joinStrategiesCopy;
	}

	void setInitStratgies(int worldSize, int order, Strategy[] initStrategies) {
		if (initStrategies.length != MasterVariables.popSizes[worldSize - 2]) {
			System.out.println("Error! BestStratgies class - method 1");
			System.exit(0);
		}
		for (int i = 0; i < initStrategies.length; i++)
			bestInitStrategies[worldSize - 2][order - 1][i] = initStrategies[i].deepCopy();
	}

	void setJoinStratgies(int worldSize, int order, Strategy[] joinStrategies) {
		if (joinStrategies.length != MasterVariables.popSizes[worldSize - 2]) {
			System.out.println("Error! BestStratgies class - method 2");
			System.exit(0);
		}
		for (int i = 0; i < joinStrategies.length; i++)
			bestJoinStrategies[worldSize - 2][order - 1][i] = joinStrategies[i].deepCopy();
	}

	Strategy getRandomInitStratgy(int worldSize, int order) {
		int randIndex = rd.nextInt(bestInitStrategies[worldSize - 2][order - 1].length);
		Strategy randomInitCopy = bestInitStrategies[worldSize - 2][order - 1][randIndex].deepCopy();
		return randomInitCopy;
	}

	Strategy getRandomJoinStratgy(int worldSize, int order) {
		int randIndex = rd.nextInt(bestJoinStrategies[worldSize - 2][order - 1].length);
		Strategy randomJoinCopy = bestJoinStrategies[worldSize - 2][order - 1][randIndex].deepCopy();
		return randomJoinCopy;
	}
}
