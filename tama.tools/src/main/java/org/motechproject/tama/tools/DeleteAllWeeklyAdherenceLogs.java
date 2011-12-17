package org.motechproject.tama.tools;

import org.motechproject.tamacallflow.domain.WeeklyAdherenceLog;
import org.motechproject.tamacallflow.repository.AllWeeklyAdherenceLogs;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeleteAllWeeklyAdherenceLogs {
    public static final String APPLICATION_CONTEXT_XML = "applicationToolsContext.xml";

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        AllWeeklyAdherenceLogs allWeeklyAdherenceLogs = applicationContext.getBean(AllWeeklyAdherenceLogs.class);
        for (WeeklyAdherenceLog log : allWeeklyAdherenceLogs.getAll() ){
            allWeeklyAdherenceLogs.remove(log);
        }
    }
}