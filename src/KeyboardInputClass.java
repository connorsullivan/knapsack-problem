import java.io.*;

public class KeyboardInputClass {

	private final BufferedReader buffer;

	public KeyboardInputClass() {
		buffer = new BufferedReader(new InputStreamReader(System.in));
	}

	private String getKeyboardInput(String prompt) {

		System.out.println(prompt);

		String inputString = "";

		try {
			inputString = buffer.readLine();
		} catch (IOException e) {
		}

		if (!inputString.isEmpty()) {
			System.out.println();
		}

		return inputString;
	}

	public String getString(String prompt, String defaultValue) {

		String inputString = getKeyboardInput(prompt);

		if (inputString.isEmpty()) {
			inputString = defaultValue;
		}

		return inputString;
	}

	public int getInteger(String prompt, int minValue, int maxValue, int defaultValue) {

		while (true) {

			// Get the input from the user
			String inputString = getKeyboardInput(prompt);

			// If the input is empty, return the default value
			if (inputString.isEmpty()) {
				return defaultValue;

			} else {
				try {

					// Try to parse the input
					int result = Integer.parseInt(inputString);

					// Check that the parsed input is within the specified limits
					if (result >= minValue && result <= maxValue) {
						return result;
					}

				} catch (NumberFormatException e) {
				}
			}

			// If this point is reached, there was a problem with the input
			System.out.println("Please choose an integer between " + minValue + " and " + maxValue + ".\n");

		}
	}

	public double getDouble(String prompt, double minValue, double maxValue, double defaultValue) {

		while (true) {

			// Get the input from the user
			String inputString = getKeyboardInput(prompt);

			// If the input is empty, return the default value
			if (inputString.isEmpty()) {
				return defaultValue;

			} else {
				try {

					// Try to parse the input
					double result = Double.parseDouble(inputString);

					// Check that the parsed input is within the specified limits
					if (result >= minValue && result <= maxValue) {
						return result;
					}

				} catch (NumberFormatException e) {
				}
			}

			// If this point is reached, there was a problem with the input
			System.out.println("Please choose a number between " + minValue + " and " + maxValue + ".\n");

		}
	}

	public char getCharacter(String prompt, String validEntries, char defaultValue) {

		while (true) {

			// Get the input from the user
			String inputString = getKeyboardInput(prompt);

			// If the input is empty, return the default value
			if (inputString.isEmpty()) {
				return defaultValue;

			} else {
				try {

					// Try to parse the input
					char result = inputString.toUpperCase().charAt(0);

					// Check that the parsed character is valid
					if (validEntries.toUpperCase().indexOf(result) >= 0) {
						return result;
					}

				} catch (NumberFormatException e) {
				}
			}
		}
	}
}
