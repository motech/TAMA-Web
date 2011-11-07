package org.motechproject.tama.web.view;


import org.motechproject.tama.domain.MealAdviceType;
import org.motechproject.tama.repository.AllMealAdviceTypes;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MealAdviceTypesView {

    private final AllMealAdviceTypes mealAdviceTypes;

    public MealAdviceTypesView(AllMealAdviceTypes mealAdviceTypes){
        this.mealAdviceTypes = mealAdviceTypes;
    }

    public List<MealAdviceType> getAll() {
        List<MealAdviceType> allMealAdviceTypes = mealAdviceTypes.getAll();
        Collections.sort(allMealAdviceTypes, new Comparator<MealAdviceType>() {
            @Override
            public int compare(MealAdviceType mealAdviceType, MealAdviceType otherMealAdviceType) {
                return mealAdviceType.getType().toLowerCase().compareTo(otherMealAdviceType.getType().toLowerCase());
            }
        });
        return allMealAdviceTypes;
    }
}
