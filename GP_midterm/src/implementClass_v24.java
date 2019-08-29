/* Based on tiny_gp.java by Riccardo Poli (email: rpoli@essex.ac.uk)
 * 
 */

/* TO DO
 * CHECK THAT TRAVEREE IS NECESSARY INSTEAD OF JUST USING ARRAY LENGTH
 * Simplify the expressions, perhaps every few generations to fight bloat
 * Check that the joining strategy doesn't affect the profiles in 2-state games
 */

/* DESCRIPTION	
 * This program use genetic programming in order to answer the following question, under what circumstances should a state initiate a war or 
 * join one that was initiated by others?
 * The aim is specifically to evaluate the realist claim
 * that, under specific distribution of material resources, there is a single way in which a state should behave in order to survive.
 *
 */

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.text.DecimalFormat;

public class implementClass_v24 implements Runnable {
	double[] fitness;
	static double[] randNum;
	Random rd = new Random();
	int TESTCASES, threadID;

	static final Simulation_v24 X = new Simulation_v24();

	int gen, POPSIZE;
	public int operationID;
	boolean type2;

	char[][][] init_strategy, join_strategy;
	
	public implementClass_v24(){
		
	}

	public implementClass_v24(int threadID, double[] randNum, int POPSIZE, double[] fitness, char[][][] init_strategy,
			char[][][] join_strategy, int TESTCASES, boolean type2) {

		this.threadID = threadID;
		this.randNum = randNum;
		this.POPSIZE = POPSIZE;
		this.fitness = fitness;
		this.init_strategy = init_strategy;
		this.join_strategy = join_strategy;
		this.TESTCASES = TESTCASES;
		this.type2 = type2;

	}

	public void setOperationID(int operationID) {
		this.operationID = operationID;
	}

	public void run() {
		// 1 create random population
		// 2 calculate fitness
		// 3 perform evolutionary operations
		/*if (operationID == 1) {
			for (int k = 0; k < POPSIZE; k++) {
				for (int i = 0; i < X.uniqueWorldSizes - 1; i++) {
					X.temp_init_strategy[threadID][k][i] = create_random_indiv(X.DEPTH);
					X.temp_join_strategy[threadID][k][i] = create_random_indiv(X.DEPTH);
				}
				X.temp_init_strategy[threadID][k][X.uniqueWorldSizes - 1] = create_random_indiv(X.DEPTH);
			}

		} else 
			*/if (operationID == 2) {
			X.tempFitness[threadID] = calculateFitness(TESTCASES);

		} else if (operationID == 3) {
			evolve();
		}

	}

	char[] create_random_indiv(int depth, boolean type2) {
		if (depth == 0)
			return (null);
		char[] indiv = grow(depth, true, type2);
		while (indiv.length > X.MAX_LEN)
			indiv = grow(depth, true, type2);
		return (indiv);
	}

