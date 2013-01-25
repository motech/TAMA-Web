package org.motechproject.tama.patient.reporting;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.reports.contract.PatientRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientRequestMapperTest {

    @Mock
    private AllIVRLanguagesCache ivrLanguagesCache;
    private PatientRequestMapper mapper;
    private IVRLanguage ivrLanguage;

    @Before
    public void setUp() {
        initMocks(this);
        mapper = new PatientRequestMapper(ivrLanguagesCache);
        ivrLanguage = new IVRLanguage("ivrLanguageId");
        ivrLanguage.setCode("ivrLanguageCode");
    }

    @Test
    public void shouldMapBasicDetails() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        when(ivrLanguagesCache.getBy(patient.getPatientPreferences().getIvrLanguageId())).thenReturn(ivrLanguage);
        PatientRequest patientRequest = mapper.map(patient);
        assertEquals(new BasicDetails(patient), new BasicDetails(patientRequest));
        assertEquals("ivrLanguageCode", patientRequest.getIvrLanguage());
    }

    @Test
    public void shouldSetIVRLanguageCodeOnPatientRequest() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        when(ivrLanguagesCache.getBy(patient.getPatientPreferences().getIvrLanguageId())).thenReturn(ivrLanguage);
        PatientRequest patientRequest = mapper.map(patient);
        assertEquals(new BasicDetails(patient), new BasicDetails(patientRequest));
    }

    @Test
    public void shouldMapIVRDetails() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        when(ivrLanguagesCache.getBy(patient.getPatientPreferences().getIvrLanguageId())).thenReturn(ivrLanguage);
        PatientRequest patientRequest = mapper.map(patient);
        assertEquals(new IVRDetails(patient), new IVRDetails(patientRequest));
    }
}
