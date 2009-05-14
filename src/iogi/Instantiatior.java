package iogi;

import iogi.conversion.DoubleConverter;
import iogi.conversion.IntegerConverter;
import iogi.conversion.TypeConverter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;



public class Instantiatior {
	private List<TypeConverter<?>> converters = Collections.unmodifiableList(Arrays.<TypeConverter<?>>asList(new IntegerConverter(), new DoubleConverter()));

	public <T> T instantiate(Class<T> rootClass, String path) {
		try {
			return rootClass.newInstance();
		} catch (java.lang.InstantiationException e) {
			throw new IogiException(e); 
		} catch (IllegalAccessException e) {
			throw new IogiException(e);
		}
	}

	public <T> T instantiate(Class<T> rootClass, String key, String value) {
		String[] keyComponents = key.split("\\.");
		String requiredParameterName = keyComponents[1]; 
		
		Constructor<?> constructor = rootClass.getConstructors()[0];
		CachingParanamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
		Class<?>[] parameterTypes = constructor.getParameterTypes();
		String[] parameterNames = paranamer.lookupParameterNames(constructor);
		
		String formalParameterName = parameterNames[0];
		Class<?> formalParameterType = parameterTypes[0];
		if (formalParameterName.equals(requiredParameterName)) {
			Object convertedValue = convert(value, formalParameterType);
			return instantiateWithConstructor(rootClass, convertedValue, constructor);
		}
		
		return null;
	}

	private Object convert(String value, Class<?> formalParameterType) {
		Object convertedValue;
		if (formalParameterType == double.class) {
			convertedValue = Double.valueOf(value);
		} else if (formalParameterType== int.class) {
			convertedValue = Integer.valueOf(value);
		} else {
			convertedValue = null;
		}
		return convertedValue;
	}

	private <T> T instantiateWithConstructor(Class<T> rootClass, Object value, Constructor<?> constructor) {
		try {
			return rootClass.cast(constructor.newInstance(value));
		} catch (IllegalArgumentException e) {
			throw new IogiException(e);
		} catch (InstantiationException e) {
			throw new IogiException(e);
		} catch (IllegalAccessException e) {
			throw new IogiException(e);
		} catch (InvocationTargetException e) {
			throw new IogiException(e);
		}
	}

}
