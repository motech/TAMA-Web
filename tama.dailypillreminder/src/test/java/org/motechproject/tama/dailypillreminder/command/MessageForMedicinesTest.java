package org.motechproject.tama.dailypillreminder.command;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

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
    private MessageForMedicines messageForMedicines;

    private DateTime now;
    private DailyPillReminderContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        Patient patient = new Patient();
        patient.setClinic_id("clinicId");
        Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinicName").build();

        messageForMedicines = new MessageForMedicines(allPatients, allClinics, null);
        context = (DailyPillReminderContextForTest) new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).pillRegimen(PillRegimenResponseBuilder.startRecording().withDefaults().build()).patientId("patientId").callDirection(CallDirection.Outbound);
        when(allPatients.get("patientId")).thenReturn(patient);
        when(allClinics.get("clinicId")).thenReturn(clinic);

        LocalDate today = DateUtil.today();
        now = DateUtil.newDateTime(today, 10, 0, 0);
        mockStatic(DateUtil.class);

        when(DateUtil.today()).thenReturn(today);
        when(DateUtil.now()).thenReturn(now);
    }

    @Test
    public void shouldReturnMessagesWithAListOfMedicinesToBeTaken() {
        int dosageHour = 16;
        DateTime timeWithinPillWindow = now.withHourOfDay(dosageHour).withMinuteOfHour(5);
        context.dosageId("currentDosageId").callStartTime(timeWithinPillWindow);
        String[] messages = messageForMedicines.executeCommand(context);

        assertEquals(5, messages.length);
        assertEquals("clinicName", messages[0]);
        assertEquals("001_02_02_itsTimeForPill1", messages[1]);
        assertEquals("pillmedicine1", messages[2]);
        assertEquals("pillmedicine2", messages[3]);
        assertEquals("001_07_07_fromTheBottle1", messages[4]);
    }
}