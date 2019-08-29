/* TO DO
 * CHECK THAT TRAVEREE IS NECESSARY INSTEAD OF JUST USING ARRAY LENGTH
 */


/* 
 * Based on tiny_gp.java by Riccardo Poli (email: rpoli@essex.ac.uk)
 *
 */

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;

public class GP_PilotStudy {
	double[] fitness;
	static double[] randNum;
	char[][] pop;
	static Random rd = new Random();
	static final int MAX_LEN = 10000, POPSIZE = 1000, DEPTH = 5, GENERATIONS = 100, TSIZE = 2,
			BESTRETAINED = POPSIZE / 20, TESTCASES = 100, PROFILES = 1000, MINSYSTEM = 2, MAXSYSTEM = 9,
			RANDOMNUMBERS = 100;
	static double MINRANDOM = 0, MAXRANDOM = 1;
	public static final double PMUT_PER_NODE = 0.05, REPLICATION_PROB = 0, CROSSOVER_PROB = 0.45,
			SUBTREE_MUT_PROB = 0.45, PMUT_PROB = 0.1;
	static final int CAPAVG = RANDOMNUMBERS, CAPMED = CAPAVG + 1, CAPSS = CAPAVG + 2, CAPSTDEV = CAPAVG + 3,
			CURRENTSTATESNUM = CAPAVG + 4, MYCAP = CAPAVG + 5, OPPCAP = CAPAVG + 6, OPPCAPSUM = CAPAVG + 7,
			ADD = CAPAVG + 8, SUB = ADD + 1, MUL = ADD + 2, DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, AND = ADD + 6,
			OR = ADD + 7, TSET_START = CAPAVG, TSET_END = OPPCAPSUM, FSET_1_START = ADD, FSET_1_END = DIV,
			FSET_2_START = GT, FSET_2_END = LT, FSET_3_START = AND, FSET_3_END = OR;
	static char[] program;
	static int PC, prevBestAttacks, prev10BestAttacks;
	static double avg_len, similarityAvg, tenSimilarity;
	static double[][] targets;
	static final boolean BLOATFIGHT = false, PRINTINDIV = false, PRINTPROFILE = false;
	static final int CAPCHANGE = 0;
	World_System_Shell[] profilingWorlds;
	boolean[][] profile;
	boolean[][] profile10;
	boolean[] profilePrevBest;
	int previousBestHash;
	
	static double TC1, TC2, TC3, TC4, TC5, TC6, TC7, TC8, TC9, TC10, TC11, TC12, TC13, TC14, TC15, TC16, TC17, TC18;

	public static void main(String[] args) {

		GP_PilotStudy gp = new GP_PilotStudy();
		gp.evolve();
		
	}

