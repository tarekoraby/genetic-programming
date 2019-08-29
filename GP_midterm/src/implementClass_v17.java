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

public class implementClass_v17 implements Runnable {
	double[] fitness;
	static double[] randNum;
	Random rd = new Random();
	int TESTCASES, threadID;

	static final GP_PilotStudy_v17 X=new GP_PilotStudy_v17();

	int gen, POPSIZE;
	public int operationID;
	
	char[][][] init_strategy, join_strategy;
	
	



	public implementClass_v17(int threadID, double[] randNum, int POPSIZE, double[] fitness,
			char[][][] init_strategy, char[][][] join_strategy, int TESTCASES) {

		this.threadID = threadID;
		this.randNum = randNum;
		this.POPSIZE = POPSIZE;
		this.fitness = fitness;
		this.init_strategy = init_strategy;
		this.join_strategy = join_strategy;
		this.TESTCASES = TESTCASES ;
		
	}
	
	
	public void setOperationID(int operationID) {
		this.operationID = operationID;
	}
	public void run() {
		// 1 create random population
		// 2 calculate fitness
		// 3 perform evolutionary operations
		if (operationID == 1) {
			for (int k = 0; k < POPSIZE; k++) {
				for (int i = 0; i < X.uniqueWorldSizes - 1; i++) {
					X.temp_init_strategy[threadID][k][i] =create_random_indiv(X.DEPTH); 
					X.temp_join_strategy[threadID][k][i] =create_random_indiv(X.DEPTH);
				}
				X.temp_init_strategy[threadID][k][X.uniqueWorldSizes - 1] = create_random_indiv(X.DEPTH); 
			}
			
		} else if (operationID == 2) {
			X.tempFitness[threadID] = calculateFitness(TESTCASES);

		} else if (operationID == 3) {
			evolve();
		}
		
	}



	char[] create_random_indiv(int depth) {
		if (depth==0)
			return(null);
		char[] indiv = grow(depth, true);
		while (indiv.length > X.MAX_LEN)
			indiv = grow(depth, true);
		return (indiv);
	}

