package org.motechproject.tama.patient.repository;

import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.LuceneResult;
import com.github.ldriscoll.ektorplucene.util.IndexUploader;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllMotechAlerts extends AbstractCouchRepository<Alert> {

    private static final String VIEW_NAME = "Alert";
    private static final String SEARCH_FUNCTION = "findByCriteria";
    private static final String INDEX_FUNCTION = "function(doc) { " +
                "var index=new Document(); " +
                "index.add(doc.externalId, {field: 'patientId'}); " +
                "index.add(doc.status, {field: 'alertStatus'});" +
                "index.add(doc.dateTime, {field: 'dateTime'});" +
                "if(doc.data != undefined) {index.add(doc.data.PatientAlertType, {field: 'alertType'});}; " +
                "return index;" +
            "}";

    @Autowired
    public AllMotechAlerts(@Qualifier("luceneAwareAlertsDbConnector") LuceneAwareCouchDbConnector db) {
        super(Alert.class, db);
        IndexUploader uploader = new IndexUploader();
        uploader.updateSearchFunctionIfNecessary(db, VIEW_NAME, SEARCH_FUNCTION, INDEX_FUNCTION);
    }

    public List<LuceneResult.Row> find(String patientId) {
//        LuceneQuery query = new LuceneQuery(VIEW_NAME, SEARCH_FUNCTION, true);    -- For couchdb >= 1.1.1
        LuceneQuery query = new LuceneQuery(VIEW_NAME, SEARCH_FUNCTION);
        query.setQuery("patientId:" + patientId);
        query.setIncludeDocs(true);
        LuceneResult luceneResult = ((LuceneAwareCouchDbConnector) db).queryLucene(query);
        return luceneResult.getRows();
    }
}
