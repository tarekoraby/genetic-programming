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

public class GP_PilotStudy_v10 {
	double[] fitness;
	static double[] randNum;
	static char[][] strategy_init_2, strategy_init_3, strategy_init_4,
			strategy_join_3;
	static Random rd = new Random();
	static final int MAX_LEN = 10000, POPSIZE = 1000, DEPTH = 5,
			GENERATIONS = 10000, TSIZE = 2, BESTRETAINED = 50,
			MOD_CROSSOVERS = BESTRETAINED, TESTCASES = 6000, PROFILES = 1000,
			MINSYSTEM = 2, MAXSYSTEM = 4, RANDOMNUMBERS = 50,
			BLOATSTART = 1000;
	static double MINRANDOM = 0, MAXRANDOM = 1;
	public static final double PMUT_PER_NODE = 0.05, REPLICATION_PROB = 0.25,
			CROSSOVER_PROB = 0.15, SUBTREE_MUT_PROB = 0.15, PMUT_PROB = 0.05,
			ABIOGENSIS_PROB = 0.4;
	static final int CAPMED = RANDOMNUMBERS, CAPMIN = CAPMED + 1,
			CAPMAX = CAPMED + 2, MYCAP = CAPMED + 3, OPPCAP = CAPMED + 4,
			MYSIDECAP = CAPMED + 5, LEFTCAPSUM = CAPMED + 6,
			ADD = LEFTCAPSUM + 1, SUB = ADD + 1, MUL = ADD + 2, DIV = ADD + 3,
			GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7,
			OR = ADD + 8, TSET_START = CAPMED, TSET_END = LEFTCAPSUM,
			FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT,
			FSET_2_END = EQ, FSET_3_START = AND, FSET_3_END = OR;
	static char[] program;
	static int prevBestAttacks, prev10BestAttacks, prevBestJoins;
	static double avg_len, similarityWarInit_2_Avg, similarityWarInit_3_Avg,
			similarityWarInit_4_Avg, similarityWarJoinAvg, tenWI_2_Similarity,
			tenWI_3_Similarity, tenWJSimilarity;
	static double[][] targets;
	static final boolean BLOATFIGHT = false, PRINTINDIV = false,
			PRINTPROFILE = false, PROFILEPOWER = false;
	static final int CAPCHANGE = 1;
	World_System_Shell_v10[] profilingWorlds;
	boolean[][] profile;
	boolean[][] profile10;
	boolean[] profilePrevBest;
	int previousBestHash_1, previousBestHash_2;
	boolean sortedFit;
	static int X = 0;

	static double TC1, TC2, TC3, TC4, TC5, TC6, TC7, TC8, TC9, TC10, TC11,
			TC12, TC13, TC14, TC15, TC16, TC17, TC18;

	public static void main(String[] args) {

		GP_PilotStudy_v10 gp = new GP_PilotStudy_v10();
		gp.evolve();

	}

