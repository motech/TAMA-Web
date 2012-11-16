package org.motechproject.tama.refdata.seed;

import org.motechproject.deliverytools.seed.Seed;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tama.refdata.repository.AllMealAdviceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MealAdviceTypeSeed {

    @Autowired
    private AllMealAdviceTypes mealAdviceTypes;

    @Seed(version = "1.0", priority = 0)
    public void load() {
        mealAdviceTypes.add(new MealAdviceType("After Meal"));
        mealAdviceTypes.add(new MealAdviceType("Before Meal"));
    }
}