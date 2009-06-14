package iogi.vraptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import iogi.Iogi;
import iogi.NullDependencyProvider;
import iogi.fixtures.ContainsParameterizedList;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.Test;

public class OgnlGenericTypesSupportTestVRaptorTest {
	private final Iogi iogi = new Iogi(new NullDependencyProvider());
	
	public static class Cat {
        private List<String> legLength;

        public void setLegLength(final List<String> legLength) {
            this.legLength = legLength;
        }

        public List<String> getLegLength() {
            return legLength;
        }

        public void setLegs(final List<Leg> legs) {
            this.legs = legs;
        }

        public List<Leg> getLegs() {
            return legs;
        }

        public void setIds(final Long[] ids) {
            this.ids = ids;
        }

        public Long[] getIds() {
            return ids;
        }

        public void setEyeColorCode(final List<Long> eyeColorCode) {
            this.eyeColorCode = eyeColorCode;
        }

        public List<Long> getEyeColorCode() {
            return eyeColorCode;
        }

        private List<Leg> legs;
        private Long[] ids;
        private List<Long> eyeColorCode;
    }
	
	public static class Leg {
        private String color;

        public void setColor(final String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }
    }
	
	@Test
    public void canInstantiatingStringsInAListSettingItsInternalValueWithoutInvokingConverters1() throws Exception {
        final Type type = ContainsParameterizedList.class.getField("listOfString").getGenericType();
		final Target<List<String>> target = new Target<List<String>>(type, "legLength");
		final List<String> legs = iogi.instantiate(target , new Parameter("legLength[0]", "small"), new Parameter("legLength[1]", "big"));
        assertThat(legs.get(1), is(equalTo("big")));
    }
	
	@Test
	public void canInstantiatingStringsInAListSettingItsInternalValueWithoutInvokingConverters2() throws Exception {
		final Target<Cat> target = Target.create(Cat.class, "cat");
		final Cat cat = iogi.instantiate(target , new Parameter("cat.legLength[0]", "small"), new Parameter("cat.legLength[1]", "big"));
		assertThat(cat.legLength.get(1), is(equalTo("big")));
	}
	
	@Test
    public void canInstantiateAndPopulateAnArrayOfWrappers1() {
		final Target<long[]> target = Target.create(long[].class, "ids");
		final long[] ids = iogi.instantiate(target, new Parameter("ids[0]", "3"), new Parameter("ids[1]", "5"));
		assertThat(ids[0], is(equalTo(3L)));
        assertThat(ids[1], is(equalTo(5L)));
    }
	
	@Test
	public void canInstantiateAndPopulateAnArrayOfWrappers2() {
		final Target<Cat> target = Target.create(Cat.class, "cat");
		final Cat cat = iogi.instantiate(target, new Parameter("cat.ids[0]", "3"), new Parameter("cat.ids[1]", "5"));
		assertThat(cat.ids[0], is(equalTo(3L)));
		assertThat(cat.ids[1], is(equalTo(5L)));
	}

    @Test
    public void canInstantiateAndPopulateAListOfWrappers1() throws Exception {
    	final Type type = ContainsParameterizedList.class.getField("listOfLong").getGenericType();
		final Target<List<Long>> target = new Target<List<Long>>(type, "eyeColorCode");
    	final List<Long> eyeColorCode = iogi.instantiate(target, new Parameter("eyeColorCode[0]", "3"), new Parameter("eyeColorCode[1]", "5"));
        assertThat(eyeColorCode.get(0), is(equalTo(3L)));
        assertThat(eyeColorCode.get(1), is(equalTo(5L)));
    }
    
    @Test
    public void canInstantiateAndPopulateAListOfWrappers() {
    	final Target<Cat> target = Target.create(Cat.class, "myCat");
        final Cat myCat = iogi.instantiate(target, new Parameter("myCat.eyeColorCode[0]", "3"), new Parameter("myCat.eyeColorCode[1]", "5"));
		assertThat(myCat.eyeColorCode.get(0), is(equalTo(3L)));
        assertThat(myCat.eyeColorCode.get(1), is(equalTo(5L)));
    }
}
