package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'IVRLanguage') { emit(null, doc) } }")
public class AllIVRLanguages extends CouchDbRepositorySupport<IVRLanguage> {

    @Autowired
    public AllIVRLanguages(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(IVRLanguage.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_code", map = "function(doc) {if (doc.documentType =='IVRLanguage' && doc.code) {emit(doc.code, doc._id);}}")
    public IVRLanguage findByLanguageCode(String code) {
        ViewQuery q = createQuery("find_by_code").key(code).includeDocs(true);
        List<IVRLanguage> ivrLanguages = db.queryView(q, IVRLanguage.class);
        return singleResult(ivrLanguages);
    }

    private IVRLanguage singleResult(List<IVRLanguage> ivrLanguages) {
        return (ivrLanguages == null || ivrLanguages.isEmpty()) ? null : ivrLanguages.get(0);
    }
}
