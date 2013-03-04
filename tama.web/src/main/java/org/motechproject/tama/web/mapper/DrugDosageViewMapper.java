package org.motechproject.tama.web.mapper;

import org.motechproject.tama.patient.domain.DrugDosage;
import org.motechproject.tama.refdata.domain.DosageType;
import org.motechproject.tama.refdata.domain.Drug;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.objectcache.AllDosageTypesCache;
import org.motechproject.tama.refdata.objectcache.AllDrugsCache;
import org.motechproject.tama.refdata.objectcache.AllMealAdviceTypesCache;
import org.motechproject.tama.web.model.DrugDosageView;

public class DrugDosageViewMapper {

    private AllDrugsCache allDrugs;
    private AllDosageTypesCache allDosageTypes;
    private AllMealAdviceTypesCache allMealAdviceTypes;

    public DrugDosageViewMapper(AllDrugsCache allDrugs, AllDosageTypesCache allDosageTypes, AllMealAdviceTypesCache allMealAdviceTypes) {
        this.allDrugs = allDrugs;
        this.allDosageTypes = allDosageTypes;
        this.allMealAdviceTypes = allMealAdviceTypes;
    }

    public DrugDosageView map(final DrugDosage drugDosage) {
        final Drug drug = allDrugs.getBy(drugDosage.getDrugId());
        final DosageType dosageType = allDosageTypes.getBy(drugDosage.getDosageTypeId());
        final MealAdviceType mealAdviceType = allMealAdviceTypes.getBy(drugDosage.getMealAdviceId());

        return new DrugDosageView() {{
            setDrugName(drug.getName());
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
