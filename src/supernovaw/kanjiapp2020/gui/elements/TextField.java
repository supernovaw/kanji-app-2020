package supernovaw.kanjiapp2020.gui.elements;

import supernovaw.kanjiapp2020.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TextField extends Element {
	private static final Color SELECTION_FILL_COLOR = new Color(0x3c0078d7, true);

	private final int offsetXFixed; // X offset from bounds start. Letters like 'j' stick out to the left and need this gap
	private int offsetX; // offset to paint text, may vary if text is larger than bounds
	private String text;
	private int caretPos, selectionFromPos;
	private final Font font;
	private final Font hintFont;
	private final int slideWhenFading; // distance text slides up when fading away
	private final int caretHeight; // caret and selection height
	private boolean hold; // for selecting text by dragging

	private final String hintText; // when no focus and no text
	private final Ease hintDisplayEase;
	private final Ease focusEase;
	private final Runnable onEnter;

	// fadingText has object parameters String (text) and int (offsetX)
	private final FadeOutAnimation fadingText;

	public TextField(Scene parent, String hintText, Runnable onEnter) {
		super(parent);

		text = "";
		this.onEnter = onEnter;

		hintDisplayEase = new Ease(300, this);
		hintDisplayEase.setInitially(text.isEmpty());
		this.hintText = hintText;

		font = Theme.getUiFont();
		float fontSize = font.getSize2D();
		hintFont = Theme.getUiFont();

		caretHeight = (int) (fontSize * 1.2d);
		offsetXFixed = offsetX = (int) fontSize / 2;
		slideWhenFading = (int) (0.7d * fontSize);

		focusEase = new Ease(300, this);
		fadingText = new FadeOutAnimation(400, () -> {
			Rectangle r = getBounds();
			repaint(new Rectangle(r.x, r.y - slideWhenFading, r.width, r.height + slideWhenFading));
		});
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		caretPos = selectionFromPos = text.length(); // set caret to the end
		offsetX = offsetXFixed; // reset offset
		repaint();
	}

	public String flushText() {
		String oldText = text;
		text = "";
		if (!oldText.isBlank()) {
			fadingText.animate(oldText, offsetX);
		}
		caretPos = selectionFromPos = 0;
		offsetX = offsetXFixed;
		return oldText;
	}

	public void triggerFocus() {
		focusEase.setSwitched(true);
		updateHintState();
	}

	private void updateHintState() {
		hintDisplayEase.setSwitched(text.isEmpty());
	}

	@Override
	protected void paint(Graphics2D g) {
		Shape clipBefore = g.getClip();
		Rectangle r = getBounds();
		Rectangle clipCurrent = new Rectangle(r.x, r.y - slideWhenFading, r.width, r.height + slideWhenFading);
		g.setClip(clipBefore.getBounds().createIntersection(clipCurrent)); // don't let the text to be painted outside the bounds

		paintHintText(g);

		g.setFont(font);
		paintFadingText(g);

		g.setColor(Theme.foreground());
		g.fill(TextUtils.getTextArea(text, r.x + offsetX, TextUtils.centerStringY(g, r.y + r.height / 2), g)); // paint text

		double focusInOut = focusEase.getEaseInOutCubic();
		float focusSine = (float) focusEase.getEaseInOutSine();
		int caretX = r.x + offsetX + stringWidth(text.substring(0, caretPos));
		int selectionX = r.x + offsetX + stringWidth(text.substring(0, selectionFromPos));

		if (selectionFromPos != caretPos) { // if there are characters selected
			Color selectionFill = SELECTION_FILL_COLOR;
			int newAlpha = (int) (selectionFill.getAlpha() * focusSine);
			selectionFill = new Color((selectionFill.getRGB() & 0xffffff) | (newAlpha << 24), true);
			g.setColor(selectionFill);
			g.fillRect(Math.min(caretX, selectionX), r.y + (r.height - caretHeight) / 2,
					Math.abs(caretX - selectionX), caretHeight);
		}
		g.setColor(Theme.foreground(focusSine)); // paint caret
		g.drawLine(caretX, r.y + (r.height - caretHeight) / 2, caretX, r.y + (r.height + caretHeight) / 2);

		if (focusInOut != 1) {
			int underlineWidth = (int) ((1d - focusInOut) * r.width);
			int underlineY = r.y + r.height - 1;
			g.setColor(Theme.foreground(1 - focusSine));
			g.drawLine(r.x + (r.width - underlineWidth) / 2, underlineY, r.x + (r.width + underlineWidth) / 2, underlineY);
		}

		g.setClip(clipBefore);
	}

	private void paintHintText(Graphics2D g) {
		double phase = hintDisplayEase.getEaseInOutSine();
		if (phase == 0) return;
		Rectangle r = getBounds();

		g.setColor(Theme.foreground(phase * 0.45));
		g.setFont(hintFont);
		g.fill(TextUtils.getTextArea(hintText, r.x + offsetXFixed, TextUtils.centerStringY(g, r.y + r.height / 2), g));
	}

	private void paintFadingText(Graphics2D g) {
		if (!fadingText.isAnimating()) return;
		Rectangle r = getBounds();

		String fadingString = (String) fadingText.getParameter(0);
		int fadingTextOffset = (int) fadingText.getParameter(1);

		double phase = fadingText.getPhase();
		g.setColor(Theme.foreground(Ease.easeInOutSine(1d - phase)));
		int slide = (int) (slideWhenFading * Ease.easeOutCubic(phase));
		g.fill(TextUtils.getTextArea(fadingString, r.x + fadingTextOffset, TextUtils.centerStringY(g, r.y + r.height / 2) - slide, g));
	}

	private int stringWidth(String s) {
		return (int) TextUtils.stringWidth(s, font);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;

		boolean in = getBounds().contains(e.getPoint());
		focusEase.setSwitched(in);
		updateHintState();
		if (!in) return;

		setCaret(getPos(e.getX() - getBounds().x - offsetX), true); // set caret to where mouse clicked
		hold = true; // indicate that mouse drags starting inside bounds
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		hold = false; // stop dragging (holding)
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (hold) { // if started dragging inside bounds
			setCaret(getPos(e.getX() - getBounds().x - offsetX), false); // set selection accordingly to mouse position
			repaint();
		}
	}

	// gets caret position corresponding to X value (without counting x() and offset)
	private int getPos(int x) {
		if (x <= 0)
			return 0;

		if (x >= stringWidth(text))
			return text.length();

		for (int i = 1; true; i++) {
			int stringW = stringWidth(text.substring(0, i));
			if (stringW < x)
				continue;

			int charW = stringWidth(text.substring(i - 1, i));
			if (stringW - x > charW / 2d) // if mouse is closer to the left position, subtract 1
				return i - 1;
			else
				return i;
		}
	}

	// is setSelection=true, selection is cleared, otherwise not affected
	private void setCaret(int pos, boolean setSelection) {
		Rectangle r = getBounds();
		// assign new values
		caretPos = pos;
		if (setSelection)
			selectionFromPos = pos;

		// if caret is this close to bounds, move text X offset to increase gap
		int wrapFrom = r.width / 4;

		// width of text from start to caret
		int textW = stringWidth(text.substring(0, pos));
		int caretGapLeft = textW + offsetX;
		int caretGapRight = r.width - caretGapLeft;

		int newOffset = offsetX; // in case it will not be affected, use old value
		if (caretGapRight < wrapFrom) {
			newOffset = -textW + r.width - wrapFrom;
		} else if (caretGapLeft < wrapFrom) {
			newOffset = -textW + wrapFrom;
		}
		newOffset = Math.min(newOffset, offsetXFixed); // prevent empty gap with no text on the left
		offsetX = newOffset;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (!focusEase.isSwitched())
			return; // if no focus
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			onEnter.run();
			return;
		}

		boolean isChar = e.getKeyCode() != KeyEvent.CHAR_UNDEFINED &&
				!e.isActionKey() && font.canDisplay(e.getKeyChar());
		if (isChar) {
			type(e.getKeyChar()); // case for letters, characters, punctuation, etc.
		} else {
			executeEditingShortcuts(e);
		}

		updateHintState();
		repaint();
	}

	// actions like Left Arrow, Ctrl + C, Shift + Ins
	private void executeEditingShortcuts(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_BACK_SPACE: // (Ctrl)+Backspace
				keyBackspace(e);
				break;
			case KeyEvent.VK_DELETE: // (Ctrl)+(Shift)+Delete
				keyDelete(e);
				break;
			case KeyEvent.VK_LEFT: // (Ctrl)+(Shift)+Left
				keyLeft(e);
				break;
			case KeyEvent.VK_RIGHT: // (Ctrl)+(Shift)+Right
				keyRight(e);
				break;
			case KeyEvent.VK_V: // Ctrl+V
				if (e.isControlDown())
					paste();
				break;
			case KeyEvent.VK_INSERT: // (Ctrl)+(Shift)+Insert
				if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK)
					paste();
				if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK)
					copy();
				break;
			case KeyEvent.VK_C: // Ctrl+C
				if (e.isControlDown())
					copy();
				break;
			case KeyEvent.VK_UP: // (Shift)+Up
			case KeyEvent.VK_HOME: // (Shift)+Home
				caretToBeginning(e);
				break;
			case KeyEvent.VK_DOWN: // (Shift)+Down
			case KeyEvent.VK_END: // (Shift)+End
				caretToEnd(e);
				break;
			case KeyEvent.VK_A: // Ctrl+A
				if (e.isControlDown())
					selectAll();
				break;
			case KeyEvent.VK_X: // Ctrl+X
				if (e.isControlDown())
					cut();
				break;
		}
	}

	private void type(char c) {
		int min = Math.min(selectionFromPos, caretPos);
		int max = Math.max(selectionFromPos, caretPos);

		// replace selected text (or an empty area) with typed character
		text = text.substring(0, min) + c + text.substring(max);
		setCaret(min + 1, true);
	}

	private void keyBackspace(KeyEvent e) {
		if (selectionFromPos != caretPos) { // if deleting selected area
			int min = Math.min(selectionFromPos, caretPos);
			int max = Math.max(selectionFromPos, caretPos);
			text = text.substring(0, min) + text.substring(max);
			setCaret(min, true);
		} else { // if deleting single character or word
			if (caretPos == 0) // if nothing to delete
				return;

			if (e.isControlDown()) { // delete a word
				int i = ctrlLeft();
				text = text.substring(0, i) + text.substring(caretPos);
				setCaret(i, true);
			} else { // delete a character
				text = text.substring(0, caretPos - 1) + text.substring(caretPos);
				setCaret(caretPos - 1, true);
			}
		}
	}

	private void keyDelete(KeyEvent e) {
		if (selectionFromPos != caretPos) { // if deleting selected area
			int min = Math.min(selectionFromPos, caretPos);
			int max = Math.max(selectionFromPos, caretPos);

			if (e.getModifiersEx() == KeyEvent.SHIFT_DOWN_MASK) {
				StringSelection sel = new StringSelection(text.substring(min, max));
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
			}

			text = text.substring(0, min) + text.substring(max);
			setCaret(min, true);
		} else { // if deleting single character or word
			if (caretPos == text.length()) // if nothing to delete
				return;

			if (e.isControlDown()) { // delete a word
				int i = ctrlRight();
				text = text.substring(0, caretPos) + text.substring(i);
			} else { // delete a character
				text = text.substring(0, caretPos) + text.substring(caretPos + 1);
			}
		}
	}

	private void keyLeft(KeyEvent e) {
		if (caretPos == 0) { // if nowhere to move
			if (!e.isShiftDown()) // if shift isn't held, reset selection
				selectionFromPos = caretPos;
			return;
		}
		int newCaretPos = e.isControlDown() ? ctrlLeft() : caretPos - 1;
		setCaret(newCaretPos, !e.isShiftDown());
	}

	private void keyRight(KeyEvent e) {
		if (caretPos == text.length()) { // if nowhere to move
			if (!e.isShiftDown()) // if shift isn't held, reset selection
				selectionFromPos = caretPos;
			return;
		}
		int newCaretPos = e.isControlDown() ? ctrlRight() : caretPos + 1;
		setCaret(newCaretPos, !e.isShiftDown());
	}

	private void paste() {
		try {
			String clipboard = (String) Toolkit.getDefaultToolkit()
					.getSystemClipboard().getData(DataFlavor.stringFlavor);
			int min = Math.min(selectionFromPos, caretPos);
			int max = Math.max(selectionFromPos, caretPos);

			// replace selected text (or an empty area) with clipboard contents
			text = text.substring(0, min) + clipboard + text.substring(max);
			setCaret(min + clipboard.length(), true);
		} catch (Exception ignore) { // empty or non-text clipboard, ignore
		}
	}

	private void copy() {
		int min = Math.min(selectionFromPos, caretPos);
		int max = Math.max(selectionFromPos, caretPos);
		if (min == max) // if nothing selected
			return;
		StringSelection sel = new StringSelection(text.substring(min, max));
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
	}

	private void cut() {
		int min = Math.min(selectionFromPos, caretPos);
		int max = Math.max(selectionFromPos, caretPos);
		if (min == max) // if nothing selected
			return;
		StringSelection sel = new StringSelection(text.substring(min, max));
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
		text = text.substring(0, min) + text.substring(max); // cut copied area
		setCaret(min, true);
	}

	private void caretToBeginning(KeyEvent e) {
		// if shift is held, make a selection by not affecting selectionFromPos
		setCaret(0, !e.isShiftDown());
	}

	private void caretToEnd(KeyEvent e) {
		// if shift is held, make a selection by not affecting selectionFromPos
		setCaret(text.length(), !e.isShiftDown());
	}

	private void selectAll() {
		selectionFromPos = 0;
		setCaret(text.length(), false);
	}

	// calculate new caret position for moving with Ctrl held
	private int ctrlLeft() {
		int index = caretPos - 1;
		while (index != 0) {
			if (text.charAt(index) == ' ')
				index--;
			else
				break;
		}
		while (index != 0) {
			if (text.charAt(index - 1) != ' ')
				index--;
			else
				break;
		}
		return index;
	}

	// calculate new caret position for moving with Ctrl held
	private int ctrlRight() {
		int index = caretPos;
		while (index != text.length()) {
			if (text.charAt(index) != ' ')
				index++;
			else
				break;
		}
		while (index != text.length()) {
			if (text.charAt(index) == ' ')
				index++;
			else
				break;
		}
		return index;
	}

	@Override
	protected void setDisplayed(boolean displayed) {
		hintDisplayEase.setDisplayed(displayed);
		focusEase.setDisplayed(displayed);
		if (!displayed) updateHintState();
		fadingText.setDisplayed(displayed);
	}
}
