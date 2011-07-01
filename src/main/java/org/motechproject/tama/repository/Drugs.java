package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.View;
import org.motechproject.tama.domain.Drug;

public class Drugs extends AbstractCouchRepository<Drug>{

	public Drugs(CouchDbConnector dbConnector) {
		super(Drug.class, dbConnector);
	}
	
	@View( name="count", map = "function(doc) { if (doc.documentType == 'Drug') { emit(null, 1) } }", reduce="function(key, values, rereduce) { return sum(values); }")
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