	char[] grow(int depth, boolean returnBoolean, boolean type2) {
		char[] buffer;
		if (returnBoolean) {
			char prim = (char) rd.nextInt(2);
			if (prim == 0 || depth == 1) {
				char[] leftBuffer = grow(depth - 1, false, type2);
				char[] rightBuffer = grow(depth - 1, false, type2);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(X.FSET_2_END - X.FSET_2_START + 1) + X.FSET_2_START);
				switch (prim) {
				case GP_PilotStudy_v24.GT:
				case GP_PilotStudy_v24.LT:
				case GP_PilotStudy_v24.EQ:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}
			} else {
				char[] leftBuffer = grow(depth - 1, true, type2);
				char[] rightBuffer = grow(depth - 1, true, type2);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(X.FSET_3_END - X.FSET_3_START + 1) + X.FSET_3_START);
				switch (prim) {
				case GP_PilotStudy_v24.AND:
				case GP_PilotStudy_v24.OR:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}
			}
		} else {
			char prim = (char) rd.nextInt(2);
			if (prim == 0 || depth == 0) {
				prim = (char) rd.nextInt(2);
				if (prim == 0)
					if (type2==false)
						prim = (char) (X.TSET_1_START + rd.nextInt(X.TSET_1_END - X.TSET_1_START + 1));
					else
						prim = (char) (X.TSET_1_START + rd.nextInt(X.TSET_2_END - X.TSET_1_START + 1));
				else
					prim = (char) rd.nextInt(X.RANDOMNUMBERS);
				buffer = new char[1];
				buffer[0] = prim;
			} else {
				char[] leftBuffer = grow(depth - 1, false, type2);
				char[] rightBuffer = grow(depth - 1, false, type2);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(X.FSET_1_END - X.FSET_1_START + 1) + X.FSET_1_START);
				switch (prim) {
				case GP_PilotStudy_v24.ADD:
				case GP_PilotStudy_v24.SUB:
				case GP_PilotStudy_v24.MUL:
				case GP_PilotStudy_v24.DIV:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}
			}
		}

		return buffer;
	}

	void evolve() {
		int indivs, parent1, parent2, parent;
		int[] parents;
		char[][] tempinit_strategy, tempjoin_strategy;
		char[] newind;
		double random;
		
		//the first state in the list, the best one shouldn't be included in evolution
		init_strategy=new char[X.init_strategy.length-1][][];
		join_strategy=new char[X.join_strategy.length-1][][];
		System.arraycopy(X.init_strategy, 1, init_strategy, 0, X.init_strategy.length-1);
		System.arraycopy(X.join_strategy, 1, join_strategy, 0, X.join_strategy.length-1);
		fitness = X.fitness;

		tempinit_strategy = new char[POPSIZE][];
		tempjoin_strategy = null;
		if (X.currentTestedSize > 2)
			tempjoin_strategy = new char[POPSIZE][];
		

		int mod_crossovers = (int) (POPSIZE * X.MOD_CROSSOVER_PROB);

		if (X.currentTestedSize == 2)
			mod_crossovers = 0;

		for (indivs = 0; indivs < mod_crossovers; indivs++) {
				tempinit_strategy[indivs] = init_strategy[tournament(fitness, X.TSIZE)][X.currentTestedSize - X.MINSYSTEM];
				tempjoin_strategy[indivs] = join_strategy[tournament(fitness, X.TSIZE)][X.currentTestedSize - X.MINSYSTEM - 1];

		}

		int i = -99;
		for (indivs = mod_crossovers; indivs < POPSIZE; indivs++) {
			i = X.currentTestedSize - X.MINSYSTEM;
			random = rd.nextDouble() * (1 - X.MOD_CROSSOVER_PROB);
			if (random < X.REPLICATION_PROB) {
				parent = tournament(fitness, X.TSIZE);
				tempinit_strategy[indivs] = init_strategy[parent][i];
			} else if ((random - X.REPLICATION_PROB) < X.CROSSOVER_PROB) {
				parent1 = tournament(fitness, X.TSIZE);
				parent2 = tournament(fitness, X.TSIZE);
				newind = crossover(init_strategy[parent1][i],
						init_strategy[parent2][i]);
				tempinit_strategy[indivs] = newind;
			} else if ((random - (X.CROSSOVER_PROB + X.REPLICATION_PROB)) < X.SUBTREE_MUT_PROB) {
				parent = tournament(fitness, X.TSIZE);
				newind = subtreeMutation(init_strategy[parent][i], type2);
				tempinit_strategy[indivs] = newind;
			} else if ((random - (X.CROSSOVER_PROB + X.REPLICATION_PROB + X.SUBTREE_MUT_PROB)) < X.PMUT_PROB) {
				parent = tournament(fitness, X.TSIZE);
				newind = pointMutation(init_strategy[parent][i], X.PMUT_PER_NODE, type2);
				tempinit_strategy[indivs] = newind;
			} else {
				tempinit_strategy[indivs] = create_random_indiv(X.DEPTH, type2);
			}

		}

		if (X.currentTestedSize > 2) {
			i = X.currentTestedSize - X.MINSYSTEM - 1;
			for (indivs = mod_crossovers; indivs < POPSIZE; indivs++) {
				random = rd.nextDouble() * (1 - X.MOD_CROSSOVER_PROB);
				if (random < X.REPLICATION_PROB) {
					parent = tournament(fitness, X.TSIZE);
					tempjoin_strategy[indivs] = join_strategy[parent][i];
				} else if ((random - X.REPLICATION_PROB) < X.CROSSOVER_PROB) {
					parent1 = tournament(fitness, X.TSIZE);
					parent2 = tournament(fitness, X.TSIZE);
					newind = crossover(join_strategy[parent1][i], join_strategy[parent2][i]);
					tempjoin_strategy[indivs] = newind;
				} else if ((random - (X.CROSSOVER_PROB + X.REPLICATION_PROB)) < X.SUBTREE_MUT_PROB) {
					parent = tournament(fitness, X.TSIZE);
					newind = subtreeMutation(join_strategy[parent][i], type2);
					tempjoin_strategy[indivs] = newind;
				} else if ((random - (X.CROSSOVER_PROB + X.REPLICATION_PROB + X.SUBTREE_MUT_PROB)) < X.PMUT_PROB) {
					parent = tournament(fitness, X.TSIZE);
					newind = pointMutation(join_strategy[parent][i], X.PMUT_PER_NODE, type2);
					tempjoin_strategy[indivs] = newind;
				} else {
					tempjoin_strategy[indivs] = create_random_indiv(X.DEPTH, type2);
				}
			}
		}

	

		for (indivs = 0; indivs < POPSIZE; indivs++) {
			X.temp_init_strategy[threadID][indivs][X.currentTestedSize - X.MINSYSTEM] = (char[]) tempinit_strategy[indivs].clone();
			if (X.currentTestedSize > 2)
				X.temp_join_strategy[threadID][indivs][X.currentTestedSize - X.MINSYSTEM - 1] = (char[]) tempjoin_strategy[indivs].clone();
		}
	}

	double[] calculateFitness(int testCases) {
		fitness = new double[POPSIZE];
		int length ;
		double[] capabilities;
		double[] probabilities, GaussProb;
		double[][] capChangeRates;
		int[] testedStrategyIndex;
		int[][] changeIndexes;
		int[][] testStrategyIndexes, initialEnmity;
		char[][][][] init_strategy, join_strategy;
		WorldSystem_v24 world;
		for (int test = 0; test < testCases; test++) {
			length = X.length[test];
			if (length != X.currentTestedSize)
				continue;
			testedStrategyIndex = (int[]) X.testedStrategyIndex[test].clone();
			capabilities = (double[]) X.capabilities[test].clone();
			probabilities = (double[]) X.probabilities[test].clone();
			GaussProb = (double[]) X.GaussProb[test].clone();
			changeIndexes = (int[][]) X.changeIndexes[test].clone();
			init_strategy = new char[length][X.CAPCHANGE + 1][X.uniqueWorldSizes][];
			join_strategy = new char[length][X.CAPCHANGE + 1][X.uniqueWorldSizes - 1][];
			testStrategyIndexes = (int[][]) X.testStrategyIndexes[test].clone();
			capChangeRates = (double[][]) X.capChangeRates[test].clone();

			initialEnmity= (int[][]) X.initialEnmity[test].clone();

			for (int m = 0; m <= X.CAPCHANGE; m++) {
				for (int i = 0; i < length; i++) {
					if (i == testedStrategyIndex[m])
						continue;
					int t = testStrategyIndexes[m][i];
					if (t >= X.currentDeme)
						t++;
					if (t < X.currentTestedSize) {
						for (int j = 0; j < X.uniqueWorldSizes; j++) {
							init_strategy[i][m][j] = (char[]) X.FT_init_strategy[X.currentTestedSize - X.MINSYSTEM][t][j]
									.clone();
						}
						if (X.currentTestedSize > 2)
							for (int j = 0; j < X.uniqueWorldSizes - 1; j++)
								join_strategy[i][m][j] = (char[]) X.FT_join_strategy[X.currentTestedSize
										- X.MINSYSTEM - 1][t][j].clone();

						/*for (int f = X.currentTestedSize - X.MINSYSTEM - 1; f >= 0; f--) {
							init_strategy[i][m][f] = (char[]) X.FT_init_strategy[f][rd
									.nextInt(X.FT_init_strategy[f].length - 1)].clone();
							if (f > 0)
								join_strategy[i][m][f - 1] = (char[]) X.FT_join_strategy[f - 1][rd
										.nextInt(X.FT_join_strategy[f - 1].length - 1)].clone();

						}*/
					}
				}
			}
			
			/*if (X.currentTestedSize == 3) {
				double capMax = -1, capMin = 10, capMid = -1;
				for (int i = 0; i < length; i++) {
					if (capabilities[i] > capMax)
						capMax = capabilities[i];
					if (capabilities[i] < capMin)
						capMin = capabilities[i];
				}
				for (int i = 0; i < length; i++) {
					if (capabilities[i] > capMin && capabilities[i] < capMax)
						capMid = capabilities[i];
				}
			}*/
			
			
			for (int i = 0; i < POPSIZE; i++) {		
				/*for (int m = 0; m <= X.CAPCHANGE; m++) {
					init_strategy[testedStrategyIndex[m]][m][0] = (char[]) this.init_strategy[i][0].clone();
					for (int k = 1; k <= X.currentTestedSize - X.MINSYSTEM; k++) {
						if (this.init_strategy[i][k] != null) {
							init_strategy[testedStrategyIndex[m]][m][k] = (char[]) this.init_strategy[i][k].clone();
							join_strategy[testedStrategyIndex[m]][m][k - 1] = (char[]) this.join_strategy[i][k - 1]
									.clone();
						}
					}
				}*/

				for (int m = 0; m <= X.CAPCHANGE; m++) {
					init_strategy[testedStrategyIndex[m]][m] = this.init_strategy[i];
					join_strategy[testedStrategyIndex[m]][m] = this.join_strategy[i];
				}
				
				

				world = new WorldSystem_v24(length, testedStrategyIndex, capabilities, probabilities, GaussProb, changeIndexes, capChangeRates, initialEnmity);
				boolean survived = world.simulate(init_strategy, join_strategy);

				if (survived)
					fitness[i]++;
			}
		}

		return (fitness);
	}

	int tournament(double[] fitness, int tsize) {
		int best = rd.nextInt(POPSIZE), i, competitor;
		double fbest = -1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(POPSIZE);
			if (fitness[competitor] > fbest) {
				fbest = fitness[competitor];
				best = competitor;
			}
		}
		return (best);
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
		while ((parent2[xo2start] < X.FSET_2_START && parent1[xo1start] >= X.FSET_2_START)
				|| (parent2[xo2start] >= X.FSET_2_START && parent1[xo1start] < X.FSET_2_START))
			xo2start = rd.nextInt(len2);
		xo2end = traverse(parent2, xo2start);

		lenoff = xo1start + (xo2end - xo2start) + (len1 - xo1end);

		offspring = new char[lenoff];

		System.arraycopy(parent1, 0, offspring, 0, xo1start);
		System.arraycopy(parent2, xo2start, offspring, xo1start, (xo2end - xo2start));
		System.arraycopy(parent1, xo1end, offspring, xo1start + (xo2end - xo2start), (len1 - xo1end));

		return (offspring);
	}

	char[] pointMutation(char[] parent, double pmut, boolean type2) {
		int len = traverse(parent, 0), i;
		int mutsite;
		char[] parentcopy = new char[len];

		System.arraycopy(parent, 0, parentcopy, 0, len);
		for (i = 0; i < len; i++) {
			if (rd.nextDouble() < pmut) {
				mutsite = i;
				if (parentcopy[mutsite] < X.FSET_1_START) {
					char prim = (char) rd.nextInt(2);
					if (prim == 0)
						if (type2==false)
							prim = (char) (X.TSET_1_START + rd.nextInt(X.TSET_1_END - X.TSET_1_START + 1));
						else
							prim = (char) (X.TSET_1_START + rd.nextInt(X.TSET_2_END - X.TSET_1_START + 1));
					else
						prim = (char) rd.nextDouble();
					parentcopy[mutsite] = prim;
				} else
					switch (parentcopy[mutsite]) {
					case GP_PilotStudy_v24.ADD:
					case GP_PilotStudy_v24.SUB:
					case GP_PilotStudy_v24.MUL:
					case GP_PilotStudy_v24.DIV:
						parentcopy[mutsite] = (char) (rd.nextInt(X.FSET_1_END - X.FSET_1_START + 1) + X.FSET_1_START);
						break;
					case GP_PilotStudy_v24.GT:
						parentcopy[mutsite] = (char) (rd.nextInt(X.FSET_2_END - X.FSET_2_START + 1) + X.FSET_2_START);
						;
						break;
					case GP_PilotStudy_v24.LT:
						parentcopy[mutsite] = (char) (rd.nextInt(X.FSET_2_END - X.FSET_2_START + 1) + X.FSET_2_START);
						;
						break;
					case GP_PilotStudy_v24.EQ:
						parentcopy[mutsite] = (char) (rd.nextInt(X.FSET_2_END - X.FSET_2_START + 1) + X.FSET_2_START);
						;
						break;
					case GP_PilotStudy_v24.AND:
						parentcopy[mutsite] = GP_PilotStudy_v24.OR;
						break;
					case GP_PilotStudy_v24.OR:
						parentcopy[mutsite] = GP_PilotStudy_v24.AND;
						break;
					}
			}
		}
		return (parentcopy);
	}

	char[] subtreeMutation(char[] parent, boolean type2) {
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
		if (parent[mutStart] >= X.FSET_2_START)	
			newSubtree = grow(1 + rd.nextInt((int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), true, type2);
		else
			newSubtree = grow(rd.nextInt(1 + (int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), false, type2);

		subtreeLen = traverse(newSubtree, 0);

		lenOff = mutStart + subtreeLen + (parentLen - mutEnd);

		offspring = new char[lenOff];

		System.arraycopy(parent, 0, offspring, 0, mutStart);
		System.arraycopy(newSubtree, 0, offspring, mutStart, subtreeLen);
		System.arraycopy(parent, mutEnd, offspring, (mutStart + subtreeLen), (parentLen - mutEnd));

		return (offspring);
	}

	int traverse(char[] buffer, int buffercount) {
		if (buffer[buffercount] < X.FSET_1_START)
			return (++buffercount);

		switch (buffer[buffercount]) {
		case GP_PilotStudy_v24.ADD:
		case GP_PilotStudy_v24.SUB:
		case GP_PilotStudy_v24.MUL:
		case GP_PilotStudy_v24.DIV:
		case GP_PilotStudy_v24.GT:
		case GP_PilotStudy_v24.LT:
		case GP_PilotStudy_v24.EQ:
		case GP_PilotStudy_v24.AND:
		case GP_PilotStudy_v24.OR:
			return (traverse(buffer, traverse(buffer, ++buffercount)));
		}
		return (0); // should never get here
	}

}
