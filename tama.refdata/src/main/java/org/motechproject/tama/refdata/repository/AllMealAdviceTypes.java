package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.MealAdviceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
public class AllMealAdviceTypes extends AbstractCouchRepository<MealAdviceType> {

    @Autowired
    public AllMealAdviceTypes(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(MealAdviceType.class, db);
    }
}
