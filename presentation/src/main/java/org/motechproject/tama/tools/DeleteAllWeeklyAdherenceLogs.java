package org.motechproject.tama.tools;

import org.motechproject.tama.domain.WeeklyAdherenceLog;
import org.motechproject.tama.repository.AllWeeklyAdherenceLogs;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeleteAllWeeklyAdherenceLogs {
    public static final String APPLICATION_CONTEXT_XML = "applicationContext-tools.xml";

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllWeeklyAdherenceLogs allWeeklyAdherenceLogs = applicationContext.getBean(AllWeeklyAdherenceLogs.class);
        for (WeeklyAdherenceLog log : allWeeklyAdherenceLogs.getAll() ){
            allWeeklyAdherenceLogs.remove(log);
        }
    }
}