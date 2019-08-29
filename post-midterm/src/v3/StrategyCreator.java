package v3;

import java.util.Random;

public class StrategyCreator {
	Random rd=new Random();

	Deme createDeme(int level, int popSize, int depth, boolean type2) {
		checkErrors(level, popSize, depth);
		Deme newDeme;
		LevelStrategy[] newLevelStrategies=createLevelStrategies(level, popSize, depth, type2);
		newDeme=new Deme(newLevelStrategies, level);
		return newDeme;
	}
	
	LevelStrategy[] createLevelStrategies(int level, int popSize, int depth, boolean type2) {
		checkErrors(level, popSize, depth);
		LevelStrategy[] newLevelStrategies = new LevelStrategy[popSize];
		if (level == 2) {
			for (int i = 0; i < popSize; i++)
				newLevelStrategies[i] = new LevelStrategy(level, create_random_indiv(depth, type2));
		} else {
			for (int i = 0; i < popSize; i++)
				newLevelStrategies[i] = new LevelStrategy(level, create_random_indiv(depth, type2),
						create_random_indiv(depth, type2), create_random_indiv(depth, type2));
		}
		return newLevelStrategies;
	}
	
	char[][] createPopulation(int popSize, int depth, boolean type2) {
		checkErrors(popSize, depth);
		char[][] pop = new char[popSize][];
		for (int i = 0; i < popSize; i++)
			pop[i] = create_random_indiv(depth, type2);
		return pop;
	}
	
	char[] create_random_indiv(int depth, boolean type2) {
		if (depth == 0)
			return (null);
		char[] indiv = grow(depth, true, type2);
		while (indiv.length > MasterVariables.MAX_LEN)
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
				prim = (char) (rd.nextInt(MasterVariables.FSET_2_END - MasterVariables.FSET_2_START + 1) + MasterVariables.FSET_2_START);
				switch (prim) {
				case MasterVariables.GT:
				case MasterVariables.LT:
				case MasterVariables.EQ:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}
			} else {
				char[] leftBuffer = grow(depth - 1, true, type2);
				char[] rightBuffer = grow(depth - 1, true, type2);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(MasterVariables.FSET_3_END - MasterVariables.FSET_3_START + 1) + MasterVariables.FSET_3_START);
				switch (prim) {
				case MasterVariables.AND:
				case MasterVariables.OR:
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
					if (type2 == false)
						prim = (char) (MasterVariables.TSET_1_START + rd.nextInt(MasterVariables.TSET_1_END - MasterVariables.TSET_1_START + 1));
					else
						prim = (char) (MasterVariables.TSET_1_START + rd.nextInt(MasterVariables.TSET_2_END - MasterVariables.TSET_1_START + 1));
				else
					prim = (char) rd.nextInt(MasterVariables.RANDOMNUMBERS);
				buffer = new char[1];
				buffer[0] = prim;
			} else {
				char[] leftBuffer = grow(depth - 1, false, type2);
				char[] rightBuffer = grow(depth - 1, false, type2);
				buffer = new char[leftBuffer.length + rightBuffer.length + 1];
				prim = (char) (rd.nextInt(MasterVariables.FSET_1_END - MasterVariables.FSET_1_START + 1) + MasterVariables.FSET_1_START);
				switch (prim) {
				case MasterVariables.ADD:
				case MasterVariables.SUB:
				case MasterVariables.MUL:
				case MasterVariables.DIV:
					buffer[0] = prim;
					System.arraycopy(leftBuffer, 0, buffer, 1, leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + leftBuffer.length), rightBuffer.length);
				}
			}
		}

		return buffer;
	}

	private void checkErrors(int level, int popSize, int depth) {
		if (level < 2 || level > MasterVariables.MAXSYSTEM || popSize < 1 || depth < 1) {
			System.out.println("StrategyCreator Class error!!!");
			System.exit(0);
		}
	}

	private void checkErrors(int popSize, int depth) {
		if (popSize < 1 || depth < 1) {
			System.out.println("StrategyCreator Class error!!!");
			System.exit(0);
		}
	}
}