	char[] grow(int depth, boolean returnBoolean) {
		char[] buffer;
		if (returnBoolean) {
			char prim = (char) rd.nextInt(2);
			if (prim == 0 || depth == 1) {
				char[] leftBuffer = grow(depth - 1, false);
				char[] rightBuffer = grow(depth - 1, false);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(X.FSET_2_END - X.FSET_2_START + 1) + X.FSET_2_START);
				switch (prim) {
				case GP_PilotStudy_v17.GT:
				case GP_PilotStudy_v17.LT:
				case GP_PilotStudy_v17.EQ:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}
			} else {
				char[] leftBuffer = grow(depth - 1, true);
				char[] rightBuffer = grow(depth - 1, true);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(X.FSET_3_END - X.FSET_3_START + 1) + X.FSET_3_START);
				switch (prim) {
				case GP_PilotStudy_v17.AND:
				case GP_PilotStudy_v17.OR:
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
					prim = (char) (X.TSET_START + rd.nextInt(X.TSET_END - X.TSET_START + 1));
				else
					prim = (char) rd.nextInt(X.RANDOMNUMBERS);
				buffer = new char[1];
				buffer[0] = prim;
			} else {
				char[] leftBuffer = grow(depth - 1, false);
				char[] rightBuffer = grow(depth - 1, false);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(X.FSET_1_END - X.FSET_1_START + 1) + X.FSET_1_START);
				switch (prim) {
				case GP_PilotStudy_v17.ADD:
				case GP_PilotStudy_v17.SUB:
				case GP_PilotStudy_v17.MUL:
				case GP_PilotStudy_v17.DIV:
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
		char[][][] tempinit_strategy, tempjoin_strategy;
		char[] newind;
		double random;
		init_strategy = X.init_strategy;
		join_strategy = X.join_strategy;
		fitness = X.fitness;

		tempinit_strategy = new char[POPSIZE][X.uniqueWorldSizes][];
		tempjoin_strategy = new char[POPSIZE][X.uniqueWorldSizes-1][];
		
		int mod_crossovers = (int) (POPSIZE * X.MOD_CROSSOVER_PROB);
		
		for (indivs = 0; indivs < mod_crossovers; indivs++) {

			for (int i = 0; i < X.uniqueWorldSizes; i++) {
				tempinit_strategy[indivs][i] = init_strategy[tournament(fitness, X.TSIZE)][i];
			}
			for (int i = 0; i < X.uniqueWorldSizes - 1; i++) {
				tempjoin_strategy[indivs][i] = join_strategy[tournament(fitness, X.TSIZE)][i];
			}

		}
			
		for (indivs = mod_crossovers; indivs < POPSIZE ; indivs++) {
			
			for (int i = 0; i < X.uniqueWorldSizes; i++) {
				random = rd.nextDouble() * (1 - X.MOD_CROSSOVER_PROB);
				if (random  < X.REPLICATION_PROB) {
					parent = tournament(fitness, X.TSIZE);
					tempinit_strategy[indivs][i] = init_strategy[parent][i];
				} else if ((random - X.REPLICATION_PROB) < X.CROSSOVER_PROB) {
					parent1 = tournament(fitness, X.TSIZE);
					parent2 = tournament(fitness, X.TSIZE);
					newind = crossover(init_strategy[parent1][i],
							init_strategy[parent2][i]);
					tempinit_strategy[indivs][i] = newind;
				} else if ((random - (X.CROSSOVER_PROB + X.REPLICATION_PROB)) < X.SUBTREE_MUT_PROB) {
					parent = tournament(fitness, X.TSIZE);
					newind = subtreeMutation(init_strategy[parent][i]);
					tempinit_strategy[indivs][i] = newind;
				} else if ((random - (X.CROSSOVER_PROB + X.REPLICATION_PROB + X.SUBTREE_MUT_PROB)) < X.PMUT_PROB) {
					parent = tournament(fitness, X.TSIZE);
					newind = pointMutation(init_strategy[parent][i], X.PMUT_PER_NODE);
					tempinit_strategy[indivs][i] = newind;
				} else {
					tempinit_strategy[indivs][i] = create_random_indiv(X.DEPTH);
				}			
			}
			
			for (int i = 0; i < X.uniqueWorldSizes-1; i++) {
				random = rd.nextDouble() * (1 - X.MOD_CROSSOVER_PROB);
				if (random < X.REPLICATION_PROB) {
					parent = tournament(fitness, X.TSIZE);
					tempjoin_strategy[indivs][i] = join_strategy[parent][i];
				} else if ((random - X.REPLICATION_PROB) < X.CROSSOVER_PROB) {
					parent1 = tournament(fitness, X.TSIZE);
					parent2 = tournament(fitness, X.TSIZE);
					newind = crossover(join_strategy[parent1][i],
							join_strategy[parent2][i]);
					tempjoin_strategy[indivs][i] = newind;
				} else if ((random - (X.CROSSOVER_PROB + X.REPLICATION_PROB)) < X.SUBTREE_MUT_PROB) {
					parent = tournament(fitness, X.TSIZE);
					newind = subtreeMutation(join_strategy[parent][i]);
					tempjoin_strategy[indivs][i] = newind;
				} else if ((random - (X.CROSSOVER_PROB + X.REPLICATION_PROB + X.SUBTREE_MUT_PROB)) < X.PMUT_PROB) {
					parent = tournament(fitness, X.TSIZE);
					newind = pointMutation(join_strategy[parent][i], X.PMUT_PER_NODE);
					tempjoin_strategy[indivs][i] = newind;
				} else {
					tempjoin_strategy[indivs][i] = create_random_indiv(X.DEPTH);
				}			
			}
			
		}
		

		X.temp_init_strategy[threadID] = tempinit_strategy;
		X.temp_join_strategy[threadID] = tempjoin_strategy;
	}

	

	double[] calculateFitness(int testCases) {
		fitness = new double[POPSIZE];
		int length, testedStrategyIndex;
		double[] capabilities;
		double[] probabilities, GaussProb;
		double[][] capChangeRates;
		int[][] changeIndexes;
		int[][] testStrategyIndexes;
		char[][][][] init_strategy, join_strategy;
		WorldSystem_v17 world;
		for (int test = 0; test < testCases; test++) {
			length = X.length[test];
			testedStrategyIndex = X.testedStrategyIndex[test];
			capabilities = (double[]) X.capabilities[test].clone();
			probabilities = (double[]) X.probabilities[test].clone();
			GaussProb= (double[]) X.GaussProb[test].clone();
			changeIndexes = (int[][]) X.changeIndexes[test].clone();
			init_strategy = new char[length][X.CAPCHANGE + 1][X.uniqueWorldSizes][];
			join_strategy = new char[length][X.CAPCHANGE + 1][X.uniqueWorldSizes - 1][];
			testStrategyIndexes = (int[][]) X.testStrategyIndexes[test].clone();
			capChangeRates= (double[][]) X.capChangeRates[test].clone();

			for (int m = 0; m <= X.CAPCHANGE; m++) {
				for (int i = 0; i < length; i++) {
					if (X.FT_init_strategy[testStrategyIndexes[m][i]][0] != null) {
							init_strategy[i][m] = (char[][]) X.FT_init_strategy[testStrategyIndexes[m][i]].clone();
							join_strategy[i][m]= (char[][]) X.FT_join_strategy[testStrategyIndexes[m][i]].clone();
					}
				}
			}

			for (int i = 0; i < POPSIZE; i++) {

				init_strategy[testedStrategyIndex][0] = this.init_strategy[i];
				join_strategy[testedStrategyIndex][0] = this.join_strategy[i];
						
				world = new WorldSystem_v17(length, testedStrategyIndex, capabilities, probabilities, GaussProb, changeIndexes, capChangeRates);
				boolean survived = world.simulate(init_strategy, join_strategy);

				if (survived)
					fitness[i]++;
			}
		}

		return (fitness);
	}

	int tournament(double[] fitness,  int tsize) {
		int best = rd.nextInt(POPSIZE ), i, competitor;
		double fbest = -1.0e34;

		for (i = 0; i < tsize; i++) {
			competitor = rd.nextInt(POPSIZE );
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

	char[] pointMutation(char[] parent, double pmut) {
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
						prim = (char) (X.TSET_START + rd.nextInt(X.TSET_END - X.TSET_START + 1));
					else
						prim = (char) rd.nextDouble();
					parentcopy[mutsite] = prim;
				}
				else
					switch (parentcopy[mutsite]) {
					case GP_PilotStudy_v17.ADD:
					case GP_PilotStudy_v17.SUB:
					case GP_PilotStudy_v17.MUL:
					case GP_PilotStudy_v17.DIV:
						parentcopy[mutsite] = (char) (rd.nextInt(X.FSET_1_END - X.FSET_1_START + 1) + X.FSET_1_START);
						break;
					case GP_PilotStudy_v17.GT:
						parentcopy[mutsite]=(char) (rd.nextInt(X.FSET_2_END - X.FSET_2_START + 1) + X.FSET_2_START);;
						break;
					case GP_PilotStudy_v17.LT:
						parentcopy[mutsite]=(char) (rd.nextInt(X.FSET_2_END - X.FSET_2_START + 1) + X.FSET_2_START);;
						break;
					case GP_PilotStudy_v17.EQ:
						parentcopy[mutsite]=(char) (rd.nextInt(X.FSET_2_END - X.FSET_2_START + 1) + X.FSET_2_START);;
						break;
					case GP_PilotStudy_v17.AND:
						parentcopy[mutsite]=GP_PilotStudy_v17.OR;
						break;
					case GP_PilotStudy_v17.OR:
						parentcopy[mutsite]=GP_PilotStudy_v17.AND;
						break;
					}
			}
		}
		return (parentcopy);
	}

