package br.com.caelum.iogi.parameters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

public class Parameter implements Comparable<Parameter> {
	private static final Pattern DECORATION_REGEX = Pattern.compile("\\[\\d+\\]$");
	private static final Pattern INDEX_PATTERN = Pattern.compile("\\[([^\\]]+)]");

	private final String value;
	private final ImmutableList<String> nameComponents;

	public Parameter(final String name, final String value) {
		this(notNull(value, "Parameter value"), computeNameComponents(notNull(name, "Parameter name")));
	}
	
	private Parameter(final String value, final ImmutableList<String> nameComponents) {
		this.value = value;
		this.nameComponents = nameComponents;
	}
	
	private static <T> T notNull(final T object, final String name) {
		if (object == null)
			throw new IllegalArgumentException(name + " cannot be null");
		return object;
	}
	
	private static ImmutableList<String> computeNameComponents(final String name) {
		return ImmutableList.copyOf(name.split("\\."));
	}

	public String getName() {
		return Joiner.on(".").join(nameComponents);
	}
	
	public String getValue() {
		return value;
	}
	
	public String getFirstNameComponentWithDecoration() {
		return nameComponents.get(0);
	}
	
	public String getFirstNameComponent() {
		final String first = getFirstNameComponentWithDecoration();
		return DECORATION_REGEX.matcher(first).replaceAll("");
	}

	public Parameter strip() {
		if (nameComponents.size() < 2)
			return new Parameter("", value);
		
		final ImmutableList<String> componentsExceptTheFirst = nameComponents.subList(1, nameComponents.size());
		return new Parameter(value, componentsExceptTheFirst);
	}
	
	public boolean isDecorated() {
		return DECORATION_REGEX.matcher(getFirstNameComponentWithDecoration()).find();
	}
	
	public int compareTo(Parameter o) {
		return getIndex(getName()) - getIndex(o.getName());
	}
	
	private int getIndex(String parameterName) {
		Matcher m1 = INDEX_PATTERN.matcher(parameterName);
		if (m1.find()) {
			return Integer.parseInt(m1.group(1));
		}
		return -1;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nameComponents == null) ? 0 : nameComponents.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Parameter other = (Parameter) obj;
		if (nameComponents == null) {
			if (other.nameComponents != null)
				return false;
		} else if (!nameComponents.equals(other.nameComponents))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Parameter(%s -> %s)", getName(), getValue());
	}
}
