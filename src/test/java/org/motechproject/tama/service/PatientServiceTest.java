package org.motechproject.tama.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.server.pillreminder.service.PillReminderService;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.domain.*;
import org.motechproject.tama.platform.service.TAMASchedulerService;
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.repository.AllUniquePatientFields;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        PatientServiceTest.PatientIsNotPersisted.class,
        PatientServiceTest.PatientIsNotPersisted.PatientPreferenceIsDailyPillReminder.class,
        PatientServiceTest.PatientIsNotPersisted.PatientPreferenceIsDailyPillReminder.PatientAgreesToBeCalledAtBestCallTime.class,
        PatientServiceTest.PatientIsPersisted.class,
        PatientServiceTest.PatientIsPersisted.PatientIsAlreadyOnDailyPillReminder.class,
        PatientServiceTest.PatientIsPersisted.PatientIsAlreadyOnDailyPillReminder.PatientHasAgreedToNowBeCalledAtBestCallTime.class,
        PatientServiceTest.PatientIsPersisted.PatientIsAlreadyOnDailyPillReminder.PatientHasChangedBestCallTime.class,
        PatientServiceTest.PatientIsPersisted.PatientIsAlreadyOnDailyPillReminder.PatientHasSwitchedToFourDayRecall.class,
        PatientServiceTest.PatientIsPersisted.PatientIsAlreadyOnDailyPillReminder.PatientHasNowDisAgreedToBeCalledAtBestCallTime.class,
        PatientServiceTest.PatientIsPersisted.PatientIsAlreadyOnFourDayRecall.class,
        PatientServiceTest.PatientIsPersisted.PatientIsAlreadyOnFourDayRecall.PatientHasChangedTheBestCallTime.class
})
public class PatientServiceTest {

    public static class PatientServiceTestScenario {
        @Mock
        protected AllPatients allPatients;
        @Mock
        protected AllUniquePatientFields allUniquePatientFields;
        @Mock
        protected TAMASchedulerService tamaSchedulerService;
        @Mock
        protected AllTreatmentAdvices allTreatmentAdvices;
        @Mock
        protected PillReminderService pillReminderService;

        protected PatientService patientService;

        public PatientServiceTestScenario() {
            initMocks(this);
            patientService = new PatientService(allPatients, allUniquePatientFields, tamaSchedulerService, allTreatmentAdvices, pillReminderService);
        }
    }

    public static class PatientIsNotPersisted extends PatientServiceTestScenario {
        protected Patient patient;

        @Before
        public void setUp() {
            patient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").build();
            patientIsNotInAllPatients();
        }

        private void patientIsNotInAllPatients() {
            when(allPatients.get("patientDocId")).thenReturn(null);
        }

        @Test
        public void create() {
            patientService.create(patient, "clinicName");
            verify(allPatients).addToClinic(patient, "clinicName");
        }

        public static class PatientPreferenceIsDailyPillReminder extends PatientIsNotPersisted {
            @Override
            public void setUp() {
                super.setUp();
                callPreferenceIsDailyPillReminder();
            }

            private void callPreferenceIsDailyPillReminder() {
                patient.getPatientPreferences().setCallPreference(CallPreference.DailyPillReminder);
            }

            public static class PatientAgreesToBeCalledAtBestCallTime extends PatientPreferenceIsDailyPillReminder {
                @Override
                public void setUp() {
                    super.setUp();
                    bestCallTimeIsNotNull();
                }

                private void bestCallTimeIsNotNull() {
                    patient.getPatientPreferences().setBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM));
                }

