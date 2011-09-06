package org.motechproject.tama.web.mapper;

import org.motechproject.tama.domain.*;
import org.motechproject.tama.repository.AllDosageTypes;
import org.motechproject.tama.repository.AllDrugs;
import org.motechproject.tama.repository.AllMealAdviceTypes;
import org.motechproject.tama.web.model.DrugDosageView;

public class DrugDosageViewMapper {

    private AllDrugs allDrugs;
    private AllDosageTypes allDosageTypes;
    private AllMealAdviceTypes allMealAdviceTypes;

    public DrugDosageViewMapper(AllDrugs allDrugs, AllDosageTypes allDosageTypes, AllMealAdviceTypes allMealAdviceTypes) {
        this.allDrugs = allDrugs;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
    }

    public DrugDosageView map(DrugDosage drugDosage) {
        Drug drug = allDrugs.get(drugDosage.getDrugId());
        Brand brand = drug.getBrand(drugDosage.getBrandId());
        DosageType dosageType = allDosageTypes.get(drugDosage.getDosageTypeId());
        MealAdviceType mealAdviceType = allMealAdviceTypes.get(drugDosage.getMealAdviceId());

        DrugDosageView drugDosageView = new DrugDosageView();
        drugDosageView.setDrugName(drug.getName());
        drugDosageView.setBrandName(brand.getName());
        drugDosageView.setDosageType(dosageType.getType());
        drugDosageView.setDosageSchedules(drugDosage.getDosageSchedules());
        drugDosageView.setStartDate(drugDosage.getStartDate());
        drugDosageView.setAdvice(drugDosage.getAdvice());
        drugDosageView.setMealAdviceType(mealAdviceType.getType());
        return drugDosageView;
    }

}
