package org.motechproject.tama.patient.util;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.motechproject.tama.patient.util.CallPlanUtil.callPlanStartDate;

public class CallPlanUtilTest {

    @Test
    public void shouldReturnCallPlanTransitionDate_WhenThereHasBeenTransition() {
        LocalDate treatmentStartDate = DateUtil.newDate(2011, 1, 1);
        LocalDate transitionDate = DateUtil.newDate(2011, 11, 10);
        final Patient patient = PatientBuilder.startRecording().withDefaults().withTransitionDate(transitionDate).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentStartDate).build();

        assertEquals(transitionDate, callPlanStartDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTreatmentStartDate_WhenThereHasBeenNoCallPlanTransition() {
        LocalDate treatmentStartDate = DateUtil.newDate(2011, 12, 1);
        final Patient patient = PatientBuilder.startRecording().withDefaults().build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentStartDate).build();

        assertEquals(treatmentStartDate, callPlanStartDate(patient, treatmentAdvice));
    }

    @Test
    public void shouldReturnTreatmentAdviceStartDateItselfForPatientAsTreatmentAdviceStartDate_IfTreatmentAdviceHasNotYetBegun_AndPatientCallPreferenceHasChanged() {
        LocalDate treatmentStartDate = DateUtil.newDate(2011, 11, 25);
        LocalDate transitionDate = DateUtil.newDate(2011, 11, 10);
        final Patient patient = PatientBuilder.startRecording().withDefaults().withTransitionDate(transitionDate).build();
        final TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withStartDate(treatmentStartDate).build();

        assertEquals(treatmentStartDate, callPlanStartDate(patient, treatmentAdvice));
    }
}
