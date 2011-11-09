package org.motechproject.tama.tools.seed;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SetupAudioSeed {
    public static final String APPLICATION_CONTEXT_XML = "META-INF/spring/applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        SeedLoader seedLoader = (SeedLoader) context.getBean("audioSeedLoader");
        seedLoader.load();
    }
}