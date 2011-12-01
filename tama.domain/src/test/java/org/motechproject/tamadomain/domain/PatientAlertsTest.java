package org.motechproject.tamadomain.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.alerts.domain.Alert;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class PatientAlertsTest {

    PatientAlerts patientAlerts;
    HashMap<String, String> fallingAdherenceData = new HashMap<String, String>();
    HashMap<String, String> symptomReportingData = new HashMap<String, String>();

    @Before
    public void setUp() {
        patientAlerts = new PatientAlerts();
        fallingAdherenceData.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.FallingAdherence.name());
        symptomReportingData.put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.SymptomReporting.name());
    }

    @Test
    public void shouldReturnLastSymptomReportedPatientAlert() {
        patientAlerts.add(new PatientAlert(){{setAlert(new Alert(){{setData(fallingAdherenceData); }});}});
        patientAlerts.add(new PatientAlert(){{setAlert(new Alert(){{setData(symptomReportingData);setDateTime(new DateTime(2011, 10, 10, 0, 0, 0)); }});}});
        patientAlerts.add(new PatientAlert(){{setAlert(new Alert(){{setData(symptomReportingData);setDateTime(new DateTime(2010, 10, 10, 0, 0, 0)); }});}});

        PatientAlert patientAlert = patientAlerts.lastSymptomReportedAlert();
        assertNotNull(patientAlert);
        assertEquals(2011, patientAlert.getAlert().getDateTime().getYear());
    }

    @Test
    public void shouldReturnNoPatientAlert_ForAlertType(){
        patientAlerts.add(new PatientAlert(){{setAlert(new Alert(){{setData(fallingAdherenceData); }});}});

        assertNull(patientAlerts.lastSymptomReportedAlert());
    }
}
