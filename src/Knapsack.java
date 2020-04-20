import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class Knapsack {

	private static final Path CONFIG_PATH = Paths.get("config.properties");

	private double[][] items;
	private double totalWeight;
	private double totalValue;

	public Knapsack(String filepath) throws IOException {

		// Read the lines from the file
		List<String> lines = Files.readAllLines(Paths.get(filepath));

		// Initialize and populate the items
		items = new double[lines.size()][2];

		// Each item has a weight and a value
		for (int line = 0; line < lines.size(); line++) {
			String[] item = lines.get(line).split(",");

			items[line][0] = Double.parseDouble(item[0].strip());
			items[line][1] = Double.parseDouble(item[1].strip());

			totalWeight += items[line][0];
			totalValue += items[line][1];
		}
	}

	public static void main(String[] args) throws IOException {

		// Get an instance of KeyboardInputClass to get command line input from the user
		KeyboardInputClass kb = new KeyboardInputClass();

		// Get the path to the file containing the knapsack data
		String filepath = kb.getString("Enter the path to the file containing knapsack data: ", "items.txt");

		// Create a new knapsack instance with the specified file
		Knapsack k = new Knapsack(filepath);

		// Load the configuration values from the properties file
		try (InputStream stream = Files.newInputStream(CONFIG_PATH)) {

			Properties config = new Properties();

			config.load(stream);

			double maxWeight = Double.parseDouble(config.getProperty("max_weight"));
			int populationSize = Integer.parseInt(config.getProperty("population_size"));
			double crossoverRate = Double.parseDouble(config.getProperty("crossover_rate"));
			double mutationRate = Double.parseDouble(config.getProperty("mutation_rate"));

			System.out.println("Creating a new population with the following parameters:\n");
			System.out.println("Total items:        " + k.items.length);
			System.out.println("Total weight:       " + k.totalWeight);
			System.out.println("Total value:        " + k.totalValue);
			System.out.println("Weight limit:       " + maxWeight);
			System.out.println("Population size:    " + populationSize);
			System.out.println("Crossover rate:     " + crossoverRate);
			System.out.println("Mutation rate:      " + mutationRate + "\n");

			// Create a new population with the specified values
			Population p = new Population(k.items, k.totalValue, maxWeight, populationSize, crossoverRate,
					mutationRate);

			kb.getCharacter("Press ENTER to begin evolution: ", "YN", 'Y');

			// Evolve the population
			while (true) {
				p.printSummary();
				p.evolve(1);
			}
		}
	}
}
