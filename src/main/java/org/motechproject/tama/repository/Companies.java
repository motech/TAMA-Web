package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Company;

public class Companies extends AbstractCouchRepository<Company> {


    public Companies(CouchDbConnector db) {
        super(Company.class, db);
    }

    @View( name="count", map = "function(doc) { if (doc.documentType == 'Company') { emit(null, 1) } }", reduce="function(key, values, rereduce) { return sum(values); }")
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
