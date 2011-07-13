package org.motechproject.tama.web.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.Patients;
import org.motechproject.tama.repository.Regimens;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.motechproject.tama.web.model.TreatmentAdviceView;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TreatmentAdviceViewMapperTest {

    private TreatmentAdvices treatmentAdvices;
    private Patients patients;
    private Regimens regimens;

    @Before
    public void setUp() {
        treatmentAdvices = mock(TreatmentAdvices.class);
        patients = mock(Patients.class);
        regimens = mock(Regimens.class);
    }

    @Test
    public void shouldReturnTreatmentAdviceViewForTreatmentAdvice() {
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        when(treatmentAdvices.get("treatmentAdviceId")).thenReturn(treatmentAdvice);

        Patient patient = PatientBuilder.startRecording().withPatientId("patientId").build();
        when(patients.get("patientId")).thenReturn(patient);

        Regimen regimen = RegimenBuilder.startRecording().withDefaults().build();
        when(regimens.get("regimenId")).thenReturn(regimen);

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceViewMapper(treatmentAdvices, patients, regimens).map("treatmentAdviceId");

        Assert.assertEquals("patientId", treatmentAdviceView.getPatientId());
        Assert.assertEquals("regimenName", treatmentAdviceView.getRegimenName());
        Assert.assertEquals("drugDisplayName", treatmentAdviceView.getRegimenCompositionName());
        Assert.assertEquals(0, treatmentAdviceView.getDrugDosageViews().size());
    }
}
