package org.freemarker.basic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xmlmatchers.XmlMatchers;
import org.xmlmatchers.transform.XmlConverters;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

@RunWith(Parameterized.class)
public class HelloWorldDiverseTemplateLoaderTest {
	private TemplateLoader templateLoader;
	private static Configuration configuration;

	@BeforeClass
	public static void setUp() {
		configuration = new Configuration(Configuration.VERSION_2_3_23);
	}

	@AfterClass
	public static void tearDown() {
		configuration = null;
	}

	@Parameters
	public static Collection<Object> parameters() throws IOException {
		Collection<Object> parameters = new ArrayList<>();
		TemplateLoader fileTemplateLoader = new FileTemplateLoader(
				FileUtils.toFile(HelloWorldDiverseTemplateLoaderTest.class
						.getResource("/templates")));
		parameters.add(fileTemplateLoader);
		TemplateLoader classTemplateLoader = new ClassTemplateLoader(
				HelloWorldDiverseTemplateLoaderTest.class.getClass(),
				"/templates");
		parameters.add(classTemplateLoader);
		TemplateLoader[] loaders = new TemplateLoader[] {
				new ClassTemplateLoader(
						HelloWorldDiverseTemplateLoaderTest.class.getClass(),
						"/templates"),
				new ClassTemplateLoader(
						HelloWorldDiverseTemplateLoaderTest.class.getClass(),
						"") };
		TemplateLoader multipleTemplateLoader = new MultiTemplateLoader(loaders);
		parameters.add(multipleTemplateLoader);
		return parameters;
	}

	public HelloWorldDiverseTemplateLoaderTest(TemplateLoader templateLoader) {
		this.templateLoader = templateLoader;
	}

	@Test
	public void testHelloWorld_FileTemplateLoader() throws Exception {
		configuration.setTemplateLoader(templateLoader);
		final Template template = configuration.getTemplate("helloworld.ftl");
		configuration.setObjectWrapper(Configuration
				.getDefaultObjectWrapper(Configuration.VERSION_2_3_23));
		final Map<String, Object> model = new HashMap<String, Object>();
		String myName = "Test name";
		model.put("userName", myName);
		/* populate model */
		StringBuilderWriter stringWriter = new StringBuilderWriter();
		template.process(model, stringWriter);
		String response = stringWriter.toString();
		assertThat("Test with template loader " + templateLoader.getClass(),
				response, not(isEmptyString()));
		assertThat(
				"Test with template loader " + templateLoader.getClass(),
				XmlConverters.the(response),
				XmlMatchers.hasXPath("//body/h1", equalTo("Hello " + myName
						+ "!")));
	}

}
