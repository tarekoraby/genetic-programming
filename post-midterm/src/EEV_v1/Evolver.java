package EEV_v1;

import java.util.Arrays;
import java.util.Random;

public class Evolver {

	static int tsize;
	static StrategyCreator creator = new StrategyCreator();
	static Random rd = new Random();

	Evolver() {
		tsize = MasterVariables.TSIZE;
	}

	public void evolve(int currentLevel, boolean isInit) {
		for (int currentOrder = 1; currentOrder <= currentLevel; currentOrder++) {
			Strategy[] population;
			if (isInit)
				population = MasterVariables.bestStratgies.getInitStratgies(currentLevel, currentOrder);
			else
				population = MasterVariables.bestStratgies.getJoinStratgies(currentLevel, currentOrder);
			for (int index = 0; index < population.length; index++) {
				TestCase currentTestCase = createTestCase(isInit, currentLevel, currentOrder);
				Strategy newIndiv = evolveIndividual(population, isInit, currentLevel, currentTestCase);
				calculateFitness(newIndiv, currentTestCase);
				calculateFitness(population[index], currentTestCase);
				if (false && currentLevel == 2)
					System.out.println("before " + population[index].getFitness());
				population[index] = considerReplacement(population[index], newIndiv, isInit);
			}
			if (isInit)
				MasterVariables.bestStratgies.setInitStratgies(currentLevel, currentOrder, population);
			else
				MasterVariables.bestStratgies.setJoinStratgies(currentLevel, currentOrder, population);
		}
	}
	
	private static TestCase createTestCase(boolean isInit, int worldSize, int order) {

		double[] capabilities = new double[worldSize], randNums = new double[1000], randGauss = new double[1000];
		double[][] capChangeRates = new double[MasterVariables.INTERACTIONROUNDS - 1][worldSize];
		int testedStrategyIndex;
		
		StrategyCollection stratColl=new StrategyCollection(worldSize);
		stratColl.populateWithRandomStrategies();
		if (!isInit){
			stratColl.setToNull(worldSize, isInit);
		}
		stratColl.setToNull(worldSize, order, isInit);


		double remainder = 1;
		for (int i = 0; i < worldSize - 1; i++) {
			capabilities[i] = remainder * rd.nextDouble();
			remainder -= capabilities[i];
		}
		capabilities[worldSize - 1] = remainder;

		// shuffle capabilities to ensure their uniform distribution
		for (int i = worldSize - 1; i > 0; i--) {
			int index = rd.nextInt(i + 1);

			double temp = capabilities[index];
			capabilities[index] = capabilities[i];
			capabilities[i] = temp;
		}
		

		for (int i = 0; i < MasterVariables.INTERACTIONROUNDS - 1; i++) {
			capChangeRates[i] = new double[worldSize];
			for (int j = 0; j < worldSize; j++)
				capChangeRates[i][j] = 1 + rd.nextInt(MasterVariables.capChangeRatesFactor);
		}

		/*double[] temp = capabilities.clone();
		Arrays.sort(temp);
		double[] temp2 = capabilities.clone();
		int j = 0;
		for (int i = capabilities.length - 1; i >= 0; i--)
			temp2[j++] = temp[i];
		temp = temp2;
		
		testedStrategyIndex = -1;
		switch (order) {
		case 1:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[0]);
			break;
		case 2:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[1]);
			break;
		case 3:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[2]);
			break;
		case 4:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[3]);
			break;
		case 5:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[4]);
			break;
		case 6:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[5]);
			break;
		case 7:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[6]);
			break;
		case 8:
			do {
				testedStrategyIndex++;
			} while (capabilities[testedStrategyIndex] != temp[7]);
			break;
		}*/
		

		for (int i = 0; i < randNums.length; i++)
			randNums[i] = rd.nextDouble();

		for (int i = 0; i < randGauss.length; i++)
			randGauss[i] = rd.nextGaussian();

		TestCase newTestCase = new TestCase(capabilities, capChangeRates, randNums, randGauss, order,
				MasterVariables.initPronness, MasterVariables.balancePronness, MasterVariables.joinPronness,
				stratColl, isInit);
		return newTestCase;
	}

	private static void calculateFitness(Strategy evolvedIndiv, TestCase currTestCase) {
		evolvedIndiv.resetFitness();
		evolvedIndiv.attacks =evolvedIndiv.balances = evolvedIndiv.bandwagons = evolvedIndiv.buckpasses = 0;
		int fitness = currTestCase.test(evolvedIndiv);
		evolvedIndiv.increaseFitnessBy(fitness);
	}

