package org.motechproject.tamacallflow.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tamacallflow.builder.TAMAPillRegimenBuilder;
import org.motechproject.tamacallflow.domain.*;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.tamacallflow.repository.AllDosageAdherenceLogs;
import org.motechproject.util.DateUtil;
import org.powermock.api.mockito.PowerMockito;

import java.util.Collections;
import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DailyReminderAdherenceServiceTest.RecordAdherence.class,
        DailyReminderAdherenceServiceTest.RecordAdherenceForSuspendedPeriod.class,
        DailyReminderAdherenceServiceTest.CalculateAdherence.class
})
public class DailyReminderAdherenceServiceTest {
    public static class TestSubject {
        @Mock
        protected AllDosageAdherenceLogs allDosageAdherenceLogs;

        @Mock
        protected TAMAPillReminderService pillReminderService;

        protected Properties ivrProperties;

        protected DailyReminderAdherenceService dailyReminderAdherenceService;

        protected DateTime now;

        protected LocalDate today;

        public void setUpTime() {
            now = new DateTime(2011, 11, 29, 10, 30, 0);
            PowerMockito.stub(method(DateUtil.class, "now")).toReturn(now);
            today = now.toLocalDate();
            PowerMockito.stub(method(DateUtil.class, "today")).toReturn(today);
        }

        private void initializeProperties() {
            ivrProperties = new Properties();
            ivrProperties.setProperty(TAMAConstants.DOSAGE_INTERVAL, "15");
            ivrProperties.setProperty(TAMAConstants.PILL_WINDOW, "2");
        }

        @Before
        public void setUp() {
            initMocks(this);
            setUpTime();
            initializeProperties();
            dailyReminderAdherenceService = new DailyReminderAdherenceService(allDosageAdherenceLogs, pillReminderService, ivrProperties);
        }
    }

