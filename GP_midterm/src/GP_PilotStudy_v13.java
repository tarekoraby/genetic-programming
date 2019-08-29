import java.util.Arrays;
import java.util.Random;


public class GP_PilotStudy_v13 {
	public static final boolean PROFILEPOWER = false;
	static int GENERATIONS = 100000, POPSIZE_PER_PROCESSOR = 250,
			MINRANDOM = 0, MAXRANDOM = 1, PROFILES = 500, MINSYSTEM = 2,
			MAXSYSTEM = 3, BESTRETAINED=50, TESTCASES = 5000 ;
	static final int RANDOMNUMBERS = 50, CAPMED = RANDOMNUMBERS,
			CAPMIN = CAPMED + 1, CAPMAX = CAPMED + 2, MYCAP = CAPMED + 3,
			OPPCAP = CAPMED + 4, MYSIDECAP = CAPMED + 5,
			LEFTCAPSUM = CAPMED + 6, ADD = LEFTCAPSUM + 1, SUB = ADD + 1, MUL = ADD + 2, DIV = ADD + 3,
			GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7,
			OR = ADD + 8, TSET_START = CAPMED, TSET_END = LEFTCAPSUM,
			FSET_1_START = ADD, FSET_1_END = DIV, FSET_2_START = GT,
			FSET_2_END = EQ, FSET_3_START = AND, FSET_3_END = OR;
	private static int currentGen;
	static implementClass_v13[] implement;
	static Thread[] thread;
	private static int processors, TOTAL_POPSIZE;
	static double[] fitness, randNum;
	static char[][] init_strategy, join_strategy;
	static World_System_Shell_v13[] profilingWorlds;
	static Random rd = new Random();
	private static boolean[][] profile, profile10;
	private static boolean[] profilePrevBest;
	private static boolean sortedFit, shuffled;
	static char[][][] temp_init_strategy, temp_join_strategy;
	static double[][] tempFitness;
	static int prevBestAttacks, prev10BestAttacks, prevBestJoins;
	static double avg_len, similarityWarInit_2_Avg, similarityWarInit_3_Avg, similarityWarJoinAvg, tenWI_2_Similarity, tenWI_3_Similarity, tenWJSimilarity;
	static final boolean BLOATFIGHT = false, PRINTINDIV = false, PRINTPROFILE = false;
	static int previousBestHash_1;
	static int previousBestHash_2;
	static int[] length, testedStrategyIndex;
	static double[][] capabilities, probabilities;



	public static void main(String[] args) {
		initalize();
		calculateFitness();
		sortDesc();
		printStats();
		for (currentGen = 1; currentGen <= GENERATIONS; currentGen++) {
			shuffle();
			evolve();
			calculateFitness();
			sortDesc();
			printStats();
		}
	}

	private static void evolve() {
		if(!shuffled){
			System.out.println("Arrays not shuffled");
			System.exit(0);
		}
		createSubArrays();
		createMasterArrays();
		initiateThreads(3);
		waitForThreadsFinish();
	}

	private static void shuffle() {
		// Implementing Fisher–Yates shuffle of fitness and population
		// arrays
		int index;
		double tempFitness;
		char[] tempIndiv_1, tempIndiv_2;
		for (int i = TOTAL_POPSIZE - 1; i > 0; i--) {
			index = rd.nextInt(i + 1);
			tempFitness = fitness[index];
			fitness[index] = fitness[i];
			fitness[i] = tempFitness;

			tempIndiv_1 = init_strategy[index];
			init_strategy[index] = init_strategy[i];
			init_strategy[i] = tempIndiv_1;

			tempIndiv_2 = join_strategy[index];
			join_strategy[index] = join_strategy[i];
			join_strategy[i] = tempIndiv_2;
		}
		shuffled=true;
		sortedFit=false;
		
	}

