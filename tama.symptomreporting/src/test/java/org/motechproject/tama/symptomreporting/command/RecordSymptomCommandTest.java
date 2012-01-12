package org.motechproject.tama.symptomreporting.command;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.factory.TAMAIVRContextFactory;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RecordSymptomCommandTest {
    @Mock
    private TAMAIVRContextFactory tamaivrContextFactory;
    @Mock
    private SymptomRecordingService symptomRecordingService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldRecordSymptomReported() {
        KooKooIVRContext kooKooIVRContext = mock(KooKooIVRContext.class);
        TAMAIVRContext tamaivrContext = new TAMAIVRContextForTest().patientDocumentId("patientId").callId("callId");
        when(tamaivrContextFactory.create(kooKooIVRContext)).thenReturn(tamaivrContext);

        RecordSymptomCommand recordSymptomCommand = new RecordSymptomCommand(tamaivrContextFactory, symptomRecordingService, "fever");
        recordSymptomCommand.execute(kooKooIVRContext);

        verify(symptomRecordingService).save(eq("fever"), eq(tamaivrContext.patientDocumentId()), eq(tamaivrContext.callId()), any(DateTime.class));
    }

}
