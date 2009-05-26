package iogi.parameters;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class Parameter {
	private final String name;
	private final String value;
	private final ImmutableList<String> nameComponents;

	public Parameter(String name, String value) {
		this(notNull(name, "Parameter name"), notNull(value, "Paramter value"), computeNameComponents(name));
	}
	
	/** 
	 * Primary constructor.
	 * Prefer calling one of the two-argument constructors to maintain
	 * consistency between name and nameComponents.
	 */
	private Parameter(String name, String value, ImmutableList<String> nameComponents) {
		this.name = name;
		this.value = value;
		this.nameComponents = nameComponents;
	}
	
	private Parameter(String value, ImmutableList<String> nameComponents) {
		this(computeName(nameComponents), value, nameComponents);
	}
	
	private static <T> T notNull(T objeto, String name) {
		if (objeto == null)
			throw new IllegalArgumentException(name + " cannot be null");
		return objeto;
	}
	
	private static ImmutableList<String> computeNameComponents(String name) {
		return ImmutableList.of(name.split("\\."));
	}

	private static String computeName(ImmutableList<String> nameComponents) {
		return Joiner.on(".").join(nameComponents);
	}

	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getFirstNameComponent() {
		return nameComponents.get(0).replaceAll("\\[\\d+\\]", "");
	}

	public Parameter strip() {
		if (nameComponents.size() < 2)
			return null;
		
		ImmutableList<String> componentsExceptTheFirst = nameComponents.subList(1, nameComponents.size());
		return new Parameter(value, componentsExceptTheFirst);
	}
	
	@Override
	public String toString() {
		return String.format("Parameter(%s -> %s)", getName(), getValue());
	}

	public String getFirstNameComponentWithDecoration() {
		return name.split("\\.")[0];
	}
}
