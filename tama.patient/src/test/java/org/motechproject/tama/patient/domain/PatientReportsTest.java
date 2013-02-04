package org.motechproject.tama.patient.domain;

import org.junit.Test;
import org.motechproject.tama.patient.builder.PatientReportBuilder;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class PatientReportsTest {

    @Test
    public void shouldGetPatientReportByPatientDocumentId() {
        String patientDocId1 = "patientDocId1";
        PatientReport patientReport1 = PatientReportBuilder.newPatientReport().withPatientDocumentId(patientDocId1).build();
        PatientReport patientReport2 = PatientReportBuilder.newPatientReport().withPatientDocumentId("patientDocId2").build();

        PatientReports patientReports = new PatientReports(asList(patientReport1, patientReport2));
        assertEquals(patientReport1, patientReports.getPatientReport(patientDocId1));
    }

    @Test
    public void shouldReturnNullIfNoPatientReportIsFoundForGivenPatientDocId() {
        PatientReports patientReports = new PatientReports(new ArrayList<PatientReport>());
        assertNull(patientReports.getPatientReport("patientDocId"));
    }

    @Test
    public void shouldReturnAllPatientDocumentIds() {
        String patientDocId1 = "patientDocId1";
        String patientDocId2 = "patientDocId2";
        PatientReport patientReport1 = PatientReportBuilder.newPatientReport().withPatientDocumentId(patientDocId1).build();
        PatientReport patientReport2 = PatientReportBuilder.newPatientReport().withPatientDocumentId(patientDocId2).build();

        PatientReports patientReports = new PatientReports(asList(patientReport1, patientReport2));
        assertEquals(asList(patientDocId1, patientDocId2), patientReports.getPatientDocIds());
    }

    @Test
    public void shouldReturnEmptyListForPatientDocIdsIfNoPatientReportsArePresent() {
        PatientReports patientReports = new PatientReports(new ArrayList<PatientReport>());
        assertTrue(patientReports.getPatientDocIds().isEmpty());
    }
}
