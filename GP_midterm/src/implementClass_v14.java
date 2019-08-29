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

public class implementClass_v14 implements Runnable {
	double[] fitness;
	static double[] randNum;
	Random rd = new Random();
	int TESTCASES, threadID;
	static final int MAX_LEN = 10000, DEPTH = 5, GENERATIONS = 10000, TSIZE = 10, BESTRETAINED = 50, MINSYSTEM = 2, MAXSYSTEM = 4, RANDOMNUMBERS = 50,
			BLOATSTART = 1;
	static double MINRANDOM = 0, MAXRANDOM = 1;
	public static final double PMUT_PER_NODE = 0.05, MOD_CROSSOVER_PROB= 0.2, REPLICATION_PROB = 0.2,
			CROSSOVER_PROB = 0.1, SUBTREE_MUT_PROB = 0.1, PMUT_PROB = 0.1,
			ABIOGENSIS_PROB = 0.5;
	static final int CAPMED = RANDOMNUMBERS, CAPMIN = CAPMED + 1,
			CAPMAX = CAPMED + 2, MYCAP = CAPMED + 3, OPPCAP = CAPMED + 4,
			MYSIDECAP = CAPMED + 5, LEFTCAPSUM = CAPMED + 6,
			ADD = LEFTCAPSUM + 1, SUB = ADD + 1, MUL = ADD + 2, DIV = ADD + 3,
			GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7,
			OR = ADD + 8, TSET_START = CAPMED, TSET_END = LEFTCAPSUM,
			FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT,
			FSET_2_END = EQ, FSET_3_START = AND, FSET_3_END = OR;
	static char[] program;
	static final boolean BLOATFIGHT = false, PRINTINDIV = false, PRINTPROFILE = false, PROFILEPOWER=false;
	static final int CAPCHANGE = 1;
	int gen, POPSIZE;
	public int operationID;
	
	char[][][] init_strategy, join_strategy;
	
	static double TC1, TC2, TC3, TC4, TC5, TC6, TC7, TC8, TC9, TC10, TC11, TC12, TC13, TC14, TC15, TC16, TC17, TC18;



