package v13;

import java.util.Random;
import java.util.concurrent.Callable;

public class Evolver implements Callable<Strategy[]> {

	Strategy[] originalPopulation;
	int tsize, original_POPSIZE, evolved_POPSIZE, evolvedSystemSize;
	StrategyCreator creator;
	Random rd = new Random();

	public Evolver(int evolvedSystemSize, Strategy[] originalPopulation, int evolved_POPSIZE) {
		this.evolvedSystemSize = evolvedSystemSize;
		this.originalPopulation = originalPopulation;
		this.evolved_POPSIZE = evolved_POPSIZE;

		tsize = MasterVariables.TSIZE;
		original_POPSIZE = originalPopulation.length;		
		creator = new StrategyCreator();
		checkErrors();
	}
	
	public Evolver(int evolvedSystemSize){
		this.evolvedSystemSize = evolvedSystemSize;
	}

	public Strategy[] call() {

		if (MasterVariables.testInit)
			return evolveInitStrategies();
		else
			return evolveJoinStrategies();
	}

	private Strategy[] evolveInitStrategies() {
		int indivs, parent1 = -99, parent2 = -99, parent = -99;
		double random;
		char[] newInitStrategy;
		Strategy[] evolved_population = new Strategy[evolved_POPSIZE];
		for (int i = 0; i < evolved_population.length; i++) {
			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newInitStrategy = originalPopulation[parent].getInitStrategy(evolvedSystemSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
				parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
				newInitStrategy = crossover(originalPopulation[parent1].getInitStrategy(evolvedSystemSize),
						originalPopulation[parent2].getInitStrategy(evolvedSystemSize));
				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newInitStrategy = subtreeMutation(originalPopulation[parent].getInitStrategy(evolvedSystemSize),
						MasterVariables.SIMULATECONSTRUCTIVISM);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newInitStrategy = pointMutation(originalPopulation[parent].getInitStrategy(evolvedSystemSize),
						MasterVariables.PMUT_PER_NODE, MasterVariables.SIMULATECONSTRUCTIVISM);
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB
					+ MasterVariables.SUBTREE_MUT_PROB + MasterVariables.PMUT_PROB)) < MasterVariables.LOGICAL_COMB_PROB) {
				parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
				parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
				newInitStrategy = logicalCombination(originalPopulation[parent1].getInitStrategy(evolvedSystemSize),
						originalPopulation[parent2].getInitStrategy(evolvedSystemSize));
				parent1 = -99;
				parent2 = -99;
			} else {
				newInitStrategy = creator.create_random_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
			}

