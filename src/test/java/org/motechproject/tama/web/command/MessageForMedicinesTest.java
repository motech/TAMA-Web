package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.ClinicBuilder;
import org.motechproject.tama.domain.Clinic;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.Clinics;
import org.motechproject.tama.repository.Patients;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageForMedicinesTest {

    @Mock
    private Patients patients;
    @Mock
    private Clinics clinics;
    @Mock
    private PillReminderService pillReminderService;
    @Mock
    private IVRContext context;
    @Mock
    private IVRRequest ivrRequest;
    @Mock
    private IVRSession ivrSession;
    private MessageForMedicines messageForMedicines;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = new Patient();
        patient.setClinic_id("clinicId");
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").build();

        messageForMedicines = new MessageForMedicines(patients, clinics, pillReminderService);
        when(context.ivrSession()).thenReturn(ivrSession);
        when(context.ivrRequest()).thenReturn(ivrRequest);
        when(ivrSession.getPatientId()).thenReturn("patientId");
        when(patients.get("patientId")).thenReturn(patient);
        when(clinics.get("clinicId")).thenReturn(clinic);
        when(pillReminderService.medicinesFor("regimenId", "dosageId")).thenReturn(Arrays.asList("medicine1", "medicine2"));
    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinestoBeTaken() {
        Map params = new HashMap<String, String>();
        params.put(PillReminderCall.REGIMEN_ID, "regimenId");
        params.put(PillReminderCall.DOSAGE_ID, "dosageId");
        when(ivrRequest.getTamaParams()).thenReturn(params);

        String[] messages = messageForMedicines.execute(context);

        assertEquals(5, messages.length);
        assertEquals("clinicName", messages[0]);
        assertEquals("its_time_for_the_pill", messages[1]);
        assertEquals("medicine1", messages[2]);
        assertEquals("medicine2", messages[3]);
        assertEquals("pill_from_the_bottle", messages[4]);
    }
}
