import java.util.Arrays;
import java.util.Random;


public class GP_PilotStudy_v14 {
	public static final boolean PROFILEPOWER = false;
	static int GENERATIONS = 10000, POPSIZE_PER_PROCESSOR = 500, MINRANDOM = 0, MAXRANDOM = 1,
			PROFILES_PER_CAT = 150, MINSYSTEM = 2, MAXSYSTEM = 4, BESTRETAINED = 50,
			TESTCASES = 5000;
	static final int RANDOMNUMBERS = 50, CAPMED = RANDOMNUMBERS, CAPMIN = CAPMED + 1,
			CAPMAX = CAPMED + 2, MYCAP = CAPMED + 3, OPPCAP = CAPMED + 4, MYSIDECAP = CAPMED + 5,
			LEFTCAPSUM = CAPMED + 6, ADD = LEFTCAPSUM + 1, SUB = ADD + 1, MUL = ADD + 2,
			DIV = ADD + 3, GT = ADD + 4, LT = ADD + 5, EQ = ADD + 6, AND = ADD + 7, OR = ADD + 8,
			TSET_START = CAPMED, TSET_END = LEFTCAPSUM, FSET_1_START = ADD, FSET_1_END = DIV,
			FSET_2_START = GT, FSET_2_END = EQ, FSET_3_START = AND, FSET_3_END = OR;
	static int currentGen, uniqueWorldSizes;
	static implementClass_v14[] implement;
	static Thread[] thread;
	private static int processors, TOTAL_POPSIZE;
	static double[] fitness, randNum;
	static char[][][] init_strategy, join_strategy;
	static World_System_Shell_v14[][] profilingWorlds_Attacks, profilingWorlds_Joins;
	static Random rd = new Random();
	private static boolean[][][] attackProfiles, joinProfiles;
	private static boolean[] profilePrevBest;
	private static boolean sortedFit, shuffled;
	static char[][][][] temp_init_strategy, temp_join_strategy;
	static double[][] tempFitness;
	static int prevBestAttacks, prevBestJoins;
	static double avg_len;
	static double[] similarityWarInit_Avg,  similarityWarJoin_Avg;
	static final boolean BLOATFIGHT = false, PRINTINDIV = false, PRINTPROFILE = false;
	static int previousBestHash_1, previousBestHash_2;
	static int[] length, testedStrategyIndex;
	static double[][] capabilities, probabilities;



	public static void main(String[] args) {
		initalize();
		calculateFitness();
		createMasterArrays();
		sortDesc();
		printStats();
		for (currentGen = 1; currentGen <= GENERATIONS; currentGen++) {
			shuffle();
			evolve();
			for (int i=0; i<BESTRETAINED; i++){
				temp_init_strategy[0][i]=init_strategy[i];
				temp_join_strategy[0][i]=join_strategy[i];
			}
			calculateFitness();
			createMasterArrays();
			sortDesc();
			printStats();
		}
	}

	private static void evolve() {
		if(!shuffled){
			System.out.println("Arrays not shuffled");
			System.exit(0);
		}
		initiateThreads(3);
		waitForThreadsFinish();
	}

	private static void shuffle() {
		// Implementing Fisher–Yates shuffle of fitness and population
		// arrays
		int index;
		double tempFitness;
		char[][] tempIndiv_1, tempIndiv_2;
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
			for (int k = 0; k < uniqueWorldSizes; k++) {
				node_count_1 += traverse(init_strategy[i][k], 0);
			}
			for (int k = 0; k < uniqueWorldSizes-1; k++) {
				node_count_2 += traverse(join_strategy[i][k], 0);
			}
		}
		favgpop /= BESTRETAINED;
		double avg_len = (double) (node_count_1 + node_count_2) / BESTRETAINED; 
		
		calcSimilarity(init_strategy,  join_strategy, BESTRETAINED);

		System.out.println("Generation=" + currentGen + "\nMedian Fitness=" + fitness[TOTAL_POPSIZE / 2] + " Best Fitness="
				+ fitness[0] + " Worst Fitness=" + fitness[TOTAL_POPSIZE - 1] +"\nAvg Fitness of best=" + (favgpop) +  "& Avg Size=" + avg_len);
		