	private static void printStats() {

		if(!sortedFit){
			System.out.println("Not Sorted!!!");
			System.exit(0);
		}
			
		double favgpop = 0;
		int node_count_1 = 0;
		int node_count_2 = 0;
		for (int i = 0; i < BESTRETAINED; i++) {
			favgpop += fitness[i];
			node_count_1 += traverse(init_strategy[i], 0);
			node_count_2 += traverse(join_strategy[i], 0);
		}
		favgpop /= BESTRETAINED;
		double avg_len = (double) (node_count_1 + node_count_2) / TOTAL_POPSIZE; 
		
		calcSimilarity(init_strategy,  join_strategy, BESTRETAINED);

		System.out.println("Generation=" + currentGen + "\nAvg Fitness=" + (favgpop) + " Median Fitness="
				+ fitness[TOTAL_POPSIZE / 2] + " Best Fitness=" + fitness[0] + " Worst Fitness=" + fitness[TOTAL_POPSIZE - 1]
				+ "\nAvg Size=" + avg_len );
		
		System.out.println("Among the top " + BESTRETAINED + " i.e. the top " + ((double) BESTRETAINED / TOTAL_POPSIZE * 100)
				+ "%\nwar 2 init similarity is " + similarityWarInit_2_Avg + "\nwar 3 init similarity is " + similarityWarInit_3_Avg +"\nwar join similarity is " + similarityWarJoinAvg);		

		int totalAttacks = 0, totalAttacks_2 = 0, totalAttacks_3 = 0, totalJoins = 0, m = 0, n = 0, o = 0;
		double cap2 = 0, min_cap2 = 1E16, cap3 = 0, min_cap3 = 1E16, cap3_join = 0, min_cap3_join = 1E16, ratio3 = 0, min_ratio3=1E16;
		double[] cap3_min_List = new double[BESTRETAINED / 4];
		for (int i = 0; i < cap3_min_List.length; i++)
			cap3_min_List[i] = 1E16;
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
						ratio3+=profilingWorlds[k].myCap / profilingWorlds[k].oppCap;
						if (profilingWorlds[k].myCap < cap3_min_List[cap3_min_List.length-1]){
							cap3_min_List[cap3_min_List.length-1]=profilingWorlds[k].myCap;
							Arrays.sort(cap3_min_List);
						}
						if (profilingWorlds[k].myCap < min_cap3)
							min_cap3 = profilingWorlds[k].myCap;
						if (profilingWorlds[k].myCap / profilingWorlds[k].oppCap < min_ratio3)
							min_ratio3 = profilingWorlds[k].myCap / profilingWorlds[k].oppCap;
					}
				}
			for (int k = PROFILES / 2; k < PROFILES; k++) {
				o++;
				if (profile[i][k] == true) {
					totalJoins++;
					cap3_join += profilingWorlds[k].myCap;
					if (profilingWorlds[k].myCap < min_cap3_join)
						min_cap3_join = profilingWorlds[k].myCap;
				}
			}
		}
		
		double[] cap2_attackers_List = new double[totalAttacks_2];
		double[] cap3_attackers_List = new double[totalAttacks_3];
		double[] cap3_joiners_List = new double[totalJoins];
		
		int a=0, b=0, c=0;
		
		for (int i = 0; i < BESTRETAINED; i++) {
			for (int k = 0; k < PROFILES / 2; k++)
				if (profilingWorlds[k].currentStatesNum == 2) {
					if (profile[i][k] == true) {
						cap2_attackers_List[a++]= profilingWorlds[k].myCap;
						
					}
				} else if (profilingWorlds[k].currentStatesNum == 3) {
					if (profile[i][k] == true) {
						cap3_attackers_List[b++]= profilingWorlds[k].myCap;
					}
				}
			for (int k = PROFILES / 2; k < PROFILES; k++) {
				if (profile[i][k] == true) {
					cap3_joiners_List[c++]= profilingWorlds[k].myCap;
				}
			}
			
		}
		
		Arrays.sort(cap2_attackers_List);
		Arrays.sort(cap3_attackers_List);
		Arrays.sort(cap3_joiners_List);

		System.out.println("\navg percentage of profile attacks is: "
				+ (double) totalAttacks * 100 / (BESTRETAINED * (int) (PROFILES / 2))  + "%");
		System.out.println("percentage of profile attacks 2 is: "
				+ (double) totalAttacks_2 * 100 / m + "%");
		System.out.println("percentage of profile attacks 3 is: "
				+ (double) totalAttacks_3 * 100 / n + "%");
		System.out.println("And avg percentage of profile joins is: "
				+ (double) totalJoins * 100 / o + "%");
		System.out.println("\nMean cap of attackers in\n2 world " + cap2
				/ totalAttacks_2);
		System.out.println("3 world " + cap3 / totalAttacks_3);
		System.out.println("3 world join " + cap3_join / totalJoins);
		System.out.println("\nMedian cap of attackers in\n2 world " + cap2_attackers_List[totalAttacks_2/2]);
		System.out.println("3 world " + cap3_attackers_List[totalAttacks_3/2]);
		System.out.println("3 world join " + cap3_joiners_List[totalJoins/2]);
		System.out.println("\nmin cap of attackers in\n2 world " + min_cap2);
		System.out.println("3 world " + min_cap3 );
		System.out.println("3 world join " + min_cap3_join );
		System.out.println("1st quartile of 3 world is: " + cap3_min_List[cap3_min_List.length-1]);
		System.out.println(ratio3/totalAttacks_3 + " " + min_ratio3);
		
		
		if (PRINTINDIV) {
			System.out.println("\nBest Individual: ");
			System.out.print("Attack strategy: ");
			print_indiv(init_strategy[0], 0);
			System.out.print("\nJoining strategy: ");
			print_indiv(join_strategy[0], 0);
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
		
		
		int curretBestHash_1 = init_strategy[0].hashCode();
		int curretBestHash_2 = join_strategy[0].hashCode();
		if (curretBestHash_1 == previousBestHash_1 && curretBestHash_2 == previousBestHash_2)
			System.out.print("\nProbably genotypically identical to previous!!");
		previousBestHash_1 = curretBestHash_1;
		previousBestHash_2 = curretBestHash_2;
		System.out.print("\n\n");
		System.out.flush();

		if (currentGen % 10 == 0 ) {
			System.out.println("Ten 2 similarity " + tenWI_2_Similarity);
			System.out.println("Ten 3 similarity " + tenWI_3_Similarity);
			System.out.println("Number of profile attacks by best 10 gen ago is " + prev10BestAttacks);
			prev10BestAttacks = bestAttacks;
			tenWI_2_Similarity = similarityWarInit_2_Avg;
			tenWI_3_Similarity = similarityWarInit_3_Avg;
		}
		System.out.println("*******************************************************************");
		System.out.println();
		
	}

	private static void sortDesc() {
		// sort fitness and strategy arrays in fitness descending order
		
		char[][] tempinit_strategy = new char[TOTAL_POPSIZE][];
		
		char[][] tempjoin_strategy = new char[TOTAL_POPSIZE][];
		for (int i = 0; i < TOTAL_POPSIZE; i++) {
			tempinit_strategy[i] = (char[]) init_strategy[i].clone();
			
			tempjoin_strategy[i] = (char[]) join_strategy[i].clone();
		}

		double[] tempFitness = (double[]) fitness.clone();
		Arrays.sort(fitness);
		
		double temp;
		for (int i = 0; i < TOTAL_POPSIZE / 2; i++) {
			temp = fitness[i];
			fitness[i] = fitness[TOTAL_POPSIZE - 1 - i];
			fitness[TOTAL_POPSIZE - 1 - i] = temp;
		}

		for (int i = 0; i < TOTAL_POPSIZE; i++) {
			init_strategy[i] = null;
			
			join_strategy[i] = null;
			int j = 0;

			while (init_strategy[i] == null && j < TOTAL_POPSIZE) {
				if (fitness[i] == tempFitness[j]) {
					init_strategy[i] = (char[]) tempinit_strategy[j].clone();
					
					join_strategy[i] = (char[]) tempjoin_strategy[j].clone();
					tempinit_strategy[j] = null;
					
					tempjoin_strategy[j] = null;
					tempFitness[j] = -1e-5;
				}
				j++;
			}
		}

		
		sortedFit=true;
		shuffled=false;
	}

	private static void calculateFitness() {
		
		for (int test = 0; test < TESTCASES; test++) {
			length[test] = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
			if (length[test] == 2)
				length[test] = MINSYSTEM
						+ rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
			testedStrategyIndex[test] = rd.nextInt(length[test]);
			capabilities[test] = new double[length[test]];
			for (int i = 0; i < length[test]; i++) {
				capabilities[test][i] = rd.nextDouble();
			}
			probabilities[test]=new double[30];
			for (int i=0; i<30; i++)
				probabilities[test][i]=rd.nextDouble();
		}
		initiateThreads(2);
		waitForThreadsFinish();
		createMasterArrays();
		sortedFit=false;
	}

	private static void createMasterArrays() {
		int counter=0;
		for (int i = 0; i < processors; i++) {
			System.arraycopy(temp_init_strategy[i], 0, init_strategy, counter,
					POPSIZE_PER_PROCESSOR);
			System.arraycopy(temp_join_strategy[i], 0, join_strategy, counter,
					POPSIZE_PER_PROCESSOR);
			System.arraycopy(tempFitness[i], 0, fitness, counter,
					POPSIZE_PER_PROCESSOR);
			counter += POPSIZE_PER_PROCESSOR;
		}
		
	}
	
	private static void createSubArrays() {
		int counter=0;
		for (int i = 0; i < processors; i++) {
			System.arraycopy(init_strategy, counter, temp_init_strategy[i], 0, 
					POPSIZE_PER_PROCESSOR);
			System.arraycopy(join_strategy, counter, temp_join_strategy[i], 0, 
					POPSIZE_PER_PROCESSOR);
			System.arraycopy(fitness, counter, tempFitness[i], 0, 
					POPSIZE_PER_PROCESSOR);
			counter += POPSIZE_PER_PROCESSOR;
		}
		
	}

	private static void initalize() {
		System.out.println("START OF PROGRAM");
		processors = Runtime.getRuntime().availableProcessors();
		TOTAL_POPSIZE = POPSIZE_PER_PROCESSOR * processors;
		System.out.println("Population size is " + TOTAL_POPSIZE);
		System.out.println("Processing is divided over " + processors + " processors");
		
		fitness = new double[TOTAL_POPSIZE];
		randNum=new double[RANDOMNUMBERS];
		for (int i = 0; i < RANDOMNUMBERS; i++)
			randNum[i] = (MAXRANDOM - MINRANDOM) * rd.nextDouble() + MINRANDOM;

		length = new int[TESTCASES];
		testedStrategyIndex = new int[TESTCASES];
		capabilities = new double[TESTCASES][];
		probabilities = new double[TESTCASES][];
		
		profilingWorlds = new World_System_Shell_v13[PROFILES];
		for (int i = 0; i < PROFILES / 2; i++)
			profilingWorlds[i] = new World_System_Shell_v13(false);
		if (MAXSYSTEM > 2)
			for (int i = PROFILES / 2; i < PROFILES; i++)
				profilingWorlds[i] = new World_System_Shell_v13(true);
		
		profile = new boolean[BESTRETAINED][PROFILES];
		profile10 = new boolean[BESTRETAINED][PROFILES];
		profilePrevBest=new boolean[PROFILES];
		
		sortedFit = false;
		shuffled = false;
		
		init_strategy=new char[TOTAL_POPSIZE][];
		join_strategy=new char[TOTAL_POPSIZE][];
		
		
		temp_init_strategy=new char[processors][][];
		temp_join_strategy=new char[processors][][];
		tempFitness = new double[processors][];
		
				
		initiateThreads(1);
		waitForThreadsFinish();

	}

	private static void initiateThreads(int operationID) {
		thread = new Thread[processors];
		implement = new implementClass_v13[processors];

		for (int threadID = 0; threadID < processors; threadID++) {
			implement[threadID] = new implementClass_v13(threadID, randNum, POPSIZE_PER_PROCESSOR,
					tempFitness[threadID], temp_init_strategy[threadID],
					temp_join_strategy[threadID], TESTCASES);
			implement[threadID].setOperationID(operationID);
			thread[threadID] = new Thread(implement[threadID]);
			thread[threadID].start();
		}

	}
	
	private static void waitForThreadsFinish() {
		for (int i = 0; i < processors; i++) {
			try {
				thread[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	static int traverse( char [] buffer, int buffercount ) {
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
	
	private static void calcSimilarity(char[][] init_strategy,  char[][] join_strategy, int indivs) {
		//calculate similarity of the best performing strategies in the population
		//This method assumes that the arrays of strategies and fitness are already sorted
		profile = new boolean[indivs][PROFILES];
		
		for (int i = 0; i < indivs; i++){
			for (int j = 0; j < PROFILES / 2; j++)
				profile[i][j] = profilingWorlds[j].willItAttack(init_strategy[i],  join_strategy[i]);
			if (MAXSYSTEM > 2)
				for (int j = PROFILES / 2; j < PROFILES; j++)
					profile[i][j] = profilingWorlds[j].willItAttack(init_strategy[i],  join_strategy[i]);
		}

		if (indivs == 1) {
			similarityWarInit_2_Avg = 1;
			similarityWarInit_3_Avg = 1;
			similarityWarJoinAvg = 1;
			return;
		}

	
		// Calculate profiles avg similarity
		similarityWarInit_2_Avg = 0;
		similarityWarInit_3_Avg = 0;
		similarityWarJoinAvg = 0;
		double n = 0, m = 0, p=0;
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
		similarityWarJoinAvg /= m;

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
