public class Member implements Comparable<Member> {

	private boolean[] selection;
	private double weight;
	private double value;
	private double fitness;
	private double normalizedFitness;

	public Member(boolean[] selection) {
		this.selection = selection.clone();
	}

	public boolean[] getSelection() {
		return selection.clone();
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		if (fitness < 0 || fitness > 1) {
			throw new IllegalArgumentException("Fitness must be between 0 and 1");
		}

		this.fitness = fitness;
	}

	public double getNormalizedFitness() {
		return normalizedFitness;
	}

	public void setNormalizedFitness(double normalizedFitness) {
		if (normalizedFitness < 0 || normalizedFitness > 1) {
			throw new IllegalArgumentException("Normalized fitness must be between 0 and 1");
		}

		this.normalizedFitness = normalizedFitness;
	}

	public void mutate(double rate) {
		for (int i = 0; i < selection.length; i++) {
			if (Math.random() <= rate) {
				selection[i] = !selection[i];
			}
		}
	}

	@Override
	public int compareTo(Member other) {

		// Sort by highest fitness
		if (fitness > other.fitness) {
			return -1;
		} else if (fitness < other.fitness) {
			return 1;
		} else {
			return 0;
		}
	}

	public Member copy() {
		return new Member(selection);
	}
}
