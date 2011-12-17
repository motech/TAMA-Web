package org.motechproject.tama.web.mapper;

import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.refdata.domain.Brand;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.repository.AllDosageTypes;
import org.motechproject.tama.refdata.repository.AllDrugs;
import org.motechproject.tama.refdata.repository.AllMealAdviceTypes;
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
