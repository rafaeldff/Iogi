package iogi.collections;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import iogi.Iogi;
import iogi.MixedObjectAndArray;
import iogi.NullDependencyProvider;
import iogi.fixtures.OneIntegerPrimitive;
import iogi.fixtures.TwoArguments;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import org.junit.Test;

public class ArrayInstantiationTests {
	private final Iogi iogi = new Iogi(new NullDependencyProvider());
	
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
		assertEquals(99, array[0].getAnInteger());
		assertEquals(98, array[1].getAnInteger());
	}
	
	@Test
	public void canInstantiateAnArrayOfObjectsWithMoreThanOneConstructorParameter() throws Exception {
		final Target<TwoArguments[]> target = Target.create(TwoArguments[].class, "arr");
		final TwoArguments[] array = iogi.instantiate(target, 
				new Parameter("arr[0].one", "10"), 
				new Parameter("arr[0].two", "11"), 
				new Parameter("arr[1].one", "20"), 
				new Parameter("arr[1].two", "21"));
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
}