	private static Strategy evolveIndividual(Strategy[] population, boolean isInit, int worldSize, TestCase currentTestCase) {
		Strategy evolvedIndiv;
		int indivs, parent1 = -99, parent2 = -99, parent = -99;
		double random;
		
		///xxxxx
		if (rd.nextBoolean())
		return (creator.create_random_strategy(worldSize, isInit));
		
		char[][] newInitStrategy;
		parent = tournament(population, MasterVariables.TSIZE);
		int index = rd.nextInt(worldSize - 1);				
		char[][] strategy = population[parent].getInitStrategy(worldSize);				
		char[] mutStrategy = Arrays.copyOfRange(strategy[index], 1, strategy[index].length);
		char[] result = subtreeMutation(mutStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
		char order = strategy[index][0];
		newInitStrategy = strategy;
		newInitStrategy[index] = new char[1 + result.length];
		newInitStrategy[index][0] = order;
		System.arraycopy(result, 0, newInitStrategy[index], 1, result.length);
		parent = -99;
		
		evolvedIndiv = new Strategy(worldSize, isInit);
		evolvedIndiv.setInitStrategy(worldSize, newInitStrategy);
		return evolvedIndiv;
		
		//xxxxxxx
		/*if (isInit) {
			char[][] newInitStrategy;
			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				newInitStrategy = population[parent].getInitStrategy(evoSysSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(population, MasterVariables.TSIZE);
				parent2 = tournament(population, MasterVariables.TSIZE);
				int index1 = rd.nextInt(evoSysSize - 1);
				int index2 = rd.nextInt(evoSysSize - 1);
				char[][] strategy1 = population[parent1].getInitStrategy(evoSysSize);
				char[][] strategy2 = population[parent2].getInitStrategy(evoSysSize);
				char[] mutStrategy1 = Arrays.copyOfRange(strategy1[index1], 1, strategy1[index1].length);
				char[] mutStrategy2 = Arrays.copyOfRange(strategy1[index2], 1, strategy1[index2].length);

				char[] result = crossover(mutStrategy1, mutStrategy2);
				char order = strategy1[index1][0];
				newInitStrategy = strategy1;
				newInitStrategy[index1] = new char[1 + result.length];
				newInitStrategy[index1][0] = order;
				System.arraycopy(result, 0, newInitStrategy[index1], 1, result.length);

				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				int index = rd.nextInt(evoSysSize - 1);				
				char[][] strategy = population[parent].getInitStrategy(evoSysSize);				
				char[] mutStrategy = Arrays.copyOfRange(strategy[index], 1, strategy[index].length);
				char[] result = subtreeMutation(mutStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				char order = strategy[index][0];
				newInitStrategy = strategy;
				newInitStrategy[index] = new char[1 + result.length];
				newInitStrategy[index][0] = order;
				System.arraycopy(result, 0, newInitStrategy[index], 1, result.length);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				int index = rd.nextInt(evoSysSize - 1);
				char[][] strategy = population[parent].getInitStrategy(evoSysSize);
				char[] mutStrategy = Arrays.copyOfRange(strategy[index], 1, strategy[index].length);
				char[] result = pointMutation(mutStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				char order = strategy[index][0];
				newInitStrategy = strategy;
				newInitStrategy[index] = new char[1 + result.length];
				newInitStrategy[index][0] = order;
				System.arraycopy(result, 0, newInitStrategy[index], 1, result.length);
				newInitStrategy = strategy;
				parent = -99;
			}  else {
				newInitStrategy = creator.create_random_init_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM, evoSysSize);
			}

			evolvedIndiv = new Strategy(evoSysSize, isInit, newInitStrategy);
			return evolvedIndiv;

		} else {
			random = rd.nextDouble();
			if (random < MasterVariables.MOD_CROSSOVER_PROB) {
				evolvedIndiv = new Strategy(evoSysSize, isInit,
						population[tournament(population, MasterVariables.TSIZE)].getJoinStrategy(evoSysSize),
						population[tournament(population, MasterVariables.TSIZE)].getBalanceStrategy(evoSysSize));
				return evolvedIndiv;
			}

			char[] newJoinStrategy, newBalanceStrategy;

			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				newJoinStrategy = population[parent].getJoinStrategy(evoSysSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(population, MasterVariables.TSIZE);
				parent2 = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy1 = Arrays.copyOfRange(population[parent1].getJoinStrategy(evoSysSize), 1, population[parent1].getJoinStrategy(evoSysSize).length); 
				char[] parentStrategy2 = Arrays.copyOfRange(population[parent2].getJoinStrategy(evoSysSize), 1, population[parent2].getJoinStrategy(evoSysSize).length);
				char[] result = crossover(parentStrategy1,
						parentStrategy2);
				newJoinStrategy = new char[result.length + 1];
				newJoinStrategy[0] = population[parent1].getJoinStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newJoinStrategy, 1, result.length);
				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy = Arrays.copyOfRange(population[parent].getJoinStrategy(evoSysSize), 1,
						population[parent].getJoinStrategy(evoSysSize).length);
				char[] result = subtreeMutation(parentStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				newJoinStrategy = new char[result.length + 1];
				newJoinStrategy[0] = population[parent].getJoinStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newJoinStrategy, 1, result.length);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy = Arrays.copyOfRange(population[parent].getJoinStrategy(evoSysSize), 1,
						population[parent].getJoinStrategy(evoSysSize).length);
				char[] result = pointMutation(parentStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				newJoinStrategy = new char[result.length + 1];
				newJoinStrategy[0] = population[parent].getJoinStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newJoinStrategy, 1, result.length);
				parent = -99;
			} else {
				newJoinStrategy = creator.create_random_join_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
			}

			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				newBalanceStrategy = population[parent].getBalanceStrategy(evoSysSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(population, MasterVariables.TSIZE);
				parent2 = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy1 = Arrays.copyOfRange(population[parent1].getBalanceStrategy(evoSysSize), 1, population[parent1].getBalanceStrategy(evoSysSize).length); 
				char[] parentStrategy2 = Arrays.copyOfRange(population[parent2].getBalanceStrategy(evoSysSize), 1, population[parent2].getBalanceStrategy(evoSysSize).length);
				char[] result = crossover(parentStrategy1,
						parentStrategy2);
				newBalanceStrategy = new char[result.length + 1];
				newBalanceStrategy[0] = population[parent1].getBalanceStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newBalanceStrategy, 1, result.length);
				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy = Arrays.copyOfRange(population[parent].getBalanceStrategy(evoSysSize), 1,
						population[parent].getBalanceStrategy(evoSysSize).length);
				char[] result = subtreeMutation(parentStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				newBalanceStrategy = new char[result.length + 1];
				newBalanceStrategy[0] = population[parent].getBalanceStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newBalanceStrategy, 1, result.length);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(population, MasterVariables.TSIZE);
				char[] parentStrategy = Arrays.copyOfRange(population[parent].getBalanceStrategy(evoSysSize), 1,
						population[parent].getBalanceStrategy(evoSysSize).length);
				char[] result = pointMutation(parentStrategy, MasterVariables.SIMULATECONSTRUCTIVISM);
				newBalanceStrategy = new char[result.length + 1];
				newBalanceStrategy[0] = population[parent].getBalanceStrategy(evoSysSize)[0];
				System.arraycopy(result, 0, newBalanceStrategy, 1, result.length);
				parent = -99;
			}  else {
				newBalanceStrategy = creator.create_random_join_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
			}

			evolvedIndiv = new Strategy(evoSysSize, isInit, newJoinStrategy, newBalanceStrategy);
			return evolvedIndiv;
		}
		*/
	}

	static int tournament(Strategy[] originalPopulation, int tsize) {
		int best = rd.nextInt(originalPopulation.length), i, competitor;
		double fbest = -1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(originalPopulation.length);
			//calculateFitness(originalPopulation[competitor]);
			if (originalPopulation[competitor].getFitness() > fbest) {
				fbest = originalPopulation[competitor].getFitness();
				best = competitor;
			}
			//originalPopulation[competitor].resetFitness();
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


	

	private Strategy considerReplacement(Strategy original, Strategy newIndiv, boolean isInit) {
		if (newIndiv.getFitness() > original.getFitness()) {			
			return newIndiv.deepCopy();
		} else if (false && newIndiv.getFitness() == original.getFitness()) {
			if (isInit) {
				boolean preferAttacker = rd.nextBoolean();
				if (preferAttacker) {
					if (newIndiv.attacks > original.attacks)
						return newIndiv.deepCopy();
				} else {
					if (newIndiv.attacks < original.attacks)
						return newIndiv.deepCopy();
				}
			} else {
				int rand = rd.nextInt(3);
				switch (rand) {
				case 0:
					//prefer balancers
					if (newIndiv.balances > original.balances)
						return newIndiv.deepCopy();
					break;
				case 1:
					//prefer bandwagoners
					if (newIndiv.bandwagons > original.bandwagons)
						return newIndiv.deepCopy();
					break;
				case 2:
					//prefer buckpassers
					if (newIndiv.buckpasses > original.buckpasses)
						return newIndiv.deepCopy();
					break;
				}
			}
		}
		
		return original;
	}


}
