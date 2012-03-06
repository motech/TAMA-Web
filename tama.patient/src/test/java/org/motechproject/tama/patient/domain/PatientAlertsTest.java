package org.motechproject.tama.patient.domain;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.patient.builder.PatientBuilder;

import java.util.HashMap;

import static junit.framework.Assert.*;

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
        patientAlerts.add(new PatientAlert() {{
            setAlert(new Alert() {{
                setData(fallingAdherenceData);
            }});
        }});
        patientAlerts.add(new PatientAlert() {{
            setAlert(new Alert() {{
                setData(symptomReportingData);
                setDateTime(new DateTime(2011, 10, 10, 0, 0, 0));
            }});
        }});
        patientAlerts.add(new PatientAlert() {{
            setAlert(new Alert() {{
                setData(symptomReportingData);
                setDateTime(new DateTime(2010, 10, 10, 0, 0, 0));
            }});
        }});

        PatientAlert patientAlert = patientAlerts.lastSymptomReportedAlert();
        assertNotNull(patientAlert);
        assertEquals(2011, patientAlert.getAlert().getDateTime().getYear());
    }

    @Test
    public void shouldReturnNoPatientAlert_ForAlertType() {
        patientAlerts.add(new PatientAlert() {{
            setAlert(new Alert() {{
                setData(fallingAdherenceData);
            }});
        }});

        assertNull(patientAlerts.lastSymptomReportedAlert());
    }

    @Test
    public void filterPatientAlerts_ByClinic() {
        Clinic clinic1 = ClinicBuilder.startRecording().withDefaults().withId("clinic1").build();
        Clinic clinic2 = ClinicBuilder.startRecording().withDefaults().withId("clinic2").build();

        Patient patient1 = PatientBuilder.startRecording().withDefaults().withClinic(clinic1).build();
        Patient patient2 = PatientBuilder.startRecording().withDefaults().withClinic(clinic1).build();
        Patient patient3 = PatientBuilder.startRecording().withDefaults().withClinic(clinic2).build();

        patientAlerts.add(PatientAlert.newPatientAlert(null, patient1));
        patientAlerts.add(PatientAlert.newPatientAlert(null, patient2));
        patientAlerts.add(PatientAlert.newPatientAlert(null, patient3));

        PatientAlerts alertsForClinic1 = patientAlerts.filterByClinic("clinic1");
        assertEquals(2, alertsForClinic1.size());
        assertEquals(patient1, alertsForClinic1.get(0).getPatient());
        assertEquals(patient2, alertsForClinic1.get(1).getPatient());
    }

    @Test
    public void filterPatientAlerts_ByAlertType() {
        HashMap<String, String> redAdherence = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.AdherenceInRed.name());
        }};

        HashMap<String, String> fallingAdherence = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.FallingAdherence.name());
        }};
        Alert alert1  = new Alert(null, null, null, 0, redAdherence);
        Alert alert2  = new Alert(null, null, null, 0, fallingAdherence);

        patientAlerts.add(PatientAlert.newPatientAlert(alert1, null));
        patientAlerts.add(PatientAlert.newPatientAlert(alert2, null));

        PatientAlerts alertsForAlertType = patientAlerts.filterByAlertType(PatientAlertType.AdherenceInRed);
        assertEquals(1, alertsForAlertType.size());
        assertEquals(PatientAlertType.AdherenceInRed.name(), alertsForAlertType.get(0).getType().name());
    }

    @Test
    public void filterPatientAlerts_ShouldReturnAllAlerts_WhenNoAlertTypeSpecified() {
        HashMap<String, String> redAdherence = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.AdherenceInRed.name());
        }};

        HashMap<String, String> fallingAdherence = new HashMap<String, String>() {{
            put(PatientAlert.PATIENT_ALERT_TYPE, PatientAlertType.FallingAdherence.name());
        }};
        Alert alert1  = new Alert(null, null, null, 0, redAdherence);
        Alert alert2  = new Alert(null, null, null, 0, fallingAdherence);

        patientAlerts.add(PatientAlert.newPatientAlert(alert1, null));
        patientAlerts.add(PatientAlert.newPatientAlert(alert2, null));

        PatientAlerts alertsForAlertType = patientAlerts.filterByAlertType(null);
        assertEquals(2, alertsForAlertType.size());
        assertEquals(PatientAlertType.AdherenceInRed.name(), alertsForAlertType.get(0).getType().name());
        assertEquals(PatientAlertType.FallingAdherence.name(), alertsForAlertType.get(1).getType().name());
    }
}
