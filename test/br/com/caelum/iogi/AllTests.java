package br.com.caelum.iogi;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class AllTests {
	public static void main(final String[] args) throws Exception {
		final int numberOfRuns = 1000;
		
		final Test suite = allTestsSuite();
		final TestResult result = new TestResult();
		System.out.println("Press enter to run tests...");
		System.in.read();
		System.out.printf("Proceeding to do %d test runs...\n", numberOfRuns);
		final long start = System.currentTimeMillis();
		for (int i = 0; i < numberOfRuns; i++) {
			suite.run(result);
		}
		System.out.printf("Finished running %d runs totaling %d tests in %d seconds\n",
				numberOfRuns, result.runCount(),
				(System.currentTimeMillis() - start)/1000);
	}
	
	private static Test allTestsSuite() {
		try {
			final TestSuite suite = new TestSuite("Iogi");
			final String pack = AllTests.class.getPackage().getName();
			final URL url = AllTests.class.getResource(".");
			final URLClassLoader loader = new URLClassLoader(new URL[] { AllTests.class.getResource("/") },
					AllTests.class.getClassLoader());
			final File root = new File(url.getFile());
			addTests(suite, root, pack, loader);
			return suite;
		} catch (final Exception e) {
			throw new RuntimeException();
		}
	}

	private static void addTests(final TestSuite suite, final File root, final String pack, final ClassLoader classLoader)
			throws ClassNotFoundException {
		for (final File f : root.listFiles()) {
			if (f.isDirectory()) {
				addTests(suite, f, pack + "." + f.getName(), classLoader);
			} else if (f.getName().endsWith("Test.class") || f.getName().endsWith("Tests.class") && !f.getName().endsWith("AllTests.class")) {
				final String className = pack + "." + f.getName().replace(".class", "");
				final Class<?> type = Class.forName(className);
				if (!Modifier.isAbstract(type.getModifiers())) {
					System.out.println("Adding test " + type);
					suite.addTest(new JUnit4TestAdapter(type));
				}
			}
		}

	}
}