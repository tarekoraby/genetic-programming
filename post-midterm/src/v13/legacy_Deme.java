package v13;

import java.util.Random;

public class legacy_Deme {

	private LevelStrategy[] privateLevelStrategies;
	private int level;
	private boolean isInitDeme;

	public Deme(LevelStrategy[] levelStrategies, int level, boolean isInitDeme) {
		this.level = level;
		this.isInitDeme = isInitDeme;
		setLevelStrategies(levelStrategies, level, isInitDeme);
		checkErrors();
	}

	public void setLevelStrategies(LevelStrategy[] levelStrategies, int level, boolean isInitDeme) {
		if (this.level != level || this.isInitDeme != isInitDeme) {
			System.out.println("Deme class error!!!");
			System.exit(0);
		}
		privateLevelStrategies = new LevelStrategy[levelStrategies.length];
		for (int i = 0; i < privateLevelStrategies.length; i++)
			privateLevelStrategies[i] = levelStrategies[i].deepCopy();
	}

	public LevelStrategy[] getLevelStrategies(int level, boolean isInitDeme) {
		if (this.level != level || this.isInitDeme != isInitDeme) {
			System.out.println("Deme class error!!!");
			System.exit(0);
		}
		LevelStrategy[] copyLevelStrategies = new LevelStrategy[privateLevelStrategies.length];
		for (int i = 0; i < copyLevelStrategies.length; i++)
			copyLevelStrategies[i] = privateLevelStrategies[i].deepCopy();
		return copyLevelStrategies;
	}
	
	public LevelStrategy getRandomLevelStrategy(int level, boolean isInitDeme) {
		if (this.level != level || this.isInitDeme != isInitDeme) {
			System.out.println("Deme class error!!!");
			System.exit(0);
		}
		Random rd = new Random();
		LevelStrategy RandomLevelStrategy = privateLevelStrategies[rd.nextInt(privateLevelStrategies.length)].deepCopy();
				
		return RandomLevelStrategy;
	}

	private void checkErrors() {
		if (privateLevelStrategies.length == 0 || privateLevelStrategies == null || level > MasterVariables.MAXSYSTEM) {
			System.out.println("Deme class error!!");
			System.exit(0);
		}

	}

}
