package org.motechproject.tama.symptomreporting.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.symptomreporting.service.SymptomRecordingService;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class RecordAdviceGivenCommandTest {

    @Mock
    private SymptomRecordingService symptomRecordingService;

    @Before
    public void setUp() {
        initMocks(this);
    }
    
    @Test
    public void shouldRecodeTheAdviceNodeName(){
        final String callId = "callId";
        final String adviceNodeName = "adv_crocin01";
        final RecordAdviceGivenCommand recordAdviceGivenCommand = new RecordAdviceGivenCommand(symptomRecordingService, adviceNodeName);
        final TAMAIVRContextForTest context = new TAMAIVRContextForTest().callId(callId);
        recordAdviceGivenCommand.executeCommand(context);
        verify(symptomRecordingService).saveAdviceGiven(callId, adviceNodeName);
    }
}