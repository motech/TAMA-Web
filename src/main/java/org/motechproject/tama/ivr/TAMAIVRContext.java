package org.motechproject.tama.ivr;

import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.tama.domain.IVRAuthenticationStatus;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.util.Cookies;
import org.motechproject.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class TAMAIVRContext {
    public static final String CALLER_ID = "caller_id";
    public static final String NUMBER_OF_ATTEMPTS = "number_of_attempts";
    private static final String CALL_STATE = "call_state";
    public static final String PATIENT_ID = "patient_id";
    public static final String PATIENT = "Patient";
    private static final String CALL_START_TIME = "call_time";
    private static final String DOSAGE_ID = PillReminderCall.DOSAGE_ID;
    private static final String PILL_REGIMEN = "PillRegimen";
    public static final String NUMBER_OF_TIMES_REMINDER_SENT = PillReminderCall.TIMES_SENT;
    private static final String TOTAL_NUMBER_OF_TIMES_TO_SEND_REMINDER = PillReminderCall.TOTAL_TIMES_TO_SEND;
    private static final String LAST_COMPLETED_TREE = "LastCompletedTree";

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

    void initialize() {
        callerId(requestedCallerId());
        cookies.add(NUMBER_OF_ATTEMPTS, "0");
        cookies.add(NUMBER_OF_TIMES_REMINDER_SENT, "0");
    }

    protected void callerId(String callerId) {
        cookies.add(CALLER_ID, callerId);
    }

    public String dtmfInput() {
        return kookooRequest.getInput();
    }

    public String callerId() {
        return cookies.getValue(CALLER_ID);
    }

    public int numberOfLoginAttempts() {
        String numberOfAttempts = cookies.getValue(NUMBER_OF_ATTEMPTS);
        return Integer.parseInt(numberOfAttempts);
    }

    public void userAuthenticated(IVRAuthenticationStatus authenticationStatus) {
        callState(CallState.AUTHENTICATED);
        cookies.add(PATIENT_ID, authenticationStatus.patientId());
        cookies.add(KooKooIVRContext.EXTERNAL_ID, authenticationStatus.patientId());
        httpRequest.getSession().setAttribute(CALL_START_TIME, DateUtil.now());
    }

    public CallState callState() {
        String value = cookies.getValue(CALL_STATE);
        return (value == null) ? CallState.STARTED : Enum.valueOf(CallState.class, value);
    }

    public void numberOfLoginAttempts(int numberOfAttempts) {
        cookies.add(NUMBER_OF_ATTEMPTS, Integer.toString(numberOfAttempts));
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

    public DateTime callStartTime() {
        return (DateTime) httpRequest.getSession().getAttribute(CALL_START_TIME);
    }

    public String dosageId() {
        return kookooRequest.getParameter(DOSAGE_ID);
    }

    public String patientId() {
        return cookies.getValue(PATIENT_ID);
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

    public int numberOfTimesReminderSent() {
        return Integer.parseInt(httpRequest.getParameter(NUMBER_OF_TIMES_REMINDER_SENT));
    }

    public int totalNumberOfTimesToSendReminder() {
        return Integer.parseInt(httpRequest.getParameter(TOTAL_NUMBER_OF_TIMES_TO_SEND_REMINDER));
    }

    public void callState(CallState callState) {
        cookies.add(CALL_STATE, callState.toString());
    }

    public void lastCompletedTree(String treeName) {
        cookies.add(LAST_COMPLETED_TREE, treeName);
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
}
