package org.motechproject.tama.integration.domain.patient;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.Patient;

import java.util.List;

public class PatientIntegrationTest {
	
	@Test
	public void shouldLoadPatientByPatientId() {
        Patient patient = PatientBuilder.startRecording().withDefaults().withPatientId("12345678").build();
        patient.persist();

        Assert.assertEquals(0, patient.findByPatientId("9999").size());

        Patient loadedPatient = patient.findByPatientId("12345678").get(0);
        Assert.assertNotNull(loadedPatient);
        Assert.assertEquals("12345678", loadedPatient.getPatientId());

        loadedPatient.remove();
        loadedPatient.flush();
    }

}
