package v13;

import java.util.concurrent.Callable;

public class FitnessCalculator implements Callable<Void> {
	Strategy[] testedStrategies;
	double[][] TestCasesCapabilities, TestCasesRandNums, TestCasesRandGauss;
	double[] TestCasesInitProness, TestCasesBalanceProness, TestCasesJoinProness;
	int[][][] TestCasesCapChangeRates;
	int[][] bestStrategiesOrder;
	int[] testedStratgyIndexes;
	int testedSize, testCases;

	public FitnessCalculator(Strategy[] testedStrategies) {
		this.testedStrategies = testedStrategies;
		TestCasesCapabilities = MasterVariables.TestCasesCapabilities;
		bestStrategiesOrder = MasterVariables.bestStrategiesOrder;
		testedStratgyIndexes = MasterVariables.testedStratgyIndexes;
		testedSize = MasterVariables.testedSize;
		testCases = MasterVariables.testCases;
		TestCasesRandNums = MasterVariables.TestCasesRandNums;
		TestCasesRandGauss = MasterVariables.TestCasesRandGauss;
		TestCasesCapChangeRates = MasterVariables.TestCasesCapChangeRates;
		TestCasesInitProness = MasterVariables.init_proness;
		TestCasesBalanceProness = MasterVariables.balance_proness;
		TestCasesJoinProness = MasterVariables.join_proness;

		checkErrors();
	}

	public Void call() {
		for (int i = 0; i < testedStrategies.length; i++) {
			testedStrategies[i].resetFitness(testedSize);
		}
		int roundsSurvived;
		double[] capabilitesConfiguration;
		for (int configNumber = 0; configNumber < TestCasesCapabilities.length; configNumber++) {
			capabilitesConfiguration = TestCasesCapabilities[configNumber];
			TestCase test = new TestCase(capabilitesConfiguration, TestCasesCapChangeRates[configNumber],
					TestCasesRandNums[configNumber], TestCasesRandGauss[configNumber],
					testedStratgyIndexes[configNumber], TestCasesInitProness[configNumber],
					TestCasesBalanceProness[configNumber], TestCasesJoinProness[configNumber]);

			for (int strategyNumber = 0; strategyNumber < testedStrategies.length; strategyNumber++) {
				char[] testedInitStrategy = null, testedJoinStrategy = null, testedBalanceStrategy = null;
				if (MasterVariables.testInit)
					testedInitStrategy = testedStrategies[strategyNumber].getInitStrategy(testedSize);
				else {
					testedJoinStrategy = testedStrategies[strategyNumber].getJoinStrategy(testedSize);
					testedBalanceStrategy = testedStrategies[strategyNumber].getBalanceStrategy(testedSize);
				}
				test.resetTestCase(testedInitStrategy, testedJoinStrategy, testedBalanceStrategy);
				roundsSurvived = test.simulate();

				testedStrategies[strategyNumber].increaseFitnessBy(testedSize, roundsSurvived);
			}
		}

		for (int i = 0; i < testedStrategies.length; i++) {
			testedStrategies[i].tested = true;
		}

		return null;
	}

	private void checkErrors() {
		if (testedSize < 2 || testedSize > MasterVariables.MAXSYSTEM || testedStrategies == null
				|| TestCasesCapabilities == null || bestStrategiesOrder == null || testedStratgyIndexes == null) {
			System.out.println("\n\nFitnessCalculator class error !!!");
			System.out.println(testedSize + " " + testedStrategies.length);
			System.exit(0);
		}

	}
}
