package org.motechproject.tama.patient.reporting;


import org.junit.Test;
import org.motechproject.tama.patient.builder.TreatmentAdviceBuilder;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.patient.domain.TreatmentAdvice;
import org.motechproject.tama.reports.contract.PillTimeRequest;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class PillTimeRequestMapperTest {

    @Test
    public void shouldMapPatientDocumentId() {
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        PillTimeRequestMapper pillTimeRequestMapper = new PillTimeRequestMapper(treatmentAdvice);

        PillTimeRequest pillTimeRequest = pillTimeRequestMapper.map();

        assertEquals(treatmentAdvice.getPatientId(), pillTimeRequest.getPatientDocumentId());
    }

    @Test
    public void testTimesWithTwoDosesEachHavingEitherMorningOrEveningTime() {
        List<DrugDosage> dosages = asList(
                dosage("09:10am", null),
                dosage(null, "02:10pm")
        );

        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        treatmentAdvice.setDrugDosages(dosages);

        PillTimeRequestMapper pillTimeRequestMapper = new PillTimeRequestMapper(treatmentAdvice);

        PillTimeRequest pillTimeRequest = pillTimeRequestMapper.map();
        assertEquals("09:10:00", pillTimeRequest.getMorningPillTime());
        assertEquals("14:10:00", pillTimeRequest.getEveningPillTime());
    }

    @Test
    public void testTimesWithOneDoseHavingBothMorningAndEveningTime() {
        List<DrugDosage> dosages = asList(
                dosage("10:10am", "02:10pm")
        );

        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().build();
        treatmentAdvice.setDrugDosages(dosages);

        PillTimeRequestMapper pillTimeRequestMapper = new PillTimeRequestMapper(treatmentAdvice);

        PillTimeRequest pillTimeRequest = pillTimeRequestMapper.map();
        assertEquals("10:10:00", pillTimeRequest.getMorningPillTime());
        assertEquals("14:10:00", pillTimeRequest.getEveningPillTime());
    }

    private DrugDosage dosage(String morningTime, String eveningTime) {
        DrugDosage dosage = new DrugDosage();
        dosage.setMorningTime(morningTime);
        dosage.setEveningTime(eveningTime);
        return dosage;
    }
}
