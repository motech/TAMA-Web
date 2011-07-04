package org.motechproject.tama.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupSeedData {
    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        SeedData seedData = context.getBean(SeedData.class);
        seedData.init();
        
        SeedLoader seedLoader = context.getBean(SeedLoader.class);
        seedLoader.load();
    }
}
