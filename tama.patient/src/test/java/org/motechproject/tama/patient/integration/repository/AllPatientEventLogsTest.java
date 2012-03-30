package org.motechproject.tama.patient.integration.repository;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientEvent;
import org.motechproject.tama.patient.domain.PatientEventLog;
import org.motechproject.tama.patient.repository.AllPatientEventLogs;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml")
public class AllPatientEventLogsTest extends SpringIntegrationTest {

    @Autowired
    private AllPatientEventLogs allPatientEventLogs;
    
    private Patient patient;
    private String patientId;

    @Before
    public void setUp() {
        patientId = "patient_id";
        patient = PatientBuilder.startRecording().withDefaults().withId(patientId).build();
    }

    @Test
    public void shouldFindByPatientId() throws Exception {
        PatientEventLog patientEventLog = new PatientEventLog(patient.getId(), PatientEvent.Suspension);
        allPatientEventLogs.add(patientEventLog);
        markForDeletion(patientEventLog);

        List<PatientEventLog> eventLogs = allPatientEventLogs.findByPatientId(patient.getId());
        assertEquals(1, eventLogs.size());
        PatientEventLog actualPatientEventLog = eventLogs.get(0);
        assertEquals(patientId, actualPatientEventLog.getPatientId());
        assertEquals(PatientEvent.Suspension, actualPatientEventLog.getEvent());
        assertEquals(DateUtil.today(), actualPatientEventLog.getDate().toLocalDate());
    }
}
