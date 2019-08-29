package v4;



import java.util.Random;
import java.util.concurrent.Callable;

public class Evolver implements Callable<LevelStrategy[]> {

	LevelStrategy[] originalPopulation, evolved_population;
	int tsize, POPSIZE, evolved_POPSIZE, evolvedSystemSize;
	StrategyCreator creator;
	Random rd = new Random();

	public Evolver(int evolvedSystemSize, LevelStrategy[] originalPopulation, int evolved_POPSIZE) {
		this.evolvedSystemSize = evolvedSystemSize;
		this.originalPopulation = originalPopulation;
		this.evolved_POPSIZE = evolved_POPSIZE;
		evolved_population = new LevelStrategy[evolved_POPSIZE];

		tsize = MasterVariables.TSIZE;
		POPSIZE = originalPopulation.length;		
		creator = new StrategyCreator();
		checkErrors();
	}
	
	public Evolver(){
		
	}

	public LevelStrategy[] call() {
		int indivs, parent1 = -99, parent2 = -99, parent = -99;
		int mod_crossovers;
		if (evolvedSystemSize == 2)
			mod_crossovers = 0;
		else
			mod_crossovers = (int) (evolved_POPSIZE * MasterVariables.MOD_CROSSOVER_PROB);

		for (indivs = 0; indivs < mod_crossovers; indivs++) {
			evolved_population[indivs] = new LevelStrategy(evolvedSystemSize, originalPopulation[tournament(
					originalPopulation, tsize)].init_strategy,
					originalPopulation[tournament(originalPopulation, tsize)].balance_strategy,
					originalPopulation[tournament(originalPopulation, tsize)].bandwagon_strategy);
		}

		char[][] init_strategy = new char[evolved_POPSIZE - mod_crossovers][], balance_strategy = null, bandwagon_strategy = null;
		double random;
		for (indivs = 0; indivs < evolved_POPSIZE - mod_crossovers; indivs++) {
			random = rd.nextDouble() * (1 - MasterVariables.MOD_CROSSOVER_PROB);
			if (random < MasterVariables.REPLICATION_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				init_strategy[indivs] = originalPopulation[parent].init_strategy;
				parent = -99;
			} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
				parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
				parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
				init_strategy[indivs] = crossover(originalPopulation[parent1].init_strategy,
						originalPopulation[parent2].init_strategy);
				parent1 = -99;
				parent2 = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				init_strategy[indivs] = subtreeMutation(originalPopulation[parent].init_strategy,
						MasterVariables.SIMULATECONSTRUCTIVISM);
				parent = -99;
			} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
				parent = tournament(originalPopulation, MasterVariables.TSIZE);
				init_strategy[indivs] = pointMutation(originalPopulation[parent].init_strategy,
						MasterVariables.PMUT_PER_NODE, MasterVariables.SIMULATECONSTRUCTIVISM);
			} else {
				init_strategy[indivs] = creator.create_random_indiv(MasterVariables.DEPTH,
						MasterVariables.SIMULATECONSTRUCTIVISM);
			}
		}

		if (evolvedSystemSize > 2) {
			balance_strategy = new char[evolved_POPSIZE - mod_crossovers][];
			bandwagon_strategy = new char[evolved_POPSIZE - mod_crossovers][];
			for (indivs = 0; indivs < evolved_POPSIZE - mod_crossovers; indivs++) {
				random = rd.nextDouble() * (1 - MasterVariables.MOD_CROSSOVER_PROB);
				if (random < MasterVariables.REPLICATION_PROB) {
					parent = tournament(originalPopulation, MasterVariables.TSIZE);
					balance_strategy[indivs] = originalPopulation[parent].balance_strategy;
					parent = -99;
				} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
					parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
					parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
					balance_strategy[indivs] = crossover(originalPopulation[parent1].balance_strategy,
							originalPopulation[parent2].balance_strategy);
					parent1 = -99;
					parent2 = -99;
				} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
					parent = tournament(originalPopulation, MasterVariables.TSIZE);
					balance_strategy[indivs] = subtreeMutation(originalPopulation[parent].balance_strategy,
							MasterVariables.SIMULATECONSTRUCTIVISM);
					parent = -99;
				} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
					parent = tournament(originalPopulation, MasterVariables.TSIZE);
					balance_strategy[indivs] = pointMutation(originalPopulation[parent].balance_strategy,
							MasterVariables.PMUT_PER_NODE, MasterVariables.SIMULATECONSTRUCTIVISM);
				} else {
					balance_strategy[indivs] = creator.create_random_indiv(MasterVariables.DEPTH,
							MasterVariables.SIMULATECONSTRUCTIVISM);
				}
			}

			for (indivs = 0; indivs < evolved_POPSIZE - mod_crossovers; indivs++) {
				random = rd.nextDouble() * (1 - MasterVariables.MOD_CROSSOVER_PROB);
				if (random < MasterVariables.REPLICATION_PROB) {
					parent = tournament(originalPopulation, MasterVariables.TSIZE);
					bandwagon_strategy[indivs] = originalPopulation[parent].bandwagon_strategy;
					parent = -99;
				} else if ((random - MasterVariables.REPLICATION_PROB) < MasterVariables.CROSSOVER_PROB) {
					parent1 = tournament(originalPopulation, MasterVariables.TSIZE);
					parent2 = tournament(originalPopulation, MasterVariables.TSIZE);
					bandwagon_strategy[indivs] = crossover(originalPopulation[parent1].bandwagon_strategy,
							originalPopulation[parent2].bandwagon_strategy);
					parent1 = -99;
					parent2 = -99;
				} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB)) < MasterVariables.SUBTREE_MUT_PROB) {
					parent = tournament(originalPopulation, MasterVariables.TSIZE);
					bandwagon_strategy[indivs] = subtreeMutation(originalPopulation[parent].bandwagon_strategy,
							MasterVariables.SIMULATECONSTRUCTIVISM);
					parent = -99;
				} else if ((random - (MasterVariables.CROSSOVER_PROB + MasterVariables.REPLICATION_PROB + MasterVariables.SUBTREE_MUT_PROB)) < MasterVariables.PMUT_PROB) {
					parent = tournament(originalPopulation, MasterVariables.TSIZE);
					bandwagon_strategy[indivs] = pointMutation(originalPopulation[parent].bandwagon_strategy,
							MasterVariables.PMUT_PER_NODE, MasterVariables.SIMULATECONSTRUCTIVISM);
				} else {
					bandwagon_strategy[indivs] = creator.create_random_indiv(MasterVariables.DEPTH,
							MasterVariables.SIMULATECONSTRUCTIVISM);
				}
			}
		}

		if (evolvedSystemSize == 2)
			for (indivs = mod_crossovers; indivs < evolved_POPSIZE; indivs++)
				evolved_population[indivs] = new LevelStrategy(evolvedSystemSize,
						init_strategy[indivs - mod_crossovers]);
		else
			for (indivs = mod_crossovers; indivs < evolved_POPSIZE; indivs++)
				evolved_population[indivs] = new LevelStrategy(evolvedSystemSize,
						init_strategy[indivs - mod_crossovers], balance_strategy[indivs - mod_crossovers],
						bandwagon_strategy[indivs - mod_crossovers]);
		return evolved_population;
	}

	

	int tournament(LevelStrategy[] originalPopulation, int tsize) {
		int best = rd.nextInt(POPSIZE), i, competitor;
		double fbest = -1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(POPSIZE);
			if (originalPopulation[competitor].fitness > fbest) {
				fbest = originalPopulation[competitor].fitness;
				best = competitor;
			}
		}
		return (best);
	}
	
	int negativeTournament(LevelStrategy[] originalPopulation, int tsize) {
		int worst = rd.nextInt(originalPopulation.length), i, competitor;
		double fworst = 1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(originalPopulation.length);
			if (originalPopulation[competitor].fitness < fworst) {
				fworst = originalPopulation[competitor].fitness;
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

		// Calculate the mutation starting point. Add 1 because subtree mutation
		// shouldn't replace the whole tree at its root.
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
		}
		return (0); // should never get here
	}

	private void checkErrors() {
		if (originalPopulation == null || evolved_population == null || evolvedSystemSize < 2
				|| evolvedSystemSize > MasterVariables.MAXSYSTEM || evolved_POPSIZE < 1 ) {
			System.out.println("Evolver class error!!");
			System.exit(0);
		}

	}

}
