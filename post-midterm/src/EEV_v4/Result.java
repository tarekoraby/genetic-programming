package EEV_v4;

public class Result {
	double EEV;
	int worldSize, attackOrder;
	double[] resDistr, terrDistr;
	
	public Result(int worldSize, int attackOrder,  double EEV, double[] resDistr, double[]terrDistr) {
		this.EEV = EEV;
		this.worldSize = worldSize;
		this.resDistr= resDistr.clone();
		this.terrDistr= terrDistr.clone();
		this.attackOrder = attackOrder;
	}
}