	public GP_PilotStudy() {
		System.out.println("START OF PROGRAM");
		if (DEPTH < 1) {
			System.out.println("Cannot excute!! Minimum depth of intitial individuals is 1");
			System.exit(0);
		}
		if (REPLICATION_PROB + CROSSOVER_PROB + SUBTREE_MUT_PROB + PMUT_PROB != 1){
			System.out.println("Probabilities don't add up to 1");
			System.exit(0);
		}
			
		fitness = new double[POPSIZE];
		randNum=new double[RANDOMNUMBERS];
		for (int i = 0; i < RANDOMNUMBERS; i++)
			randNum[i] = (MAXRANDOM - MINRANDOM) * rd.nextDouble() + MINRANDOM;
		
		profilingWorlds = new World_System_Shell[PROFILES];
		for (int i = 0; i < PROFILES; i++)
			profilingWorlds[i]=new World_System_Shell();
		profile = new boolean[BESTRETAINED][PROFILES];
		profile10 = new boolean[BESTRETAINED][PROFILES];
		profilePrevBest=new boolean[PROFILES];
		
		pop = create_random_pop(POPSIZE, DEPTH, fitness);
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
				if (prim == 0 )
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
		char[][] tempPop;
		fitness = calculateFitness(pop, TESTCASES);
		sortDesc(pop, fitness);
		calcTopSimilarity(pop, 0);
		stats( fitness, pop, 0 );
		for (int gen = 0; gen < GENERATIONS; gen++) { 
			TC11=0; // Num of worlds
			TC12=0; // Num of attacks
			tempPop = new char[POPSIZE][];
			
			//Keep the top strategies in the new population
			System.arraycopy(pop, 0, tempPop, 0, BESTRETAINED);



			// For the remaining slots in the new population probabilistically
			// perform crossover, subtree mutation or point mutation
			for (indivs = BESTRETAINED; indivs < POPSIZE; indivs++) {
				TC7++;
				char[] newind;
				double random = rd.nextDouble();
				if (random < REPLICATION_PROB) {
					TC13++;
					parent = tournament(fitness, TSIZE);
					tempPop[indivs] = pop[parent];
				} else if ((random - REPLICATION_PROB) < CROSSOVER_PROB) {
					TC8++;
					parent1 = tournament(fitness, TSIZE);
					parent2 = tournament(fitness, TSIZE);
					newind = crossover(pop[parent1], pop[parent2]);
					tempPop[indivs] = newind;
				} else if ((random - (CROSSOVER_PROB + REPLICATION_PROB)) < SUBTREE_MUT_PROB) {
					TC9++;
					parent = tournament(fitness, TSIZE);
					newind = subtreeMutation(pop[parent]);
					tempPop[indivs] = newind;
				} else {
					TC10++;
					parent = tournament(fitness, TSIZE);
					newind = pointMutation(pop[parent], PMUT_PER_NODE);
					tempPop[indivs] = newind;
				}
			}
			
			pop = tempPop;
			fitness = calculateFitness(pop, TESTCASES);
			sortDesc(pop, fitness);
			calcTopSimilarity(pop, gen);
			stats( fitness, pop, gen );
			
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

	private void sortDesc(char[][] pop, double[] fitness) {
		// sort fitness and pop arrays in fitness descending order
		double[] tempFitness = fitness.clone();
		char[][] tempPop = new char[POPSIZE][];
		for (int i = 0; i < POPSIZE; i++) {
			tempPop[i] = pop[i].clone();
		}

		double[] sortedFitness = fitness.clone();
		Arrays.sort(sortedFitness);

		int x = 0;
		for (int i = POPSIZE - 1; i >= 0; i--) {
			fitness[x] = sortedFitness[i];
			x++;
		}

		for (int i = 0; i < POPSIZE; i++) {
			pop[i] = null;
			int j = 0;

			while (pop[i] == null && j < POPSIZE) {
				if (fitness[i] == tempFitness[j]) {
					pop[i] = tempPop[j].clone();
					tempPop[j] = null;
					tempFitness[j] = -1e-5;
				}
				j++;
			}
		}

		this.pop = pop;
		this.fitness = fitness;
	}

	private void calcTopSimilarity(char[][] pop, int gen) {
		//calculate similarity of the best in the population
		for (int i = 0; i < BESTRETAINED; i++)
			for (int j = 0; j < PROFILES; j++)
				profile[i][j] = profilingWorlds[j].willItAttack(pop[i]);

		//Calculate profiles avg similarity
		similarityAvg = 0;
		double n = 0;
		for (int i = 0; i < BESTRETAINED - 1; i++)
			for (int j = i + 1; j < BESTRETAINED; j++)
				for (int k = 0; k < PROFILES; k++) {
					n++;
					if (profile[i][k] == profile[j][k])
						similarityAvg++;
				}

		similarityAvg /= n;

		if (gen == 0) {
			for (int i = 0; i < BESTRETAINED; i++)
				for (int k = 0; k < PROFILES; k++)
					profile10[i][k]=profile[i][k];
		}
			//profile10=Arrays.copyOf(profile, POPSIZE);
			//System.arraycopy(profile, 0, profile10, 0, POPSIZE);
		if (gen % 10 == 0 && gen !=0) {
			tenSimilarity = 0;
			double c = 0;
			for (int i = 0; i < BESTRETAINED; i++)
				for (int j = 0; j < BESTRETAINED; j++)
					for (int k = 0; k < PROFILES; k++) {
						c++;
						if (profile[i][k] == profile10[j][k])
							tenSimilarity++;
					}
			tenSimilarity /= c;
			for (int i = 0; i < BESTRETAINED; i++)
				for (int k = 0; k < PROFILES; k++)
					profile10[i][k]=profile[i][k];
		}
	}

	void stats(double[] fitness, char[][] pop, int gen) {
		double favgpop = 0;
		int node_count = 0;
		for (int i = 0; i < POPSIZE; i++) {
			favgpop += fitness[i];
			node_count += traverse(pop[i], 0);
		}
		favgpop /= POPSIZE;
		avg_len = (double) node_count / POPSIZE;

		System.out.println("Generation=" + gen + "\nAvg Fitness=" + (favgpop) + " Median Fitness=" + fitness[POPSIZE / 2]
				+ " Best Fitness=" + fitness[0] + " Worst Fitness=" + fitness[POPSIZE - 1] + "\nAvg Size=" + avg_len
				+ " Avg no of attacks=" + (TC12 / TC11) + " Avg top " + ((double) BESTRETAINED / POPSIZE * 100)
				+ "% similarity " + similarityAvg);

		if (PRINTINDIV) {
			System.out.println("\nBest Individual: ");
			print_indiv(pop[0], 0);
			System.out.println();
		}

		int bestAttacks = -1;
		if (PRINTPROFILE) {
			bestAttacks = 0;
			System.out.println("And it's behavioral profile is:");
			for (int i = 0; i < PROFILES; i++)
				if (profile[0][i] == true) {
					System.out.print("1");
					bestAttacks++;
				} else
					System.out.print("0");
			System.out.println();
		}

		if (bestAttacks == -1) {
			bestAttacks = 0;
			for (int i = 0; i < PROFILES; i++)
				if (profile[0][i] == true) {
					bestAttacks++;
				}
		}

		System.out.println("\nNumber of profile attacks by best is " + bestAttacks);
		System.out.println("Number of profile attacks by prev best is " + prevBestAttacks);
		prevBestAttacks = bestAttacks;
		System.out.print("Profile differences from previous best is ");
		int differences = 0;
		for (int i = 0; i < PROFILES; i++)
			if (profile[0][i] != profilePrevBest[i])
				differences++;
		profilePrevBest = Arrays.copyOf(profile[0], PROFILES);
		System.out.print(differences);
		
		int topAttacks=0;
		for (int i = 0; i < BESTRETAINED; i++)
			for (int k = 0; k < PROFILES; k++)
				if (profile[i][k] == true) {
					topAttacks++;
				}
		System.out.println("\nOut of " + PROFILES + " profiles, the avg number of profile attacks by top " + ((double) BESTRETAINED / POPSIZE * 100) +  " is " + (double) topAttacks / BESTRETAINED);
		
		int curretBestHash = pop[0].hashCode();
		if (curretBestHash == previousBestHash)
			System.out.print("\nProbably genotypically identical to previous!!");
		previousBestHash = curretBestHash;
		System.out.print("\n\n");
		System.out.flush();

		if (gen % 10 == 0 ) {
			System.out.println("Ten similarity " + tenSimilarity);
			System.out.println("Number of profile attacks by best 10 gen ago is " + prev10BestAttacks);
			prev10BestAttacks = bestAttacks;
		}
		
		System.out.println();
	}

	double[] calculateFitness(char[][] pop, int testCases) {
		// This method changes the order of the population array
		fitness = new double[POPSIZE];
		while (testCases > 0) {
			// Implementing Fisher–Yates shuffle of fitness and population
			// arrays
			int index;
			double tempFitness;
			char[] tempIndiv;
			for (int i = POPSIZE - 1; i > 0; i--) {
				index = rd.nextInt(i + 1);
				tempFitness = fitness[index];
				fitness[index] = fitness[i];
				fitness[i] = tempFitness;

				tempIndiv = pop[index];
				pop[index] = pop[i];
				pop[i] = tempIndiv;
			}
			
			//Assign states into world systems. Each system is of a length between minSystem and maxSystem. There must be remaining at least two states in the end for a system to be created.
			int counter = 0, length;
			while (counter < POPSIZE) {
				
				length = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
				/*while(length + counter > POPSIZE || (POPSIZE - counter - length < MINSYSTEM && POPSIZE - counter - length != 0))
				{
					length = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
				}
				*/
				if (POPSIZE - counter <= MAXSYSTEM)
					length = POPSIZE - counter;
				else if (POPSIZE - counter < MAXSYSTEM + MINSYSTEM)
					length = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM);
				else
					length = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
				char[][] testPop = new char[length][];
				for (int i = 0; i < length; i++)
					testPop[i] = pop[counter + i].clone();
				
				

				WorldSystem world = new WorldSystem(testPop);
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
				fitness[i] -= pop[i].length;

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
						parentcopy[mutsite]=LT;
						break;
					case LT:
						parentcopy[mutsite]=GT;
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
			case CAPAVG:
				System.out.print("CAP_AVG");
				break;
			case CAPMED:
				System.out.print("CAP_MED");
				break;
			case CAPSS:
				System.out.print("CAP_SS");
				break;
			case CAPSTDEV:
				System.out.print("CAP_STDEV");
				break;
			case CURRENTSTATESNUM:
				System.out.print("STATES_NUM");
				break;
			case MYCAP:
				System.out.print("MY_CAP");
				break;
			case OPPCAP:
				System.out.print("OPP_CAP");
				break;
			case OPPCAPSUM:
				System.out.print("OPP_CAP_SUM");
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

