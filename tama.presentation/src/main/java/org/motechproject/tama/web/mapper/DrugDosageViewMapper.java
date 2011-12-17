package org.motechproject.tama.web.mapper;

import org.motechproject.tama.web.model.DrugDosageView;
import org.motechproject.tamadomain.repository.AllDosageTypes;
import org.motechproject.tamadomain.repository.AllDrugs;
import org.motechproject.tamadomain.repository.AllMealAdviceTypes;

public class DrugDosageViewMapper {

    private AllDrugs allDrugs;
    private AllDosageTypes allDosageTypes;
    private AllMealAdviceTypes allMealAdviceTypes;

    public DrugDosageViewMapper(AllDrugs allDrugs, AllDosageTypes allDosageTypes, AllMealAdviceTypes allMealAdviceTypes) {
        this.allDrugs = allDrugs;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
    }

    public DrugDosageView map(final DrugDosage drugDosage) {
        final Drug drug = allDrugs.get(drugDosage.getDrugId());
        final Brand brand = drug.getBrand(drugDosage.getBrandId());
        final DosageType dosageType = allDosageTypes.get(drugDosage.getDosageTypeId());
        final MealAdviceType mealAdviceType = allMealAdviceTypes.get(drugDosage.getMealAdviceId());

        return new DrugDosageView() {{
	        setDrugName(drug.getName());
	        setBrandName(brand.getName());
	        setDosageType(dosageType.getType());
	        setOffsetDays(drugDosage.getOffsetDays());
	        setMorningTime(drugDosage.getMorningTime());
	        setEveningTime(drugDosage.getEveningTime());
	        setStartDate(drugDosage.getStartDate());
	        setAdvice(drugDosage.getAdvice());
	        setMealAdviceType(mealAdviceType.getType());
        }};

    }

}
