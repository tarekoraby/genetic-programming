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
import java.io.*;
import java.text.DecimalFormat;

public class GP_PilotStudy_v6 {
	double[] fitness;
	static double[] randNum;
	static char[][] strategy_init_2, strategy_2;
	static Random rd = new Random();
	static final int MAX_LEN = 10000, POPSIZE = 1002, DEPTH = 4, GENERATIONS = 10000, TSIZE = 2, BESTRETAINED = 50,
			TESTCASES = 1000, PROFILES = 50, MINSYSTEM = 3, MAXSYSTEM = 3, RANDOMNUMBERS = 50;
	static double MINRANDOM = 0, MAXRANDOM = 1;
	public static final double PMUT_PER_NODE = 0.05, REPLICATION_PROB = 0.1, CROSSOVER_PROB = 0.35, SUBTREE_MUT_PROB = 0.35,
			PMUT_PROB = 0.1, ABIOGENSIS_PROB = 0.1;
	static final int CAPMED = RANDOMNUMBERS, CAPMIN = CAPMED + 1, CAPMAX = CAPMED + 2, MYCAP = CAPMED + 3,
			OPPCAP = CAPMED + 4, MYSIDECAP = CAPMED + 5, LEFTCAPSUM = CAPMED + 6, ADD = LEFTCAPSUM + 1, SUB = ADD + 1,
			MUL = ADD + 2, DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7, OR = ADD + 8,
			TSET_START = CAPMED, TSET_END = LEFTCAPSUM, FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT,
			FSET_2_END = EQ, FSET_3_START = AND, FSET_3_END = OR;
	static char[] program;
	static int prevBestAttacks, prev10BestAttacks, prevBestJoins;
	static double avg_len, similarityWarInitAvg, similarityWarJoinAvg, tenWISimilarity, tenWJSimilarity;
	static double[][] targets;
	static final boolean BLOATFIGHT = false, PRINTINDIV = false, PRINTPROFILE = false;
	static final int CAPCHANGE = 1;
	World_System_Shell_v6[] profilingWorlds;
	boolean[][] profile;
	boolean[][] profile10;
	boolean[] profilePrevBest;
	int previousBestHash_1, previousBestHash_2;
	boolean sortedFit;
	
	static double TC1, TC2, TC3, TC4, TC5, TC6, TC7, TC8, TC9, TC10, TC11, TC12, TC13, TC14, TC15, TC16, TC17, TC18;

	public static void main(String[] args) {

		GP_PilotStudy_v6 gp = new GP_PilotStudy_v6();
		gp.evolve();
		
	}

