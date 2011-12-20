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
import org.motechproject.tama.dailypillreminder.builder.TAMAPillRegimenBuilder;
import org.motechproject.tama.dailypillreminder.domain.*;
import org.motechproject.tama.dailypillreminder.repository.AllDosageAdherenceLogs;
import org.motechproject.tama.ivr.service.AdherenceService;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.util.DateUtil;

import java.util.Collections;
import java.util.Properties;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DailyPillReminderAdherenceServiceTest.RecordAdherence.class,
        DailyPillReminderAdherenceServiceTest.RecordAdherenceForSuspendedPeriod.class,
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
            when(allPatients.get("patientId")).thenReturn(PatientBuilder.startRecording().withLastSuspendedDate(DateUtil.now()).build());
            PillRegimen pillRegimen = TAMAPillRegimenBuilder.startRecording().withThreeDosagesInTotal().withTwoDosagesFrom(DateUtil.now()).build();
            when(pillReminderService.getPillRegimen("patientId")).thenReturn(pillRegimen);
            dailyReminderAdherenceService.recordAdherence("patientId", false);
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


