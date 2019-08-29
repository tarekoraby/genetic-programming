package v14;

import java.util.Random;

public class Evolver {

	static Strategy[] population;
	static int evoSysSize, tsize, POPSIZE;
	static StrategyCreator creator = new StrategyCreator();
	static Random rd = new Random();
	static TestCase currTestCase;

	public static Strategy[] evolve(Strategy[] pop, int currentLevel) {
		population = pop;
		POPSIZE = population.length;
		evoSysSize = currentLevel;
		tsize = MasterVariables.TSIZE;
		for (int counter = 0; counter < population.length; counter++) {
			createTestCase();
			Strategy evolvedIndiv = evolveIndividual();
			calculateFitness(evolvedIndiv);
			int replacementIndex = rd.nextInt(POPSIZE);
			calculateFitness(population[replacementIndex]);
			if (evolvedIndiv.getFitness(evoSysSize) > population[replacementIndex].getFitness(evoSysSize)) {
				population[replacementIndex] = evolvedIndiv.deepCopy();
			}
			population[replacementIndex].resetFitness(evoSysSize);
		}
		return population;
	}

	private static void calculateFitness(Strategy evolvedIndiv) {
		evolvedIndiv.resetFitness(evoSysSize);
		if (evoSysSize == 2)
			currTestCase.resetTestCase(evolvedIndiv.getInitStrategy(evoSysSize), null, null);
		else
			currTestCase.resetTestCase(evolvedIndiv.getInitStrategy(evoSysSize),
					evolvedIndiv.getJoinStrategy(evoSysSize), evolvedIndiv.getBalanceStrategy(evoSysSize));

		int fitness = currTestCase.simulate();
		evolvedIndiv.increaseFitnessBy(evoSysSize, fitness);
	}

