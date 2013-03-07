package org.motechproject.tama.web.builder;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Patients;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.Gender;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.web.mapper.CallLogViewMapper;
import org.motechproject.tama.web.model.CallLogSummary;
import org.motechproject.tama.web.view.CallLogView;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CallLogSummaryBuilderTest {

    @Mock
    AllPatients allPatients;

    @Mock
    CallLogViewMapper callLogViewMapper;

    @Mock
    AllIVRLanguagesCache allIVRLanguages;

    private CallLog callLog;

    private DateTime startTime;

    private DateTime initiatedTime;

    private CallLogSummaryBuilder callLogSummaryBuilder;

    @Before
    public void setUp() {
        initMocks(this);
    }

    private void setUpCallLog(CallDirection callDirection) {
        String patientDocId = "patientDocId";
        String clinicId = "clinicId";
        callLog = new CallLog(patientDocId);
        callLog.patientId("patientId");
        initiatedTime = DateUtil.newDateTime(2011, 10, 10, 10, 9, 10);
        startTime = DateUtil.newDateTime(2011, 10, 10, 10, 10, 10);
        callLog.setStartTime(initiatedTime);
        callLog.setEndTime(DateUtil.now());
        callLog.setCallDirection(callDirection);
        callLog.clinicId(clinicId);
        callLog.setPhoneNumber("1234567890");
        callLog.callLanguage("en");

        final CallEvent callEvent = mock(CallEvent.class);
        when(callEvent.getTimeStamp()).thenReturn(startTime);
        when(callEvent.getData()).thenReturn(new CallEventCustomData());
        callLog.setCallEvents(new ArrayList<CallEvent>() {{
            add(callEvent);
        }});
        Clinic clinic = new Clinic(clinicId);
        Patient patient = PatientBuilder.startRecording().withId(patientDocId).withPatientId("patientId").withGender(Gender.newGender("Male")).withClinic(clinic).withTravelTimeToClinicInDays(1).withTravelTimeToClinicInHours(1).withTravelTimeToClinicInMinutes(1).withDateOfBirth(DateUtil.today().minusYears(40)).build();
        CallLogView callLogView = mock(CallLogView.class);
        clinic.setName("clinicName");

        when(allIVRLanguages.getByCode("en")).thenReturn(IVRLanguage.newIVRLanguage("English", "en"));
        when(allPatients.getAll()).thenReturn(Arrays.asList(patient));
        when(callLogViewMapper.toCallLogView(Arrays.asList(callLog))).thenReturn(Arrays.asList(callLogView));

        callLogSummaryBuilder = new CallLogSummaryBuilder(allPatients, new Patients(allPatients.getAll()), allIVRLanguages);
    }

    @Test
    public void shouldBuildCallSummaryForOutboundGivenCallLog() {
        setUpCallLog(CallDirection.Outbound);
        CallLogSummary callLogSummary = callLogSummaryBuilder.build(callLog);

        assertNotNull(callLogSummary);
        assertEquals("clinicName", callLogSummary.getClinicName());
        assertEquals("TAMA", callLogSummary.getSourcePhoneNumber());
        assertEquals("1234567890", callLogSummary.getDestinationPhoneNumber());
        assertEquals("2011-10-10 10:09:10", callLogSummary.getInitiatedDateTime());
        assertEquals("2011-10-10 10:10:10", callLogSummary.getStartDateTime());
        assertEquals("English", callLogSummary.getLanguage());
        assertEquals("patientId", callLogSummary.getPatientId());
        assertEquals("1 Days, 1 Hours, and 1 Minutes", callLogSummary.getPatientDistanceFromClinic());
        assertEquals("40", callLogSummary.getAge());
        assertEquals("Male", callLogSummary.getGender());
    }

    @Test
    public void shouldBuildCallSummaryWithEmptyCallFlowDetailsWhenCallLogDoesNotHaveAnyFlows() {
        DateTime callStartTime = DateUtil.now();
        CallLog callLog = new CallLog();
        callLog.setStartTime(callStartTime);
        callLog.setEndTime(callStartTime.plusMinutes(1));

        CallLogSummary logSummary = new CallLogSummaryBuilder(allPatients, new Patients(allPatients.getAll()), allIVRLanguages).build(callLog);
        assertNotNull(logSummary.getFlowDetailsMap());
    }

    @Test
    public void shouldBuildCallSummaryForGivenInboundCallLog() {
        setUpCallLog(CallDirection.Inbound);
        CallLogSummary callLogSummary = callLogSummaryBuilder.build(callLog);

        assertNotNull(callLogSummary);
        assertEquals("clinicName", callLogSummary.getClinicName());
        assertEquals("1234567890", callLogSummary.getSourcePhoneNumber());
        assertEquals("TAMA", callLogSummary.getDestinationPhoneNumber());
        assertEquals("NA", callLogSummary.getInitiatedDateTime());
        assertEquals("2011-10-10 10:10:10", callLogSummary.getStartDateTime());
        assertEquals("English", callLogSummary.getLanguage());
        assertEquals("patientId", callLogSummary.getPatientId());
        assertEquals("1 Days, 1 Hours, and 1 Minutes", callLogSummary.getPatientDistanceFromClinic());
        assertEquals("40", callLogSummary.getAge());
        assertEquals("Male", callLogSummary.getGender());
    }
}