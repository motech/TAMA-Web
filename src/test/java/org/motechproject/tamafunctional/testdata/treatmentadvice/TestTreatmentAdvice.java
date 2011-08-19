package org.motechproject.tamafunctional.testdata.treatmentadvice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestTreatmentAdvice {
    private List<TestDrugDosage> drugDosages = new ArrayList<TestDrugDosage>();
    private String regimenName;
    private String drugCompositionName;

    public TestTreatmentAdvice() {
    }

    public static TestTreatmentAdvice withExtrinsic(TestDrugDosage... drugDosages) {
        TestTreatmentAdvice treatmentAdvice = new TestTreatmentAdvice();
        treatmentAdvice.drugDosages.addAll(Arrays.asList(drugDosages));
        return treatmentAdvice.regimenName("AZT + 3TC + EFV").drugCompositionName("AZT+3TC+EFV");
    }

    public TestTreatmentAdvice drugCompositionName(String name) {
        drugCompositionName = name;
        return this;
    }

    public String drugCompositionName() {
        return drugCompositionName;
    }

    public TestTreatmentAdvice regimenName(String regimenName) {
        this.regimenName = regimenName;
        return this;
    }

    public String regimenName() {
        return regimenName;
    }

    public List<TestDrugDosage> drugDosages() {
        return drugDosages;
    }
}
