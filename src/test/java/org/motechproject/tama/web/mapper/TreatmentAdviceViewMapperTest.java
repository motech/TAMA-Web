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
import org.motechproject.tama.repository.AllPatients;
import org.motechproject.tama.repository.AllRegimens;
import org.motechproject.tama.repository.AllTreatmentAdvices;
import org.motechproject.tama.web.model.TreatmentAdviceView;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TreatmentAdviceViewMapperTest {

    private AllTreatmentAdvices allTreatmentAdvices;
    private AllPatients allPatients;
    private AllRegimens allRegimens;

    @Before
    public void setUp() {
        allTreatmentAdvices = mock(AllTreatmentAdvices.class);
        allPatients = mock(AllPatients.class);
        allRegimens = mock(AllRegimens.class);
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
        when(allTreatmentAdvices.get("treatmentAdviceId")).thenReturn(treatmentAdvice);
        when(allPatients.get(patient.getId())).thenReturn(patient);
        when(allRegimens.get(regimen.getId())).thenReturn(regimen);

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceViewMapper(allTreatmentAdvices, allPatients, allRegimens, null, null, null).map("treatmentAdviceId");

        Assert.assertEquals(treatmentAdvice.getId(), treatmentAdviceView.getTreatmentAdviceId());
        Assert.assertEquals(treatmentAdvice.getPatientId(), treatmentAdviceView.getPatientIdentifier());
        Assert.assertEquals(patient.getPatientId(), treatmentAdviceView.getPatientId());
        Assert.assertEquals(regimen.getDisplayName(), treatmentAdviceView.getRegimenName());
        Assert.assertEquals(group.getName(), treatmentAdviceView.getDrugCompositionName());
        Assert.assertEquals(0, treatmentAdviceView.getDrugDosages().size());
    }
}
