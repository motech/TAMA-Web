package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.motechproject.tamacommon.repository.AbstractCouchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class AllMealAdviceTypes extends AbstractCouchRepository<MealAdviceType> {

    @Autowired
    public AllMealAdviceTypes(@Qualifier("tamaDbConnector") CouchDbConnector db) {
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
