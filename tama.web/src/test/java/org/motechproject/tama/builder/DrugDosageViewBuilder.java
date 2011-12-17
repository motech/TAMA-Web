package org.motechproject.tama.builder;

import org.motechproject.tama.web.model.DrugDosageView;

public class DrugDosageViewBuilder {

    private DrugDosageView drugDosage = new DrugDosageView();

    public DrugDosageViewBuilder withBrandName(String brandName) {
        this.drugDosage.setBrandName(brandName);
        return this;
    }

    public DrugDosageViewBuilder withDosageType(String dosageType) {
        this.drugDosage.setDosageType(dosageType);
        return this;
    }

    public DrugDosageViewBuilder withDosageScheduleMorning(String morningTime) {
        this.drugDosage.setMorningTime(morningTime);
        return this;
    }

    public DrugDosageViewBuilder withDosageScheduleEvening(String eveningTime) {
        this.drugDosage.setMorningTime(eveningTime);
        return this;
    }

    public DrugDosageViewBuilder withMealAdviceType(String mealAdviceType) {
        this.drugDosage.setMealAdviceType(mealAdviceType);
        return this;
    }

    public DrugDosageView build() {
        return this.drugDosage;
    }

    public static DrugDosageViewBuilder startRecording() {
        return new DrugDosageViewBuilder();
    }

    public DrugDosageViewBuilder withDefaults() {
        String morningTime = "10:00";
        return this.withBrandName("Efferven").withDosageType("Evening Daily").withDosageScheduleMorning(morningTime).withMealAdviceType("After Meal");
    }
}
