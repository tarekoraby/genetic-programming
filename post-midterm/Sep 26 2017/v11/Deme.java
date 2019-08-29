package v11;


public class Deme {

	LevelStrategy[] strategies;
	int level;

	public Deme(LevelStrategy[] strategies, int level) {
		this.strategies = strategies;
		this.level = level;
		checkErrors();
	}

	private void checkErrors() {
		if (strategies.length == 0 || strategies == null || level > MasterVariables.MAXSYSTEM) {
			System.out.println("Deme class error!!");
			System.exit(0);
		}

	}

}
