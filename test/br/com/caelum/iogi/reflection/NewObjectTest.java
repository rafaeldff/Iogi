package br.com.caelum.iogi.reflection;

import static org.junit.Assert.*;

import org.junit.Test;

import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;

public class NewObjectTest {
	
	private abstract static class SampleParent {
		abstract void setInherited(String inherited);
		abstract String getInherited();
	}
	
	@SuppressWarnings("unused")
	private static class Sample extends SampleParent {
		private String simple, returning, inherited; 
		
		public void setSimple(String simple) {
			this.simple = simple;
		}
		public String getSimple() {
			return simple;
		}
		
		public void setReturning(String returning) {
			this.returning = returning;
		}
		public String getReturning() {
			return returning;
		}
		
		@Override
		public void setInherited(String inherited) {
			this.inherited = inherited;
		}
		@Override
		public String getInherited() {
			return this.inherited;
		}
	}
	
	private abstract static class GenericParent<T> {
		public abstract T getInherited();
		public abstract void setInherited(T t);
	}
	
	@SuppressWarnings("unused")
	private static class GenericSample<T> extends GenericParent<T>{
		private T generic, inherited;
		public void setGeneric(T generic) {
			this.generic = generic;
		}
		public T getGeneric() {
			return generic;
		}
		@Override
		public T getInherited() {
			return inherited;
		}
		@Override
		public void setInherited(T t) {
			this.inherited = t;
		}
	}
	
	@Test
	public void valueWithPropertiesSetReturnsTheInitialValue() throws Exception {
		Sample sampleObject = new Sample();
		NewObject newObject = new NewObject(new StringConverter(), new Parameters(), sampleObject);
		assertSame(sampleObject, newObject.valueWithPropertiesSet());
	}

	@Test
	public void callsSetterMethodsToPopulateInstance() {
		Parameters parameters = new Parameters(new Parameter("simple", "value"));
		NewObject newObject = new NewObject(new StringConverter(), parameters, new Sample());
		Sample populatedSampleObject = (Sample) newObject.valueWithPropertiesSet();
		assertEquals("value", populatedSampleObject.getSimple());
	}
	
	@Test
	public void callsSetterMethodsThatReturnValues() {
		Parameters parameters = new Parameters(new Parameter("returning", "value"));
		NewObject newObject = new NewObject(new StringConverter(), parameters, new Sample());
		Sample populatedSampleObject = (Sample) newObject.valueWithPropertiesSet();
		assertEquals("value", populatedSampleObject.getReturning());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void callsGenericSetterMethods() throws Exception {
		Parameters parameters = new Parameters(new Parameter("generic", "value"));
		NewObject newObject = new NewObject(new StringConverter(), parameters, new GenericSample<String>());
		GenericSample<String> populatedSampleObject = (GenericSample<String>) newObject.valueWithPropertiesSet();
		assertEquals("value", populatedSampleObject.getGeneric());		
	}

	@Test
	public void callsInheritedSetterMethods() throws Exception {
		Parameters parameters = new Parameters(new Parameter("inherited", "value"));
		NewObject newObject = new NewObject(new StringConverter(), parameters, new Sample());
		Sample populatedSampleObject = (Sample) newObject.valueWithPropertiesSet();
		assertEquals("value", populatedSampleObject.getInherited());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void callsInheritedGenericSetterMethods() throws Exception {
		Parameters parameters = new Parameters(new Parameter("inherited", "value"));
		NewObject newObject = new NewObject(new StringConverter(), parameters, new GenericSample<String>());
		GenericSample<String> populatedSampleObject = (GenericSample<String>) newObject.valueWithPropertiesSet();
		assertEquals("value", populatedSampleObject.getInherited());
	}
}
