package supernovaw.kanjiapp2020.cards;

import supernovaw.kanjiapp2020.main.Assets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CardsGroup {
	private final String name;
	private final Card[] cards;

	public CardsGroup(String name) {
		this.name = name;
		String file;
		try {
			file = new String(Assets.loadBytes("kanji_groups/" + name + ".txt"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new Error(e);
		}
		file = file.replaceAll("\r\n", "\n").replaceAll("\r", "\n");
		String[] lines = file.split("\n");

		cards = new Card[lines.length];
		for (int i = 0; i < lines.length; i++)
			cards[i] = new Card(lines[i]);
	}

	public String getName() {
		return name;
	}

	public Card[] getCards() {
		return cards;
	}
}
