package org.motechproject.tama.refdata.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.refdata.domain.IVRLanguage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Component
public class AllIVRLanguages extends AbstractCouchRepository<IVRLanguage> {

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

    public void removeByCode(String languageCode) {
        IVRLanguage language = findByLanguageCode(languageCode);
        if (null != language) {
            remove(language);
        }
    }
}
