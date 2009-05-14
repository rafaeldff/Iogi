package iogi;

public class Parameter {
	private final String name;
	private final String value;

	public Parameter(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
}
