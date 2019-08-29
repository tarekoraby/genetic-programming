package EEV_v1;

public class Act {
	int attackOrder;
	double EEV;


	public Act(int attackOrder) {
		this.attackOrder = attackOrder;
	}

	public Act deepCopy() {
		Act actCopy = new Act(attackOrder);
		return actCopy;
	}

	
}
