package org.motechproject.tama.ivr.log;

import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CallFlowDetailsTest {

    private CallFlowDetails callFlowDetails;

    @Before
    public void setup() {
        callFlowDetails = new CallFlowDetails();
    }

    @Test
    public void shouldNotFailWithNullPointerWhenAddingNullResponses() {
        callFlowDetails.respondedWith(null);
        assertTrue(callFlowDetails.getResponses().isEmpty());
    }

    @Test
    public void shouldSummarizeAllResponses() {
        callFlowDetails.respondedWith(asList("response1"));
        callFlowDetails.respondedWith(asList("response2"));
        assertEquals(asList("response1", "response2"), callFlowDetails.getResponses());
    }

    @Test
    public void shouldReturnAllResponsesAsString() {
        callFlowDetails.respondedWith(asList("response1"));
        callFlowDetails.respondedWith(asList("response2"));
        assertEquals("response1, response2", callFlowDetails.getResponsesAsString());
    }

    @Test
    public void shouldReturnNAWhenNoResponsesAreAvailable() {
        assertEquals("NA", callFlowDetails.getResponsesAsString());
    }

    @Test
    public void shouldSummarizeTheNumberOfTimesThatTheFlowWasAccessed() {
        callFlowDetails.flowAccessed(1);
        callFlowDetails.flowAccessed(2);
        assertEquals("1, 2", callFlowDetails.getIndividualAccessDurations());
    }
}
