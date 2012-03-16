package org.motechproject.tama.web.builder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.repository.AllIVRLanguages;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogSummaryBuilderTest {

    CallLogSummaryBuilder callLogSummaryBuilder;

    @Mock
    AllPatients allPatients;
    @Mock
    CallLogViewMapper callLogViewMapper;
    @Mock
    AllClinics allClinics;
    @Mock
    AllIVRLanguages allIVRLanguages;

    private TamaIVRMessage ivrMessage;

    @Before
    public void setUp() {
        initMocks(this);
        callLogSummaryBuilder = new CallLogSummaryBuilder(allPatients, callLogViewMapper, allClinics, allIVRLanguages);
        Properties properties = new Properties();
        ivrMessage = new TamaIVRMessage(properties);
    }

    @Test
    public void shouldBuildCallSummaryForGivenCallLog(){
        String patientDocId = "patientDocId";
        String clinicId = "clinicId";
        CallLog callLog = new CallLog(patientDocId);
        callLog.patientId("patientId");
        callLog.setStartTime(DateUtil.now().minusMinutes(20));
        callLog.setEndTime(DateUtil.now());
        callLog.setCallDirection(CallDirection.Inbound);
        callLog.clinicId(clinicId);
        callLog.setPhoneNumber("1234567890");
        callLog.callLanguage("en");

        Clinic clinic = new Clinic(clinicId);
        CallLogView callLogView = mock(CallLogView.class);
        clinic.setName("clinicName");

        when(allIVRLanguages.findByLanguageCode("en")).thenReturn(IVRLanguage.newIVRLanguage("English", "en"));
        when(allClinics.get(clinicId)).thenReturn(clinic);
        when(allPatients.get(patientDocId)).thenReturn(PatientBuilder.startRecording().withId(patientDocId).withPatientId("patientId").withTravelTimeToClinicInDays(1).withTravelTimeToClinicInHours(1).withTravelTimeToClinicInMinutes(1).build());
        when(callLogViewMapper.toCallLogView(Arrays.asList(callLog))).thenReturn(Arrays.asList(callLogView));

        CallLogSummary callLogSummary = callLogSummaryBuilder.build(callLog);

        assertNotNull(callLogSummary);
        assertEquals("clinicName", callLogSummary.getClinicName());
        assertEquals("TAMA", callLogSummary.getDestinationPhoneNumber());
        assertEquals("1234567890", callLogSummary.getSourcePhoneNumber());
        assertEquals("English", callLogSummary.getLanguage());
        assertEquals("patientId", callLogSummary.getPatientId());
        assertEquals("1 Days, 1 Hours, And 1 Minutes.", callLogSummary.getPatientDistanceFromClinic());
    }
}