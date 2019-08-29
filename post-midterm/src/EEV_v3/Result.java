package EEV_v3;

public class Result {
	double EEV;
	int worldSize, attackOrder;
	double[] resDistr, terrDistr;
	
	public Result(int worldSize, int attackOrder, double EEV, double[] resDistr, double[]terrDistr) {
		this.EEV = EEV;
		this.worldSize = worldSize;
		this.attackOrder= attackOrder;
		this.resDistr= resDistr.clone();
		this.terrDistr= terrDistr.clone();
	}
}