	public GP_PilotStudy_v6() {
		System.out.println("START OF PROGRAM");
		if (DEPTH < 1) {
			System.out.println("Cannot excute!! Minimum depth of intitial individuals is 1");
			System.exit(0);
		}
		if (1 - Math.abs(REPLICATION_PROB + CROSSOVER_PROB + SUBTREE_MUT_PROB + PMUT_PROB + ABIOGENSIS_PROB) > 1E-10){
			System.out.println("Probabilities don't add up to 1");
			System.exit(0);
		}
		if (MINSYSTEM>=MAXSYSTEM){
			System.out.println("Warning !!!! MAXSYSTEM is less than or equal MINSYSTEM");
		}
			
			
		fitness = new double[POPSIZE];
		randNum=new double[RANDOMNUMBERS];
		for (int i = 0; i < RANDOMNUMBERS; i++)
			randNum[i] = (MAXRANDOM - MINRANDOM) * rd.nextDouble() + MINRANDOM;

		profilingWorlds = new World_System_Shell_v6[PROFILES];
		for (int i = 0; i < PROFILES / 2; i++)
			profilingWorlds[i] = new World_System_Shell_v6(false);
		if (MAXSYSTEM > 2)
			for (int i = PROFILES / 2; i < PROFILES; i++)
				profilingWorlds[i] = new World_System_Shell_v6(true);
		
		strategy_init_2 = create_random_pop(POPSIZE, DEPTH, fitness);
		strategy_2 = create_random_pop(POPSIZE, DEPTH, fitness);
		
		profile = new boolean[BESTRETAINED][PROFILES];
		profile10 = new boolean[BESTRETAINED][PROFILES];
		profilePrevBest=new boolean[PROFILES];
		
		sortedFit=false;
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
		char[][] tempstrategy_init_2, tempStrategy_2;
			
		fitness = calculateFitness(strategy_init_2, strategy_2,  TESTCASES);
		stats( fitness, strategy_init_2, strategy_2, 0 );
				
		for (int gen = 1; gen <= GENERATIONS; gen++) { 
			TC11=0; // Num of worlds
			TC12=0; // Num of attacks
			
			tempstrategy_init_2 = new char[POPSIZE][];
			tempStrategy_2 = new char[POPSIZE][];
			
			// Keep the best strategies in the new population
			if (sortedFit) {
				System.arraycopy(strategy_init_2, 0, tempstrategy_init_2, 0, BESTRETAINED);
				System.arraycopy(strategy_2, 0, tempStrategy_2, 0, BESTRETAINED);
			} else {
				System.out.println("Not sorted!!");
				System.exit(0);
			}
			

			// For the remaining slots in the new population probabilistically
			// perform replication, crossover, subtree mutation or point mutation
			// The evolutionary operations acts independently for both strategies
			for (indivs = BESTRETAINED; indivs < POPSIZE; indivs++) {
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
					newind = crossover(strategy_init_2[parent1], strategy_init_2[parent2]);
					tempstrategy_init_2[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(strategy_init_2[parent]);
					tempstrategy_init_2[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(strategy_init_2[parent], PMUT_PER_NODE);
					tempstrategy_init_2[indivs] = newind;
				} else {
					tempstrategy_init_2[indivs] = create_random_indiv(DEPTH);
				}
			}
			
			//only consider the joining strategy if the maximum no of states is greater than 2

			for (indivs = BESTRETAINED; indivs < POPSIZE; indivs++) {
				// TC7++;
				char[] newind;
				double random = rd.nextDouble();
				if (random < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempStrategy_2[indivs] = strategy_2[parent];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(strategy_2[parent1], strategy_2[parent2]);
					tempStrategy_2[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(strategy_2[parent]);
					tempStrategy_2[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(strategy_2[parent], PMUT_PER_NODE);
					tempStrategy_2[indivs] = newind;
				} else {
					tempStrategy_2[indivs] = create_random_indiv(DEPTH);
				}
			}

			strategy_init_2 = tempstrategy_init_2;
			strategy_2 = tempStrategy_2;

			fitness = calculateFitness(strategy_init_2, strategy_2, TESTCASES);
			stats(fitness, strategy_init_2, strategy_2, gen);

			TC14 += similarityWarInitAvg;
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
		System.out.println("avg no of war initialization avg similarity " + TC14 / GENERATIONS); // should calculate for war join as well
		System.out.print("END OF PROGRAM EXCUTION");
		System.exit(1);
	}

	private void sortDesc(char[][] strategy_init_2, char[][] strategy_2, double[] fitness) {
		// sort fitness and strategy arrays in fitness descending order
		
		char[][] tempstrategy_init_2 = new char[POPSIZE][];
		char[][] tempStrategy_2 = new char[POPSIZE][];
		for (int i = 0; i < POPSIZE; i++) {
			tempstrategy_init_2[i] = strategy_init_2[i].clone();
			tempStrategy_2[i] = strategy_2[i].clone();
		}

		double[] tempFitness = fitness.clone();
		Arrays.sort(fitness);
		
		double temp;
		for (int i = 0; i < POPSIZE / 2; i++) {
			temp = fitness[i];
			fitness[i] = fitness[POPSIZE - 1 - i];
			fitness[POPSIZE - 1 - i] = temp;
		}

		for (int i = 0; i < POPSIZE; i++) {
			strategy_init_2[i] = null;
			strategy_2[i] = null;
			int j = 0;

			while (strategy_init_2[i] == null && j < POPSIZE) {
				if (fitness[i] == tempFitness[j]) {
					strategy_init_2[i] = tempstrategy_init_2[j].clone();
					strategy_2[i] = tempStrategy_2[j].clone();
					tempstrategy_init_2[j] = null;
					tempStrategy_2[j] = null;
					tempFitness[j] = -1e-5;
				}
				j++;
			}
		}

		GP_PilotStudy_v6.strategy_init_2 = strategy_init_2;
		GP_PilotStudy_v6.strategy_2 = strategy_2;
		this.fitness = fitness;
		
		sortedFit=true;
	}

	private void calcSimilarity(char[][] strategy_init_2, char[][] strategy_2, int indivs) {
		//calculate similarity of the best performing strategies in the population
		//This method assumes that the arrays of strategies and fitness are already sorted
		profile = new boolean[indivs][PROFILES];
		
		for (int i = 0; i < indivs; i++){
			for (int j = 0; j < PROFILES / 2; j++)
				profile[i][j] = profilingWorlds[j].willItAttack(strategy_init_2[i], strategy_2[i]);
			if (MAXSYSTEM > 2)
				for (int j = PROFILES / 2; j < PROFILES; j++)
					profile[i][j] = profilingWorlds[j].willItAttack(strategy_init_2[i], strategy_2[i]);
		}

		if (indivs == 1) {
			similarityWarInitAvg = 1;
			similarityWarJoinAvg = 1;
			return;
		}

	
		// Calculate profiles avg similarity
		similarityWarInitAvg = 0;
		similarityWarJoinAvg = 0;
		double n = 0, m = 0;
		for (int i = 0; i < indivs - 1; i++)
			for (int j = i + 1; j < indivs; j++) {
				for (int k = 0; k < PROFILES / 2; k++) {
					n++;
					if (profile[i][k] == profile[j][k])
						similarityWarInitAvg++;
				}
				if (MAXSYSTEM > 2)
					for (int k = PROFILES / 2; k < PROFILES / 2; k++) {
						m++;
						if (profile[i][k] == profile[j][k])
							similarityWarJoinAvg++;
					}
			}

		similarityWarInitAvg /= n;
		similarityWarJoinAvg /= m;

	}

	void stats(double[] fitness, char[][] strategy_init_2, char[][] strategy_2, int gen) {
		if(!sortedFit){
			System.out.println("Not Sorted!!!");
			System.exit(0);
		}
			
		double favgpop = 0;
		int node_count_1 = 0;
		int node_count_2 = 0;
		for (int i = 0; i < POPSIZE; i++) {
			favgpop += fitness[i];
			node_count_1 += traverse(strategy_init_2[i], 0);
			node_count_2 += traverse(strategy_2[i], 0);
		}
		favgpop /= POPSIZE;
		avg_len = (double) (node_count_1 + node_count_2) / POPSIZE; 
		
		calcSimilarity(strategy_init_2, strategy_2, BESTRETAINED);

		System.out.println("Generation=" + gen + "\nAvg Fitness=" + (favgpop) + " Median Fitness="
				+ fitness[POPSIZE / 2] + " Best Fitness=" + fitness[0] + " Worst Fitness=" + fitness[POPSIZE - 1]
				+ "\nAvg Size=" + avg_len );
		
		System.out.println("Avg top " + ((double) BESTRETAINED / POPSIZE * 100)
				+ "% war init similarity is " + similarityWarInitAvg + " and war joining similarity is " + similarityWarJoinAvg);		
		
		
		int totalAttacks = 0;
		int totalJoins = 0;
		for (int i = 0; i < BESTRETAINED; i++) {
			for (int k = 0; k < PROFILES / 2; k++)
				if (profile[i][k] == true)
					totalAttacks++;
			for (int k = PROFILES / 2; k < PROFILES; k++)
				if (profile[i][k] == true)
					totalJoins++;
		}
		
		System.out.println("\nAmong the top " + ((double) BESTRETAINED / POPSIZE * 100)
				+ "%, avg percentage of profile attacks is: "
				+ (double) totalAttacks * 100 / (BESTRETAINED * (int) (PROFILES / 2))  + "%");
		System.out.println("And avg percentage of profile joins is: " + (double) totalJoins / (BESTRETAINED * (PROFILES - ((int) (PROFILES / 2)))) * 100 + "%");

		if (PRINTINDIV) {
			System.out.println("\nBest Individual: ");
			System.out.print("Attack strategy: ");
			print_indiv(strategy_init_2[0], 0);
			System.out.print("\nJoining strategy: ");
			print_indiv(strategy_2[0], 0);
			System.out.println();
		}

		int bestAttacks = -1;
		int bestJoins = -1;

		if (PRINTPROFILE) {
			bestAttacks = 0;
			bestJoins = 0;
			System.out.println("It's attacking behavioral profile is:");
			for (int i = 0; i < PROFILES/2; i++)
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
		
		

		System.out.println("\nNumber of profile attacks by best is " + bestAttacks);
		System.out.println("Number of profile attacks by prev best is " + prevBestAttacks);
		prevBestAttacks = bestAttacks;
		System.out.println("Number of profile joins by best is " + bestJoins);
		System.out.println("Number of profile joins by prev best is " + prevBestJoins);
		prevBestJoins = bestJoins;
		System.out.print("Profile differences from previous best is ");
		int differences = 0;
		for (int i = 0; i < PROFILES; i++)
			if (profile[0][i] != profilePrevBest[i])
				differences++;
		profilePrevBest = Arrays.copyOf(profile[0], PROFILES);
		System.out.print(differences);
		
		
		int curretBestHash_1 = strategy_init_2[0].hashCode();
		int curretBestHash_2 = strategy_2[0].hashCode();
		if (curretBestHash_1 == previousBestHash_1 && curretBestHash_2 == previousBestHash_2)
			System.out.print("\nProbably genotypically identical to previous!!");
		previousBestHash_1 = curretBestHash_1;
		previousBestHash_2 = curretBestHash_2;
		System.out.print("\n\n");
		System.out.flush();

		if (gen % 10 == 0 ) {
			System.out.println("Ten similarity " + tenWISimilarity);
			System.out.println("Number of profile attacks by best 10 gen ago is " + prev10BestAttacks);
			prev10BestAttacks = bestAttacks;
			tenWISimilarity = similarityWarInitAvg;
		}
		
		System.out.println();
	}

	double[] calculateFitness(char[][] strategy_init_2, char[][] strategy_2, int testCases) {
		// This method changes the order of the population arrays
		fitness = new double[POPSIZE];
		while (testCases > 0) {
			// Implementing Fisher–Yates shuffle of fitness and population
			// arrays
			int index;
			double tempFitness;
			char[] tempIndiv_1, tempIndiv_2;
			for (int i = POPSIZE - 1; i > 0; i--) {
				index = rd.nextInt(i + 1);
				tempFitness = fitness[index];
				fitness[index] = fitness[i];
				fitness[i] = tempFitness;

				tempIndiv_1 = strategy_init_2[index];
				strategy_init_2[index] = strategy_init_2[i];
				strategy_init_2[i] = tempIndiv_1;

				tempIndiv_2 = strategy_2[index];
				strategy_2[index] = strategy_2[i];
				strategy_2[i] = tempIndiv_2;
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
				char[][] testStrategy_2 = new char[length][];
				for (int i = 0; i < length; i++) {
					teststrategy_init_2[i] = strategy_init_2[counter + i].clone();
					testStrategy_2[i] = strategy_2[counter + i].clone();
				}

				WorldSystem_v6 world = new WorldSystem_v6(teststrategy_init_2, testStrategy_2, length);
				boolean[] simResult = world.simulate();
				for (int i = 0; i < length; i++)
					if (simResult[i])
						fitness[counter + i]++;

				counter += length;
			}

			testCases--;
		}

		if (BLOATFIGHT) // punish longer strategies to fight bloating
			for (int i = 0; i < POPSIZE; i++)
				fitness[i] -= (strategy_init_2[i].length + strategy_2[i].length) / 2;

		
		
		sortedFit=false;
		sortDesc(strategy_init_2, strategy_2, fitness);
		
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

