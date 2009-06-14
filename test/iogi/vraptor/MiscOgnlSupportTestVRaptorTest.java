package iogi.vraptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import iogi.Iogi;
import iogi.NullDependencyProvider;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import java.util.Calendar;

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
    
    private final Iogi iogi = new Iogi(new NullDependencyProvider());
    
    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() {
        final Target<House> target = Target.create(House.class, "house");
        final Parameter parameter = new Parameter("house.cat.firstLeg.id", "");
        final House house = iogi.instantiate(target, parameter);
        assertThat(house.cat.firstLeg.id, is(equalTo(null)));
    }
}
