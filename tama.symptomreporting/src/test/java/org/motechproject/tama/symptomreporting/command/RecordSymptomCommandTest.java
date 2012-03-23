package org.motechproject.tama.symptomreporting.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.patient.service.PatientAlertService;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RecordSymptomCommandTest {
    @Mock
    private TAMAIVRContextFactory tamaivrContextFactory;
    @Mock
    private SymptomRecordingService symptomRecordingService;
    @Mock
    private PatientAlertService patientAlertService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldRecordSymptomReported() {
        KooKooIVRContext kooKooIVRContext = mock(KooKooIVRContext.class);
        HttpServletRequest httpServletRequest = mock(HttpServletRequest.class);
        HttpSession httpSession = mock(HttpSession.class);

        when(kooKooIVRContext.httpRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getSession()).thenReturn(httpSession);
        when(httpSession.getAttribute(TAMAIVRContext.PATIENT_ID)).thenReturn("patientId");
        when(kooKooIVRContext.callId()).thenReturn("callId");

        RecordSymptomCommand recordSymptomCommand = new RecordSymptomCommand(symptomRecordingService, patientAlertService, "fever");
        recordSymptomCommand.execute(kooKooIVRContext);

        verify(symptomRecordingService).save(eq("fever"), eq("patientId"), eq("callId"), any(DateTime.class));
        verify(patientAlertService).appendSymptomToAlert("patientId", "fever");
    }

}