	public GP_PilotStudy_v10() {
		System.out.println("START OF PROGRAM");
		if (BLOATFIGHT) {
			System.out.println("Bloat fight is on!!");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (PROFILEPOWER) {
			System.out.println("PROFILEPOWER is on!!");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (DEPTH < 1) {
			System.out
					.println("Cannot excute!! Minimum depth of intitial individuals is 1");
			System.exit(0);
		}
		if (1 - Math.abs(REPLICATION_PROB + CROSSOVER_PROB + SUBTREE_MUT_PROB
				+ PMUT_PROB + ABIOGENSIS_PROB) > 1E-10) {
			System.out.println("Probabilities don't add up to 1");
			System.exit(0);
		}
		if (MINSYSTEM >= MAXSYSTEM) {
			System.out
					.println("Warning !!!! MAXSYSTEM is less than or equal MINSYSTEM");
		}

		fitness = new double[POPSIZE];
		randNum = new double[RANDOMNUMBERS];
		for (int i = 0; i < RANDOMNUMBERS; i++)
			randNum[i] = (MAXRANDOM - MINRANDOM) * rd.nextDouble() + MINRANDOM;

		profilingWorlds = new World_System_Shell_v10[PROFILES];
		for (int i = 0; i < PROFILES / 2; i++)
			profilingWorlds[i] = new World_System_Shell_v10(false);
		if (MAXSYSTEM > 2)
			for (int i = PROFILES / 2; i < PROFILES; i++)
				profilingWorlds[i] = new World_System_Shell_v10(true);

		strategy_init_2 = create_random_pop(POPSIZE, DEPTH, fitness);
		strategy_init_3 = create_random_pop(POPSIZE, DEPTH, fitness);
		strategy_init_4 = create_random_pop(POPSIZE, DEPTH, fitness);
		strategy_join_3 = create_random_pop(POPSIZE, DEPTH, fitness);

		profile = new boolean[BESTRETAINED][PROFILES];
		profile10 = new boolean[BESTRETAINED][PROFILES];
		profilePrevBest = new boolean[PROFILES];

		sortedFit = false;
	}

	char[][] create_random_pop(int popSize, int depth, double[] fitness) {
		char[][] pop = new char[popSize][];
		int i;

		for (i = 0; i < popSize; i++) {
			pop[i] = create_random_indiv(depth);
		}
		return (pop);
	}

	char[] create_random_indiv(int depth) {
		if (depth == 0)
			return (null);
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
					System.arraycopy(leftBuffer, 0, buffer, 1,
							leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer,
							(1 + leftBuffer.length), rightBuffer.length);
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
					System.arraycopy(leftBuffer, 0, buffer, 1,
							leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer,
							(1 + leftBuffer.length), rightBuffer.length);
				}
			}
		} else {
			char prim = (char) rd.nextInt(2);
			if (prim == 0 || depth == 0) {
				prim = (char) rd.nextInt(2);
				if (prim == 0)
					prim = (char) (TSET_START + rd.nextInt(TSET_END
							- TSET_START + 1));
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
					System.arraycopy(leftBuffer, 0, buffer, 1,
							leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer,
							(1 + leftBuffer.length), rightBuffer.length);
				}
			}
		}

		return buffer;
	}

	void evolve() {
		int indivs, parent1, parent2, parent;
		int[] parents;
		char[][] tempstrategy_init_2, tempstrategy_init_3, tempstrategy_init_4, tempstrategy_join_3;

		fitness = calculateFitness(strategy_init_2, strategy_init_3,
				strategy_init_4, strategy_join_3, TESTCASES);
		stats(fitness, strategy_init_2, strategy_init_3, strategy_init_4,
				strategy_join_3, 0);

		for (int gen = 1; gen <= GENERATIONS; gen++) {

			TC11 = 0; // Num of worlds
			TC12 = 0; // Num of attacks

			tempstrategy_init_2 = new char[POPSIZE][];
			tempstrategy_init_3 = new char[POPSIZE][];
			tempstrategy_init_4 = new char[POPSIZE][];
			tempstrategy_join_3 = new char[POPSIZE][];

			// Keep the best strategies in the new population
			if (sortedFit) {
				System.arraycopy(strategy_init_2, 0, tempstrategy_init_2, 0,
						BESTRETAINED);
				System.arraycopy(strategy_init_3, 0, tempstrategy_init_3, 0,
						BESTRETAINED);
				System.arraycopy(strategy_init_4, 0, tempstrategy_init_4, 0,
						BESTRETAINED);
				System.arraycopy(strategy_join_3, 0, tempstrategy_join_3, 0,
						BESTRETAINED);
			} else {
				System.out.println("Not sorted!!");
				System.exit(0);
			}

			// Perform crossover at the module level
			for (indivs = BESTRETAINED; indivs < MOD_CROSSOVERS + BESTRETAINED; indivs++) {
				parents = new int[3];
				parents[0] = tournament(fitness, TSIZE);
				parents[1] = tournament(fitness, TSIZE);
				parents[2] = tournament(fitness, TSIZE);
				tempstrategy_init_2[indivs] = strategy_init_2[parents[rd
						.nextInt(3)]];
				tempstrategy_init_3[indivs] = strategy_init_3[parents[rd
						.nextInt(3)]];
				tempstrategy_init_4[indivs] = strategy_init_4[parents[rd
						.nextInt(3)]];
				tempstrategy_join_3[indivs] = strategy_join_3[parents[rd
						.nextInt(3)]];
			}

			// For the remaining slots in the new population probabilistically
			// perform replication, crossover, subtree mutation or point
			// mutation
			// The evolutionary operations acts independently for both
			// strategies
			for (indivs = MOD_CROSSOVERS + BESTRETAINED; indivs < POPSIZE; indivs++) {
				TC7++;
				char[] newind;
				double random = rd.nextDouble();
				if (random < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempstrategy_init_2[indivs] = strategy_init_2[parent];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(strategy_init_2[parent1],
							strategy_init_2[parent2]);
					tempstrategy_init_2[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(strategy_init_2[parent]);
					tempstrategy_init_2[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(strategy_init_2[parent],
							PMUT_PER_NODE);
					tempstrategy_init_2[indivs] = newind;
				} else {
					tempstrategy_init_2[indivs] = create_random_indiv(DEPTH);
				}
			}

			for (indivs = MOD_CROSSOVERS + BESTRETAINED; indivs < POPSIZE; indivs++) {
				// TC7++;
				char[] newind;
				double random = rd.nextDouble();
				if (random < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempstrategy_init_3[indivs] = strategy_init_3[parent];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(strategy_init_3[parent1],
							strategy_init_3[parent2]);
					tempstrategy_init_3[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(strategy_init_3[parent]);
					tempstrategy_init_3[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(strategy_init_3[parent],
							PMUT_PER_NODE);
					tempstrategy_init_3[indivs] = newind;
				} else {
					tempstrategy_init_3[indivs] = create_random_indiv(DEPTH);
				}
			}

			for (indivs = MOD_CROSSOVERS + BESTRETAINED; indivs < POPSIZE; indivs++) {
				// TC7++;
				char[] newind;
				double random = rd.nextDouble();
				if (random < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempstrategy_init_4[indivs] = strategy_init_4[parent];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(strategy_init_4[parent1],
							strategy_init_4[parent2]);
					tempstrategy_init_4[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(strategy_init_4[parent]);
					tempstrategy_init_4[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(strategy_init_4[parent],
							PMUT_PER_NODE);
					tempstrategy_init_4[indivs] = newind;
				} else {
					tempstrategy_init_4[indivs] = create_random_indiv(DEPTH);
				}
			}

			for (indivs = MOD_CROSSOVERS + BESTRETAINED; indivs < POPSIZE; indivs++) {
				// TC7++;
				char[] newind;
				double random = rd.nextDouble();
				if (random < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempstrategy_join_3[indivs] = strategy_join_3[parent];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(strategy_join_3[parent1],
							strategy_join_3[parent2]);
					tempstrategy_join_3[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(strategy_join_3[parent]);
					tempstrategy_join_3[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(strategy_join_3[parent],
							PMUT_PER_NODE);
					tempstrategy_join_3[indivs] = newind;
				} else {
					tempstrategy_join_3[indivs] = create_random_indiv(DEPTH);
				}
			}

			strategy_init_2 = tempstrategy_init_2;
			strategy_init_3 = tempstrategy_init_3;
			strategy_init_4 = tempstrategy_init_4;
			strategy_join_3 = tempstrategy_join_3;

			fitness = calculateFitness(strategy_init_2, strategy_init_3,
					strategy_init_4, strategy_join_3, TESTCASES);
			stats(fitness, strategy_init_2, strategy_init_3, strategy_init_4,
					strategy_join_3, gen);

			// TC14 += similarityWarInitAvg;
		}
		System.out.println("number of created worlds is: " + (int) TC1);
		System.out.println("avg state num is " + (TC2 / TC1));
		System.out.println("avg of initial states avgCap " + TC3 / TC1);
		System.out.println("avg of initial states stdDev " + TC4 / TC1);
		System.out.println("avg of initial states capMed " + TC5 / TC1);
		System.out.println("replication percentage " + TC13 / TC7);
		System.out.println("crossover percentage " + TC8 / TC7);
		System.out.println("subtree mutation percentage " + TC9 / TC7);
		System.out.println("point mutation percentage " + TC10 / TC7);
		System.out.println("avg no of attacks " + TC6 / TC1);
		System.out.println("avg no of war initialization avg similarity "
				+ TC14 / GENERATIONS); // should calculate for war join as well
		System.out.print("END OF PROGRAM EXCUTION");
		System.exit(1);
	}

	private void sortDesc(char[][] strategy_init_2, char[][] strategy_init_3,
			char[][] strategy_init_4, char[][] strategy_join_3, double[] fitness) {
		// sort fitness and strategy arrays in fitness descending order

		char[][] tempstrategy_init_2 = new char[POPSIZE][];
		char[][] tempstrategy_init_3 = new char[POPSIZE][];
		char[][] tempstrategy_init_4 = new char[POPSIZE][];
		char[][] tempstrategy_join_3 = new char[POPSIZE][];
		for (int i = 0; i < POPSIZE; i++) {
			tempstrategy_init_2[i] = (char[]) strategy_init_2[i].clone();
			tempstrategy_init_3[i] = (char[]) strategy_init_3[i].clone();
			tempstrategy_init_4[i] = (char[]) strategy_init_4[i].clone();
			tempstrategy_join_3[i] = (char[]) strategy_join_3[i].clone();
		}

		double[] tempFitness = (double[]) fitness.clone();
		Arrays.sort(fitness);

		double temp;
		for (int i = 0; i < POPSIZE / 2; i++) {
			temp = fitness[i];
			fitness[i] = fitness[POPSIZE - 1 - i];
			fitness[POPSIZE - 1 - i] = temp;
		}

		for (int i = 0; i < POPSIZE; i++) {
			strategy_init_2[i] = null;
			strategy_init_3[i] = null;
			strategy_init_4[i] = null;
			strategy_join_3[i] = null;
			int j = 0;

			while (strategy_init_2[i] == null && j < POPSIZE) {
				if (fitness[i] == tempFitness[j]) {
					strategy_init_2[i] = (char[]) tempstrategy_init_2[j]
							.clone();
					strategy_init_3[i] = (char[]) tempstrategy_init_3[j]
							.clone();
					strategy_init_4[i] = (char[]) tempstrategy_init_4[j]
							.clone();
					strategy_join_3[i] = (char[]) tempstrategy_join_3[j]
							.clone();
					tempstrategy_init_2[j] = null;
					tempstrategy_init_3[j] = null;
					tempstrategy_init_4[j] = null;
					tempstrategy_join_3[j] = null;
					tempFitness[j] = -1e-5;
				}
				j++;
			}
		}

		GP_PilotStudy_v10.strategy_init_2 = strategy_init_2;
		GP_PilotStudy_v10.strategy_init_3 = strategy_init_3;
		GP_PilotStudy_v10.strategy_init_4 = strategy_init_4;
		GP_PilotStudy_v10.strategy_join_3 = strategy_join_3;
		this.fitness = fitness;

		sortedFit = true;
	}

	private void calcSimilarity(char[][] strategy_init_2,
			char[][] strategy_init_3, char[][] strategy_init_4,
			char[][] strategy_join_3, int indivs) {
		// calculate similarity of the best performing strategies in the
		// population
		// This method assumes that the arrays of strategies and fitness are
		// already sorted
		profile = new boolean[indivs][PROFILES];

		for (int i = 0; i < indivs; i++) {
			for (int j = 0; j < PROFILES / 2; j++)
				profile[i][j] = profilingWorlds[j].willItAttack(
						strategy_init_2[i], strategy_init_3[i],
						strategy_init_4[i], strategy_join_3[i]);
			if (MAXSYSTEM > 2)
				for (int j = PROFILES / 2; j < PROFILES; j++)
					profile[i][j] = profilingWorlds[j].willItAttack(
							strategy_init_2[i], strategy_init_3[i],
							strategy_init_4[i], strategy_join_3[i]);
		}

		if (indivs == 1) {
			similarityWarInit_2_Avg = 1;
			similarityWarInit_3_Avg = 1;
			similarityWarInit_4_Avg = 1;
			similarityWarJoinAvg = 1;
			return;
		}

		// Calculate profiles avg similarity
		similarityWarInit_2_Avg = 0;
		similarityWarInit_3_Avg = 0;
		similarityWarInit_4_Avg = 0;
		similarityWarJoinAvg = 0;
		double n = 0, m = 0, p = 0, q = 0;
		for (int i = 0; i < indivs - 1; i++)
			for (int j = i + 1; j < indivs; j++) {
				for (int k = 0; k < PROFILES / 2; k++) {
					if (profilingWorlds[k].currentStatesNum == 2) {
						n++;
						if (profile[i][k] == profile[j][k])
							similarityWarInit_2_Avg++;
					} else if (profilingWorlds[k].currentStatesNum == 3) {
						p++;
						if (profile[i][k] == profile[j][k])
							similarityWarInit_3_Avg++;
					} else if (profilingWorlds[k].currentStatesNum == 4) {
						q++;
						if (profile[i][k] == profile[j][k])
							similarityWarInit_4_Avg++;
					}
				}
				if (MAXSYSTEM > 2)
					for (int k = PROFILES / 2; k < PROFILES; k++) {
						m++;
						if (profile[i][k] == profile[j][k])
							similarityWarJoinAvg++;
					}
			}

		similarityWarInit_2_Avg /= n;
		similarityWarInit_3_Avg /= p;
		similarityWarInit_4_Avg /= q;
		similarityWarJoinAvg /= m;

	}

	void stats(double[] fitness, char[][] strategy_init_2,
			char[][] strategy_init_3, char[][] strategy_init_4,
			char[][] strategy_join_3, int gen) {
		if (!sortedFit) {
			System.out.println("Not Sorted!!!");
			System.exit(0);
		}

		double favgpop = 0;
		int node_count_1 = 0;
		int node_count_2 = 0;
		int node_count_3 = 0;
		int node_count_4 = 0;

		for (int i = 0; i < POPSIZE; i++) {
			favgpop += fitness[i];
			node_count_1 += traverse(strategy_init_2[i], 0);
			node_count_2 += traverse(strategy_init_3[i], 0);
			node_count_3 += traverse(strategy_init_4[i], 0);
			node_count_4 += traverse(strategy_join_3[i], 0);
		}
		favgpop /= POPSIZE;
		avg_len = (double) (node_count_1 + node_count_2 + node_count_3 + node_count_4)
				/ POPSIZE;

		calcSimilarity(strategy_init_2, strategy_init_3, strategy_init_4,
				strategy_join_3, BESTRETAINED);

		System.out.println("Generation=" + gen + "\nAvg Fitness=" + (favgpop)
				+ " Median Fitness=" + fitness[POPSIZE / 2] + " Best Fitness="
				+ fitness[0] + " Worst Fitness=" + fitness[POPSIZE - 1]
				+ "\nAvg Size=" + avg_len);

		System.out.println("Among the top " + BESTRETAINED + " i.e. the top "
				+ ((double) BESTRETAINED / POPSIZE * 100)
				+ "%\nwar 2 init similarity is " + similarityWarInit_2_Avg
				+ "\nwar 3 init similarity is " + similarityWarInit_3_Avg
				+ "\nwar 4 init similarity is " + similarityWarInit_4_Avg
				+ "\nwar join similarity is " + similarityWarJoinAvg);

		int totalAttacks = 0, totalAttacks_2 = 0, totalAttacks_3 = 0, totalAttacks_4 = 0, totalJoins = 0, m = 0, n = 0, o=0;
		double cap2 = 0, min_cap2 = 1E16, cap3 = 0, cap4 = 0, min_cap3 = 1E16, min_cap4 = 1E16, cap3_join = 0, min_cap3_join = 1E16;
		for (int i = 0; i < BESTRETAINED; i++) {
			for (int k = 0; k < PROFILES / 2; k++)
				if (profilingWorlds[k].currentStatesNum == 2) {
					m++;
					if (profile[i][k] == true) {
						totalAttacks++;
						totalAttacks_2++;
						cap2 += profilingWorlds[k].myCap;
						if (profilingWorlds[k].myCap < min_cap2)
							min_cap2 = profilingWorlds[k].myCap;
					}
				} else if (profilingWorlds[k].currentStatesNum == 3) {
					n++;
					if (profile[i][k] == true) {
						totalAttacks++;
						totalAttacks_3++;
						cap3 += profilingWorlds[k].myCap;
						if (profilingWorlds[k].myCap < min_cap3)
							min_cap3 = profilingWorlds[k].myCap;
					}
				} else if (profilingWorlds[k].currentStatesNum == 4){
					o++;
					if (profile[i][k] == true) {
						totalAttacks++;
						totalAttacks_4++;
						cap4 += profilingWorlds[k].myCap;
						if (profilingWorlds[k].myCap < min_cap4)
							min_cap4 = profilingWorlds[k].myCap;
					}
				}
			for (int k = PROFILES / 2; k < PROFILES; k++)
				if (profile[i][k] == true) {
					totalJoins++;
					cap3_join += profilingWorlds[k].myCap;
					if (profilingWorlds[k].myCap < min_cap3_join)
						min_cap3_join = profilingWorlds[k].myCap;
				}
		}

		System.out.println("\navg percentage of profile attacks is: "
				+ (double) totalAttacks * 100
				/ (BESTRETAINED * (int) (PROFILES / 2)) + "%");
		System.out.println("percentage of profile attacks 2 is: "
				+ (double) totalAttacks_2 * 100 / m + "%");
		System.out.println("percentage of profile attacks 3 is: "
				+ (double) totalAttacks_3 * 100 / n + "%");
		System.out.println("percentage of profile attacks 4 is: "
				+ (double) totalAttacks_4 * 100 / o + "%");
		System.out.println("And avg percentage of profile joins is: "
				+ (double) totalJoins
				/ (BESTRETAINED * (PROFILES - ((int) (PROFILES / 2)))) * 100
				+ "%");
		System.out.println("\navg cap of attackers in\n2 world " + cap2
				/ totalAttacks_2);
		System.out.println("3 world " + cap3 / totalAttacks_3);
		System.out.println("4 world " + cap4 / totalAttacks_4);
		System.out.println("3 world join " + cap3_join / totalJoins);
		System.out.println("\nmin cap of attackers in\n2 world " + min_cap2);
		System.out.println("3 world " + min_cap3);
		System.out.println("4 world " + min_cap4);
		System.out.println("3 world join " + min_cap3_join);

		if (PRINTINDIV) {
			System.out.println("\nBest Individual: ");
			System.out.print("Attack strategy: ");
			print_indiv(strategy_init_2[0], 0);
			System.out.print("\nJoining strategy: ");
			print_indiv(strategy_join_3[0], 0);
			System.out.println();
		}

		int bestAttacks = -1;
		int bestJoins = -1;

		if (PRINTPROFILE) {
			bestAttacks = 0;
			bestJoins = 0;
			System.out.println("It's attacking behavioral profile is:");
			for (int i = 0; i < PROFILES / 2; i++)
				if (profile[0][i] == true) {
					System.out.print("1");
					bestAttacks++;
				} else
					System.out.print("0");
			System.out.println();
			System.out.println("It's joining behavioral profile is:");
			for (int i = PROFILES / 2; i < PROFILES; i++)
				if (profile[0][i] == true) {
					System.out.print("1");
					bestJoins++;
				} else
					System.out.print("0");
			System.out.println();
		}

		if (bestAttacks == -1 && bestJoins == -1) {
			bestAttacks = 0;
			for (int i = 0; i < PROFILES / 2; i++)
				if (profile[0][i] == true) {
					bestAttacks++;
				}
			bestJoins = 0;
			for (int i = PROFILES / 2; i < PROFILES; i++)
				if (profile[0][i] == true) {
					bestJoins++;
				}
		}

		System.out.println("\nNumber of profile attacks by best is "
				+ bestAttacks);
		System.out.println("Number of profile attacks by prev best is "
				+ prevBestAttacks);
		prevBestAttacks = bestAttacks;
		System.out.println("Number of profile joins by best is " + bestJoins);
		System.out.println("Number of profile joins by prev best is "
				+ prevBestJoins);
		prevBestJoins = bestJoins;
		System.out.print("Profile differences from previous best is ");
		int differences = 0;
		for (int i = 0; i < PROFILES; i++)
			if (profile[0][i] != profilePrevBest[i])
				differences++;
		profilePrevBest = Arrays.copyOf(profile[0], PROFILES);
		System.out.print(differences);

		int curretBestHash_1 = strategy_init_2[0].hashCode();
		int curretBestHash_2 = strategy_join_3[0].hashCode();
		if (curretBestHash_1 == previousBestHash_1
				&& curretBestHash_2 == previousBestHash_2)
			System.out
					.print("\nProbably genotypically identical to previous!!");
		previousBestHash_1 = curretBestHash_1;
		previousBestHash_2 = curretBestHash_2;
		System.out.print("\n\n");
		System.out.flush();

		if (gen % 10 == 0) {
			System.out.println("Ten 2 similarity " + tenWI_2_Similarity);
			System.out.println("Ten 3 similarity " + tenWI_3_Similarity);
			System.out
					.println("Number of profile attacks by best 10 gen ago is "
							+ prev10BestAttacks);
			prev10BestAttacks = bestAttacks;
			tenWI_2_Similarity = similarityWarInit_2_Avg;
			tenWI_3_Similarity = similarityWarInit_3_Avg;
		}
		System.out
				.println("*******************************************************************");
		System.out.println();
	}

	double[] calculateFitness(char[][] strategy_init_2,
			char[][] strategy_init_3, char[][] strategy_init_4,
			char[][] strategy_join_3, int testCases) {
		// This method changes the order of the population arrays
		fitness = new double[POPSIZE];
		while (testCases > 0) {
			// Implementing Fisher–Yates shuffle of fitness and population
			// arrays
			int index;
			double tempFitness;
			char[] tempIndiv_1, tempIndiv_2, tempIndiv_3, tempIndiv_4;
			for (int i = POPSIZE - 1; i > 0; i--) {
				index = rd.nextInt(i + 1);
				tempFitness = fitness[index];
				fitness[index] = fitness[i];
				fitness[i] = tempFitness;

				tempIndiv_1 = strategy_init_2[index];
				strategy_init_2[index] = strategy_init_2[i];
				strategy_init_2[i] = tempIndiv_1;

				tempIndiv_2 = strategy_init_3[index];
				strategy_init_3[index] = strategy_init_3[i];
				strategy_init_3[i] = tempIndiv_2;

				tempIndiv_3 = strategy_init_4[index];
				strategy_init_4[index] = strategy_init_4[i];
				strategy_init_4[i] = tempIndiv_3;

				tempIndiv_4 = strategy_join_3[index];
				strategy_join_3[index] = strategy_join_3[i];
				strategy_join_3[i] = tempIndiv_4;
			}

			// Assign states into world systems. Each system is of a length
			// between minSystem and maxSystem. There must be remaining at least
			// minSystem number of states in the end for a system to be created.
			int counter = 0, length;
			while (counter < POPSIZE) {
				if (POPSIZE - counter <= MAXSYSTEM)
					length = POPSIZE - counter;
				else if (POPSIZE - counter < MAXSYSTEM + MINSYSTEM)
					length = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM);
				else
					length = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);

				char[][] teststrategy_init_2 = new char[length][];
				char[][] teststrategy_init_3 = new char[length][];
				char[][] teststrategy_init_4 = new char[length][];
				char[][] teststrategy_join_3 = new char[length][];
				for (int i = 0; i < length; i++) {
					teststrategy_init_2[i] = (char[]) strategy_init_2[counter
							+ i].clone();
					teststrategy_init_3[i] = (char[]) strategy_init_3[counter
							+ i].clone();
					teststrategy_init_4[i] = (char[]) strategy_init_4[counter
							+ i].clone();
					teststrategy_join_3[i] = (char[]) strategy_join_3[counter
							+ i].clone();
				}

				// generate randomly capabilities
				double[][] capList = new double[CAPCHANGE + 1][length];
				for (int h = 0; h <= CAPCHANGE; h++)
					for (int i = 0; i < length; i++)
						capList[h][i] = rd.nextDouble();

				for (int j = 0; j < length; j++) {
					WorldSystem_v10 world = new WorldSystem_v10(
							teststrategy_init_2, teststrategy_init_3,
							teststrategy_init_4, teststrategy_join_3, length,
							capList);
					boolean[] simResult = world.simulate();
					for (int i = 0; i < length; i++)
						if (simResult[i])
							fitness[counter + i]++;
					for (int h = 0; h <= CAPCHANGE; h++) {
						double capLast = capList[h][length - 1];
						for (int i = length - 1; i > 0; i--)
							capList[h][i] = capList[h][i - 1];
						capList[h][0] = capLast;
					}
				}

				counter += length;
			}

			testCases--;
		}

		if (BLOATFIGHT && X >= BLOATSTART) // punish longer strategies to fight
											// bloating
			for (int i = 0; i < POPSIZE; i++)
				fitness[i] -= (strategy_init_2[i].length + strategy_join_3[i].length) / 2;

		sortedFit = false;
		sortDesc(strategy_init_2, strategy_init_3, strategy_init_4,
				strategy_join_3, fitness);

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
		while ((parent2[xo2start] < FSET_2_START && parent1[xo1start] >= FSET_2_START)
				|| (parent2[xo2start] >= FSET_2_START && parent1[xo1start] < FSET_2_START))
			xo2start = rd.nextInt(len2);
		xo2end = traverse(parent2, xo2start);

		lenoff = xo1start + (xo2end - xo2start) + (len1 - xo1end);

		offspring = new char[lenoff];

		System.arraycopy(parent1, 0, offspring, 0, xo1start);
		System.arraycopy(parent2, xo2start, offspring, xo1start,
				(xo2end - xo2start));
		System.arraycopy(parent1, xo1end, offspring, xo1start
				+ (xo2end - xo2start), (len1 - xo1end));

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
						prim = (char) (TSET_START + rd.nextInt(TSET_END
								- TSET_START + 1));
					else
						prim = (char) rd.nextDouble();
					parentcopy[mutsite] = prim;
				} else
					switch (parentcopy[mutsite]) {
					case ADD:
					case SUB:
					case MUL:
					case DIV:
						parentcopy[mutsite] = (char) (rd.nextInt(FSET_1_END
								- FSET_1_START + 1) + FSET_1_START);
						break;
					case GT:
						parentcopy[mutsite] = (char) (rd.nextInt(FSET_2_END
								- FSET_2_START + 1) + FSET_2_START);
						;
						break;
					case LT:
						parentcopy[mutsite] = (char) (rd.nextInt(FSET_2_END
								- FSET_2_START + 1) + FSET_2_START);
						;
						break;
					case EQ:
						parentcopy[mutsite] = (char) (rd.nextInt(FSET_2_END
								- FSET_2_START + 1) + FSET_2_START);
						;
						break;
					case AND:
						parentcopy[mutsite] = OR;
						break;
					case OR:
						parentcopy[mutsite] = AND;
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
		// the new subtree returns boolean as well. The new subtree cannot be
		// more
		// than one level deeper than the replaced one
		if (parent[mutStart] >= FSET_2_START)
			newSubtree = grow(1 + rd.nextInt((int) (Math.log(2 + mutEnd
					- mutStart) / Math.log(2))), true);
		else
			newSubtree = grow(rd.nextInt(1 + (int) (Math.log(2 + mutEnd
					- mutStart) / Math.log(2))), false);

		subtreeLen = traverse(newSubtree, 0);

		lenOff = mutStart + subtreeLen + (parentLen - mutEnd);

		offspring = new char[lenOff];

		System.arraycopy(parent, 0, offspring, 0, mutStart);
		System.arraycopy(newSubtree, 0, offspring, mutStart, subtreeLen);
		System.arraycopy(parent, mutEnd, offspring, (mutStart + subtreeLen),
				(parentLen - mutEnd));

		return (offspring);
	}

	int traverse(char[] buffer, int buffercount) {
		if (buffer[buffercount] < FSET_1_START)
			return (++buffercount);

		switch (buffer[buffercount]) {
		case ADD:
		case SUB:
		case MUL:
		case DIV:
		case GT:
		case LT:
		case EQ:
		case AND:
		case OR:
			return (traverse(buffer, traverse(buffer, ++buffercount)));
		}
		return (0); // should never get here
	}

	static int print_indiv(char[] buffer, int buffercounter) {
		int a1 = 0, a2;
		if (buffer[buffercounter] < FSET_1_START) {
			switch (buffer[buffercounter]) {
			case CAPMED:
				System.out.print("CAP_MED");
				break;
			case CAPMIN:
				System.out.print("CAP_MIN");
				break;
			case CAPMAX:
				System.out.print("CAP_MAX");
				break;
			case MYCAP:
				System.out.print("MY_CAP");
				break;
			case OPPCAP:
				System.out.print("OPP_CAP");
				break;
			case MYSIDECAP:
				System.out.print("MY_SIDE_CAP");
				break;
			case LEFTCAPSUM:
				System.out.print("LEFT_CAP_SUM");
				break;
			default:
				System.out.print(randNum[buffer[buffercounter]]);
				break;
			}
			return (++buffercounter);
		}

		switch (buffer[buffercounter]) {
		case ADD:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" + ");
			break;
		case SUB:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" - ");
			break;
		case MUL:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" * ");
			break;
		case DIV:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" / ");
			break;
		case GT:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" > ");
			break;
		case LT:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" < ");
			break;
		case EQ:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" = ");
			break;
		case AND:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" AND ");
			break;
		case OR:
			System.out.print("(");
			a1 = print_indiv(buffer, ++buffercounter);
			System.out.print(" OR ");
			break;
		}
		a2 = print_indiv(buffer, a1);
		System.out.print(")");
		return (a2);
	}

}