                @Test
                public void create() {
                    patientService.create(patient, "clinicName");
                    verify(tamaSchedulerService).scheduleJobForOutboxCall(patient);
                }
            }
        }
    }

    public static class PatientIsPersisted extends PatientServiceTestScenario {
        protected Patient dbPatient;
        protected Patient updatedPatient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").withMobileNumber("77777777777").build();

        @Before
        public void setUp() {
            dbPatient = PatientBuilder.startRecording().withDefaults().withId("patientDocId").build();
            patientIdIsUnique();
            patientIsInAllPatients();
        }

        private void patientIdIsUnique() {
            when(allUniquePatientFields.get("patientDocId")).thenReturn(new UniquePatientField("patientId", "patientDocId"));
        }

        private void patientIsInAllPatients() {
            when(allPatients.get("patientDocId")).thenReturn(dbPatient);
        }

        @Test
        public void update() {
            patientService.update(updatedPatient);
            verify(allUniquePatientFields, times(1)).remove(dbPatient);
            verify(allUniquePatientFields, times(1)).add(dbPatient);
            verify(allPatients).update(updatedPatient);
        }

        public static class PatientIsAlreadyOnDailyPillReminder extends PatientIsPersisted {
            @Override
            public void setUp() {
                super.setUp();
                callPreferenceIsDailyPillReminder();
            }

            private void callPreferenceIsDailyPillReminder() {
                dbPatient.getPatientPreferences().setCallPreference(CallPreference.DailyPillReminder);
            }

            public static class PatientHasAgreedToNowBeCalledAtBestCallTime extends PatientIsAlreadyOnDailyPillReminder {


                @Override
                public void setUp() {
                    super.setUp();
                    bestCallTimeWasNull();
                }

                private void bestCallTimeWasNull() {
                    dbPatient.getPatientPreferences().setBestCallTime(new TimeOfDay(null, null, null));
                }

                @Test
                public void update() {
                    updatedPatient.getPatientPreferences().setBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM));
                    patientService.update(updatedPatient);
                    verify(tamaSchedulerService, times(1)).scheduleJobForOutboxCall(updatedPatient);
                }
            }

            public static class PatientHasNowDisAgreedToBeCalledAtBestCallTime extends PatientIsAlreadyOnDailyPillReminder {
                @Override
                public void setUp() {
                    super.setUp();
                    bestCallTimeWasNotNull();
                }

                private void bestCallTimeWasNotNull() {
                    dbPatient.getPatientPreferences().setBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM));
                }

                @Test
                public void update() {
                    updatedPatient.getPatientPreferences().setBestCallTime(new TimeOfDay(null, null, null));
                    patientService.update(updatedPatient);
                    verify(tamaSchedulerService, times(1)).unscheduleJobForOutboxCall(dbPatient);
                    verify(tamaSchedulerService, times(1)).unscheduleRepeatingJobForOutboxCall(dbPatient.getId());
                }
            }

            public static class PatientHasChangedBestCallTime extends PatientIsAlreadyOnDailyPillReminder {
                @Override
                public void setUp() {
                    super.setUp();
                    bestCallTimeIsNotNull();
                }

                private void bestCallTimeIsNotNull() {
                    dbPatient.getPatientPreferences().setBestCallTime(new TimeOfDay(10, 0, TimeMeridiem.AM));
                }

                @Test
                public void update() {
                    updatedPatient.getPatientPreferences().setBestCallTime(new TimeOfDay(11, 10, TimeMeridiem.PM));
                    patientService.update(updatedPatient);
                    verify(tamaSchedulerService, times(1)).unscheduleJobForOutboxCall(dbPatient);
                    verify(tamaSchedulerService, times(1)).scheduleJobForOutboxCall(updatedPatient);
                }
            }

            public static class PatientHasSwitchedToFourDayRecall extends PatientIsAlreadyOnDailyPillReminder {
                @Override
                public void setUp() {
                    super.setUp();
                    patientHadATreatmentAdvice();
                }

                private void patientHadATreatmentAdvice() {
                    when(allTreatmentAdvices.findByPatientId(dbPatient.getId())).thenReturn(new TreatmentAdvice());
                }

                @Test
                public void update() {
                    updatedPatient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
                    patientService.update(updatedPatient);
                    verify(pillReminderService, times(1)).unscheduleJobs(updatedPatient.getId());
                    verify(tamaSchedulerService).unscheduleJobForAdherenceTrendFeedback(Matchers.<TreatmentAdvice>any());
                    verify(tamaSchedulerService, times(1)).scheduleJobsForFourDayRecall(same(updatedPatient), Matchers.<TreatmentAdvice>any());
                    verify(tamaSchedulerService, times(1)).unscheduleJobForOutboxCall(dbPatient);
                    verify(tamaSchedulerService, times(1)).unscheduleRepeatingJobForOutboxCall(dbPatient.getId());
                }
            }
        }

        public static class PatientIsAlreadyOnFourDayRecall extends PatientIsPersisted {
            @Override
            public void setUp() {
                super.setUp();
                callPreferenceIsFourDayRecall();
            }

            private void callPreferenceIsFourDayRecall() {
                dbPatient.getPatientPreferences().setCallPreference(CallPreference.FourDayRecall);
            }

            public static class PatientHasChangedTheBestCallTime extends PatientIsAlreadyOnFourDayRecall {
                @Override
                public void setUp() {
                    super.setUp();
                    dbPatient.getPatientPreferences().setBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM));
                    updatedPatient.getPatientPreferences().setBestCallTime(new TimeOfDay(10, 10, TimeMeridiem.AM));
                }

                @Test
                public void update() {
                    tamaSchedulerService.unScheduleFourDayRecallJobs(dbPatient);
                    tamaSchedulerService.scheduleJobsForFourDayRecall(same(updatedPatient), Matchers.<TreatmentAdvice>any());
                }
            }
        }
    }
}
