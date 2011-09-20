package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.server.service.ivr.IVRContext;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.AllClinics;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.util.TamaSessionUtil;
import org.motechproject.tama.util.TamaSessionUtil.TamaSessionAttribute;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;


@RunWith(PowerMockRunner.class)
@PrepareForTest(DateUtil.class)
public class MessageForMedicinesTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllClinics allClinics;
    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;

    private MessageForMedicines messageForMedicines;

    private LocalDate today;

    private DateTime now;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = new Patient();
        patient.setClinic_id("clinicId");
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").build();

        messageForMedicines = new MessageForMedicines(allPatients, allClinics);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.get(TamaSessionUtil.TamaSessionAttribute.REGIMEN_FOR_PATIENT)).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        when(context.ivrRequest()).thenReturn(ivrRequest);
        when(ivrSession.get(TamaSessionUtil.TamaSessionAttribute.PATIENT_DOC_ID)).thenReturn("patientId");
        when(allPatients.get("patientId")).thenReturn(patient);
        when(allClinics.get("clinicId")).thenReturn(clinic);

        today = DateUtil.today();
        now = DateUtil.now();

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.now()).thenReturn(now);

    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken() {
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);

        when(ivrRequest.getParameter(PillReminderCall.DOSAGE_ID)).thenReturn("currentDosageId");
        when(ivrSession.getCallTime()).thenReturn(timeWithinPillWindow);

        String[] messages = messageForMedicines.execute(context);

        assertEquals(5, messages.length);
        assertEquals("clinicName", messages[0]);
        assertEquals("001_02_02_itsTimeForPill1", messages[1]);
        assertEquals("pillmedicine1", messages[2]);
        assertEquals("pillmedicine2", messages[3]);
        assertEquals("001_07_07_fromTheBottle1", messages[4]);
    }
}
