package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tamacallflow.builder.TAMAPillRegimenBuilder;
import org.motechproject.tamacallflow.domain.Dosage;
import org.motechproject.tamacallflow.domain.PillRegimen;
import org.motechproject.tamadomain.domain.DosageAdherenceLog;
import org.motechproject.tamadomain.domain.DosageStatus;
import org.motechproject.tamadomain.domain.SuspendedAdherenceData;
import org.motechproject.tamadomain.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

public class DailyReminderAdherenceServiceTest {

    @Mock
    private AllDosageAdherenceLogs allDosageAdherenceLogs;
    @Mock
    private TAMAPillReminderService pillReminderService;
    @Mock
    private Properties ivrProperties;

    private DailyReminderAdherenceService dailyReminderAdherenceService;

    @Before
    public void setUp() {
        initMocks(this);
        dailyReminderAdherenceService = new DailyReminderAdherenceService(allDosageAdherenceLogs, pillReminderService, ivrProperties);
        setUpTime();
    }

    private void setUpTime() {
        DateTime now = new DateTime(2011, 11, 29, 10, 30, 0);
        PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
        final LocalDate today = now.toLocalDate();
        PowerMockito.stub(method(DateUtil.class, "today")).toReturn(today);
    }

    @Test
    public void shouldCreateAdherenceLogsForEveryDosageWhenRecordingAdherence() {
        SuspendedAdherenceData suspendedAdherenceData = SuspendedAdherenceDataPreset.fromYesterdayWithAnyStatus();
        PillRegimen pillRegimen = TAMAPillRegimenBuilder.startRecording().withThreeDosagesInTotal().withTwoDosagesFrom(suspendedAdherenceData.suspendedFrom()).build();
        when(ivrProperties.getProperty(any(String.class))).thenReturn("2");
        when(pillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
        dailyReminderAdherenceService.recordAdherence(suspendedAdherenceData);
        verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
    }

    @Test
    public void shouldCalculateTheAdherenceForPatient() {
        String patientId = "patientId";
        int dosesTaken = 7;
        int totalDoses = 28;

        PillRegimen pillRegimen = mock(PillRegimen.class);
        when(pillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);
        DateTime asOfDate = DateUtil.now();
        when(pillRegimen.getId()).thenReturn("regimenId");
        when(pillRegimen.getDosesIn(4, asOfDate)).thenReturn(totalDoses);

        when(allDosageAdherenceLogs.countBy(same("regimenId"), same(DosageStatus.TAKEN), Matchers.<LocalDate>any(), Matchers.<LocalDate>any())).thenReturn(dosesTaken);
        assertEquals(((double) dosesTaken / totalDoses), dailyReminderAdherenceService.getAdherence(patientId, asOfDate));
    }

    @Test
    public void shouldCalculateAdherenceAsOfLastTakenDosage() {
        LocalDate doseDate = new LocalDate(2011, 10, 10);
        String patientId = "patientId";
        DosageAdherenceLog dosageAdherenceLog = new DosageAdherenceLog(patientId, "regimenId", "dosageId", DosageStatus.TAKEN, doseDate);
        when(allDosageAdherenceLogs.getLatestLogForPatient(patientId)).thenReturn(dosageAdherenceLog);

        PillRegimen pillRegimen = mock(PillRegimen.class);
        when(pillRegimen.getId()).thenReturn("regimenId");
        when(pillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);

        when(pillRegimen.getDosage("dosageId")).thenReturn(new Dosage(new DosageResponse("dosageId", new Time(10, 0), null, null, doseDate, null)));
        DateTime doseTime = DateUtil.newDateTime(doseDate, 10, 0, 0);
        when(pillRegimen.getDosesIn(4, doseTime)).thenReturn(4);
        when(allDosageAdherenceLogs.countBy("regimenId", DosageStatus.TAKEN, doseTime.minusWeeks(4).toLocalDate(), doseTime.toLocalDate())).thenReturn(1);

        assertEquals(0.25, dailyReminderAdherenceService.getAdherenceAsOfLastRecordedDose(patientId));
    }

    @Test
    public void shouldReturnAdherenceForLastWeek() {
        LocalDate doseDate = new LocalDate(2011, 10, 10);
        String patientId = "patientId";
        PillRegimen pillRegimen = mock(PillRegimen.class);
        when(pillRegimen.getId()).thenReturn("regimenId");
        when(pillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);
        when(pillRegimen.getDosage("dosageId")).thenReturn(new Dosage(new DosageResponse("dosageId", new Time(10, 0), null, null, doseDate, null)));
        DateTime doseDateTime = DateUtil.newDateTime(doseDate, 10, 0, 0);
        when(pillRegimen.getDosesIn(1, doseDateTime)).thenReturn(14);
        when(allDosageAdherenceLogs.countBy("regimenId", DosageStatus.TAKEN, doseDateTime.minusWeeks(1).toLocalDate(), doseDateTime.toLocalDate())).thenReturn(1);
        assertEquals(1.0 / 14.0, dailyReminderAdherenceService.getAdherenceForLastWeek(patientId, doseDateTime));
    }
}


