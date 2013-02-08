package org.motechproject.tama.patient.reporting;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.reports.contract.PatientRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientRequestMapperTest {

    @Mock
    private AllIVRLanguagesCache ivrLanguagesCache;
    @Mock
    private AllGendersCache gendersCache;

    private PatientRequestMapper mapper;
    private IVRLanguage ivrLanguage;
    private Gender gender;

    @Before
    public void setUp() {
        initMocks(this);
        mapper = new PatientRequestMapper(ivrLanguagesCache, gendersCache);
        setupIvrLanguage();
        setupGender();
    }

    private void setupGender() {
        gender = new Gender("genderId");
        gender.setType("Female");
    }

    private void setupIvrLanguage() {
        ivrLanguage = new IVRLanguage("ivrLanguageId");
        ivrLanguage.setName("ivrLanguageName");
    }

    @Test
    public void shouldMapBasicDetails() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        when(gendersCache.getBy(patient.getGenderId())).thenReturn(gender);
        when(ivrLanguagesCache.getBy(patient.getPatientPreferences().getIvrLanguageId())).thenReturn(ivrLanguage);
        PatientRequest patientRequest = mapper.map(patient);

        assertEquals(new BasicDetails(patient), new BasicDetails(patientRequest));
        assertEquals("ivrLanguageName", patientRequest.getIvrLanguage());
    }

    @Test
    public void shouldSetIVRLanguageNameOnPatientRequest() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        when(gendersCache.getBy(patient.getGenderId())).thenReturn(gender);
        when(ivrLanguagesCache.getBy(patient.getPatientPreferences().getIvrLanguageId())).thenReturn(ivrLanguage);
        PatientRequest patientRequest = mapper.map(patient);
        assertEquals(new BasicDetails(patient), new BasicDetails(patientRequest));
    }

    @Test
    public void shouldMapIVRDetails() {
        Patient patient = PatientBuilder.startRecording().withDefaults().build();

        when(gendersCache.getBy(patient.getGenderId())).thenReturn(gender);
        when(ivrLanguagesCache.getBy(patient.getPatientPreferences().getIvrLanguageId())).thenReturn(ivrLanguage);
        PatientRequest patientRequest = mapper.map(patient);
        assertEquals(new IVRDetails(patient, ivrLanguage.getName(), gender.getType()), new IVRDetails(patientRequest));
    }
}
