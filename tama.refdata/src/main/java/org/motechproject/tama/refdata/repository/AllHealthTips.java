package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.HealthTip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AllHealthTips extends AbstractCouchRepository<HealthTip> {

    @Autowired
    public AllHealthTips(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(HealthTip.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_category", map = "function(doc) {if (doc.documentType =='HealthTip' && doc.category) {emit(doc.category, doc._id);}}")
    public List<HealthTip> findByCategory(final String category) {
        ViewQuery q = createQuery("find_by_category").key(category).includeDocs(true);
        return db.queryView(q, HealthTip.class);
    }
}
