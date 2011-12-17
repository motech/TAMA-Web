package org.motechproject.tama.web.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.patient.builder.PatientBuilder;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.Status;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllTreatmentAdvices;
import org.motechproject.tama.refdata.builder.RegimenBuilder;
import org.motechproject.tama.refdata.domain.DrugCompositionGroup;
import org.motechproject.tama.refdata.domain.Regimen;
import org.motechproject.tama.refdata.repository.AllRegimens;
import org.motechproject.tama.web.model.TreatmentAdviceView;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
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
        DrugCompositionGroup group = new ArrayList<DrugCompositionGroup>(regimen.getDrugCompositionGroups()).get(0);
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

    @Test
    public void shouldShowChangeRegimenButtonIfPatientIsActive() {
        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        treatmentAdviceView.setPatientStatus(Status.Active);
        assertTrue(treatmentAdviceView.getShowChangeRegimenButton());
    }

    @Test
    public void shouldNotShowChangeRegimenButtonIfPatientIsInActive() {
        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        treatmentAdviceView.setPatientStatus(Status.Inactive);
        assertFalse(treatmentAdviceView.getShowChangeRegimenButton());
    }

    @Test
    public void shouldNotShowChangeRegimenButtonIfPatientIsSuspended() {
        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceView();
        treatmentAdviceView.setPatientStatus(Status.Suspended);
        assertFalse(treatmentAdviceView.getShowChangeRegimenButton());
    }

}
