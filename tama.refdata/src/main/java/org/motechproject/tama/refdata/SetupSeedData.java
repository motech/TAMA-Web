package org.motechproject.tama.refdata;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupSeedData {
    public static final String APPLICATION_CONTEXT_XML = "applicationRefDataContext.xml";

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        SeedLoader seedLoader = (SeedLoader) context.getBean("seedLoader");
        seedLoader.load();
    }
}
