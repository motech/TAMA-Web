package org.motechproject.tama.web.servlet;

import org.springframework.core.io.ClassPathResource;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import static java.lang.String.format;

public class TAMAServletConfig implements ServletConfig {

    private ServletConfig config;

    public TAMAServletConfig(ServletConfig config) {
        this.config = config;
    }

    @Override
    public String getServletName() {
        return config.getServletName();
    }

    @Override
    public ServletContext getServletContext() {
        return config.getServletContext();
    }

    @Override
    public String getInitParameter(String name) {
        if ("host".equals(name)) {
            return getURL(fetchProperties("tamaReports.properties"));
        } else {
            return config.getInitParameter(name);
        }
    }

    @Override
    public Enumeration getInitParameterNames() {
        return config.getInitParameterNames();
    }

    private String getURL(Properties properties) {
        return format("http://%s:%s/%s/", properties.getProperty("tama.reports.host"), properties.getProperty("tama.reports.port"), properties.getProperty("tama.reports.context.path"));
    }

    private Properties fetchProperties(String props) {
        final Properties propsFromFile = new Properties();
        try {
            propsFromFile.load(new ClassPathResource(props).getInputStream());
        } catch (final IOException e) {
        }
        return propsFromFile;
    }
}
