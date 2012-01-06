package org.motechproject.tama.dailypillreminder;

import org.joda.time.DateTime;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.dailypillreminder.context.DailyPillReminderContext;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;

import java.util.List;

public class DailyPillReminderContextForTest extends DailyPillReminderContext {
    private PillRegimen pillRegimen;
    private TAMAIVRContextForTest tamaivrContext;

    public DailyPillReminderContextForTest(TAMAIVRContextForTest tamaivrContext) {
        super(tamaivrContext);
        this.tamaivrContext = tamaivrContext;
    }

    @Override
    public PillRegimen pillRegimen(PillReminderService pillReminderService) {
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
        return tamaivrContext.callStartTime();
    }

    public DailyPillReminderContextForTest callStartTime(DateTime callStartTime) {
        tamaivrContext.callStartTime(callStartTime);
        return this;
    }

    @Override
    public CallDirection callDirection() {
        return tamaivrContext.callDirection();
    }

    public DailyPillReminderContextForTest callDirection(CallDirection callDirection) {
        tamaivrContext.callDirection(callDirection);
        return this;
    }

    @Override
    public String patientId() {
        return tamaivrContext.patientId();
    }

    public DailyPillReminderContextForTest patientId(String patientId) {
        tamaivrContext.patientId(patientId);
        return this;
    }

    @Override
    public int retryInterval() {
        return tamaivrContext.retryInterval();
    }

    public DailyPillReminderContextForTest retryInterval(int retryInterval) {
        tamaivrContext.retryInterval(retryInterval);
        return this;
    }

    @Override
    public int numberOfTimesReminderSent() {
        return tamaivrContext.numberOfTimesReminderSent();
    }

    public DailyPillReminderContextForTest numberOfTimesReminderSent(int numberOfTimesReminderSent) {
        tamaivrContext.numberOfTimesReminderSent(numberOfTimesReminderSent);
        return this;
    }

    @Override
    public int totalNumberOfTimesToSendReminder() {
        return tamaivrContext.totalNumberOfTimesToSendReminder();
    }

    public DailyPillReminderContextForTest totalNumberOfTimesToSendReminder(int totalNumberOfTimesToSendReminder) {
        tamaivrContext.totalNumberOfTimesToSendReminder(totalNumberOfTimesToSendReminder);
        return this;
    }

    @Override
    public String dtmfInput() {
        return tamaivrContext.dtmfInput();
    }

    public DailyPillReminderContextForTest dtmfInput(String dtmfInput) {
        tamaivrContext.dtmfInput(dtmfInput);
        return this;
    }

    @Override
    public String preferredLanguage() {
        return tamaivrContext.preferredLanguage();
    }

    public void preferredLanguage(String preferredLanguage) {
        tamaivrContext.preferredLanguage(preferredLanguage);
    }

    public void addLastCompletedTreeToListOfCompletedTrees(String treeName) {
        tamaivrContext.addLastCompletedTreeToListOfCompletedTrees(treeName);
    }

    @Override
    public List<String> getListOfCompletedTrees() {
        return tamaivrContext.getListOfCompletedTrees();
    }
}
