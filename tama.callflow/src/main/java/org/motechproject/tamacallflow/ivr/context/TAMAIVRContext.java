package org.motechproject.tamacallflow.ivr.context;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tamacallflow.ivr.CallState;
import org.motechproject.tamacallflow.ivr.PillRegimenSnapshot;
import org.motechproject.tamacallflow.ivr.call.PillReminderCall;
import org.motechproject.tamadomain.domain.IVRAuthenticationStatus;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.repository.AllPatients;
import org.motechproject.util.Cookies;
import org.motechproject.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class TAMAIVRContext {
    static final String CALLER_ID = "caller_id";
    static final String NUMBER_OF_ATTEMPTS = "number_of_attempts";
    private static final String CALL_STATE = "call_state";
    private static final String SYMPTOM_REPORTING_TREE = "symptom_reporting_tree";
    public static final String PATIENT_ID = "patient_id";
    public static final String PATIENT = "Patient";
    private static final String CALL_START_TIME = "call_time";
    private static final String DOSAGE_ID = PillReminderCall.DOSAGE_ID;
    private static final String PILL_REGIMEN = "PillRegimen";
    public static final String NUMBER_OF_TIMES_REMINDER_SENT = PillReminderCall.TIMES_SENT;
    private static final String TOTAL_NUMBER_OF_TIMES_TO_SEND_REMINDER = PillReminderCall.TOTAL_TIMES_TO_SEND;
    private static final String RETRY_INTERVAL = PillReminderCall.RETRY_INTERVAL;
    public static final String IS_OUTBOX_CALL = "outbox_call";
    private static final String LAST_COMPLETED_TREE = "LastCompletedTree";
    private static final String HEALTH_TIPS_PLAYED_COUNT = "healthTipsPlayedCount";
    private static final String LAST_PLAYED_HEALTH_TIP = "lastPlayedHealthTip";


    private KookooRequest kookooRequest;
    private HttpServletRequest httpRequest;
    private Cookies cookies;
    private KooKooIVRContext kooKooIVRContext;

    protected TAMAIVRContext() {
    }

    public TAMAIVRContext(KooKooIVRContext kooKooIVRContext) {
        this(kooKooIVRContext.kooKooRequest(), kooKooIVRContext.httpRequest(), kooKooIVRContext.cookies());
        this.kooKooIVRContext = kooKooIVRContext;
    }

    TAMAIVRContext(KookooRequest kookooRequest, HttpServletRequest httpRequest, Cookies cookies) {
        this.kookooRequest = kookooRequest;
        this.httpRequest = httpRequest;
        this.cookies = cookies;
    }

    public void initialize() {
        callerId(requestedCallerId());
        setInSession(NUMBER_OF_ATTEMPTS, "0");
    }

    private void setInSession(String name, Object value) {
        httpRequest.getSession().setAttribute(name, value);
    }

    public void addLastCompletedTreeToListOfCompletedTrees(String treeName) {
        kooKooIVRContext.addToListOfCompletedTrees(treeName);
    }

    public List<String> getListOfCompletedTrees() {
        return kooKooIVRContext.getListOfCompletedTrees();
    }

    public boolean hasTraversedTree(String treeName) {
        return getListOfCompletedTrees() != null && getListOfCompletedTrees().contains(treeName);
    }

    protected void callerId(String callerId) {
        setInSession(CALLER_ID, callerId);
    }

    public String dtmfInput() {
        return kookooRequest.getInput();
    }

    public String callerId() {
        return fromSession(CALLER_ID);
    }

    private String fromSession(String name) {
        return (String) httpRequest.getSession().getAttribute(name);
    }

    public int numberOfLoginAttempts() {
        String numberOfAttempts = fromSession(NUMBER_OF_ATTEMPTS);
        return Integer.parseInt(numberOfAttempts);
    }

    public void userAuthenticated(IVRAuthenticationStatus authenticationStatus) {
        callState(CallState.AUTHENTICATED);
        setInSession(PATIENT_ID, authenticationStatus.patientId());
        setInSession(KooKooIVRContext.EXTERNAL_ID, authenticationStatus.patientId());
        setInSession(CALL_START_TIME, DateUtil.now());
        kooKooIVRContext.preferredLanguage(authenticationStatus.language());
    }

    public CallState callState() {
        String value = fromSession(CALL_STATE);
        return (value == null) ? CallState.STARTED : Enum.valueOf(CallState.class, value);
    }

    public void numberOfLoginAttempts(int numberOfAttempts) {
        setInSession(NUMBER_OF_ATTEMPTS, Integer.toString(numberOfAttempts));
    }

    public String callId() {
        return kooKooIVRContext.callId();
    }

    public CallDirection callDirection() {
        return kookooRequest.getCallDirection();
    }

    public boolean isIncomingCall() {
        return callDirection().equals(CallDirection.Inbound);
    }

    public boolean isOutgoingCall() {
        return callDirection().equals(CallDirection.Outbound);
    }

    public DateTime callStartTime() {
        return (DateTime) httpRequest.getSession().getAttribute(CALL_START_TIME);
    }

    public String dosageId() {
        return kookooRequest.getParameter(DOSAGE_ID);
    }

    public String patientId() {
        return fromSession(PATIENT_ID);
    }

    public PillRegimenResponse pillRegimen(PillReminderService pillReminderService) {
        HttpSession session = httpRequest.getSession();
        PillRegimenResponse pillRegimen = (PillRegimenResponse) session.getAttribute(PILL_REGIMEN);
        if (pillRegimen == null) {
            pillRegimen = pillReminderService.getPillRegimen(patientId());
            session.setAttribute(PILL_REGIMEN, pillRegimen);
        }
        return pillRegimen;
    }

    public Patient patient(AllPatients allPatients) {
        Patient patient = (Patient) httpRequest.getAttribute(PATIENT);
        if (patient == null) {
            patient = allPatients.get(patientId());
            httpRequest.setAttribute(PATIENT, patient);
        }
        return patient;
    }

    public boolean isOutBoxCall() {
        return "true".equals(kookooRequest.getParameter(IS_OUTBOX_CALL));
    }

    public int numberOfTimesReminderSent() {
        return Integer.parseInt(kookooRequest.getParameter(NUMBER_OF_TIMES_REMINDER_SENT));
    }

    public int totalNumberOfTimesToSendReminder() {
        return Integer.parseInt(kookooRequest.getParameter(TOTAL_NUMBER_OF_TIMES_TO_SEND_REMINDER));
    }

    public int retryInterval() {
        return Integer.parseInt(kookooRequest.getParameter(RETRY_INTERVAL));
    }

    public void callState(CallState callState) {
        setInSession(CALL_STATE, callState.toString());
    }

    public void lastCompletedTree(String treeName) {
        cookies.add(LAST_COMPLETED_TREE, treeName);
        addLastCompletedTreeToListOfCompletedTrees(treeName);
    }

    public String lastCompletedTree() {
        return cookies.getValue(LAST_COMPLETED_TREE);
    }

    public PillRegimenSnapshot pillRegimenSnapshot(PillReminderService pillReminderService) {
        return new PillRegimenSnapshot(this, pillRegimen(pillReminderService));
    }

    public String requestedCallerId() {
        return kookooRequest.getCid();
    }

    public boolean hasOutboxCompleted() {
        OutboxContext outboxContext = new OutboxContext(kooKooIVRContext);
        return outboxContext.hasOutboxCompleted();
    }

    public String preferredLanguage() {
        return kooKooIVRContext.preferredLanguage();
    }

    public void currentDecisionTreePath(String path) {
        kooKooIVRContext.currentDecisionTreePath(path);
    }

    public void symptomReportingTree(String symptomReportingTree) {
        setInSession(SYMPTOM_REPORTING_TREE, symptomReportingTree);
    }

    public String symptomReportingTree() {
        return fromSession(SYMPTOM_REPORTING_TREE);
    }

    public void resetForMenuRepeat() {
        this.currentDecisionTreePath("");
        this.lastCompletedTree(null);
        setInSession(PILL_REGIMEN, null);
        this.callState(CallState.AUTHENTICATED);
    }

    public void setPlayedHealthTipsCount(int count) {
        this.cookies.add(HEALTH_TIPS_PLAYED_COUNT, String.valueOf(count));
    }

    public int getPlayedHealthTipsCount() {
        String value = this.cookies.getValue(HEALTH_TIPS_PLAYED_COUNT);
        return value == null ? 0 : Integer.valueOf(value);
    }

    public void setLastPlayedHealthTip(String message) {
        this.cookies.add(LAST_PLAYED_HEALTH_TIP, message);
    }

    public String getLastPlayedHealthTip() {
        return this.cookies.getValue(LAST_PLAYED_HEALTH_TIP);
    }
}