		System.out.println("Among the top " + BESTRETAINED + " i.e. the top " + ((double) BESTRETAINED / TOTAL_POPSIZE * 100)
				+ "%\nwar init similarity of ");
		int counter = MINSYSTEM;
		for (int i = 0; i < uniqueWorldSizes; i++) {
			System.out.println(counter + " world is " + similarityWarInit_Avg[i]);
			counter++;
		}
		System.out.println("And war joining similarity of ");
		counter = MINSYSTEM + 1;
		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			System.out.println(counter + " world is " + similarityWarJoin_Avg[i]);
			counter++;
		}

		int[] attacks=new int[uniqueWorldSizes];
		//int[] attacksProfiles=new int[uniqueWorldSizes];
		int[] joins=new int[uniqueWorldSizes-1];
		//int[] joinsProfiles=new int[uniqueWorldSizes-1];
		int totalAttacks = 0, totalJoins = 0;
		
		
		for (int i = 0; i < BESTRETAINED; i++) {
			for (int k = 0; k < uniqueWorldSizes; k++) {
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					if (attackProfiles[i][k][l]==true){
						totalAttacks++;
						attacks[k]++;
					}
				}
			}
		}
		
		if (MAXSYSTEM>2)
			for (int i = 0; i < BESTRETAINED; i++) {
				for (int k = 0; k < uniqueWorldSizes-1; k++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (joinProfiles[i][k][l]==true){
							totalJoins++;
							joins[k]++;
						}
					}
				}
			}

		double[][] attacksCapabilities = new double[uniqueWorldSizes][];
		double[][] attacksCapabilitiesRatios = new double[uniqueWorldSizes][];
		for (int i = 0; i < uniqueWorldSizes; i++) {
			attacksCapabilities[i] = new double[attacks[i]];
			attacksCapabilitiesRatios[i] = new double[attacks[i]];
			for (int m = 0; m < attacks[i]; m++) {
				for (int j = 0; j < BESTRETAINED; j++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						if (attackProfiles[j][i][l] == true) {
							attacksCapabilities[i][m] = profilingWorlds_Attacks[i][l].myCap;
							attacksCapabilitiesRatios[i][m++] = profilingWorlds_Attacks[i][l].myCap
									/ profilingWorlds_Attacks[i][l].oppCap;
						}
					}
				}
			}
		}
		
		double[][] joinCapabilities = new double[uniqueWorldSizes][];
		double[][] joinCapabilitiesRatios = new double[uniqueWorldSizes][];
		if (MAXSYSTEM > 2) {
			for (int i = 0; i < uniqueWorldSizes-1; i++) {
				joinCapabilities[i] = new double[joins[i]];
				joinCapabilitiesRatios[i] = new double[joins[i]];
				for (int m = 0; m < joins[i]; m++) {
					for (int j = 0; j < BESTRETAINED; j++) {
						for (int l = 0; l < PROFILES_PER_CAT; l++) {
							if (joinProfiles[j][i][l] == true) {
								joinCapabilities[i][m] = profilingWorlds_Joins[i][l].myCap;
								joinCapabilitiesRatios[i][m++] = profilingWorlds_Joins[i][l].myCap
										/ profilingWorlds_Joins[i][l].oppCap;
							}
						}
					}
				}
			}
		}

		
		for (int i = 0; i < uniqueWorldSizes; i++) {
			Arrays.sort(attacksCapabilities[i]);
			Arrays.sort(attacksCapabilitiesRatios[i]);
		}
		for (int i = 0; i < uniqueWorldSizes-1; i++) {
			Arrays.sort(joinCapabilities[i]);
			Arrays.sort(joinCapabilitiesRatios[i]);
		}
		
		
		

		System.out.println("\navg percentage of profile attacks is: "
				+ (double) totalAttacks * 100 / (BESTRETAINED * uniqueWorldSizes * PROFILES_PER_CAT)  + "%");
		System.out.println("percentage of profile attacks in a " );
		for (int i=0; i<uniqueWorldSizes; i++){
			System.out.println( (i + MINSYSTEM) + " world is " + (double) attacks[i] * 100 / (BESTRETAINED * PROFILES_PER_CAT)  + "%");
		}
		
		
		System.out.println("\nMedian cap of attackers in a");
		for (int i = 0; i < uniqueWorldSizes; i++) {
			if (attacksCapabilities[i] != null)
				System.out.println((i + MINSYSTEM) + " world is "
						+ attacksCapabilities[i][attacks[i] / 2]);
		}

		System.out.println("\nMin and 1st quart cap_ratio of attacks in a ");
		for (int i=0; i<uniqueWorldSizes; i++){
			System.out.println( (i + MINSYSTEM) + " world is " + attacksCapabilitiesRatios[i][0] + " & " + attacksCapabilitiesRatios[i][attacks[i]/2]);
		}
		
		
		System.out.println("\nMin and 1st quart cap of attackers in a");
		for (int i=0; i<uniqueWorldSizes; i++){
			System.out.println( (i + MINSYSTEM) + " world are " + attacksCapabilities[i][0] + " & " + attacksCapabilities[i][attacks[i]/4]);
		}
		
		System.out.println("\navg percentage of profile joins in a " );
		for (int i=0; i<uniqueWorldSizes-1; i++){
			System.out.println( (i + MINSYSTEM + 1) + " world is " + (double) joins[i] * 100 / (BESTRETAINED * PROFILES_PER_CAT) + "%");
		}
		
		
		System.out.println("\nMedian cap of joiners in a");
		for (int i=0; i<uniqueWorldSizes-1; i++){
			System.out.println( (i + MINSYSTEM + 1) + " world is " + joinCapabilities[i][joins[i]/2]);
		}
		
		System.out.println("\nMin and 1st quart cap of joiners in a");
		for (int i=0; i<uniqueWorldSizes-1; i++){
			System.out.println( (i + MINSYSTEM + 1) + " world are " + joinCapabilities[i][0] + " & " + joinCapabilities[i][joins[i]/4]);
		}
		
		
		
		
		if (PRINTINDIV) {
			System.out.println("\nBest Individual: ");
			System.out.print("Attack strategy: ");
			for (int k = 0; k < uniqueWorldSizes; k++) {
				System.out.println();
				print_indiv(init_strategy[k][0], 0);
			}
			System.out.print("\nJoining strategy: ");
			for (int k = 0; k < join_strategy.length; k++) {
				print_indiv(join_strategy[k][0], 0);
				System.out.println();
			}
			System.out.println();
		}

		/*int bestAttacks = -1;
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
		*/
		
		int curretBestHash_1 = init_strategy[0].hashCode();
		int curretBestHash_2 = join_strategy[0].hashCode();
		if (curretBestHash_1 == previousBestHash_1 && curretBestHash_2 == previousBestHash_2)
			System.out.print("\nProbably genotypically identical to previous!!");
		previousBestHash_1 = curretBestHash_1;
		previousBestHash_2 = curretBestHash_2;
		System.out.print("\n\n");
		System.out.flush();

		
		System.out.println("*******************************************************************");
		System.out.println();
		
	}

	private static void sortDesc() {
		// sort fitness and strategy arrays in fitness descending order
		
		char[][][] tempinit_strategy = new char[TOTAL_POPSIZE][][];
		
		char[][][] tempjoin_strategy = new char[TOTAL_POPSIZE][][];
		for (int i = 0; i < TOTAL_POPSIZE; i++) {
			tempinit_strategy[i] = (char[][]) init_strategy[i].clone();
			
			tempjoin_strategy[i] = (char[][]) join_strategy[i].clone();
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
					init_strategy[i] = (char[][]) tempinit_strategy[j].clone();
					
					join_strategy[i] = (char[][]) tempjoin_strategy[j].clone();
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

		
		initiateThreads(2);
		waitForThreadsFinish();
		sortedFit=false;
	}

	private static void createMasterArrays() {
		int counter = 0;
		for (int i = 0; i < processors; i++) {
			System.arraycopy(temp_init_strategy[i], 0, init_strategy, counter, POPSIZE_PER_PROCESSOR);
			System.arraycopy(temp_join_strategy[i], 0, join_strategy, counter, POPSIZE_PER_PROCESSOR);
			System.arraycopy(tempFitness[i], 0, fitness, counter, POPSIZE_PER_PROCESSOR);
			counter += POPSIZE_PER_PROCESSOR;
		}

	}

	private static void createSubArrays() {
		int counter = 0;
		for (int i = 0; i < processors; i++) {
			System.arraycopy(init_strategy, counter, temp_init_strategy[i], 0, POPSIZE_PER_PROCESSOR);
			System.arraycopy(join_strategy, counter, temp_join_strategy[i], 0, POPSIZE_PER_PROCESSOR);
			System.arraycopy(fitness, counter, tempFitness[i], 0, POPSIZE_PER_PROCESSOR);
			counter += POPSIZE_PER_PROCESSOR;
		}

	}

	private static void initalize() {
		System.out.println("START OF PROGRAM");
		if (MINSYSTEM!=2){
			System.out.println("Error!!! MINSYSTEM must be 2");
			System.exit(0);
		}
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
		
		uniqueWorldSizes = 1 + MAXSYSTEM - MINSYSTEM;
		init_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes][];
		join_strategy = new char[TOTAL_POPSIZE][uniqueWorldSizes - 1][];
		
		profilingWorlds_Attacks = new World_System_Shell_v14[uniqueWorldSizes][PROFILES_PER_CAT];
		profilingWorlds_Joins = new World_System_Shell_v14[uniqueWorldSizes-1][PROFILES_PER_CAT];
		
		for (int i = 0; i < uniqueWorldSizes; i++) {
			for (int k = 0; k < PROFILES_PER_CAT; k++) {
				profilingWorlds_Attacks[i][k] = new World_System_Shell_v14(false, (i
						+ MINSYSTEM));
			}
		}

		for (int i = 0; i < uniqueWorldSizes - 1; i++) {
			for (int k = 0; k < PROFILES_PER_CAT; k++) {
				profilingWorlds_Joins[i][k] = new World_System_Shell_v14(true, i
						+ MINSYSTEM + 1);
			}
		}
		
		
		
		
		
		sortedFit = false;
		shuffled = false;

		temp_init_strategy=new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes][];
		temp_join_strategy=new char[processors][POPSIZE_PER_PROCESSOR][uniqueWorldSizes - 1][];
		tempFitness = new double[processors][];
		
				
		initiateThreads(1);
		waitForThreadsFinish();
		
		for (int test = 0; test < TESTCASES; test++) {
			length[test] = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
			//if (currentGen > 25 && length[test] == 2)
				length[test] = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
			//if (currentGen > 100 && (length[test] == 2 || length[test] == 3) )
				length[test] = MINSYSTEM + rd.nextInt(MAXSYSTEM - MINSYSTEM + 1);
			testedStrategyIndex[test] = rd.nextInt(length[test]);
			capabilities[test] = new double[length[test]];
			for (int i = 0; i < length[test]; i++) {
				capabilities[test][i] = rd.nextDouble();
			}
			probabilities[test] = new double[60];
			for (int i = 0; i < 60; i++)
				probabilities[test][i] = rd.nextDouble();
		}

	}

	private static void initiateThreads(int operationID) {
		thread = new Thread[processors];
		implement = new implementClass_v14[processors];

		for (int threadID = 0; threadID < processors; threadID++) {
			implement[threadID] = new implementClass_v14(threadID, randNum, POPSIZE_PER_PROCESSOR,
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
	
	private static void calcSimilarity(char[][][] init_strategy,  char[][][] join_strategy, int indivs) {
		similarityWarInit_Avg = new double[uniqueWorldSizes];
		similarityWarJoin_Avg = new double[uniqueWorldSizes - 1];

		attackProfiles= new boolean[indivs][uniqueWorldSizes][PROFILES_PER_CAT];
		joinProfiles = new boolean[indivs][uniqueWorldSizes][PROFILES_PER_CAT];
		

		for (int i = 0; i < indivs; i++) {
			for (int k = 0; k < uniqueWorldSizes; k++) {
				for (int l = 0; l < PROFILES_PER_CAT; l++) {
					attackProfiles[i][k][l] = profilingWorlds_Attacks[k][l]
							.willItAttack(init_strategy[i][k]);
				}
			}
		}
		
		if (MAXSYSTEM > 2)
			for (int i = 0; i < indivs; i++) {
				for (int k = 0; k < uniqueWorldSizes - 1; k++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						joinProfiles[i][k][l] = profilingWorlds_Joins[k][l]
								.willItAttack(join_strategy[i][k]);
					}
				}
			}
		
		int[] m= new int[uniqueWorldSizes]; 
		int[] n= new int[uniqueWorldSizes-1]; 
		for (int i = 0; i < indivs - 1; i++)
			for (int j = i + 1; j < indivs; j++) {
				for (int k = 0; k < uniqueWorldSizes; k++) {
					for (int l = 0; l < PROFILES_PER_CAT; l++) {
						m[k]++;
						if (attackProfiles[i][k][l] == attackProfiles[j][k][l]) {
							similarityWarInit_Avg[k]++;
						}
					}
				}
			}
		
		
		if (MAXSYSTEM > 2)
			for (int i = 0; i < indivs - 1; i++)
				for (int j = i + 1; j < indivs; j++) {
					for (int k = 0; k < uniqueWorldSizes - 1; k++) {
						for (int l = 0; l < PROFILES_PER_CAT; l++) {
							n[k]++;
							if (joinProfiles[i][k][l] == joinProfiles[j][k][l]) {
								similarityWarJoin_Avg[k]++;
							}
						}
					}
				}

		for (int i = 0; i < uniqueWorldSizes; i++) {
			similarityWarInit_Avg[i] = similarityWarInit_Avg[i] / m[i];
		}

		if (MAXSYSTEM > 2)
			for (int i = 0; i < uniqueWorldSizes - 1; i++) {
				similarityWarJoin_Avg[i] = similarityWarJoin_Avg[i] / n[i];
			}

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