	char[] subtreeMutation(char[] parent) {
		int mutStart, mutEnd, parentLen = traverse(parent, 0), subtreeLen, lenOff;
		char[] newSubtree, offspring;

		// Calculate the mutation starting point. Add 1 because subtree mutation
		// shouldn't replace the whole tree at its root.
		mutStart = 1 + rd.nextInt(parentLen - 1);
		mutEnd = traverse(parent, mutStart);

		// Grow new subtree. If the replaced tree returned boolean, make sure
		// the new subtree returns boolean as well. The new subtree cannot be more
		// than one level deeper than the replaced one
		if (parent[mutStart] >= X.FSET_2_START)
			newSubtree = grow(1 + rd.nextInt((int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), true);
		else
			newSubtree = grow(rd.nextInt(1 + (int) (Math.log(2 + mutEnd - mutStart) / Math.log(2))), false);
		
		subtreeLen=traverse(newSubtree, 0);
		
		lenOff = mutStart + subtreeLen + (parentLen-mutEnd);

		offspring = new char[lenOff];

		System.arraycopy(parent, 0, offspring, 0, mutStart);
		System.arraycopy(newSubtree, 0, offspring, mutStart, subtreeLen);
		System.arraycopy(parent, mutEnd, offspring, (mutStart + subtreeLen ) , (parentLen-mutEnd));

		return (offspring);
	}
	
	int traverse( char [] buffer, int buffercount ) {
		if ( buffer[buffercount] < X.FSET_1_START )
			return( ++buffercount );

		switch(buffer[buffercount]) {
		case GP_PilotStudy_v17.ADD: 
		case GP_PilotStudy_v17.SUB: 
		case GP_PilotStudy_v17.MUL: 
		case GP_PilotStudy_v17.DIV:
		case GP_PilotStudy_v17.GT:
		case GP_PilotStudy_v17.LT:
		case GP_PilotStudy_v17.EQ:
		case GP_PilotStudy_v17.AND:
		case GP_PilotStudy_v17.OR:
			return( traverse( buffer, traverse( buffer, ++buffercount ) ) );
		}
		return( 0 ); // should never get here
	}


}

