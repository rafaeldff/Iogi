package iogi;

import com.google.common.collect.ImmutableList;

public class Parameter {
	private final String value;
	private final String name;
	private final ImmutableList<String> nameComponents;

	/** 
	 * Primary constructor.
	 * Prefer calling one of the two-argument constructors to maintain
	 * consistency between name and nameComponents.
	 */
	private Parameter(String value, String name, ImmutableList<String> nameComponents) {
		this.value = value;
		this.name = name;
		this.nameComponents = nameComponents;
	}
	
	public Parameter(String name, String value) {
		this(value, computeNameComponents(name));
	}

	private Parameter(String value, ImmutableList<String> nameComponents) {
		this(value, computeName(nameComponents), nameComponents);
	}
	
	private static ImmutableList<String> computeNameComponents(String name) {
		return ImmutableList.of(name.split("\\."));
	}

	private static String computeName(ImmutableList<String> nameComponents) {
		StringBuilder name1 = new StringBuilder();
		for (String component : nameComponents) {
			name1.append(component);
			name1.append(".");
		}
		name1.deleteCharAt(name1.length() - 1);
		return name1.toString();
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.format("Parameter(%s -> %s)", getName(), getValue());
	}

	public String getFirstNameComponent() {
		return nameComponents.get(0);
	}

	public Parameter strip() {
		if (nameComponents.size() < 2)
			return null;
		
		ImmutableList<String> componentsExceptTheFirst = nameComponents.subList(1, nameComponents.size());
		return new Parameter(value, componentsExceptTheFirst);
	}
	
	public String getName() {
		return name;
	}
}
