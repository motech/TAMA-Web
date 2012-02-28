package org.motechproject.tama.patient.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.patient.domain.VitalStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllVitalStatistics extends AbstractCouchRepository<VitalStatistics> {

    @Autowired
    public AllVitalStatistics(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(VitalStatistics.class, db);
        initStandardDesignDocument();
    }

    @View(name = "find_by_patientId", map = "function(doc) {if (doc.documentType =='VitalStatistics' && doc.patientId) {emit([doc.patientId, doc.captureDate], doc._id);}}")
    public List<VitalStatistics> findAllByPatientId(String patientId, LocalDate startDate, Object endDate) {
        ComplexKey startKey = ComplexKey.of(patientId, startDate);
        ComplexKey endKey = ComplexKey.of(patientId, endDate);
        return db.queryView(createQuery("find_by_patientId").startKey(startKey).endKey(endKey).includeDocs(true), VitalStatistics.class);
    }

    public VitalStatistics findLatestVitalStatisticByPatientId(String patientId) {
        return lastResult(findAllByPatientId(patientId, null, ComplexKey.emptyObject()));
    }
}
