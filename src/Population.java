import java.util.Arrays;

public class Population {

	private int generation = 0;

	private Member[] members;
	private double totalFitness;

	private final double[][] items;
	private final double totalValue;
	private final double maxWeight;

	private final int size;
	private final double crossoverRate;
	private final double mutationRate;

	public Population(double[][] items, double totalValue, double maxWeight, int size, double crossoverRate,
			double mutationRate) {

		if (size < 2 || size % 2 != 0) {
			throw new IllegalArgumentException("Population size must be a positive, even number");
		}

		if (crossoverRate < 0 || crossoverRate > 1) {
			throw new IllegalArgumentException("Crossover rate must be a decimal between 0 and 1 (inclusive)");
		}

		if (mutationRate < 0 || mutationRate > 1) {
			throw new IllegalArgumentException("Mutation rate must be a decimal between 0 and 1 (inclusive)");
		}

		this.items = items;
		this.totalValue = totalValue;
		this.maxWeight = maxWeight;
		this.size = size;
		this.crossoverRate = crossoverRate;
		this.mutationRate = mutationRate;

		// Create the members for this population
		createPopulation();
	}

	private void createPopulation() {

		// Initialize the members array
		members = new Member[size];

		// Create each member of the population
		for (int i = 0; i < members.length; i++) {

			boolean[] selection = new boolean[items.length];

			double memberWeight = 0;
			double itemCount = 0;

			// Create a random selection that is not overweight
			while (memberWeight < maxWeight) {
				if (itemCount == selection.length)
					break;

				int randomIndex = (int) (Math.random() * selection.length);

				// If this item hasn't been selected yet
				if (!selection[randomIndex]) {

					// Get the weight of this item
					double itemWeight = items[randomIndex][0];

					// Check that adding this item won't put this member over the weight limit
					if (memberWeight + itemWeight <= maxWeight) {
						selection[randomIndex] = true;
						memberWeight += itemWeight;
						itemCount += 1;

					} else {
						break;
					}
				}
			}

			members[i] = new Member(selection);
		}

		scoreMembers();
	}

	private void scoreMembers() {

		// Keep track of the total fitness of the population
		totalFitness = 0;

		// For each member of the population
		for (int i = 0; i < members.length; i++) {
			double memberWeight = 0, memberValue = 0, memberFitness = 0;

			boolean[] memberItems = members[i].getSelection();

			// Add up the weight and value of the member's items
			for (int j = 0; j < memberItems.length; j++) {
				if (memberItems[j]) {
					memberWeight += items[j][0];
					memberValue += items[j][1];
				}
			}

			// A member must not be overweight, or its fitness will be zero
			if (memberWeight <= maxWeight) {

				// Maximize for the value of the selected items
				memberFitness += memberValue / totalValue;
			}

			// Add this member's fitness to the running total
			totalFitness += memberFitness;

			// Set the calculated values for this member
			members[i].setWeight(memberWeight);
			members[i].setValue(memberValue);
			members[i].setFitness(memberFitness);
		}

		// Calculate the normalized fitness for each member
		for (Member member : members) {
			double normalizedFitness = member.getFitness() / totalFitness;
			member.setNormalizedFitness(normalizedFitness);
		}

		// Sort the list of members
		Arrays.sort(members);
	}

	public void evolve(int generations) {

		for (int i = 0; i < generations; i++) {

			// Save the best member from the current generation
			Member previousBestMember = members[0];

			// Create a new generation of members
			Member[] nextGeneration = new Member[size];

			for (int j = 0; j < members.length; j += 2) {

				// Get a pair of members from the current generation
				Member[] pair = pairSelection();

				// Decide if the pair of members will crossover
				if (Math.random() <= crossoverRate) {
					pair = pairCrossover(pair);
				}

				// Assign the pair to the next generation
				nextGeneration[j + 0] = pair[0];
				nextGeneration[j + 1] = pair[1];
			}

			// Mutate the members of the new generation (with some probability)
			for (Member m : nextGeneration) {
				m.mutate(mutationRate);
			}

			// Replace the current generation with the next one
			members = nextGeneration;

			// Score the new generation
			scoreMembers();

			// If the best member from the last generation was more fit, keep them around
			if (previousBestMember.getFitness() > members[0].getFitness()) {

				// Replace the least fit member of the new generation with this old member
				members[members.length - 1] = previousBestMember;

				// Re-score the members
				scoreMembers();
			}

			// Increment the generation count
			generation += 1;
		}
	}

	public Member[] pairSelection() {

		// Create a pair of members that will be returned
		Member[] pair = new Member[2];

		// If no members are currently viable, then randomly select two members
		// ... otherwise, select with a preference for higher fitness
		if (members[0].getFitness() == 0) {
			pair[0] = members[(int) (Math.random() * members.length)].copy();
			pair[1] = members[(int) (Math.random() * members.length)].copy();

		} else {
			double threshold = Math.random();
			double summation = 0;

			// Choose the first member
			for (int i = 0; i < members.length; i++) {
				summation += members[i].getNormalizedFitness();

				if (summation >= threshold) {
					pair[0] = members[i].copy();
					break;
				}
			}

			threshold = Math.random();
			summation = 0;

			// Choose the second member
			for (int j = 0; j < members.length; j++) {
				summation += members[j].getNormalizedFitness();

				if (summation >= threshold) {
					pair[1] = members[j].copy();
					break;
				}
			}
		}

		return pair;
	}

	public static Member[] pairCrossover(Member[] pair) {

		// Get the item arrays for each member in the pair
		boolean[] parentItemsA = pair[0].getSelection();
		boolean[] parentItemsB = pair[1].getSelection();

		// Check that the item arrays are of the same length
		if (parentItemsA.length != parentItemsB.length) {
			throw new IllegalArgumentException("Member item arrays must be of equal length");
		}

		// Choose a random crossover point
		int crossover = (int) (Math.random() * parentItemsA.length);

		// Create and populate the item arrays for the children
		boolean[] childItemsA = new boolean[parentItemsA.length];
		boolean[] childItemsB = new boolean[parentItemsA.length];

		for (int i = 0; i < crossover; i++) {
			childItemsA[i] = parentItemsA[i];
			childItemsB[i] = parentItemsB[i];
		}

		for (int i = crossover; i < parentItemsA.length; i++) {
			childItemsA[i] = parentItemsB[i];
			childItemsB[i] = parentItemsA[i];
		}

		// Create, populate, and return the member array for the children
		Member[] children = new Member[2];

		children[0] = new Member(childItemsA);
		children[1] = new Member(childItemsB);

		return children;
	}

	public void printSummary() {
		System.out.printf("Generation %d: Weight = %.0f; Value = %.0f; Fitness = %.3f\n", generation,
				members[0].getWeight(), members[0].getValue(), members[0].getFitness());
	}
}
