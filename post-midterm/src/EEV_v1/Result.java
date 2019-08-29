package EEV_v1;

public class Result {
	double EEV;
	int worldSize, attackOrder;
	int[] resDistr, terrDistr;
	
	public Result(int worldSize, int attackOrder, double EEV, int[] resDistr, int[]terrDistr) {
		this.EEV = EEV;
		this.worldSize = worldSize;
		this.attackOrder= attackOrder;
		this.resDistr= resDistr.clone();
		this.terrDistr= terrDistr.clone();
	}
}
