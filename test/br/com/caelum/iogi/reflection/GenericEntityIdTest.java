package br.com.caelum.iogi.reflection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.caelum.iogi.Iogi;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.util.DefaultLocaleProvider;
import br.com.caelum.iogi.util.NullDependencyProvider;

/**
 * Test if IOGI can instantiate bridge methods properly
 * @author Rafael Ponte
 *
 */
public class GenericEntityIdTest {

	private final Iogi iogi = new Iogi(new NullDependencyProvider(), new DefaultLocaleProvider());

	public static interface MyGenericInterface<T extends Number> {
		T getId();
		void setId(T id);
	}
	
	public static abstract class MyGenericClass<T extends Number> {
		abstract T getCode();
		abstract void setCode(T code);
	}

	public static class Product extends MyGenericClass<Integer> implements MyGenericInterface<Integer> {

		private Integer id;
		private Integer code;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}
		
		public Integer getCode() {
			return code;
		}
		
		public void setCode(Integer code) {
			this.code = code;
		}
	}

	@Test
	public void shouldInstantiateEntityWithGenericInterface() {
		final Target<Product> target = Target.create(Product.class, "product");
		final Parameter parameter = new Parameter("product.id", "42");
		final Product product = iogi.instantiate(target, parameter);

		assertEquals("product id", new Integer(42), product.getId());
	}

	@Test
	public void shouldInstantiateEntityWithGenericSuperclass() {
		final Target<Product> target = Target.create(Product.class, "product");
		final Parameter parameter = new Parameter("product.code", "42");
		final Product product = iogi.instantiate(target, parameter);

		assertEquals("product code", new Integer(42), product.getCode());
	}
}