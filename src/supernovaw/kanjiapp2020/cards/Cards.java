package supernovaw.kanjiapp2020.cards;

import java.util.ArrayList;
import java.util.List;

public final class Cards {
	private static List<CardsGroup> groups;
	private static List<Card> currentGroup;

	public static void load() {
		groups = new ArrayList<>();
		groups.add(new CardsGroup("N5"));
	}

	public static List<CardsGroup> getGroups() {
		return groups;
	}

	public static void setCurrentGroup(List<Card> list) {
		currentGroup = list;
	}

	public static List<Card> getCurrentGroup() {
		return currentGroup;
	}
}
