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
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.server.pillreminder.contract.MedicineResponse;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.dailypillreminder.builder.PillRegimenResponseBuilder;
import org.motechproject.tama.dailypillreminder.domain.DosageAdherenceLog;
import org.motechproject.tama.dailypillreminder.domain.DosageStatus;
import org.motechproject.tama.dailypillreminder.domain.Dose;
import org.motechproject.tama.dailypillreminder.domain.PillRegimen;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DailyPillReminderAdherenceServiceTest.RecordDosageAdherenceAsCaptured.class,
        DailyPillReminderAdherenceServiceTest.RecordDosageAdherenceAsNotCaptured.class,
        DailyPillReminderAdherenceServiceTest.BackFillAdherenceForSingleDosage.class,
        DailyPillReminderAdherenceServiceTest.BackFillAdherenceForMultipleDosages.class,
        DailyPillReminderAdherenceServiceTest.CalculateAdherence.class,
        DailyPillReminderAdherenceServiceTest.WasAnyDoseMissedLastWeek.class,
        DailyPillReminderAdherenceServiceTest.WasAnyDoseTakenLateSince.class
})
public class DailyPillReminderAdherenceServiceTest {

    public static class TestSubject {
        @Mock
        protected AllDosageAdherenceLogs allDosageAdherenceLogs;
        @Mock
        protected DailyPillReminderService dailyPillReminderService;
        @Mock
        protected AllPatients allPatients;

        protected Properties ivrProperties;

        protected DailyPillReminderAdherenceService dailyReminderAdherenceService;

        protected DateTime now;

