package br.com.caelum.iogi.reflection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

/**
 * Test if IOGI can instantiate bridge methods properly
 * @author Rafael Ponte
 *
 */
public class GenericEntityIdTest {

	private final Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());

	public static interface Entity<T extends Number> {

		public T getId();

		public void setId(T id);

	}

	public static class Product implements Entity<Integer> {

		private Integer id;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
	}

	@Test
	public void shouldInstantiateEntity() {

		final Target<Product> target = Target.create(Product.class, "product");
		final Parameter parameter = new Parameter("product.id", "42");
		final Product product = iogi.instantiate(target, parameter);

		assertEquals("product id", new Integer(42), product.getId());
	}

}