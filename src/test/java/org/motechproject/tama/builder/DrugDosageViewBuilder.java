package org.motechproject.tama.builder;

import org.motechproject.tama.web.model.DrugDosageView;

import java.util.ArrayList;
import java.util.List;

public class DrugDosageViewBuilder {

    private DrugDosageView drugDosage = new DrugDosageView();

    public DrugDosageViewBuilder withBrandName(String brandName){
        this.drugDosage.setBrandName(brandName);
        return this;
    }

    public DrugDosageViewBuilder withDosageType(String dosageType){
        this.drugDosage.setDosageType(dosageType);
        return this;
    }

    public DrugDosageViewBuilder withDosageSchedules(List<String> dosageSchedules){
        this.drugDosage.setDosageSchedules(dosageSchedules);
        return this;
    }

    public DrugDosageViewBuilder withMealAdviceType(String mealAdviceType){
        this.drugDosage.setMealAdviceType(mealAdviceType);
        return this;
    }

    public DrugDosageView build() {
        return this.drugDosage;
    }

    public static DrugDosageViewBuilder startRecording() {
        return new DrugDosageViewBuilder();
    }

    public DrugDosageViewBuilder withDefaults(){
        List<String> dosageSchedules = new ArrayList<String>();
        dosageSchedules.add("10:00");
        return this.withBrandName("Efferven").withDosageType("Evening Daily").withDosageSchedules(dosageSchedules).withMealAdviceType("After Meal");
    }
}
