package v12;

import java.util.concurrent.Callable;

public class FitnessCalculator implements Callable<Void> {
	Strategy[] testedStrategies, bestStrategies;
	int[][] TestCasesCapabilities;
	double[][] TestCasesRandNums, TestCasesRandGauss;
	int[][][] TestCasesCapChangeRates;
	int[][] bestStrategiesOrder;
	int[] testedStratgyIndexes;
	int testedSize, testCases;
	

	public FitnessCalculator(Strategy[] testedStrategies) {
		this.testedStrategies = testedStrategies;
		bestStrategies = MasterVariables.bestStrategies;
		TestCasesCapabilities = MasterVariables.TestCasesCapabilities;
		bestStrategiesOrder = MasterVariables.bestStrategiesOrder;
		testedStratgyIndexes = MasterVariables.testedStratgyIndexes;
		testedSize = MasterVariables.testedSize;
		testCases = MasterVariables.testCases;
		TestCasesRandNums = MasterVariables.TestCasesRandNums;
		TestCasesRandGauss = MasterVariables.TestCasesRandGauss;
		TestCasesCapChangeRates = MasterVariables.TestCasesCapChangeRates;

		checkErrors();
	}

	public Void call() {
		for (int i = 0; i < testedStrategies.length; i++) {
			testedStrategies[i].fitness = 0;
		}
		int roundsSurvived;
		int[] capabilitesConfiguration;
		for (int configNumber = 0; configNumber < TestCasesCapabilities.length; configNumber++) {
			capabilitesConfiguration = TestCasesCapabilities[configNumber].clone();
			TestCase test = new TestCase(capabilitesConfiguration, TestCasesCapChangeRates[configNumber],
					TestCasesRandNums[configNumber], TestCasesRandGauss[configNumber],
					testedStratgyIndexes[configNumber]);
			Strategy[] strategyList = new Strategy[testedSize];
			for (int j = 0; j < bestStrategies.length; j++) {
				if (j >= testedStratgyIndexes[configNumber])
					strategyList[j + 1] = bestStrategies[bestStrategiesOrder[configNumber][j]];
				else
					strategyList[j] = bestStrategies[bestStrategiesOrder[configNumber][j]];
			}
			
			
			for (int strategyNumber = 0; strategyNumber < testedStrategies.length; strategyNumber++) {
				strategyList[testedStratgyIndexes[configNumber]] = testedStrategies[strategyNumber];
				test.resetTestCase(strategyList);
				roundsSurvived = test.simulate();
				
				testedStrategies[strategyNumber].increaseFitness(roundsSurvived);
			}
			

		}

		for (int i = 0; i < testedStrategies.length; i++) {
			testedStrategies[i].tested = true;
		}

		return null;
	}

	private void checkErrors() {
		if (testedSize < 2 || testedSize > MasterVariables.MAXSYSTEM || testedSize - 1 != bestStrategies.length
				|| testedStrategies == null || bestStrategies == null || TestCasesCapabilities == null
				|| bestStrategiesOrder == null || testedStratgyIndexes == null) {
			System.out.println("\n\nFitnessCalculator class error !!!");
			System.out.println(testedSize + " " + bestStrategies.length + " " + testedStrategies.length);
			System.exit(0);
		}

	}
}
