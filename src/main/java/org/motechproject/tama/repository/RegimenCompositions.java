package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Company;
import org.motechproject.tama.domain.Regimen;
import org.motechproject.tama.domain.RegimenComposition;

@View( name="all", map = "function(doc) { if (doc.documentType == 'RegimenComposition') { emit(null, 1) } }", reduce="function(key, values, rereduce) { return sum(values); }")
public class RegimenCompositions extends CouchDbRepositorySupport<RegimenComposition> {

    public RegimenCompositions(CouchDbConnector db) {
        super(RegimenComposition.class, db);
        initStandardDesignDocument();
    }

    @View( name="count", map = "function(doc) { if (doc.documentType == 'RegimenComposition') { emit(null, 1) } }", reduce="function(key, values, rereduce) { return sum(values); }")
    public long count() {

        ViewQuery query = createQuery("count");
        ViewResult result = db.queryView(query);

        int count = 0;
        for (ViewResult.Row row : result.getRows()) {
          count = row.getValueAsInt();
          break;
        }

        return count;
    }


}
