package v18;

import java.util.Arrays;
import java.util.Random;

public class StrategyCreator {
	Random rd = new Random();
	
	char[] create_random_join_indiv(int depth, boolean type2) {
		if (depth == 0)
			return (null);
		char[] strategy = grow(depth, true, type2);
		while (strategy.length > MasterVariables.MAX_LEN)
			strategy = grow(depth, true, type2);
		char[] indiv = new char[strategy.length + 1];
		indiv[0] = (char) rd.nextInt(2);
		System.arraycopy(strategy, 0, indiv, 1, strategy.length);
		return (indiv);
	}
	
	char[][] create_random_init_indiv(int depth, boolean type2, int worldSize) {
		if (depth == 0)
			return (null);
		char[][] indiv = new char[worldSize - 1][];
		boolean[] takenIndex = new boolean[worldSize];
		char[] referentStateIndex = new char[1];

		for (int i = 0; i < indiv.length; i++) {
			char[] condition = grow(depth, true, type2);
			while (condition.length > MasterVariables.MAX_LEN)
				condition = grow(depth, true, type2);
			referentStateIndex[0] = (char) rd.nextInt(worldSize);
			while (takenIndex[(int) referentStateIndex[0]])
				referentStateIndex[0] = (char) rd.nextInt(worldSize);
			takenIndex[(int) referentStateIndex[0]] = true;

			indiv[i] = new char[1 + condition.length];
			indiv[i][0] = referentStateIndex[0];
			System.arraycopy(condition, 0, indiv[i], 1, condition.length);
		}
		return (indiv);
	}



	char[] grow(int depth, boolean returnBoolean, boolean type2) {
		char[] buffer;
		if (returnBoolean) {
			//xxx
			char prim = (char) rd.nextInt(4);
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
				prim = (char) rd.nextInt(3);
				if (prim == 0) {
					// If-then-else
					char[] condition = grow(depth - 1, true, type2);
					char[] leftBuffer = grow(depth - 1, true, type2);
					char[] rightBuffer = grow(depth - 1, true, type2);
					buffer = new char[condition.length + leftBuffer.length + rightBuffer.length + 1];
					buffer[0] = (char) MasterVariables.IF_THEN_ELSE;
					System.arraycopy(condition, 0, buffer, 1, condition.length);
					System.arraycopy(leftBuffer, 0, buffer, (1 + condition.length), leftBuffer.length);
					System.arraycopy(rightBuffer, 0, buffer, (1 + condition.length + leftBuffer.length),
							rightBuffer.length);
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
				prim = (char) rd.nextInt(5);
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