	public implementClass_v14(int threadID, double[] randNum, int POPSIZE, double[] fitness,
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
				for (int i = 0; i < GP_PilotStudy_v14.uniqueWorldSizes - 1; i++) {
					GP_PilotStudy_v14.temp_init_strategy[threadID][k][i] =create_random_indiv(DEPTH); 
					GP_PilotStudy_v14.temp_join_strategy[threadID][k][i] =create_random_indiv(DEPTH);
				}
				GP_PilotStudy_v14.temp_init_strategy[threadID][k][GP_PilotStudy_v14.uniqueWorldSizes - 1] = create_random_indiv(DEPTH); 
			}
			
		} else if (operationID == 2) {
			GP_PilotStudy_v14.tempFitness[threadID]=calculateFitness(TESTCASES);

		} else if (operationID == 3) {
			evolve();
		}
		
	}



	char[] create_random_indiv(int depth) {
		if (depth==0)
			return(null);
		char[] indiv = grow(depth, true);
		while (indiv.length > MAX_LEN)
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
				prim = (char) (rd.nextInt(FSET_2_END - FSET_2_START + 1) + FSET_2_START);
				switch (prim) {
				case GT:
				case LT:
				case EQ:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}
			} else {
				char[] leftBuffer = grow(depth - 1, true);
				char[] rightBuffer = grow(depth - 1, true);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(FSET_3_END - FSET_3_START + 1) + FSET_3_START);
				switch (prim) {
				case AND:
				case OR:
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
					prim = (char) (TSET_START + rd.nextInt(TSET_END - TSET_START + 1));
				else
					prim = (char) rd.nextInt(RANDOMNUMBERS);
				buffer = new char[1];
				buffer[0] = prim;
			} else {
				char[] leftBuffer = grow(depth - 1, false);
				char[] rightBuffer = grow(depth - 1, false);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(FSET_1_END - FSET_1_START + 1) + FSET_1_START);
				switch (prim) {
				case ADD:
				case SUB:
				case MUL:
				case DIV:
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
		GP_PilotStudy_v14 x = new GP_PilotStudy_v14();
		init_strategy = x.init_strategy;
		join_strategy = x.join_strategy;
		fitness = x.fitness;

		tempinit_strategy = new char[POPSIZE][x.uniqueWorldSizes][];
		tempjoin_strategy = new char[POPSIZE][x.uniqueWorldSizes-1][];
		
		int mod_crossovers = (int) (POPSIZE * MOD_CROSSOVER_PROB);
		
		for (indivs = 0; indivs < mod_crossovers; indivs++) {

			for (int i = 0; i < x.uniqueWorldSizes; i++) {
				tempinit_strategy[indivs][i] = init_strategy[tournament(fitness, TSIZE)][i];
			}
			for (int i = 0; i < x.uniqueWorldSizes - 1; i++) {
				tempjoin_strategy[indivs][i] = join_strategy[tournament(fitness, TSIZE)][i];
			}

		}
			
		for (indivs = mod_crossovers; indivs < POPSIZE ; indivs++) {
			
			for (int i = 0; i < x.uniqueWorldSizes; i++) {
				random = rd.nextDouble();
				if (random  < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempinit_strategy[indivs][i] = init_strategy[parent][i];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(init_strategy[parent1][i],
							init_strategy[parent2][i]);
					tempinit_strategy[indivs][i] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(init_strategy[parent][i]);
					tempinit_strategy[indivs][i] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(init_strategy[parent][i], PMUT_PER_NODE);
					tempinit_strategy[indivs][i] = newind;
				} else {
					tempinit_strategy[indivs][i] = create_random_indiv(DEPTH);
				}			
			}
			
			for (int i = 0; i < x.uniqueWorldSizes-1; i++) {
				random = rd.nextDouble();
				if (random < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempjoin_strategy[indivs][i] = join_strategy[parent][i];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(join_strategy[parent1][i],
							join_strategy[parent2][i]);
					tempjoin_strategy[indivs][i] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(join_strategy[parent][i]);
					tempjoin_strategy[indivs][i] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(join_strategy[parent][i], PMUT_PER_NODE);
					tempjoin_strategy[indivs][i] = newind;
				} else {
					tempjoin_strategy[indivs][i] = create_random_indiv(DEPTH);
				}			
			}
			
		}
		

		GP_PilotStudy_v14.temp_init_strategy[threadID] = tempinit_strategy;
		GP_PilotStudy_v14.temp_join_strategy[threadID] = tempjoin_strategy;
	}

	

	double[] calculateFitness(int testCases) {
		fitness = new double[POPSIZE];
		int length, testedStrategyIndex;
		double[] capabilities;
		double[] probabilities;
		GP_PilotStudy_v14 x = new GP_PilotStudy_v14();
		WorldSystem_v14 world;
		for (int test = 0; test < testCases; test++) {
			length = x.length[test];
			testedStrategyIndex = x.testedStrategyIndex[test];
			capabilities = (double[]) x.capabilities[test].clone();
			probabilities = (double[]) x.probabilities[test].clone();

			for (int i = 0; i < POPSIZE; i++) {
				world = new WorldSystem_v14(length, testedStrategyIndex, capabilities, probabilities);
				boolean survived = world.simulate(init_strategy[i], join_strategy[i]);

				if (survived)
					fitness[i]++;
			}
		}

		if (BLOATFIGHT && gen>=BLOATSTART) // punish longer strategies to fight bloating
			for (int i = 0; i < POPSIZE; i++)
				fitness[i] -= (init_strategy[i].length + join_strategy[i].length) / 2;

		
		
		
		
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
		while ((parent2[xo2start] < FSET_2_START && parent1[xo1start] >= FSET_2_START)
				|| (parent2[xo2start] >= FSET_2_START && parent1[xo1start] < FSET_2_START))
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
				if (parentcopy[mutsite] < FSET_1_START) {
					char prim = (char) rd.nextInt(2);
					if (prim == 0)
						prim = (char) (TSET_START + rd.nextInt(TSET_END - TSET_START + 1));
					else
						prim = (char) rd.nextDouble();
					parentcopy[mutsite] = prim;
				}
				else
					switch (parentcopy[mutsite]) {
					case ADD:
					case SUB:
					case MUL:
					case DIV:
						parentcopy[mutsite] = (char) (rd.nextInt(FSET_1_END - FSET_1_START + 1) + FSET_1_START);
						break;
					case GT:
						parentcopy[mutsite]=(char) (rd.nextInt(FSET_2_END - FSET_2_START + 1) + FSET_2_START);;
						break;
					case LT:
						parentcopy[mutsite]=(char) (rd.nextInt(FSET_2_END - FSET_2_START + 1) + FSET_2_START);;
						break;
					case EQ:
						parentcopy[mutsite]=(char) (rd.nextInt(FSET_2_END - FSET_2_START + 1) + FSET_2_START);;
						break;
					case AND:
						parentcopy[mutsite]=OR;
						break;
					case OR:
						parentcopy[mutsite]=AND;
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
		if (parent[mutStart] >= FSET_2_START)
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
		if ( buffer[buffercount] < FSET_1_START )
			return( ++buffercount );

		switch(buffer[buffercount]) {
		case ADD: 
		case SUB: 
		case MUL: 
		case DIV:
		case GT:
		case LT:
		case EQ:
		case AND:
		case OR:
			return( traverse( buffer, traverse( buffer, ++buffercount ) ) );
		}
		return( 0 ); // should never get here
	}


}

