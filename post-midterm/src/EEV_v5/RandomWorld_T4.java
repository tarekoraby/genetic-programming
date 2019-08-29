package EEV_v5;

import java.util.Random;

public class RandomWorld_T4 {

	
	double[] resDistr, terrDistr;
	boolean isDead;
	Random rd = new Random();

	public RandomWorld_T4(double[] resDistr, double[] terrDistr) {
		if (resDistr.length!=2 | terrDistr.length!=2){
			Thread.dumpStack();
			System.exit(0);
		}
		this.resDistr = resDistr.clone();
		this.terrDistr = terrDistr.clone();

	}

	public double reCalcValue() {

		isDead = false;
		return simulateRound();
	}

	double simulateRound() {
		if (isDead)
			return 0;
		


		// determine real power as function of observed resources
		double realSideACap = resDistr[0] + (resDistr[0] * rd.nextGaussian() * 0.25);
		double realSideBCap = resDistr[1] + (resDistr[1] * rd.nextGaussian() * 0.25);

		/*if (attackOrder == testedStateIndex)
			System.out.println("AO " + attackOrder + " OAO " + orig_attackOrder + " sideA " + sideAcap + " realA "
					+ realSideACap);*/

		// System.out.println(realSideACap + " " + realSideBCap);
		//double attackersNecessarySuperiority = Math.log(1-rd.nextDouble())/(-4);
		double attackersNecessarySuperiority = 0;
		if ( realSideBCap + (realSideBCap * attackersNecessarySuperiority) > realSideACap){
			isDead = true;
			return 0;
		}

		return (1);

	}

	


}
