package org.motechproject.tama.tools.seed;

import org.motechproject.tama.domain.MealAdviceType;
import org.motechproject.tama.repository.AllMealAdviceTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MealAdviceTypeSeed extends Seed {
    @Autowired
    private AllMealAdviceTypes mealAdviceTypes;

    @Override
    public void load() {
        mealAdviceTypes.add(new MealAdviceType("After Meal"));
        mealAdviceTypes.add(new MealAdviceType("Before Meal"));
    }
}