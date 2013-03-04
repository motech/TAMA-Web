package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageForMedicinesTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllClinics allClinics;
    @Mock
    private ClinicNameMessageBuilder clinicNameMessageBuilder;
    private MessageForMedicines messageForMedicines;

    private DateTime now;
    private DailyPillReminderContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = new Patient();
        patient.setClinic_id("clinicId");
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").build();

        messageForMedicines = new MessageForMedicines(allPatients, allClinics, null, clinicNameMessageBuilder);
        context = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(PillRegimenResponseBuilder.startRecording().withDefaults().build()).patientDocumentId("patientId").callDirection(CallDirection.Outbound);
        when(allPatients.get("patientId")).thenReturn(patient);
        when(allClinics.get("clinicId")).thenReturn(clinic);
        when(clinicNameMessageBuilder.getOutboundMessage(clinic, patient.getPatientPreferences().getIvrLanguage())).thenReturn("test_clinic");

        LocalDate today = DateUtil.today();
        now = DateUtil.newDateTime(today, 10, 0, 0);
    }

    @Test
    public void shouldReturnMessages() {
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);
        context.callStartTime(timeWithinPillWindow);
        String[] messages = messageForMedicines.executeCommand(context);

        assertEquals(3, messages.length);
        assertEquals("test_clinic", messages[0]);
        assertEquals(TamaIVRMessage.ITS_TIME_FOR_THE_PILL_OUTGOING_CALL_FOR_CURRENT_DOSAGE, messages[1]);
        assertEquals(TamaIVRMessage.FROM_THE_BOTTLE_OUTGOING_CALL_FOR_CURRENT_DOSAGE, messages[2]);
    }
}
