package org.motechproject.tama.symptomreporting.domain;

import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationSymptomReportingContext.xml", inheritLocations = false)
public class SymptomReportingPropertiesTest extends SpringIntegrationTest {

    @Autowired
    SymptomReportingProperties symptomReportingProperties;

    @Test
    public void shouldGetDescriptionForSymptom() {
        assertEquals("Nausea or Vomiting", symptomReportingProperties.symptomDescription("nauseaorvomiting"));
        assertEquals("Low urine output or General weakness", symptomReportingProperties.symptomDescription("lowurineorgenweakness"));
        assertEquals("Tingling sensation or Numbness or Pain in the feet but not in the rest of the legs", symptomReportingProperties.symptomDescription("painfeettingnumb"));
    }

    @Test
    public void shouldGetLabelForSymptom() {
        assertEquals("Nausea or Vomiting", symptomReportingProperties.symptomLabel("nauseaorvomiting"));
        assertEquals("Swelling", symptomReportingProperties.symptomLabel("swellfacelegs"));
        assertEquals("Numbness/Pain in the feet", symptomReportingProperties.symptomLabel("painfeettingnumb"));
    }
}
