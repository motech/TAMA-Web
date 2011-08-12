package org.motechproject.tama.web.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.PillRegimenResponse;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRMessage;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageOnPillTakenDuringIncomingCallTest {

    IVRContext context;

    @Mock
    IVRRequest ivrRequest;

    @Mock
    private IVRSession ivrSession;

    MessageOnPillTakenDuringIncomingCall messageOnPillTakenDuringIncomingCall;

    private PillRegimenResponse pillRegimenResponse;

    @Mock
    private IVRMessage ivrMessage;

    @Before
    public void setup() {
        initMocks(this);
        messageOnPillTakenDuringIncomingCall = new MessageOnPillTakenDuringIncomingCall(ivrMessage);
        context = new IVRContext(ivrRequest, ivrSession);

        pillRegimenResponse = PillRegimenResponseBuilder.startRecording().withDefaults().build();
        when(ivrSession.getPillRegimen()).thenReturn(pillRegimenResponse);
        when(ivrRequest.hasNoTamaData()).thenReturn(true);
    }

    @Test
    public void shouldNotReturnAnyMessageIfPatientCallsBeforeDosageHourWithinDosageInterval() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).minusMinutes(2));
        assertArrayEquals(new String[0], messageOnPillTakenDuringIncomingCall.execute(context));
    }

    @Test
    public void shouldNotReturnAnyMessageIfPatientCallsAfterDosageHourWithinDosageInterval() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).plusMinutes(2));
        assertArrayEquals(new String[0], messageOnPillTakenDuringIncomingCall.execute(context));
    }


    @Test
    public void shouldNotReturnAnyMessageIfPatientCallsAfterDosageHour_OutsideDosageInterval_WithinPillWindow() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).plusMinutes((dosageInterval + 2)));
        assertArrayEquals(new String[0], messageOnPillTakenDuringIncomingCall.execute(context));
    }

    @Test
    public void shouldReturnDosageTakenEarlyMessageIfPatientCallsBeforeDosageHour_OutsideDosageInterval_WithinPillWindow() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).minusMinutes((dosageInterval+2)));
        assertArrayEquals(new String[]{IVRMessage.TOOK_DOSE_BEFORE_TIME}, messageOnPillTakenDuringIncomingCall.execute(context));
    }


    @Test
    public void shouldReturnDosageTakenLateMessageIfPatientCallsAfterDosageHour_OutsidePillWindow() {
        DosageResponse dosage = pillRegimenResponse.getDosages().get(0);
        int reminderRepeatWindowInHours = pillRegimenResponse.getReminderRepeatWindowInHours();
        Integer dosageInterval = 15;

        when(ivrMessage.get(TAMAConstants.DOSAGE_INTERVAL)).thenReturn(dosageInterval.toString());
        when(ivrSession.getCallTime()).thenReturn(DateUtil.now().withHourOfDay(dosage.getDosageHour()).withMinuteOfHour(dosage.getDosageMinute()).plusHours(reminderRepeatWindowInHours).plusHours(1));
        assertArrayEquals(new String[]{IVRMessage.TOOK_DOSE_LATE}, messageOnPillTakenDuringIncomingCall.execute(context));
    }
}
