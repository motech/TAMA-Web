package org.motechproject.tama.tools.seed;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class IncreamentalSetupSeedData {
    public static final String APPLICATION_CONTEXT_XML = "applicationSeedDataContext.xml";

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        loadSeedData(context);
    }

    public static void loadSeedData(ApplicationContext context) throws InterruptedException {
        SeedLoader seedLoader = (SeedLoader) context.getBean("increamentalSeedLoader");
        seedLoader.load();
    }

}
