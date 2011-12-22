package org.motechproject.tama.dailypillreminder.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.builder.TAMAPillRegimenBuilder;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DailyPillReminderAdherenceServiceTest.RecordDosageAdherenceAsCaptured.class,
        DailyPillReminderAdherenceServiceTest.RecordDosageAdherenceAsNotCaptured.class,
        DailyPillReminderAdherenceServiceTest.BackFillAdherenceForPeriodOfSuspension.class,
        DailyPillReminderAdherenceServiceTest.CalculateAdherence.class,
        DailyPillReminderAdherenceServiceTest.WasAnyDoseMissedLastWeek.class,
        DailyPillReminderAdherenceServiceTest.WasAnyDoseTakenLateSince.class
})
public class DailyPillReminderAdherenceServiceTest {

    public static class TestSubject {
        @Mock
        protected AllDosageAdherenceLogs allDosageAdherenceLogs;
        @Mock
        protected AllPatients allPatients;

        @Mock
        protected TAMAPillReminderService pillReminderService;

        protected Properties ivrProperties;

        protected DailyPillReminderAdherenceService dailyReminderAdherenceService;

        protected DateTime now;

        protected LocalDate today;

        public void setUpTime() {
            now = new DateTime(2011, 11, 29, 10, 30, 0);
            today = now.toLocalDate();
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
            dailyReminderAdherenceService = new DailyPillReminderAdherenceService(allPatients, allDosageAdherenceLogs, pillReminderService, ivrProperties, new AdherenceService());
        }
    }

    public static class BackFillAdherenceForPeriodOfSuspension extends TestSubject {
        @Test
        public void createsAdherenceLogsForEveryDosage() {
            when(allPatients.get("patientId")).thenReturn(PatientBuilder.startRecording().withLastSuspendedDate(DateUtil.now()).build());
            PillRegimen pillRegimen = TAMAPillRegimenBuilder.startRecording().withTwoDosages().build();
            when(pillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
            dailyReminderAdherenceService.backFillAdherenceForPeriodOfSuspension("patientId", false);
            verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
        }
    }

    public static class RecordDosageAdherenceAsCaptured extends TestSubject {