    public static class RecordAdherence extends TestSubject {
        @Test
        public void createsNewAdherenceLog_WhenNoLogExists() {
            Dose dose = mock(Dose.class);
            DateTime doseTakenTime = DateUtil.newDateTime(new LocalDate(2011, 10, 10), 10, 0, 0);
            when(dose.getDosageId()).thenReturn("dosageId");
            when(dose.getDoseTime()).thenReturn(doseTakenTime);
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId", doseTakenTime.toLocalDate())).thenReturn(null);

            dailyReminderAdherenceService.recordAdherence("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            verify(allDosageAdherenceLogs).add(any(DosageAdherenceLog.class));
        }

        @Test
        public void updatesExistingAdherenceLog() {
            LocalDate doseDate = now.toLocalDate();
            DosageAdherenceLog existingLog = new DosageAdherenceLog("patientId", "regimenId", "dosageId", DosageStatus.NOT_TAKEN, doseDate.minusDays(1));
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId", doseDate)).thenReturn(existingLog);

            Dose dose = mock(Dose.class);
            when(dose.getDosageId()).thenReturn("dosageId");
            when(dose.getDate()).thenReturn(doseDate);
            when(dose.getDoseTime()).thenReturn(now);
            DateTime doseTakenTime = DateUtil.newDateTime(new LocalDate(2011, 10, 10), 10, 0, 0);

            dailyReminderAdherenceService.recordAdherence("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            verify(allDosageAdherenceLogs).update(existingLog);
            assertEquals(DosageStatus.TAKEN, existingLog.getDosageStatus());
        }

        @Test
        public void recordsWhenDoseIsTakenLate() {
            Time scheduledDoseTime = new Time(10, 0);
            DosageResponse morningDosage = new DosageResponse("currentDosageId", scheduledDoseTime, today, null, null, Collections.<MedicineResponse>emptyList());
            Dose dose = new Dose(morningDosage, today);
            DateTime doseTime = scheduledDoseTime.getDateTime(now);

            Integer dosageWindow = Integer.parseInt(ivrProperties.getProperty(TAMAConstants.DOSAGE_INTERVAL));
            DateTime doseTakenTime = doseTime.plusMinutes(dosageWindow + 1);

            dailyReminderAdherenceService.recordAdherence("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            ArgumentCaptor<DosageAdherenceLog> adherenceLogArgumentCaptor = ArgumentCaptor.forClass(DosageAdherenceLog.class);
            verify(allDosageAdherenceLogs).add(adherenceLogArgumentCaptor.capture());
            assertTrue(adherenceLogArgumentCaptor.getValue().isDosageTakenLate());
        }

        @Test
        public void recordsDoseIsTakenOnTime() {
            Time scheduledDoseTime = new Time(10, 0);
            DosageResponse morningDosage = new DosageResponse("currentDosageId", scheduledDoseTime, today, null, null, Collections.<MedicineResponse>emptyList());
            Dose dose = new Dose(morningDosage, today);
            DateTime doseTime = scheduledDoseTime.getDateTime(now);

            Integer dosageWindow = Integer.parseInt(ivrProperties.getProperty(TAMAConstants.DOSAGE_INTERVAL));
            DateTime doseTakenTime = doseTime.plusMinutes(dosageWindow - 1);

            dailyReminderAdherenceService.recordAdherence("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            ArgumentCaptor<DosageAdherenceLog> adherenceLogArgumentCaptor = ArgumentCaptor.forClass(DosageAdherenceLog.class);
            verify(allDosageAdherenceLogs).add(adherenceLogArgumentCaptor.capture());
            assertFalse(adherenceLogArgumentCaptor.getValue().isDosageTakenLate());
        }
    }

    public static class RecordAdherenceForSuspendedPeriod extends TestSubject {
        @Test
        public void createsAdherenceLogsForEveryDosage() {
            SuspendedAdherenceData suspendedAdherenceData = SuspendedAdherenceDataPreset.fromYesterdayWithAnyStatus();
            PillRegimen pillRegimen = TAMAPillRegimenBuilder.startRecording().withThreeDosagesInTotal().withTwoDosagesFrom(suspendedAdherenceData.suspendedFrom()).build();
            when(pillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
            dailyReminderAdherenceService.recordAdherence(suspendedAdherenceData);
            verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
        }
    }

    public static class CalculateAdherence extends TestSubject {
        @Test
        public void forPatient() {
            String patientId = "patientId";
            int dosesTaken = 7;
            int totalDoses = 28;

            PillRegimen pillRegimen = mock(PillRegimen.class);
            when(pillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);
            DateTime asOfDate = now;
            when(pillRegimen.getId()).thenReturn("regimenId");
            when(pillRegimen.getDosesIn(4, asOfDate)).thenReturn(totalDoses);

            when(allDosageAdherenceLogs.countBy(same("regimenId"), same(DosageStatus.TAKEN), Matchers.<LocalDate>any(), Matchers.<LocalDate>any())).thenReturn(dosesTaken);
            assertEquals(((double) dosesTaken / totalDoses) * 100, dailyReminderAdherenceService.getAdherenceInPercentage(patientId, asOfDate));
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
            assertEquals(1.0 / 14.0 * 100, dailyReminderAdherenceService.getAdherenceForLastWeekInPercentage(patientId, doseDateTime));
        }

        @Test
        public void shouldReturn100PercentIfDosageNotStartedYet() {
            LocalDate doseDate = new LocalDate(2011, 10, 10);
            String patientId = "patientId";
            PillRegimen pillRegimen = mock(PillRegimen.class);
            when(pillRegimen.getId()).thenReturn("regimenId");
            when(pillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);
            DateTime doseDateTime = DateUtil.newDateTime(doseDate, 10, 0, 0);
            when(pillRegimen.getDosesIn(1, doseDateTime)).thenReturn(0);
            when(allDosageAdherenceLogs.countBy("regimenId", DosageStatus.TAKEN, doseDateTime.minusWeeks(1).toLocalDate(), doseDateTime.toLocalDate())).thenReturn(1);
            assertEquals(100.0, dailyReminderAdherenceService.getAdherenceForLastWeekInPercentage(patientId, doseDateTime));
        }
    }

    public static class AnyDoseTakenLateSince extends TestSubject {
        @Test
        public void shouldBeTrueWhenAtLeastOneDoseWasTakenSinceGivenDate() {
            LocalDate someDate = DateUtil.newDate(2011, 10, 10);
            when(allDosageAdherenceLogs.getDoseTakenLateCount("patient_id", someDate, true)).thenReturn(1);

            assertTrue(dailyReminderAdherenceService.anyDoseTakenLateSince("patient_id", someDate));
        }

        @Test
        public void shouldBeFalseWhenNoDoseWasTakenLateLastWeek() {
            LocalDate someDate = DateUtil.newDate(2011, 10, 10);
            when(allDosageAdherenceLogs.getDoseTakenLateCount("patient_id", someDate, true)).thenReturn(0);

            assertFalse(dailyReminderAdherenceService.anyDoseTakenLateSince("patient_id", someDate));
        }
    }
}


