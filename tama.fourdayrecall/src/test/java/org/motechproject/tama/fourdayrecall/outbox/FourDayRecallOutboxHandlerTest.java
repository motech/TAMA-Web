package org.motechproject.tama.fourdayrecall.outbox;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.model.DayOfWeek;
import org.motechproject.model.MotechEvent;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.outbox.listener.OutboxCallListener;
import org.motechproject.tama.outbox.service.OutboxSchedulerService;
import org.motechproject.tama.outbox.service.OutboxService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TimeOfDay;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.HashMap;
import java.util.Properties;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class FourDayRecallOutboxHandlerTest extends BaseUnitTest {

    @Mock
    private OutboxService outboxService;
    @Mock
    private AllPatients allPatients;
    @Mock
    private Properties fourDayRecallProperties;
    @Mock
    private OutboxCallListener outboxListener;

    private FourDayRecallOutboxHandler fourDayRecallOutboxHandler;
    private MotechEvent motechEvent;

    @Before
    public void setUp() {
        initMocks(this);
        fourDayRecallOutboxHandler = new FourDayRecallOutboxHandler(allPatients, fourDayRecallProperties, outboxService, outboxListener);
        final String patientId = "patientId";
        motechEvent = new MotechEvent("foo", new HashMap<String, Object>() {{
            put(OutboxSchedulerService.EXTERNAL_ID_KEY, patientId);
        }});
        final Patient patient = PatientBuilder.startRecording().withDefaults().withWeeklyCallPreference(DayOfWeek.Friday, new TimeOfDay()).build();

        when(allPatients.get(patientId)).thenReturn(patient);
        when(fourDayRecallProperties.getProperty(TAMAConstants.FOUR_DAY_RECALL_DAYS_TO_RETRY)).thenReturn("2");
    }

    @Test
    public void shouldMakeOutboxCalls_OnlyWhenItsNotDayOfFourDayRecall() {
        mockCurrentDate(new DateTime(2012, 2, 2, 10, 0, 0)); // Thursday : not a day of four day recall
        fourDayRecallOutboxHandler.handle(motechEvent);

        verify(outboxService).call(motechEvent);
        verify(outboxListener).register(CallPreference.FourDayRecall, fourDayRecallOutboxHandler);
    }

    @Test
    public void shouldNotMakeOutboxCalls_OnDayOfFourDayRecall() {
        mockCurrentDate(new DateTime(2012, 2, 4, 10, 0, 0)); // Saturday : a retry day for four day recall
        fourDayRecallOutboxHandler.handle(motechEvent);

        verify(outboxService, times(0)).call(motechEvent);
        verify(outboxListener).register(CallPreference.FourDayRecall, fourDayRecallOutboxHandler);
    }
}
