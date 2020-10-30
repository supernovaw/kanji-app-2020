package supernovaw.kanjiapp2020.recognition;

// used in comparison of a written answer to make a list of possible words
public class ComparisonResultEntry {
	private final int codepoint;
	private final double difference; // can range from 0 (most similar) to 1 (most dissimilar)

	ComparisonResultEntry(int codepoint, double difference) {
		this.codepoint = codepoint;
		this.difference = difference;
	}

	int getCodepoint() {
		return codepoint;
	}

	String getChars() {
		return new String(Character.toChars(codepoint));
	}

	double getDifference() {
		return difference;
	}
}
