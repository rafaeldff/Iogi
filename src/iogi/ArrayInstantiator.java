package iogi;

import iogi.parameters.Parameter;
import iogi.parameters.Parameters;
import iogi.reflection.Target;

import java.lang.reflect.Array;
import java.util.List;

public class ArrayInstantiator<T> implements Instantiator<T[]> {

	@Override
	public T[] instantiate(Target<?> target, Parameters parameters) {
		Class<?> classType = target.getClassType().getComponentType();
		List<Parameter> parametersList = parameters.getParametersList();
		Object[] newArray = (Object[])Array.newInstance(classType, parametersList.size());
		for (int i = 0; i < parametersList.size(); i++) {
			newArray[i] = parametersList.get(i).getValue();
		}
		@SuppressWarnings("unchecked")
		T[] arrayOfT = (T[])newArray;
		return arrayOfT;
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType().isArray();
	}

}