			evolved_population[i] = new Strategy(evolvedSystemSize, true, newInitStrategy);
		}
		return evolved_population;
	}
	
	private Strategy[] evolveJoinStrategies(){
		int indivs, parent1 = -99, parent2 = -99, parent = -99;
		double random;
		char[] newJoinStrategy, newBalanceStrategy;
		Strategy[] evolved_population = new Strategy[evolved_POPSIZE];
		for (int i = 0; i < evolved_population.length; i++) {
			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newJoinStrategy = originalPopulation[parent].getJoinStrategy(evolvedSystemSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
				parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
				newJoinStrategy = crossover(originalPopulation[parent1].getJoinStrategy(evolvedSystemSize),
						originalPopulation[parent2].getJoinStrategy(evolvedSystemSize));
				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newJoinStrategy = subtreeMutation(originalPopulation[parent].getJoinStrategy(evolvedSystemSize),
						MasterVariables.SIMULATECONSTRUCTIVISM);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newJoinStrategy = pointMutation(originalPopulation[parent].getJoinStrategy(evolvedSystemSize),
						MasterVariables.PMUT_PER_NODE, MasterVariables.SIMULATECONSTRUCTIVISM);
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB
					+ MasterVariables.SUBTREE_MUT_PROB + MasterVariables.PMUT_PROB)) < MasterVariables.LOGICAL_COMB_PROB) {
				parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
				parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
				newJoinStrategy = logicalCombination(originalPopulation[parent1].getJoinStrategy(evolvedSystemSize),
						originalPopulation[parent2].getJoinStrategy(evolvedSystemSize));
				parent1 = -99;
				parent2 = -99;
			} else {
				newJoinStrategy = creator.create_random_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
			}
			

			random = rd.nextDouble();
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newBalanceStrategy = originalPopulation[parent].getBalanceStrategy(evolvedSystemSize);
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
				parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
				newBalanceStrategy = crossover(originalPopulation[parent1].getBalanceStrategy(evolvedSystemSize),
						originalPopulation[parent2].getBalanceStrategy(evolvedSystemSize));
				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newBalanceStrategy = subtreeMutation(originalPopulation[parent].getBalanceStrategy(evolvedSystemSize),
						MasterVariables.SIMULATECONSTRUCTIVISM);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				newBalanceStrategy = pointMutation(originalPopulation[parent].getBalanceStrategy(evolvedSystemSize),
						MasterVariables.PMUT_PER_NODE, MasterVariables.SIMULATECONSTRUCTIVISM);
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB
					+ MasterVariables.SUBTREE_MUT_PROB + MasterVariables.PMUT_PROB)) < MasterVariables.LOGICAL_COMB_PROB) {
				parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
				parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
				newBalanceStrategy = logicalCombination(originalPopulation[parent1].getBalanceStrategy(evolvedSystemSize),
						originalPopulation[parent2].getBalanceStrategy(evolvedSystemSize));
				parent1 = -99;
				parent2 = -99;
			} else {
				newBalanceStrategy = creator.create_random_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
			}

			evolved_population[i] = new Strategy(evolvedSystemSize, false, newJoinStrategy, newBalanceStrategy);
		}
		return evolved_population;
	}

	private char[] logicalCombination(char[] strategy1, char[] strategy2) {
		char[] result = new char[strategy1.length + strategy2.length + 1];
		System.arraycopy(strategy1, 0, result, 1, strategy1.length);
		System.arraycopy(strategy2, 0, result, 1 + strategy1.length, strategy2.length);
		Random rd = new Random();
		if (rd.nextInt(2) == 0)
			result[0] = MasterVariables.AND;
		else
			result[0] = MasterVariables.OR;
		return result;
	}

	int tournament(Strategy[] originalPopulation, int tsize) {
		int best = rd.nextInt(originalPopulation.length), i, competitor;
		double fbest = -1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(originalPopulation.length);
			if (originalPopulation[competitor].getFitness(evolvedSystemSize) > fbest
					/*|| (originalPopulation[competitor].fitness == fbest && originalPopulation[competitor]
							.calculateLength() < originalPopulation[best].calculateLength())*/) {
				fbest = originalPopulation[competitor].getFitness(evolvedSystemSize);
				best = competitor;
			}
		}
		return (best);
	}

	int negativeTournament(Strategy[] originalPopulation, int tsize) {
		int worst = rd.nextInt(originalPopulation.length), i, competitor;
		double fworst = 1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(originalPopulation.length);
			if (originalPopulation[competitor].getFitness(evolvedSystemSize) < fworst
					/*|| (originalPopulation[competitor].fitness == fworst && originalPopulation[competitor]
							.calculateLength() > originalPopulation[worst].calculateLength())*/) {
				fworst = originalPopulation[competitor].getFitness(evolvedSystemSize);
				worst = competitor;
			}
		}
		return (worst);
	}

	char[] crossover(char[] parent1, char[] parent2) {		
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

	char[] pointMutation(char[] parent, double pmut, boolean simulateConstructivism) {
		int len = traverse(parent, 0), i;
		int mutsite;
		char[] parentcopy = new char[len];

		System.arraycopy(parent, 0, parentcopy, 0, len);
		for (i = 0; i < len; i++) {
			if (rd.nextDouble() < pmut) {
				mutsite = i;
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
						parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_1_END
								- MasterVariables.FSET_1_START + 1) + MasterVariables.FSET_1_START);
						break;
					case MasterVariables.GT:
						parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END
								- MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
						;
						break;
					case MasterVariables.LT:
						parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END
								- MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
						;
						break;
					case MasterVariables.EQ:
						parentcopy[mutsite] = (char) (rd.nextInt(MasterVariables.FSET_2_END
								- MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
						;
						break;
					case MasterVariables.AND:
						parentcopy[mutsite] = MasterVariables.OR;
						break;
					case MasterVariables.OR:
						parentcopy[mutsite] = MasterVariables.AND;
						break;
					}
			}
		}
		return (parentcopy);
	}

	char[] subtreeMutation(char[] parent, boolean simulateConstructivism) {
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
			newSubtree = creator.grow(1 + rd.nextInt((int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), true, simulateConstructivism);
		else
			newSubtree = creator.grow(rd.nextInt(1 + (int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), false, simulateConstructivism);

		subtreeLen = traverse(newSubtree, 0);

		lenOff = mutStart + subtreeLen + (parentLen - mutEnd);

		offspring = new char[lenOff];

		System.arraycopy(parent, 0, offspring, 0, mutStart);
		System.arraycopy(newSubtree, 0, offspring, mutStart, subtreeLen);
		System.arraycopy(parent, mutEnd, offspring, (mutStart + subtreeLen), (parentLen - mutEnd));

		return (offspring);
	}

	int traverse(char[] buffer, int buffercount) {
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
		if (originalPopulation == null || evolvedSystemSize < 2
				|| evolvedSystemSize > MasterVariables.MAXSYSTEM || evolved_POPSIZE < 1 ) {
			System.out.println("Evolver class error!!");
			System.exit(0);
		}

	}

}
