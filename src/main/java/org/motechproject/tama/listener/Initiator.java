package org.motechproject.tama.listener;

import org.motechproject.server.event.annotations.EventAnnotationBeanPostProcessor;
import org.motechproject.server.pillreminder.service.PillReminderServiceImpl;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;

public class Initiator implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            EventAnnotationBeanPostProcessor.registerHandlers(getListeners(sce.getServletContext()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

    private HashMap<String, Object> getListeners(ServletContext servletContext) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        HashMap<String, Object> beans = new HashMap<String, Object>();
        beans.put(PillReminderListener.class.getName(), webApplicationContext.getBean(PillReminderListener.class));
        beans.put(PillReminderServiceImpl.class.getName(), webApplicationContext.getBean(PillReminderServiceImpl.class));
        return beans;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
