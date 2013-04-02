package org.motechproject.tama.messages.message;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.refdata.domain.IVRLanguage;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.util.DateUtil.now;

public class VisitReminderMessageTest {

    private Patient patient;
    @Mock
    private ClinicVisit clinicVisit;
    @Mock
    private TAMAIVRContext tamaivrContext;
    @Mock
    private IVRLanguage ivrLanguage;

    private VisitReminderMessage visitReminderMessage;

    @Before
    public void setup() {
        initMocks(this);
        when(ivrLanguage.getCode()).thenReturn(IVRLanguage.ENGLISH_CODE);
        patient = PatientBuilder.startRecording().withDefaults().withIVRLanguage(ivrLanguage).build();
        visitReminderMessage = new VisitReminderMessage(3, clinicVisit, patient);
    }

    @Test
    public void shouldBeValidIfCurrentDateIsInReminderWindow() {
        DateTime now = now();

        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now.plusDays(3));
        when(clinicVisit.isUpcoming()).thenReturn(true);

        assertTrue(visitReminderMessage.isValid(now));
    }

    @Test
    public void shouldNotBeValidIfCurrentDateIsBeforeReminderWindow() {
        DateTime now = now();

        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now.plusDays(4));

        assertFalse(visitReminderMessage.isValid(now));
    }

    @Test
    public void shouldNotBeValidIfCurrentDateIsAfterConfirmedDate() {
        DateTime now = now();

        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now.minusDays(1));

        assertFalse(visitReminderMessage.isValid(now));
    }

    @Test
    public void shouldNotBeValidIfVisitIsNotUpcoming() {
        DateTime now = now();

        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now.plusDays(3));
        when(clinicVisit.isUpcoming()).thenReturn(false);

        assertFalse(visitReminderMessage.isValid(now));
    }

    @Test
    public void shouldNotBeValidIfClinicVisitIsNull() {
        DateTime now = now();
        visitReminderMessage = new VisitReminderMessage(3, null, patient);

        assertFalse(visitReminderMessage.isValid(now));
    }

    @Test
    public void shouldReturnPatientDocumentIdWithClinicVisitIdAsUniqueId() {
        assertEquals(patient.getId() + clinicVisit.getId(), visitReminderMessage.getId());
    }

    @Test
    public void shouldBuildVisitReminderMessage() {
        when(clinicVisit.getConfirmedAppointmentDate()).thenReturn(now());
        KookooIVRResponseBuilder response = visitReminderMessage.build(tamaivrContext);
        assertTrue(response.getPlayAudios().contains(TamaIVRMessage.NEXT_VISIT_REMINDER_IS_DUE_PART1));
    }
}
