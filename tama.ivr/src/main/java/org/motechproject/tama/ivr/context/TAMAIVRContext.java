package org.motechproject.tama.ivr.context;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.ivr.kookoo.KooKooIVRContext;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.tama.ivr.domain.CallState;
import org.motechproject.tama.ivr.domain.IVRAuthenticationStatus;
import org.motechproject.tama.ivr.domain.TAMAMessageTypes;
import org.motechproject.util.Cookies;
import org.motechproject.util.DateUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

import static java.lang.Boolean.valueOf;
import static org.apache.commons.lang.StringUtils.isNotBlank;

public class TAMAIVRContext {

    public static final String PATIENT_ID = "patient_id";
    public static final String IS_OUTBOX_CALL = "outbox_call";
    public static final String LAST_PLAYED_HEALTH_TIP = "lastPlayedHealthTip";
    public static final String SWITCH_TO_DIAL_STATE = "switch_to_dial_state";
    public static final String DO_NOT_PROMPT_FOR_HANG_UP = "do_not_prompt_for_hang_up";
    public static final String MESSAGE_CATEGORY_NAME = "message_category_name";
    public static final String TAMA_MESSAGE_TYPE = "tama_message_type";
    public static final String LAST_PLAYED_VOICE_MESSAGE_ID = "LastPlayedVoiceMessageID";
    protected static final String PILL_REGIMEN = "PillRegimen";
    static final String CALLER_ID = "caller_id";
    static final String NUMBER_OF_ATTEMPTS = "number_of_attempts";
    private static final String SYMPTOM_REPORTING_TREE = "symptom_reporting_tree";
    private static final String CALL_START_TIME = "call_time";
    private static final String LAST_COMPLETED_TREE = "LastCompletedTree";
    private static final String HEALTH_TIPS_PLAYED_COUNT = "healthTipsPlayedCount";
    private static final String MESSAGES_PUSHED = "messages_pushed";

    protected KookooRequest kookooRequest;
    protected HttpServletRequest httpRequest;
    private Cookies cookies;
    private KooKooIVRContext kooKooIVRContext;

    protected TAMAIVRContext() {
    }

    public TAMAIVRContext(KooKooIVRContext kooKooIVRContext) {
        this(kooKooIVRContext.kooKooRequest(), kooKooIVRContext.httpRequest(), kooKooIVRContext.cookies());
        this.kooKooIVRContext = kooKooIVRContext;
    }

    public TAMAIVRContext(TAMAIVRContext tamaivrContext) {
        this(tamaivrContext.kookooRequest, tamaivrContext.httpRequest, tamaivrContext.cookies);
        this.kooKooIVRContext = tamaivrContext.kooKooIVRContext;
    }

    TAMAIVRContext(KookooRequest kookooRequest, HttpServletRequest httpRequest, Cookies cookies) {
        this.kookooRequest = kookooRequest;
        this.httpRequest = httpRequest;
        this.cookies = cookies;
    }

