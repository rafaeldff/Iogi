package iogi.vraptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import iogi.Iogi;
import iogi.parameters.Parameter;
import iogi.reflection.Target;
import iogi.spi.LocaleProvider;
import iogi.util.NullDependencyProvider;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

public class MiscOgnlSupportTestVRaptorTest {
	public static class Cat {
        private Leg firstLeg;

        public void setFirstLeg(final Leg firstLeg) {
            this.firstLeg = firstLeg;
        }

        public Leg getFirstLeg() {
            return firstLeg;
        }
    }

    public static class Leg {
        private Integer id;
        private Calendar birthDay; // weird leg birthday!!

        public void setId(final Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setBirthDay(final Calendar birthDay) {
            this.birthDay = birthDay;
        }

        public Calendar getBirthDay() {
            return birthDay;
        }
    }

    public static class House {
        private Cat cat;

        public void setCat(final Cat cat) {
            this.cat = cat;
        }

        public Cat getCat() {
            return cat;
        }
    }

	private LocaleProvider mockLocaleProvider;	
	private Mockery context;
    
    private Iogi iogi;
    
    @Before
    public void setUp() {
    	this.context = new Mockery();
    	this.mockLocaleProvider = context.mock(LocaleProvider.class);
    	this.iogi = new Iogi(new NullDependencyProvider(), mockLocaleProvider);
    }
    
    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() {
        final Target<House> target = Target.create(House.class, "house");
        final Parameter parameter = new Parameter("house.cat.firstLeg.id", "");
        final House house = iogi.instantiate(target, parameter);
        assertThat(house.cat.firstLeg.id, is(equalTo(null)));
    }
    
    @Test
    public void isCapableOfDealingWithEmptyParameterForIynternalValueWhichNeedsAConverter() throws Exception {
    	final Target<House> target = Target.create(House.class, "house");
    	final Parameter parameter = new Parameter("house.cat.firstLeg.birthDay", "10/5/2010");
    	context.checking(new Expectations() {{
    		allowing(mockLocaleProvider).getLocale();
    		will(returnValue(new Locale("pt", "BR")));
    	}});
    	final House house = iogi.instantiate(target, parameter);
    	assertThat(house.cat.firstLeg.birthDay, is(equalTo((Calendar)new GregorianCalendar(2010, 4, 10))));
    }
}
