package iogi;

import java.util.Arrays;
import java.util.LinkedList;

public class Parameter {
	private final String value;
	private LinkedList<String> nameComponents;

	public Parameter(String name, String value) {
		this(value, new LinkedList<String>(Arrays.asList(name.split("\\."))));
	}

	private Parameter(String value, LinkedList<String> nameComponents) {
		this.value = value;
		this.nameComponents = nameComponents;
	}

	public String getName() {
		StringBuilder name = new StringBuilder();
		for (String component : nameComponents) {
			name.append(component);
			name.append(".");
		}
		name.deleteCharAt(name.length() - 1);
		return name.toString();
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format("Parameter(%s -> %s)", getName(), getValue());
	}

	public String getFirstNameComponent() {
		return nameComponents.getFirst();
	}

	public Parameter strip() {
		if (nameComponents.size() < 2)
			return null;
		
		LinkedList<String> newNameComponents = new LinkedList<String>(nameComponents);
		newNameComponents.removeFirst();
		return new Parameter(value, newNameComponents);
	}
}