	private static Strategy evolveIndividual() {
		Strategy evolvedIndiv;
		int indivs, parent1 = -99, parent2 = -99, parent = -99;
		double random;

		if (evoSysSize > 2) {
			random = rd.nextDouble();
			if (random < MasterVariables.MOD_CROSSOVER_PROB) {
				evolvedIndiv = new Strategy(evoSysSize,
						population[tournament(population, MasterVariables.TSIZE)].getInitStrategy(evoSysSize),
						population[tournament(population, MasterVariables.TSIZE)].getJoinStrategy(evoSysSize),
						population[tournament(population, MasterVariables.TSIZE)].getBalanceStrategy(evoSysSize));
				return evolvedIndiv;
			}
		}

		char[] newInitStrategy;
		random = rd.nextDouble();
		if (random < MasterVariables.REPLICATION_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newInitStrategy = population[parent].getInitStrategy(evoSysSize);
			parent = -99;
		} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
			parent1 = tournament(population, MasterVariables.TSIZE);
			parent2 = tournament(population, MasterVariables.TSIZE);
			newInitStrategy = crossover(population[parent1].getInitStrategy(evoSysSize),
					population[parent2].getInitStrategy(evoSysSize));
			parent1 = -99;
			parent2 = -99;
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newInitStrategy = subtreeMutation(population[parent].getInitStrategy(evoSysSize),
					MasterVariables.SIMULATECONSTRUCTIVISM);
			parent = -99;
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newInitStrategy = pointMutation(population[parent].getInitStrategy(evoSysSize),
					MasterVariables.SIMULATECONSTRUCTIVISM);
			parent = -99;
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB
				+ MasterVariables.SUBTREE_MUT_PROB + MasterVariables.PMUT_PROB)) < MasterVariables.LOGICAL_COMB_PROB) {
			parent1 = tournament(population, MasterVariables.TSIZE);
			parent2 = tournament(population, MasterVariables.TSIZE);
			newInitStrategy = logicalCombination(population[parent1].getInitStrategy(evoSysSize),
					population[parent2].getInitStrategy(evoSysSize));
			parent1 = -99;
			parent2 = -99;
		} else {
			newInitStrategy = creator
					.create_random_indiv(MasterVariables.DEPTH, MasterVariables.SIMULATECONSTRUCTIVISM);
		}

		if (evoSysSize == 2) {
			evolvedIndiv = new Strategy(evoSysSize, newInitStrategy);
			return evolvedIndiv;
		}

		// ///////////////////////
		char[] newJoinStrategy, newBalanceStrategy;

		random = rd.nextDouble();
		if (random < MasterVariables.REPLICATION_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newJoinStrategy = population[parent].getJoinStrategy(evoSysSize);
			parent = -99;
		} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
			parent1 = tournament(population, MasterVariables.TSIZE);
			parent2 = tournament(population, MasterVariables.TSIZE);
			newJoinStrategy = crossover(population[parent1].getJoinStrategy(evoSysSize),
					population[parent2].getJoinStrategy(evoSysSize));
			parent1 = -99;
			parent2 = -99;
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newJoinStrategy = subtreeMutation(population[parent].getJoinStrategy(evoSysSize),
					MasterVariables.SIMULATECONSTRUCTIVISM);
			parent = -99;
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newJoinStrategy = pointMutation(population[parent].getJoinStrategy(evoSysSize),
					MasterVariables.SIMULATECONSTRUCTIVISM);
			parent = -99;
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB
				+ MasterVariables.SUBTREE_MUT_PROB + MasterVariables.PMUT_PROB)) < MasterVariables.LOGICAL_COMB_PROB) {
			parent1 = tournament(population, MasterVariables.TSIZE);
			parent2 = tournament(population, MasterVariables.TSIZE);
			newJoinStrategy = logicalCombination(population[parent1].getJoinStrategy(evoSysSize),
					population[parent2].getJoinStrategy(evoSysSize));
			parent1 = -99;
			parent2 = -99;
		} else {
			newJoinStrategy = creator
					.create_random_indiv(MasterVariables.DEPTH, MasterVariables.SIMULATECONSTRUCTIVISM);
		}

		random = rd.nextDouble();
		if (random < MasterVariables.REPLICATION_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newBalanceStrategy = population[parent].getBalanceStrategy(evoSysSize);
			parent = -99;
		} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
			parent1 = tournament(population, MasterVariables.TSIZE);
			parent2 = tournament(population, MasterVariables.TSIZE);
			newBalanceStrategy = crossover(population[parent1].getBalanceStrategy(evoSysSize),
					population[parent2].getBalanceStrategy(evoSysSize));
			parent1 = -99;
			parent2 = -99;
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newBalanceStrategy = subtreeMutation(population[parent].getBalanceStrategy(evoSysSize),
					MasterVariables.SIMULATECONSTRUCTIVISM);
			parent = -99;
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
			parent = tournament(population, MasterVariables.TSIZE);
			newBalanceStrategy = pointMutation(population[parent].getBalanceStrategy(evoSysSize),
					MasterVariables.SIMULATECONSTRUCTIVISM);
		} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB
				+ MasterVariables.SUBTREE_MUT_PROB + MasterVariables.PMUT_PROB)) < MasterVariables.LOGICAL_COMB_PROB) {
			parent1 = tournament(population, MasterVariables.TSIZE);
			parent2 = tournament(population, MasterVariables.TSIZE);
			newBalanceStrategy = logicalCombination(population[parent1].getBalanceStrategy(evoSysSize),
					population[parent2].getBalanceStrategy(evoSysSize));
			parent1 = -99;
			parent2 = -99;
		} else {
			newBalanceStrategy = creator.create_random_indiv(MasterVariables.DEPTH,
					MasterVariables.SIMULATECONSTRUCTIVISM);
		}

		evolvedIndiv = new Strategy(evoSysSize, newInitStrategy, newJoinStrategy, newBalanceStrategy);
		return evolvedIndiv;
	}

	private static void createTestCase() {
		double[] capabilities = new double[evoSysSize], randNums = new double[1000], randGauss = new double[1000];
		double[][] capChangeRates = new double[MasterVariables.INTERACTIONROUNDS - 1][evoSysSize];
		int testedStrategyIndex = rd.nextInt(evoSysSize);
		Strategy[][] strategies = new Strategy[evoSysSize - 1][];

		double remainder = 1;
		for (int i = 0; i < evoSysSize - 1; i++) {
			capabilities[i] = remainder * rd.nextDouble();
			remainder -= capabilities[i];
		}
		capabilities[evoSysSize - 1] = remainder;

		// shuffle capabilities to ensure their uniform distribution
		for (int i = evoSysSize - 1; i > 0; i--) {
			int index = rd.nextInt(i + 1);

			double temp = capabilities[index];
			capabilities[index] = capabilities[i];
			capabilities[i] = temp;
		}

		for (int i = 0; i < MasterVariables.INTERACTIONROUNDS - 1; i++) {
			capChangeRates[i] = new double[evoSysSize];
			for (int j = 0; j < evoSysSize; j++)
				capChangeRates[i][j] = 1 + rd.nextInt(MasterVariables.capChangeRatesFactor);
		}

		testedStrategyIndex = rd.nextInt(evoSysSize);

		for (int currLowerLevel = 2; currLowerLevel < evoSysSize; currLowerLevel++) {
			strategies[currLowerLevel - 2] = new Strategy[currLowerLevel];
			for (int j = 0; j < currLowerLevel; j++)
				strategies[currLowerLevel - 2][j] = MasterVariables.masterPopulations[currLowerLevel - 2][rd
						.nextInt(MasterVariables.masterPopulations[currLowerLevel - 2].length)].deepCopy();

		}

		strategies[evoSysSize - 2] = new Strategy[evoSysSize - 1];
		for (int j = 0; j < strategies[evoSysSize - 2].length; j++)
			strategies[evoSysSize - 2][j] = population[rd.nextInt(population.length)].deepCopy();

		for (int i = 0; i < randNums.length; i++)
			randNums[i] = rd.nextDouble();

		for (int i = 0; i < randGauss.length; i++)
			randGauss[i] =  rd.nextGaussian();

		currTestCase = new TestCase(capabilities, capChangeRates, randNums, randGauss, testedStrategyIndex,
				MasterVariables.initPronness, MasterVariables.balancePronness, MasterVariables.joinPronness, strategies);

	}

	private static char[] logicalCombination(char[] strategy1, char[] strategy2) {
		char[] result;
		Random rd = new Random();
		if (rd.nextInt(2) == 0) {
			result = new char[strategy1.length + strategy2.length + 1];
			System.arraycopy(strategy1, 0, result, 1, strategy1.length);
			System.arraycopy(strategy2, 0, result, 1 + strategy1.length, strategy2.length);
			if (rd.nextInt(2) == 0)
				result[0] = MasterVariables.AND;
			else
				result[0] = MasterVariables.OR;
		} else {
			// combine using If-then-else
			char[] condition = creator.create_random_indiv(MasterVariables.DEPTH,
					MasterVariables.SIMULATECONSTRUCTIVISM);
			result = new char[1 + condition.length + strategy1.length + strategy2.length + 1];
			result[0] = MasterVariables.IF_THEN_ELSE;
			System.arraycopy(condition, 0, result, 1, condition.length);
			System.arraycopy(strategy1, 0, result, 1 + condition.length, strategy1.length);
			System.arraycopy(strategy2, 0, result, 1 + condition.length + strategy1.length, strategy2.length);

		}
		return result;
	}

	static int tournament(Strategy[] originalPopulation, int tsize) {
		int best = rd.nextInt(originalPopulation.length), i, competitor;
		double fbest = -1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(originalPopulation.length);
			calculateFitness(originalPopulation[competitor]);
			if (originalPopulation[competitor].getFitness(evoSysSize) > fbest) {
				fbest = originalPopulation[competitor].getFitness(evoSysSize);
				best = competitor;
			}
			originalPopulation[competitor].resetFitness(evoSysSize);
		}
		return (best);
	}

	static char[] crossover(char[] parent1, char[] parent2) {
		int xo1start, xo1end, xo2start, xo2end;
		char[] offspring;
		int len1 = traverse(parent1, 0);
		int len2 = traverse(parent2, 0);
		int lenoff;

		xo1start = 1 + rd.nextInt(len1 - 1); // 1+ because the root of the tree
												// shouldn't be replaced
		xo1end = traverse(parent1, xo1start);

		xo2start = rd.nextInt(len2);
		while ((parent2[xo2start] < MasterVariables.FSET_2_START && parent1[xo1start] >= MasterVariables.FSET_2_START)
				|| (parent2[xo2start] >= MasterVariables.FSET_2_START && parent1[xo1start] < MasterVariables.FSET_2_START))
			xo2start = rd.nextInt(len2);
		xo2end = traverse(parent2, xo2start);

		lenoff = xo1start + (xo2end - xo2start) + (len1 - xo1end);

		offspring = new char[lenoff];

		System.arraycopy(parent1, 0, offspring, 0, xo1start);
		System.arraycopy(parent2, xo2start, offspring, xo1start, (xo2end - xo2start));
		System.arraycopy(parent1, xo1end, offspring, xo1start + (xo2end - xo2start), (len1 - xo1end));

		return (offspring);
	}

	static char[] pointMutation(char[] parent, boolean simulateConstructivism) {
		int len = traverse(parent, 0), i;
		int mutsite = 1 + rd.nextInt(len - 1); // This is to avoid replacing the
												// root of the tree
		char[] parentcopy = new char[len];

		System.arraycopy(parent, 0, parentcopy, 0, len);
		if (parentcopy[mutsite] < MasterVariables.FSET_1_START) {
			char prim = (char) rd.nextInt(2);
			if (prim == 0)
				if (simulateConstructivism == false)
					prim = (char) (MasterVariables.TSET_1_START + rd.nextInt(MasterVariables.TSET_1_END
							- MasterVariables.TSET_1_START + 1));
				else
					prim = (char) (MasterVariables.TSET_1_START + rd.nextInt(MasterVariables.TSET_2_END
							- MasterVariables.TSET_1_START + 1));
			else
				prim = (char) rd.nextDouble();
			parentcopy[mutsite] = prim;
		} else
			switch (parentcopy[mutsite]) {
			case MasterVariables.ADD:
			case MasterVariables.SUB:
			case MasterVariables.MUL:
			case MasterVariables.DIV:
				parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_1_END - MasterVariables.FSET_1_START + 1) + MasterVariables.FSET_1_START);
				break;
			case MasterVariables.GT:
				parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END - MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
				break;
			case MasterVariables.LT:
				parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END - MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
				break;
			case MasterVariables.EQ:
				parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END - MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
				break;
			case MasterVariables.AND:
				parentcopy[mutsite] = MasterVariables.OR;
				break;
			case MasterVariables.OR:
				parentcopy[mutsite] = MasterVariables.AND;
				break;
			case MasterVariables.IF_THEN_ELSE:
				// do nothing in case of if-then-else
				break;
			default:
				System.out.println("ERROR!! Evolver class ... point mutation method");
				System.exit(0);
			}

		return (parentcopy);
	}

	static char[] subtreeMutation(char[] parent, boolean simulateConstructivism) {
		int mutStart, mutEnd, parentLen = traverse(parent, 0), subtreeLen, lenOff;
		char[] newSubtree, offspring;

		// Calculate the mutation starting point.
		mutStart = 1 + rd.nextInt(parentLen - 1);
		mutEnd = traverse(parent, mutStart);

		// Grow new subtree. If the replaced tree returned boolean, make sure
		// the new subtree returns boolean as well. The new subtree cannot be
		// more
		// than one level deeper than the replaced one
		if (parent[mutStart] >= MasterVariables.FSET_2_START)
			newSubtree = creator.grow(1 + rd.nextInt((int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), true,
					simulateConstructivism);
		else
			newSubtree = creator.grow(rd.nextInt(1 + (int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), false,
					simulateConstructivism);

		subtreeLen = traverse(newSubtree, 0);

		lenOff = mutStart + subtreeLen + (parentLen - mutEnd);

		offspring = new char[lenOff];

		System.arraycopy(parent, 0, offspring, 0, mutStart);
		System.arraycopy(newSubtree, 0, offspring, mutStart, subtreeLen);
		System.arraycopy(parent, mutEnd, offspring, (mutStart + subtreeLen), (parentLen - mutEnd));

		return (offspring);
	}

	static int traverse(char[] buffer, int buffercount) {
		if (buffer[buffercount] < MasterVariables.FSET_1_START)
			return (++buffercount);

		switch (buffer[buffercount]) {
		case MasterVariables.ADD:
		case MasterVariables.SUB:
		case MasterVariables.MUL:
		case MasterVariables.DIV:
		case MasterVariables.GT:
		case MasterVariables.LT:
		case MasterVariables.EQ:
		case MasterVariables.AND:
		case MasterVariables.OR:
			return (traverse(buffer, traverse(buffer, ++buffercount)));
		case MasterVariables.IF_THEN_ELSE:
			return (traverse(buffer, traverse(buffer, traverse(buffer, ++buffercount))));
		}
		System.out.println("traverse method error!!!");
		return (0); // should never get here
	}

	private void checkErrors() {
		if (population == null || evoSysSize < 2 || evoSysSize > MasterVariables.MAXSYSTEM || POPSIZE < 1) {
			System.out.println("Evolver class error!!");
			System.exit(0);
		}

	}

}
