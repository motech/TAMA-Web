package org.motechproject.tama.patient.integration.repository;

import com.github.ldriscoll.ektorplucene.LuceneResult;
import org.ektorp.BulkDeleteDocument;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Test;
import org.motechproject.tama.common.integration.repository.SpringIntegrationTest;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.PatientAlertType;
import org.motechproject.tama.patient.repository.AllMotechAlerts;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@ContextConfiguration(locations = "classpath*:applicationPatientContext.xml", inheritLocations = false)
public class AllMotechAlertsTest extends SpringIntegrationTest {

    @Qualifier("luceneAwareAlertsDbConnector")
    @Autowired
    protected CouchDbConnector luceneAwareAlertsDbConnector;

    @Autowired
    private PatientAlertService patientAlertService;
    @Autowired
    private AllMotechAlerts allMotechAlerts;
    @Autowired
    private AllPatients allPatients;

    @After
    public void tearDown(){
        deleteAllMotechAlerts();
    }

    @Test
    public void shouldGetAllAlerts(){
        Patient patient = PatientBuilder.startRecording().withDefaults().build();
        allPatients.add(patient, "clinician0");
        markForDeletion(patient);

        patientAlertService.createAlert(patient.getId(), 0, "alertName", "some description", PatientAlertType.AdherenceInRed);
        patientAlertService.createAlert(patient.getId(), 0, "anotherAlertName", "somethinf", PatientAlertType.FallingAdherence);

        List<LuceneResult.Row> alerts = allMotechAlerts.find(patient.getId());

        assertEquals(2, alerts.size());
    }

    private void deleteAllMotechAlerts() {
        List<BulkDeleteDocument> toDelete = new ArrayList<BulkDeleteDocument>();
        for (Object document : allMotechAlerts.getAll().toArray()) {
            toDelete.add(BulkDeleteDocument.of(document));
        }
        luceneAwareAlertsDbConnector.executeBulk(toDelete);
    }
}
