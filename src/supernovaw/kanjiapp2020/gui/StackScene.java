package supernovaw.kanjiapp2020.gui;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StackScene extends Scene {
	private final TransitionScene transition;
	private final Map<Class<? extends Scene>, Scene> sceneInstances;
	private final List<Scene> scenesStack;

	public StackScene(Scene parent, Class<? extends Scene> initialScene) {
		super(parent);
		setLayout(new StackLayout());
		transition = new TransitionScene(this);
		addElement(transition);
		sceneInstances = new HashMap<>();
		scenesStack = new ArrayList<>();

		add(initialScene);
	}

	private Scene getInstance(Class<? extends Scene> sceneClass) {
		if (sceneInstances.containsKey(sceneClass)) {
			return sceneInstances.get(sceneClass);
		} else {
			Constructor<? extends Scene> constructor;
			try {
				constructor = sceneClass.getConstructor(Scene.class);
			} catch (NoSuchMethodException e) {
				throw new Error("Cannot instantiate the given scene class with required constructor", e);
			}
			try {
				Scene instance = constructor.newInstance(transition);
				sceneInstances.put(sceneClass, instance);
				return instance;
			} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
				throw new Error("Failed to create a new scene of the given class", e);
			}
		}
	}

	public void add(Class<? extends Scene> sceneClass) {
		Scene instance = getInstance(sceneClass);
		boolean success = transition.setCurrent(instance);
		if (success) {
			scenesStack.add(instance);
		}
	}

	public void change(Class<? extends Scene> sceneClass) {
		Scene instance = getInstance(sceneClass);
		boolean success = transition.setCurrent(instance);
		if (success) {
			scenesStack.remove(scenesStack.size() - 1);
			scenesStack.add(instance);
		}
	}

	public void remove() {
		if (scenesStack.size() < 2) throw new Error("The stack contains " + scenesStack.size() +
				" scene(s). There has to be at least one remaining after the removal.");
		boolean success = transition.setCurrent(scenesStack.get(scenesStack.size() - 2));
		if (success) {
			scenesStack.remove(scenesStack.size() - 1);
		}
	}

	private static class StackLayout extends Layout {
		private int width, height;

		@Override
		public void addElement(Element e) {
		}

		@Override
		public void removeElement(Element e) {
		}

		@Override
		public Rectangle getBounds(Element e) {
			return new Rectangle(width, height);
		}

		@Override
		public void setSize(int width, int height) {
			this.width = width;
			this.height = height;
		}

		@Override
		public Dimension getSize() {
			return new Dimension(width, height);
		}
	}
}
