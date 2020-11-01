package supernovaw.kanjiapp2020.cards;

import supernovaw.kanjiapp2020.recognition.Recognition;

public class Card {
	private String kanji;
	private String readingOn;
	private String readingKun;
	private String meaning;
	private int examples;
	private String[] exampleWords, exampleTranslations;

	public Card(String line) {
		String[] split = line.split("\t");
		if (split.length < 4) throw new IllegalArgumentException("Not enough parts in line '" + line + "'");
		if (split.length % 2 != 0) throw new IllegalArgumentException("Odd number of parts in line '" + line + "'");

		kanji = split[0];
		readingOn = split[1];
		readingKun = split[2];
		meaning = split[3];
		examples = (split.length - 4) / 2;

		exampleWords = new String[examples];
		exampleTranslations = new String[examples];
		for (int i = 0; i < examples; i++) {
			exampleWords[i] = split[4 + 2 * i];
			exampleTranslations[i] = split[5 + 2 * i];
		}

		if (Character.codePointCount(kanji, 0, kanji.length()) != 1) {
			throw new IllegalArgumentException("There has to be one character for this line's kanji: '" + line + "'");
		}
		if (!Recognition.hasCharacter(kanji)) {
			throw new IllegalArgumentException("There is no character " + kanji + " in the recognition sample characters set");
		}
	}
}
