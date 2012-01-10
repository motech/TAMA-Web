package org.motechproject.tama.patient.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.repository.AllLabTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AllLabResults extends AbstractCouchRepository<LabResult> {

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
        for (LabResult labResult : labResults) {
            loadDependencies(labResult);
        }
        return labResults;
    }

    @View(name = "find_by_patientId_and_labTest_id", map = "function(doc) {if (doc.documentType =='LabResult' && doc.patientId && doc.labTest_id) {emit([doc.patientId, doc.labTest_id], doc._id);}}")
    public LabResults findLatestLabResultsByPatientId(String patientId) {
        LabResults latestLabResults = new LabResults();

        for (LabTest labTest : allLabTests.getAll()) {
            ViewQuery query = createQuery("find_by_patientId_and_labTest_id").key(ComplexKey.of(patientId, labTest.getId())).includeDocs(true);
            List<LabResult> labResults = db.queryView(query, LabResult.class);

            if(labResults.size() > 0) {
                latestLabResults.add(getLatestLabResult(labResults));
            }
        }
        return latestLabResults;
    }

    private LabResult getLatestLabResult(List<LabResult> labResults) {
        Collections.sort(labResults, new LabResult.LabResultComparator());
        LabResult latestLabResult = labResults.get(0);
        loadDependencies(latestLabResult);
        return latestLabResult;
    }

    private void loadDependencies(LabResult labResult) {
        labResult.setLabTest(allLabTests.get(labResult.getLabTest_id()));
    }

    public void upsert(LabResult labResult) {
        if(labResult.getId() == null) {
            addLabResult(labResult);
            return;
        }
        LabResult labResultInDb = get(labResult.getId());
        if (labResultInDb.getTestDate().isEqual(labResult.getTestDate())) {
            labResultInDb.setResult(labResult.getResult());
            update(labResultInDb);
        } else {
            addLabResult(labResult);
        }
    }

    private void addLabResult(LabResult labResult) {
        LabResult newLabResult = LabResult.newDefault();
        newLabResult.setPatientId(labResult.getPatientId());
        newLabResult.setLabTest_id(labResult.getLabTest_id());
        newLabResult.setTestDate(labResult.getTestDate());
        newLabResult.setTestDateAsDate(labResult.getTestDateAsDate());
        newLabResult.setResult(labResult.getResult());

        add(newLabResult);
    }

}