        @Test
        public void whenDoseIsTaken(){
            Dose dose = mock(Dose.class);
            DateTime doseTakenTime = DateUtil.newDateTime(new LocalDate(2011, 10, 10), 10, 0, 0);
            when(dose.getDosageId()).thenReturn("dosageId");
            when(dose.getDoseTime()).thenReturn(doseTakenTime);
            when(dose.getDate()).thenReturn(doseTakenTime.toLocalDate());
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId", doseTakenTime.toLocalDate())).thenReturn(null);

            dailyReminderAdherenceService.recordDosageAdherenceAsCaptured("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            ArgumentCaptor<DosageAdherenceLog> adherenceLogArgumentCaptor = ArgumentCaptor.forClass(DosageAdherenceLog.class);
            verify(allDosageAdherenceLogs).add(adherenceLogArgumentCaptor.capture());
            assertFalse(adherenceLogArgumentCaptor.getValue().isDosageTakenLate());
            verify(pillReminderService).setLastCapturedDate("regimenId", "dosageId", doseTakenTime.toLocalDate());
        }

        @Test
        public void whenDoseIsTaken_AfterFirstCall_ButWithinDosageInterval() {
            DateTime doseDate = DateUtil.newDateTime(new LocalDate(2011, 10, 10), 10, 0, 0);
            DosageAdherenceLog existingLog = new DosageAdherenceLog("patientId", "regimenId", "dosageId", DosageStatus.NOT_TAKEN, doseDate.toLocalDate());
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId", doseDate.toLocalDate())).thenReturn(existingLog);

            Dose dose = mock(Dose.class);
            when(dose.getDosageId()).thenReturn("dosageId");
            when(dose.getDate()).thenReturn(doseDate.toLocalDate());
            when(dose.getDoseTime()).thenReturn(doseDate);
            DateTime doseTakenTime = now;

            dailyReminderAdherenceService.recordDosageAdherenceAsCaptured("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            verify(allDosageAdherenceLogs).update(existingLog);
            assertEquals(DosageStatus.TAKEN, existingLog.getDosageStatus());
            verify(pillReminderService).setLastCapturedDate("regimenId", "dosageId", doseDate.toLocalDate());
        }

        @Test
        public void whenDoseIsTakenLate() {
            DateTime doseDate = DateUtil.newDateTime(new LocalDate(2011, 10, 10), 10, 0, 0);

            Dose dose = mock(Dose.class);
            when(dose.getDosageId()).thenReturn("dosageId");
            when(dose.getDate()).thenReturn(doseDate.toLocalDate());
            when(dose.getDoseTime()).thenReturn(doseDate);
            DateTime doseTakenTime = doseDate.plusHours(1);

            dailyReminderAdherenceService.recordDosageAdherenceAsCaptured("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            ArgumentCaptor<DosageAdherenceLog> adherenceLogArgumentCaptor = ArgumentCaptor.forClass(DosageAdherenceLog.class);
            verify(allDosageAdherenceLogs).add(adherenceLogArgumentCaptor.capture());
            assertTrue(adherenceLogArgumentCaptor.getValue().isDosageTakenLate());
            verify(pillReminderService).setLastCapturedDate("regimenId", "dosageId", doseDate.toLocalDate());
        }
    }

    public static class RecordDosageAdherenceAsNotCaptured extends TestSubject {
        @Test
        public void whenDoseIsReportedAsWillTakeLater(){
            Dose dose = mock(Dose.class);
            DateTime doseTakenTime = DateUtil.newDateTime(new LocalDate(2011, 10, 10), 10, 0, 0);
            when(dose.getDosageId()).thenReturn("dosageId");
            when(dose.getDoseTime()).thenReturn(doseTakenTime);
            when(dose.getDate()).thenReturn(doseTakenTime.toLocalDate());
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId", doseTakenTime.toLocalDate())).thenReturn(null);

            dailyReminderAdherenceService.recordDosageAdherenceAsNotCaptured("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            ArgumentCaptor<DosageAdherenceLog> adherenceLogArgumentCaptor = ArgumentCaptor.forClass(DosageAdherenceLog.class);
            verify(allDosageAdherenceLogs).add(adherenceLogArgumentCaptor.capture());
            assertFalse(adherenceLogArgumentCaptor.getValue().isDosageTakenLate());
            verify(pillReminderService, never()).setLastCapturedDate("regimenId", "dosageId", doseTakenTime.toLocalDate());
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
            when(pillRegimen.getDosesBetween(asOfDate.minusWeeks(4).toLocalDate(), asOfDate)).thenReturn(totalDoses);

            when(allDosageAdherenceLogs.countBy(same("regimenId"), same(DosageStatus.TAKEN), Matchers.<LocalDate>any(), Matchers.<LocalDate>any())).thenReturn(dosesTaken);
            assertEquals(((double) dosesTaken / totalDoses) * 100, dailyReminderAdherenceService.getAdherencePercentage(patientId, asOfDate));
        }

        @Test
        public void shouldReturn100PercentIfDosageNotStartedYet() {
            String patientId = "patientId";
            PillRegimen pillRegimen = mock(PillRegimen.class);
            when(pillRegimen.getId()).thenReturn("regimenId");
            when(pillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);
            when(pillRegimen.getDosesBetween(Matchers.<LocalDate>any(), Matchers.<DateTime>any())).thenReturn(0);

            assertEquals(100.0, dailyReminderAdherenceService.getAdherencePercentage(patientId, now));
            verifyZeroInteractions(allDosageAdherenceLogs);
        }
    }

    public static class WasAnyDoseTakenLateSince extends TestSubject {

        @Test
        public void shouldBeTrueWhenAtLeastOneDoseWasTakenSinceGivenDate() {
            LocalDate someDate = DateUtil.newDate(2011, 10, 10);
            when(allDosageAdherenceLogs.getDoseTakenLateCount("patient_id", someDate, true)).thenReturn(1);
            final Patient patient = PatientBuilder.startRecording().withId("patient_id").build();

            assertTrue(dailyReminderAdherenceService.wasAnyDoseTakenLateSince(patient, someDate));
        }

        @Test
        public void shouldBeFalseWhenNoDoseWasTakenLateLastWeek() {
            LocalDate someDate = DateUtil.newDate(2011, 10, 10);
            when(allDosageAdherenceLogs.getDoseTakenLateCount("patient_id", someDate, true)).thenReturn(0);
            final Patient patient = PatientBuilder.startRecording().withId("patient_id").build();

            assertFalse(dailyReminderAdherenceService.wasAnyDoseTakenLateSince(patient, someDate));
        }
    }

    public static class WasAnyDoseMissedLastWeek extends TestSubject {

        @Test
        public void shouldReturnAdherenceForLastWeek() {
            String patientId = "patientId";
            Patient patient = PatientBuilder.startRecording().withId(patientId).build();

            PillRegimen pillRegimen = mock(PillRegimen.class);
            when(pillRegimen.getId()).thenReturn("regimenId");
            when(pillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);

            ArgumentCaptor<LocalDate> fromDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
            ArgumentCaptor<DateTime> toDateCaptor = ArgumentCaptor.forClass(DateTime.class);
            when(pillRegimen.getDosesBetween(fromDateCaptor.capture(), toDateCaptor.capture())).thenReturn(7);

            ArgumentCaptor<LocalDate> adherenceLogFromDateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
            ArgumentCaptor<LocalDate> adherenceLogToDateArgumentCaptor = ArgumentCaptor.forClass(LocalDate.class);
            when(allDosageAdherenceLogs.countBy(Matchers.eq("regimenId"), Matchers.eq(DosageStatus.TAKEN), adherenceLogFromDateArgumentCaptor.capture(), adherenceLogToDateArgumentCaptor.capture())).thenReturn(1);

            assertTrue(dailyReminderAdherenceService.wasAnyDoseMissedLastWeek(patient));

            assertEquals(DateUtil.now().minusWeeks(1).toString("MM/dd/YYYY"), fromDateCaptor.getValue().toString("MM/dd/YYYY"));
            assertEquals(DateUtil.newDateTime(DateUtil.today().minusDays(1), 23, 59, 59).toString("MM/dd/YYYY HH:mm:ss"), toDateCaptor.getValue().toString("MM/dd/YYYY HH:mm:ss"));

            assertEquals(DateUtil.today().minusWeeks(1).toString("MM/dd/YYYY"), adherenceLogFromDateArgumentCaptor.getValue().toString("MM/dd/YYYY"));
            assertEquals(DateUtil.today().minusDays(1).toString("MM/dd/YYYY"), adherenceLogToDateArgumentCaptor.getValue().toString("MM/dd/YYYY"));
        }
    }
}


