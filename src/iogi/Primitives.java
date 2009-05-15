package iogi;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class Primitives {
	private static final Map<Class<?>, Class<?>> primitiveToObject = ImmutableMap.<Class<?>, Class<?>>builder()
	.put(Boolean.TYPE, Boolean.class)
	.put(Character.TYPE, Character.class)
	.put(Byte.TYPE, Byte.class)
	.put(Short.TYPE, Short.class)
	.put(Integer.TYPE, Integer.class)
	.put(Long.TYPE, Long.class)
	.put(Float.TYPE, Float.class)
	.put(Double.TYPE, Double.class)
	.put(Void.TYPE, Void.class)
	.build();

	private Primitives() {}

	/**
	 * @param type
	 * @return true iff type represents a primitive (like int.class) or a primitive wrapper (like Integer.class)
	 */
	public static boolean isPrimitiveLike(Class<?> type) {
		return primitiveToObject.keySet().contains(type) || primitiveToObject.values().contains(type);
	}

	@SuppressWarnings("unchecked")
	public static <T> T primitiveCast(Object object, Class<T> type) {
		return (T) primitiveToObject.get(type).cast(object);
	}
	
}
