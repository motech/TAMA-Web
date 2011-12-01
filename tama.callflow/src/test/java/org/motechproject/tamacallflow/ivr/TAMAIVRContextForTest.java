package org.motechproject.tamacallflow.ivr;

import org.joda.time.DateTime;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamadomain.domain.IVRAuthenticationStatus;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.tamacallflow.ivr.context.TAMAIVRContext;

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
    private String dosageId;
    private String patientId;
    private Patient patient;
    private String lastCompletedTree;
    private PillRegimenResponse pillRegimenResponse;
    private PillRegimenSnapshot pillRegimenSnapshot;
    private int numberOfTimesReminderSent;
    private int totalNumberOfTimesToSendReminder;
    private String requestedCallerId;
    private boolean outboxCompleted;
    private String preferredLanguage;
    private String isOutboundCall;
    private String symptomReportingTree;
    private int retryInterval;
    private List<String> completedTrees = new ArrayList<String>();

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
    public String dosageId() {
        return dosageId;
    }

    public TAMAIVRContextForTest dosageId(String dosageId) {
        this.dosageId = dosageId;
        return this;
    }

    @Override
    public String patientId() {
        return patientId;
    }

    public TAMAIVRContextForTest patientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    @Override
    public PillRegimenResponse pillRegimen(PillReminderService pillReminderService) {
        return pillRegimenResponse;
    }

    public TAMAIVRContextForTest pillRegimen(PillRegimenResponse pillRegimenResponse) {
        this.pillRegimenResponse = pillRegimenResponse;
        return this;
    }

    @Override
    public int numberOfTimesReminderSent() {
        return numberOfTimesReminderSent;
    }

    public TAMAIVRContextForTest numberOfTimesReminderSent(int numberOfTimesRemindersSent) {
        this.numberOfTimesReminderSent = numberOfTimesRemindersSent;
        return this;
    }

    @Override
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
    public Patient patient(AllPatients allPatients) {
        return patient;
    }

    public TAMAIVRContextForTest patient(Patient patient) {
        this.patient = patient;
        return this;
    }

    @Override
    public void lastCompletedTree(String treeName) {
        this.lastCompletedTree = treeName;
    }

    @Override
    public String lastCompletedTree() {
        return lastCompletedTree;
    }

    public TAMAIVRContextForTest pillRegimenSnapshot(PillRegimenSnapshot pillRegimenSnapshot) {
        this.pillRegimenSnapshot = pillRegimenSnapshot;
        return this;
    }

    @Override
    public PillRegimenSnapshot pillRegimenSnapshot(PillReminderService pillReminderService) {
        return pillRegimenSnapshot;
    }

    @Override
    public String requestedCallerId() {
        return requestedCallerId;
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
    public boolean hasOutboxCompleted() {
        return outboxCompleted;
    }

    public TAMAIVRContextForTest outboxCompleted(boolean outboxCompleted) {
        this.outboxCompleted = outboxCompleted;
        return this;
    }

    @Override
    public String preferredLanguage() {
        return preferredLanguage;
    }

    public TAMAIVRContextForTest preferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
        return this;
    }

    @Override
    public String symptomReportingTree() {
        return symptomReportingTree;
    }

    @Override
    public void symptomReportingTree(String symptomReportingTree) {
        this.symptomReportingTree  = symptomReportingTree;
    }

    public TAMAIVRContextForTest retryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
        return this;
    }

    @Override
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
}
