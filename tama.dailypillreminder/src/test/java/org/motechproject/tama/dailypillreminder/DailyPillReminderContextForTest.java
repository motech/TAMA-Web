package org.motechproject.tama.dailypillreminder;

import org.joda.time.DateTime;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;

import java.util.List;

public class DailyPillReminderContextForTest extends DailyPillReminderContext {
    private PillRegimen pillRegimen;
    private TAMAIVRContextForTest tamaIVRContext;

    public DailyPillReminderContextForTest(TAMAIVRContextForTest tamaIVRContext) {
        super(tamaIVRContext, null);
        this.tamaIVRContext = tamaIVRContext;
    }

    @Override
    public PillRegimen pillRegimen() {
        return pillRegimen;
    }

    public DailyPillReminderContextForTest pillRegimen(PillRegimenResponse pillRegimenResponse) {
        return pillRegimen(new PillRegimen(pillRegimenResponse));
    }

    public DailyPillReminderContextForTest pillRegimen(PillRegimen pillRegimen) {
        this.pillRegimen = pillRegimen;
        return this;
    }

    @Override
    public DateTime callStartTime() {
        return tamaIVRContext.callStartTime();
    }

    public DailyPillReminderContextForTest callStartTime(DateTime callStartTime) {
        tamaIVRContext.callStartTime(callStartTime);
        return this;
    }

    @Override
    public CallDirection callDirection() {
        return tamaIVRContext.callDirection();
    }

    public DailyPillReminderContextForTest callDirection(CallDirection callDirection) {
        tamaIVRContext.callDirection(callDirection);
        return this;
    }

    @Override
    public String patientDocumentId() {
        return tamaIVRContext.patientDocumentId();
    }

    public DailyPillReminderContextForTest patientDocumentId(String patientId) {
        tamaIVRContext.patientDocumentId(patientId);
        return this;
    }

    @Override
    public int retryInterval() {
        return tamaIVRContext.retryInterval();
    }

    public DailyPillReminderContextForTest retryInterval(int retryInterval) {
        tamaIVRContext.retryInterval(retryInterval);
        return this;
    }

    @Override
    public int numberOfTimesReminderSent() {
        return tamaIVRContext.numberOfTimesReminderSent();
    }

    public DailyPillReminderContextForTest numberOfTimesReminderSent(int numberOfTimesReminderSent) {
        tamaIVRContext.numberOfTimesReminderSent(numberOfTimesReminderSent);
        return this;
    }

    @Override
    public int totalNumberOfTimesToSendReminder() {
        return tamaIVRContext.totalNumberOfTimesToSendReminder();
    }

    public DailyPillReminderContextForTest totalNumberOfTimesToSendReminder(int totalNumberOfTimesToSendReminder) {
        tamaIVRContext.totalNumberOfTimesToSendReminder(totalNumberOfTimesToSendReminder);
        return this;
    }

    @Override
    public String dtmfInput() {
        return tamaIVRContext.dtmfInput();
    }

    public DailyPillReminderContextForTest dtmfInput(String dtmfInput) {
        tamaIVRContext.dtmfInput(dtmfInput);
        return this;
    }

    @Override
    public String preferredLanguage() {
        return tamaIVRContext.preferredLanguage();
    }

    public void preferredLanguage(String preferredLanguage) {
        tamaIVRContext.preferredLanguage(preferredLanguage);
    }

    public void addLastCompletedTreeToListOfCompletedTrees(String treeName) {
        tamaIVRContext.addLastCompletedTreeToListOfCompletedTrees(treeName);
    }

    @Override
    public List<String> getListOfCompletedTrees() {
        return tamaIVRContext.getListOfCompletedTrees();
    }
}
