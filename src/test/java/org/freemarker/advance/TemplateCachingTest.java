package org.freemarker.advance;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsSame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateCachingTest {
	private Configuration configuration;

	@Before
	public void setUp() throws IOException {
		configuration = new Configuration(Configuration.VERSION_2_3_23);
		TemplateLoader fileTemplateLoader = new FileTemplateLoader(
				FileUtils.toFile(this.getClass().getResource("/templates")));
		configuration.setTemplateLoader(fileTemplateLoader);
	}

	@After
	public void tearDown() {
		configuration = null;
	}

	@Test
	public void testCaching() throws Exception {
		Template template = configuration.getTemplate("helloworld.ftl");
		for (int i = 0; i < 100; i++) {
			Template newTemplate = configuration.getTemplate("helloworld.ftl");
			assertThat(template, IsSame.sameInstance(newTemplate));
		}
	}
	
	@Test
	public void testCaching_CleanCache() throws Exception {
		Template template = configuration.getTemplate("helloworld.ftl");
		for (int i = 0; i < 100; i++) {
			Template newTemplate = configuration.getTemplate("helloworld.ftl");
			assertThat(template, IsSame.sameInstance(newTemplate));
		}
		configuration.clearTemplateCache();
		Template newLoadedTemplate = configuration.getTemplate("helloworld.ftl");
		assertThat(template, not(IsSame.sameInstance(newLoadedTemplate)));
	}

}
