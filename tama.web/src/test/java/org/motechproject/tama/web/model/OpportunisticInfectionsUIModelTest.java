package org.motechproject.tama.web.model;


import org.junit.Test;
import org.motechproject.tama.patient.domain.OpportunisticInfections;

import static junit.framework.Assert.assertTrue;

public class OpportunisticInfectionsUIModelTest {

    public static final String PATIENT_ID = "patientId";

    private OpportunisticInfectionsUIModel opportunisticInfectionsUIModel;

    public OpportunisticInfectionsUIModelTest() {
        opportunisticInfectionsUIModel = new OpportunisticInfectionsUIModel(PATIENT_ID);
    }

    @Test
    public void shouldOpportunisticInfectionToSummary() {
        OpportunisticInfections opportunisticInfections = new OpportunisticInfections(PATIENT_ID);
        opportunisticInfections.setAnemia(true);

        opportunisticInfectionsUIModel.setOpportunisticInfections(opportunisticInfections);
        assertTrue(opportunisticInfectionsUIModel.getSummary().contains("Anemia"));
    }
}
