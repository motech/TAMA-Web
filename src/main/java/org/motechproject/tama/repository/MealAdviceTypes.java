package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.domain.MealAdviceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class MealAdviceTypes extends AbstractCouchRepository<MealAdviceType>{

    @Autowired
	public MealAdviceTypes(CouchDbConnector db) {
		super(MealAdviceType.class, db);
	}

    @Override
    public List<MealAdviceType> getAll() {
        List<MealAdviceType> all = super.getAll();
        Collections.sort(all, new Comparator<MealAdviceType>() {
            @Override
            public int compare(MealAdviceType mealAdviceType1, MealAdviceType mealAdviceType2) {
                return mealAdviceType1.getType().compareTo(mealAdviceType2.getType());
            }
        });
        return all;
    }
}
