package org.motechproject.tama.refdata.objectcache;

import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.repository.AllMealAdviceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllMealAdviceTypesCache extends Cachable<MealAdviceType> {

    @Autowired
    public AllMealAdviceTypesCache(AllMealAdviceTypes allMealAdviceTypes) {
        super(allMealAdviceTypes);
    }

    @Override
    protected String getKey(MealAdviceType mealAdviceType) {
        return mealAdviceType.getId();
    }

    @Override
    protected int compareTo(MealAdviceType t1, MealAdviceType t2) {
        return t1.getType().compareTo(t2.getType());
    }
}
