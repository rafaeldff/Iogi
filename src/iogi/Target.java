package iogi;

public class Target<T> {
	private Class<T> type;
	private final String name;

	public Target(Class<T> type, String name) {
		this.name = name;
		this.type = type;
	}

	public Class<T> getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}

	public boolean isPrimitiveLike() {
		return Primitives.isPrimitiveLike(type);
	}
	
	public T cast(Object object) {
		if (type.isPrimitive())
			return Primitives.primitiveCast(object, type);
			
		return type.cast(object);

	}
	
	@Override
	public String toString() {
		return String.format("Target(name=%s, type=%s)", name, type);
	}
}