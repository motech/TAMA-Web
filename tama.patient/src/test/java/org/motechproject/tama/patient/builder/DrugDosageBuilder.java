package org.motechproject.tama.patient.builder;

import org.joda.time.LocalDate;
import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.refdata.domain.Brand;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.util.DateUtil;

public class DrugDosageBuilder {

    private DrugDosage drugDosage = new DrugDosage();

    public static DrugDosageBuilder startRecording() {
        return new DrugDosageBuilder();
    }

    public DrugDosageBuilder withDetails() {
        LocalDate startDate = DateUtil.today();

        drugDosage.setDrugId("drugId");
        drugDosage.setMorningTime("11:30");
        drugDosage.setStartDate(startDate);
        drugDosage.setEndDate(startDate.plusYears(2));
        drugDosage.setDosageTypeId("drugDosageTypeId");
        drugDosage.setMealAdviceId("mealAdviceId");
        drugDosage.setBrandId("companyId");
        return this;
    }

    public DrugDosageBuilder withDrug(Drug drug) {
        drugDosage.setDrugId(drug.getId());
        return this;
    }

    public DrugDosageBuilder withMorningTime(String time) {
        drugDosage.setMorningTime("11:30");
        return this;
    }

    public DrugDosageBuilder withDateRange(LocalDate startDate, LocalDate endDate) {
        drugDosage.setStartDate(startDate);
        drugDosage.setEndDate(endDate);
        return this;
    }

    public DrugDosageBuilder withDosageType(DosageType dosageType) {
        drugDosage.setDosageTypeId(dosageType.getId());
        return this;
    }

    public DrugDosageBuilder withMealAdviceType(MealAdviceType mealAdviceType) {
        drugDosage.setMealAdviceId(mealAdviceType.getId());
        return this;
    }

    public DrugDosageBuilder withBrand(Brand brand) {
        drugDosage.setBrandId(brand.getCompanyId());
        return this;
    }

    public DrugDosage build() {
        return drugDosage;
    }
}
