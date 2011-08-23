package org.motechproject.tama.web.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.PatientBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.DrugCompositionGroup;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.Patients;
import org.motechproject.tama.repository.Regimens;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.motechproject.tama.web.model.TreatmentAdviceView;

import java.util.ArrayList;

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
        Regimen regimen = RegimenBuilder.startRecording()
                .withDefaults()
                .build();
        Patient patient = PatientBuilder.startRecording().withPatientId("patientId").build();
        DrugCompositionGroup group = (DrugCompositionGroup) (new ArrayList(regimen.getDrugCompositionGroups()).get(0));
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording()
                                                                .withRegimenId(regimen.getId())
                                                                .withDrugCompositionGroupId(group.getId())
                                                                .withPatientId(patient.getId())
                                                                  .build();
        when(treatmentAdvices.get("treatmentAdviceId")).thenReturn(treatmentAdvice);

        when(patients.get(patient.getId())).thenReturn(patient);

        when(regimens.get(regimen.getId())).thenReturn(regimen);

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceViewMapper(treatmentAdvices, patients, regimens, null, null, null).map("treatmentAdviceId");

        Assert.assertEquals(treatmentAdvice.getId(), treatmentAdviceView.getTreatmentAdviceId());
        Assert.assertEquals(treatmentAdvice.getPatientId(), treatmentAdviceView.getPatientIdentifier());
        Assert.assertEquals(patient.getPatientId(), treatmentAdviceView.getPatientId());
        Assert.assertEquals(regimen.getDisplayName(), treatmentAdviceView.getRegimenName());
        Assert.assertEquals(group.getName(), treatmentAdviceView.getDrugCompositionName());
        Assert.assertEquals(0, treatmentAdviceView.getDrugDosages().size());
    }
}
