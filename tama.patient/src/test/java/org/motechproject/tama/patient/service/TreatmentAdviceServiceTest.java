package org.motechproject.tama.patient.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.CallPreference;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.patient.strategy.DailyPillReminder;
import org.motechproject.tama.patient.strategy.FourDayRecall;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TreatmentAdviceServiceTest {

    @Mock
    private AllPatients allPatients;
    @Mock
    private AllTreatmentAdvices allTreatmentAdvices;
    @Mock
    private DailyPillReminder dailyPillReminder;
    @Mock
    private FourDayRecall fourDayRecall;
    private TreatmentAdviceService treatmentAdviceService;

    @Before
    public void setUp() {
        initMocks(this);
        treatmentAdviceService = new TreatmentAdviceService(allPatients, allTreatmentAdvices);
    }

    @Test
    public void dailyPillReminderPatient_createsANewRegimen() {
        treatmentAdviceService.registerDailyPillReminder(dailyPillReminder);
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.createRegimen(treatmentAdvice);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(dailyPillReminder).enroll(patient, treatmentAdvice);
    }

    @Test
    public void fourDayRecallPatient_createsANewRegimen() {
        treatmentAdviceService.registerFourDayRecall(fourDayRecall);
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.createRegimen(treatmentAdvice);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(fourDayRecall).enroll(patient, treatmentAdvice);
    }

    @Test
    public void dailyPillReminderPatient_changesCurrentRegimen() {
        treatmentAdviceService.registerDailyPillReminder(dailyPillReminder);
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.DailyPillReminder).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allTreatmentAdvices.get(existingTreatmentAdvice.getId())).thenReturn(existingTreatmentAdvice);
        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.changeRegimen(existingTreatmentAdvice.getId(), "stop", treatmentAdvice);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(allTreatmentAdvices).update(existingTreatmentAdvice);
        verify(dailyPillReminder).reEnroll(patient, treatmentAdvice);
    }

    @Test
    public void fourDayRecallPatient_changesCurrentRegimen() {
        treatmentAdviceService.registerFourDayRecall(fourDayRecall);
        Patient patient = PatientBuilder.startRecording().withDefaults().withCallPreference(CallPreference.FourDayRecall).build();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        TreatmentAdvice existingTreatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();

        when(allTreatmentAdvices.get(existingTreatmentAdvice.getId())).thenReturn(existingTreatmentAdvice);
        when(allPatients.get(treatmentAdvice.getPatientId())).thenReturn(patient);

        treatmentAdviceService.changeRegimen(existingTreatmentAdvice.getId(), "stop", treatmentAdvice);
        verify(allTreatmentAdvices).add(treatmentAdvice);
        verify(allTreatmentAdvices).update(existingTreatmentAdvice);
        verify(fourDayRecall).reEnroll(patient, treatmentAdvice);
    }
}
