package iogi;

import static org.junit.Assert.assertEquals;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import java.util.List;

import org.junit.Test;


public class VRaptorTests {
	private Iogi iogi = new Iogi(new NullDependencyProvider());

	// OgnlParametersProviderTest
	public static class Cat {
        private String id;

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class House {
        private Cat cat;

        public void setCat(Cat cat) {
            this.cat = cat;
        }

        public Cat getCat() {
            return cat;
        }

        public void setExtraCats(List<Cat> extraCats) {
            this.extraCats = extraCats;
        }

        public List<Cat> getExtraCats() {
            return extraCats;
        }

        public void setIds(Long[] ids) {
            this.ids = ids;
        }

        private List<String> owners;

        public Long[] getIds() {
            return ids;
        }

        public void setOwners(List<String> owners) {
            this.owners = owners;
        }

        public List<String> getOwners() {
            return owners;
        }

        private List<Cat> extraCats;

        private Long[] ids;

    }

    class MyResource {
        void buyA(House house) {
        }
    }

    public static class BuyASetter {
        private House House_;

        public void setHouse(House house_) {
            House_ = house_;
        }

        public House getHouse() {
            return House_;
        }
    }
    
    @Test
    public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() {
    	Parameter parameter = new Parameter("house.cat.id", "guilherme");
    	Target<House> target =  Target.create(House.class, "house");
    	House house = iogi.instantiate(target, parameter);
    	assertEquals("guilherme", house.cat.id);
    }
	//     -----------------
}
