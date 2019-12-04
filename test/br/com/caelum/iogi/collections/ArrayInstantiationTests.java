package br.com.caelum.iogi.collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.MixedObjectAndArray;
import br.com.caelum.iogi.fixtures.OneIntegerPrimitive;
import br.com.caelum.iogi.fixtures.TwoArguments;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

public class ArrayInstantiationTests {
	private final Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());
	
	@Test
	public void canInstantiateAnArrayOfStrings() throws Exception {
		final Target<String[]> target = Target.create(String[].class, "arr");
		final String[] array = iogi.instantiate(target, new Parameter("arr[0]", "one"), new Parameter("arr[1]", "two"));
		assertArrayEquals(new String[] {"one", "two"}, array);
	}
	
	@Test
	public void canInstantiateAnArrayOfIntegerWrappers() throws Exception {
		final Target<Integer[]> target = Target.create(Integer[].class, "arr");
		final Integer[] array = iogi.instantiate(target, new Parameter("arr[0]", "99"), new Parameter("arr[1]", "98"));
		assertArrayEquals(new Integer[] {99, 98}, array);
	}
	
	@Test
	public void canInstantiateAnArrayOfPrimitiveIntegers() throws Exception {
		final Target<int[]> target = Target.create(int[].class, "arr");
		final int[] array = iogi.instantiate(target, new Parameter("arr[0]", "99"), new Parameter("arr[1]", "98"));
		assertArrayEquals(new int[] {99, 98}, array);
	}
	
	@Test
	public void canInstantiateAnArrayOfObjects() throws Exception {
		final Target<OneIntegerPrimitive[]> target = Target.create(OneIntegerPrimitive[].class, "arr");
		final OneIntegerPrimitive[] array = iogi.instantiate(target, new Parameter("arr[0].anInteger", "99"), new Parameter("arr[1].anInteger", "98"));
		assertEquals(2, array.length);
		assertEquals(99, array[0].getAnInteger());
		assertEquals(98, array[1].getAnInteger());
	}
	
	@Test
	public void willInstantiateAnZeroLengthArrayIfGivenNoParameters() {
		final Target<String[]> target = Target.create(String[].class, "arr");
		final String[] array = iogi.instantiate(target);
		assertEquals(0, array.length);
	}
	
	@Test
	public void canInstantiateAnArrayOfObjectsWithMoreThanOneConstructorParameter() throws Exception {
		final Target<TwoArguments[]> target = Target.create(TwoArguments[].class, "arr");
		final TwoArguments[] array = iogi.instantiate(target, 
				new Parameter("arr[0].one", "10"), 
				new Parameter("arr[0].two", "11"), 
				new Parameter("arr[1].one", "20"), 
				new Parameter("arr[1].two", "21"));
		assertEquals(2, array.length);
		assertEquals(10, array[0].getOne());
		assertEquals(11, array[0].getTwo());
		assertEquals(20, array[1].getOne());
		assertEquals(21, array[1].getTwo());
	}
	
	@Test
	public void canMixArraysWithNonArraysAsParametersForAConstructor() throws Exception {
		final Target<MixedObjectAndArray> target = Target.create(MixedObjectAndArray.class, "root");
		final MixedObjectAndArray rootObject = iogi.instantiate(target, 
				new Parameter("root.array[0].someString", "10"), 
				new Parameter("root.array[1].someString", "20"),
				new Parameter("root.object.someString", "00")); 
		
		assertEquals("00", rootObject.getObject().getSomeString());
		assertEquals("10", rootObject.getArray()[0].getSomeString());
		assertEquals("20", rootObject.getArray()[1].getSomeString());
	}
	
	@Test
	public void canInstantiateMoreThanOneArrayFromTheSameParameterList() throws Exception {
		final Target<String[]> firstArrayTarget = Target.create(String[].class, "arr1");
		final Target<String[]> secondArrayTarget = Target.create(String[].class, "arr2");
		
		final Parameters parameters = new Parameters(Arrays.asList(
				new Parameter("arr1[0]", "10"), 
				new Parameter("arr1[1]", "11"), 
				new Parameter("arr2[0]", "20"),
				new Parameter("arr2[1]", "21")));
		
		final String[] firstArray = iogi.instantiate(firstArrayTarget, parameters);  
		final String[] secondArray = iogi.instantiate(secondArrayTarget, parameters);  
		
		assertArrayEquals(new String[] {"10", "11"}, firstArray);
		assertArrayEquals(new String[] {"20", "21"}, secondArray);
	}
	
	@Test
	public void willCreateAnArrayWithLengthEqualToTheNumberOfSpecifiedParameters() {
		final Target<OneIntegerPrimitive[]> target = Target.create(OneIntegerPrimitive[].class, "arr");
		final Parameter at0 = new Parameter("arr[0].anInteger", "1");
		final Parameter at16 = new Parameter("arr[16].anInteger", "2");
		
		final OneIntegerPrimitive[] array = iogi.instantiate(target, at0, at16);
		assertEquals(2, array.length);
	}

	@Test
	public void orderOfElementsInTheArrayCorrespondsToTheOrderOfTheIndexes() {
		final Target<OneIntegerPrimitive[]> target = Target.create(OneIntegerPrimitive[].class, "arr");
		final Parameter at16 = new Parameter("arr[16].anInteger", "3");
		final Parameter at0 = new Parameter("arr[0].anInteger", "1");
		final Parameter at5 = new Parameter("arr[5].anInteger", "2");
		
		final OneIntegerPrimitive[] array = iogi.instantiate(target, at16, at0, at5);
		assertEquals(1, array[0].getAnInteger());
		assertEquals(2, array[1].getAnInteger());
		assertEquals(3, array[2].getAnInteger());
	}
}
