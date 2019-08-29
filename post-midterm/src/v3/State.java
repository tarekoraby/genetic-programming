package v3;
public class State {
	Strategy strategy;
	double capability;

	State(Strategy strategy, double capability) {
		this.strategy=strategy;
		this.capability = capability;
	}
}
