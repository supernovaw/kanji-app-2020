package supernovaw.kanjiapp2020.cards;

import java.util.ArrayList;
import java.util.List;

/* Stores strings with furigana hints. The source string
 * format is taken from Anki Japanese Support addon. */
public final class FuriganaText {
	public static final float FURIGANA_SCALE = 0.6f;

	private final String text;
	private final List<String> textParts, furiganaParts;

	public FuriganaText(String raw) {
		textParts = new ArrayList<>();
		furiganaParts = new ArrayList<>();
		StringBuilder text = new StringBuilder();
		for (String part : raw.split(" ")) {
			for (String subpart : part.split("]")) {
				int bracket = subpart.indexOf('[');
				if (bracket != -1 && bracket != subpart.lastIndexOf('['))
					throw new IllegalArgumentException(raw);
				String textPart;
				if (bracket == -1) {
					textParts.add(subpart);
					textPart = subpart;
					furiganaParts.add(null);
				} else {
					textPart = subpart.substring(0, bracket);
					textParts.add(textPart);
					furiganaParts.add(subpart.substring(bracket + 1));
				}
				text.append(textPart);
			}
		}
		this.text = text.toString();
	}

	public String getText() {
		return text;
	}

	public int getParts() {
		return textParts.size();
	}

	public String getTextPart(int index) {
		return textParts.get(index);
	}

	public String getFuriganaPart(int index) {
		return furiganaParts.get(index);
	}
}
