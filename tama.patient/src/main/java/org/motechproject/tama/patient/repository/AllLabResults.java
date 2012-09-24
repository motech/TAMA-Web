package org.motechproject.tama.patient.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.patient.domain.LabResult;
import org.motechproject.tama.patient.domain.LabResults;
import org.motechproject.tama.refdata.domain.LabTest;
import org.motechproject.tama.refdata.objectcache.AllLabTestsCache;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class AllLabResults extends AuditableCouchRepository<LabResult> {

    private AllLabTestsCache allLabTests;

    @Autowired
    public AllLabResults(@Qualifier("tamaDbConnector") CouchDbConnector db, AllLabTestsCache allLabTests, AllAuditRecords allAuditRecords) {
        super(LabResult.class, db, allAuditRecords);
        this.allLabTests = allLabTests;
        initStandardDesignDocument();
    }

    @Override
    public LabResult get(String id) {
        LabResult labResult = super.get(id);
        loadDependencies(labResult);
        return labResult;
    }

    @View(name = "find_by_patientId_and_labTest_id", map = "function(doc) {if (doc.documentType =='LabResult' && doc.patientId && doc.labTest_id) {emit([doc.patientId, doc.labTest_id], doc._id);}}")
    public LabResults allLabResults(String patientId) {
        LabResults latestLabResults = new LabResults();

        for (LabTest labTest : allLabTests.getAll()) {
            ViewQuery query = createQuery("find_by_patientId_and_labTest_id").key(ComplexKey.of(patientId, labTest.getId())).includeDocs(true);
            List<LabResult> labResults = db.queryView(query, LabResult.class);

            if (labResults.size() > 0) {
                latestLabResults.add(getLatestLabResult(labResults));
            }
        }
        return latestLabResults;
    }

    private LabResult getLatestLabResult(List<LabResult> labResults) {
        Collections.sort(labResults, new LabResult.LabResultComparator(false));
        LabResult latestLabResult = labResults.get(0);
        loadDependencies(latestLabResult);
        return latestLabResult;
    }

    private void loadDependencies(LabResult labResult) {
        labResult.setLabTest(allLabTests.getBy(labResult.getLabTest_id()));
    }

    public String upsert(LabResult labResult, String userName) {
        if (labResult.getId() == null) {
            if (labResult.getResult() == null || labResult.getResult().isEmpty()) return null;
            add(labResult, userName);
            return labResult.getId();
        } else {
            final LabResult savedLabResult = get(labResult.getId());
            if (labResult.getResult() == null || labResult.getResult().isEmpty()) {
                remove(savedLabResult, userName);
                return null;
            } else {
                savedLabResult.setTestDate(labResult.getTestDate());
                savedLabResult.setResult(labResult.getResult());
                update(savedLabResult, userName);
                return savedLabResult.getId();
            }
        }
    }

    public List<LabResult> findCD4LabResultsFor(String patientId, int rangeInMonths) {
        return findAllByLabTestFor(TAMAConstants.LabTestType.CD4, patientId, rangeInMonths);
    }

    public List<LabResult> findPVLLabResultsFor(String patientId, int rangeInMonths) {
        return findAllByLabTestFor(TAMAConstants.LabTestType.PVL, patientId, rangeInMonths);
    }

    @View(name = "by_id", map = "function(doc) {if (doc.documentType =='LabResult') {emit(doc._id, doc._id);}}")
    public List<LabResult> withIds(List<String> labResultIds) {
        if (null == labResultIds) {
            return Collections.emptyList();
        }
        ViewQuery query = createQuery("by_id").keys(labResultIds).includeDocs(true);
        List<LabResult> results = db.queryView(query, LabResult.class);
        for (LabResult result : results) {
            loadDependencies(result);
        }
        return (null == results) ? Collections.<LabResult>emptyList() : results;
    }

    @View(name = "find_by_patientId_and_labTest_id_and_testDate", map = "function(doc) {if (doc.documentType =='LabResult' && doc.patientId && doc.labTest_id) {emit([doc.patientId, doc.labTest_id, doc.testDate], doc._id);}}")
    private List<LabResult> findAllByLabTestFor(TAMAConstants.LabTestType labTestType, String patientId, int rangeInMonths) {
        LocalDate today = DateUtil.today();
        LocalDate startDate = today.minusMonths(rangeInMonths);
        LabTest labTest = allLabTests.getByName(labTestType.getName());

        ViewQuery query = createQuery("find_by_patientId_and_labTest_id_and_testDate").
                startKey(ComplexKey.of(patientId, labTest.getId(), startDate)).
                endKey(ComplexKey.of(patientId, labTest.getId(), today)).
                inclusiveEnd(true).
                includeDocs(true);

        List<LabResult> labResults = db.queryView(query, LabResult.class);
        for (LabResult labResult : labResults) {
            labResult.setLabTest(labTest);
        }

        return labResults;
    }
}
