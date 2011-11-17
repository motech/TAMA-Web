package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.TAMAIVRContextForTest;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageOnPillTakenDuringIncomingCallTest {
    MessageOnPillTakenDuringIncomingCall messageOnPillTakenDuringIncomingCall;
    private PillRegimenResponse pillRegimenResponse;

    @Mock
    private TamaIVRMessage ivrMessage;
    private TAMAIVRContextForTest context;

    @Before
    public void setup() {
        initMocks(this);
        messageOnPillTakenDuringIncomingCall = new MessageOnPillTakenDuringIncomingCall(ivrMessage, null);
        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        context = new TAMAIVRContextForTest().pillRegimen(pillRegimenResponse).callDirection(CallDirection.Inbound);
    }

    @Test
    public void shouldReturnDoseTakenMessageIfPatientCallsBeforeDosageHourWithinDosageInterval() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        context.callStartTime(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).minusMinutes(2));
        assertArrayEquals(new String[]{TamaIVRMessage.DOSE_TAKEN_ON_TIME, TamaIVRMessage.DOSE_RECORDED}, messageOnPillTakenDuringIncomingCall.executeCommand(context));
    }

    @Test
    public void shouldReturnDoseTakenMessageIfPatientCallsAfterDosageHourWithinDosageInterval() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        context.callStartTime(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).plusMinutes(2));
        assertArrayEquals(new String[]{TamaIVRMessage.DOSE_TAKEN_ON_TIME, TamaIVRMessage.DOSE_RECORDED}, messageOnPillTakenDuringIncomingCall.executeCommand(context));
    }

    @Test
    public void shouldOnlyReturnDoseRecordedMessageIfPatientCallsAfterDosageHour_OutsideDosageInterval_WithinPillWindow() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        context.callStartTime(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).plusMinutes((dosageInterval + 2)));
        assertArrayEquals(new String[]{TamaIVRMessage.DOSE_RECORDED}, messageOnPillTakenDuringIncomingCall.executeCommand(context));
    }

    @Test
    public void shouldReturnDosageTakenEarlyMessageIfPatientCallsBeforeDosageHour_OutsideDosageInterval_WithinPillWindow() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        context.callStartTime(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).minusMinutes((dosageInterval + 2)));
        assertArrayEquals(new String[]{TamaIVRMessage.TOOK_DOSE_BEFORE_TIME, TamaIVRMessage.DOSE_RECORDED}, messageOnPillTakenDuringIncomingCall.executeCommand(context));
    }

    @Test
    public void shouldReturnDosageTakenLateMessageIfPatientCallsAfterDosageHour_OutsidePillWindow() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        int reminderRepeatWindowInHours = pillRegimenResponse.getReminderRepeatWindowInHours();
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        context.callStartTime(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).plusHours(reminderRepeatWindowInHours).plusHours(1));
        assertArrayEquals(new String[]{TamaIVRMessage.TOOK_DOSE_LATE, TamaIVRMessage.DOSE_RECORDED}, messageOnPillTakenDuringIncomingCall.executeCommand(context));
    }
}
