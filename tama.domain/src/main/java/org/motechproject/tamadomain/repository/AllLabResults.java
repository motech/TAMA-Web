package org.motechproject.tamadomain.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tamadomain.domain.LabResult;
import org.motechproject.tamadomain.domain.LabResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'LabResult') { emit(null, doc) } }")
public class AllLabResults extends CouchDbRepositorySupport<LabResult> {

    private AllLabTests allLabTests;

    @Autowired
    public AllLabResults(@Qualifier("tamaDbConnector") CouchDbConnector db, AllLabTests allLabTests) {
        super(LabResult.class, db);
        this.allLabTests = allLabTests;
        initStandardDesignDocument();
    }

    @Override
    public LabResult get(String id) {
        LabResult labResult = super.get(id);
        loadDependencies(labResult);
        return labResult;
    }

    @View(name = "find_by_patientId", map = "function(doc) {if (doc.documentType =='LabResult' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public LabResults findByPatientId(String patientId) {
        ViewQuery query = createQuery("find_by_patientId").key(patientId).includeDocs(true);
        LabResults labResults = new LabResults(db.queryView(query, LabResult.class));
        for(LabResult labResult : labResults){
            loadDependencies(labResult);
        }
        return labResults;
    }

    private void loadDependencies(LabResult labResult) {
        labResult.setLabTest(allLabTests.get(labResult.getLabTest_id()));
    }

    public void merge(LabResults labResultsForPatient) {
        for(LabResult labResult : labResultsForPatient){
            LabResult labResultInDb = get(labResult.getId());
            labResultInDb.setResult(labResult.getResult());
            labResultInDb.setTestDate(labResult.getTestDate());
            update(labResultInDb);
        }
    }
}
