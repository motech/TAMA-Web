package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.context.TAMAIVRContext;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.domain.IVRAuthenticationStatus;
import org.motechproject.tama.patient.domain.Patient;

import java.util.ArrayList;
import java.util.List;

public class TAMAIVRContextForTest extends TAMAIVRContext {

    private String dtmfInput;
    private String callerId;
    private int numberOfLoginAttempts;
    private IVRAuthenticationStatus authenticationStatus;
    private CallState callState;
    private String callId;
    private CallDirection callDirection;
    private DateTime callStartTime;
    private String patientId;
    private Patient patient;
    private String lastCompletedTree;
    private int numberOfTimesReminderSent;
    private int totalNumberOfTimesToSendReminder;
    private String requestedCallerId;
    private boolean outboxCompleted;
    private String preferredLanguage;
    private String isOutboundCall;
    private String symptomReportingTree;
    private String lastPlayedHealthTip;
    private int numberOfHealthTipsPlayed;
    private int retryInterval;
    private boolean isDialState;
    private List<String> completedTrees = new ArrayList<String>();
    private boolean hasTraversedAnyTree;
    private boolean messagePushed;

    public TAMAIVRContextForTest() {
        super();
    }

    public TAMAIVRContextForTest(KooKooIVRContext kooKooIVRContext) {
        super(kooKooIVRContext);
    }

    @Override
    public String dtmfInput() {
        return dtmfInput;
    }

    public TAMAIVRContextForTest dtmfInput(String dtmfInput) {
        this.dtmfInput = dtmfInput;
        return this;
    }

    @Override
    public String callerId() {
        return callerId;
    }

    public void callerId(String callerId) {
        this.callerId = callerId;
    }

    @Override
    public int numberOfLoginAttempts() {
        return numberOfLoginAttempts;
    }

    public void numberOfLoginAttempts(int numberOfLoginAttempts) {
        this.numberOfLoginAttempts = numberOfLoginAttempts;
    }

    @Override
    public void userAuthenticated(IVRAuthenticationStatus authenticationStatus) {
        this.authenticationStatus = authenticationStatus;
    }

    public IVRAuthenticationStatus authenticationStatus() {
        return authenticationStatus;
    }

    @Override
    public CallState callState() {
        return callState;
    }

    @Override
    public String callId() {
        return callId;
    }

    public TAMAIVRContextForTest callId(String callid) {
        this.callId = callid;
        return this;
    }

    @Override
    public boolean isDialState() {
        return isDialState;
    }

    public TAMAIVRContextForTest isDialState(boolean dialState) {
        isDialState = dialState;
        return this;
    }

    @Override
    public CallDirection callDirection() {
        return callDirection;
    }

    public TAMAIVRContextForTest callDirection(CallDirection callDirection) {
        this.callDirection = callDirection;
        return this;
    }

    @Override
    public DateTime callStartTime() {
        return callStartTime;
    }

    public TAMAIVRContextForTest callStartTime(DateTime callStartTime) {
        this.callStartTime = callStartTime;
        return this;
    }

    @Override
    public String patientDocumentId() {
        return patientId;
    }

    public TAMAIVRContextForTest patientDocumentId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public int numberOfTimesReminderSent() {
        return numberOfTimesReminderSent;
    }

    public TAMAIVRContextForTest numberOfTimesReminderSent(int numberOfTimesRemindersSent) {
        this.numberOfTimesReminderSent = numberOfTimesRemindersSent;
        return this;
    }

    public int totalNumberOfTimesToSendReminder() {
        return totalNumberOfTimesToSendReminder;
    }

    public TAMAIVRContextForTest totalNumberOfTimesToSendReminder(int totalNumberOfTimesToSendReminder) {
        this.totalNumberOfTimesToSendReminder = totalNumberOfTimesToSendReminder;
        return this;
    }

    @Override
    public void callState(CallState callState) {
        this.callState = callState;
    }

    @Override
    public void lastCompletedTree(String treeName) {
        this.lastCompletedTree = treeName;
    }

    @Override
    public String lastCompletedTree() {
        return lastCompletedTree;
    }

    @Override
    public String requestedCallerId() {
        return requestedCallerId;
    }

    @Override
    public boolean hasTraversedAnyTree() {
        return hasTraversedAnyTree;
    }

    @Override
    public boolean isMessagesPushed() {
        return messagePushed;
    }

    @Override
    public void setMessagesPushed(boolean value) {
        this.messagePushed = value;
    }

    public void hasTraversedAnyTree(boolean hasTraversedAnyTree) {
        this.hasTraversedAnyTree = hasTraversedAnyTree;
    }

    @Override
    public boolean isOutBoxCall() {
        return "true".equals(isOutboundCall);
    }

    public TAMAIVRContextForTest isOutBoxCall(String isOutboundCall) {
        this.isOutboundCall = isOutboundCall;
        return this;
    }

    public TAMAIVRContextForTest requestedCallerId(String requestedCallerId) {
        this.requestedCallerId = requestedCallerId;
        return this;
    }

    @Override
    public String preferredLanguage() {
        return preferredLanguage;
    }

    public void preferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    @Override
    public String symptomReportingTree() {
        return symptomReportingTree;
    }

    @Override
    public void symptomReportingTree(String symptomReportingTree) {
        this.symptomReportingTree = symptomReportingTree;
    }

    public TAMAIVRContextForTest retryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
        return this;
    }

    public int retryInterval() {
        return retryInterval;
    }

    @Override
    public List<String> getListOfCompletedTrees() {
        return this.completedTrees;
    }

    @Override
    public boolean hasTraversedTree(String treeName) {
        return completedTrees.contains(treeName);
    }

    @Override
    public void addLastCompletedTreeToListOfCompletedTrees(String treeName) {
        this.completedTrees.add(treeName);
    }

    @Override
    public void setLastPlayedHealthTip(String message) {
        this.lastPlayedHealthTip = message;
    }

    @Override
    public String getLastPlayedHealthTip() {
        return this.lastPlayedHealthTip;
    }

    @Override
    public void setPlayedHealthTipsCount(int count) {
        numberOfHealthTipsPlayed = count;
    }

    @Override
    public int getPlayedHealthTipsCount() {
        return numberOfHealthTipsPlayed;
    }


}
