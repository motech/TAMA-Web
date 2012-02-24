package org.motechproject.tama.web.view;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.CallEventCustomData;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.decisiontree.TAMATreeRegistry;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallState;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CallLogViewTest {

    private CallLogView callLogView;

    @Test
    public void titleShouldBeTamaCalledPatientId_WhenCallDirectionIsOutbound_AndPatientIsAuthenticated() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Outbound);
        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());

        assertEquals("Tama called patientId || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void titleShouldBeTamaCalledPatientId_WhenCallDirectionIsOutbound_AndPatientIsNotAuthenticated() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Outbound);
        callLogView = new CallLogView(null, callLog, "clinicName", new ArrayList<String>() {{
            add("P1");
            add("P2");
        }});

        assertEquals("Tama called P1 or P2 || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void titleShouldBePatientIdCalledTama_WhenCallDirectionIsInbound_AndPatientIsAuthenticated() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Inbound);
        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());

        assertEquals("patientId called Tama || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void titleShouldBePatientIdCalledTama_WhenCallDirectionIsInbound_AndPatientIsNotAuthenticated() {
        CallLog callLog = setUpCallLogs();

        callLog.setCallDirection(CallDirection.Inbound);
        callLogView = new CallLogView(null, callLog, "clinicName", new ArrayList<String>() {{
            add("P1");
        }});

        assertEquals("P1 called Tama || Clinic :clinicName", callLogView.getTitle());
    }

    @Test
    public void shouldFormatDateToRemove_IST_And_Time() {
        CallLog callLog = setUpCallLogs();

        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());

        assertEquals("Fri Oct 07 2011", callLogView.getCallDateFromCallLogDateTime());
    }

    @Test
    public void getAListOfCallFlows_GivenAListOfCallEvents() {
        CallLog callLog = setUpCallLogs();
        final CallEvent healthTipsCallEvent = createCallEvent("GotDTMF", CallState.HEALTH_TIPS.name(), "", "");

        callLog.setCallEvents(new ArrayList<CallEvent>() {{
            add(healthTipsCallEvent);
        }});

        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());
        assertEquals(1, callLogView.getCallFlowGroupViews().size());
        assertEquals("Health Tips", callLogView.getFlows());
    }

    @Test
    public void constructCallLog_ForAMissedCallEvent() {
        CallLog callLog = setUpCallLogs();
        final CallEvent missedCallEvent = createCallEvent("Missed", "", "", "");

        callLog.setCallEvents(new ArrayList<CallEvent>() {{
            add(missedCallEvent);
        }});

        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());
        assertEquals(1, callLogView.getCallFlowGroupViews().size());
        assertEquals("Missed", callLogView.getFlows());
    }

    @Test
    public void callFlowGroupTitle_AsUnauthenticated_WhenTheUserHungUpTheCall_BeforeAuthentication() {
        CallLog callLog = setUpCallLogs();
        final CallEvent newCallEvent = createCallEvent("NewCall", CallState.STARTED.name(), "", "signature_music");
        final CallEvent wrongPasscodeEvent = createCallEvent("NewCall", CallState.STARTED.name(), "", "signature_music");
        final CallEvent hangUpEvent = createCallEvent("Hangup", "", "", "");

        callLog.setCallEvents(new ArrayList<CallEvent>() {{
            add(newCallEvent);
            add(wrongPasscodeEvent);
            add(hangUpEvent);
        }});

        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());
        assertEquals(1, callLogView.getCallFlowGroupViews().size());
        assertEquals("Unauthenticated", callLogView.getFlows());
    }

    @Test
    public void associateEventsToRightFlows_GivenAListOfCallEvents() {
        CallLog callLog = setUpCallLogs();
        final CallEvent newCallEvent = createCallEvent("NewCall", CallState.STARTED.name(), "", "signature_music");
        final CallEvent menuEvent = createCallEvent("gotDtmf", CallState.AUTHENTICATED.name(), TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, "responseXML1");
        final CallEvent pillConfirmationEvent = createCallEvent("gotDtmf", CallState.AUTHENTICATED.name(), TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, "responseXML2");
        final CallEvent transitioningToSymptomEvent = createCallEvent("gotDtmf", CallState.SYMPTOM_REPORTING.name(), TAMATreeRegistry.CURRENT_DOSAGE_CONFIRM, "");
        final CallEvent transitionedToSymptomEvent = createCallEvent("gotDtmf", CallState.SYMPTOM_REPORTING_TREE.name(), TAMATreeRegistry.REGIMEN_1_TO_6, "responseXML3");
        final CallEvent symptomReportingEvent = createCallEvent("gotDtmf", CallState.SYMPTOM_REPORTING_TREE.name(), TAMATreeRegistry.REGIMEN_1_TO_6, "responseXML4");
        final CallEvent hangUpEvent = createCallEvent("Hangup", "", "", "");

        callLog.setCallEvents(new ArrayList<CallEvent>() {{
            add(newCallEvent);
            add(menuEvent);
            add(pillConfirmationEvent);
            add(transitioningToSymptomEvent);
            add(transitionedToSymptomEvent);
            add(symptomReportingEvent);
            add(hangUpEvent);
        }});

        callLogView = new CallLogView("patientId", callLog, "clinicName", new ArrayList<String>());
        assertEquals(4, callLogView.getCallFlowGroupViews().size());
        assertEquals("Menu, Symptoms, Pill Reminder", callLogView.getFlows());
    }

    private CallEvent createCallEvent(String name, String callState, String treeName, String responseXML) {
        CallEventCustomData callEventCustomData = new CallEventCustomData();
        callEventCustomData.add(CallEventConstants.CALL_STATE, callState);
        callEventCustomData.add(CallEventConstants.TREE_NAME, treeName);
        callEventCustomData.add(CallEventConstants.CUSTOM_DATA_LIST, responseXML);
        CallEvent callEvent = new CallEvent(name);
        callEvent.setData(callEventCustomData);
        return callEvent;
    }

    private CallLog setUpCallLogs() {
        CallLog callLog = new CallLog();
        callLog.setStartTime(new DateTime(2011, 10, 7, 0, 0, 0));
        callLog.setEndTime(new DateTime(2011, 10, 7, 0, 0, 0).plusMinutes(2));
        return callLog;
    }
}
