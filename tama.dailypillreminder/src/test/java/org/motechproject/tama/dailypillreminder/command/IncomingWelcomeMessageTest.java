package org.motechproject.tama.dailypillreminder.command;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.dailypillreminder.DailyPillReminderContextForTest;
import org.motechproject.tama.dailypillreminder.service.DailyPillReminderService;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.command.ClinicNameMessageBuilder;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.refdata.domain.IVRLanguage;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class IncomingWelcomeMessageTest {

    private final String EXPECTED_WELCOME_MESSAGE = "Welcome to TAMA";
    @Mock
    private ClinicNameMessageBuilder clinicMessageBuilder;
    @Mock
    private DailyPillReminderService dailyPillReminderService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private AllClinics allClinics;

    private IncomingWelcomeMessage incomingWelcomeMessage;

    private DailyPillReminderContextForTest ivrContext;

    @Before
    public void setUp() {
        initMocks(this);

        ivrContext = new DailyPillReminderContextForTest(new TAMAIVRContextForTest()).patientDocumentId("patientDocId");

        Patient patient = PatientBuilder.startRecording().withDefaults().withId(ivrContext.patientDocumentId()).build();

        when(allPatients.get(ivrContext.patientDocumentId())).thenReturn(patient);
        when(clinicMessageBuilder.getInboundMessage(any(Clinic.class),
                any(IVRLanguage.class))).thenReturn(EXPECTED_WELCOME_MESSAGE);

        incomingWelcomeMessage = new IncomingWelcomeMessage(allPatients, allClinics, clinicMessageBuilder, dailyPillReminderService);
    }

    @Test
    public void shouldPlayClinicGreetingMessageIfNoTreeHasBeenTraversed() {
        ivrContext.hasTraversedAnyTree(false);
        assertArrayEquals(new String[] {EXPECTED_WELCOME_MESSAGE}, incomingWelcomeMessage.executeCommand(ivrContext));
    }

    @Test
    public void shouldNotPlayClinicGreetingMessageIfAnyTreeWasTraversed() {
        ivrContext.hasTraversedAnyTree(true);
        assertTrue(ArrayUtils.isEmpty(incomingWelcomeMessage.executeCommand(ivrContext)));
    }

}
