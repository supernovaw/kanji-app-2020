package supernovaw.kanjiapp2020.recognition;

import supernovaw.kanjiapp2020.main.Assets;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public final class Recognition {
	private static Map<Integer, CharacterWriting> allWritings;

	public static void load() {
		ByteBuffer buffer;

		// load writings file
		try {
			buffer = ByteBuffer.wrap(Assets.loadBytes("writings"));
		} catch (IOException e) {
			throw new Error("Cannot read writings file", e);
		}

		// read the writings
		allWritings = new HashMap<>(6744);
		while (buffer.hasRemaining()) {
			int symbol = buffer.getInt();
			buffer.position(buffer.position() + 4); // skip length info

			CharacterWriting writing = new CharacterWriting(buffer);
			allWritings.put(symbol, writing);
		}
	}

	public static String getMostSimilarCharacter(CharacterWriting answer) {
		answer.rescale();
		List<ComparisonResultEntry> variants = new ArrayList<>();
		allWritings.forEach((codepoint, writing) -> {
			double diff = answer.compare(writing);
			variants.add(new ComparisonResultEntry(codepoint, diff));
		});
		variants.sort(Comparator.comparingDouble(ComparisonResultEntry::getDifference));
		return variants.get(0).getChars();
	}

	public static boolean hasCharacter(String character) {
		if (Character.codePointCount(character, 0, character.length()) != 1)
			throw new IllegalArgumentException("There has to be one character in the input string: " + character);
		int codePoint = Character.codePointAt(character, 0);
		return allWritings.containsKey(codePoint);
	}
}
