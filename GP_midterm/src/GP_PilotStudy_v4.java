/* Based on tiny_gp.java by Riccardo Poli (email: rpoli@essex.ac.uk)
 * 
 */
 
/* TO DO
 * CHECK THAT TRAVEREE IS NECESSARY INSTEAD OF JUST USING ARRAY LENGTH
 * Simplify the expressions, perhaps every few generations to fight bloat
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

public class GP_PilotStudy_v4 {
	double[] fitness;
	static double[] randNum;
	char[][] strategy_1, strategy_2, FT_strategy_1, FT_strategy_2;
	static Random rd = new Random();
	static final int MAX_LEN = 10000, POPSIZE = 10000, DEPTH = 5, GENERATIONS = 10000, TSIZE = 2, TESTCASES = 100,
			PROFILES = 100, MINSYSTEM = 2, MAXSYSTEM = 2, RANDOMNUMBERS = 50;
	static double MINRANDOM = 0, MAXRANDOM = 1;
	public static final double PMUT_PER_NODE = 0.05, REPLICATION_PROB = 0, CROSSOVER_PROB = 0.3, SUBTREE_MUT_PROB = 0.3,
			PMUT_PROB = 0.1, ABIOGENSIS_PROB = 0.3;
	static final int CAPMED = RANDOMNUMBERS, CAPMIN = CAPMED + 1, CAPMAX = CAPMED + 2, MYCAP = CAPMED + 3,
			OPPCAP = CAPMED + 4, MYSIDECAP = CAPMED + 5, LEFTCAPSUM = CAPMED + 6, ADD = LEFTCAPSUM + 1, SUB = ADD + 1,
			MUL = ADD + 2, DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7, OR = ADD + 8,
			TSET_START = CAPMED, TSET_END = LEFTCAPSUM, FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT,
			FSET_2_END = EQ, FSET_3_START = AND, FSET_3_END = OR;
	static char[] program;
	static int PC, prevBestAttacks, prev10BestAttacks, prevBestJoins;
	static double avg_len, similarityAvg, tenSimilarity;
	static double[][] targets;
	static final boolean BLOATFIGHT = false, PRINTINDIV = true, PRINTPROFILE = true;
	static final int CAPCHANGE = 1;
	World_System_Shell_v4[] profilingWorlds;
	boolean[][] profile;
	boolean[][] profile10;
	boolean[] profilePrevBest;
	int previousBestHash_1, previousBestHash_2;
	
	static double TC1, TC2, TC3, TC4, TC5, TC6, TC7, TC8, TC9, TC10, TC11, TC12, TC13, TC14, TC15, TC16, TC17, TC18;

	public static void main(String[] args) {

		GP_PilotStudy_v4 gp = new GP_PilotStudy_v4();
		gp.evolve();
		
	}

	public GP_PilotStudy_v4() {
		System.out.println("START OF PROGRAM");
		if (DEPTH < 1) {
			System.out.println("Cannot excute!! Minimum depth of intitial individuals is 1");
			System.exit(0);
		}
		if (REPLICATION_PROB + CROSSOVER_PROB + SUBTREE_MUT_PROB + PMUT_PROB + ABIOGENSIS_PROB != 1){
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
		
		profilingWorlds = new World_System_Shell_v4[PROFILES/2];
		for (int i = 0; i < PROFILES/2; i++)
			profilingWorlds[i]=new World_System_Shell_v4(false);
		//for (int i = PROFILES/2; i < PROFILES; i++)
			//profilingWorlds[i]=new World_System_Shell_v4(true);

		
		strategy_1 = create_random_pop(POPSIZE, DEPTH, fitness);
		strategy_2 = create_random_pop(POPSIZE, DEPTH, fitness);
		
		profilePrevBest=new boolean[PROFILES];
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
		int indivs, parent1, parent2, parent, numBestStratgies;
		double fitBest;
		char[][] tempStrategy_1, tempStrategy_2;
		// for the first round take the first three randomly generated strategies as the test strategies
		FT_strategy_1 = new char[3][];
		FT_strategy_2 = new char[3][];
		for (int i = 0; i < 3; i++) {
			FT_strategy_1[i] = strategy_1[i].clone();
			FT_strategy_2[i] = strategy_2[i].clone();
		}
		
		fitness = calculateFitness(strategy_1, strategy_2, FT_strategy_1, FT_strategy_2, TESTCASES);
		setupNewTestStrategies();
		stats( fitness, strategy_1, strategy_2, 0 );

		
		
		
		for (int gen = 1; gen <= GENERATIONS; gen++) { 
			TC11=0; // Num of worlds
			TC12=0; // Num of attacks
			

			
			tempStrategy_1 = new char[POPSIZE][];
			tempStrategy_2 = new char[POPSIZE][];
			
			//Keep the best strategies in the new population
			System.arraycopy(FT_strategy_1, 0, tempStrategy_1, 0, FT_strategy_1.length);
			System.arraycopy(FT_strategy_2, 0, tempStrategy_2, 0, FT_strategy_2.length);



			// For the remaining slots in the new population probabilistically
			// perform replication, crossover, subtree mutation or point mutation
			// The evolutionary operations acts independently for both strategies
			for (indivs = FT_strategy_1.length; indivs < POPSIZE; indivs++) {
				TC7++;
				char[] newind;
				double random = rd.nextDouble();
				if (random < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempStrategy_1[indivs] = strategy_1[parent];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(strategy_1[parent1], strategy_1[parent2]);
					tempStrategy_1[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(strategy_1[parent]);
					tempStrategy_1[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB + SUBTREE_MUT_PROB)) < PMUT_PROB) {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(strategy_1[parent], PMUT_PER_NODE);
					tempStrategy_1[indivs] = newind;
				} else {
					tempStrategy_1[indivs] = create_random_indiv(DEPTH);
				}
			}
			
			for (indivs = FT_strategy_1.length; indivs < POPSIZE; indivs++) {
				//TC7++;
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
				}
				else {
					tempStrategy_2[indivs] = create_random_indiv(DEPTH);
				}
			}
			
			
			
			strategy_1 = tempStrategy_1;
			strategy_2 = tempStrategy_2;
			
			fitness = calculateFitness(strategy_1, strategy_2, FT_strategy_1, FT_strategy_2, TESTCASES);
			setupNewTestStrategies();
			stats( fitness, strategy_1, strategy_2, gen );
			
			TC14 += similarityAvg;
		}
		System.out.println("number of created worlds is: " + (int)TC1);
		System.out.println("avg state num is " + (TC2 / TC1));
		System.out.println("avg of initial states avgCap " + TC3 / TC1);
		System.out.println("avg of initial states stdDev " + TC4 / TC1);
		System.out.println("avg of initial states capMed " + TC5 / TC1);
		System.out.println("replication percentage " + TC13 / TC7);
		System.out.println("crossover percentage " + TC8 / TC7);
		System.out.println("subtree mutation percentage " + TC9 / TC7);
		System.out.println("point mutation percentage " + TC10 / TC7);
		System.out.println("avg no of attacks " + TC6 / TC1);
		System.out.println("avg no of avg similarity " + TC14 / GENERATIONS);
		System.out.print("END OF PROGRAM EXCUTION");
		System.exit(1);
	}

	private void setupNewTestStrategies() {
		
		sortDesc(strategy_1, strategy_2, fitness);
		// assumes that the arrays are already sorted
		int bestStratgiesNum, uniqueBestStratgiesNum;

		double fitBest = fitness[0];
		// calculate the number of best strategies
		bestStratgiesNum = 1;
		while (bestStratgiesNum < POPSIZE && fitness[bestStratgiesNum] == fitBest) {
			bestStratgiesNum++;
		}

		// copy the strategies with the best fitness to a new temp array
		char[][] tempStrategy_1 = new char[bestStratgiesNum][];
		char[][] tempStrategy_2 = new char[bestStratgiesNum][];
		for (int i = 0; i < bestStratgiesNum; i++) {
			tempStrategy_1[i] = strategy_1[i].clone();
			tempStrategy_2[i] = strategy_2[i].clone();
		}

		// calculate the profiles of the best strategies
		profile = new boolean[bestStratgiesNum][PROFILES/2];
		for (int i = 0; i < bestStratgiesNum; i++)
			for (int j = 0; j < PROFILES/2; j++) {
				profile[i][j] = profilingWorlds[j].willItAttack(tempStrategy_1[i], tempStrategy_2[i]);
			}
		
		// delete strategies with the same profile from the temp array (set to
		// null)
		// keep the shorter strategy if both have the same profile
		int i=0;
		do {
			if (tempStrategy_1[i] != null)
				for (int j = i + 1; j < bestStratgiesNum; j++) {
					if (tempStrategy_1[j] == null)
						continue;
					if (Arrays.equals(profile[i], profile[j])) {
						if (tempStrategy_1[i].length + tempStrategy_2[i].length < tempStrategy_1[j].length
								+ tempStrategy_2[j].length)
							tempStrategy_1[j] = null;
						else {
							tempStrategy_1[i] = null;
							break;
						}
					}
				}
			i++;
		} while (i < bestStratgiesNum - 1);
		
		

		// count the number of unique strategies in temp array
		uniqueBestStratgiesNum = 0;
		for (i = 0; i < bestStratgiesNum ; i++){
			if (tempStrategy_1[i] != null)
				uniqueBestStratgiesNum++;
		}

		// create new test strategy arrays and copy the unqie stratgies to them
		

		FT_strategy_1 = new char[uniqueBestStratgiesNum][];
		FT_strategy_2 = new char[uniqueBestStratgiesNum][];
		int counter = 0;
		for (i = 0; i < uniqueBestStratgiesNum; i++) {
			while (tempStrategy_1[counter] == null)
				counter++;
			FT_strategy_1[i] = tempStrategy_1[counter].clone();
			FT_strategy_2[i] = tempStrategy_1[counter].clone();
			counter++;
		}

		
	}

	private void sortDesc(char[][] strategy_1, char[][] strategy_2, double[] fitness) {
		// sort fitness and strategy arrays in fitness descending order
		
		char[][] tempStrategy_1 = new char[POPSIZE][];
		char[][] tempStrategy_2 = new char[POPSIZE][];
		for (int i = 0; i < POPSIZE; i++) {
			tempStrategy_1[i] = strategy_1[i].clone();
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
			strategy_1[i] = null;
			strategy_2[i] = null;
			int j = 0;

			while (strategy_1[i] == null && j < POPSIZE) {
				if (fitness[i] == tempFitness[j]) {
					strategy_1[i] = tempStrategy_1[j].clone();
					strategy_2[i] = tempStrategy_2[j].clone();
					tempStrategy_1[j] = null;
					tempStrategy_2[j] = null;
					tempFitness[j] = -1e-5;
				}
				j++;
			}
		}

		this.strategy_1 = strategy_1;
		this.strategy_2 = strategy_2;
		this.fitness = fitness;
	}

	private void calcSimilarity(char[][] FT_strategy_1, char[][] FT_strategy_2, int gen) {
		//calculate similarity of the best performing strategies in the population
		//This method assumes that the arrays of strategies and fitness are already sorted
		profile = new boolean[FT_strategy_1.length][PROFILES];
		
		if (FT_strategy_1.length == 1) {
			for (int j = 0; j < PROFILES/2; j++)
				profile[0][j] = profilingWorlds[j].willItAttack(FT_strategy_1[0], FT_strategy_2[0]);
			similarityAvg = 1;
			return;
		}

		for (int i = 0; i < FT_strategy_1.length; i++)
			for (int j = 0; j < PROFILES/2; j++)
				profile[i][j] = profilingWorlds[j].willItAttack(FT_strategy_1[i], FT_strategy_2[i]);
				
		// Calculate profiles avg similarity
		similarityAvg = 0;
		double n = 0;
		for (int i = 0; i < FT_strategy_1.length - 1; i++)
			for (int j = i + 1; j < FT_strategy_1.length; j++)
				for (int k = 0; k < PROFILES/2; k++) {
					n++;
					if (profile[i][k] == profile[j][k])
						similarityAvg++;
				}

		similarityAvg /= n;

	}

	void stats(double[] fitness, char[][] strategy_1, char[][] strategy_2, int gen) {
		double favgpop = 0;
		int node_count_1 = 0;
		int node_count_2 = 0;
		for (int i = 0; i < POPSIZE; i++) {
			favgpop += fitness[i];
			node_count_1 += traverse(strategy_1[i], 0);
			node_count_2 += traverse(strategy_2[i], 0);
		}
		favgpop /= POPSIZE;
		avg_len = (double) (node_count_1 + node_count_2) / POPSIZE; 
		
		calcSimilarity(FT_strategy_1, FT_strategy_2, gen);

		System.out.println("Generation=" + gen + "\nAvg Fitness=" + (favgpop) + " Median Fitness="
				+ fitness[POPSIZE / 2] + " Best Fitness=" + fitness[0] + " Worst Fitness=" + fitness[POPSIZE - 1]
				+ "\nAvg Size=" + avg_len );
		System.out.println("number of unique best stratgies " + FT_strategy_1.length  + " and their similarity is " +   similarityAvg);		
		
		
				
		
		int totalAttacks = 0;
		int totalJoins = 0;
		for (int i = 0; i < FT_strategy_1.length; i++) {
			for (int k = 0; k < PROFILES / 2; k++)
				if (profile[i][k] == true)
					totalAttacks++;
			for (int k = PROFILES / 2; k < PROFILES; k++)
				if (profile[i][k] == true)
					totalJoins++;
		}
		
		System.out.println("\nAmong the top " + ((double) FT_strategy_1.length / POPSIZE * 100)
				+ "%, avg percentage of profile attacks is: "
				+ (double) totalAttacks * 100 / (FT_strategy_1.length * (int) (PROFILES / 2))  + "%");
		System.out.println("And avg percentage of profile joins is: " + (double) totalJoins / (FT_strategy_1.length * (PROFILES - ((int) (PROFILES / 2)))) * 100 + "%");

		if (PRINTINDIV) {
			System.out.println("\nBest Individual: ");
			System.out.print("Attack strategy: ");
			print_indiv(FT_strategy_1[0], 0);
			System.out.print("\nJoining strategy: ");
			print_indiv(FT_strategy_2[0], 0);
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
		
		
		int curretBestHash_1 = FT_strategy_1[0].hashCode();
		int curretBestHash_2 = FT_strategy_2[0].hashCode();
		if (curretBestHash_1 == previousBestHash_1 && curretBestHash_2 == previousBestHash_2)
			System.out.print("\nProbably genotypically identical to previous!!");
		previousBestHash_1 = curretBestHash_1;
		previousBestHash_2 = curretBestHash_2;
		System.out.print("\n\n");
		System.out.flush();

		if (gen % 10 == 0 ) {
			System.out.println("Ten similarity " + tenSimilarity);
			System.out.println("Number of profile attacks by best 10 gen ago is " + prev10BestAttacks);
			prev10BestAttacks = bestAttacks;
		}
		
		System.out.println();
	}

	double[] calculateFitness(char[][] strategy_1, char[][] strategy_2, char[][] FT_strategy_1, char[][] FT_strategy_2, int testCases) {
		// This method changes the order of the population array
		fitness = new double[POPSIZE];
		int length, index;
		WorldSystem_v4 world;
		for (int i=0; i<POPSIZE; i++){
			for (int test = 0; test < testCases; test++){
				length = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
				char[][] testStrategy_1 = new char[length][];
				char[][] testStrategy_2 = new char[length][];
				testStrategy_1[0] = strategy_1[i].clone();
				testStrategy_2[0] = strategy_2[i].clone();

				for (int k = 1; k < length; k++) {
					index = rd.nextInt(FT_strategy_1.length);
					testStrategy_1[k] = FT_strategy_1[index].clone();
					testStrategy_2[k] = FT_strategy_2[index].clone();
				}

				world = new WorldSystem_v4(testStrategy_1, testStrategy_2, length);
				boolean survived = world.simulate();
				if (survived)
						fitness[i]++;
			}
		}

		if (BLOATFIGHT) // punish longer strategies to fight bloating
			for (int i = 0; i < POPSIZE; i++)
				fitness[i] -= (strategy_1[i].length + strategy_2[i].length) / 2;

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

	int print_indiv(char[] buffer, int buffercounter) {
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

