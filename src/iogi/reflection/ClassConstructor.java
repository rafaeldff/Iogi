package iogi.reflection;

import iogi.Instantiator;
import iogi.exceptions.IogiException;
import iogi.parameters.Parameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;

/**
 * Equality based on constructor names; 
 *
 */
public class ClassConstructor {
	private static final CachingParanamer paranamer = new CachingParanamer(new BytecodeReadingParanamer());
	private final Set<String> names;
	private final Constructor<?> constructor;
	
	private ClassConstructor(Constructor<?> constructor, Set<String> parameterNames) {
		this.constructor = constructor;
		this.names = parameterNames;
	}
	
	public ClassConstructor(Constructor<?> constructor) {
		this(constructor, parameterNames(constructor));
	}
	
	public ClassConstructor(Set<String> parameterNames) {
		this(null, parameterNames);
	}
	
	private static Set<String> parameterNames(Constructor<?> constructor) {
		HashSet<String> parameterNames = new HashSet<String>();
		String[] lookedUpNames = paranamer.lookupParameterNames(constructor);
		for (String parameterName : lookedUpNames) {
			if (!parameterName.isEmpty()) //To account for http://jira.codehaus.org/browse/PARANAMER-10
				parameterNames.add(parameterName);
		}
		return parameterNames;
	}
	
	public Set<String> getNames() {
		return names;
	}

	public Object instantiate(Instantiator<?> instantiator, Parameters parameters) {
		Type[] parameterTypes = constructor.getGenericParameterTypes();
		String[] parameterNames = namesInOrder();
		Object[] argumentValues = new Object[parameterNames.length];
		
		for (int i = 0; i < parameterNames.length; i++) {
			String name = parameterNames[i];
			
			Target<?> target = new Target<Object>(parameterTypes[i], name);
			
			Object value = instantiator.instantiate(target, parameters);
			argumentValues[i] = value;
		}
		
		return instantiateWithConstructor(argumentValues);
	}

	private String[] namesInOrder() {
		String[] foundByParanamer = paranamer.lookupParameterNames(constructor);
		
		if (foundByParanamer.length == 1 && foundByParanamer[0].isEmpty())
			return new String[] {}; //To account for http://jira.codehaus.org/browse/PARANAMER-10
		
		return foundByParanamer;
	}

	private Object instantiateWithConstructor(Object[] values) {
		try {
			return constructor.newInstance(values);
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
	
	@Override
	public String toString() {
		return "(" + Joiner.on(", ").join(names) + ")"; 
	}

	public Set<ClassConstructor> compatible(Set<ClassConstructor> candidates) {
		return Sets.filter(candidates, namesAreContainedInThis);
	}

	private Predicate<? super ClassConstructor> namesAreContainedInThis = new Predicate<ClassConstructor>(){
		@Override
		public boolean apply(ClassConstructor input) {
			return names.containsAll(input.names);
		}
	};
}
