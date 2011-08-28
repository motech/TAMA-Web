package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.motechproject.tama.domain.LabResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'LabResult') { emit(null, doc) } }")
public class LabResults extends CouchDbRepositorySupport<LabResult> {

    private LabTests labTests;

    @Autowired
    public LabResults(@Qualifier("tamaDbConnector") CouchDbConnector db, LabTests labTests) {
        super(LabResult.class, db);
        this.labTests = labTests;
        initStandardDesignDocument();
    }

    @Override
    public LabResult get(String id) {
        LabResult labResult = super.get(id);
        loadDependencies(labResult);
        return labResult;
    }

    @View(name = "find_by_patientId", map = "function(doc) {if (doc.documentType =='LabResult' && doc.patientId) {emit(doc.patientId, doc._id);}}")
    public List<LabResult> findByPatientId(String patientId) {
        ViewQuery query = createQuery("find_by_patientId").key(patientId).includeDocs(true);
        List<LabResult> labResults = db.queryView(query, LabResult.class);
        for(LabResult labResult : labResults){
            loadDependencies(labResult);
        }
        return labResults;
    }

    private void loadDependencies(LabResult labResult) {
        labResult.setLabTest(labTests.get(labResult.getLabTest_id()));
    }

    public void merge(List<LabResult> labResultsForPatient) {
        for(LabResult labResult : labResultsForPatient){
            LabResult labResultInDb = get(labResult.getId());
            labResultInDb.setResult(labResult.getResult());
            labResultInDb.setTestDate(labResult.getTestDate());
            update(labResultInDb);
        }
    }
}
