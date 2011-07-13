package org.motechproject.tama.web.mapper;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.builder.DrugBuilder;
import org.motechproject.tama.builder.RegimenBuilder;
import org.motechproject.tama.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.TreatmentAdvice;
import org.motechproject.tama.repository.Drugs;
import org.motechproject.tama.repository.Regimens;
import org.motechproject.tama.repository.TreatmentAdvices;
import org.motechproject.tama.web.model.TreatmentAdviceView;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TreatmentAdviceViewMapperTest {

    private Drugs drugs;
    private Regimens regimens;
    private TreatmentAdvices treatmentAdvices;

    @Before
    public void setUp() {
        drugs = mock(Drugs.class);
        regimens = mock(Regimens.class);
        treatmentAdvices = mock(TreatmentAdvices.class);
    }

    @Test
    public void shouldReturnTreatmentAdviceViewForTreatmentAdvice() {
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        when(treatmentAdvices.get("treatmentAdviceId")).thenReturn(treatmentAdvice);

        Regimen regimen = RegimenBuilder.startRecording().withDefaults().build();
        when(regimens.get("regimenId")).thenReturn(regimen);

        List<Drug> returnedDrugs = new ArrayList<Drug>();
        returnedDrugs.add(DrugBuilder.startRecording().withId("drugId1").withName("Drug1").build());
        returnedDrugs.add(DrugBuilder.startRecording().withId("drugId2").withName("Drug2").build());
        when(drugs.getDrugs(regimen.getCompositionsFor("regimenCompositionId").getDrugIds())).thenReturn(returnedDrugs);

        TreatmentAdviceView treatmentAdviceView = new TreatmentAdviceViewMapper(regimens, treatmentAdvices, drugs).map("treatmentAdviceId");

        Assert.assertEquals("patientId", treatmentAdviceView.getPatientId());
        Assert.assertEquals("regimenName", treatmentAdviceView.getRegimenName());
        Assert.assertEquals("Drug1 / Drug2", treatmentAdviceView.getRegimenCompositionName());
        Assert.assertEquals(0, treatmentAdviceView.getDrugDosageViews().size());
    }
}
