package org.motechproject.tama.web.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Patients;
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
    private Patients patients;
    @Mock
    private Clinics clinics;
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

        messageForMedicines = new MessageForMedicines(patients, clinics);

        when(context.ivrSession()).thenReturn(ivrSession);
        when(ivrSession.getPillRegimen()).thenReturn(PillRegimenResponseBuilder.startRecording().withDefaults().build());
        when(context.ivrRequest()).thenReturn(ivrRequest);
        when(ivrSession.getPatientId()).thenReturn("patientId");
        when(patients.get("patientId")).thenReturn(patient);
        when(clinics.get("clinicId")).thenReturn(clinic);

        today = DateUtil.today();
        now = DateUtil.now();

        mockStatic(DateUtil.class);
        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.now()).thenReturn(now);

    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.DOSAGE_ID, "currentDosageId");

        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);

        when(ivrRequest.getTamaParams()).thenReturn(params);
        when(ivrSession.getCallTime()).thenReturn(timeWithinPillWindow);

        String[] messages = messageForMedicines.execute(context);

        assertEquals(5, messages.length);
        assertEquals("clinicName", messages[0]);
        assertEquals("001_02_02_itsTimeForPill1", messages[1]);
        assertEquals("medicine1", messages[2]);
        assertEquals("medicine2", messages[3]);
        assertEquals("001_07_07_fromTheBottle1", messages[4]);
    }
}