    public KooKooIVRContext getKooKooIVRContext() {
        return kooKooIVRContext;
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

    public boolean hasTraversedAnyTree() {
        return CollectionUtils.isNotEmpty(getListOfCompletedTrees());
    }

    protected void callerId(String callerId) {
        setInSession(CALLER_ID, callerId);
    }

    public String dtmfInput() {
        try {
            return kookooRequest.getInput();
        } catch (Exception e) {
            return "";
        }
    }

    public String callerId() {
        return fromSession(CALLER_ID);
    }

    private String fromSession(String name) {
        return (String) httpRequest.getSession().getAttribute(name);
    }

    public int numberOfLoginAttempts() {
        String numberOfAttempts = fromSession(NUMBER_OF_ATTEMPTS);
        return Integer.parseInt(StringUtils.isEmpty(numberOfAttempts) ? "0" : numberOfAttempts);
    }

    public void userAuthenticated(IVRAuthenticationStatus authenticationStatus) {
        callState(CallState.AUTHENTICATED);
        setInSession(PATIENT_ID, authenticationStatus.patientId());
        setInSession(KooKooIVRContext.EXTERNAL_ID, authenticationStatus.patientId());
        setInSession(CALL_START_TIME, DateUtil.now());
        kooKooIVRContext.preferredLanguage(authenticationStatus.language());
    }

    public CallState callState() {
        String value = fromSession(CallEventConstants.CALL_STATE);
        return (value == null) ? CallState.STARTED : Enum.valueOf(CallState.class, value);
    }

    public void numberOfLoginAttempts(int numberOfAttempts) {
        setInSession(NUMBER_OF_ATTEMPTS, Integer.toString(numberOfAttempts));
    }

    public String callId() {
        return kooKooIVRContext.callId();
    }

    public String callDetailRecordId() {
        return kooKooIVRContext.callDetailRecordId();
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

    public String patientDocumentId() {
        return fromSession(PATIENT_ID);
    }

    public boolean isOutBoxCall() {
        return "true".equals(kookooRequest.getParameter(IS_OUTBOX_CALL));
    }

    public void callState(CallState callState) {
        setInSession(CallEventConstants.CALL_STATE, callState.toString());
        log(CallEventConstants.CALL_STATE, callState.toString());
    }

    private void log(String key, String value) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(key, value);
        setDataToLog(map);
    }

    public void lastCompletedTree(String treeName) {
        cookies.add(LAST_COMPLETED_TREE, treeName);
        addLastCompletedTreeToListOfCompletedTrees(treeName);
    }

    public String lastCompletedTree() {
        return cookies.getValue(LAST_COMPLETED_TREE);
    }

    public String requestedCallerId() {
        return kookooRequest.getCid();
    }

    public boolean isDialState() {
        return valueOf(cookies.getValue(SWITCH_TO_DIAL_STATE));
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

    public int getPlayedHealthTipsCount() {
        String value = this.cookies.getValue(HEALTH_TIPS_PLAYED_COUNT);
        return value == null ? 0 : Integer.valueOf(value);
    }

    public void setPlayedHealthTipsCount(int count) {
        this.cookies.add(HEALTH_TIPS_PLAYED_COUNT, String.valueOf(count));
    }

    public String getLastPlayedHealthTip() {
        return this.cookies.getValue(LAST_PLAYED_HEALTH_TIP);
    }

    public void setLastPlayedHealthTip(String message) {
        this.cookies.add(LAST_PLAYED_HEALTH_TIP, message);
    }

    public void setDataToLog(HashMap<String, String> map) {
        kooKooIVRContext.dataToLog(map);
    }

    public boolean doNotPromptForHangUp() {
        String value = this.cookies.getValue(DO_NOT_PROMPT_FOR_HANG_UP);
        return (isNotBlank(value)) ? valueOf(value) : false;
    }

    public void doNoPromptForHangUp(boolean value) {
        this.cookies.add(DO_NOT_PROMPT_FOR_HANG_UP, String.valueOf(value));
    }

    public boolean isAnswered() {
        return kooKooIVRContext.isAnswered();
    }

    protected Cookies cookies() {
        return cookies;
    }

    public boolean isMessagesPushed() {
        String pushed = this.cookies.getValue(MESSAGES_PUSHED);
        return (StringUtils.isNotBlank(pushed)) ? Boolean.valueOf(pushed) : false;
    }

    public void setMessagesPushed(boolean value) {
        this.cookies.add(MESSAGES_PUSHED, String.valueOf(value));
    }

    public TAMAMessageTypes getMessagesCategory() {
        String name = this.cookies.getValue(MESSAGE_CATEGORY_NAME);
        return (isNotBlank(name)) ? TAMAMessageTypes.valueOf(name) : null;
    }

    public void setMessagesCategory(String categoryName) {
        this.cookies.add(MESSAGE_CATEGORY_NAME, categoryName);
        log(MESSAGE_CATEGORY_NAME, categoryName);
    }

    public String getTAMAMessageType() {
        String name = this.cookies.getValue(TAMA_MESSAGE_TYPE);
        return (isNotBlank(name)) ? name : "";
    }

    public void setTAMAMessageType(String typeName) {
        this.cookies.add(TAMA_MESSAGE_TYPE, typeName);
        log(TAMA_MESSAGE_TYPE, typeName);
    }

    public String lastPlayedMessageId() {
        return cookies.getValue(LAST_PLAYED_VOICE_MESSAGE_ID);
    }

    public void lastPlayedMessageId(String messageId) {
        cookies.add(LAST_PLAYED_VOICE_MESSAGE_ID, messageId);
    }
}
