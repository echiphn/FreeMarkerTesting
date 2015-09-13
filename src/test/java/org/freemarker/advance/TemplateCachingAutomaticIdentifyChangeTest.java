package org.freemarker.advance;

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsSame;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TemplateCachingAutomaticIdentifyChangeTest {
	private static final int TEMPLATE_UPDATE_DELAY = 1000;
	private static final File ORIGINAL_TEMPLATE_FILE = new File(
			"src/test/resources/templates/helloworld.ftl");
	private static final File BACKUP_FILE = new File(
			"src/test/resources/templates/helloworld.ftl.bak");
	private Configuration configuration;

	@Before
	public void setUp() throws IOException {
		configuration = new Configuration(Configuration.VERSION_2_3_23);
		TemplateLoader fileTemplateLoader = new FileTemplateLoader(
				ORIGINAL_TEMPLATE_FILE.getParentFile());
		configuration.setTemplateLoader(fileTemplateLoader);
		configuration.setTemplateUpdateDelayMilliseconds(TEMPLATE_UPDATE_DELAY);
		FileUtils.copyFile(ORIGINAL_TEMPLATE_FILE, BACKUP_FILE);
	}

	@After
	public void tearDown() throws IOException {
		configuration = null;
		FileUtils.copyFile(BACKUP_FILE, ORIGINAL_TEMPLATE_FILE);
		FileUtils.forceDelete(BACKUP_FILE);
	}

	@Test
	public void testCaching() throws Exception {
		Template template = configuration.getTemplate("helloworld.ftl");
		FileUtils.copyFile(
				FileUtils.toFile(this.getClass().getResource(
						"/templates/helloworld_updated.ftl")),
				ORIGINAL_TEMPLATE_FILE);
		Thread.sleep(TEMPLATE_UPDATE_DELAY+1);
		Template template_updated = configuration.getTemplate("helloworld.ftl");
		assertThat(template, IsNot.not(IsSame.sameInstance(template_updated)));
	}

}
