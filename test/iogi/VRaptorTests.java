package iogi;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import iogi.parameters.Parameter;
import iogi.reflection.Target;

import java.util.List;

import org.junit.Test;

public class VRaptorTests {
	private final Iogi iogi = new Iogi(new NullDependencyProvider());

	// OgnlParametersProviderTest
	public static class Cat {
		private String id;

		public void setId(final String id) {
			this.id = id;
		}

		public String getId() {
			return id;
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

		public void setExtraCats(final List<Cat> extraCats) {
			this.extraCats = extraCats;
		}

		public List<Cat> getExtraCats() {
			return extraCats;
		}

		public void setIds(final Long[] ids) {
			this.ids = ids;
		}

		private List<String> owners;

		public Long[] getIds() {
			return ids;
		}

		public void setOwners(final List<String> owners) {
			this.owners = owners;
		}

		public List<String> getOwners() {
			return owners;
		}

		private List<Cat> extraCats;

		private Long[] ids;

	}

	class MyResource {
		void buyA(final House house) {
		}
	}

	public static class BuyASetter {
		private House House_;

		public void setHouse(final House house_) {
			House_ = house_;
		}

		public House getHouse() {
			return House_;
		}
	}

	@Test
	public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() {
		final Parameter parameter = new Parameter("house.cat.id", "guilherme");
		final Target<House> target = Target.create(House.class, "house");
		final House house = iogi.instantiate(target, parameter);
		assertEquals("guilherme", house.cat.id);
	}

	@Test
	public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSet() {
		final Target<House> target = Target.create(House.class, "house");
		final Parameter parameter = new Parameter("house.extraCats[1].id", "guilherme");
		final House house = iogi.instantiate(target, parameter);
		assertThat(house.extraCats, hasSize(1));
		assertThat(house.extraCats.get(0).id, is(equalTo("guilherme")));
	}

	@Test
    public void removeFromTheCollectionIfAnElementIsCreatedWithinAnArrayButNoFieldIsSet() {
		final Target<House> target = Target.create(House.class, "house");
		final Parameter parameter = new Parameter("house.ids[1].id", "3");
		final House house = iogi.instantiate(target, parameter);
		assertThat(house.ids.length, is(equalTo(1)));
		assertThat(house.ids[0], is(equalTo(3l)));
    }

	// -----------------
}
