package org.motechproject.tama.web.mapper;

import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.DosageTypes;
import org.motechproject.tama.repository.Drugs;
import org.motechproject.tama.repository.MealAdviceTypes;
import org.motechproject.tama.web.model.DrugDosageView;

public class DrugDosageViewMapper {

    private Drugs drugs;
    private DosageTypes dosageTypes;
    private MealAdviceTypes mealAdviceTypes;

    public DrugDosageViewMapper(Drugs drugs, DosageTypes dosageTypes, MealAdviceTypes mealAdviceTypes) {
        this.drugs = drugs;
        this.dosageTypes = dosageTypes;
        this.mealAdviceTypes = mealAdviceTypes;
    }

    public DrugDosageView map(DrugDosage drugDosage) {
        Drug drug = drugs.get(drugDosage.getDrugId());
        Brand brand = drug.getBrand(drugDosage.getBrandId());
        DosageType dosageType = dosageTypes.get(drugDosage.getDosageTypeId());
        MealAdviceType mealAdviceType = mealAdviceTypes.get(drugDosage.getMealAdviceId());

        DrugDosageView drugDosageView = new DrugDosageView();
        drugDosageView.setDrugName(drug.getName());
        drugDosageView.setBrandName(brand.getName());
        drugDosageView.setDosageType(dosageType.getType());
        drugDosageView.setDosageSchedules(drugDosage.getDosageSchedules());
        drugDosageView.setStartDate(drugDosage.getStartDate());
        drugDosageView.setEndDate(drugDosage.getEndDate());
        drugDosageView.setAdvice(drugDosage.getAdvice());
        drugDosageView.setMealAdviceType(mealAdviceType.getType());
        return drugDosageView;
    }

}
