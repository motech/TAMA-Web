package org.motechproject.tama.symptomreporting.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class SymptomReportingProperties {

    private Properties symptomProperties;

    @Autowired
    public SymptomReportingProperties(@Qualifier("symptomProperties") Properties symptomProperties) {
        this.symptomProperties = symptomProperties;
    }

    public String symptomLabel(String symptomId) {
        return symptomProperties.getProperty(symptomId);
    }

    public String symptomDescription(String symptomId) {
    	return symptomProperties.getProperty(symptomId + ".desc");
	}
}