        protected LocalDate today;
        protected Patient patient;

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
            dailyReminderAdherenceService = new DailyPillReminderAdherenceService(allDosageAdherenceLogs, dailyPillReminderService, ivrProperties, new AdherenceService(), allPatients);
            patient = PatientBuilder.startRecording().withId("patientId").withCallPreference(CallPreference.DailyPillReminder).build();
        }
    }

    public static class BackFillAdherenceForSingleDosage extends TestSubject {

        @Test
        public void whenFirstApplicableDoseIsBeforeSpecifiedStartTime_AndIsNotAlreadyRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 9, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 15, 0);
            final Time dosageTime = new Time(6, 0);
            PillRegimen pillRegimen = pillRegimenWithSingleDosage(dosageTime, startDate.toLocalDate(), "dosageId1");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(3)).add(Matchers.<DosageAdherenceLog>any());
        }

        @Test
        public void whenFirstApplicableDoseIsBeforeSpecifiedStartTime_AndIsAlreadyRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 9, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 15, 0);
            final Time dosageTime = new Time(6, 0);
            PillRegimen pillRegimen = pillRegimenWithSingleDosage(dosageTime, startDate.toLocalDate().minusDays(10), "dosageId");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId", startDate.toLocalDate())).thenReturn(new DosageAdherenceLog());

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
        }

        @Test
        public void whenFirstApplicableDoseIsAfterSpecifiedStartTime_ButWithinPillWindow_AndIsAlreadyRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 9, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 15, 0);
            final Time dosageTime = new Time(10, 0);
            PillRegimen pillRegimen = pillRegimenWithSingleDosage(dosageTime, startDate.toLocalDate(), "dosageId");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId", startDate.toLocalDate())).thenReturn(new DosageAdherenceLog());

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
        }

        @Test
        public void whenFirstApplicableDoseIsOnThePreviousDay_AndIsNotRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 9, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 15, 0);
            final Time dosageTime = new Time(22, 0);
            PillRegimen pillRegimen = pillRegimenWithSingleDosage(dosageTime, startDate.toLocalDate().minusDays(1), "dosageId");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(3)).add(Matchers.<DosageAdherenceLog>any());
        }

        @Test
        public void whenFirstApplicableDoseIsOnTheNextDay_AndIsNotRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 20, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 6, 0);
            final Time dosageTime = new Time(6, 0);
            PillRegimen pillRegimen = pillRegimenWithSingleDosage(dosageTime, startDate.toLocalDate().plusDays(1), "dosageId");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(2)).add(Matchers.<DosageAdherenceLog>any());
        }

        @Test
        public void _whenFirstApplicableDoseIsOnTheNextDay_AndIsNotRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 23, 30);
            final DateTime endDate = new DateTime(2011, 10, 11, 6, 0);
            final Time dosageTime = new Time(1, 0);
            PillRegimen pillRegimen = pillRegimenWithSingleDosage(dosageTime, startDate.minusDays(5).toLocalDate(), "dosageId");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(1)).add(Matchers.<DosageAdherenceLog>any());
        }

        private PillRegimen pillRegimenWithSingleDosage(Time dosageTime, LocalDate dosageStartDate, String dosageId) {
            DosageResponse doseResponse = new DosageResponse(dosageId, dosageTime, dosageStartDate, null, null, Collections.<MedicineResponse>emptyList());
            final List<DosageResponse> dosages = Arrays.asList(doseResponse);
            return new PillRegimen(PillRegimenResponseBuilder.startRecording().withDosages(dosages).build());
        }
    }

    public static class BackFillAdherenceForMultipleDosages extends TestSubject {

        @Test
        public void whenFirstApplicableDoseIsTheFirstDose_AndIsBeforeSpecifiedStartTime_AndIsNotAlreadyRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 9, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 15, 45);
            PillRegimen pillRegimen = pillRegimenWithMultipleDosages(new Time(6, 0), startDate.toLocalDate(), "dosageId1", new Time(16, 0), startDate.toLocalDate(), "dosageId2");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(5)).add(Matchers.<DosageAdherenceLog>any());
        }

        @Test
        public void whenFirstApplicableDoseIsBeforeSpecifiedStartTime_AndIsAlreadyRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 9, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 15, 0);
            PillRegimen pillRegimen = pillRegimenWithMultipleDosages(new Time(6, 0), startDate.toLocalDate(), "dosageId1", new Time(16, 0), startDate.toLocalDate(), "dosageId2");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId1", startDate.toLocalDate())).thenReturn(new DosageAdherenceLog());

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(4)).add(Matchers.<DosageAdherenceLog>any());
        }

        @Test
        public void whenFirstApplicableDoseIsTheSecondDose_AndIsAfterSpecifiedStartTime_AndIsNotAlreadyRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 15, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 17, 0);
            PillRegimen pillRegimen = pillRegimenWithMultipleDosages(new Time(6, 0), startDate.toLocalDate(), "dosageId1", new Time(16, 0), startDate.toLocalDate(), "dosageId2");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(5)).add(Matchers.<DosageAdherenceLog>any());
        }

        @Test
        public void whenSecondDoseStartsInFuture_AndFirstDoseIsAlreadyRecorded() {
            final DateTime startDate = new DateTime(2011, 10, 10, 9, 0);
            final DateTime endDate = new DateTime(2011, 10, 12, 17, 0);
            PillRegimen pillRegimen = pillRegimenWithMultipleDosages(new Time(6, 0), startDate.toLocalDate(), "dosageId1", new Time(16, 0), startDate.toLocalDate().plusDays(2), "dosageId2");

            when(dailyPillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
            when(allDosageAdherenceLogs.findByDosageIdAndDate("dosageId1", startDate.toLocalDate())).thenReturn(new DosageAdherenceLog());

            dailyReminderAdherenceService.backFillAdherence("patientId", false, startDate, endDate);
            verify(allDosageAdherenceLogs, times(3)).add(Matchers.<DosageAdherenceLog>any());
        }

        private PillRegimen pillRegimenWithMultipleDosages(Time dosage1Time, LocalDate dosage1StartDate, String dosage1Id, Time dosage2Time, LocalDate dosage2StartDate, String dosage2Id) {
            DosageResponse doseResponse1 = new DosageResponse(dosage1Id, dosage1Time, dosage1StartDate, null, null, Collections.<MedicineResponse>emptyList());
            DosageResponse doseResponse2 = new DosageResponse(dosage2Id, dosage2Time, dosage2StartDate, null, null, Collections.<MedicineResponse>emptyList());
            final List<DosageResponse> dosages = Arrays.asList(doseResponse1, doseResponse2);
            return new PillRegimen(PillRegimenResponseBuilder.startRecording().withDosages(dosages).build());
        }
    }

    public static class RecordDosageAdherenceAsCaptured extends TestSubject {

        @Test
        public void whenDoseIsTaken() {
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
            verify(dailyPillReminderService).setLastCapturedDate("regimenId", "dosageId", doseTakenTime.toLocalDate());
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
            verify(dailyPillReminderService).setLastCapturedDate("regimenId", "dosageId", doseDate.toLocalDate());
        }

        @Test
        public void whenDoseIsTakenLate() {
            DateTime doseDate = DateUtil.newDateTime(new LocalDate(2011, 10, 10), 10, 0, 0);
            DateTime doseTakenTime = doseDate.plusHours(1);

            Dose dose = mock(Dose.class);
            when(dose.getDosageId()).thenReturn("dosageId");
            when(dose.getDate()).thenReturn(doseDate.toLocalDate());
            when(dose.getDoseTime()).thenReturn(doseDate);
            when(dose.isLateToTake(doseTakenTime, 15)).thenReturn(true);

            dailyReminderAdherenceService.recordDosageAdherenceAsCaptured("patientId", "regimenId", dose, DosageStatus.TAKEN, doseTakenTime);

            ArgumentCaptor<DosageAdherenceLog> adherenceLogArgumentCaptor = ArgumentCaptor.forClass(DosageAdherenceLog.class);
            verify(allDosageAdherenceLogs).add(adherenceLogArgumentCaptor.capture());
            assertTrue(adherenceLogArgumentCaptor.getValue().isDosageTakenLate());
            verify(dailyPillReminderService).setLastCapturedDate("regimenId", "dosageId", doseDate.toLocalDate());
        }
    }

    public static class RecordDosageAdherenceAsNotCaptured extends TestSubject {
        @Test
        public void whenDoseIsReportedAsWillTakeLater() {
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
            verify(dailyPillReminderService, never()).setLastCapturedDate("regimenId", "dosageId", doseTakenTime.toLocalDate());
        }
    }

    public static class CalculateAdherence extends TestSubject {
        @Test
        public void forPatient() {
            String patientId = "patientId";
            int dosesTaken = 7;
            int totalDoses = 28;

            PillRegimen pillRegimen = mock(PillRegimen.class);
            DateTime asOfDate = now;

            when(dailyPillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);
            when(pillRegimen.getId()).thenReturn("regimenId");
            when(pillRegimen.getDosesBetween(asOfDate.minusWeeks(4).toLocalDate(), asOfDate)).thenReturn(totalDoses);
            when(allPatients.get(anyString())).thenReturn(patient);
            when(allDosageAdherenceLogs.countBy(same("regimenId"), same(DosageStatus.TAKEN), Matchers.<LocalDate>any(), Matchers.<LocalDate>any())).thenReturn(dosesTaken);

            assertEquals(((double) dosesTaken / totalDoses) * 100, dailyReminderAdherenceService.getAdherencePercentage(patientId, asOfDate));
        }

        @Test
        public void shouldReturn100PercentIfDosageNotStartedYet() {
            String patientId = "patientId";
            PillRegimen pillRegimen = mock(PillRegimen.class);

            when(pillRegimen.getId()).thenReturn("regimenId");
            when(dailyPillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);
            when(pillRegimen.getDosesBetween(Matchers.<LocalDate>any(), Matchers.<DateTime>any())).thenReturn(0);
            when(allPatients.get(anyString())).thenReturn(patient);

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
            when(dailyPillReminderService.getPillRegimen(patientId)).thenReturn(pillRegimen);

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


